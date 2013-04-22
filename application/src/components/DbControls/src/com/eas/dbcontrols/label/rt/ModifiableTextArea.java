/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.label.rt;

import com.eas.dbcontrols.InitializingMethod;
import java.awt.Rectangle;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 *
 * @author mg
 */
public class ModifiableTextArea extends JTextArea {

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
    protected boolean modified = false;
    protected boolean borderless;
    protected TextListener listener = new TextListener();
    protected InitializingMethod kind = InitializingMethod.UNDEFINED;

    public ModifiableTextArea(InitializingMethod aMethod, boolean aIsBorderless) {
        super();
        kind = aMethod;
        borderless = aIsBorderless;
        if (borderless) {
            setBorder(null);
        }
    }

    public boolean isBorderless() {
        return borderless;
    }

    public void setBorderless(boolean borderless) {
        this.borderless = borderless;
    }

    @Override
    public void setText(String t) {
        Document doc = getDocument();
        if (doc != null) {
            doc.removeDocumentListener(listener);
            modified = false;
        }
        super.setText(t);
        doc = getDocument();
        if (doc != null) {
            doc.addDocumentListener(listener);
        }
    }

    public boolean isModified() {
        return modified;
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
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
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void validate() {
        if (kind == InitializingMethod.EDITOR) {
            super.validate();
        }
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void revalidate() {
        if (kind == InitializingMethod.EDITOR) {
            super.revalidate();
        }
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        if (kind == InitializingMethod.EDITOR) {
            super.repaint(tm, x, y, width, height);
        }
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void repaint(Rectangle r) {
        if (kind == InitializingMethod.EDITOR) {
            super.repaint(r);
        }
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     *
     * @since 1.5
     */
    @Override
    public void repaint() {
        if (kind == InitializingMethod.EDITOR) {
            super.repaint();
        }
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
}
