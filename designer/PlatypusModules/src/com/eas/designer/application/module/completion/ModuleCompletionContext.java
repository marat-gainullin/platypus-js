/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.designer.application.indexer.AppElementInfo;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import static com.eas.designer.application.module.completion.CompletionContext.MODEL_SCRIPT_NAME;
import static com.eas.designer.application.module.completion.CompletionContext.addItem;
import com.eas.designer.application.module.parser.AstUtlities;
import com.eas.designer.explorer.utils.StringUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.ExpressionStatement;
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

    public static final String THIS_KEYWORD = "this"; //NOI18N
    
    protected PlatypusModuleDataObject dataObject;

    public ModuleCompletionContext(PlatypusModuleDataObject aDataObject, Class<?> aClass) {
        super(aClass);
        dataObject = aDataObject;
    }

    public PlatypusModuleDataObject getDataObject() {
        return dataObject;
    }

    @Override
    public void applyCompletionItems(JsCompletionProvider.CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        JsCodeCompletionScopeInfo completionScopeInfo = getCompletionScopeInfo(dataObject, offset, point.filter);
        if (completionScopeInfo.mode == CompletionMode.CONSTRUCTORS) {
            fillSystemConstructors(point, resultSet);
            fillApplicatonElementsConstructors(IndexerQuery.appElementsByPrefix(dataObject.getProject(), point.filter != null ? point.filter : ""), point, resultSet);//NOI18N
        }
    }

    protected void fillVariablesAndFunctions(JsCompletionProvider.CompletionPoint point, CompletionResultSet resultSet) throws Exception {
        fillFieldsValues(dataObject.getModel().getParametersEntity().getFields(), point, resultSet);
        fillEntities(dataObject.getModel().getEntities().values(), resultSet, point);
        addItem(resultSet, point.filter, new BeanCompletionItem(dataObject.getModel().getClass(), MODEL_SCRIPT_NAME, null, point.caretBeginWordOffset, point.caretEndWordOffset));
        addItem(resultSet, point.filter, new BeanCompletionItem(dataObject.getModel().getParametersEntity().getRowset().getClass(), PARAMS_SCRIPT_NAME, null, point.caretBeginWordOffset, point.caretEndWordOffset));
        fillJavaCompletionItems(point, resultSet);
    }

    protected void fillSystemConstructors(JsCompletionProvider.CompletionPoint point, CompletionResultSet resultSet) {
        for (CompletionSupportService scp : Lookup.getDefault().lookupAll(CompletionSupportService.class)) {
            Collection<SystemConstructorCompletionItem> items = scp.getSystemConstructors(point);
            if (items != null) {
                for (SystemConstructorCompletionItem item : items) {
                    addItem(resultSet, point.filter, item);
                }
            }
        }
    }

    protected void fillApplicatonElementsConstructors(Collection<AppElementInfo> appElements, JsCompletionProvider.CompletionPoint point, CompletionResultSet resultSet) {
        for (CompletionSupportService scp : Lookup.getDefault().lookupAll(CompletionSupportService.class)) {
            Collection<AppElementConstructorCompletionItem> items = scp.getAppElementsConstructors(appElements, point);
            if (items != null) {
                for (AppElementConstructorCompletionItem item : items) {
                    addItem(resultSet, point.filter, item);
                }
            }
        }
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
        CompletionContext typeCompletionContext = findCompletionContext(fieldName, offset, this);
        return typeCompletionContext;
    }

    protected static JsCodeCompletionScopeInfo getCompletionScopeInfo(PlatypusModuleDataObject aDataObject, int offset, String text) {
        AstRoot tree = aDataObject.getAst();
        AstNode offsetNode = AstUtlities.getOffsetNode(tree, offset);
        CompletionMode codeCompletionInfo = isInNewExpression(offsetNode, text) ? CompletionMode.CONSTRUCTORS : CompletionMode.VARIABLES_AND_FUNCTIONS;
        return new JsCodeCompletionScopeInfo(offsetNode, codeCompletionInfo);
    }

    private static boolean isInNewExpression(AstNode aNode, String txt) {
        return (aNode != null) && ((aNode instanceof NewExpression && (txt == null || txt.isEmpty()))
                || (aNode instanceof Name && aNode.getParent() instanceof NewExpression));
    }

    public static ModuleCompletionContext getModuleCompletionContext(Project project, String appElementId) {
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

    public static CompletionContext findCompletionContext(String fieldName, int offset, ModuleCompletionContext parentModuleContext) {
        AstRoot ast = parentModuleContext.dataObject.getAst();
        if (ast != null) {     
            AstNode offsetNode = AstUtlities.getOffsetNode(ast, offset);
            AstNode currentNode = offsetNode;
            for (;;) {//up to the root node  
                if (currentNode instanceof ScriptNode) {
                    ScriptNode scriptNode = (ScriptNode) currentNode;
                    ModuleCompletionContext.FindModuleElementSupport visitor =
                            new ModuleCompletionContext.FindModuleElementSupport(scriptNode, fieldName, parentModuleContext);
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

    public enum CompletionMode {

        VARIABLES_AND_FUNCTIONS, CONSTRUCTORS
    }

    public static class JsCodeCompletionScopeInfo {

        public final AstNode scope;
        public final CompletionMode mode;

        public JsCodeCompletionScopeInfo(AstNode aScope, CompletionMode aMode) {
            scope = aScope;
            mode = aMode;
        }
    }

    public static class ScanJsItemsSupport {

        private Map<String, JsCompletionItem> functionsMap;
        private Map<String, JsCompletionItem> fieldsMap;
        private AstNode currentNode;

        public Collection<JsCompletionItem> getScopeCompletionItems(JsCodeCompletionScopeInfo scopeInfo, String rightText, JsCompletionProvider.CompletionPoint point) {
            functionsMap = new HashMap<>();
            fieldsMap = new HashMap<>();
            fillScopeCompletionItems(scopeInfo, rightText, point);
            List<JsCompletionItem> items = new ArrayList<>(functionsMap.values());
            items.addAll(new ArrayList<>(fieldsMap.values()));
            return items;
        }

        public Collection<JsCompletionItem> getPropertyCompletionItems(JsCodeCompletionScopeInfo scopeInfo, String rightText, JsCompletionProvider.CompletionPoint point) {
            functionsMap = new HashMap<>();
            fieldsMap = new HashMap<>();
            fillPropetyCompletionItems(scopeInfo, point.getLastContextItem(), point);
            List<JsCompletionItem> items = new ArrayList<>(functionsMap.values());
            items.addAll(new ArrayList<>(fieldsMap.values()));
            return items;
        }

        private void fillScopeCompletionItems(final JsCodeCompletionScopeInfo scopeInfo, final String rightText, final JsCompletionProvider.CompletionPoint point) {
            currentNode = scopeInfo.scope;
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
                                FunctionNode fn = (FunctionNode) an;
                                functionsMap.put(fn.getName(),
                                        new JsFunctionCompletionItem(fn.getName(), rightText, getFunctionParams(fn), fn.getJsDoc(), point.caretBeginWordOffset, point.caretEndWordOffset));
                                return false;
                            }
                            if (scopeInfo.mode == CompletionMode.VARIABLES_AND_FUNCTIONS && an instanceof VariableDeclaration) {
                                VariableDeclaration variableDeclaration = (VariableDeclaration) an;
                                if (variableDeclaration.getVariables() != null) {
                                    for (VariableInitializer variableInitializer : variableDeclaration.getVariables()) {
                                        fieldsMap.put(variableInitializer.getTarget().getString(),
                                                new JsFieldCompletionItem(variableInitializer.getTarget().getString(), rightText, variableDeclaration.getJsDoc(), point.caretBeginWordOffset, point.caretEndWordOffset));
                                    }
                                }
                                return false;
                            }
                            return an instanceof ScriptNode || an instanceof Block;
                        }
                    });
                    if (scopeInfo.mode == CompletionMode.VARIABLES_AND_FUNCTIONS) {
                        // get parameters in the scope
                        for (Symbol symbol : scriptNode.getSymbols()) {
                            switch (symbol.getDeclType()) {
                                case Token.LP:
                                    fieldsMap.put(symbol.getName(), new JsFieldCompletionItem(symbol.getName(), "", null, point.caretBeginWordOffset, point.caretEndWordOffset));//NOI18N
                                    break;

                            }
                        }
                    }
                }
                currentNode = currentNode.getParent();
                if (currentNode == null) {
                    break;
                }
            }
        }

        private void fillPropetyCompletionItems(final JsCodeCompletionScopeInfo scopeInfo, final String rightText, final JsCompletionProvider.CompletionPoint point) {
            scopeInfo.scope.getAstRoot().visit(new NodeVisitor() {
                @Override
                public boolean visit(AstNode an) {
                    if (an == scopeInfo.scope.getAstRoot()) {
                        return true;
                    }

                    if (an instanceof ExpressionStatement) {
                        ExpressionStatement es = (ExpressionStatement) an;
                        if (es.getExpression() instanceof Assignment) {
                            Assignment as = (Assignment) es.getExpression();
                            if (as.getLeft() instanceof PropertyGet) {
                                PropertyGet pg = (PropertyGet) as.getLeft();
                                if (pg.getLeft() instanceof Name) {
                                    Name n = (Name) pg.getLeft();
                                    if (n.getIdentifier().equals(rightText)) {
                                        if (as.getRight() instanceof FunctionNode) {
                                            FunctionNode fn = (FunctionNode) as.getRight();
                                            functionsMap.put(pg.getProperty().getIdentifier(),
                                                    new JsFunctionCompletionItem(pg.getProperty().getIdentifier(), "", getFunctionParams(fn), as.getJsDoc(), point.caretBeginWordOffset, point.caretEndWordOffset));
                                        }
                                    }
                                }
                            }
                        }

                    }
                    return false;
                }
            });
        }

        private List<String> getFunctionParams(FunctionNode aFunctionNode) {
            List<String> params = new ArrayList<>();
            if (aFunctionNode.getSymbols() != null) {
                for (Symbol symbol : aFunctionNode.getSymbols()) { // get the function's parameters
                    if (symbol.getDeclType() == Token.LP) {
                        params.add(symbol.getName());
                    }
                }
            }
            return params;
        }
    }

    private static class FindModuleElementSupport {

        private final AstNode node;
        private final String fieldName;
        private final ModuleCompletionContext parentContext;
        private CompletionContext ctx;

        public FindModuleElementSupport(AstNode aNode, String aFieldName, ModuleCompletionContext aParentContext) {
            node = aNode;
            fieldName = aFieldName;
            parentContext = aParentContext;
        }

        public CompletionContext getModuleInfo() {
            if (node instanceof AstRoot && THIS_KEYWORD.equals(fieldName)) {
                return new ThisCompletionContext(parentContext);
            } else {
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
                                                    ctx = getModuleCompletionContext(parentContext.getDataObject().getProject(), stripElementId(ne.getArguments().get(0).toSource()));
                                                    return false;
                                                }
                                                //checks for Platypus API classes
                                                for (CompletionSupportService scp : Lookup.getDefault().lookupAll(CompletionSupportService.class)) {
                                                    Class clazz = scp.getClassByName(ne.getTarget().getString());
                                                    if (clazz != null) {
                                                        ctx = new CompletionContext(clazz);
                                                        return false;
                                                    }
                                                }
                                                //checks for new ModuleName() expression
                                                CompletionContext cc = getModuleCompletionContext(parentContext.getDataObject().getProject(), stripElementId(ne.getTarget().getString()));
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
                                                        ctx = getModuleCompletionContext(parentContext.getDataObject().getProject(), stripElementId(fc.getArguments().get(0).toSource()));
                                                        return false;
                                                    }
                                                }
                                            }
                                        } else if (variableInitializer.getInitializer() instanceof KeywordLiteral && Token.THIS == variableInitializer.getInitializer().getType() && variableDeclaration.getParent() instanceof AstRoot) {
                                            ctx = new ThisCompletionContext(parentContext);
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
        }

        private static String stripElementId(String str) {
            return StringUtils.strip(StringUtils.strip(StringUtils.strip(str, "\""), "'"));//NOI18N
        }
    }

    public static class ThisCompletionContext extends CompletionContext {

        private ModuleCompletionContext parentContext;

        public ThisCompletionContext(ModuleCompletionContext aParentContext) {
            super(aParentContext.getScriptClass());
            parentContext = aParentContext;
        }

        @Override
        public void applyCompletionItems(JsCompletionProvider.CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
            JsCodeCompletionScopeInfo completionScopeInfo = getCompletionScopeInfo(parentContext.getDataObject(), offset, point.filter);
            fillJsEntities(completionScopeInfo, point, resultSet);
            if (completionScopeInfo.mode == CompletionMode.VARIABLES_AND_FUNCTIONS) {
                fillVariablesAndFunctions(point, resultSet);
            }
        }

        protected void fillVariablesAndFunctions(JsCompletionProvider.CompletionPoint point, CompletionResultSet resultSet) throws Exception {
            fillFieldsValues(parentContext.getDataObject().getModel().getParametersEntity().getFields(), point, resultSet);
            fillEntities(parentContext.getDataObject().getModel().getEntities().values(), resultSet, point);
            addItem(resultSet, point.filter, new BeanCompletionItem(parentContext.getDataObject().getModel().getClass(), MODEL_SCRIPT_NAME, null, point.caretBeginWordOffset, point.caretEndWordOffset));
            addItem(resultSet, point.filter, new BeanCompletionItem(parentContext.getDataObject().getModel().getParametersEntity().getRowset().getClass(), PARAMS_SCRIPT_NAME, null, point.caretBeginWordOffset, point.caretEndWordOffset));
            fillJavaCompletionItems(point, resultSet);
        }

        protected static void fillJsEntities(JsCodeCompletionScopeInfo completionScopeInfo, JsCompletionProvider.CompletionPoint point, CompletionResultSet resultSet) {
            ScanJsItemsSupport cs = new ScanJsItemsSupport();
            Collection<JsCompletionItem> items = cs.getPropertyCompletionItems(completionScopeInfo, "", point);
            for (JsCompletionItem item : items) {
                addItem(resultSet, point.filter, item);
            }
        }
    }
}
