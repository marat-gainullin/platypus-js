package com.eas.client.gxtcontrols.model;

import java.util.Date;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.Utils;
import com.eas.client.gxtcontrols.ControlsUtils;
import com.eas.client.gxtcontrols.converters.DateRowValueConverter;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterStandaloneField;
import com.eas.client.gxtcontrols.wrappers.handled.PlatypusDateHandledField;
import com.eas.client.gxtcontrols.wrappers.handled.PlatypusTextHandledArea;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.sencha.gxt.cell.core.client.form.DateCell;
import com.sencha.gxt.widget.core.client.form.DateTimePropertyEditor;

public class ModelDate extends PlatypusAdapterStandaloneField<Date> {

	protected String format;

	public ModelDate() {
		super(new PlatypusDateHandledField(new DateCell()));
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String aValue) {
		format = aValue;
		if (format != null)
			format = ControlsUtils.convertDateFormatString(format);
		DateTimeFormat dtFormat = format != null ? DateTimeFormat.getFormat(format) : DateTimeFormat.getFormat("dd.MM.yyyy");
		DateTimePropertyEditor pe = new DateTimePropertyEditor(dtFormat);
		pe.setParseStrict(false);
		((PlatypusDateHandledField) target).setPropertyEditor(pe);
	}

	public void setPublishedField(JavaScriptObject aValue) {
		super.setPublishedField(aValue);
		if (publishedField != null) {
			publish(this, publishedField);
		}
	}

	protected native static void publish(ModelDate aField, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "onSelect", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelDate::getOnSelect()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelDate::setOnSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "onRender", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelDate::getOnRender()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelDate::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "editable", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelDate::isEditable()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelDate::setEditable(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "selectOnly", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelDate::isSelectOnly()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelDate::setSelectOnly(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "value", {
			get : function() {
				return $wnd.boxAsJs(aField.@com.eas.client.gxtcontrols.model.ModelDate::getJsValue()());
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelDate::setJsValue(Ljava/lang/Object;)($wnd.boxAsJava(aValue));
			}
		});
		Object.defineProperty(aPublished, "field", {
			get : function() {
				return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aField.@com.eas.client.gxtcontrols.model.ModelDate::getField()());
			},
			set : function(aValue) {
				if (aValue != null)
					aField.@com.eas.client.gxtcontrols.model.ModelDate::setField(Lcom/bearsoft/rowset/metadata/Field;)(aValue.unwrap());
				else
					aField.@com.eas.client.gxtcontrols.model.ModelDate::setField(Lcom/bearsoft/rowset/metadata/Field;)(null);
			}
		});
	}-*/;

	public ModelElementRef getModelElement() {
		return ((PlatypusDateHandledField) target).getModelElement();
	}

	public void setModelElement(ModelElementRef aValue) {
		((PlatypusDateHandledField) target).setModelElement(aValue);
	}

	public void setField(Field aField) throws Exception {
		super.setField(aField, new DateRowValueConverter());
	}

	public JavaScriptObject getOnRender() {
		return ((PlatypusDateHandledField) target).getCellFunction();
	}

	public void setOnRender(JavaScriptObject aValue) {
		((PlatypusDateHandledField) target).setCellFunction(aValue);
	}

	public PlatypusDateHandledField getTarget() {
		assert target instanceof PlatypusDateHandledField;
		return (PlatypusDateHandledField) target;
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

	public Object getJsValue() throws Exception {
		return Utils.toJs(getValue());
	}

	public void setJsValue(Object aValue) throws Exception {
		Object javaValue = Utils.toJava(aValue);
		if (javaValue instanceof Date)
			setValue((Date) javaValue, true);
		else
			throw new IllegalArgumentException("A value of type 'Date' expected");
	}

	@Override
	public Date getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(Date value) {
		super.setValue(value);
	}

	@Override
	public void setValue(Date value, boolean fireEvents) {
		super.setValue(value, fireEvents);
	}
	
	public void setEmptyText(String aValue){
		ControlsUtils.setEmptyText(((PlatypusDateHandledField)target), aValue);
	}
	
	public String getEmptyText(){
		return ControlsUtils.getEmptyText(((PlatypusDateHandledField)target));
	}	
}
