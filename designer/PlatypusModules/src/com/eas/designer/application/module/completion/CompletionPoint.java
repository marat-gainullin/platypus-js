/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.script.Scripts;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.text.BadLocationException;
import jdk.nashorn.internal.ir.AccessNode;
import jdk.nashorn.internal.ir.ExpressionStatement;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.IdentNode;
import jdk.nashorn.internal.ir.IndexNode;
import jdk.nashorn.internal.ir.LexicalContext;
import jdk.nashorn.internal.ir.LiteralNode;
import jdk.nashorn.internal.ir.Node;
import jdk.nashorn.internal.ir.PropertyNode;
import jdk.nashorn.internal.ir.visitor.NodeVisitor;
import jdk.nashorn.internal.parser.Token;
import org.netbeans.modules.editor.NbEditorDocument;

/**
 * The class hold information about a completion environment near the cursor
 * position in a document.
 *
 * @author vv
 */
public class CompletionPoint {

    private final static char DOT_CHARACTER = '.';//NOI18N
    private String filter = "";//NOI18N
    private List<CompletionToken> completionTokens;
    private int caretBeginWordOffset;
    private int caretEndWordOffset;
    private FunctionNode astRoot;

    public String getFilter() {
        return filter;
    }

    public List<CompletionToken> getCompletionTokens() {
        return completionTokens;
    }

    public int getCaretBeginWordOffset() {
        return caretBeginWordOffset;
    }

    public int getCaretEndWordOffset() {
        return caretEndWordOffset;
    }

    public FunctionNode getAstRoot() {
        return astRoot;
    }

    public static CompletionPoint createInstance(NbEditorDocument doc, int caretOffset) throws Exception {
        final CompletionPoint cp = new CompletionPoint();
        if (caretOffset > 0) {
            char caretPositionChar = doc.getChars(caretOffset, 1)[0];
            char preCaretPositionChar = doc.getChars(caretOffset - 1, 1)[0];
            if (Character.isJavaIdentifierPart(preCaretPositionChar) || preCaretPositionChar == DOT_CHARACTER) {
                boolean afterDotCaretPosintion = !Character.isJavaIdentifierPart(caretPositionChar)
                        && preCaretPositionChar == DOT_CHARACTER;
                String docStr = doc.getText(0, doc.getLength());
                cp.astRoot = Scripts.parseJs(
                        afterDotCaretPosintion
                                ? sanitizeDot(docStr, caretOffset - 1) : docStr);
                if (cp.astRoot != null) {
                    List<CompletionToken> ctxTokens = getContextTokens(cp.astRoot, afterDotCaretPosintion ? caretOffset - 1 : caretOffset);
                    List<CompletionToken> offsetTokens = getOffsetTokens(ctxTokens, caretOffset);
                    cp.completionTokens = filterIndexIdentNodes(offsetTokens);
                } else {
                    cp.completionTokens = Collections.emptyList();
                }
            }
            cp.caretBeginWordOffset = getStartWordOffset(doc, caretOffset);
            cp.caretEndWordOffset = getEndWordOffset(doc, caretOffset);
            if (caretOffset - cp.caretBeginWordOffset > 0) {
                cp.filter = doc.getText(cp.caretBeginWordOffset, caretOffset - cp.caretBeginWordOffset);
            }
        }
        return cp;
    }

    public static List<CompletionToken> getContextTokens(final Node ast, final int offset) {
        class AccessNodeLexicalContext extends LexicalContext {

            final Deque<ExpressionStatement> expressionsNodes = new ArrayDeque<>();

        }
        final AccessNodeLexicalContext lc = new AccessNodeLexicalContext();
        final List<CompletionToken> ctx = new ArrayList<>();
        ast.accept(new NodeVisitor<AccessNodeLexicalContext>(lc) {

            @Override
            protected boolean enterDefault(Node node) {
                return true;
            }

            @Override
            public boolean enterExpressionStatement(final ExpressionStatement expressionStatement) {
                lc.expressionsNodes.push(expressionStatement);
                return super.enterDefault(expressionStatement);
            }

            @Override
            public Node leaveExpressionStatement(ExpressionStatement expressionStatement) {
                lc.expressionsNodes.pop();
                return super.leaveExpressionStatement(expressionStatement);
            }

            @Override
            public boolean enterAccessNode(AccessNode accessNode) {
                if (!lc.expressionsNodes.isEmpty()
                        && Scripts.isInNode(lc.expressionsNodes.peekLast(), accessNode)
                        && Scripts.isInNode(lc.expressionsNodes.peekLast(), offset)) {
                    ctx.add(new CompletionToken(accessNode.getProperty(), accessNode));
                } else if (lc.expressionsNodes.isEmpty() && Scripts.isInNode(accessNode, offset)) {
                    ctx.add(new CompletionToken(accessNode.getProperty(), accessNode));
                }
                return super.enterAccessNode(accessNode);
            }

            @Override
            public boolean enterIdentNode(IdentNode identNode) {
                if (!lc.expressionsNodes.isEmpty()
                        && Scripts.isInNode(lc.expressionsNodes.peekLast(), identNode)
                        && Scripts.isInNode(lc.expressionsNodes.peekLast(), offset)) {
                    ctx.add(new CompletionToken(identNode.getName(), identNode));
                } else if (lc.expressionsNodes.isEmpty() && Scripts.isInNode(identNode, offset)) {
                    ctx.add(new CompletionToken(identNode.getName(), identNode));
                }
                return super.enterIdentNode(identNode);
            }

            @Override
            public boolean enterIndexNode(IndexNode indexNode) {
                if (!lc.expressionsNodes.isEmpty()
                        && Scripts.isInNode(lc.expressionsNodes.peekLast(), indexNode)
                        && Scripts.isInNode(lc.expressionsNodes.peekLast(), offset)) {
                    //System.out.println(indexNode.getIndex());
                    if (indexNode.getIndex() instanceof LiteralNode) {
                        LiteralNode ln = (LiteralNode) indexNode.getIndex();
                        ctx.add(new CompletionToken(ln.getString(), indexNode));
                    } else if (indexNode.getIndex() instanceof IdentNode) {
                        IdentNode in = (IdentNode) indexNode.getIndex();
                        ctx.add(new CompletionToken(in.getName(), indexNode));
                    }
                }
                return super.enterIndexNode(indexNode);
            }

        });
        Collections.sort(ctx);
        return ctx;
    }

    private static List<CompletionToken> getOffsetTokens(List<CompletionToken> contextTokens, int offset) {
        final List<CompletionToken> tokens = new ArrayList<>();
        for (CompletionToken token : contextTokens) {
            if (token.node.getFinish() < offset) {
                tokens.add(token);
            } else {
                break;
            }
        }
        return tokens;
    }

    private static List<CompletionToken> filterIndexIdentNodes(List<CompletionToken> tokens) {
        final List<CompletionToken> filtered = new ArrayList<>();
        Set<Node> met = new HashSet<>();
        tokens.forEach((CompletionToken aToken) -> {
            if (aToken.node instanceof IndexNode
                    && ((IndexNode) aToken.node).getIndex() instanceof IdentNode) {
                met.add(((IndexNode) aToken.node).getIndex());
            }
        });
        tokens.forEach((CompletionToken aToken) -> {
            if (!met.contains(aToken.node)) {
                filtered.add(aToken);
            }
        });
        return filtered;
    }

    private static String sanitizeDot(String str, int position) {
        StringBuilder sb = new StringBuilder(str.substring(0, position));
        sb.append(" "); //NOI18N
        sb.append(str.substring(position + 1));
        return sb.toString();
    }

    private static int getStartWordOffset(NbEditorDocument aDoc, int caretOffset) throws Exception {
        while (caretOffset > 0 && aDoc.getLength() > 0
                && (Character.isJavaIdentifierPart(aDoc.getText(caretOffset - 1, 1).toCharArray()[0]))) {
            caretOffset--;
        }
        return caretOffset;
    }

    private static int getEndWordOffset(NbEditorDocument aDoc, int caretOffset) throws BadLocationException {
        while (caretOffset < aDoc.getLength() && aDoc.getLength() > 0
                && Character.isJavaIdentifierPart(aDoc.getText(caretOffset, 1).toCharArray()[0])) {
            caretOffset++;
        }
        return caretOffset;
    }

    /**
     * Represents an element in a completion chain. TODO Should be removed?
     */
    public static class CompletionToken implements Comparable<CompletionToken> {

        public final String name;
        public final Node node;

        public CompletionToken(String aName, Node aNode) {
            name = aName;
            node = aNode;
        }

        @Override
        public int compareTo(CompletionToken o) {
            return node.position() - o.node.position();
        }

    }
}
