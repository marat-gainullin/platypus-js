package com.eas.widgets;

import com.eas.core.HasPublished;
import com.eas.ui.PublishedMarginConstraints;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Widget;

public class AnchorsPane extends MarginsPane {
	
	public AnchorsPane() {
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
	public void add(Widget aChild, PublishedMarginConstraints aConstraints) {
	    super.add(aChild, aConstraints != null ? aConstraints : PublishedMarginConstraints.createDefaultAnchors());
	}
	
	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		published.add = function(toAdd, aConstraints) {
			if(toAdd && toAdd.unwrap) {
				if(toAdd.parent == published)
					throw 'A widget already added to this container';
				if(!aConstraints){
					aConstraints = {left: toAdd.left, top: toAdd.top, width: toAdd.width, height: toAdd.height};
				}
				aWidget.@com.eas.widgets.AnchorsPane::add(Lcom/google/gwt/user/client/ui/Widget;Lcom/eas/ui/PublishedMarginConstraints;)(toAdd.unwrap(), aConstraints || null);
			}
		};
	}-*/;
}
