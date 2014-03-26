package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.progress.SliderBar;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;

public class PlatypusSlider extends SliderBar implements HasPublished {

	protected JavaScriptObject published;

	public PlatypusSlider(double aMinValue, double aMaxValue, LabelFormatter aLabelFormatter) {
		super(aMinValue, aMaxValue, aLabelFormatter);
	}

	public PlatypusSlider(double aMinValue, double aMaxValue) {
		super(aMinValue, aMaxValue);
	}
	
	public PlatypusSlider() {
		super(0, 100);
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
		Object.defineProperty(published, "maximum", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusSlider::getMaxValue()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusSlider::setMaxValue(D)(aValue);
			}
		});
		Object.defineProperty(published, "minimum", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusSlider::getMinValue()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusSlider::setMinValue(D)(aValue);
			}
		});
		Object.defineProperty(published, "value", {
			get : function() {
				var value = aWidget.@com.eas.client.form.published.widgets.PlatypusSlider::getValue()();
				return (value == null ? 0 :	value.@java.lang.Integer::intValue()());
			},
			set : function(aValue) {
				var value = @java.lang.Double::new(Ljava/lang/String;)(''+aValue);
				aWidget.@com.eas.client.form.published.widgets.PlatypusSlider::setValue(Ljava/lang/Double;)(value);
			}
		});
	}-*/;
}
