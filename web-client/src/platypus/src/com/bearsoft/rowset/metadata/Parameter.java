/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.bearsoft.rowset.metadata;

import com.bearsoft.rowset.Utils;

import java.sql.ParameterMetaData;

/**
 * This class is a parameter specification for queries, forms, reports and datamodules.
 * It holds information about field as the <code>Field</code> class and additional
 * information about parameter mode, value, default value and it's selection form.
 * If <code>getFk()</code> returns reference to a <code>PrimaryKeySpec</code>, than it is a foreign key
 * in corresponding query, form, report or datamodule, and it references to returning <code>PrimaryKeySpec</code>.
 * @author mg
 */
public class Parameter extends Field {

    protected int mode = ParameterMetaData.parameterModeIn;
    protected Double selectionForm = null;
    protected Object defaultValue = null;
    protected Object value = null;
    protected boolean modified = false;

    /**
     * The default constructor.
     */
    public Parameter() {
        super();
    }

    /**
     * Constructor with name.
     * @param aName Name of the created parameter.
     */
    public Parameter(String aName) {
        super(aName);
    }

    /**
     * Constructor with name and description.
     * @param aName Name of the created parameter.
     * @param aDescription Description of the created parameter.
     */
    public Parameter(String aName, String aDescription) {
        super(aName, aDescription);
    }

    /**
     * Constructor with name, description and typeInfo.
     * @param aName Name of the created parameter.
     * @param aDescription Description of the created parameter.
     * @param aTypeInfo Type info of the created parameter.
     * @see DataTypeInfo
     */
    public Parameter(String aName, String aDescription, DataTypeInfo aTypeInfo) {
        super(aName, aDescription, aTypeInfo);
    }

    public Parameter(String aName, String aDescription, int aSize, DataTypeInfo aTypeInfo) {
        super(aName, aDescription, aSize, aTypeInfo);
    }
    /**
     * Copy constructor of parameter using base field information.
     * @param aSourceParam Source of created parameter.
     */
    public Parameter(Field aSourceField) {
        super(aSourceField);
    }

    /**
     * Copy constructor of parameter using full parameter information.
     * @param aSourceParam Source of created parameter.
     */
    public Parameter(Parameter aSourceParam) {
        super(aSourceParam);
        mode = aSourceParam.getMode();
        selectionForm = aSourceParam.getSelectionForm();
        value = aSourceParam.getValue();
        defaultValue = aSourceParam.getDefaultValue();
        modified = aSourceParam.isModified();
    }

    /**
     * Tests if this Parameter is equal to another parameter object.
     * @param obj The another object to test the equality.
     * @return If this Parameter is equal to another parameter object.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        Parameter sp = (Parameter) obj;
        if (mode != sp.getMode()) {
            return false;
        }
        if (selectionForm == null && sp.getSelectionForm() != null) {
            return false;
        }
        if (selectionForm != null && !selectionForm.equals(sp.getSelectionForm())) {
            return false;
        }
        if (value == null && sp.getValue() != null) {
            return false;
        }
        if (value != null && !value.equals(sp.getValue())) {
            return false;
        }
        if (defaultValue == null && sp.getDefaultValue() != null) {
            return false;
        }
        if (defaultValue != null && !defaultValue.equals(sp.getDefaultValue())) {
            return false;
        }
        if (modified != sp.isModified()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 47 * hash + this.mode;
        return hash;
    }

    /**
     * Assigning method of field/parameter information.
     * @param aSourceField Source of assigning.
     */
    @Override
    public void assignFrom(Field aSourceField) {
        super.assignFrom(aSourceField);
        if (aSourceField instanceof Parameter) {
            Parameter param = (Parameter) aSourceField;
            setMode(param.getMode());
            Object dv = param.getDefaultValue();
            if (dv != null) {
                setDefaultValue(dv);
            } else {
                setDefaultValue(null);
            }
            Object v = param.getValue();
            if (v != null) {
                setValue(v);
            } else {
                setValue(null);
            }
            setModified(param.isModified());
        }
    }

    /**
     * Returns parameter's mode.
     * @return Parameter's mode.
     */
    public int getMode() {
        return mode;
    }

    /**
     * Sets the parameter's mode (in, out, in/out).
     * @param aValue The mode.
     */
    public void setMode(int aValue) {
        int oldValue = mode;
        mode = aValue;
    }

    /**
     * Returns parameter's default value.
     * @return Parameter's default value.
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value of the parameter.
     * @param defaultValue A value to be set as the default value
     */
    public void setDefaultValue(Object aValue) {
        defaultValue = aValue;
    }

    /**
     * Returns parameter's value.
     * @return Parameter's value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the value of the parameter.
     * @param aValue A value to be set as the parameter's value.
     */
    public void setValue(Object aValue) {
        //Object oldValue = value;
        if (!readonly) {
            value = aValue;
            modified = true;
        }
    }

    public Object getJsValue()
    {
    	return Utils.toJs(getValue());
    }
    
    public void setJsValue(Object aValue) throws Exception
    {
    	setValue(Utils.toJava(aValue));
    }
    
    /**
     * Returns selection form of the parameter.
     * @return Selection form of the parameter.
     */
    public Double getSelectionForm() {
        return selectionForm;
    }

    /**
     * Sets the selection form of the parameter.
     * @param selectionForm Selection form of the parameter.
     */
    public void setSelectionForm(Double aValue) {
        selectionForm = aValue;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean aValue) {
        modified = aValue;
    }

    /**
     * Copies the parameter information to another instance.
     * @return Copied parameter.
     */
    @Override
    public Parameter copy() {
        return new Parameter(this);
    }
}
