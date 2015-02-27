/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.containers;

import com.bearsoft.gwt.ui.CommonResources;
import com.bearsoft.gwt.ui.Orientation;
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
public class BoxPanel extends ComplexPanel implements RequiresResize, ProvidesResize, HasDirection {

	protected int orientation;// horizontal
	protected int hgap;
	protected Direction direction = Direction.LTR;
	protected int vgap;

	public BoxPanel() {
		super();
		setElement(Document.get().createDivElement());
		getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		getElement().<XElement> cast().addResizingTransitionEnd(this);
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int aValue) {
		if (orientation != aValue) {
			orientation = aValue;
			for (int i = 0; i < getWidgetCount(); i++) {
				Widget w = getWidget(i);
				formatWidget(w);
				if (i > 0) {
					if (orientation == Orientation.HORIZONTAL) {
						if (direction == Direction.LTR) {
							w.getElement().getStyle().setMarginLeft(hgap, Style.Unit.PX);
							w.getElement().getStyle().setMarginRight(0, Style.Unit.PX);
						} else {
							w.getElement().getStyle().setMarginLeft(0, Style.Unit.PX);
							w.getElement().getStyle().setMarginRight(hgap, Style.Unit.PX);
						}
					} else {
						w.getElement().getStyle().setMarginTop(vgap, Style.Unit.PX);
					}
				}
			}
			if (orientation == Orientation.HORIZONTAL) {
				ajustWidth();
			} else {
				ajustHeight();
			}
			onResize();
		}
	}

	public int getHgap() {
		return hgap;
	}

	public void setHgap(int aValue) {
		if (aValue >= 0) {
			hgap = aValue;
			for (int i = 1; i < getWidgetCount(); i++) {
				Widget w = getWidget(i);
				w.getElement().getStyle().setMarginLeft(aValue, Style.Unit.PX);
			}
			ajustWidth();
		}
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

	protected void formatWidget(Widget child) {
		boolean visible = !child.getElement().hasAttribute("aria-hidden");
		CommonResources.INSTANCE.commons().ensureInjected();
		Style es = child.getElement().getStyle();
		if (orientation == Orientation.HORIZONTAL) {
			es.clearTop();
			es.clearBottom();
			es.setPosition(Style.Position.RELATIVE);
			es.setHeight(100, Style.Unit.PCT);
			es.setDisplay(visible ? Style.Display.INLINE_BLOCK : Style.Display.NONE);
			es.setFloat(direction == Direction.LTR ? Style.Float.LEFT : Style.Float.RIGHT);
		} else {
			es.setPosition(Style.Position.RELATIVE);
			es.setDisplay(visible ? Style.Display.BLOCK : Style.Display.NONE);
			es.setLeft(0, Style.Unit.PX);
			es.clearRight();
			es.setWidth(100, Style.Unit.PCT);
		}
		child.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());
	}

	public void ajustDisplay(Widget child){
		if(child.getParent() == this){
			boolean visible = !child.getElement().hasAttribute("aria-hidden");
			if (orientation == Orientation.HORIZONTAL) {
				child.getElement().getStyle().setDisplay(visible ? Style.Display.INLINE_BLOCK : Style.Display.NONE);
			} else {
				child.getElement().getStyle().setDisplay(visible ? Style.Display.BLOCK : Style.Display.NONE);
			}
			if(child instanceof RequiresResize)
				((RequiresResize)child).onResize();
		}
	}
	
	@Override
	public void add(Widget child) {
		if (orientation == Orientation.HORIZONTAL) {
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
			formatWidget(child);// Don't move this call fron here because of crazy GWT. It clears position style property!
			if (isAttached()) {
				ajustWidth();
			}
		} else {
			if (getWidgetCount() > 0) {
				child.getElement().getStyle().setMarginTop(vgap, Style.Unit.PX);
			}
			super.add(child, getElement().<Element> cast());
			formatWidget(child);// Don't move this call fron here because of crazy GWT. It clears position style property!
			if (isAttached()) {
				ajustHeight();
			}
		}
	}

	@Override
	public boolean remove(Widget w) {
		if (orientation == Orientation.HORIZONTAL) {
			boolean res = super.remove(w);
			if (res && isAttached()) {
				ajustWidth();
			}
			return res;
		} else {
			boolean res = super.remove(w);
			if (res && isAttached()) {
				ajustHeight();
			}
			return res;
		}
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		if (orientation == Orientation.HORIZONTAL)
			ajustWidth();
		else
			ajustHeight();
	}

	@Override
	public void onResize() {
		if (orientation == Orientation.HORIZONTAL) {
			if (getParent() instanceof ScrollPanel) {
				getElement().getStyle().setHeight(100, Style.Unit.PCT);
			}
			for (Widget child : getChildren()) {
				child.getElement().getStyle().setHeight(100, Style.Unit.PCT);
				if (child instanceof RequiresResize) {
					((RequiresResize) child).onResize();
				}
			}
		} else {
			if (getParent() instanceof ScrollPanel) {
				getElement().getStyle().setWidth(100, Style.Unit.PCT);
			}
			for (Widget child : getChildren()) {
				child.getElement().getStyle().clearRight();
				child.getElement().getStyle().setWidth(100, Style.Unit.PCT);
				if (child instanceof RequiresResize) {
					((RequiresResize) child).onResize();
				}
			}
		}
	}

	protected boolean ajustWidth() {
		if (isAttached() && (getParent() instanceof ScrollPanel || (getParent() instanceof BoxPanel && ((BoxPanel) getParent()).getOrientation() == Orientation.HORIZONTAL))) {
			if (getWidgetCount() > 0) {
				double width = 0;
				for (Widget child : getChildren()) {
					String ssChildWidth = child.getElement().getStyle().getWidth();
					double sChildWidth = ssChildWidth.isEmpty() ? 0 : Double.valueOf(ssChildWidth.substring(0, ssChildWidth.length() - 2));
					width += sChildWidth;
				}
				width += hgap * (getWidgetCount() - 1);
				setAjustedWidth(width);
				return true;
			} else {
				return false;
			}
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

	protected void setAjustedWidth(double aValue) {
		setWidth(aValue + "px");
		if (getParent() instanceof BoxPanel && ((BoxPanel) getParent()).getOrientation() == Orientation.HORIZONTAL) {
			BoxPanel parentBox = (BoxPanel) getParent();
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

	protected boolean ajustHeight() {
		if (isAttached() && (getParent() instanceof ScrollPanel || (getParent() instanceof BoxPanel && ((BoxPanel) getParent()).getOrientation() == Orientation.VERTICAL))) {
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
		if (getParent() instanceof BoxPanel && ((BoxPanel) getParent()).getOrientation() == Orientation.VERTICAL) {
			BoxPanel parentBox = (BoxPanel) getParent();
			parentBox.ajustHeight();
		}
	}
}
