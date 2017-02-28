package com.eas.client.metadata;

import com.eas.script.ScriptFunction;
import java.sql.ParameterMetaData;

/**
 * This class is a parameter specification for queries, forms, reports and
 * datamodules. It holds information about field as the <code>Field</code> class
 * and additional information about parameter mode, value, default value and
 * it's selection form. If <code>getFk()</code> returns reference to a
 * <code>PrimaryKeySpec</code>, than it is a foreign key in corresponding query,
 * form, report or datamodule, and it references to returning
 * <code>PrimaryKeySpec</code>.
 *
 * @author mg
 */
public class Parameter extends Field {

    private int mode = ParameterMetaData.parameterModeIn;
    private String selectionForm;
    private Object defaultValue;
    private Object value;
    private boolean modified;

    /**
     * The default constructor.
     */
    public Parameter() {
        super();
    }

    /**
     * Constructor with name.
     *
     * @param aName Name of the created parameter.
     */
    public Parameter(String aName) {
        super(aName);
    }

    /**
     * Constructor with name and description.
     *
     * @param aName Name of the created parameter.
     * @param aDescription Description of the created parameter.
     */
    public Parameter(String aName, String aDescription) {
        super(aName, aDescription);
    }

    /**
     * Constructor with name, description and typeInfo.
     *
     * @param aName Name of the created parameter.
     * @param aDescription Description of the created parameter.
     * @param aType Type name of the created parameter.
     */
    public Parameter(String aName, String aDescription, String aType) {
        super(aName, aDescription, aType);
    }

    /**
     * Copy constructor of parameter using base field information.
     *
     * @param aSourceField Source of created parameter.
     */
    public Parameter(Field aSourceField) {
        super(aSourceField);
    }

    /**
     * Copy constructor of parameter using full parameter information.
     *
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
     *
     * @param obj The another object to test the equality.
     * @return If this Parameter is equal to another parameter object.
     */
    @Override
    public boolean isEqual(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        if (!super.isEqual(obj)) {
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
    /*
     @Override
     public int hashCode() {
     int hash = super.hashCode();
     hash = 67 * hash + this.mode;
     hash = 67 * hash + Objects.hashCode(this.selectionForm);
     hash = 67 * hash + Objects.hashCode(this.defaultValue);
     hash = 67 * hash + Objects.hashCode(this.value);
     hash = 67 * hash + (this.modified ? 1 : 0);
     return hash;
     }
     */

    /**
     * Assigning method of field/parameter information.
     *
     * @param aSourceField Source of assigning.
     */
    @Override
    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Assigning method of field/parameter information using specified source.\n"
            + " * @params sourceField \n"
            + " */", params = {"sourceField"})
    public void assignFrom(Field aSourceField) {
        super.assignFrom(aSourceField);
        if (aSourceField instanceof Parameter) {
            Parameter param = (Parameter) aSourceField;
            if (getMode() != param.getMode()) {
                setMode(param.getMode());
            }
            if (!equalsOrNulls(getDefaultValue(), param.getDefaultValue())) {
                setDefaultValue(param.getDefaultValue());
            }
            if (!equalsOrNulls(getValue(), param.getValue())) {
                setValue(param.getValue());
            }
            if (isModified() != param.isModified()) {
                setModified(param.isModified());
            }
        }
    }

    /**
     * Returns parameter's mode.
     *
     * @return Parameter's mode.
     */
    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Parameter's mode (in, out, in/out).\n"
            + " */")
    public int getMode() {
        return mode;
    }

    /**
     * Sets the parameter's mode (in, out, in/out).
     *
     * @param aValue The mode.
     */
    @ScriptFunction
    public void setMode(int aValue) {
        int oldValue = mode;
        mode = aValue;
        changeSupport.firePropertyChange("mode", oldValue, aValue);
    }

    /**
     * Returns parameter's default value.
     *
     * @return Parameter's default value.
     */
    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The default value of the parameter.\n"
            + " */")
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value of the parameter.
     *
     * @param aValue A value to be set as the default value
     */
    @ScriptFunction
    public void setDefaultValue(Object aValue) {
        Object oldValue = defaultValue;
        defaultValue = aValue;
        changeSupport.firePropertyChange("defaultValue", oldValue, aValue);
    }

    /**
     * Returns parameter's value.
     *
     * @return Parameter's value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the value of the parameter.
     *
     * @param aValue A value to be set as the parameter's value.
     */
    public void setValue(Object aValue) {
        Object oldValue = value;
        if (!readonly) {
            value = aValue;
            modified = true;
            changeSupport.firePropertyChange("value", oldValue, aValue);
        }
    }

    @ScriptFunction(name = "value", jsDoc = ""
            + "/**\n"
            + " * The value of the parameter.\n"
            + " */")
    public Object getJsValue() {
        return getValue();
    }

    @ScriptFunction
    public void setJsValue(Object aValue) {
        setValue(aValue);
    }

    /**
     * Gets parameter's value as a String, whether it feasible. The result
     * exists only for non-null values and some simple types.
     *
     * @return String representing Parameter's value, this value can be used to
     * set the value using <code>setValueByString()</code>.
     * @throws Exception if parameter's value not to be converted.
     */
    public String getValueAsString() throws Exception {
        if (getValue() != null) {
            if (getValue() instanceof java.math.BigDecimal
                    || getValue() instanceof Float
                    || getValue() instanceof Double
                    || getValue() instanceof Short
                    || getValue() instanceof Integer
                    || getValue() instanceof Boolean
                    || getValue() instanceof String) {
                return String.valueOf(getValue());
            } else if (getValue() instanceof java.util.Date) {
                return String.valueOf(((java.util.Date) getValue()).getTime());
            } else {
                throw new IllegalStateException();
            }
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Sets the value of the parameter using a String in the format compatible
     * with <code>getValueAsString()</code> method.
     *
     * @throws Exception if operation fails.
    public void setValueByString(String aValue) throws Exception {
        if (aValue != null) {
            if (getType() != null) {
                if (getTypeInfo().javaClassName.equals(String.class.getName())) {
                    value = aValue;
                } else if (getTypeInfo().javaClassName.equals(java.math.BigDecimal.class.getName())) {
                    value = new BigDecimal(aValue);
                } else if (getTypeInfo().javaClassName.equals(Float.class.getName())) {
                    value = Float.valueOf(aValue);
                } else if (getTypeInfo().javaClassName.equals(Double.class.getName())) {
                    value = Double.valueOf(aValue);
                } else if (getTypeInfo().javaClassName.equals(Short.class.getName())) {
                    value = Short.valueOf(aValue);
                } else if (getTypeInfo().javaClassName.equals(Integer.class.getName())) {
                    value = Integer.valueOf(aValue);
                } else if (getTypeInfo().javaClassName.equals(Boolean.class.getName())) {
                    value = Boolean.valueOf(aValue);
                } else if (getTypeInfo().javaClassName.equals(java.util.Date.class.getName())) {
                    value = new java.util.Date(Long.valueOf(aValue));
                } else {
                    throw new IllegalStateException();
                }
            } else {
                throw new IllegalStateException();
            }
        } else {
            throw new IllegalArgumentException("Parameter must not be null."); //NOI18N
        }
    }
     */

    /**
     * Returns selection form of the parameter.
     *
     * @return Selection form of the parameter.
     */
    public String getSelectionForm() {
        return selectionForm;
    }

    /**
     * Sets the selection form of the parameter.
     *
     * @param aValue Selection form of the parameter.
     */
    public void setSelectionForm(String aValue) {
        String oldValue = selectionForm;
        selectionForm = aValue;
        changeSupport.firePropertyChange("selectionForm", oldValue, aValue);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Determines if parameter was modified.\n"
            + " */")
    public boolean isModified() {
        return modified;
    }

    @ScriptFunction
    public void setModified(boolean aValue) {
        boolean oldValue = modified;
        modified = aValue;
        changeSupport.firePropertyChange("modified", oldValue, aValue);
    }

    /**
     * Copies the parameter information to another instance.
     *
     * @return Copied parameter.
     */
    @Override
    public Parameter copy() {
        return new Parameter(this);
    }

}
