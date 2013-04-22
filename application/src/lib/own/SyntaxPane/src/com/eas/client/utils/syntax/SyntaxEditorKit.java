/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.utils.syntax;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.NavigationFilter;
import javax.swing.text.Position;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.TextAction;
import javax.swing.text.Utilities;

/**
 *
 * @author Marat
 */
public abstract class SyntaxEditorKit extends StyledEditorKit {

    public static final String whiteSpaceRegEx = "\\s+";
    public static final String whiteSpaceEndedRegEx = ".+" + whiteSpaceRegEx;
    public static String indentSymbol = "\t";

    class SelectWordAction extends TextAction {

        protected PreviousWordAction prevWord = new PreviousWordAction(previousWordAction, false);
        protected NextWordAction nextWord = new NextWordAction(nextWordAction, true);

        SelectWordAction() {
            super(selectWordAction);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            prevWord.actionPerformed(e);
            nextWord.actionPerformed(e);
        }
    }
    private JDialog findDialog = null;
    private FindReplaceDialogView m_panel = null;

    class FindAction extends TextAction {

        FindAction() {
            super(FindAction.class.getSimpleName());
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextComponent tc = getTextComponent(e);
            if (tc instanceof JSyntaxPane) {
                JSyntaxPane sp = (JSyntaxPane) tc;
                if (findDialog == null) {
                    findDialog = new JDialog();
                    findDialog.addWindowListener(new WindowAdapter() {

                        @Override
                        public void windowDeactivated(WindowEvent e) {
                            findDialog.setVisible(false);
                            findDialog.dispose();
                        }

                        @Override
                        public void windowClosing(WindowEvent e) {
                            findDialog.setVisible(false);
                            findDialog.dispose();
                        }
                    });
                    findDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
                    m_panel = new FindReplaceDialogView();
                    findDialog.getContentPane().setLayout(new BorderLayout());
                    findDialog.getContentPane().add(m_panel, BorderLayout.CENTER);
                    findDialog.setTitle(m_panel.getLocalizedString("Find_or_replace"));
                    findDialog.setResizable(false);
                    findDialog.pack();
                    Dimension d = findDialog.getPreferredSize();
                    findDialog.setSize(d);
                    findDialog.setMinimumSize(d);
                    findDialog.setMaximumSize(d);
                }
                m_panel.setSp(sp);
                findDialog.setVisible(true);
                m_panel.setVisible(true);
            }
        }
    }

    class LookupAction extends TextAction {

        public LookupAction() {
            super(LookupAction.class.getSimpleName());
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextComponent tc = getTextComponent(e);
            if (tc instanceof JSyntaxPane) {
                try {
                    JSyntaxPane sp = (JSyntaxPane) tc;
                    if (gLookupPopup != null) {
                        gLookupPopup.setVisible(false);
                    }
                    int pos = sp.getCaret().getDot();
                    Document doc = sp.getDocument();
                    int line = sp.getLineOfOffset(pos);
                    int lineStart = sp.getLineStartOffset(line);
                    int lineEnd = sp.getLineStartOffset(line + 1);
                    if (lineEnd == -1) {
                        lineEnd = doc.getLength();
                    }
                    String textLine = doc.getText(lineStart, lineEnd - lineStart);
                    String leftLine = textLine.substring(0, pos - lineStart);
                    processLookup(sp, line, leftLine, textLine);
                } catch (BadLocationException ex) {
                    Logger.getLogger(SyntaxEditorKit.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /*
     * Action to move the selection by way of the
     * getNextVisualPositionFrom method. Constructor indicates direction
     * to use.
     */
    protected class NextVisualPositionSyntaxAction extends TextAction {

        /**
         * Create this action with the appropriate identifier.
         * @param nm  the name of the action, Action.NAME.
         * @param select whether to extend the selection when
         *  changing the caret position.
         */
        NextVisualPositionSyntaxAction(String nm, boolean select, int direction) {
            super(nm);
            this.select = select;
            this.direction = direction;
        }

        /** The operation to perform when this action is triggered. */
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target != null) {
                if (gLookupList != null) {
                    if (getValue(Action.NAME).equals(upAction)) {
                        int selection = gLookupList.getSelectedIndex();
                        selection--;
                        if (selection < 0) {
                            selection = 0;
                        }
                        gLookupList.setSelectedIndex(selection);
                        gLookupList.ensureIndexIsVisible(selection);
                    } else if (getValue(Action.NAME).equals(downAction)) {
                        int selection = gLookupList.getSelectedIndex();
                        selection++;
                        if (selection >= gLookupList.getModel().getSize()) {
                            selection = gLookupList.getModel().getSize() - 1;
                        }
                        gLookupList.setSelectedIndex(selection);
                        gLookupList.ensureIndexIsVisible(selection);
                    } else {
                        doWork(target);
                    }
                } else {
                    doWork(target);
                }
            }
        }

        private void doWork(JTextComponent target) {
            Caret caret = target.getCaret();
            DefaultCaret bidiCaret = (caret instanceof DefaultCaret) ? (DefaultCaret) caret : null;
            int dot = caret.getDot();
            Position.Bias[] bias = new Position.Bias[1];
            Point magicPosition = caret.getMagicCaretPosition();
            try {
                if (magicPosition == null && (direction == SwingConstants.NORTH || direction == SwingConstants.SOUTH)) {
                    Rectangle r = (bidiCaret != null) ? target.getUI().modelToView(target, dot, bidiCaret.getDotBias()) : target.modelToView(dot);
                    magicPosition = new Point(r.x, r.y);
                }
                NavigationFilter filter = target.getNavigationFilter();
                if (filter != null) {
                    dot = filter.getNextVisualPositionFrom(target, dot, (bidiCaret != null) ? bidiCaret.getDotBias() : Position.Bias.Forward, direction, bias);
                } else {
                    dot = target.getUI().getNextVisualPositionFrom(target, dot, (bidiCaret != null) ? bidiCaret.getDotBias() : Position.Bias.Forward, direction, bias);
                }
                if (bias[0] == null) {
                    bias[0] = Position.Bias.Forward;
                }
                if (bidiCaret != null) {
                    if (select) {
                        bidiCaret.moveDot(dot, bias[0]);
                    } else {
                        bidiCaret.setDot(dot, bias[0]);
                    }
                } else {
                    if (select) {
                        caret.moveDot(dot);
                    } else {
                        caret.setDot(dot);
                    }
                }
                if (magicPosition != null && (direction == SwingConstants.NORTH || direction == SwingConstants.SOUTH)) {
                    target.getCaret().setMagicCaretPosition(magicPosition);
                }
            } catch (BadLocationException ex) {
            }
        }
        private boolean select;
        private int direction;
    }
    /*
     * Deletes the word that precedes/follows the beginning of the selection.
     * @see DefaultEditorKit#getActions
     */

    class DeleteWordAction extends TextAction {

        DeleteWordAction(String name) {
            super(name);
            assert (name.equals(deletePrevWordAction)
                    || name.equals(deleteNextWordAction));
        }

        /**
         * The operation to perform when this action is triggered.
         *
         * @param e the action event
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            final JTextComponent target = getTextComponent(e);
            if ((target != null) && (e != null)) {
                if ((!target.isEditable()) || (!target.isEnabled())) {
                    UIManager.getLookAndFeel().provideErrorFeedback(target);
                    return;
                }
                boolean beep = true;
                try {
                    final int start = target.getSelectionStart();
                    final Element line =
                            Utilities.getParagraphElement(target, start);
                    int end;
                    if (deleteNextWordAction == getValue(Action.NAME)) {
                        end = Utilities.getNextWord(target, start);
                        if (end == java.text.BreakIterator.DONE) {
                            //last word in the paragraph
                            final int endOfLine = line.getEndOffset();
                            if (start == endOfLine - 1) {
                                //for last position remove last \n
                                end = endOfLine;
                            } else {
                                //remove to the end of the paragraph
                                end = endOfLine - 1;
                            }
                        } else {
                            String ltxt = target.getText(start, end - start);
                            if (ltxt.matches(whiteSpaceEndedRegEx) && !ltxt.matches(whiteSpaceRegEx)) {
                                ltxt = ltxt.replaceAll(whiteSpaceRegEx, "");
                                end = start + ltxt.length();
                            } else if (!ltxt.isEmpty() && !ltxt.matches(whiteSpaceRegEx)) {
                                int dIndex = firstDelimiterIndex(ltxt);
                                if (dIndex != -1) {
                                    end = target.getCaretPosition() + dIndex;
                                    if (dIndex == 0) {
                                        end++;
                                    }
                                } else {
                                    end = start + ltxt.length();
                                }
                            }
                        }
                    } else {
                        end = Utilities.getPreviousWord(target, start);
                        if (end == java.text.BreakIterator.DONE) {
                            //there is no previous word in the paragraph
                            final int startOfLine = line.getStartOffset();
                            if (start == startOfLine) {
                                //for first position remove previous \n
                                end = startOfLine - 1;
                            } else {
                                //remove to the start of the paragraph
                                end = startOfLine;
                            }
                        } else {
                            String ltxt = target.getText(end, start - end);
                            if (ltxt.matches(whiteSpaceEndedRegEx) && !ltxt.matches(whiteSpaceRegEx)) {
                                ltxt = ltxt.replaceAll(whiteSpaceRegEx, "");
                                end += ltxt.length();
                            } else if (!ltxt.isEmpty() && !ltxt.matches(whiteSpaceRegEx)) {
                                int dIndex = lastDelimiterIndex(ltxt);
                                if (dIndex != -1) {
                                    end += dIndex + 1;
                                    if (dIndex == ltxt.length() - 1) {
                                        end--;
                                    }
                                }
                            }
                        }
                    }
                    int offs = Math.min(start, end);
                    int len = Math.abs(end - start);
                    if (offs >= 0) {
                        target.getDocument().remove(offs, len);
                        beep = false;
                    }
                } catch (BadLocationException ignore) {
                }
                if (beep) {
                    UIManager.getLookAndFeel().provideErrorFeedback(target);
                }
            }
        }
    }

    /*
     * Position the caret to the beginning of the previous word.
     * @see DefaultEditorKit#previousWordAction
     * @see DefaultEditorKit#selectPreviousWordAction
     * @see DefaultEditorKit#getActions
     */
    class PreviousWordAction extends TextAction {

        /** 
         * Create this action with the appropriate identifier. 
         * @param nm  the name of the action, Action.NAME.
         * @param select whether to extend the selection when
         *  changing the caret position.
         */
        PreviousWordAction(String nm, boolean select) {
            super(nm);
            this.select = select;
        }

        /** The operation to perform when this action is triggered. */
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target != null) {
                int offs = target.getCaretPosition();
                boolean failed = false;
                try {
                    Element curPara =
                            Utilities.getParagraphElement(target, offs);
                    offs = Utilities.getPreviousWord(target, offs);
                    if (offs < curPara.getStartOffset()) {
                        // we should first move to the end of the
                        // previous paragraph (bug #4278839)
                        offs = Utilities.getParagraphElement(target, offs).
                                getEndOffset() - 1;
                    } else {
                        String ltxt = target.getText(offs, target.getCaretPosition() - offs);
                        if (ltxt != null) {
                            if (ltxt.matches(whiteSpaceEndedRegEx) && !ltxt.matches(whiteSpaceRegEx)) {
                                ltxt = ltxt.replaceAll(whiteSpaceRegEx, "");
                                offs += ltxt.length();
                            } else if (!ltxt.isEmpty() && !ltxt.matches(whiteSpaceRegEx)) {
                                int dIndex = lastDelimiterIndex(ltxt);
                                if (dIndex != -1) {
                                    offs += dIndex + 1;
                                    if (dIndex == ltxt.length() - 1) {
                                        offs--;
                                    }
                                }
                            }
                        }
                    }
                } catch (BadLocationException bl) {
                    if (offs != 0) {
                        offs = 0;
                    } else {
                        failed = true;
                    }
                }
                if (!failed) {
                    if (select) {
                        target.moveCaretPosition(offs);
                    } else {
                        target.setCaretPosition(offs);
                    }
                } else {
                    UIManager.getLookAndFeel().provideErrorFeedback(target);
                }
            }
        }
        private boolean select;
    }

    /*
     * Position the caret to the next of the word.
     * @see DefaultEditorKit#nextWordAction
     * @see DefaultEditorKit#selectNextWordAction
     * @see DefaultEditorKit#getActions
     */
    class NextWordAction extends TextAction {

        /** 
         * Create this action with the appropriate identifier. 
         * @param nm  the name of the action, Action.NAME.
         * @param select whether to extend the selection when
         *  changing the caret position.
         */
        NextWordAction(String nm, boolean select) {
            super(nm);
            this.select = select;
        }

        /** The operation to perform when this action is triggered. */
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target != null) {
                int offs = target.getCaretPosition();
                boolean failed = false;
                int oldOffs = offs;
                Element curPara =
                        Utilities.getParagraphElement(target, offs);
                try {
                    offs = Utilities.getNextWord(target, offs);
                    if (offs >= curPara.getEndOffset()
                            && oldOffs != curPara.getEndOffset() - 1) {
                        // we should first move to the end of current
                        // paragraph (bug #4278839)
                        offs = curPara.getEndOffset() - 1;
                    } else {
                        String ltxt = target.getText(target.getCaretPosition(), offs - target.getCaretPosition());
                        if (ltxt != null) {
                            if (ltxt.matches(whiteSpaceEndedRegEx) && !ltxt.matches(whiteSpaceRegEx)) {
                                ltxt = ltxt.replaceAll(whiteSpaceRegEx, "");
                                offs = target.getCaretPosition() + ltxt.length();
                            }
                            if (!ltxt.isEmpty() && !ltxt.matches(whiteSpaceRegEx)) {
                                int dIndex = firstDelimiterIndex(ltxt);
                                if (dIndex != -1) {
                                    offs = target.getCaretPosition() + dIndex;
                                    if (dIndex == 0) {
                                        offs++;
                                    }
                                } else {
                                    offs = target.getCaretPosition() + ltxt.length();
                                }
                            }
                        }
                    }
                } catch (BadLocationException bl) {
                    int end = target.getDocument().getLength();
                    if (offs != end) {
                        if (oldOffs != curPara.getEndOffset() - 1) {
                            offs = curPara.getEndOffset() - 1;
                        } else {
                            offs = end;
                        }
                    } else {
                        failed = true;
                    }
                }
                if (!failed) {
                    if (select) {
                        target.moveCaretPosition(offs);
                    } else {
                        target.setCaretPosition(offs);
                    }
                } else {
                    UIManager.getLookAndFeel().provideErrorFeedback(target);
                }
            }
        }
        private boolean select;
    }

    static class CommentBlockAction extends TextAction {

        CommentBlockAction() {
            super(commnentBlockAction);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, InputEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (getValue(Action.NAME).equals(commnentBlockAction)) {
                JTextComponent txtTarget = getTextComponent(e);
                if (txtTarget != null && txtTarget instanceof JSyntaxPane) {
                    JSyntaxPane target = (JSyntaxPane) txtTarget;
                    if (target.getSyntax() != null && target.getSyntax().isSingleLineCommentsPresent()) {
                        String commentSymbol = target.getSyntax().getSingleCommentSymbol();
                        try {
                            target.enterUndoSection();
                            try {
                                Document doc = target.getDocument();
                                int start = target.getSelectionStart();
                                int end = target.getSelectionEnd();
                                int loffs = start;
                                int lInserted = 0;
                                while (loffs <= (end + lInserted)) {
                                    Element startLine = Utilities.getParagraphElement(target, loffs);
                                    int lineStart = startLine.getStartOffset();
                                    String lCommented = target.getText(lineStart, commentSymbol.length());
                                    if (lCommented == null || !lCommented.equals(commentSymbol)) {
                                        doc.insertString(lineStart, commentSymbol, null);
                                        lInserted += commentSymbol.length();
                                    }
                                    loffs = startLine.getEndOffset() + 1;
                                }
                            } finally {
                                target.leaveUndoSection();
                            }
                        } catch (BadLocationException ex) {
                            Logger.getLogger(SyntaxEditorKit.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }

    static class UncommentBlockAction extends TextAction {

        UncommentBlockAction() {
            super(uncommnentBlockAction);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DIVIDE, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (getValue(Action.NAME).equals(uncommnentBlockAction)) {
                JTextComponent txtTarget = getTextComponent(e);
                if (txtTarget != null && txtTarget instanceof JSyntaxPane) {
                    JSyntaxPane target = (JSyntaxPane) txtTarget;
                    if (target.getSyntax() != null && target.getSyntax().isSingleLineCommentsPresent()) {
                        String commentSymbol = target.getSyntax().getSingleCommentSymbol();
                        target.enterUndoSection();
                        try {
                            Document doc = target.getDocument();
                            int start = target.getSelectionStart();
                            int end = target.getSelectionEnd();
                            int loffs = start;
                            int lInserted = 0;
                            while (loffs <= (end + lInserted)) {
                                Element startLine = Utilities.getParagraphElement(target, loffs);
                                int lineStart = startLine.getStartOffset();
                                try {
                                    String lCommented = target.getText(lineStart, commentSymbol.length());
                                    if (lCommented != null && lCommented.equals(commentSymbol)) {
                                        doc.remove(lineStart, commentSymbol.length());
                                        lInserted -= commentSymbol.length();
                                    }
                                } catch (Exception ex) {
                                    Logger.getLogger(SyntaxEditorKit.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                loffs = startLine.getEndOffset() + 1;
                            }
                        } finally {
                            target.leaveUndoSection();
                        }
                    }
                }
            }
        }
    }

    static class IncrementIndentAction extends TextAction {

        IncrementIndentAction() {
            super(incrementIndentAction);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (getValue(Action.NAME).equals(incrementIndentAction)) {
                JTextComponent txtTarget = getTextComponent(e);
                if (txtTarget != null && txtTarget instanceof JSyntaxPane) {
                    JSyntaxPane target = (JSyntaxPane) txtTarget;
                    try {
                        target.enterUndoSection();
                        try {
                            Document doc = target.getDocument();
                            int start = target.getSelectionStart();
                            int end = target.getSelectionEnd();
                            int loffs = start;
                            int lInserted = 0;
                            while (loffs <= (end + lInserted)) {
                                Element startLine = Utilities.getParagraphElement(target, loffs);
                                int lineStart = startLine.getStartOffset();
                                doc.insertString(lineStart, indentSymbol, null);
                                lInserted += indentSymbol.length();
                                loffs = startLine.getEndOffset() + 1;
                            }
                        } finally {
                            target.leaveUndoSection();
                        }
                    } catch (BadLocationException ex) {
                        Logger.getLogger(SyntaxEditorKit.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    static class DecrementIndentAction extends TextAction {

        DecrementIndentAction() {
            super(decrementIndentAction);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (getValue(Action.NAME).equals(decrementIndentAction)) {
                JTextComponent txtTarget = getTextComponent(e);
                if (txtTarget != null && txtTarget instanceof JSyntaxPane) {
                    JSyntaxPane target = (JSyntaxPane) txtTarget;
                    target.enterUndoSection();
                    try {
                        Document doc = target.getDocument();
                        int start = target.getSelectionStart();
                        int end = target.getSelectionEnd();
                        int loffs = start;
                        int lInserted = 0;
                        while (loffs <= (end + lInserted)) {
                            Element startLine = Utilities.getParagraphElement(target, loffs);
                            int lineStart = startLine.getStartOffset();
                            try {
                                String lIndent = target.getText(lineStart, indentSymbol.length());
                                if (lIndent != null && lIndent.equals(indentSymbol)) {
                                    doc.remove(lineStart, indentSymbol.length());
                                    lInserted -= indentSymbol.length();
                                }
                            } catch (Exception ex) {
                                Logger.getLogger(SyntaxEditorKit.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            loffs = startLine.getEndOffset() + 1;
                        }
                    } finally {
                        target.leaveUndoSection();
                    }
                }
            }
        }
    }

    protected class InsertIndentAction extends InsertBreakAction {

        /**
         * Creates this object with the appropriate identifier.
         */
        InsertIndentAction() {
            super();
        }

        /**
         * The operation to perform when this action is triggered.
         *
         * @param e the action event
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target != null) {
                if ((!target.isEditable()) || (!target.isEnabled())) {
                    UIManager.getLookAndFeel().provideErrorFeedback(target);
                    return;
                }
                if (gLookupList != null) {
                    KeyListener[] listeners = gLookupList.getKeyListeners();
                    if (listeners != null) {
                        for (KeyListener l : listeners) {
                            l.keyPressed(new KeyEvent(gLookupList, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, (char) Character.LINE_SEPARATOR));
                        }
                    }
                } else {
                    doWork(target);
                }
            }
        }

        private void doWork(JTextComponent target) {
            String suffix = "";
            try {
                int offs = Math.min(target.getSelectionStart(), target.getSelectionEnd());
                int start = Utilities.getRowStart(target, offs);
                int end = Utilities.getRowEnd(target, offs);
                if (start != -1 && end != -1) {
                    String line = target.getText(start, end - start);
                    if (line != null && !line.equals("") && line.matches(whiteSpaceRegEx + ".+")) {
                        String tail = line.replaceFirst(whiteSpaceRegEx, "");
                        if (tail != null && !tail.equals("")) {
                            suffix = line.substring(0, line.length() - tail.length());
                        }
                    }
                }
            } catch (Exception ex) {
                target.replaceSelection("\n");
            }
            target.replaceSelection("\n" + suffix);
        }
    }
    protected Highlighting highlighting;
    protected SyntaxLookup lookup;
    DocumentListener doclistener = null;
    private static final String commnentBlockAction = "comment-block";
    private static final String uncommnentBlockAction = "uncomment-block";
    private static final String incrementIndentAction = "increment-indent";
    private static final String decrementIndentAction = "decrement-indent";
    private Action[] actions = null;
    private LookupAction lookupAction = new LookupAction();

    public SyntaxEditorKit() {
        super();
        createHighlighting();
    }

    public SyntaxLookup getLookup() {
        return lookup;
    }

    public void setLookup(SyntaxLookup lookup) {
        this.lookup = lookup;
    }

    @Override
    public MutableAttributeSet getInputAttributes() {
        return super.getInputAttributes();
    }

    @Override
    public Action[] getActions() {
        if (actions == null) {
            actions = new Action[]{
                        new DeleteWordAction(deletePrevWordAction),
                        new DeleteWordAction(deleteNextWordAction),
                        new PreviousWordAction(previousWordAction, false),
                        new NextWordAction(nextWordAction, false),
                        new PreviousWordAction(selectionPreviousWordAction, true),
                        new NextWordAction(selectionNextWordAction, true),
                        new CommentBlockAction(),
                        new UncommentBlockAction(),
                        new InsertIndentAction(),
                        new IncrementIndentAction(),
                        new DecrementIndentAction(),
                        new SelectWordAction(),
                        new FindAction(),
                        lookupAction,
                        new NextVisualPositionSyntaxAction(upAction, false,
                        SwingConstants.NORTH),
                        new NextVisualPositionSyntaxAction(downAction, false,
                        SwingConstants.SOUTH)
                    };
        }

        return TextAction.augmentList(super.getActions(), actions);
    }

    public DocumentListener getDoclistener() {
        return doclistener;
    }

    public void setDoclistener(DocumentListener doclistener) {
        this.doclistener = doclistener;
    }

    @Override
    public String getContentType() {
        return super.getContentType();
    }

    public Highlighting getHighlighting() {
        return highlighting;
    }

    public abstract void createHighlighting();

    protected class LookupReshower implements CaretListener {

        protected JSyntaxPane sp;
        protected int line = -1;

        public LookupReshower(JSyntaxPane aSp) {
            super();
            sp = aSp;
            line = aSp.getLineOfOffset(aSp.getCaret().getDot());
        }

        @Override
        public void caretUpdate(CaretEvent e) {
            if (sp != null) {
                sp.removeCaretListener(this);
            }
            if (gLookupPopup != null) {
                gLookupPopup.setVisible(false);
                int newLine = sp.getLineOfOffset(e.getDot());
                if(newLine == line)
                    lookupAction.actionPerformed(new ActionEvent(sp, 1, null));
            }
        }
    }

    protected class LookupInserter extends MouseAdapter implements KeyListener {

        private PreviousWordAction prevWord = new PreviousWordAction(previousWordAction, false);
        private NextWordAction nextWord = new NextWordAction(nextWordAction, true);
        private JList lookupList;
        private JPopupMenu lookupPopup;
        private JSyntaxPane sp;
        private LookupReshower reshower;

        public LookupInserter(JList aLookupList, JPopupMenu aLookupPopup, JSyntaxPane aSp, LookupReshower aReshower) {
            super();
            lookupList = aLookupList;
            lookupPopup = aLookupPopup;
            sp = aSp;
            reshower = aReshower;
        }

        private boolean isSeparatorsInString(String aText) {
            for (int i = 0; i < aText.length(); i++) {
                if (highlighting.isSeparartor(aText.substring(i, i + 1))) {
                    return true;
                }
            }
            return false;
        }

        protected void insertLookedUp(ActionEvent ev) throws BadLocationException {
            assert lookup != null;
            Object value = lookupList.getSelectedValue();
            String toInsert = lookup.convertItem2String(value);
            if (toInsert != null && !toInsert.isEmpty()) {
                sp.removeCaretListener(reshower);
                try {
                    JTextComponent target = (JTextComponent) ev.getSource();
                    int oldDot = target.getCaret().getDot();
                    prevWord.actionPerformed(ev);
                    nextWord.actionPerformed(ev);
                    String setText = target.getSelectedText();
                    if (isSeparatorsInString(setText)) {
                        target.getDocument().insertString(oldDot, toInsert, null);
                    } else {
                        target.replaceSelection(toInsert);
                    }
                    target.select(target.getCaret().getDot(), target.getCaret().getDot());
                } finally {
                    sp.addCaretListener(reshower);
                }
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getModifiers() == 0) {
                assert lookupList == e.getSource();
                if (!lookupList.isSelectionEmpty()) {
                    try {
                        insertLookedUp(new ActionEvent(sp, 0, "replace"));
                        lookupPopup.setVisible(false);
                    } catch (BadLocationException ex) {
                        Logger.getLogger(SyntaxEditorKit.class.getName()).log(Level.SEVERE, null, ex);
                        lookupPopup.setVisible(false);
                    }
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!e.isConsumed() && e.getClickCount() > 1 && !e.isShiftDown() && !e.isControlDown() && !e.isAltDown() && e.getButton() == MouseEvent.BUTTON1) {
                assert lookupList == e.getSource();
                if (!lookupList.isSelectionEmpty()) {
                    try {
                        insertLookedUp(new ActionEvent(sp, 0, "replace"));
                        lookupPopup.setVisible(false);
                    } catch (BadLocationException ex) {
                        Logger.getLogger(SyntaxEditorKit.class.getName()).log(Level.SEVERE, null, ex);
                        lookupPopup.setVisible(false);
                    }
                }
            }
        }
    }

    protected class LookupListRemover implements PopupMenuListener {

        protected JPopupMenu popup;

        public LookupListRemover(JPopupMenu aPopup) {
            super();
            popup = aPopup;
        }

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            if (popup == gLookupPopup) {
                gLookupList = null;
                gLookupPopup = null;
            }
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            if (popup == gLookupPopup) {
                gLookupList = null;
                gLookupPopup = null;
            }
        }
    }
    protected JList gLookupList;
    protected JPopupMenu gLookupPopup;

    protected String extractLastSentence(String aLine) {
        for (int i = aLine.length() - 1; i >= 0; i--) {
            String token = aLine.substring(i, i + 1);
            if (!token.equals(".") && getHighlighting().isSeparartor(token)) {
                return aLine.substring(i + 1);
            }
        }
        return aLine;
    }

    protected void processLookup(JSyntaxPane aSp, int lineNumber, String toLeft, String wholeLine) {
        if (lookup != null) {
            try {
                Rectangle rt = aSp.modelToView(aSp.getCaret().getDot());
                if (rt != null) {
                    Point cusorPt = rt.getLocation();
                    cusorPt.x += rt.width - 10;
                    cusorPt.y += rt.height;
                    gLookupPopup = new JPopupMenu();
                    gLookupPopup.addPopupMenuListener(new LookupListRemover(gLookupPopup));
                    String lastSentence = extractLastSentence(toLeft);
                    ArrayList<Object> lookupItems = lookup.getAvailableItems(lastSentence, wholeLine);
                    if (!lookupItems.isEmpty()) {
                        gLookupList = new JList(lookupItems.toArray());
                        if (lookup.getRenderer() != null) {
                            gLookupList.setCellRenderer(lookup.getRenderer());
                        }
                        gLookupList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        LookupReshower reshower = new LookupReshower(aSp);
                        LookupInserter listener = new LookupInserter(gLookupList, gLookupPopup, aSp, reshower);
                        gLookupList.addMouseListener(listener);
                        gLookupList.addKeyListener(listener);
                        gLookupPopup.add(new JScrollPane(gLookupList));
                        aSp.addCaretListener(reshower);
                        gLookupPopup.show(aSp, cusorPt.x + 4, cusorPt.y);
                        aSp.requestFocus();
                        String[] sentences = toLeft.split(";");
                        if (sentences != null && sentences.length > 0) {
                            if (lastSentence.endsWith(".")) {
                                lastSentence += " ";
                            }
                            String[] expressions = lastSentence.split("\\.");
                            if (expressions != null && expressions.length > 0) {
                                String lastExpression = expressions[expressions.length - 1];
                                lastExpression = lastExpression.trim();
                                for (int i = 0; i < lookupItems.size(); i++) {
                                    Object oItem = lookupItems.get(i);
                                    String sItem = lookup.convertItem2String(oItem);
                                    if (sItem.toLowerCase().startsWith(lastExpression.toLowerCase())) {
                                        gLookupList.setSelectedIndex(i);
                                        gLookupList.ensureIndexIsVisible(i);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (BadLocationException ex) {
                Logger.getLogger(SyntaxEditorKit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public int firstDelimiterIndex(String aText) {
        if (highlighting != null && aText != null && !aText.isEmpty()) {
            for (int i = 0; i < aText.length(); i++) {
                if (highlighting.isSeparartor(aText.substring(i, i + 1))) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int lastDelimiterIndex(String aText) {
        if (highlighting != null && aText != null && !aText.isEmpty()) {
            for (int i = aText.length() - 1; i >= 0; i--) {
                if (highlighting.isSeparartor(aText.substring(i, i + 1))) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public Document createDefaultDocument() {
        return new StyledScriptDocument(doclistener);
    }
}
