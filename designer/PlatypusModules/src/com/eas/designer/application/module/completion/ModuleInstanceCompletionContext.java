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
import jdk.nashorn.internal.ir.visitor.NodeOperatorVisitor;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author vv
 */
public class ModuleInstanceCompletionContext extends CompletionContext {

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
            Set<String> thisAlises = new HashSet<>();
            thisAlises.add("this");//TODO add othe this aliases
            scan(point, thisAlises);
            List<JsCompletionItem> items = new ArrayList<>(functionsMap.values());
            return items;
        }

        private void scan(final CompletionPoint point, final Set<String> thisAliases) {
            if (moduleConstructor.getBody() != null) {
                LexicalContext lc  = new LexicalContext();
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

                        return super.enterExpressionStatement(expressionStatement); //To change body of generated methods, choose Tools | Templates.
                    }

                });

//                moduleConstructor.getBody().visit(new NodeVisitor() {
//                    @Override
//                    public boolean visit(AstNode an) {
//                        if (an.equals(moduleConstructor.getBody())) {
//                            return true;
//                        }
//                        if (an instanceof ExpressionStatement) {
//                            ExpressionStatement es = (ExpressionStatement) an;
//                            if (es.getExpression() instanceof Assignment) {
//                                Assignment a = (Assignment) es.getExpression();
//                                if (a.getLeft() instanceof PropertyGet) {
//                                    PropertyGet pg = (PropertyGet) a.getLeft();
//                                    if (pg.getTarget().getType() == Token.THIS
//                                            || (pg.getTarget() instanceof Name && thisAliases.contains(((Name)pg.getTarget()).getIdentifier()))) {
//                                        if (a.getRight() instanceof FunctionNode) {
//                                            FunctionNode fn = (FunctionNode) a.getRight();
//                                            List<String> params = new ArrayList<>();
//                                            if (fn.getSymbols() != null) {
//                                                for (Symbol symbol : fn.getSymbols()) { // get function parameters
//                                                    if (symbol.getDeclType() == Token.LP) {
//                                                        params.add(symbol.getName());
//                                                    }
//                                                }
//                                            }
//                                            functionsMap.put(pg.getProperty().getIdentifier(),
//                                                    new JsFunctionCompletionItem(pg.getProperty().getIdentifier(), "", params, a.getJsDoc(), point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        return false;
//                    }
//                });
            }
        }
    }

}
