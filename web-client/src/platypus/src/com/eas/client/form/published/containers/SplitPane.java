package com.eas.client.form.published.containers;

import com.bearsoft.gwt.ui.XElement;
import com.bearsoft.gwt.ui.containers.SplittedPanel;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.events.AddEvent;
import com.eas.client.form.events.AddHandler;
import com.eas.client.form.events.HasAddHandlers;
import com.eas.client.form.events.HasHideHandlers;
import com.eas.client.form.events.HasRemoveHandlers;
import com.eas.client.form.events.HasShowHandlers;
import com.eas.client.form.events.HideEvent;
import com.eas.client.form.events.HideHandler;
import com.eas.client.form.events.RemoveEvent;
import com.eas.client.form.events.RemoveHandler;
import com.eas.client.form.events.ShowEvent;
import com.eas.client.form.events.ShowHandler;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasEventsExecutor;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

public class SplitPane extends SplittedPanel implements HasJsFacade, HasEnabled, HasComponentPopupMenu, HasEventsExecutor, HasShowHandlers, HasHideHandlers, HasResizeHandlers, HasAddHandlers,
        HasRemoveHandlers {

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
	protected int dividerSize = 8;

	public SplitPane() {
		super();
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
		super.addWest(widget, size);
		AddEvent.fire(this, widget);
	}

	@Override
	public void addEast(Widget widget, double size) {
		super.addEast(widget, size);
		AddEvent.fire(this, widget);
	}

	@Override
	public void addNorth(Widget widget, double size) {
		super.addNorth(widget, size);
		AddEvent.fire(this, widget);
	}

	@Override
	public void addSouth(Widget widget, double size) {
		super.addSouth(widget, size);
		AddEvent.fire(this, widget);
	}

	@Override
	public void add(Widget widget) {
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

	public int getDividerSize() {
		return dividerSize;
	}

	public void setDividerSize(int aValue) {
		if (dividerSize != aValue) {
			dividerSize = aValue;
			for (int i = 0; i < getWidgetCount(); i++) {
				Widget w = getWidget(i);
				if (w.getStyleName().contains("gwt-SplitLayoutPanel-HDragger") || w.getStyleName().contains("gwt-SplitLayoutPanel-VDragger")) {
					w.getElement().getStyle().setPropertyPx("width", dividerSize);
				}
			}
			forceLayout();
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
		Object.defineProperty(published, "orientation", {
			get : function() {
				var orientation = aWidget.@com.eas.client.form.published.containers.SplitPane::getOrientation()();
				if (orientation == @com.eas.client.form.published.containers.SplitPane::VERTICAL_SPLIT) {
					return $wnd.P.Orientation.VERTICAL;
				} else {
					return $wnd.P.Orientation.HORIZONTAL;
				}
			},
			set : function(aOrientation) {
				if (aOrientation == $wnd.P.Orientation.VERTICAL) {
					aWidget.@com.eas.client.form.published.containers.SplitPane::setOrientation(I)(@com.eas.client.form.published.containers.SplitPane::VERTICAL_SPLIT);
				} else {
					aWidget.@com.eas.client.form.published.containers.SplitPane::setOrientation(I)(@com.eas.client.form.published.containers.SplitPane::HORIZONTAL_SPLIT);
				}
			}
		});
		Object.defineProperty(published, "firstComponent", {
			get : function() {
				var comp = aWidget.@com.eas.client.form.published.containers.SplitPane::getFirstWidget()();
				return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
			},
			set : function(aChild) {
				var child = (aChild == null ? null: aChild.unwrap());
				aWidget.@com.eas.client.form.published.containers.SplitPane::setFirstWidget(Lcom/google/gwt/user/client/ui/Widget;)(child);
			}
		});
		Object.defineProperty(published, "secondComponent", {
			get : function() {
				var comp = aWidget.@com.eas.client.form.published.containers.SplitPane::getSecondWidget()();
				return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
			},
			set : function(aChild) {
				var child = (aChild == null ? null: aChild.unwrap());
				aWidget.@com.eas.client.form.published.containers.SplitPane::setSecondWidget(Lcom/google/gwt/user/client/ui/Widget;)(child);
			}
		});
		Object.defineProperty(published, "dividerLocation", {
			get : function() {
				return aWidget.@com.eas.client.form.published.containers.SplitPane::getDividerLocation()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.containers.SplitPane::setDividerLocation(I)(aValue);
			}
		});
		Object.defineProperty(published, "oneTouchExpandable", {
			get : function() {
				return aWidget.@com.eas.client.form.published.containers.SplitPane::isOneTouchExpandable()();
			},
			set : function(aValue) {
				aComponent.@com.eas.client.form.published.containers.SplitPane::setOneTouchExpandable(Z)(false != aValue);
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
				aWidget.@com.eas.client.form.published.containers.SplitPane::remove(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());				
			}
		};
		published.child = function(aIndex) {
			var widget = aWidget.@com.eas.client.form.published.containers.SplitPane::getWidget(I)(aIndex);
			return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(widget);
		};
	}-*/;
}
