package com.eas.client.form.published.widgets;

import com.eas.client.form.published.HasEmptyText;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.TextBox;

public class PlatypusTextField extends TextBox implements HasPublished, HasEmptyText {

	protected JavaScriptObject published;

	public PlatypusTextField() {
		super();
	}

	@Override
	public String getEmptyText() {
		return null;
	}
	
	@Override
	public void setEmptyText(String aValue) {
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
				return aWidget.@com.eas.client.form.published.widgets.PlatypusTextField::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusTextField::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
			}
		});
	}-*/;
}
