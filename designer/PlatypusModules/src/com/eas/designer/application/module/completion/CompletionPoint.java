/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import static com.eas.designer.application.module.completion.JsCompletionProvider.getStartWordOffset;
import com.eas.designer.application.module.parser.AstUtlities;
import com.eas.script.JsParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;
import org.netbeans.modules.editor.NbEditorDocument;

/**
 *
 * @author vv
 */
public class CompletionPoint {

    private static char DOT_CHARACTER = '.';//NOI18N
    public String filter = "";//NOI18N
    public CompletionToken[] context;
    public int caretBeginWordOffset;
    public int caretEndWordOffset;
    
    public static CompletionPoint createInstance(NbEditorDocument doc, int caretOffset) throws Exception {
        final CompletionPoint cp = new CompletionPoint();
        if (caretOffset > 0) {
            char caretPositionChar = doc.getChars(caretOffset, 1)[0];
            char preCaretPositionChar = doc.getChars(caretOffset - 1, 1)[0];
            if (Character.isJavaIdentifierPart(preCaretPositionChar) || preCaretPositionChar == DOT_CHARACTER) {
                boolean afterDotCaretPosintion = !Character.isJavaIdentifierPart(caretPositionChar)
                        && preCaretPositionChar == DOT_CHARACTER;
                String docStr = doc.getText(0, doc.getLength());
                AstRoot root = JsParser.parse(afterDotCaretPosintion ? sanitizeDot(docStr, caretOffset - 1) : docStr);
                AstNode offsetNode = AstUtlities.getOffsetNode(root, afterDotCaretPosintion ? caretOffset - 1 : caretOffset);
                final AstNode subRoot = getCompletionSubtree(root, offsetNode);
                if (subRoot != null) {
                    List<CompletionToken> ctx = getContextTokens(subRoot);
                    if (ctx.size() > 0) {
                        if (!afterDotCaretPosintion) {
                            cp.context = ctx.subList(0, ctx.size() - 1).toArray(new CompletionToken[0]);
                        } else {
                            cp.context = ctx.subList(0, ctx.size()).toArray(new CompletionToken[0]);
                        }
                    }
                }
                cp.caretBeginWordOffset = getStartWordOffset(doc, caretOffset);
                cp.caretEndWordOffset = getEndWordOffset(doc, caretOffset);
                if (caretOffset - cp.caretBeginWordOffset > 0) {
                    cp.filter = doc.getText(cp.caretBeginWordOffset, caretOffset - cp.caretBeginWordOffset);
                }
            }
        }
        return cp;
    }

    private static List<CompletionToken> getContextTokens(final AstNode subRoot) {
        final List<CompletionToken> ctx = new ArrayList<>();
        subRoot.visit(new NodeVisitor() {
            @Override
            public boolean visit(AstNode an) {
                if (an == subRoot) {
                    if (an instanceof Name) { // prop1.
                        ctx.add(new CompletionToken(((Name) an).getIdentifier(), CompletionTokenType.IDENTIFIER));
                        return false;
                    }
                } else if (an.getParent() instanceof ElementGet) {
                    ElementGet eg = (ElementGet) an.getParent();
                    if (eg.getElement() == an) { //prop1[prop2] , don't drill deeper
                        ctx.add(new CompletionToken(an.toSource(), CompletionTokenType.ELEMENT_GET));
                        return false;
                    }
                } else if (an.getParent() instanceof PropertyGet) { //prop1.prop2
                    PropertyGet pg = (PropertyGet) an.getParent();
                    if (pg.getTarget() == an && an instanceof Name) {
                        ctx.add(new CompletionToken(((Name) an).getIdentifier(), CompletionTokenType.IDENTIFIER));
                        return false;
                    } else if (pg.getProperty() == an && an instanceof Name) {
                        ctx.add(new CompletionToken(((Name) an).getIdentifier(), CompletionTokenType.PROPERTY_GET));
                        return false;
                    }
                }
                return an instanceof PropertyGet || an instanceof ElementGet;
            }
        });
        return ctx;
    }

    private static String sanitizeDot(String str, int position) {
        StringBuilder sb = new StringBuilder(str.substring(0, position));
        sb.append(" "); //NOI18N
        sb.append(str.substring(position + 1));
        return sb.toString();
    }

    private static AstNode getCompletionSubtree(AstRoot root, AstNode node) {
        if (node instanceof Name || node instanceof ElementGet) {
            AstNode subTree = node;
            while ((subTree.getParent() instanceof PropertyGet)) {
                subTree = subTree.getParent();
            }
            return subTree;
        } else if (node instanceof PropertyGet || node instanceof ElementGet) {
            return node;
        } else {
            return null;
        }
    }

    protected static int getStartWordOffset(NbEditorDocument aDoc, int caretOffset) throws Exception {
        while (caretOffset > 0 && aDoc.getLength() > 0
                && (Character.isJavaIdentifierPart(aDoc.getText(caretOffset - 1, 1).toCharArray()[0]))) {
            caretOffset--;
        }
        return caretOffset;
    }

    public static int getEndWordOffset(NbEditorDocument aDoc, int caretOffset) throws BadLocationException {
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

        public CompletionToken(String aName, CompletionTokenType aType) {
            name = aName;
            type = aType;
        }
    }
}
