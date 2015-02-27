/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.containers;

import java.util.ArrayList;
import java.util.List;

import com.bearsoft.gwt.ui.XElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class BorderPanel extends DockLayoutPanel {

	protected int hgap;
	protected int vgap;

	public BorderPanel() {
		super(Style.Unit.PX);
		getElement().<XElement> cast().addResizingTransitionEnd(this);
	}

	public int getHgap() {
		return hgap;
	}

	public void setHgap(int aValue) {
		hgap = aValue;
		recalcMargins();
	}

	public int getVgap() {
		return vgap;
	}

	public void setVgap(int aValue) {
		vgap = aValue;
		recalcMargins();
	}

	@Override
	protected void insert(Widget child, Direction direction, double size, Widget before) {
		super.insert(child, direction, size, before);
		recalcMargins();
		child.getElement().getStyle().clearRight();
		child.getElement().getStyle().clearBottom();
		child.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		child.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		com.bearsoft.gwt.ui.CommonResources.INSTANCE.commons().ensureInjected();
		child.getElement().addClassName(com.bearsoft.gwt.ui.CommonResources.INSTANCE.commons().borderSized());
	}

	@Override
	public boolean remove(Widget w) {
		boolean res = super.remove(w);
		recalcMargins();
		return res;
	}

	protected void recalcMargins() {
		List<Widget> north = new ArrayList<>();
		List<Widget> south = new ArrayList<>();
		Widget center = getCenter();
		for (Widget w : getChildren()) {
			Direction d = getResolvedDirection(getWidgetDirection(w));
			if (d == Direction.WEST) {
				// west = w;
			} else if (d == Direction.EAST) {
				// east = w;
			} else if (d == Direction.NORTH) {
				north.add(w);
			} else if (d == Direction.SOUTH) {
				south.add(w);
			}
		}
		if (center != null) {
			Element centerContainer = getWidgetContainerElement(center);
			centerContainer.getStyle().setMarginLeft(hgap, Style.Unit.PX);
			centerContainer.getStyle().setMarginRight(hgap, Style.Unit.PX);
			centerContainer.getStyle().setMarginTop(vgap, Style.Unit.PX);
			centerContainer.getStyle().setMarginBottom(vgap, Style.Unit.PX);
		}
		if (!north.isEmpty()) {
			for (Widget w : north) {
				Element northContainer = getWidgetContainerElement(w);
				northContainer.getStyle().setMarginLeft(hgap, Style.Unit.PX);
				northContainer.getStyle().setMarginRight(hgap, Style.Unit.PX);
			}
		}
		if (!south.isEmpty()) {
			for (Widget w : south) {
				Element southContainer = getWidgetContainerElement(w);
				southContainer.getStyle().setMarginLeft(hgap, Style.Unit.PX);
				southContainer.getStyle().setMarginRight(hgap, Style.Unit.PX);
			}
		}
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		forceLayout();
	}

}
