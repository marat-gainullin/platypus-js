package com.eas.client.gxtcontrols.wrappers.container;

import com.google.gwt.user.client.ui.Widget;

public interface OrderedContainer {

	public void toFront(Widget aWidget);

	public void toFront(Widget aWidget, int aCount);

	public void toBack(Widget aWidget);
	
	public void toBack(Widget aWidget, int aCount);
	
}
