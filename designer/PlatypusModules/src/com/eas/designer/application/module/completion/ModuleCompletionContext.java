/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.scripts.ScriptRunner;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.module.AstProvider;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.application.module.parser.AstUtlities;
import com.eas.designer.explorer.utils.StringUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.Symbol;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.netbeans.api.project.Project;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Lookup;

/**
 *
 * @author vv
 */
public class ModuleCompletionContext extends CompletionContext {

    protected PlatypusModuleDataObject dataObject;

    public ModuleCompletionContext(PlatypusModuleDataObject aDataObject, Class<? extends ScriptRunner> aClass) {
        super(aClass);
        dataObject = aDataObject;
    }

    public PlatypusModuleDataObject getDataObject() {
        return dataObject;
    }

    @Override
    public void applyCompletionItems(JsCompletionProvider.CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        fillFieldsValues(dataObject.getModel().getParametersEntity().getFields(), point, resultSet);
        fillEntities(dataObject.getModel().getEntities().values(), resultSet, point);
        addItem(resultSet, point.filter, new BeanCompletionItem(dataObject.getModel().getClass(), MODEL_SCRIPT_NAME, null, point.caretBeginWordOffset, point.caretEndWordOffset));
        addItem(resultSet, point.filter, new BeanCompletionItem(dataObject.getModel().getParametersEntity().getRowset().getClass(), PARAMS_SCRIPT_NAME, null, point.caretBeginWordOffset, point.caretEndWordOffset));
        fillJavaCompletionItems(point, resultSet);
        fillJsEntities(offset, point, resultSet);
    }

    @Override
    public CompletionContext getChildContext(String fieldName, int offset) throws Exception {
        switch (fieldName) {
            case MODEL_SCRIPT_NAME: {
                return new ModelCompletionContext(dataObject);
            }
            case PARAMS_SCRIPT_NAME: {
                return new EntityCompletionContext(dataObject.getModel().getParametersEntity());
            }
        }
        ApplicationDbEntity entity = dataObject.getModel().getEntityByName(fieldName);
        if (entity != null) {
            return new EntityCompletionContext(entity);
        }
        CompletionContext typeCompletionContext = findModuleCompletionContext(fieldName, offset, dataObject);
        if (typeCompletionContext != null) {
            return typeCompletionContext;
        }
        return null;
    }
    
    protected void fillJsEntities(int offset, JsCompletionProvider.CompletionPoint point, CompletionResultSet resultSet) {
        JsCompletonItemsSupport cs = new JsCompletonItemsSupport();
        Collection<JsCompletionItem> items = cs.getCompletionItems(dataObject, "", offset, point);
        for (JsCompletionItem item : items) {
            addItem(resultSet, point.filter, item);
        }
    }

    public static CompletionContext findModuleCompletionContext(String fieldName, int offset, PlatypusModuleDataObject appElementDataObject) {
        AstRoot ast = appElementDataObject.getAst();
        if (ast != null) {
            AstNode offsetNode = AstUtlities.getOffsetNode(ast, offset);
            AstNode currentNode = offsetNode;
            for (;;) {//up to the root node  
                if (currentNode instanceof ScriptNode) {
                    ScriptNode scriptNode = (ScriptNode) currentNode;
                    ModuleCompletionContext.FindModuleElementSupport visitor =
                            new ModuleCompletionContext.FindModuleElementSupport(appElementDataObject.getProject(), scriptNode, fieldName);
                    CompletionContext ctx = visitor.getModuleInfo();
                    if (ctx != null) {
                        return ctx;
                    }
                }
                currentNode = currentNode.getParent();
                if (currentNode == null) {
                    break;
                }
            }
        }
        return null;
    }

    private static boolean isModuleInitializerName(String name) {
        return name.equals(MODULE_NAME)
                || name.equals(SERVER_MODULE_NAME)
                || name.equals(FORM_MODULE_NAME)
                || name.equals(REPORT_MODULE_NAME);
    }

    public static class JsCompletonItemsSupport {

        private Map<String, JsCompletionItem> functionsMap;
        private Map<String, JsCompletionItem> fieldsMap;
        private AstNode currentNode;

        public Collection<JsCompletionItem> getCompletionItems(AstProvider astProvider, String rightText, int offset, JsCompletionProvider.CompletionPoint point) {
            functionsMap = new HashMap<>();
            fieldsMap = new HashMap<>();
            AstRoot tree = astProvider.getAst();
            if (tree != null) {
                AstNode offsetNode = AstUtlities.getOffsetNode(tree, offset);
                if (isInNewExpression(offsetNode, point.filter)) {
                } else {
                    if (offsetNode == null) {
                        offsetNode = tree;
                    }
                    scanSymbolsPath(offsetNode, rightText, point);
                }

            }
            List<JsCompletionItem> items = new ArrayList<>(functionsMap.values());
            items.addAll(new ArrayList<>(fieldsMap.values()));
            return items;
        }

        private boolean isInNewExpression(AstNode aNode, String txt) {
            return (aNode != null) && ((aNode instanceof NewExpression && (txt == null || txt.isEmpty()))
                    || (aNode instanceof Name && aNode.getParent() instanceof NewExpression));
        }

        private void scanSymbolsPath(AstNode node, final String rightText, final JsCompletionProvider.CompletionPoint point) {
            currentNode = node;
            for (;;) {//up to the root node  
                if (currentNode instanceof ScriptNode) {
                    ScriptNode scriptNode = (ScriptNode) currentNode;
                    //scan for variables and functions because we need more data about them
                    scriptNode.visit(new NodeVisitor() {
                        @Override
                        public boolean visit(AstNode an) {
                            if (an == currentNode) {
                                return true;
                            }
                            if (an instanceof FunctionNode) {
                                FunctionNode functionNode = (FunctionNode) an;
                                List<String> params = new ArrayList<>();
                                if (functionNode.getSymbols() != null) {
                                    for (Symbol symbol : functionNode.getSymbols()) { // get function parameters
                                        if (symbol.getDeclType() == Token.LP) {
                                            params.add(symbol.getName());
                                        }
                                    }
                                }
                                functionsMap.put(functionNode.getName(),
                                        new JsFunctionCompletionItem(functionNode.getName(), rightText, params, functionNode.getJsDoc(), point.caretBeginWordOffset, point.caretEndWordOffset));
                                return false;
                            }
                            if (an instanceof VariableDeclaration) {
                                VariableDeclaration variableDeclaration = (VariableDeclaration) an;
                                if (variableDeclaration.getVariables() != null) {
                                    for (VariableInitializer variableInitializer : variableDeclaration.getVariables()) {
                                        fieldsMap.put(variableInitializer.getTarget().getString(),
                                                new JsFieldCompletionItem(variableInitializer.getTarget().getString(), rightText, variableDeclaration.getJsDoc(), point.caretBeginWordOffset, point.caretEndWordOffset));
                                    }
                                }
                                return false;
                            }
                            return true;
                        }
                    });

                    // get parameters in the scope
                    for (Symbol symbol : scriptNode.getSymbols()) {
                        switch (symbol.getDeclType()) {
                            case Token.LP:
                                fieldsMap.put(symbol.getName(), new JsFieldCompletionItem(symbol.getName(), "", null, point.caretBeginWordOffset, point.caretEndWordOffset));//NOI18N
                                break;

                        }
                    }
                }
                currentNode = currentNode.getParent();
                if (currentNode == null) {
                    break;
                }
            }
        }
    }

    private static class FindModuleElementSupport {

        private Project prj;
        private AstNode node;
        private String fieldName;
        private CompletionContext ctx;

        public FindModuleElementSupport(Project project, AstNode node, String fieldName) {
            this.prj = project;
            this.node = node;
            this.fieldName = fieldName;
        }

        public CompletionContext getModuleInfo() {
            node.visit(new NodeVisitor() {
                @Override
                public boolean visit(AstNode an) {
                    if (an == node) {
                        return true;
                    }
                    if (an instanceof FunctionNode) {
                        return false;
                    }
                    if (an instanceof VariableDeclaration) {
                        VariableDeclaration variableDeclaration = (VariableDeclaration) an;
                        if (variableDeclaration.getVariables() != null) {
                            for (VariableInitializer variableInitializer : variableDeclaration.getVariables()) {
                                if (variableInitializer.getTarget() != null
                                        && variableInitializer.getTarget().getString().equals(fieldName)
                                        && variableInitializer.getInitializer() != null) {
                                    if (variableInitializer.getInitializer() instanceof NewExpression) {
                                        NewExpression ne = (NewExpression) variableInitializer.getInitializer();
                                        if (ne.getTarget() != null && ne.getTarget() instanceof Name) {
                                            //checks for new Module(moduleName) like expression 
                                            if (isModuleInitializerName(ne.getTarget().getString())
                                                    && ne.getArguments() != null
                                                    && ne.getArguments().size() > 0) {
                                                ctx = getModuleCompletionContext(prj, stripElementId(ne.getArguments().get(0).toSource()));
                                                return false;
                                            }
                                            //checks for Platypus API classes
                                            for (ScriptClassProvider scp : Lookup.getDefault().lookupAll(ScriptClassProvider.class)) {
                                                Class clazz = scp.getClassByName(ne.getTarget().getString());
                                                if (clazz != null) {
                                                    ctx = new CompletionContext(clazz);
                                                    return false;
                                                }
                                            }
                                            //checks for new moduleName() expression
                                            CompletionContext cc = getModuleCompletionContext(prj, stripElementId(ne.getTarget().getString()));
                                            if (cc != null) {
                                                ctx = cc;
                                                return false;
                                            }
                                        }
                                        //checks for Modules.get(moduleName) expression
                                    } else if (variableInitializer.getInitializer() instanceof FunctionCall) {
                                        FunctionCall fc = (FunctionCall) variableInitializer.getInitializer();
                                        if (fc.getTarget() instanceof PropertyGet) {
                                            PropertyGet pg = (PropertyGet) fc.getTarget();
                                            if (pg.getLeft().getString().equals(MODULES_OBJECT_NAME)
                                                    && pg.getRight().getString().equals(GET_METHOD_NAME)) {
                                                if (fc.getArguments() != null && fc.getArguments().size() > 0) {
                                                    ctx = getModuleCompletionContext(prj, stripElementId(fc.getArguments().get(0).toSource()));
                                                    return false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return false;
                    }
                    return true;
                }
            });
            return ctx;
        }

        private String stripElementId(String str) {
            return StringUtils.strip(StringUtils.strip(StringUtils.strip(str, "\""), "'"));//NOI18N
        }

        private CompletionContext getModuleCompletionContext(Project project, String appElementId) {
            FileObject appElementFileObject = IndexerQuery.appElementId2File(project, appElementId);
            if (appElementFileObject == null) {
                return null;
            }
            try {
                DataObject referencedDataObject = DataObject.find(appElementFileObject);
                if (referencedDataObject instanceof PlatypusModuleDataObject) {
                    return ((PlatypusModuleDataObject) DataObject.find(appElementFileObject)).getCompletionContext();
                }
            } catch (DataObjectNotFoundException ex) {
                //no-op
            }
            return null;
        }
    }
}
