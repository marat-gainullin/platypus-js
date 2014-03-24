package com.eas.client.form.published.widgets;

import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.RichTextArea;

public class PlatypusHtmlEditor extends RichTextArea implements HasPublished {
	
	protected JavaScriptObject published;

	public PlatypusHtmlEditor(){
		super();
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
		Object.defineProperty(published, "emptyText", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusHtmlEditor::getEmptyText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusHtmlEditor::setEmptyText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
			}
		});
	}-*/;
}
