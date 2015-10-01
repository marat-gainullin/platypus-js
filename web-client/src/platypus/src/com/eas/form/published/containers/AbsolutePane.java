package com.eas.form.published.containers;

import com.eas.client.HasPublished;
import com.eas.form.published.PublishedAbsoluteConstraints;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Widget;

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

	@Override
	public void add(Widget aChild, PublishedAbsoluteConstraints aConstraints) {
		super.add(aChild, aConstraints != null ? aConstraints : PublishedAbsoluteConstraints.createDefaultAnchors());
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		published.add = function(toAdd, aConstraints) {
			if(toAdd && toAdd.unwrap) {
				if(toAdd.parent == published)
					throw 'A widget already added to this container';
				if(!aConstraints){
					aConstraints = {left: toAdd.left, top: toAdd.top, width: toAdd.width, height: toAdd.height};
				}
				aWidget.@com.eas.form.published.containers.AbsolutePane::add(Lcom/google/gwt/user/client/ui/Widget;Lcom/eas/form/published/PublishedAbsoluteConstraints;)(toAdd.unwrap(), aConstraints || null);
			}
		};
	}-*/;
}
