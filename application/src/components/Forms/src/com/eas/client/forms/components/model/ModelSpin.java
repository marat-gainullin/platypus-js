/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model;

import com.eas.client.forms.components.rt.HasEmptyText;
import com.eas.client.forms.components.rt.VSpinner;
import com.eas.script.ScriptFunction;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 *
 * @author mg
 */
public class ModelSpin extends ModelComponentDecorator<VSpinner, Double> implements HasEmptyText{

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A model component that represents a combination of a numeric text box and arrow buttons to change the value incrementally. \n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelSpin() {
        super();
        setDecorated(new VSpinner());
    }
    
    private static final String EDITABLE_JSDOC = ""
            + "/**\n"
            + " * Determines if component is editable.\n"
            + " */";

    @ScriptFunction(jsDoc = EDITABLE_JSDOC)
    public boolean getEditable() {
        return decorated.getEditable();
    }

    @ScriptFunction
    public void setEditable(boolean aValue) {
        decorated.setEditable(aValue);
    }
    private static final String MIN_JSDOC = ""
            + "/**\n"
            + "* Determines the lower bound of spinner's value. If it's null, valus is unlimited at lower bound.\n"
            + "*/";

    @ScriptFunction(jsDoc = MIN_JSDOC)
    public Double getMin() {
        return decorated.getMin();
    }

    @ScriptFunction
    public void setMin(Double aValue) throws Exception {
        decorated.setMin(aValue);
    }
    private static final String MAX_JSDOC = ""
            + "/**\n"
            + "* Determines the upper bound of spinner's value. If it's null, valus is unlimited at upper bound.\n"
            + "*/";

    @ScriptFunction(jsDoc = MAX_JSDOC)
    public Double getMax() {
        return decorated.getMax();
    }

    @ScriptFunction
    public void setMax(Double aValue) throws Exception {
        decorated.setMax(aValue);
    }
    private static final String STEP_JSDOC = ""
            + "/**\n"
            + "* Determines the spinner's value change step. Can't be null.\n"
            + "*/";

    @ScriptFunction(jsDoc = STEP_JSDOC)
    public double getStep() {
        return decorated.getStep();
    }

    @ScriptFunction
    public void setStep(double aValue) throws Exception {
        decorated.setStep(aValue);
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
    public String getText() {
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
