package com.eas.client.form.published.containers;

import com.bearsoft.gwt.ui.containers.Toolbar;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;

public class ToolBar extends Toolbar implements HasPublished {
	
	protected JavaScriptObject published;

	public ToolBar(){
		super();
	}
	
	@Override
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
		published.add = function(toAdd){
			if(toAdd && toAdd.unwrap){
				aComponent.@com.eas.client.form.published.containers.ToolBar::add(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
			}
		};		
	}-*/;
}
