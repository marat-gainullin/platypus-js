package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.progress.ProgressBar;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;

public class PlatypusProgressBar extends ProgressBar implements HasPublished {

	protected JavaScriptObject published;
	protected String text;
	protected TextFormatter formatter = new TextFormatter() {

		@Override
		public String getText(ProgressBar bar, Double curProgress) {
			return text;
		}
	};

	public PlatypusProgressBar() {
		super();
	}

	public String getText() {
		return text;
	}

	public void setText(String aValue) {
		if (text == null && aValue != null || !text.equals(aValue)) {
			text = aValue;
			if (text != null) {
				setTextFormatter(formatter);
			} else {
				setTextFormatter(null);
			}
			
		}
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
		Object.defineProperty(published, "value", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusProgressBar::getValue()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusProgressBar::setValue(Ljava/lang/Double;)(aValue);
			}
		});
		Object.defineProperty(published, "minimum", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusProgressBar::getMinProgress()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusProgressBar::setMinProgress(D)(aValue);
			}
		});
		Object.defineProperty(published, "maximum", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusProgressBar::getMaxProgress()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusProgressBar::setMaxProgress(D)(aValue);
			}
		});
		Object.defineProperty(published, "text", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusProgressBar::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusProgressBar::setText(Ljava/lang/String;)('' + aValue);
			}
		});
	}-*/;
}
