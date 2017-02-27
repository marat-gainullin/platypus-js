package com.eas.client.metadata;

import com.eas.core.Utils;
import com.google.gwt.core.client.JavaScriptObject;

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

	protected int mode = ParameterMetaData.parameterModeIn;
	protected String selectionForm;
	protected Object defaultValue;
	protected Object value;
	protected boolean modified;

	/**
	 * The default constructor.
	 */
	public Parameter() {
		super();
	}

	/**
	 * Constructor with name.
	 * 
	 * @param aName
	 *            Name of the created parameter.
	 */
	public Parameter(String aName) {
		super(aName);
	}

	/**
	 * Constructor with name and description.
	 * 
	 * @param aName
	 *            Name of the created parameter.
	 * @param aDescription
	 *            Description of the created parameter.
	 */
	public Parameter(String aName, String aDescription) {
		super(aName, aDescription);
	}

	/**
	 * Copy constructor of parameter using base field information.
	 * 
	 * @param aSourceParam
	 *            Source of created parameter.
	 */
	public Parameter(Field aSourceField) {
		super(aSourceField);
	}

	/**
	 * Copy constructor of parameter using full parameter information.
	 * 
	 * @param aSourceParam
	 *            Source of created parameter.
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
	 * @param obj
	 *            The another object to test the equality.
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
	 * 
	 * @param aSourceField
	 *            Source of assigning.
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
	 * 
	 * @return Parameter's mode.
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Sets the parameter's mode (in, out, in/out).
	 * 
	 * @param aValue
	 *            The mode.
	 */
	public void setMode(int aValue) {
		mode = aValue;
	}

	/**
	 * Returns parameter's default value.
	 * 
	 * @return Parameter's default value.
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the default value of the parameter.
	 * 
	 * @param defaultValue
	 *            A value to be set as the default value
	 */
	public void setDefaultValue(Object aValue) {
		defaultValue = aValue;
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
	 * @param aValue
	 *            A value to be set as the parameter's value.
	 */
	public void setValue(Object aValue) {
		// Object oldValue = value;
		if (!readonly) {
			value = aValue;
			modified = true;
		}
	}

	public Object getJsValue() {
		Object oValue = getValue();
		return Utils.toJs(oValue);
	}

	public void setJsValue(Object aValue) throws Exception {
		setValue(Utils.toJava(aValue));
	}

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
	 * @param selectionForm
	 *            Selection form of the parameter.
	 */
	public void setSelectionForm(String aValue) {
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
	 * 
	 * @return Copied parameter.
	 */
	@Override
	public Parameter copy() {
		return new Parameter(this);
	}

	@Override
	public void setPublished(JavaScriptObject aPublished) {
		super.setPublished(aPublished);
		if (jsPublished != null) {
			publishFacade(jsPublished, this);
		}
	}

	public static native void publishFacade(JavaScriptObject aTarget, Parameter aField)/*-{
		var B = @com.eas.core.Predefine::boxing;
		Object.defineProperty(aTarget, "modified", {
			get : function() {
				return aField.@com.eas.client.metadata.Parameter::isModified()();
			}
		});
		Object.defineProperty(aTarget, "value", {
			get : function() {
				return B.boxAsJs(aField.@com.eas.client.metadata.Parameter::getJsValue()());
			},
			set : function(aValue) {
				aField.@com.eas.client.metadata.Parameter::setJsValue(Ljava/lang/Object;)(B.boxAsJava(aValue));
			}
		});
	}-*/;
}
