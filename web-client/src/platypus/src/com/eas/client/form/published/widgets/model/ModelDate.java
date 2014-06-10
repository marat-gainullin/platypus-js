package com.eas.client.form.published.widgets.model;

import java.util.Date;

import com.bearsoft.gwt.ui.widgets.DateTimeBox;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.metadata.Field;
import com.eas.client.converters.DateRowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.published.HasEmptyText;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.DateBox;

public class ModelDate extends PublishedDecoratorBox<Date> implements HasEmptyText {

	protected String emptyText;
    private static final DateBox.DefaultFormat DEFAULT_FORMAT = GWT.create(DateBox.DefaultFormat.class);
	protected String format;

	public ModelDate() {
		super(new DateTimeBox());
		format = DEFAULT_FORMAT.getDateTimeFormat().getPattern();
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String aValue) {
		format = aValue;
		if (format != null)
			format = ControlsUtils.convertDateFormatString(format);
		DateTimeFormat dtFormat = format != null ? DateTimeFormat.getFormat(format) : DateTimeFormat.getFormat("dd.MM.yyyy");
		((DateTimeBox)decorated).setFormat(new DateBox.DefaultFormat(dtFormat));
	}

	public String getText() {
		DateTimeBox box = (DateTimeBox)decorated;
		return box.getText();
	}
	
	@Override
	public String getEmptyText() {
		return emptyText;
	}
	
	@Override
	public void setEmptyText(String aValue) {
		emptyText = aValue;
		ControlsUtils.applyEmptyText(getElement(), emptyText);
	}
		
	public void setPublished(JavaScriptObject aValue) {
		super.setPublished(aValue);
		if (published != null) {
			publish(this, published);
		}
	}

	private native static void publish(ModelDate aWidget, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "emptyText", {
			get : function() {
				return aWidget.@com.eas.client.form.published.HasEmptyText::getEmptyText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.HasEmptyText::setEmptyText(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
			}
		});
		Object.defineProperty(aPublished, "value", {
			get : function() {
				return $wnd.P.boxAsJs(aWidget.@com.eas.client.form.published.widgets.model.ModelDate::getJsValue()());
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelDate::setJsValue(Ljava/lang/Object;)($wnd.P.boxAsJava(aValue));
			}
		});
		Object.defineProperty(aPublished, "text", {
			get : function() {
				return $wnd.P.boxAsJs(aWidget.@com.eas.client.form.published.widgets.model.ModelDate::getText()());
			}
		});
		Object.defineProperty(aPublished, "dateFormat", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelDate::getFormat()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelDate::setFormat(Ljava/lang/String;)('' + aValue);
			}
		});
	}-*/;

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
	public void setBinding(Field aField) throws Exception {
		super.setBinding(aField, new DateRowValueConverter());
	}
}
