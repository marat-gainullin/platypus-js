/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jdk.nashorn.internal.ir.AccessNode;
import jdk.nashorn.internal.ir.BinaryNode;
import jdk.nashorn.internal.ir.ExpressionStatement;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.IdentNode;
import jdk.nashorn.internal.ir.LexicalContext;
import jdk.nashorn.internal.ir.VarNode;
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
        ScanJsElementsSupport scanner = new ScanJsElementsSupport(parentContext.getDataObject().getConstructor());
        for (JsCompletionItem i : scanner.getCompletionItems(point)) {
            addItem(resultSet, point.getFilter(), i);
        }

    }

    private static class ScanJsElementsSupport {

        private final FunctionNode moduleConstructor;
        private Map<String, JsCompletionItem> functionsMap;

        public ScanJsElementsSupport(FunctionNode aModuleConstructor) {
            moduleConstructor = aModuleConstructor;
        }

        public Collection<JsCompletionItem> getCompletionItems(CompletionPoint point) {
            functionsMap = new HashMap<>();
            Set<String> thisAlises = getThisAliases(moduleConstructor);
            thisAlises.add("this");//TODO add othe this aliases
            scan(point, thisAlises);
            List<JsCompletionItem> items = new ArrayList<>(functionsMap.values());
            return items;
        }

        public static Set<String> getThisAliases(final FunctionNode moduleConstructor) {
            final Set<String> aliases = new HashSet<>();
            if (moduleConstructor.getBody() != null) {
                aliases.add(THIS_KEYWORD);
                LexicalContext lc = new LexicalContext();
                moduleConstructor.accept(new NodeOperatorVisitor<LexicalContext>(lc) { 

                    @Override
                    public boolean enterVarNode(VarNode varNode) {
                        if (lc.getCurrentFunction() == moduleConstructor) {
                            if (varNode.getAssignmentSource() instanceof IdentNode) {
                                IdentNode in = (IdentNode) varNode.getAssignmentSource();
                                if (THIS_KEYWORD.equals(in.getName())) {
                                   aliases.add(varNode.getAssignmentDest().getName());
                                }
                            }
                        }
                        return super.enterVarNode(varNode);
                    }
                });
            }

            return aliases;
        }

        private void scan(final CompletionPoint point, final Set<String> thisAliases) {
            if (moduleConstructor.getBody() != null) {
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
                                        functionsMap.put(propName,
                                                new JsFunctionCompletionItem(propName,
                                                        "",
                                                        params,
                                                        "jsDoc",//TODO
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
