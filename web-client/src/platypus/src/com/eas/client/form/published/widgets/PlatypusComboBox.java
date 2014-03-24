package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.StyledListBox;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;

public class PlatypusComboBox extends StyledListBox<Object> implements HasPublished{

	protected JavaScriptObject published;
	
	public PlatypusComboBox(){
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

	private native static void publish(HasPublished aWidget, JavaScriptObject aPublished)/*-{
	}-*/;
}
