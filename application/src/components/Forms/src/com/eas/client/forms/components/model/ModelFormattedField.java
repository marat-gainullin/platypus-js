/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model;

import com.eas.client.forms.components.rt.HasEditable;
import com.eas.client.forms.components.rt.HasEmptyText;
import com.eas.client.forms.components.rt.VFormattedField;
import com.eas.script.ScriptFunction;
import java.awt.BorderLayout;
import java.text.ParseException;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 *
 * @author mg
 */
public class ModelFormattedField extends ModelComponentDecorator<VFormattedField, Object> implements HasEmptyText, HasEditable {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A model component that shows a date.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelFormattedField() {
        super();
        setDecorated(new VFormattedField());
        setBackground(getDecorated().getBackground());
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

    private static final String FORMAT_JSDOC = ""
            + "/**\n"
            + "* The format string of the component.\n"
            + "*/";

    @ScriptFunction(jsDoc = FORMAT_JSDOC)
    public String getFormat() {
        return decorated.getFormat();
    }

    @ScriptFunction
    public void setFormat(String aValue) throws ParseException {
        decorated.setFormat(aValue);
    }

    @ScriptFunction(jsDoc = ""
            + "/**"
            + " * ValueType hint for the field. It is used to determine, how to interpret format pattern."
            + " */")
    public int getValueType(){
        return decorated.getValueType();
    }
    
    @ScriptFunction
    public void setValueType(int aValue){
        decorated.setValueType(aValue);
    }
    
    @ScriptFunction
    public String getText() throws Exception {
        return decorated.getText();
    }

    @ScriptFunction
    public void setText(String aValue) throws Exception {
        decorated.setText(aValue);
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
    
    @Override
    protected void setupCellRenderer(JTable table, int row, int column, boolean isSelected) {
        remove(decorated);
        JLabel rendererLine = new JLabel(decorated.getText());
        rendererLine.setOpaque(false);
        add(rendererLine, BorderLayout.CENTER);
    }

    @Override
    public boolean isFieldContentModified() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
