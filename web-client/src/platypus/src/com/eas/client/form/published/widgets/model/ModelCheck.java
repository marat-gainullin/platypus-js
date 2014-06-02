package com.eas.client.form.published.widgets.model;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.converters.BooleanRowValueConverter;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.CheckBox;

public class ModelCheck extends PublishedDecoratorBox<Boolean> {

	public ModelCheck() {
		super(new CheckBox());
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		super.setPublished(aValue);
		if (published != null) {
			publish(this, published);
		}
	}

	private static native void publish(ModelCheck aField, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "text", {
			get : function() {
				return aField.@com.eas.client.form.published.widgets.model.ModelCheck::getText()();
			},
			set : function(aValue) {
				if (aValue != null)
					aField.@com.eas.client.form.published.widgets.model.ModelCheck::setText(Ljava/lang/String;)('' + aValue);
				else
					aField.@com.eas.client.form.published.widgets.model.ModelCheck::setText(Ljava/lang/String;)(null);
			}
		});
		Object.defineProperty(aPublished, "value", {
			get : function() {
				var javaValue = aField.@com.eas.client.form.published.widgets.model.ModelCheck::getValue()();
				if (javaValue == null)
					return null;
				else
					return javaValue.@java.lang.Boolean::booleanValue()();
			},
			set : function(aValue) {
				if (aValue != null) {
					var javaValue = $wnd.P.boxAsJava((false != aValue));
					aField.@com.eas.client.form.published.widgets.model.ModelCheck::setValue(Ljava/lang/Boolean;Z)(javaValue, true);
				} else {
					aField.@com.eas.client.form.published.widgets.model.ModelCheck::setValue(Ljava/lang/Boolean;Z)(null, true);
				}
			}
		});
	}-*/;

	public String getText() {
		return ((CheckBox) decorated).getText();
	}

	public void setText(String aValue) {
		((CheckBox) decorated).setText(aValue);
	}

	@Override
	public Boolean getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(Boolean value, boolean fireEvents) {
		super.setValue(value, fireEvents);
	}

	@Override
	public void setBinding(Field aField) throws Exception {
		super.setBinding(aField, new BooleanRowValueConverter());
	}
}
