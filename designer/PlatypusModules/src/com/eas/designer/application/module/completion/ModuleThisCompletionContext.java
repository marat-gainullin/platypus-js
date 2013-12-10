/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.cache.PlatypusFilesSupport;
import com.eas.client.model.application.ApplicationDbEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.Symbol;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author vv
 */
public class ModuleThisCompletionContext extends CompletionContext {

    private boolean enableJsElementsCompletion;
    private ModuleCompletionContext parentContext;

    public ModuleThisCompletionContext(ModuleCompletionContext aParentContext, boolean anEnableJsElementsCompletion) {
        super(aParentContext.getScriptClass());
        parentContext = aParentContext;
        enableJsElementsCompletion = anEnableJsElementsCompletion;
    }

    public ModuleCompletionContext getParentContext() {
        return parentContext;
    }

    @Override
    public CompletionContext getChildContext(String fieldName, int offset) throws Exception {
        switch (fieldName) {
            case MODEL_SCRIPT_NAME: {
                return new ModelCompletionContext(parentContext.getDataObject());
            }
            case PARAMS_SCRIPT_NAME: {
                return new ParametersCompletionContext(parentContext.getDataObject().getModel().getParametersEntity().getFields());
            }
        }
        ApplicationDbEntity entity = parentContext.getDataObject().getModel().getEntityByName(fieldName);
        if (entity != null) {
            return new EntityCompletionContext(entity);
        }
        return null;
    }

    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        super.applyCompletionItems(point, offset, resultSet);
        ModuleCompletionContext.JsCodeCompletionScopeInfo completionScopeInfo = ModuleCompletionContext.getCompletionScopeInfo(parentContext.getDataObject(), offset, point.filter);
        if (completionScopeInfo.mode == ModuleCompletionContext.CompletionMode.VARIABLES_AND_FUNCTIONS) {
            fillVariablesAndFunctions(point, resultSet);
        }
    }

    public static Set<String> getThisAliases(FunctionNode moduleConstructor) {
        Set<String> aliases = new HashSet<>();
        Iterator<Node> i = moduleConstructor.getBody().iterator();
        while (i.hasNext()) {
            Node n = i.next();
            if (n instanceof VariableDeclaration) {
                VariableDeclaration vd = (VariableDeclaration) n;
                for (VariableInitializer vi : vd.getVariables()) {
                    if (vi.getInitializer() != null && vi.getInitializer().getType() == Token.THIS && vi.getTarget() instanceof Name) {
                        Name nameNode = (Name) vi.getTarget();
                        aliases.add(nameNode.getIdentifier());
                    }
                }
            }
        }
        return aliases;
    }

    protected void fillVariablesAndFunctions(CompletionPoint point, CompletionResultSet resultSet) throws Exception {
        addItem(resultSet, point.filter, new BeanCompletionItem(parentContext.getDataObject().getModel().getClass(), MODEL_SCRIPT_NAME, null, point.caretBeginWordOffset, point.caretEndWordOffset));
        addItem(resultSet, point.filter, new BeanCompletionItem(parentContext.getDataObject().getModel().getParametersEntity().getRowset().getClass(), PARAMS_SCRIPT_NAME, null, point.caretBeginWordOffset, point.caretEndWordOffset));
        if (enableJsElementsCompletion) {
            ScanJsElementsSupport scanner = new ScanJsElementsSupport(PlatypusFilesSupport.extractModuleConstructor(parentContext.getDataObject().getAst()));
            for (JsCompletionItem i : scanner.getCompletionItems(point)) {
                addItem(resultSet, point.filter, i);
            }
        }
    }

    public static class ScanJsElementsSupport {

        private final FunctionNode moduleConstructor;
        private Map<String, JsCompletionItem> functionsMap;
        private Map<String, JsCompletionItem> fieldsMap;

        public ScanJsElementsSupport(FunctionNode aModuleConstructor) {
            moduleConstructor = aModuleConstructor;
        }

        public Collection<JsCompletionItem> getCompletionItems(CompletionPoint point) {
            functionsMap = new HashMap<>();
            fieldsMap = new HashMap<>();
            scan(point, getThisAliases(moduleConstructor));
            List<JsCompletionItem> items = new ArrayList<>(functionsMap.values());
            items.addAll(new ArrayList<>(fieldsMap.values()));
            return items;
        }

        private void scan(final CompletionPoint point, final Set<String> thisAliases) {
            if (moduleConstructor.getBody() != null) {
                moduleConstructor.getBody().visit(new NodeVisitor() {
                    @Override
                    public boolean visit(AstNode an) {
                        if (an.equals(moduleConstructor.getBody())) {
                            return true;
                        }
                        if (an instanceof ExpressionStatement) {
                            ExpressionStatement es = (ExpressionStatement) an;
                            if (es.getExpression() instanceof Assignment) {
                                Assignment a = (Assignment) es.getExpression();
                                if (a.getLeft() instanceof PropertyGet) {
                                    PropertyGet pg = (PropertyGet) a.getLeft();
                                    if (pg.getTarget().getType() == Token.THIS
                                            || (pg.getTarget() instanceof Name && thisAliases.contains(((Name)pg.getTarget()).getIdentifier()))) {
                                        if (a.getRight() instanceof FunctionNode) {
                                            FunctionNode fn = (FunctionNode) a.getRight();
                                            List<String> params = new ArrayList<>();
                                            if (fn.getSymbols() != null) {
                                                for (Symbol symbol : fn.getSymbols()) { // get function parameters
                                                    if (symbol.getDeclType() == Token.LP) {
                                                        params.add(symbol.getName());
                                                    }
                                                }
                                            }
                                            functionsMap.put(pg.getProperty().getIdentifier(),
                                                    new JsFunctionCompletionItem(pg.getProperty().getIdentifier(), "", params, a.getJsDoc(), point.caretBeginWordOffset, point.caretEndWordOffset));
                                        }
                                    }
                                }
                            }
                        }
                        return false;
                    }
                });
            }
        }
    }
}
