package com.eas.client.form.published.widgets;

import com.eas.client.form.published.HasEmptyText;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.RichTextArea;

public class PlatypusHtmlEditor extends RichTextArea implements HasPublished, HasEmptyText {
	
	protected JavaScriptObject published;

	public PlatypusHtmlEditor(){
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
				return aWidget.@com.eas.client.form.published.widgets.PlatypusHtmlEditor::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusHtmlEditor::setText(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
			}
		});
	}-*/;
}
