package com.eas.client.gxtcontrols.wrappers.container;

import com.eas.client.gxtcontrols.AbsoluteJSConstraints;
import com.eas.client.gxtcontrols.MarginConstraints;
import com.eas.client.gxtcontrols.MarginJSConstraints;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

public class PlatypusMarginLayoutContainer extends SimpleContainer implements OrderedContainer {

	protected static String MARGIN_LAYOUT_DATA = "anchorsData";

	protected LayoutPanel content = new LayoutPanel();

	public PlatypusMarginLayoutContainer() {
		super();
		getElement().getStyle().setBackgroundColor("transparent");
		setWidget(content);
	}

	private Component wrap(IsWidget aWidget) {
		BorderLayoutContainer border = new BorderLayoutContainer();
		border.getElement().getStyle().setBackgroundColor("transparent");
		border.setCenterWidget(aWidget, new MarginData(0, 0, 0, 0));
		return border;
	}

	private Widget unwrap(Widget aWidget) {
		if (aWidget != null) {
			assert aWidget instanceof BorderLayoutContainer;
			return ((BorderLayoutContainer) aWidget).getCenterWidget();
		}
		return null;
	}

	public void add(Widget aChild, MarginConstraints aConstraints) {
		Component comp = wrap(aChild);
		comp.setData(MARGIN_LAYOUT_DATA, aConstraints);
		content.add(comp);
		applyConstraints(comp, aConstraints);
		content.getElement().insertFirst(content.getElement().getLastChild());
	}

	protected void applyConstraints(Widget aWidget, MarginConstraints aConstraints) {
		MarginConstraints.Margin left = aConstraints.getLeft();
		MarginConstraints.Margin top = aConstraints.getTop();
		MarginConstraints.Margin right = aConstraints.getRight();
		MarginConstraints.Margin bottom = aConstraints.getBottom();
		MarginConstraints.Margin width = aConstraints.getWidth();
		MarginConstraints.Margin height = aConstraints.getHeight();

		// horizontal
		if (left != null && width != null)
			right = null;
		if (left != null && right != null) {
			setWidgetLeftRight(aWidget, left.value, left.unit, right.value, right.unit);
		} else if (left == null && right != null) {
			assert width != null : "left may be absent in presence of width and right";
			setWidgetRightWidth(aWidget, right.value, right.unit, width.value, width.unit);
		} else if (right == null && left != null) {
			assert width != null : "right may be absent in presence of width and left";
			setWidgetLeftWidth(aWidget, left.value, left.unit, width.value, width.unit);
		} else {
			assert false : "At least left with width, right with width or both (without width) must present";
		}
		// vertical
		if (top != null && height != null)
			bottom = null;
		if (top != null && bottom != null) {
			setWidgetTopBottom(aWidget, top.value, top.unit, bottom.value, bottom.unit);
		} else if (top == null && bottom != null) {
			assert height != null : "top may be absent in presence of height and bottom";
			setWidgetBottomHeight(aWidget, bottom.value, bottom.unit, height.value, height.unit);
		} else if (bottom == null && top != null) {
			assert height != null : "bottom may be absent in presence of height and top";
			setWidgetTopHeight(aWidget, top.value, top.unit, height.value, height.unit);
		} else {
			assert false : "At least top with height, bottom with height or both (without height) must present";
		}
		if (isAttached()) {
			content.forceLayout();
		}
	}

	public void add(Widget aChild, MarginJSConstraints aConstraints) {
		MarginConstraints margins = new MarginConstraints();
		int h = 0;
		int v = 0;
		String left = aConstraints.getLeft();
		if (left != null && !left.isEmpty()) {
			margins.setLeft(new MarginConstraints.Margin(left));
			h++;
		}
		String right = aConstraints.getRight();
		if (right != null && !right.isEmpty()) {
			margins.setRight(new MarginConstraints.Margin(right));
			h++;
		}
		String width = aConstraints.getWidth();
		if (width != null && !width.isEmpty()) {
			margins.setWidth(new MarginConstraints.Margin(width));
			h++;
		}
		String top = aConstraints.getTop();
		if (top != null && !top.isEmpty()) {
			margins.setTop(new MarginConstraints.Margin(top));
			v++;
		}
		String bottom = aConstraints.getBottom();
		if (bottom != null && !bottom.isEmpty()) {
			margins.setBottom(new MarginConstraints.Margin(bottom));
			v++;
		}
		String height = aConstraints.getHeight();
		if (height != null && !height.isEmpty()) {
			margins.setHeight(new MarginConstraints.Margin(height));
			v++;
		}
		if (h < 2)
			throw new IllegalArgumentException("There are must be at least two horizontal values in anchors.");
		if (v < 2)
			throw new IllegalArgumentException("There are must be at least two vertical values in anchors.");
		add(aChild, margins);
	}

	public void add(Widget aChild, AbsoluteJSConstraints aConstraints) {
		MarginConstraints anchors = new MarginConstraints();
		String left = aConstraints != null && aConstraints.getLeft() != null ? aConstraints.getLeft() : "0";
		if (left != null && !left.isEmpty()) {
			anchors.setLeft(new MarginConstraints.Margin(left));
		}
		String width = aConstraints != null && aConstraints.getWidth() != null ? aConstraints.getWidth() : "10";
		if (width != null && !width.isEmpty()) {
			anchors.setWidth(new MarginConstraints.Margin(width));
		}
		String top = aConstraints != null && aConstraints.getTop() != null ? aConstraints.getTop() : "0";
		if (top != null && !top.isEmpty()) {
			anchors.setTop(new MarginConstraints.Margin(top));
		}
		String height = aConstraints != null && aConstraints.getHeight() != null ? aConstraints.getHeight() : "10";
		if (height != null && !height.isEmpty()) {
			anchors.setHeight(new MarginConstraints.Margin(height));
		}
		add(aChild, anchors);
	}

	/**
	 * Takes into account inner structure of MarginLayoutContainer and possible
	 * FieldSet wrappers.
	 * 
	 * @param aChild
	 * @return
	 */
	protected Component lookupLayoutedChild(Widget aChild) {
		MarginConstraints anchors = (MarginConstraints) ((Component) aChild.getParent()).getData(MARGIN_LAYOUT_DATA);
		if (anchors == null)
			return (Component) aChild.getParent().getParent().getParent();
		else
			return (Component) aChild.getParent();
	}

	public void ajustWidth(Widget aChild, int aValue) {
		Component layouted = lookupLayoutedChild(aChild);
		MarginConstraints anchors = layouted.getData(MARGIN_LAYOUT_DATA);
		int containerWidth = layouted.getParent().getOffsetWidth();
		if (anchors.getWidth() != null) {
			anchors.getWidth().setPlainValue(aValue, containerWidth);
		} else if (anchors.getLeft() != null && anchors.getRight() != null) {
			anchors.getRight().setPlainValue(containerWidth - DOM.getElementPropertyInt(layouted.getElement(), "offsetLeft") - aValue, containerWidth);
		}
		applyConstraints(layouted, anchors);
	}

	public void ajustHeight(Widget aChild, int aValue) {
		Component layouted = lookupLayoutedChild(aChild);
		MarginConstraints anchors = layouted.getData(MARGIN_LAYOUT_DATA);
		int containerHeight = layouted.getParent().getOffsetHeight();
		if (anchors.getHeight() != null) {
			anchors.getHeight().setPlainValue(aValue, containerHeight);
		} else if (anchors.getTop() != null && anchors.getBottom() != null) {
			anchors.getBottom().setPlainValue(containerHeight - DOM.getElementPropertyInt(layouted.getElement(), "offsetTop") - aValue, containerHeight);
		}
		applyConstraints(layouted, anchors);
	}

	public void ajustLeft(Widget aChild, int aValue) {
		Component layouted = lookupLayoutedChild(aChild);
		MarginConstraints anchors = layouted.getData(MARGIN_LAYOUT_DATA);
		int containerWidth = layouted.getParent().getOffsetWidth();
		int childWidth = aChild.getParent().getOffsetWidth();
		if (anchors.getLeft() != null && anchors.getWidth() != null) {
			anchors.getLeft().setPlainValue(aValue, containerWidth);
		} else if (anchors.getWidth() != null && anchors.getRight() != null) {
			anchors.getRight().setPlainValue(containerWidth - aValue - childWidth, containerWidth);
		} else if (anchors.getLeft() != null && anchors.getRight() != null) {
			anchors.getLeft().setPlainValue(aValue, containerWidth);
			anchors.getRight().setPlainValue(containerWidth - aValue - childWidth, containerWidth);
		}
		applyConstraints(layouted, anchors);
	}

	public void ajustTop(Widget aChild, int aValue) {
		Component layouted = lookupLayoutedChild(aChild);
		MarginConstraints anchors = layouted.getData(MARGIN_LAYOUT_DATA);
		int containerHeight = layouted.getParent().getOffsetHeight();
		int childHeight = layouted.getOffsetHeight();
		if (anchors.getTop() != null && anchors.getHeight() != null) {
			anchors.getTop().setPlainValue(aValue, containerHeight);
		} else if (anchors.getHeight() != null && anchors.getBottom() != null) {
			anchors.getBottom().setPlainValue(containerHeight - aValue - childHeight, containerHeight);
		} else if (anchors.getTop() != null && anchors.getBottom() != null) {
			anchors.getTop().setPlainValue(aValue, containerHeight);
			anchors.getBottom().setPlainValue(containerHeight - aValue - childHeight, containerHeight);
		}
		applyConstraints(layouted, anchors);
	}

	@Override
	public void add(Widget aChild) {
		content.add(wrap(aChild));
	}

	public void setWidgetBottomHeight(Widget child, double bottom, Unit bottomUnit, double height, Unit heightUnit) {
		content.setWidgetBottomHeight(child, bottom, bottomUnit, height, heightUnit);
	}

	public void setWidgetBottomHeight(IsWidget child, double bottom, Unit bottomUnit, double height, Unit heightUnit) {
		content.setWidgetBottomHeight(child, bottom, bottomUnit, height, heightUnit);
	}

	public void setWidgetHorizontalPosition(Widget child, Alignment position) {
		content.setWidgetHorizontalPosition(child, position);
	}

	public void setWidgetLeftRight(Widget child, double left, Unit leftUnit, double right, Unit rightUnit) {
		content.setWidgetLeftRight(child, left, leftUnit, right, rightUnit);
	}

	public void setWidgetLeftRight(IsWidget child, double left, Unit leftUnit, double right, Unit rightUnit) {
		content.setWidgetLeftRight(child, left, leftUnit, right, rightUnit);
	}

	public void setWidgetLeftWidth(Widget child, double left, Unit leftUnit, double width, Unit widthUnit) {
		content.setWidgetLeftWidth(child, left, leftUnit, width, widthUnit);
	}

	public void setWidgetLeftWidth(IsWidget child, double left, Unit leftUnit, double width, Unit widthUnit) {
		content.setWidgetLeftWidth(child, left, leftUnit, width, widthUnit);
	}

	public void setWidgetRightWidth(Widget child, double right, Unit rightUnit, double width, Unit widthUnit) {
		content.setWidgetRightWidth(child, right, rightUnit, width, widthUnit);
	}

	public void setWidgetRightWidth(IsWidget child, double right, Unit rightUnit, double width, Unit widthUnit) {
		content.setWidgetRightWidth(child, right, rightUnit, width, widthUnit);
	}

	public void setWidgetTopBottom(Widget child, double top, Unit topUnit, double bottom, Unit bottomUnit) {
		content.setWidgetTopBottom(child, top, topUnit, bottom, bottomUnit);
	}

	public void setWidgetTopBottom(IsWidget child, double top, Unit topUnit, double bottom, Unit bottomUnit) {
		content.setWidgetTopBottom(child, top, topUnit, bottom, bottomUnit);
	}

	public void setWidgetTopHeight(Widget child, double top, Unit topUnit, double height, Unit heightUnit) {
		content.setWidgetTopHeight(child, top, topUnit, height, heightUnit);
	}

	public void setWidgetTopHeight(IsWidget child, double top, Unit topUnit, double height, Unit heightUnit) {
		content.setWidgetTopHeight(child, top, topUnit, height, heightUnit);
	}

	public void setWidgetVerticalPosition(Widget child, Alignment position) {
		content.setWidgetVerticalPosition(child, position);
	}

	public int getCompsCount() {
		return content.getWidgetCount();
	}

	public Widget getComp(int aIndex) {
		return unwrap(content.getWidget(aIndex));
	}

	public boolean removeComp(Widget aWidget) {
		if (aWidget != null) {
			Widget parent = aWidget.getParent();
			aWidget.removeFromParent();
			return content.remove(parent);
		}
		return true;
	}

	@Override
	public void toFront(Widget aWidget) {
		if (aWidget != null) {
			content.getElement().insertBefore(aWidget.getParent().getElement().getParentNode(), content.getElement().getLastChild()); // exclude
																																	  // last
																																	  // element
		}
	}

	@Override
	public void toFront(Widget aWidget, int aCount) {
		if (aWidget != null && aCount > 0) {
			XElement container = content.getElement().cast();
			Element widgetElement = aWidget.getParent().getElement().getParentElement();
			int index = container.getChildIndex(widgetElement);
			if (index < 0 || (index + aCount) >= container.getChildCount() - 1) {// exclude
																				 // last
																				 // element
				content.getElement().insertBefore(widgetElement, container.getLastChild());
			} else {
				content.getElement().insertAfter(widgetElement, container.getChild(index + aCount));
			}
		}
	}

	@Override
	public void toBack(Widget aWidget) {
		if (aWidget != null) {
			content.getElement().insertFirst(aWidget.getParent().getElement().getParentNode());
		}
	}

	@Override
	public void toBack(Widget aWidget, int aCount) {
		if (aWidget != null && aCount > 0) {
			XElement container = content.getElement().cast();
			Element widgetElement = aWidget.getParent().getElement().getParentElement();
			int index = container.getChildIndex(widgetElement);
			if (index < 0 || (index - aCount) < 0) {
				content.getElement().insertFirst(widgetElement);
			} else {
				content.getElement().insertBefore(widgetElement, container.getChild(index - aCount));
			}
		}
	}

	@Override
	public void clear() {
		if (content != null)
			content.clear();
		else
			super.clear();
	}
}
