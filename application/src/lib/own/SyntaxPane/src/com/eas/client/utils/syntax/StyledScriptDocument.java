/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.utils.syntax;

import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

class SyntaxInsertEdit extends Object implements UndoableEdit {

    protected static final java.util.ResourceBundle messages = java.util.ResourceBundle.getBundle("com/eas/client/utils/syntax/messages");
    protected int offset = -1;
    protected int length = -1;
    protected String data = "";
    protected StyledScriptDocument doc = null;
    private boolean alive = true;

    public SyntaxInsertEdit(StyledScriptDocument adoc, int aoffset, int alength) {
        super();
        doc = adoc;
        offset = aoffset;
        length = alength;
    }

    protected SyntaxInsertEdit() {
        super();
    }

    @Override
    public void undo() throws CannotUndoException {
        try {
            if (!canUndo()) {
                throw new CannotUndoException();
            }
            undoWork();
        } catch (Exception e) {
            throw new CannotUndoException();
        }
    }

    @Override
    public boolean canUndo() {
        return (alive && doc != null && offset > -1 && (offset + length) <= doc.getLength() && length > -1);
    }

    @Override
    public void redo() throws CannotRedoException {
        try {
            if (!canRedo()) {
                throw new CannotRedoException();
            }
            redoWork();
        } catch (Exception e) {
            throw new CannotRedoException();
        }
    }

    @Override
    public boolean canRedo() {
        return (alive && doc != null && offset > -1 && offset <= doc.getLength());// && data != null && !data.equals(""));
    }

    @Override
    public void die() {
        alive = false;
    }

    @Override
    public boolean isSignificant() {
        return true;
    }

    @Override
    public boolean addEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public boolean replaceEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public String getPresentationName() {
        return messages.getString("Insert");
    }

    @Override
    public String getUndoPresentationName() {
        return getPresentationName();
    }

    @Override
    public String getRedoPresentationName() {
        return getPresentationName();
    }

    protected void undoWork() throws BadLocationException {
        String lData = doc.getText(offset, length);
        if (data == null || data.equals("")) {
            data = lData;
        }
        assert (data.equals(lData));
        doc.silentRemove(offset, length);
    }

    protected void redoWork() throws BadLocationException {
        if (length < 0) {
            length = data.length();
        }
        assert (length == data.length());
        doc.silentInsertString(offset, data, null);
    }
}

class SyntaxRemoveEdit extends SyntaxInsertEdit {

    public SyntaxRemoveEdit(StyledScriptDocument adoc, int aoffset, String adata) {
        super();
        doc = adoc;
        offset = aoffset;
        data = adata;
    }

    @Override
    public boolean canUndo() {
        return super.canRedo();
    }

    @Override
    public boolean canRedo() {
        return super.canUndo();
    }

    @Override
    public String getPresentationName() {
        return messages.getString("Remove");
    }

    @Override
    protected void undoWork() throws BadLocationException {
        super.redoWork();
    }

    @Override
    protected void redoWork() throws BadLocationException {
        super.undoWork();
    }
}

/**
 *
 * @author Marat
 */
public class StyledScriptDocument extends DefaultStyledDocument {

    JInternalSyntaxPane pane = null;
    DocumentListener docHandler = null;
    boolean readOnly = false;

    public StyledScriptDocument(DocumentListener adocHandler) {
        super();
        docHandler = adocHandler;
        addDocumentListener(docHandler);
    }

    public void setPane(JInternalSyntaxPane pane) {
        this.pane = pane;
        setTabStops();
    }

    public JInternalSyntaxPane getPane() {
        return pane;
    }

    public boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean areadOnly) {
        readOnly = areadOnly;
    }

    public boolean isMCommentStyled(int elStartOffset) {
        Element el = getCharacterElement(elStartOffset);
        if (el != null) {
            AttributeSet firstStyle = el.getAttributes();
            if (firstStyle != null) {
                Object attr = firstStyle.getAttribute(AbstractDocument.ElementNameAttribute);
                if (attr != null) {
                    return (attr.equals(LexemeStylesNames.SYNTAX_STYLE_MCOMMENT));
                } else {
                    Object attr1 = firstStyle.getAttribute(StyleConstants.NameAttribute);
                    if (attr1 != null) {
                        return (attr1.equals(LexemeStylesNames.SYNTAX_STYLE_MCOMMENT));
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
        if (!readOnly && len > 0) {
            String toBeRemoved = getText(offs, len);
            silentRemove(offs, len);
            pane.undo.addEdit(new SyntaxRemoveEdit(this, offs, toBeRemoved));
        }
    }

    public void silentRemove(int offs, int len) throws BadLocationException {
        int start = offs;
        int length = len;

        Highlighting syntax = pane.getSyntax();

        int lMaxBias = syntax.getMaximumSeparatorSize() * 2;
        int lMinusBias = 0;
        while (offs - lMinusBias > 0 && lMinusBias < lMaxBias) {
            lMinusBias++;
            start--;
            length++;
        }

        int lPlusBias = 0;
        while (offs + len + lPlusBias < getLength() && lPlusBias < lMaxBias) {
            lPlusBias++;
            length++;
        }
        String sepMLStart = syntax.getMultilineCommentStartSeparator();
        String sepMLEnd = syntax.getMultilineCommentEndSeparator();
        String prevRemove = getText(start, offs - start);
        String nextRemove = getText(offs + len, (start + length) - (offs + len));
        String afterRemove = prevRemove + nextRemove;
        String toRemove = getText(start, length);
        int lsidx = toRemove.indexOf(sepMLStart);
        int leidx = toRemove.indexOf(sepMLEnd);
        int lsidxAr = afterRemove.indexOf(sepMLStart);
        int leidxAr = afterRemove.indexOf(sepMLEnd);

        if (lsidx != -1 || leidx != -1
                || lsidxAr != -1 || leidxAr != -1) {
            super.remove(offs, len);
            int startline = pane.getLineOfOffset(offs);
            int endline = pane.getLineOfOffset(offs + len);
            if (startline != -1 && endline != -1) {
                pane.updateHighlighting(startline, endline);
                pane.highlightMLComments(false);
            } else {
                pane.highlightMLComments(true);
            }
        } else {
            if (isMCommentStyled(start) && isMCommentStyled(offs + len - 1)) {
                super.remove(offs, len);
            } else {
                super.remove(offs, len);
                int removeEndOffset = offs + len;
                if (removeEndOffset > pane.getDocument().getLength()) {
                    removeEndOffset = pane.getDocument().getLength();
                }
                int startline = pane.getLineOfOffset(offs);
                int endline = pane.getLineOfOffset(removeEndOffset);
                if (startline != -1 && endline != -1) {
                    pane.updateHighlighting(startline, endline);
                }
            }
        }
    }

    @Override
    public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (!readOnly) {
            pane.enterUndoSection();
            try {
                remove(offset, length);
                insertString(offset, text, attrs);
            } finally {
                pane.leaveUndoSection();
            }
        }
    }

    protected void setTabStops() {
        if (pane != null && pane.getSyntax() != null) {
            writeLock();
            try {
                Element[] els = getRootElements();
                for (int i = 0; i < els.length; i++) {
                    if (els[i] != null && els[i].getAttributes() instanceof MutableAttributeSet) {
                        StyleConstants.setTabSet((MutableAttributeSet) els[i].getAttributes(), pane.getSyntax().getTabs());
                    }
                }
            } finally {
                writeUnlock();
            }
        }
    }

    @Override
    protected void insert(int offset, ElementSpec[] data) throws BadLocationException {
        if (!readOnly) {
            super.insert(offset, data);
        }
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (!readOnly) {
            silentInsertString(offs, str, a);
            pane.undo.addEdit(new SyntaxInsertEdit(this, offs, str.length()));
        }
    }

    public void silentInsertString(int offs, String str, AttributeSet a) throws BadLocationException {
        String prefix = "";
        String suffix = "";
        if (str == null) {
            str = "";
        }

        Highlighting syntax = pane.getSyntax();
        int lMaxBias = syntax.getMaximumSeparatorSize() * 2;
        int lMinusBias = 0;
        while (offs - lMinusBias > 0 && lMinusBias < lMaxBias) {
            lMinusBias++;
        }
        prefix = getText(offs - lMinusBias, lMinusBias);
        int lPlusBias = 0;
        while (offs + lPlusBias < getLength() && lPlusBias < lMaxBias) {
            lPlusBias++;
        }
        suffix = getText(offs, lPlusBias);


        String sepMLStart = syntax.getMultilineCommentStartSeparator();
        String sepMLEnd = syntax.getMultilineCommentEndSeparator();
        String strBeforeInsert = prefix + suffix;
        String strAfterInsert = prefix + str + suffix;
        int lssidxbi = strBeforeInsert.indexOf(sepMLStart);
        int lseidxbi = strBeforeInsert.indexOf(sepMLEnd);
        int lssidxai = strAfterInsert.indexOf(sepMLStart);
        int lseidxai = strAfterInsert.indexOf(sepMLEnd);

        if (lssidxbi != -1 || lseidxbi != -1
                || lssidxai != -1 || lseidxai != -1) {
            super.insertString(offs, str, a);
            pane.updateHighlighting(pane.getLineOfOffset(offs), pane.getLineOfOffset(offs + str.length()));
            pane.highlightMLComments(false);
        } else {
            if (isMCommentStyled(offs - 1) && isMCommentStyled(offs)) {
                super.insertString(offs, str, a);
                setCharacterAttributes(offs, str.length(), syntax.getStyle(LexemeStylesNames.SYNTAX_STYLE_MCOMMENT), true);
            } else {
                super.insertString(offs, str, a);
                pane.updateHighlighting(pane.getLineOfOffset(offs), pane.getLineOfOffset(offs + str.length()));
            }
        }
    }
}

