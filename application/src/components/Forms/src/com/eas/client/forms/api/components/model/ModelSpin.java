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

    protected ModelSpin(DbSpin aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public ModelSpin() {
        super();
        setDelegate(new DbSpin());
    }

    @ScriptFunction(jsDoc = "Determines if component is editable.")
    public boolean isEditable() {
        return delegate.isEditable();
    }

    @ScriptFunction
    public void setEditable(boolean aValue) {
        delegate.setEditable(aValue);
    }

    @ScriptFunction(jsDoc = "Determines lower bound of spinner's value. If it's null, valus is unlimited at lower bound.")
    public Double getMin() {
        return delegate.getMin();
    }

    @ScriptFunction
    public void setMin(Double aValue) throws Exception
    {
        delegate.setMin(aValue);
    }
    
    @ScriptFunction(jsDoc = "Determines upper bound of spinner's value. If it's null, valus is unlimited at upper bound.")
    public Double getMax() {
        return delegate.getMax();
    }

    @ScriptFunction
    public void setMax(Double aValue) throws Exception
    {
        delegate.setMax(aValue);
    }
    
    @ScriptFunction(jsDoc = "Determines spinner's value change step. Can't be null.")
    public double getStep() {
        return delegate.getStep();
    }
    
    @ScriptFunction
    public void setStep(double aValue) throws Exception
    {
        delegate.setStep(aValue);
    }
}
