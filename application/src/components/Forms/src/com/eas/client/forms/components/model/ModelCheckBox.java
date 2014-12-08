/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model;

import com.eas.client.forms.components.rt.VCheckBox;
import com.eas.script.ScriptFunction;
import javax.swing.JTable;

/**
 *
 * @author mg
 */
public class ModelCheckBox extends ModelComponentDecorator<VCheckBox, Boolean> {

    public ModelCheckBox() {
        super();
        setDecorated(new VCheckBox());
        setOpaque(false);
    }

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * An implementation of a model check box -- an item that can be selected or deselected, and which displays its state to the user.\n"
            + " * @param text the text of the component (optional).\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"text"})
    public ModelCheckBox(String aText) throws Exception {
        this();
        decorated.setText(aText);
    }

    @Override
    public void setDecorated(VCheckBox aComponent) {
        super.setDecorated(aComponent);
        if (decorated != null) {
            decorated.setOpaque(false);
        }
    }

    private static final String TEXT_JSDOC = ""
            + "/**\n"
            + "* Text on the check box."
            + "*/";

    @ScriptFunction(jsDoc = TEXT_JSDOC)
    public String getText() {
        return decorated.getText();
    }

    @ScriptFunction
    public void setText(String aValue) {
        decorated.setText(aValue);
    }

    @Override
    protected void setupCellRenderer(JTable table, int row, int column, boolean isSelected) {
    }

    @Override
    public boolean isFieldContentModified() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
