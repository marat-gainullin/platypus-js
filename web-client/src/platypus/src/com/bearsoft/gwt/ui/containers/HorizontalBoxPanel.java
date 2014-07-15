/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.containers;

import com.bearsoft.gwt.ui.CommonResources;
import com.bearsoft.gwt.ui.XElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class HorizontalBoxPanel extends ComplexPanel implements RequiresResize, ProvidesResize, HasDirection {

	protected int hgap = 0;
	protected Direction direction = Direction.LTR;

	public HorizontalBoxPanel() {
		super();
		setElement(Document.get().createDivElement());
		getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
	}

	public int getHgap() {
		return hgap;
	}

	public void setHgap(int aValue) {
		hgap = aValue;
		for (int i = 1; i < getWidgetCount(); i++) {
			Widget w = getWidget(i);
			w.getElement().getStyle().setMarginLeft(aValue, Style.Unit.PX);
		}
		ajustWidth();
	}

	@Override
	public void add(Widget child) {
		child.getElement().getStyle().clearTop();
		child.getElement().getStyle().clearBottom();
		child.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		child.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		CommonResources.INSTANCE.commons().ensureInjected();
		child.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());
		child.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		child.getElement().getStyle().setFloat(direction == Direction.LTR ? Style.Float.LEFT : Style.Float.RIGHT);
		if (getWidgetCount() > 0) {
			if (direction == Direction.LTR) {
				child.getElement().getStyle().setMarginLeft(hgap, Style.Unit.PX);
				child.getElement().getStyle().setMarginRight(0, Style.Unit.PX);
			} else {
				child.getElement().getStyle().setMarginLeft(0, Style.Unit.PX);
				child.getElement().getStyle().setMarginRight(hgap, Style.Unit.PX);
			}
		}
		super.add(child, getElement().<Element> cast());
		if (isAttached()) {
			ajustWidth();
		}
	}

	@Override
	public boolean remove(Widget w) {
		boolean res = super.remove(w);
		if (res && isAttached()) {
			ajustWidth();
		}
		return res;
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		ajustWidth();
	}

	@Override
	public void onResize() {
		for (Widget child : getChildren()) {
			if (child instanceof RequiresResize) {
				((RequiresResize) child).onResize();
			}
		}
	}

	protected boolean ajustWidth() {
		if (isAttached() && (getParent() instanceof ScrollPanel || getParent() instanceof HorizontalBoxPanel)) {
			int parentClientHeight = getParent().getElement().getClientHeight();
			if (parentClientHeight > 0) {
				setHeight(parentClientHeight + "px");
			}
			double width = 0;
			// int containerContentHeight =
			// getElement().<XElement>cast().getContentHeight();
			for (Widget child : getChildren()) {
				/*
				 * if (!(child instanceof FocusWidget)) {
				 * child.getElement().getStyle().setHeight(100, Style.Unit.PCT);
				 * int widgetContentHeight =
				 * child.getElement().<XElement>cast().getContentHeight(); int
				 * widgetOffsetHeight = child.getElement().getOffsetHeight();
				 * int widgetDecorsHeight = widgetOffsetHeight -
				 * widgetContentHeight;
				 * child.getElement().getStyle().setHeight(containerContentHeight
				 * - widgetDecorsHeight, Style.Unit.PX); }
				 */
				child.getElement().getStyle().setHeight(100, Style.Unit.PCT);
				//
				if (child instanceof RequiresResize) {
					((RequiresResize) child).onResize();
				}
				int subPixelDelta;
				double computedWidth = child.getElement().<XElement> cast().getSubPixelComputedWidth();
				if (computedWidth != -1) {
					double fraction = computedWidth - Math.floor(computedWidth);
					if (fraction > 0 && fraction < 0.5) {
						subPixelDelta = 1;
					} else {
						subPixelDelta = 0;
					}
				} else {
					subPixelDelta = 0;
				}
				String ssChildWidth = child.getElement().getStyle().getWidth();
				double sChildWidth = Double.valueOf(ssChildWidth.substring(0, ssChildWidth.length() - 2));
				width += sChildWidth + subPixelDelta;
				// width += child.getElement().getOffsetWidth() + subPixelDelta;
			}
			if (getWidgetCount() > 0) {
				width += hgap * (getWidgetCount() - 1);
			}
			setAjustedWidth(width);
			return true;
		} else {
			return false;
		}
	}

	public void ajustWidth(Widget aChild, int aValue) {
		if (aChild != null) {
			Element we = aChild.getElement();
			we.getStyle().setWidth(aValue, Style.Unit.PX);
			ajustWidth();
		}
	}

	protected void setAjustedWidth(double aValue){
		setWidth(aValue + "px");
		if (getParent() instanceof HorizontalBoxPanel) {
			HorizontalBoxPanel parentBox = (HorizontalBoxPanel) getParent();
			parentBox.ajustWidth();
		}
	}
	
	@Override
	public void setDirection(Direction aValue) {
		direction = aValue;
	}

	@Override
	public Direction getDirection() {
		return direction;
	}
}
