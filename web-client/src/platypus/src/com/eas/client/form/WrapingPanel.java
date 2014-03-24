package com.eas.client.form;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class WrapingPanel extends AbsolutePanel{
	
	public WrapingPanel(Element aElement) {
		super(aElement);
		onAttach();
	}
}
