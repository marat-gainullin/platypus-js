/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.containers;

import com.bearsoft.gwt.ui.CommonResources;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class VerticalBoxPanel extends ComplexPanel implements RequiresResize, ProvidesResize {

	protected int vgap = 0;

	public VerticalBoxPanel() {
		super();
		setElement(Document.get().createDivElement());
		getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
	}

	public int getVgap() {
		return vgap;
	}

	public void setVgap(int aValue) {
		if (aValue >= 0) {
			vgap = aValue;
			for (int i = 1; i < getWidgetCount(); i++) {
				Widget w = getWidget(i);
				w.getElement().getStyle().setMarginTop(aValue, Style.Unit.PX);
			}
			ajustHeight();
		}
	}

	@Override
	public void add(Widget child) {
		child.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		child.getElement().getStyle().setDisplay(Style.Display.BLOCK);
		child.getElement().getStyle().setLeft(0, Style.Unit.PX);
		// if(child instanceof FocusWidget){
		child.getElement().getStyle().clearRight();
		child.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		CommonResources.INSTANCE.commons().ensureInjected();
		child.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());
		/*
		 * } else { child.getElement().getStyle().setRight(0, Style.Unit.PX);
		 * child.getElement().getStyle().clearWidth(); }
		 */
		if (getWidgetCount() > 0) {
			child.getElement().getStyle().setMarginTop(vgap, Style.Unit.PX);
		}
		super.add(child, getElement().<Element> cast());
		if (isAttached()) {
			ajustHeight();
		}
	}

	@Override
	public boolean remove(Widget w) {
		boolean res = super.remove(w);
		if (res && isAttached()) {
			ajustHeight();
		}
		return res;
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		ajustHeight();
	}

	@Override
	public void onResize() {
		for (Widget child : getChildren()) {
			if (child instanceof RequiresResize) {
				((RequiresResize) child).onResize();
			}
		}
	}

	protected boolean ajustHeight() {
		if (isAttached() && (getParent() instanceof ScrollPanel || getParent() instanceof VerticalBoxPanel)) {
			if (getWidgetCount() > 0) {
				int height = 0;
				for (int i = 0; i < getWidgetCount(); i++) {
					Widget w = getWidget(i);
					String ssChildHeight = w.getElement().getStyle().getHeight();
					double sChildHeight = ssChildHeight.isEmpty() ? 0 : Double.valueOf(ssChildHeight.substring(0, ssChildHeight.length() - 2));
					height += sChildHeight;
				}
				height += (getWidgetCount() - 1) * vgap;
				setAjustedHeight(height);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void ajustHeight(Widget aChild, int aValue) {
		if (aChild != null) {
			Element we = aChild.getElement();
			we.getStyle().setHeight(aValue, Style.Unit.PX);
			ajustHeight();
		}
	}

	protected void setAjustedHeight(double aValue) {
		setHeight(aValue + "px");
		if (getParent() instanceof VerticalBoxPanel) {
			VerticalBoxPanel parentBox = (VerticalBoxPanel) getParent();
			parentBox.ajustHeight();
		}
	}

}
