/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.dbcontrols.spin.DbSpin;
import com.eas.script.ScriptFunction;

/**
 *
 * @author mg
 */
public class ModelSpin extends ScalarModelComponent<DbSpin> {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A model component that represents a combination of a numeric text box and arrow buttons to change the value incrementally. \n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelSpin() {
        super();
        setDelegate(new DbSpin());
    }

    protected ModelSpin(DbSpin aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    private static final String EDITABLE_JSDOC = ""
            + "/**\n"
            + " * Determines if component is editable.\n"
            + " */";

    @ScriptFunction(jsDoc = EDITABLE_JSDOC)
    public boolean getEditable() {
        return delegate.isEditable();
    }

    @ScriptFunction
    public void setEditable(boolean aValue) {
        delegate.setEditable(aValue);
    }
    private static final String MIN_JSDOC = ""
            + "/**\n"
            + "* Determines the lower bound of spinner's value. If it's null, valus is unlimited at lower bound.\n"
            + "*/";

    @ScriptFunction(jsDoc = MIN_JSDOC)
    public Double getMin() {
        return delegate.getMin();
    }

    @ScriptFunction
    public void setMin(Double aValue) throws Exception {
        delegate.setMin(aValue);
    }
    private static final String MAX_JSDOC = ""
            + "/**\n"
            + "* Determines the upper bound of spinner's value. If it's null, valus is unlimited at upper bound.\n"
            + "*/";

    @ScriptFunction(jsDoc = MAX_JSDOC)
    public Double getMax() {
        return delegate.getMax();
    }

    @ScriptFunction
    public void setMax(Double aValue) throws Exception {
        delegate.setMax(aValue);
    }
    private static final String STEP_JSDOC = ""
            + "/**\n"
            + "* Determines the spinner's value change step. Can't be null.\n"
            + "*/";

    @ScriptFunction(jsDoc = STEP_JSDOC)
    public double getStep() {
        return delegate.getStep();
    }

    @ScriptFunction
    public void setStep(double aValue) throws Exception {
        delegate.setStep(aValue);
    }
    
    @ScriptFunction
    public String getEmptyText() {
        return delegate.getEmptyText();
    }

    @ScriptFunction
    public void setEmptyText(String aValue) {
        delegate.setEmptyText(aValue);
    }

}
