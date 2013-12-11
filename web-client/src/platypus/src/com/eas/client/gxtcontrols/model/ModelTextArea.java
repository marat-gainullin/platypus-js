package com.eas.client.gxtcontrols.model;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.gxtcontrols.converters.StringRowValueConverter;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterStandaloneField;
import com.eas.client.gxtcontrols.wrappers.handled.PlatypusFormattedTextHandledField;
import com.eas.client.gxtcontrols.wrappers.handled.PlatypusTextHandledArea;
import com.eas.client.gxtcontrols.wrappers.handled.PlatypusTextHandledAreaInputCell;
import com.google.gwt.core.client.JavaScriptObject;

public class ModelTextArea extends PlatypusAdapterStandaloneField<String> {

	public ModelTextArea() {
		super(new PlatypusTextHandledArea(new PlatypusTextHandledAreaInputCell()));
	}

	public void setPublishedField(JavaScriptObject aValue) {
		super.setPublishedField(aValue);
		if (publishedField != null) {
			publish(this, publishedField);
		}
	}

	protected native static void publish(ModelTextArea aField, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "onSelect", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelTextArea::getOnSelect()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelTextArea::setOnSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "onRender", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelTextArea::getOnRender()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelTextArea::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "editable", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelTextArea::isEditable()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelTextArea::setEditable(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "selectOnly", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelTextArea::isSelectOnly()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelTextArea::setSelectOnly(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "value", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelTextArea::getValue()();
			},
			set : function(aValue) {
				if (aValue != null)
					aField.@com.eas.client.gxtcontrols.model.ModelTextArea::setValue(Ljava/lang/String;)(''+aValue);
				else
					aField.@com.eas.client.gxtcontrols.model.ModelTextArea::setValue(Ljava/lang/String;)(null);
			}
		});
		Object.defineProperty(aPublished, "field", {
			get : function() {
				return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aField.@com.eas.client.gxtcontrols.model.ModelTextArea::getField()());
			},
			set : function(aValue) {
				if (aValue != null)
					aField.@com.eas.client.gxtcontrols.model.ModelTextArea::setField(Lcom/bearsoft/rowset/metadata/Field;)(aValue.unwrap());
				else
					aField.@com.eas.client.gxtcontrols.model.ModelTextArea::setField(Lcom/bearsoft/rowset/metadata/Field;)(null);
			}
		});
	}-*/;

	public ModelElementRef getModelElement() {
		return ((PlatypusTextHandledArea) target).getModelElement();
	}

	public void setModelElement(ModelElementRef aValue) {
		((PlatypusTextHandledArea) target).setModelElement(aValue);
	}
	
	public void setField(Field aField) throws Exception {
	    super.setField(aField, new StringRowValueConverter());
	}

	public JavaScriptObject getOnRender() {
		assert target.getCell() instanceof PlatypusTextHandledAreaInputCell;
		PlatypusTextHandledAreaInputCell cell = (PlatypusTextHandledAreaInputCell) target.getCell();
		return cell.getCellFunction();
	}

	public void setOnRender(JavaScriptObject aValue) {
		assert target.getCell() instanceof PlatypusTextHandledAreaInputCell;
		PlatypusTextHandledAreaInputCell cell = (PlatypusTextHandledAreaInputCell) target.getCell();
		cell.setCellFunction(aValue);
	}

	public PlatypusTextHandledArea getTarget() {
		assert target instanceof PlatypusTextHandledArea;
		return (PlatypusTextHandledArea) target;
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
	public String getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
	}
	
	@Override
	public void setValue(String value, boolean fireEvents) {
	    super.setValue(value, fireEvents);
	}
	
	public void setEmptyText(String aValue){
		((PlatypusTextHandledArea)target).setEmptyText(aValue);
	}
	
	public String getEmptyText(){
		return ((PlatypusTextHandledArea)target).getEmptyText();
	}
}
