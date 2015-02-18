/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.containers;

import com.bearsoft.gwt.ui.CommonResources;
import com.bearsoft.gwt.ui.Orientation;
import com.bearsoft.gwt.ui.XElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
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
	public void insert(Widget widget, int beforeIndex) {
		super.insert(widget, beforeIndex);
		widget.getElement().getStyle().clearRight();
		widget.getElement().getStyle().clearBottom();
		widget.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		widget.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		CommonResources.INSTANCE.commons().ensureInjected();
		widget.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());
	}

	@Override
	public void onResize() {
		super.onResize();
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		forceLayout();
	}

}
