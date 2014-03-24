package com.eas.client.form.published.widgets.model;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.converters.StringRowValueConverter;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.TextBox;

public class ModelTextArea extends PublishedDecoratorBox<String> {

	public ModelTextArea() {
		super(new TextBox());
	}

	public void setPublished(JavaScriptObject aValue) {
		super.setPublished(aValue);
		if (published != null) {
			publish(this, published);
		}
	}

	protected native static void publish(ModelTextArea aField, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "value", {
			get : function() {
				return aField.@com.eas.client.form.published.widgets.model.ModelTextArea::getValue()();
			},
			set : function(aValue) {
				if (aValue != null)
					aField.@com.eas.client.form.published.widgets.model.ModelTextArea::setValue(Ljava/lang/String;)(''+aValue);
				else
					aField.@com.eas.client.form.published.widgets.model.ModelTextArea::setValue(Ljava/lang/String;)(null);
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
