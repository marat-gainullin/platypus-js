package com.eas.client.form;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class WrappingPanel extends AbsolutePanel{
	
	public WrappingPanel(Element aElement) {
		super(aElement);
		onAttach();
	}
}
