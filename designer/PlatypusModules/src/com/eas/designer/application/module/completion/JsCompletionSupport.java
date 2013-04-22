/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.designer.application.module.AstProvider;
import com.eas.designer.application.module.completion.JsCompletionProvider.CompletionPoint;
import com.eas.designer.application.module.parser.AstUtlities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.Symbol;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

/**
 *
 * @author vv
 */
public class JsCompletionSupport {

    private static final String JSDOC_PREFIX = "/**"; //NOI18N
    
    private Map<String, JsCompletionItem> functionsMap;
    private Map<String, JsCompletionItem> fieldsMap;
    private AstNode currentNode;
    
    public synchronized Collection<JsCompletionItem> getCompletionItems(AstProvider astProvider, String rightText, int offset, CompletionPoint point) {
        functionsMap = new HashMap<>();
        fieldsMap = new HashMap<>();
        AstRoot tree = astProvider.getAst();
        if (tree != null) {
            AstNode offsetNode = AstUtlities.getOffsetNode(tree, offset);
            if (offsetNode == null) {
                offsetNode = tree;
            }
            scanSymbolsPath(offsetNode, rightText, point);

        }
        List<JsCompletionItem> items = new ArrayList<>(functionsMap.values());
        items.addAll(new ArrayList<>(fieldsMap.values()));
        return items;
    }
    
    public static List<String> getComments(String jsDoc) {
        if (jsDoc == null || jsDoc.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> comments = new ArrayList<>();
        for (String line : Arrays.asList(jsDoc.split("\n"))) {
            String trimmedLine = line.trim();
            // ignore end of block comment: "*/"
            if (!trimmedLine.equals(JSDOC_PREFIX)) {
                if (trimmedLine.startsWith(JSDOC_PREFIX)) {
                    comments.add(trimmedLine.substring(JSDOC_PREFIX.length()).trim());
                } else if (trimmedLine.length() == 1 || (trimmedLine.length() > 1 && trimmedLine.charAt(1) != '/')) { //NOI18N
                    comments.add(trimmedLine.substring(1));
                }
            }
        }
        return comments;
    }
    
    private void scanSymbolsPath(AstNode node, final String rightText, final CompletionPoint point) {
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
                            VariableDeclaration variableDeclaration = (VariableDeclaration)an;
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
