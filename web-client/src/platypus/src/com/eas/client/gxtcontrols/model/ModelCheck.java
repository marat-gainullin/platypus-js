package com.eas.client.gxtcontrols.model;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.Utils;
import com.eas.client.gxtcontrols.converters.BooleanRowValueConverter;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterStandaloneField;
import com.eas.client.gxtcontrols.wrappers.handled.PlatypusCheckBoxHandledField;
import com.google.gwt.core.client.JavaScriptObject;
import com.sencha.gxt.cell.core.client.form.CheckBoxCell;

public class ModelCheck extends PlatypusAdapterStandaloneField<Boolean> {

	public ModelCheck() {
		super(new PlatypusCheckBoxHandledField(new CheckBoxCell()));
	}

	public void setPublishedField(JavaScriptObject aValue) {
		super.setPublishedField(aValue);
		if (publishedField != null) {
			publish(this, publishedField);
		}
	}

	protected native static void publish(ModelCheck aField, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "onSelect", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelCheck::getOnSelect()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelCheck::setOnSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "onRender", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelCheck::getOnRender()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelCheck::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "text", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelCheck::getText()();
			},
			set : function(aValue) {
				if(aValue != null)
					aField.@com.eas.client.gxtcontrols.model.ModelCheck::setText(Ljava/lang/String;)(''+aValue);
				else
					aField.@com.eas.client.gxtcontrols.model.ModelCheck::setText(Ljava/lang/String;)(null);
			}
		});
		Object.defineProperty(aPublished, "editable", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelCheck::isEditable()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelCheck::setEditable(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "selectOnly", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelCheck::isSelectOnly()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelCheck::setSelectOnly(Z)((false != aValue));
			}
		});
		
		Object.defineProperty(aPublished, "value", {
			get : function() {
				var javaValue = aField.@com.eas.client.gxtcontrols.model.ModelCheck::getValue()();
				if(javaValue == null)
					return null;
				else
					return javaValue.@java.lang.Boolean::booleanValue()();
			},
			set : function(aValue) {
				if (aValue != null) {
					var javaValue = $wnd.boxAsJava((false != aValue));
					aField.@com.eas.client.gxtcontrols.model.ModelCheck::setValue(Ljava/lang/Boolean;Z)(javaValue, true);
				} else {
					aField.@com.eas.client.gxtcontrols.model.ModelCheck::setValue(Ljava/lang/Boolean;Z)(null, true);
				}
			}
		});
		Object.defineProperty(aPublished, "field", {
			get : function() {
				return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aField.@com.eas.client.gxtcontrols.model.ModelCheck::getField()());
			},
			set : function(aValue) {
				if (aValue != null)
					aField.@com.eas.client.gxtcontrols.model.ModelCheck::setField(Lcom/bearsoft/rowset/metadata/Field;)(aValue.unwrap());
				else
					aField.@com.eas.client.gxtcontrols.model.ModelCheck::setField(Lcom/bearsoft/rowset/metadata/Field;)(null);
			}
		});
	}-*/;

	public ModelElementRef getModelElement() {
		return ((PlatypusCheckBoxHandledField) target).getModelElement();
	}

	public void setModelElement(ModelElementRef aValue) {
		((PlatypusCheckBoxHandledField) target).setModelElement(aValue);
	}

	public void setField(Field aField) throws Exception {
		super.setField(aField, new BooleanRowValueConverter());
	}

	public JavaScriptObject getOnRender() {
		return ((PlatypusCheckBoxHandledField) target).getCellFunction();
	}

	public void setOnRender(JavaScriptObject aValue) {
		((PlatypusCheckBoxHandledField) target).setCellFunction(aValue);
	}

	public PlatypusCheckBoxHandledField getTarget() {
		assert target instanceof PlatypusCheckBoxHandledField;
		return (PlatypusCheckBoxHandledField) target;
	}

	@Override
	public JavaScriptObject getOnSelect() {
		return super.getOnSelect();
	}

	@Override
	public void setOnSelect(JavaScriptObject aSelectFunction) {
		super.setOnSelect(aSelectFunction);
	}

	@Override
	public boolean isEditable() {
		return super.isEditable();
	}

	@Override
	public void setEditable(boolean aValue) {
		super.setEditable(aValue);
	}

	public String getText() {
		return getTarget().getBoxLabel();
	}

	public void setText(String aValue) {
		getTarget().setBoxLabel(aValue);
	}

	@Override
	public boolean isSelectOnly() {
		return super.isSelectOnly();
	}

	@Override
	public void setSelectOnly(boolean aValue) {
		super.setSelectOnly(aValue);
	}

	@Override
	public Boolean getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(Boolean value) {
		super.setValue(value);
	}

	public void setValue(Boolean value, boolean fireEvents) {
		super.setValue(value, fireEvents);
	}
}
