package com.eas.client.form.published.widgets.model;

import com.bearsoft.gwt.ui.widgets.NullableTextArea;
import com.eas.client.Utils;
import com.eas.client.converters.StringValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.published.HasEmptyText;
import com.google.gwt.core.client.JavaScriptObject;

public class ModelTextArea extends ModelDecoratorBox<String> implements HasEmptyText {

	protected String emptyText;

	public ModelTextArea() {
		super(new NullableTextArea());
		((NullableTextArea) decorated).getElement().getStyle().setProperty("wordWrap", "normal");
		((NullableTextArea) decorated).getElement().getStyle().setProperty("resize", "none");
	}

	@Override
	public String convert(Object aValue) {
		StringValueConverter c = new StringValueConverter();
		return c.convert(aValue);
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

	protected native static void publish(ModelTextArea aWidget, JavaScriptObject aPublished)/*-{
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
				return aWidget.@com.eas.client.form.published.widgets.model.ModelTextArea::getValue()();
			},
			set : function(aValue) {
				if (aValue != null)
					aWidget.@com.eas.client.form.published.widgets.model.ModelTextArea::setValue(Ljava/lang/String;)('' + aValue);
				else
					aWidget.@com.eas.client.form.published.widgets.model.ModelTextArea::setValue(Ljava/lang/String;)(null);
			}
		});
		Object.defineProperty(aPublished, "text", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelTextArea::getText()();
			},
			set : function(aValue) {
				if (aValue != null)
					aWidget.@com.eas.client.form.published.widgets.model.ModelTextArea::setValue(Ljava/lang/String;)('' + aValue);
				else
					aWidget.@com.eas.client.form.published.widgets.model.ModelTextArea::setValue(Ljava/lang/String;)(null);
			}
		});
	}-*/;

	public String getText() {
		String v = getValue();
		return v == null ? "" : v;
	}

	@Override
	public void setText(String text) {
		setValue(text);
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
	public Object getJsValue() {
		return Utils.toJs(getValue());
	}

	@Override
	public void setJsValue(Object aValue) throws Exception {
		Object javaValue = Utils.toJava(aValue);
		setValue(convert(javaValue), true);
	}

	@Override
    protected void setReadonly(boolean aValue) {
		((NullableTextArea)decorated).getElement().setPropertyBoolean("readOnly", aValue);
    }

	@Override
    protected boolean isReadonly() {
		return ((NullableTextArea)decorated).getElement().getPropertyBoolean("readOnly");
    }
}
