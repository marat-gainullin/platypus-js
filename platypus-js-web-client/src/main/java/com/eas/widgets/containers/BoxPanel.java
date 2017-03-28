package com.eas.widgets.containers;

import com.eas.core.XElement;
import com.eas.ui.CommonResources;
import com.eas.ui.Orientation;
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
		getElement().getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
		getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
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
				Style es = w.getElement().getStyle();
				es.setMarginLeft(0, Style.Unit.PX);
				es.setMarginRight(0, Style.Unit.PX);
				es.setMarginTop(0, Style.Unit.PX);
				if (i > 0) {
					if (orientation == Orientation.HORIZONTAL) {
						if (direction == Direction.LTR) {
							es.setMarginLeft(hgap, Style.Unit.PX);
							es.setMarginRight(0, Style.Unit.PX);
						} else {
							es.setMarginLeft(0, Style.Unit.PX);
							es.setMarginRight(hgap, Style.Unit.PX);
						}
					} else {
						es.setMarginTop(vgap, Style.Unit.PX);
					}
				}
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
			if (orientation == Orientation.HORIZONTAL) {
				for (int i = 1; i < getWidgetCount(); i++) {
					Widget w = getWidget(i);
					w.getElement().getStyle().setMarginLeft(aValue, Style.Unit.PX);
				}
			}
		}
	}

	public int getVgap() {
		return vgap;
	}

	public void setVgap(int aValue) {
		if (aValue >= 0) {
			vgap = aValue;
			if (orientation == Orientation.VERTICAL) {
				for (int i = 1; i < getWidgetCount(); i++) {
					Widget w = getWidget(i);
					w.getElement().getStyle().setMarginTop(aValue, Style.Unit.PX);
				}
			}
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
			if (direction == Direction.LTR)
				es.clearFloat();
			else
				es.setFloat(Style.Float.RIGHT);
		} else {
			es.setPosition(Style.Position.RELATIVE);
			es.setDisplay(visible ? Style.Display.BLOCK : Style.Display.NONE);
			es.setLeft(0, Style.Unit.PX);
			es.clearRight();
			es.setWidth(100, Style.Unit.PCT);
		}
		es.setVerticalAlign(Style.VerticalAlign.MIDDLE);
		child.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());
	}

	public void ajustDisplay(Widget child) {
		if (child.getParent() == this) {
			boolean visible = !child.getElement().hasAttribute("aria-hidden");
			if (orientation == Orientation.HORIZONTAL) {
				child.getElement().getStyle().setDisplay(visible ? Style.Display.INLINE_BLOCK : Style.Display.NONE);
			} else {
				child.getElement().getStyle().setDisplay(visible ? Style.Display.BLOCK : Style.Display.NONE);
			}
			if (child instanceof RequiresResize)
				((RequiresResize) child).onResize();
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
			formatWidget(child);// Don't move this call from here because of
			                    // crazy GWT. It clears position style property!
		} else {
			if (getWidgetCount() > 0) {
				child.getElement().getStyle().setMarginTop(vgap, Style.Unit.PX);
			}
			super.add(child, getElement().<Element> cast());
			formatWidget(child);// Don't move this call from here because of
			                    // crazy GWT. It clears position style property!
		}
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		if (orientation == Orientation.HORIZONTAL) {
			if (getParent() instanceof ScrollPanel) {
				getElement().getStyle().setHeight(100, Style.Unit.PCT);
			}
		} else {
			if (getParent() instanceof ScrollPanel) {
				getElement().getStyle().setWidth(100, Style.Unit.PCT);
			}
		}
	}

	@Override
	public void onResize() {
		if (orientation == Orientation.HORIZONTAL) {
			for (Widget child : getChildren()) {
				child.getElement().getStyle().setHeight(100, Style.Unit.PCT);
				if (child instanceof RequiresResize) {
					((RequiresResize) child).onResize();
				}
			}
		} else {
			for (Widget child : getChildren()) {
				child.getElement().getStyle().clearRight();
				child.getElement().getStyle().setWidth(100, Style.Unit.PCT);
				if (child instanceof RequiresResize) {
					((RequiresResize) child).onResize();
				}
			}
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
