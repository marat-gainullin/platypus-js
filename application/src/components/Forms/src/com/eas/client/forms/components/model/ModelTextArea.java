/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model;

import com.eas.client.forms.components.rt.HasEditable;
import com.eas.client.forms.components.rt.HasEmptyText;
import com.eas.client.forms.components.rt.VTextArea;
import com.eas.script.ScriptFunction;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 *
 * @author mg
 *       
 *      if(aValue instanceof Number){
            Number n = (Number)aValue;
            DecimalFormat df = new DecimalFormat("#.#");
            aValue = df.format(n.doubleValue());
        }

 */
public class ModelTextArea extends ModelComponentDecorator<VTextArea, String> implements HasEmptyText, HasEditable{

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A model components for a text area.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelTextArea() {
        super();
        setDecorated(new VTextArea());
    }

    @ScriptFunction(jsDoc = EDITABLE_JSDOC)
    @Override
    public boolean getEditable() {
        return decorated.isEditable();
    }

    @ScriptFunction
    @Override
    public void setEditable(boolean aValue) {
        decorated.setEditable(aValue);
    }

    @ScriptFunction
    @Override
    public String getEmptyText() {
        return decorated.getEmptyText();
    }

    @ScriptFunction
    @Override
    public void setEmptyText(String aValue) {
        decorated.setEmptyText(aValue);
    }

    @ScriptFunction
    public String getText() throws Exception {
        return decorated.getText();
    }

    @ScriptFunction
    public void setText(String aValue) throws Exception {
        decorated.setText(aValue);
    }

    @Override
    protected void setupCellRenderer(JTable table, int row, int column, boolean isSelected) {
        remove(decorated);
        JLabel rendererLine = new JLabel(decorated.getText());
        rendererLine.setOpaque(false);
        add(rendererLine, BorderLayout.CENTER);
    }

    @Override
    public boolean isFieldContentModified() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
