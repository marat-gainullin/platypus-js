/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.label.rt;

import com.eas.dbcontrols.InitializingMethod;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import javax.swing.Action;
import javax.swing.JFormattedTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author mg
 */
public class ModifiableTextField extends JFormattedTextField {

    protected class TextListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            modified = true;
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            modified = true;
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    }

    protected class TextKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                fireActionPerformed();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    protected class TextFocusListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
        }

        @Override
        public void focusLost(FocusEvent e) {
            Action commitAction = getAction();
            if (commitAction != null) {
                commitAction.actionPerformed(new ActionEvent(ModifiableTextField.this, 0, ""));
            }
        }
    }
    protected boolean modified = false;
    protected boolean borderless;
    protected TextListener listener = new TextListener();
    protected InitializingMethod kind = InitializingMethod.UNDEFINED;
    protected String mask = null;

    public ModifiableTextField(InitializingMethod aMethod, AbstractFormatterFactory aFormatterFactory, boolean aBorderless) {
        super();
        kind = aMethod;
        setFormatterFactory(aFormatterFactory);
        borderless = aBorderless;
        if (borderless) {
            setBorder(null);
        }
        addKeyListener(new TextKeyListener());
        addFocusListener(new TextFocusListener());
        getDocument().addDocumentListener(listener);
    }
    
    public boolean isTextValue() {
        return getFormatter() instanceof MaskFormatter;
    }

    @Override
    public void commitEdit() throws ParseException {
        super.commitEdit();
        modified = true;
    }

    public void setModified(boolean aValue) {
        modified = aValue;
    }

    public boolean isModified() {
        return modified;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (borderless) {
            setBorder(null);
        }
    }

    @Override
    public void setBorder(Border border) {
        if (borderless) {
            super.setBorder(null);
        } else {
            super.setBorder(border);
        }
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     *
     * @since 1.5
     */
    @Override
    public void invalidate() {
        if (kind == InitializingMethod.EDITOR) {
            super.invalidate();
        }
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void validate() {
        if (kind == InitializingMethod.EDITOR) {
            super.validate();
        }
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void revalidate() {
        if (kind == InitializingMethod.EDITOR) {
            super.revalidate();
        }
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        if (kind == InitializingMethod.EDITOR) {
            super.repaint(tm, x, y, width, height);
        }
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void repaint(Rectangle r) {
        if (kind == InitializingMethod.EDITOR) {
            super.repaint(r);
        }
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     *
     * @since 1.5
     */
    @Override
    public void repaint() {
        if (kind == InitializingMethod.EDITOR) {
            super.repaint();
        }
    }
}
