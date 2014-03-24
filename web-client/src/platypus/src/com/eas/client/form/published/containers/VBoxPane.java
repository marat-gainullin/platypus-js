package com.eas.client.form.published.containers;

import com.bearsoft.gwt.ui.containers.VerticalBoxPanel;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;

public class VBoxPane extends VerticalBoxPanel implements HasPublished {

	protected JavaScriptObject published;

	public VBoxPane() {
		super();
	}

	public VBoxPane(int aVGap) {
		super();
		setVgap(aVGap);
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
