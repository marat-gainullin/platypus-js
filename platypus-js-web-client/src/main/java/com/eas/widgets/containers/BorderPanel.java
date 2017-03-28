/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.widgets.containers;

import java.util.ArrayList;
import java.util.List;

import com.eas.core.XElement;
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
		com.eas.ui.CommonResources.INSTANCE.commons().ensureInjected();
		child.getElement().addClassName(com.eas.ui.CommonResources.INSTANCE.commons().borderSized());
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
		/*
		 * if (!north.isEmpty()) { for (Widget w : north) { Element
		 * northContainer = getWidgetContainerElement(w);
		 * northContainer.getStyle().setMarginLeft(hgap, Style.Unit.PX);
		 * northContainer.getStyle().setMarginRight(hgap, Style.Unit.PX); } } if
		 * (!south.isEmpty()) { for (Widget w : south) { Element southContainer
		 * = getWidgetContainerElement(w);
		 * southContainer.getStyle().setMarginLeft(hgap, Style.Unit.PX);
		 * southContainer.getStyle().setMarginRight(hgap, Style.Unit.PX); } }
		 */
	}

	public void ajustWidth(Widget aWidget, int aWidth) {
		Direction direction = getWidgetDirection(aWidget);
		if (direction == Direction.WEST) {
			remove(aWidget);
			Widget centerWidget = getCenter();
			if (centerWidget != null)
				remove(centerWidget);
			addWest(aWidget, aWidth);
			if (centerWidget != null)
				add(centerWidget);
		} else if (direction == Direction.EAST) {
			remove(aWidget);
			Widget centerWidget = getCenter();
			if (centerWidget != null)
				remove(centerWidget);
			addEast(aWidget, aWidth);
			if (centerWidget != null)
				add(centerWidget);
		}
	}

	public void ajustHeight(Widget aWidget, int aHeight) {
		Direction direction = getWidgetDirection(aWidget);
		if (direction == Direction.NORTH) {
			remove(aWidget);
			Widget centerWidget = getCenter();
			if (centerWidget != null)
				remove(centerWidget);
			addNorth(aWidget, aHeight);
			if (centerWidget != null)
				add(centerWidget);
		} else if (direction == Direction.SOUTH) {
			remove(aWidget);
			Widget centerWidget = getCenter();
			if (centerWidget != null)
				remove(centerWidget);
			addSouth(aWidget, aHeight);
			if (centerWidget != null)
				add(centerWidget);
		}
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		forceLayout();
	}

}
