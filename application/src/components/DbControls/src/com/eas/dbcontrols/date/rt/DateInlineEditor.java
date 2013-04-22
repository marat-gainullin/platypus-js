/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.date.rt;

import java.awt.Component;
import javax.swing.ComboBoxEditor;
import javax.swing.JFormattedTextField;

/**
 *
 * @author mg
 */
public class DateInlineEditor extends JFormattedTextField implements ComboBoxEditor {

    public DateInlineEditor(AbstractFormatterFactory factory) {
        super(factory);
    }

    @Override
    public Component getEditorComponent() {
        return this;
    }

    @Override
    public void setItem(Object anObject) {
    }

    @Override
    public Object getItem() {
        return null;
    }
}
