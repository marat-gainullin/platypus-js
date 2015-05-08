/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.containers;

import com.bearsoft.gwt.ui.CommonResources;
import com.bearsoft.gwt.ui.XElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class AnchorsPanel extends LayoutPanel {

	public AnchorsPanel() {
		super();
		getElement().<XElement> cast().addResizingTransitionEnd(this);
	}

	public void ajustDisplay(Widget child) {
		if (child.getParent() == this) {
			Element el = getWidgetContainerElement(child);
			boolean visible = !child.getElement().hasAttribute("aria-hidden");
			if (visible)
				el.getStyle().clearDisplay();
			else
				el.getStyle().setDisplay(Style.Display.NONE);
		}
	}

	@Override
	public void insert(Widget aChild, int beforeIndex) {
		super.insert(aChild, beforeIndex);
		Element wce = getWidgetContainerElement(aChild);
		wce.getStyle().clearOverflow();
		aChild.getElement().getStyle().clearRight();
		aChild.getElement().getStyle().clearBottom();
		aChild.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		aChild.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		CommonResources.INSTANCE.commons().ensureInjected();
		aChild.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());
		ajustDisplay(aChild);
	}

	@Override
	public void onResize() {
		// Crazy GWT layout system clears display property of childrens' styles.
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget child = getWidget(i);
			ajustDisplay(child);
		}
		super.onResize();
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		forceLayout();
	}

}
