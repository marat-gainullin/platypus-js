package com.eas.client.form.published.widgets;

import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.DoubleBox;

public class PlatypusSpinnerField extends ConstraintedSpinnerBox implements HasPublished {

	protected JavaScriptObject published;

	public PlatypusSpinnerField() {
		super(new DoubleBox());
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
