package com.eas.client.form.published.widgets.model;

import com.bearsoft.gwt.ui.widgets.FormattedObjectBox;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.metadata.Field;
import com.eas.client.converters.ObjectRowValueConverter;
import com.google.gwt.core.client.JavaScriptObject;

public class ModelFormattedField extends PublishedDecoratorBox<Object> {

	public ModelFormattedField() {
		super(new FormattedObjectBox());
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		super.setPublished(aValue);
		if (published != null) {
			publish(this, published);
		}
	}

	private native static void publish(ModelFormattedField aField, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "value", {
			get : function() {
				return $wnd.boxAsJs(aField.@com.eas.client.form.published.widgets.model.ModelFormattedField::getJsValue()());
			},
			set : function(aValue) {
				aField.@com.eas.client.form.published.widgets.model.ModelFormattedField::setJsValue(Ljava/lang/Object;)($wnd.boxAsJava(aValue));
			}
		});
	}-*/;

	public Object getJsValue() {
		return Utils.toJs(getValue());
	}

	public void setJsValue(Object value) throws Exception {
		setValue(Utils.toJava(value), true);
	}

	@Override
    public void setBinding(Field aField) throws Exception {
		super.setBinding(aField, new ObjectRowValueConverter());
    }
}
