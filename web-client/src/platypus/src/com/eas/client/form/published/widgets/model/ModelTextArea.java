package com.eas.client.form.published.widgets.model;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.converters.StringRowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.published.HasEmptyText;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.TextBox;

public class ModelTextArea extends PublishedDecoratorBox<String> implements HasEmptyText {

	protected String emptyText;
	
	public ModelTextArea() {
		super(new TextBox());
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
					aWidget.@com.eas.client.form.published.widgets.model.ModelTextArea::setValue(Ljava/lang/String;)(''+aValue);
				else
					aWidget.@com.eas.client.form.published.widgets.model.ModelTextArea::setValue(Ljava/lang/String;)(null);
			}
		});
		Object.defineProperty(aPublished, "text", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.model.ModelTextArea::getValue()();
			},
			set : function(aValue) {
				if (aValue != null)
					aWidget.@com.eas.client.form.published.widgets.model.ModelTextArea::setValue(Ljava/lang/String;)(''+aValue);
				else
					aWidget.@com.eas.client.form.published.widgets.model.ModelTextArea::setValue(Ljava/lang/String;)(null);
			}
		});
	}-*/;

	@Override
	public String getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
	}

	@Override
    public void setBinding(Field aField) throws Exception {
		super.setBinding(aField, new StringRowValueConverter());
    }
}
