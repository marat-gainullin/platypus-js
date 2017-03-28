package com.eas.widgets;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.eas.menu.HasComponentPopupMenu;
import com.eas.menu.PlatypusPopupMenu;
import com.eas.ui.HasEventsExecutor;
import com.eas.ui.HasJsFacade;
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
import com.eas.widgets.containers.SplittedPanel;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

public class SplitPane extends SplittedPanel implements HasJsFacade, HasEnabled, HasComponentPopupMenu, HasEventsExecutor, HasShowHandlers,
		HasHideHandlers, HasResizeHandlers, HasAddHandlers, HasRemoveHandlers, HasChildrenPosition {

	public static int HORIZONTAL_SPLIT = 1;
	public static int VERTICAL_SPLIT = 0;

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected boolean enabled = true;
	protected String name;
	protected JavaScriptObject published;

	protected Widget firstWidget;
	protected Widget secondWidget;

	protected boolean oneTouchExpandable;
	protected int orientation = HORIZONTAL_SPLIT;
	protected int dividerLocation = 84;

	public SplitPane() {
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

	public Widget getFirstWidget() {
		return firstWidget;
	}

	public void setFirstWidget(Widget aFirstWidget) {
		if (firstWidget != aFirstWidget) {
			if (firstWidget != null)
				firstWidget.removeFromParent();
			firstWidget = aFirstWidget;
			if (firstWidget != null) {
				if (orientation == HORIZONTAL_SPLIT) {
					addWest(firstWidget, dividerLocation);
				} else
					addNorth(firstWidget, dividerLocation);
			}
		}
	}

	public Widget getSecondWidget() {
		return secondWidget;
	}

	public void setSecondWidget(Widget aSecondWidget) {
		if (secondWidget != aSecondWidget) {
			if (secondWidget != null)
				secondWidget.removeFromParent();
			secondWidget = aSecondWidget;
			if (secondWidget != null) {
				add(secondWidget);
			}
		}
	}

	@Override
	public void addWest(Widget widget, double size) {
		widget.getElement().getStyle().clearWidth();
		widget.getElement().getStyle().clearHeight();
		super.addWest(widget, size);
		AddEvent.fire(this, widget);
	}

	@Override
	public void addEast(Widget widget, double size) {
		widget.getElement().getStyle().clearWidth();
		widget.getElement().getStyle().clearHeight();
		super.addEast(widget, size);
		AddEvent.fire(this, widget);
	}

	@Override
	public void addNorth(Widget widget, double size) {
		widget.getElement().getStyle().clearWidth();
		widget.getElement().getStyle().clearHeight();
		super.addNorth(widget, size);
		AddEvent.fire(this, widget);
	}

	@Override
	public void addSouth(Widget widget, double size) {
		widget.getElement().getStyle().clearWidth();
		widget.getElement().getStyle().clearHeight();
		super.addSouth(widget, size);
		AddEvent.fire(this, widget);
	}

	@Override
	public void add(Widget widget) {
		widget.getElement().getStyle().clearWidth();
		widget.getElement().getStyle().clearHeight();
		super.add(widget);
		AddEvent.fire(this, widget);
	}

	@Override
	public boolean remove(Widget child) {
		boolean res = super.remove(child);
		if (res) {
			RemoveEvent.fire(this, child);
		}
		return res;
	}

	public boolean isOneTouchExpandable() {
		return oneTouchExpandable;
	}

	public int getOrientation() {
		return orientation;
	}

	public int getDividerLocation() {
		return dividerLocation;
	}

	public void setOneTouchExpandable(boolean aValue) {
		if (oneTouchExpandable != aValue) {
			oneTouchExpandable = aValue;
			if (firstWidget != null)
				setWidgetToggleDisplayAllowed(firstWidget, oneTouchExpandable);
			if (secondWidget != null)
				setWidgetToggleDisplayAllowed(secondWidget, oneTouchExpandable);
		}
	}

	public void setOrientation(int aValue) {
		if (orientation != aValue) {
			orientation = aValue;
			if (firstWidget != null) {
				firstWidget.removeFromParent();
				if (orientation == HORIZONTAL_SPLIT) {
					addWest(firstWidget, dividerLocation);
				} else {
					addNorth(firstWidget, dividerLocation);
				}
			}
		}
	}

	public void setDividerLocation(int aValue) {
		if (dividerLocation != aValue) {
			dividerLocation = aValue;
			if (firstWidget != null)
				super.setWidgetSize(firstWidget, aValue);
		}
	}

	@Override
	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
			if (published != null) {
				publish(this, aValue);
			}
		}
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		var Orientation = @com.eas.ui.JsUi::Orientation;
		Object.defineProperty(published, "orientation", {
			get : function() {
				var orientation = aWidget.@com.eas.widgets.SplitPane::getOrientation()();
				if (orientation == @com.eas.widgets.SplitPane::VERTICAL_SPLIT) {
					return Orientation.VERTICAL;
				} else {
					return Orientation.HORIZONTAL;
				}
			},
			set : function(aOrientation) {
				if (aOrientation == Orientation.VERTICAL) {
					aWidget.@com.eas.widgets.SplitPane::setOrientation(I)(@com.eas.widgets.SplitPane::VERTICAL_SPLIT);
				} else {
					aWidget.@com.eas.widgets.SplitPane::setOrientation(I)(@com.eas.widgets.SplitPane::HORIZONTAL_SPLIT);
				}
			}
		});
		Object.defineProperty(published, "firstComponent", {
			get : function() {
				var comp = aWidget.@com.eas.widgets.SplitPane::getFirstWidget()();
				return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
			},
			set : function(aChild) {
				var child = (aChild == null ? null: aChild.unwrap());
				aWidget.@com.eas.widgets.SplitPane::setFirstWidget(Lcom/google/gwt/user/client/ui/Widget;)(child);
			}
		});
		Object.defineProperty(published, "secondComponent", {
			get : function() {
				var comp = aWidget.@com.eas.widgets.SplitPane::getSecondWidget()();
				return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
			},
			set : function(aChild) {
				var child = (aChild == null ? null: aChild.unwrap());
				aWidget.@com.eas.widgets.SplitPane::setSecondWidget(Lcom/google/gwt/user/client/ui/Widget;)(child);
			}
		});
		Object.defineProperty(published, "dividerLocation", {
			get : function() {
				return aWidget.@com.eas.widgets.SplitPane::getDividerLocation()();
			},
			set : function(aValue) {
				aWidget.@com.eas.widgets.SplitPane::setDividerLocation(I)(aValue);
			}
		});
		Object.defineProperty(published, "oneTouchExpandable", {
			get : function() {
				return aWidget.@com.eas.widgets.SplitPane::isOneTouchExpandable()();
			},
			set : function(aValue) {
				aComponent.@com.eas.widgets.SplitPane::setOneTouchExpandable(Z)(false != aValue);
			}
		});
		published.add = function(toAdd){
			if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined) {
				if(toAdd.parent == published)
					throw 'A widget already added to this container';
				if (published.firstComponent == null) {
					published.firstComponent = toAdd;
				}else {
					published.secondComponent = toAdd;
				}
			}
		}
		published.remove = function(aChild) {
			if (aChild != undefined && aChild != null && aChild.unwrap != undefined) {
				aWidget.@com.eas.widgets.SplitPane::remove(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());				
			}
		};
		published.child = function(aIndex) {
			var widget = aWidget.@com.eas.widgets.SplitPane::getWidget(I)(aIndex);
			return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(widget);
		};
	}-*/;

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
