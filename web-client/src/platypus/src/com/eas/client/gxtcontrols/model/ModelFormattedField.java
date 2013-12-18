package com.eas.client.gxtcontrols.model;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.Utils;
import com.eas.client.gxtcontrols.ControlsUtils;
import com.eas.client.gxtcontrols.converters.ObjectRowValueConverter;
import com.eas.client.gxtcontrols.wrappers.component.ObjectFormat;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterStandaloneField;
import com.eas.client.gxtcontrols.wrappers.handled.PlatypusFormattedTextHandledField;
import com.eas.client.gxtcontrols.wrappers.handled.PlatypusFormattedTextHandledInputCell;
import com.google.gwt.core.client.JavaScriptObject;

public class ModelFormattedField extends PlatypusAdapterStandaloneField<Object> {

	public ModelFormattedField() {
		super(new PlatypusFormattedTextHandledField(new PlatypusFormattedTextHandledInputCell(null)));
	}

	public ModelFormattedField(ObjectFormat aFormat) {
		super(new PlatypusFormattedTextHandledField(new PlatypusFormattedTextHandledInputCell(aFormat)));
	}
	
	public void setPublishedField(JavaScriptObject aValue) {
		super.setPublishedField(aValue);
		if (publishedField != null) {
			publish(this, publishedField);
		}
	}

	protected native static void publish(ModelFormattedField aField, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "onSelect", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelFormattedField::getOnSelect()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelFormattedField::setOnSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "onRender", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelFormattedField::getOnRender()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelFormattedField::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "editable", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelFormattedField::isEditable()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelFormattedField::setEditable(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "selectOnly", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelFormattedField::isSelectOnly()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelFormattedField::setSelectOnly(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "value", {
			get : function() {
				return $wnd.boxAsJs(aField.@com.eas.client.gxtcontrols.model.ModelFormattedField::getJsValue()());
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelFormattedField::setJsValue(Ljava/lang/Object;)($wnd.boxAsJava(aValue));
			}
		});
		Object.defineProperty(aPublished, "field", {
			get : function() {
				return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aField.@com.eas.client.gxtcontrols.model.ModelFormattedField::getField()());
			},
			set : function(aValue) {
				if (aValue != null)
					aField.@com.eas.client.gxtcontrols.model.ModelFormattedField::setField(Lcom/bearsoft/rowset/metadata/Field;)(aValue.unwrap());
				else
					aField.@com.eas.client.gxtcontrols.model.ModelFormattedField::setField(Lcom/bearsoft/rowset/metadata/Field;)(null);
			}
		});
	}-*/;

	public ModelElementRef getModelElement() {
		return ((PlatypusFormattedTextHandledField) target).getModelElement();
	}

	public void setModelElement(ModelElementRef aValue) {
		((PlatypusFormattedTextHandledField) target).setModelElement(aValue);
	}

	public void setField(Field aField) throws Exception {
		super.setField(aField, new ObjectRowValueConverter());
	}

	public JavaScriptObject getOnRender() {
		assert target.getCell() instanceof PlatypusFormattedTextHandledInputCell;
		PlatypusFormattedTextHandledInputCell cell = (PlatypusFormattedTextHandledInputCell) target.getCell();
		return cell.getCellFunction();
	}

	public void setOnRender(JavaScriptObject aValue) {
		assert target.getCell() instanceof PlatypusFormattedTextHandledInputCell;
		PlatypusFormattedTextHandledInputCell cell = (PlatypusFormattedTextHandledInputCell) target.getCell();
		cell.setCellFunction(aValue);
	}

	public PlatypusFormattedTextHandledField getTarget() {
		assert target instanceof PlatypusFormattedTextHandledField;
		return (PlatypusFormattedTextHandledField) target;
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

	@Override
	public boolean isSelectOnly() {
		return super.isSelectOnly();
	}

	@Override
	public void setSelectOnly(boolean aValue) {
		super.setSelectOnly(aValue);
	}

	@Override
	public Object getValue() {
		return super.getValue();
	}

	public Object getJsValue() {
		return Utils.toJs(getValue());
	}

	@Override
	public void setValue(Object value) {
		super.setValue(value);
	}

	public void setJsValue(Object value) throws Exception {
		setValue(Utils.toJava(value), true);
	}

	@Override
	public void setValue(Object value, boolean fireEvents) {
		super.setValue(value, fireEvents);
	}
	
	public void setEmptyText(String aValue){
		ControlsUtils.setEmptyText(((PlatypusFormattedTextHandledField)target), aValue);
	}
	
	public String getEmptyText(){
		return ControlsUtils.getEmptyText(((PlatypusFormattedTextHandledField)target));
	}
	
}
