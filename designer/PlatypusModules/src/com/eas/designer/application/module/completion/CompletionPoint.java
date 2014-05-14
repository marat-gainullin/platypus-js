/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.designer.application.module.parser.AstUtlities;
import com.eas.script.ScriptUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Stack;
import javax.swing.text.BadLocationException;
import jdk.nashorn.internal.ir.AccessNode;
import jdk.nashorn.internal.ir.BlockLexicalContext;
import jdk.nashorn.internal.ir.Expression;
import jdk.nashorn.internal.ir.ExpressionStatement;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.IdentNode;
import jdk.nashorn.internal.ir.LexicalContext;
import jdk.nashorn.internal.ir.LiteralNode;
import jdk.nashorn.internal.ir.Node;
import jdk.nashorn.internal.ir.PropertyNode;
import jdk.nashorn.internal.ir.visitor.NodeVisitor;
import org.netbeans.modules.editor.NbEditorDocument;

/**
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
            boolean inBetweenSentence = false;
            if (Character.isJavaIdentifierPart(preCaretPositionChar) || preCaretPositionChar == DOT_CHARACTER) {
                boolean afterDotCaretPosintion = !Character.isJavaIdentifierPart(caretPositionChar)
                        && preCaretPositionChar == DOT_CHARACTER;
                String docStr = doc.getText(0, doc.getLength());
                cp.astRoot = ScriptUtils.parseJs(afterDotCaretPosintion ? sanitizeDot(docStr, caretOffset - 1) : docStr);
                //Node offsetNode = AstUtlities.getOffsetNode(cp.astRoot, afterDotCaretPosintion ? caretOffset - 1 : caretOffset);
                //final Node subRoot = getCompletionSubtree(cp.astRoot, caretOffset);
                //if (subRoot != null) {
                List<CompletionToken> ctxTokens = getContextTokens(cp.astRoot, afterDotCaretPosintion ? caretOffset - 1 : caretOffset);
                List<CompletionToken> offsetTokens = getOffsetTokens(ctxTokens, caretOffset);
                inBetweenSentence = ctxTokens.size() > offsetTokens.size() + 1;
                cp.completionTokens = offsetTokens;
                //}
            }
            cp.caretBeginWordOffset = getStartWordOffset(doc, caretOffset);
            cp.caretEndWordOffset = getEndWordOffset(doc, caretOffset);
            if (caretOffset - cp.caretBeginWordOffset > 0 && !inBetweenSentence) {
                cp.filter = doc.getText(cp.caretBeginWordOffset, caretOffset - cp.caretBeginWordOffset);
            }
        }
        return cp;
    }

    public static class AccessNodeLexicalContext extends LexicalContext {

        private Deque<AccessNode> accessNodes = new ArrayDeque<>();

    }

    public static class ExpressionStatementContext extends LexicalContext {

        private ExpressionStatement expressionStatement;

    }

    public static List<CompletionToken> getContextTokens(final Node ast, final int offset) {
        final List<CompletionToken> ctx = new ArrayList<>();
        final AccessNodeLexicalContext lc = new AccessNodeLexicalContext();
        ast.accept(new NodeVisitor<AccessNodeLexicalContext>(lc) {

            @Override
            protected boolean enterDefault(Node node) {
                return true;// AstUtlities.isInNode(node, offset);
            }

            @Override
            public boolean enterAccessNode(AccessNode accessNode) {
                lc.accessNodes.push(accessNode);
                return super.enterAccessNode(accessNode);
            }

            @Override
            public Node leaveAccessNode(AccessNode accessNode) {
                lc.accessNodes.pop();
                return super.leaveAccessNode(accessNode);
            }
            
            

            @Override
            public boolean enterIdentNode(IdentNode identNode) {
                if (!lc.accessNodes.isEmpty()
                        && AstUtlities.isInNode(lc.accessNodes.peekLast(), identNode)
                        && AstUtlities.isInNode(lc.accessNodes.peekLast(), offset)
                    || lc.accessNodes.isEmpty() 
                        && AstUtlities.isInNode(identNode, offset)) {
                    ctx.add(new CompletionToken(identNode.getName(), CompletionTokenType.IDENTIFIER, identNode));
                }
                return true;
            }

        });
        /*
         subRoot.visit(new NodeVisitor() {
         @Override
         public boolean visit(AstNode an) {
         if (an == subRoot) {
         if (an instanceof KeywordLiteral) { // this.
         ctx.add(new CompletionToken(an.toSource(), CompletionTokenType.IDENTIFIER, an));
         return false;
         }
         if (an instanceof Name) { // prop1.
         ctx.add(new CompletionToken(((Name) an).getIdentifier(), CompletionTokenType.IDENTIFIER, an));
         return false;
         }
         return true;
         } else if (an.getParent() instanceof ElementGet) {
         ElementGet eg = (ElementGet) an.getParent();
         if (eg.getElement() == an) { //prop1[prop2] , don't drill deeper
         ctx.add(new CompletionToken(an.toSource(), CompletionTokenType.ELEMENT_GET, an));
         return false;
         }
         } else if (an.getParent() instanceof PropertyGet) { //prop1.prop2
         PropertyGet pg = (PropertyGet) an.getParent();
         if (pg.getTarget() == an && an instanceof Name) {
         ctx.add(new CompletionToken(((Name) an).getIdentifier(), CompletionTokenType.IDENTIFIER, an));
         return false;
         }
         if (pg.getTarget() == an && an instanceof KeywordLiteral) {
         ctx.add(new CompletionToken(an.toSource(), CompletionTokenType.IDENTIFIER, an));
         return false;
         } else if (pg.getProperty() == an && an instanceof Name) {
         ctx.add(new CompletionToken(((Name) an).getIdentifier(), CompletionTokenType.PROPERTY_GET, an));
         return false;
         }
         }
         return an instanceof PropertyGet || an instanceof ElementGet;
         }
         });
         */
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

    public enum CompletionTokenType {

        IDENTIFIER,
        PROPERTY_GET,
        ELEMENT_GET
    }

    public static class CompletionToken {

        public final String name;
        public final CompletionTokenType type;
        public final Node node;

        public CompletionToken(String aName, CompletionTokenType aType, Node aNode) {
            name = aName;
            type = aType;
            node = aNode;
        }
    }
}
