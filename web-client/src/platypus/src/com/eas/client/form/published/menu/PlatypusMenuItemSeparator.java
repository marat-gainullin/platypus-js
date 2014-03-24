package com.eas.client.form.published.menu;

import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.MenuItemSeparator;

public class PlatypusMenuItemSeparator extends MenuItemSeparator implements HasPublished {

	protected JavaScriptObject published;

	public PlatypusMenuItemSeparator() {
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
	}-*/;
}
