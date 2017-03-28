package com.eas.widgets;

import java.util.HashMap;
import java.util.Map;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.eas.menu.HasComponentPopupMenu;
import com.eas.menu.PlatypusPopupMenu;
import com.eas.ui.HasEventsExecutor;
import com.eas.ui.HasJsFacade;
import com.eas.ui.MarginConstraints;
import com.eas.ui.PublishedAbsoluteConstraints;
import com.eas.ui.PublishedMarginConstraints;
import com.eas.ui.events.AddEvent;
import com.eas.ui.events.AddHandler;
import com.eas.ui.events.EventsExecutor;
import com.eas.ui.events.HasAddHandlers;
import com.eas.ui.events.HasHideHandlers;
import com.eas.ui.events.HasRemoveHandlers;
import com.eas.ui.events.HasShowHandlers;
import com.eas.ui.events.HideEvent;
import com.eas.ui.events.HideHandler;
import com.eas.ui.events.RemoveEvent;
import com.eas.ui.events.RemoveHandler;
import com.eas.ui.events.ShowEvent;
import com.eas.ui.events.ShowHandler;
import com.eas.widgets.containers.AnchorsPanel;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.dom.client.Style;

public class MarginsPane extends AnchorsPanel implements HasLayers, HasPublished, HasJsFacade, HasEnabled, HasComponentPopupMenu,
		HasEventsExecutor, HasShowHandlers, HasHideHandlers, HasResizeHandlers, HasAddHandlers, HasRemoveHandlers, HasChildrenPosition {

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected boolean enabled = true;
	protected String name;
	protected JavaScriptObject published;

	protected Map<Widget, MarginConstraints> constraints = new HashMap<>();

	public MarginsPane() {
		super();
		getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		getElement().getStyle().setPosition(Style.Position.RELATIVE);
	}

	@Override
	public HandlerRegistration addAddHandler(AddHandler handler) {
		return addHandler(handler, AddEvent.getType());
	}

	@Override
	public HandlerRegistration addRemoveHandler(RemoveHandler handler) {
		return addHandler(handler, RemoveEvent.getType());
	}

	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}

	@Override
	public void onResize() {
		super.onResize();
		if (isAttached()) {
			ResizeEvent.fire(this, getElement().getOffsetWidth(), getElement().getOffsetHeight());
		}
	}

	@Override
	public HandlerRegistration addHideHandler(HideHandler handler) {
		return addHandler(handler, HideEvent.getType());
	}

	@Override
	public HandlerRegistration addShowHandler(ShowHandler handler) {
		return addHandler(handler, ShowEvent.getType());
	}

	@Override
	public void setVisible(boolean visible) {
		boolean oldValue = isVisible();
		super.setVisible(visible);
		if (oldValue != visible) {
			if (visible) {
				ShowEvent.fire(this, this);
			} else {
				HideEvent.fire(this, this);
			}
		}
	}

	@Override
	public EventsExecutor getEventsExecutor() {
		return eventsExecutor;
	}

	@Override
	public void setEventsExecutor(EventsExecutor aExecutor) {
		eventsExecutor = aExecutor;
	}

	@Override
	public PlatypusPopupMenu getPlatypusPopupMenu() {
		return menu;
	}

	protected HandlerRegistration menuTriggerReg;

	@Override
	public void setPlatypusPopupMenu(PlatypusPopupMenu aMenu) {
		if (menu != aMenu) {
			if (menuTriggerReg != null)
				menuTriggerReg.removeHandler();
			menu = aMenu;
			if (menu != null) {
				menuTriggerReg = super.addDomHandler(new ContextMenuHandler() {

					@Override
					public void onContextMenu(ContextMenuEvent event) {
						event.preventDefault();
						event.stopPropagation();
						menu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
						menu.show();
					}
				}, ContextMenuEvent.getType());
			}
		}
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean aValue) {
		boolean oldValue = enabled;
		enabled = aValue;
		if (!oldValue && enabled) {
			getElement().<XElement> cast().unmask();
		} else if (oldValue && !enabled) {
			getElement().<XElement> cast().disabledMask();
		}
	}

	@Override
	public String getJsName() {
		return name;
	}

	@Override
	public void setJsName(String aValue) {
		name = aValue;
	}

	protected void applyConstraints(Widget aWidget, MarginConstraints aConstraints) {
		constraints.put(aWidget, aConstraints);

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
		aWidget.getElement().getStyle().setMargin(0, Style.Unit.PX);
		if (isAttached()) {
			forceLayout();
		}
	}

	public void add(Widget aChild, PublishedMarginConstraints aConstraints) {
		super.add(aChild);
		MarginConstraints anchors = new MarginConstraints();
		int h = 0;
		int v = 0;
		String left = aConstraints.getLeft();
		if (left != null && !left.isEmpty()) {
			anchors.setLeft(MarginConstraints.Margin.parse(left));
			h++;
		}
		String right = aConstraints.getRight();
		if (right != null && !right.isEmpty()) {
			anchors.setRight(MarginConstraints.Margin.parse(right));
			h++;
		}
		String width = aConstraints.getWidth();
		if (width != null && !width.isEmpty()) {
			anchors.setWidth(MarginConstraints.Margin.parse(width));
			h++;
		}
		String top = aConstraints.getTop();
		if (top != null && !top.isEmpty()) {
			anchors.setTop(MarginConstraints.Margin.parse(top));
			v++;
		}
		String bottom = aConstraints.getBottom();
		if (bottom != null && !bottom.isEmpty()) {
			anchors.setBottom(MarginConstraints.Margin.parse(bottom));
			v++;
		}
		String height = aConstraints.getHeight();
		if (height != null && !height.isEmpty()) {
			anchors.setHeight(MarginConstraints.Margin.parse(height));
			v++;
		}
		if (h < 2)
			throw new IllegalArgumentException("There are must be at least two horizontal values in anchors.");
		if (v < 2)
			throw new IllegalArgumentException("There are must be at least two vertical values in anchors.");
		applyConstraints(aChild, anchors);
	}

	public void add(Widget aChild, PublishedAbsoluteConstraints aConstraints) {
		MarginConstraints anchors = new MarginConstraints();
		String left = aConstraints != null && aConstraints.getLeft() != null ? aConstraints.getLeft() : "0";
		if (left != null && !left.isEmpty()) {
			anchors.setLeft(MarginConstraints.Margin.parse(left));
		}
		String width = aConstraints != null && aConstraints.getWidth() != null ? aConstraints.getWidth() : "10";
		if (width != null && !width.isEmpty()) {
			anchors.setWidth(MarginConstraints.Margin.parse(width));
		}
		String top = aConstraints != null && aConstraints.getTop() != null ? aConstraints.getTop() : "0";
		if (top != null && !top.isEmpty()) {
			anchors.setTop(MarginConstraints.Margin.parse(top));
		}
		String height = aConstraints != null && aConstraints.getHeight() != null ? aConstraints.getHeight() : "10";
		if (height != null && !height.isEmpty()) {
			anchors.setHeight(MarginConstraints.Margin.parse(height));
		}
		add(aChild);
		applyConstraints(aChild, anchors);
	}

	public void add(Widget aChild, MarginConstraints anchors) {
		add(aChild);
		applyConstraints(aChild, anchors);
	}

	@Override
	public void add(Widget widget) {
		super.add(widget);
		AddEvent.fire(this, widget);
	}

	@Override
	public boolean remove(Widget w) {
		constraints.remove(w);
		boolean res = super.remove(w);
		if (res) {
			RemoveEvent.fire(this, w);
		}
		return res;
	}

	@Override
	public void clear() {
		super.clear();
		constraints.clear();
	}

	public void ajustWidth(Widget layouted, int aValue) {
		MarginConstraints anchors = constraints.get(layouted);
		int containerWidth = layouted.getParent().getOffsetWidth();
		if (anchors.getWidth() != null) {
			anchors.getWidth().setPlainValue(aValue, containerWidth);
		} else if (anchors.getLeft() != null && anchors.getRight() != null) {
			anchors.getRight().setPlainValue(containerWidth - layouted.getElement().getParentElement().getOffsetLeft() - aValue, containerWidth);
		}
		applyConstraints(layouted, anchors);
	}

	public void ajustHeight(Widget layouted, int aValue) {
		MarginConstraints anchors = constraints.get(layouted);
		int containerHeight = layouted.getParent().getElement().getOffsetHeight();
		if (anchors.getHeight() != null) {
			anchors.getHeight().setPlainValue(aValue, containerHeight);
		} else if (anchors.getTop() != null && anchors.getBottom() != null) {
			anchors.getBottom().setPlainValue(containerHeight - layouted.getElement().getParentElement().getOffsetTop() - aValue, containerHeight);
		}
		applyConstraints(layouted, anchors);
	}

	public void ajustLeft(Widget layouted, int aValue) {
		MarginConstraints anchors = constraints.get(layouted);
		int containerWidth = layouted.getParent().getElement().getOffsetWidth();
		int childWidth = layouted.getElement().getParentElement().getOffsetWidth();
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

	public void ajustTop(Widget layouted, int aValue) {
		MarginConstraints anchors = constraints.get(layouted);
		int containerHeight = layouted.getParent().getElement().getOffsetHeight();
		int childHeight = layouted.getElement().getParentElement().getOffsetHeight();
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
	public void toFront(Widget aWidget) {
		if (aWidget != null) {
			getElement().insertBefore(aWidget.getElement().getParentNode(), getElement().getLastChild()); // exclude
																											// last
																											// element
		}
	}

	@Override
	public void toFront(Widget aWidget, int aCount) {
		if (aWidget != null && aCount > 0) {
			XElement container = getElement().cast();
			Element widgetElement = aWidget.getElement().getParentElement();
			int index = container.getChildIndex(widgetElement);
			if (index < 0 || (index + aCount) >= container.getChildCount() - 1) {// exclude
																					// last
																					// element
				getElement().insertBefore(widgetElement, container.getLastChild());
			} else {
				getElement().insertAfter(widgetElement, container.getChild(index + aCount));
			}
		}
	}

	@Override
	public void toBack(Widget aWidget) {
		if (aWidget != null) {
			getElement().insertFirst(aWidget.getElement().getParentElement());
		}
	}

	@Override
	public void toBack(Widget aWidget, int aCount) {
		if (aWidget != null && aCount > 0) {
			XElement container = getElement().cast();
			Element widgetElement = aWidget.getElement().getParentElement();
			int index = container.getChildIndex(widgetElement);
			if (index < 0 || (index - aCount) < 0) {
				getElement().insertFirst(widgetElement);
			} else {
				getElement().insertBefore(widgetElement, container.getChild(index - aCount));
			}
		}
	}

	@Override
	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		published = aValue;
	}

	@Override
	public int getTop(Widget aWidget) {
		assert aWidget.getParent() == this : "widget should be a child of this container";
		return aWidget.getElement().getParentElement().getOffsetTop();
	}

	@Override
	public int getLeft(Widget aWidget) {
		assert aWidget.getParent() == this : "widget should be a child of this container";
		return aWidget.getElement().getParentElement().getOffsetLeft();
	}
}
