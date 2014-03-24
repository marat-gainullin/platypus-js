package com.eas.client.form.published.containers;

import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;

public class AbsolutePane extends MarginsPane {
	
	public AbsolutePane() {
		super();
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
		published.add = function(toAdd, aConstraints) {
			if(toAdd && toAdd.unwrap) {
				aComponent.@com.eas.client.form.published.containers.AbsolutePane::add(Lcom/google/gwt/user/client/ui/Widget;Lcom/eas/client/form/AbsoluteJSConstraints;)(toAdd.unwrap(), aConstraints || null);
			}
		};
	}-*/;
}
