/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.script.PropertiesAnnotationsMiner;
import com.eas.script.ScriptUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jdk.nashorn.internal.ir.AccessNode;
import jdk.nashorn.internal.ir.BinaryNode;
import jdk.nashorn.internal.ir.ExpressionStatement;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.IdentNode;
import jdk.nashorn.internal.ir.LexicalContext;
import jdk.nashorn.internal.ir.visitor.NodeOperatorVisitor;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 * Represents an instance of a Module, Form or Report.
 *
 * @author vv
 */
public class ModuleInstanceCompletionContext extends CompletionContext {

    public static final String THIS_KEYWORD = "this";//NOI18N
    private final ModuleCompletionContext parentContext;

    public ModuleInstanceCompletionContext(ModuleCompletionContext aParentContext) {
        super(null);
        parentContext = aParentContext;
    }

    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        ScanJsElementsSupport scanner = new ScanJsElementsSupport(parentContext.getDataObject().getAstRoot(), parentContext.getDataObject().getConstructor());
        for (JsCompletionItem i : scanner.getCompletionItems(point)) {
            addItem(resultSet, point.getFilter(), i);
        }

    }

    private static class ScanJsElementsSupport {

        private final FunctionNode astRoot;
        private final FunctionNode moduleConstructor;
        private Map<String, JsCompletionItem> functionsMap;

        public ScanJsElementsSupport(FunctionNode anAstRoot, FunctionNode aModuleConstructor) {
            astRoot = anAstRoot;
            moduleConstructor = aModuleConstructor;
        }

        public Collection<JsCompletionItem> getCompletionItems(CompletionPoint point) {
            functionsMap = new HashMap<>();
            Set<String> thisAlises = ScriptUtils.getThisAliases(moduleConstructor);
            scan(point, thisAlises);
            List<JsCompletionItem> items = new ArrayList<>(functionsMap.values());
            return items;
        }

        private void scan(final CompletionPoint point, final Set<String> thisAliases) {
            if (moduleConstructor.getBody() != null) {

                final Map<IdentNode, String> propertiesComments = new HashMap<>();
                astRoot.accept(new PropertiesAnnotationsMiner(moduleConstructor.getSource(), thisAliases) {

                    @Override
                    protected void commentedProperty(IdentNode aProperty, String aComment) {
                        propertiesComments.put(aProperty, aComment);
                    }
                    
                    @Override
                    protected void commentedFunction(FunctionNode aFunction, String aComment) {
                        //no-op
                    }

                });

                LexicalContext lc = new LexicalContext();
                moduleConstructor.accept(new NodeOperatorVisitor<LexicalContext>(lc) {

                    @Override
                    public boolean enterExpressionStatement(ExpressionStatement expressionStatement) {
                        if (lc.getCurrentFunction() == moduleConstructor
                                && expressionStatement.getExpression() instanceof BinaryNode) {
                            BinaryNode bn = (BinaryNode) expressionStatement.getExpression();
                            if (bn.getAssignmentSource() instanceof FunctionNode) {
                                String propName;
                                if (bn.getAssignmentDest() instanceof AccessNode) {
                                    AccessNode an = (AccessNode) bn.getAssignmentDest();
                                    if (an.getBase() instanceof IdentNode
                                            && thisAliases.contains(((IdentNode) an.getBase()).getName())) {
                                        propName = an.getProperty().getName();
                                        FunctionNode fn = (FunctionNode) bn.getAssignmentSource();
                                        List<String> params = new ArrayList<>();
                                        for (IdentNode paramNode : fn.getParameters()) {
                                            params.add(paramNode.getName());
                                        }
                                        String jsDoc = propertiesComments.get(an.getProperty());
                                        functionsMap.put(propName,
                                                new JsFunctionCompletionItem(propName,
                                                        "",
                                                        params,
                                                        jsDoc,
                                                        point.getCaretBeginWordOffset(),
                                                        point.getCaretEndWordOffset()));
                                    }
                                }
                            }
                            System.out.println(bn);
                        }
                        return super.enterExpressionStatement(expressionStatement);
                    }

                });
            }
        }
    }

}
