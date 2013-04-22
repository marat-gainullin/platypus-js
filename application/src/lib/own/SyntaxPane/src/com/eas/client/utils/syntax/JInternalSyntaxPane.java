package com.eas.client.utils.syntax;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Marat
 */
class ParsedToken {

    public int begScanOffset = 0;
    public int begOffset = -1;
    public int begTokenOffset = -1;
    public int endOffset = -1;
    public int endTokenOffset = -1;

    public ParsedToken() {
    }
}

public class JInternalSyntaxPane extends JTextPane {

    public final static String CONTENT_TYPE_JS = "text/javaScript";
    public final static String CONTENT_TYPE_SQL = "text/Sql";

    static {
        registerEditorKitForContentType(CONTENT_TYPE_JS, JsEditorKit.class.getName());
        registerEditorKitForContentType(CONTENT_TYPE_SQL, SqlEditorKit.class.getName());
    }
    private Highlighting syntax = null;
    private static final java.util.ResourceBundle messages = java.util.ResourceBundle.getBundle("com/eas/client/utils/syntax/messages");
    SyntaxUndoManager undo = new SyntaxUndoManager();
    public DocumentListener docListener = null;

    public JInternalSyntaxPane() {
        super();
        setContentType(CONTENT_TYPE_JS);
        createHighlihgting();
        completeInputMap();
        undo.setLimit(-1);
        setDoubleBuffered(true);
        setSelectedTextColor(Color.WHITE);
        setSelectionColor(new Color(60, 134, 208));
        Style identifierStyle = syntax.getStyle(LexemeStylesNames.SYNTAX_STYLE_IDENTIFIER);
        if (identifierStyle != null) {
            setCharacterAttributes(identifierStyle, true);
            Font font = new Font(StyleConstants.getFontFamily(identifierStyle), 0, StyleConstants.getFontSize(identifierStyle));
            setFont(font);
        }
    }

    @Override
    protected EditorKit createDefaultEditorKit() {
        return new JsEditorKit();
    }

    /**
     * Registers an <code>UndoableEditListener</code>.
     * The listener is notified whenever an edit occurs which can be undone.
     *
     * @param l  an <code>UndoableEditListener</code> object
     * @see #removeUndoableEditListener
     */
    public void addUndoableEditListener(UndoableEditListener l) {
        undo.addUndoableEditListener(l);
    }

    /**
     * Removes an <code>UndoableEditListener</code>.
     *
     * @param l  the <code>UndoableEditListener</code> object to be removed
     * @see #addUndoableEditListener
     */
    public void removeUndoableEditListener(UndoableEditListener l) {
        undo.removeUndoableEditListener(l);
    }

    public void enterUndoSection() {
        undo.enterUndoSection();
    }

    public void leaveUndoSection() {
        undo.leaveUndoSection();
    }

    public boolean isInUndoSection() {
        return undo.isInUndoSection();
    }

    public void clearUndoableEdits() {
        undo.discardAllEdits();
    }

    public void discardEdits() {
        undo.discardAllEdits();
    }

    public boolean canUndo() {
        return undo.canUndo();
    }

    public void undo() {
        if (undo.canUndo()) {
            undo.undo();
        }
    }

    public boolean canRedo() {
        return undo.canRedo();
    }

    public void redo() {
        if (undo.canRedo()) {
            undo.redo();
        }
    }

    public boolean isModified() {
        return undo.canUndo();
    }

    public void setUndoLimit(int limit) {
        if (limit > 0) {
            undo.setLimit(limit);
        }
    }

    @Override
    public void setComponentOrientation(ComponentOrientation o) {
        // no op
    }

    @Override
    public void setDocument(Document doc) {
        if (doc instanceof StyledDocument) {
            super.setDocument(doc);
            if (doc instanceof StyledScriptDocument) {
                createHighlihgting();
                completeInputMap();
                ((StyledScriptDocument) doc).setPane(this);
            }
        } else {
            throw new IllegalArgumentException(messages.getString("Model_must_be_StyledDocument"));
        }
    }

    public int getLineStartOffset(int line) {
        Document doc = getDocument();
        if (doc != null) {
            Element rel = doc.getDefaultRootElement();
            if (rel != null && line >= 0 && line < rel.getElementCount()) {
                Element el = rel.getElement(line);
                if (el != null) {
                    return el.getStartOffset();
                }
            }
        }
        return -1;
    }

    public int getLineOfOffset(int offset) {
        Document doc = getDocument();
        if (doc != null) {
            Element rel = doc.getDefaultRootElement();
            if (rel != null && offset >= 0 && offset <= doc.getLength()) {
                return rel.getElementIndex(offset);
            }
        }
        return -1;
    }

    public int getLineCount() {
        Document doc = getDocument();
        if (doc != null) {
            Element rel = doc.getDefaultRootElement();
            if (rel != null) {
                return rel.getElementCount();
            }
        }
        return 0;
    }

    public Point getLineLocation(int line) {
        Point lpt = new Point(0, 0);
        Document doc = getDocument();
        if (doc != null) {
            Element rel = doc.getDefaultRootElement();
            if (rel != null) {
                if (line >= 0 && line < rel.getElementCount()) {
                    TextUI tui = getUI();
                    if (tui != null) {
                        Element el = rel.getElement(line);
                        if (el != null) {
                            try {
                                Rectangle lrt = tui.modelToView(this, el.getStartOffset());
                                if (lrt != null) {
                                    lpt.x = lrt.x;
                                    lpt.y = lrt.y;
                                }
                            } catch (BadLocationException ex) {
                                Logger.getLogger(JSyntaxPane.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        }
        return lpt;
    }

    public int getLineHeight(int line) {
        int lHeight = 0;
        Document doc = getDocument();
        if (doc != null) {
            Element rel = doc.getDefaultRootElement();
            if (rel != null) {
                if (line >= 0 && line < rel.getElementCount()) {
                    TextUI tui = getUI();
                    if (tui != null) {
                        Element el = rel.getElement(line);
                        if (el != null) {
                            try {
                                Rectangle lrt = tui.modelToView(this, el.getStartOffset());
                                if (lrt != null) {
                                    lHeight = lrt.height;
                                }
                            } catch (BadLocationException ex) {
                                Logger.getLogger(JSyntaxPane.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        }
        return lHeight;
    }

    public int getLineOfPoint(Point apt) {
        return getLineOfOffset(getUI().viewToModel(this, apt));
    }

    public List<Style> getStyles() {
        return syntax.getStylesAsVector();
    }

    public void updateHighlighting(int lStartElement, int lEndElement) {
        assert (lStartElement <= lEndElement) : String.format("lStartElement(%d) <= lEndElement(%d)", lStartElement, lEndElement);
        StyledDocument doc = (StyledDocument) getDocument();
        Element lRoot = doc.getDefaultRootElement();
        if (lRoot != null) {
            for (int i = lStartElement; i <= lEndElement; i++) {
                Element el = lRoot.getElement(i);
                if (el != null) {
                    highlightElement(el);
                }
            }
        }
    }

    public void updateHighlighting() {
        highlightMLComments(true);
    }

    public Highlighting getSyntax() {
        return syntax;
    }

    public void setSyntax(Highlighting syntax) {
        this.syntax = syntax;
    }

    private void clearHighlighting(StyledDocument doc, Element paragraphElement) {
        Style style = syntax.getStyle(LexemeStylesNames.SYNTAX_STYLE_EMPTY);
        if (style != null) {
            doc.setCharacterAttributes(paragraphElement.getStartOffset(), paragraphElement.getEndOffset() - paragraphElement.getStartOffset(), style, true);
        }
    }

    private void highlightComment1LineTail(Element el, int currentMLStartIndex) {
        if (el != null) {
            StyledDocument doc = (StyledDocument) el.getDocument();
            Style style = syntax.getStyle(LexemeStylesNames.SYNTAX_STYLE_MCOMMENT);
            if (style != null) {
                doc.setCharacterAttributes(currentMLStartIndex, el.getEndOffset() - currentMLStartIndex, style, true);
            }
        }
    }

    private void highlightCommentLastLineBegining(Element el, int endMLIndex) {
        if (el != null) {
            StyledDocument doc = (StyledDocument) el.getDocument();
            Style style = syntax.getStyle(LexemeStylesNames.SYNTAX_STYLE_MCOMMENT);
            if (style != null) {
                doc.setCharacterAttributes(el.getStartOffset(), endMLIndex - el.getStartOffset(), style, true);
            }
        }
    }

    private void highlightCommentWholeLine(Element el) {
        if (el != null) {
            StyledDocument doc = (StyledDocument) el.getDocument();
            Style style = syntax.getStyle(LexemeStylesNames.SYNTAX_STYLE_MCOMMENT);
            if (style != null) {
                doc.setCharacterAttributes(el.getStartOffset(), el.getEndOffset() - el.getStartOffset(), style, true);
            }
        }
    }

    private void highlightElement(Element paragraphElement) {
        if (paragraphElement != null) {
            StyledDocument doc = (StyledDocument) paragraphElement.getDocument();
            clearHighlighting(doc, paragraphElement);
            ParsedToken pe = new ParsedToken();
            pe.begScanOffset = paragraphElement.getStartOffset() - 1;
            while (scan4NextToken(pe, paragraphElement.getEndOffset())) {
                try {
                    Style style = getClassfiedTokenStyle(pe, paragraphElement);
                    if (style != null) {
                        doc.setCharacterAttributes(pe.begTokenOffset, pe.endTokenOffset - pe.begTokenOffset, style, true);
                    }
                    style = getClassfiedSeparatorStyle(pe, paragraphElement, true);
                    if (style != null) {
                        doc.setCharacterAttributes(pe.begOffset, pe.begTokenOffset - pe.begOffset, style, true);
                    }
                    style = getClassfiedSeparatorStyle(pe, paragraphElement, false);
                    if (style != null) {
                        doc.setCharacterAttributes(pe.endTokenOffset, pe.endOffset - pe.endTokenOffset, style, true);
                    }
                    // Try to replace this by context buffer in future
                    tryApplySepsDupletStyle(pe, doc);
                    if (syntax.isOverlappingSeparator(docGetText(pe.endTokenOffset, pe.endOffset - pe.endTokenOffset), docGetText(pe.begOffset, pe.begTokenOffset - pe.begOffset))) {
                        pe.begOffset = pe.endTokenOffset;
                        pe.begTokenOffset = pe.endOffset;
                        pe.begScanOffset = pe.begOffset;
                    } else {
                        pe.begOffset = pe.endOffset;
                        pe.begTokenOffset = pe.endOffset;
                        pe.begScanOffset = pe.endOffset;
                    }
                } catch (BadLocationException ex) {
                    Logger.getLogger(JSyntaxPane.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    protected String docGetText(int offset, int length) throws BadLocationException {
        if (offset < 0) {
            return "\n";
        } else {
            return getDocument().getText(offset, length);
        }
    }

    public void createHighlihgting() {
        EditorKit kit = getEditorKit();
        if (kit != null && kit instanceof SyntaxEditorKit) {
            SyntaxEditorKit sKit = (SyntaxEditorKit) kit;
            syntax = sKit.getHighlighting();
        }
    }

    public void completeInputMap() {
        InputMap inmap = getInputMap();
        if (inmap != null) {
            EditorKit kit = getEditorKit();
            if (kit != null) {
                Action[] actions = kit.getActions();
                if (actions != null) {
                    for (int i = 0; i < actions.length; i++) {
                        Action action = actions[i];
                        if (action != null) {
                            Object oKeyStroke = action.getValue(Action.ACCELERATOR_KEY);
                            if (oKeyStroke != null && oKeyStroke instanceof KeyStroke) {
                                KeyStroke ks = (KeyStroke) oKeyStroke;
                                inmap.put(ks, action.getValue(Action.NAME));
                                if (ks.getKeyCode() == KeyEvent.VK_SLASH) {
                                    inmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DIVIDE, ks.getModifiers()), action.getValue(Action.NAME));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private int findUnenclosedMLComment(Element el, String line, String lMLStart, String lMLEnd, int ainlineMLFindStartIndex) {
        int inlineMLFindStartIndex = ainlineMLFindStartIndex;
        int currentMLStartIndex = -1;
        int inlineMLStartIndex = -1;
        int inlineMLEndIndex = -1;
        do {
            inlineMLStartIndex = line.indexOf(lMLStart, inlineMLFindStartIndex);
            if (inlineMLStartIndex != -1 && !syntax.isValidMLCommentStart(line, inlineMLStartIndex)) {
                inlineMLStartIndex = -1;
            }
            if (inlineMLStartIndex != -1) {
                inlineMLEndIndex = line.indexOf(lMLEnd, inlineMLStartIndex + lMLStart.length());
                if (inlineMLEndIndex != -1) {
                    inlineMLFindStartIndex = inlineMLEndIndex + lMLEnd.length();
                } else {
                    inlineMLFindStartIndex = line.length();
                    currentMLStartIndex = inlineMLStartIndex + el.getStartOffset();
                }
            }
        } while (inlineMLStartIndex != -1 && inlineMLEndIndex != -1);

        return currentMLStartIndex;
    }

    public void highlightMLComments(boolean forceAllFormatting) {
        if (syntax.isMultilineCommentsPresent()) {
            StyledScriptDocument doc = (StyledScriptDocument) getDocument();
            try {
                Element docRoot = doc.getDefaultRootElement();
                String lMLStart = syntax.getMultilineCommentStartSeparator();
                String lMLEnd = syntax.getMultilineCommentEndSeparator();
                int currentMLStartIndex = -1;
                int inlineMLEndIndex = -1;
                for (int i = 0; i < docRoot.getElementCount(); i++) {
                    Element el = docRoot.getElement(i);
                    if (el != null) {
                        int elStartOffset = el.getStartOffset();
                        int elEndOffset = el.getEndOffset();
                        String line = doc.getText(elStartOffset, elEndOffset - elStartOffset);
                        if (line != null) {
                            if (forceAllFormatting) {
                                highlightElement(el);
                            }

                            boolean lneedElementHighlighting = true;

                            if (currentMLStartIndex == -1) {// Find multiline comment unenclosed yet
                                currentMLStartIndex = findUnenclosedMLComment(el, line, lMLStart, lMLEnd, 0);
                                if (currentMLStartIndex != -1 && syntax.needHighlight1LineTail()) {
                                    lneedElementHighlighting = false;
                                    highlightElement(el);
                                    highlightComment1LineTail(el, currentMLStartIndex);
                                }
                            } else {
                                inlineMLEndIndex = line.indexOf(lMLEnd, 0);
                                if (inlineMLEndIndex != -1) {
                                    lneedElementHighlighting = false;
                                    highlightElement(el);
                                    if (syntax.needHighlightLastLineBegining()) {
                                        highlightCommentLastLineBegining(el, inlineMLEndIndex + lMLEnd.length() + el.getStartOffset());
                                    }

                                    int inlineMLFindStartIndex = inlineMLEndIndex + lMLEnd.length();
                                    currentMLStartIndex = -1;
                                    if (inlineMLFindStartIndex < line.length()) {
                                        currentMLStartIndex = findUnenclosedMLComment(el, line, lMLStart, lMLEnd, inlineMLFindStartIndex);
                                        if (currentMLStartIndex != -1) {
                                            lneedElementHighlighting = false;
                                            highlightElement(el);
                                            if (syntax.needHighlight1LineTail()) {
                                                highlightComment1LineTail(el, currentMLStartIndex);
                                            }
                                        }
                                    }
                                } else {
                                    lneedElementHighlighting = false;
                                    highlightCommentWholeLine(el);
                                }
                            }
                            if (doc.isMCommentStyled(elStartOffset) && lneedElementHighlighting) {
                                highlightElement(el);
                            }
                        }
                    }
                }
            } catch (BadLocationException ex) {
                Logger.getLogger(JSyntaxPane.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Style getClassfiedSeparatorStyle(ParsedToken pe, Element paragraph, boolean first) {
        try {
            int tokenLength = 0;
            String separator = null;
            if (first) {
                tokenLength = pe.begTokenOffset - pe.begOffset;
                assert (tokenLength >= 0);
                separator = docGetText(pe.begOffset, tokenLength);
            } else {
                tokenLength = pe.endOffset - pe.endTokenOffset;
                assert (tokenLength >= 0);
                separator = docGetText(pe.endTokenOffset, tokenLength);
            }
            if (separator != null) {
                String styleName = syntax.getTokenStyleName(separator);
                if (styleName != null) {
                    return syntax.getStyle(styleName);
                }
            }
        } catch (BadLocationException ex) {
            Logger.getLogger(JSyntaxPane.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Style getClassfiedTokenStyle(ParsedToken pe, Element paragraph) {
        try {
            int tokenLength = pe.endTokenOffset - pe.begTokenOffset;
            assert (tokenLength >= 0);
            String token = docGetText(pe.begTokenOffset, tokenLength);
            String styleName = null;
            if (tokenLength == 0) {
            } else {
                String firstSeparator = docGetText(pe.begOffset, pe.begTokenOffset - pe.begOffset);
                String secondSeparator = docGetText(pe.endTokenOffset, pe.endOffset - pe.endTokenOffset);

                styleName = syntax.getSyntaxElementStyleName(firstSeparator, secondSeparator, token);
            }
            if (styleName == null) {
                styleName = syntax.getTokenStyleName(token);
            }

            if (styleName != null) {
                return syntax.getStyle(styleName);
            }
        } catch (BadLocationException ex) {
            Logger.getLogger(JSyntaxPane.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Style getMultilineCommentEndStyle() {
        String styleName = syntax.getMultilineCommentEndStyleName();
        if (styleName != null) {
            return syntax.getStyle(styleName);
        } else {
            return null;
        }
    }

    private boolean getNextSeparator(int begScanOffset, int endScanOffset, ParsedToken pe, String firstSep) throws BadLocationException {
        boolean isSeparator = false;
        if (begScanOffset < 0) {
            pe.begOffset = begScanOffset;
            pe.begTokenOffset = begScanOffset + 1;
            return true;
        }
        if (begScanOffset < endScanOffset) {
            int length = syntax.getMaximumSeparatorSize();
            int scanOffset = begScanOffset;
            String txt = null;
            do {
                while (length > 1 && (scanOffset + length) > endScanOffset) {
                    length--;
                }
                txt = docGetText(scanOffset, length);
                isSeparator = syntax.isSeparartor(txt);
                if (isSeparator && firstSep != null && syntax.isOverridingNextSepsSeparator(firstSep)) {
                    isSeparator = syntax.isCorrespondingSeparator(firstSep, txt);
                }
                if (!isSeparator && length <= 1)//>= syntax.getMaximumSeparatorSize())
                {
                    scanOffset++;
                    length = syntax.getMaximumSeparatorSize();//length = 1;
                } else {
                    length--;//length++;
                    if (syntax.isSkippingSeparator(txt)) {
                        scanOffset += 2;
                        length = 1;
                    }
                }
            } while (((scanOffset + 1/*length*/) <= endScanOffset)
                    && !isSeparator);
            if (isSeparator) {
                pe.begOffset = scanOffset;
                pe.begTokenOffset = scanOffset + length + 1;//- 1;
            }
        }
        return isSeparator;
    }

    private boolean scan4NextToken(ParsedToken pe, int scanEndOffset) {
        if (pe.begScanOffset < scanEndOffset) {
            try {
                ParsedToken lpe = new ParsedToken();
                boolean isSeparator = getNextSeparator(pe.begScanOffset, scanEndOffset, lpe, null);
                if (!isSeparator) {
                    return false;
                } else {
                    pe.begOffset = lpe.begOffset;
                    pe.begTokenOffset = lpe.begTokenOffset;
                }
                String firstSep = docGetText(pe.begOffset, pe.begTokenOffset - pe.begOffset);
                isSeparator = getNextSeparator(pe.begTokenOffset, scanEndOffset, lpe, firstSep);
                if (!isSeparator) {
                    return false;
                } else {
                    pe.endTokenOffset = lpe.begOffset;
                    pe.endOffset = lpe.begTokenOffset;
                }
                pe.begScanOffset = pe.endOffset;
                return true;
            } catch (BadLocationException ex) {
                Logger.getLogger(JSyntaxPane.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    private boolean isMultilineCommentEndSeparator(ParsedToken pe) throws BadLocationException {
        String lSep = docGetText(pe.endTokenOffset, pe.endOffset - pe.endTokenOffset);
        if (lSep != null) {
            return syntax.isMultilineCommentEndSeparator(lSep);
        } else {
            return false;
        }
    }

    private void tryApplySepsDupletStyle(ParsedToken pe, StyledDocument doc) throws BadLocationException {
        if (pe.begTokenOffset == pe.endTokenOffset) {
            String sepsDuplet = docGetText(pe.begOffset, pe.endOffset - pe.begOffset);
            if (sepsDuplet != null) {
                String styleName = syntax.getTokenStyleName(sepsDuplet);
                if (styleName != null) {
                    Style style = syntax.getStyle(styleName);
                    if (style != null) {
                        doc.setCharacterAttributes(pe.begOffset, pe.endOffset - pe.begOffset, style, true);
                    }
                }
            }
        }
    }
}
