package com.eas.client.form.published.containers;

import com.bearsoft.gwt.ui.containers.HorizontalBoxPanel;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;

public class HBoxPane extends HorizontalBoxPanel implements HasPublished {

	protected JavaScriptObject published;

	public HBoxPane() {
		super();
	}

	public HBoxPane(int aHGap) {
		super();
		setHgap(aHGap);
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

	private native static void publish(HasPublished aWidget, JavaScriptObject aPublished)/*-{
	}-*/;
}
