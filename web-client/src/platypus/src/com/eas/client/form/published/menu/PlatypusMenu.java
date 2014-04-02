package com.eas.client.form.published.menu;

import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;

public class PlatypusMenu extends PlatypusMenuBar {

	protected String text;
	
	public PlatypusMenu() {
		super(true);
    }
	
	public String getText() {
	    return text;
    }
	
	public void setText(String aValue) {
	    text = aValue;
    }
	
	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			super.setPublished(aValue);
			if (published != null) {
				publish(this, aValue);
			}
		}
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		Object.defineProperty(published, "text", {
			get : function() {
				return aComponent.@com.eas.client.form.published.menu.PlatypusMenu::getText()();
			},
			set : function(aValue) {
				aComponent.@com.eas.client.form.published.menu.PlatypusMenu::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
			}
		});			
	}-*/;
}
