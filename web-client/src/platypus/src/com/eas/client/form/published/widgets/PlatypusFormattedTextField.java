package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.FormattedObjectBox;
import com.bearsoft.rowset.Utils;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;

public class PlatypusFormattedTextField extends FormattedObjectBox implements HasPublished {

	protected JavaScriptObject published;

	public PlatypusFormattedTextField() {
		super();
	}

	public Object getJsValue() {
		return Utils.toJs(getValue());
	}

	public void setJsValue(Object aValue) throws Exception {
		setValue(Utils.toJava(aValue), true);
	}

	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
			if (published != null) {
				publish(this, aValue);
			}
		}
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		Object.defineProperty(published, "text", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
			}
		});
		// FormattedField is plain non-model control.
		// But it has value property as an only case.
		// In other cases only model-controls have value property
		Object.defineProperty(published, "value", {
			get : function() {
				return $wnd.boxAsJs(aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::getJsValue()());
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::setJsValue(Ljava/lang/Object;)($wnd.boxAsJava(aValue));
			}
		});
		Object.defineProperty(published, "format", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::getFormat()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::setFormat(Ljava/lang/String;)(aValue!=null?''+aValue:null);
			}
		});
	}-*/;
}
