/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.containers;

import com.bearsoft.gwt.ui.XElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class SplittedPanel extends SplitLayoutPanel {
	
	public SplittedPanel() {
		super();
		getElement().<XElement>cast().addResizingTransitionEnd(this);
	}

	public SplittedPanel(int aSplitterSize) {
		super(aSplitterSize);
		getElement().<XElement>cast().addResizingTransitionEnd(this);
	}
	
	@Override
	public void insert(Widget child, Direction direction, double size, Widget before) {
		super.insert(child, direction, size, before);
		// if (child instanceof FocusWidget) {
		child.getElement().getStyle().clearRight();
		child.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		com.bearsoft.gwt.ui.CommonResources.INSTANCE.commons().ensureInjected();
		child.getElement().addClassName(com.bearsoft.gwt.ui.CommonResources.INSTANCE.commons().borderSized());
		// }
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		forceLayout();// GWT animations are deprecated because of CSS3 transitions
	}

}
