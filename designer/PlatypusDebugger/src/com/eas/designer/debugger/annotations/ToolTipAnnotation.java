/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger.annotations;

import com.eas.designer.debugger.DebuggerConstants;
import com.eas.designer.debugger.DebuggerEnvironment;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import org.openide.text.Annotation;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.text.DataEditorSupport;
import org.openide.text.Line;
import org.openide.text.Line.Part;
import org.netbeans.spi.debugger.ui.EditorContextDispatcher;
import org.openide.text.NbDocument;

/**
 *
 * @author mg
 */
public class ToolTipAnnotation extends Annotation {

    @Override
    public String getAnnotationType() {
        return null;//"Platypus-debugger-tooltip-annotation";
    }

    @Override
    public String getShortDescription() {
        Part lpart = (Part) getAttachedAnnotatable();
        final JEditorPane ep = EditorContextDispatcher.getDefault().getMostRecentEditor();
        DebuggerEngine currentEngine = DebuggerManager.getDebuggerManager().getCurrentEngine();
        if (lpart != null && ep != null && ep.getDocument() != null && currentEngine != null) {
            DebuggerEnvironment env = currentEngine.lookupFirst(DebuggerConstants.DEBUGGER_SERVICERS_PATH, DebuggerEnvironment.class);
            if (env != null) {
                Line line = lpart.getLine();
                DataObject dob = DataEditorSupport.findDataObject(line);
                if (dob != null) {
                    EditorCookie leCookie = dob.getLookup().lookup(EditorCookie.class);
                    if (leCookie != null) {
                        boolean[] isMethodPtr = new boolean[]{false};
                        StyledDocument doc = leCookie.getDocument();
                        final String expression = getIdentifier(
                                doc,
                                ep,
                                NbDocument.findLineOffset(
                                doc,
                                lpart.getLine().getLineNumber()) + lpart.getColumn(),
                                isMethodPtr);
                        if (expression != null) {
                            try {
                                return expression + " = " + env.mDebugger.evaluate(expression);
                            } catch (Exception ex) {
                                return ex.getLocalizedMessage();
                            }
                        }
                        /*
                        part = lpart;
                        eCookie = leCookie;
                        eui = Utilities.getEditorUI(ep);
                        return NbBundle.getMessage(DebuggerUtils.class, "LBL_tooltip_substitute");
                         * 
                         */
                    }
                }
            }
        }
        return null;
    }

    private static String getIdentifier(
            StyledDocument doc,
            JEditorPane ep,
            int offset,
            boolean[] isMethodPtr) {
        // do always evaluation if the tooltip is invoked on a text selection
        String t = null;
        if ((ep.getSelectionStart() <= offset)
                && (offset <= ep.getSelectionEnd())) {
            t = ep.getSelectedText();
        }
        if (t != null) {
            return t;
        }
        int line = NbDocument.findLineNumber(
                doc,
                offset);
        int col = NbDocument.findLineColumn(
                doc,
                offset);
        try {
            Element lineElem =
                    NbDocument.findLineRootElement(doc).
                    getElement(line);

            if (lineElem == null) {
                return null;
            }
            int lineStartOffset = lineElem.getStartOffset();
            int lineLen = lineElem.getEndOffset() - lineStartOffset;
            t = doc.getText(lineStartOffset, lineLen);
            int identStart = col;
            while (identStart > 0
                    && (Character.isJavaIdentifierPart(
                    t.charAt(identStart - 1))
                    || (t.charAt(identStart - 1) == '.'))) {
                identStart--;
            }
            int identEnd = col;
            while (identEnd < lineLen
                    && Character.isJavaIdentifierPart(t.charAt(identEnd))) {
                identEnd++;
            }

            if (identStart == identEnd) {
                return null;
            }

            String ident = t.substring(identStart, identEnd);
            while (identEnd < lineLen
                    && Character.isWhitespace(t.charAt(identEnd))) {
                identEnd++;
            }
            if (identEnd < lineLen && t.charAt(identEnd) == '(') {
                // We're at a method call
                isMethodPtr[0] = true;
            }
            return ident;
        } catch (BadLocationException e) {
            return null;
        }
    }
}
