package com.eas.widgets;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.eas.menu.HasComponentPopupMenu;
import com.eas.menu.PlatypusPopupMenu;
import com.eas.ui.HasEventsExecutor;
import com.eas.ui.HasJsFacade;
import com.eas.ui.HorizontalPosition;
import com.eas.ui.VerticalPosition;
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
import com.eas.widgets.containers.BorderPanel;
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

public class BorderPane extends BorderPanel implements HasJsFacade, HasEnabled, HasComponentPopupMenu, HasEventsExecutor, HasShowHandlers, HasHideHandlers, HasResizeHandlers, HasAddHandlers,
        HasRemoveHandlers, HasChildrenPosition {

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected boolean enabled = true;
	protected String name;
	protected JavaScriptObject published;

	public BorderPane() {
		super();
		getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		getElement().getStyle().setPosition(Style.Position.RELATIVE);
	}

	public BorderPane(int aVGap, int aHGap) {
		this();
		getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		getElement().getStyle().setPosition(Style.Position.RELATIVE);
		setHgap(aHGap);
		setVgap(aVGap);
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

	@Override
	public boolean remove(Widget w) {
		boolean res = super.remove(w);
		if (res)
			RemoveEvent.fire(this, w);
		return res;
	}

	public void add(Widget aWidget, int aPlace, int aSize) {
		switch (aPlace) {
		case HorizontalPosition.LEFT:
			setLeftComponent(aWidget, aSize);
			break;
		case HorizontalPosition.RIGHT:
			setRightComponent(aWidget, aSize);
			break;
		case VerticalPosition.TOP:
			setTopComponent(aWidget, aSize);
			break;
		case VerticalPosition.BOTTOM:
			setBottomComponent(aWidget, aSize);
			break;
		default:
			setCenterComponent(aWidget);
			break;
		}
	}

	public Widget getLeftComponent() {
		for (Widget w : getChildren()) {
			Direction d = getResolvedDirection(getWidgetDirection(w));
			if (d == Direction.WEST) {
				return w;
			}
		}
		return null;
	}

	public void setLeftComponent(Widget w, double size) {
		Widget old = getLeftComponent();
		if (old != w) {
			if (old != null)
				remove(old);
			if (w != null) {
				Widget centerWidget = super.getCenter();
				if (centerWidget != null)
					super.remove(centerWidget);
				addWest(w, size);
				if (centerWidget != null)
					super.add(centerWidget);
				AddEvent.fire(this, w);
			}
		}
	}

	public Widget getRightComponent() {
		for (Widget w : getChildren()) {
			Direction d = getResolvedDirection(getWidgetDirection(w));
			if (d == Direction.EAST) {
				return w;
			}
		}
		return null;
	}

	public void setRightComponent(Widget w, double size) {
		Widget old = getRightComponent();
		if (old != w) {
			if (old != null)
				remove(old);
			if (w != null) {
				Widget centerWidget = super.getCenter();
				if (centerWidget != null)
					super.remove(centerWidget);
				addEast(w, size);
				if (centerWidget != null)
					super.add(centerWidget);
				AddEvent.fire(this, w);
			}
		}
	}

	public Widget getTopComponent() {
		for (Widget w : getChildren()) {
			Direction d = getResolvedDirection(getWidgetDirection(w));
			if (d == Direction.NORTH) {
				return w;
			}
		}
		return null;
	}

	public void setTopComponent(Widget w, double size) {
		Widget old = getTopComponent();
		if (old != w) {
			if (old != null)
				remove(old);
			if (w != null) {
				Widget centerWidget = super.getCenter();
				if (centerWidget != null)
					super.remove(centerWidget);
				addNorth(w, size);
				if (centerWidget != null)
					super.add(centerWidget);
				AddEvent.fire(this, w);
			}
		}
	}

	public Widget getBottomComponent() {
		for (Widget w : getChildren()) {
			Direction d = getResolvedDirection(getWidgetDirection(w));
			if (d == Direction.SOUTH) {
				return w;
			}
		}
		return null;
	}

	public void setBottomComponent(Widget w, double size) {
		Widget old = getBottomComponent();
		if (old != w) {
			if (old != null)
				remove(old);
			if (w != null) {
				Widget centerWidget = super.getCenter();
				if (centerWidget != null)
					super.remove(centerWidget);
				addSouth(w, size);
				if (centerWidget != null)
					super.add(centerWidget);
				AddEvent.fire(this, w);
			}
		}
	}

	public Widget getCenterComponent() {
		return getCenter();
	}

	public void setCenterComponent(Widget w) {
		Widget old = getCenter();
		if (old != w) {
			if (old != null)
				remove(old);
			if (w != null) {
				super.add(w);
				AddEvent.fire(this, w);
			}
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
		var VerticalPosition = @com.eas.ui.JsUi::VerticalPosition;		
		var HorizontalPosition = @com.eas.ui.JsUi::HorizontalPosition;		
		Object.defineProperty(published, "hgap", {
			get : function(){
				return aWidget.@com.eas.widgets.BorderPane::getHgap()();
			},
			set : function(aValue){
				aWidget.@com.eas.widgets.BorderPane::setHgap(I)(aValue);
			}
		});
		Object.defineProperty(published, "vgap", {
			get : function(){
				return aWidget.@com.eas.widgets.BorderPane::getVgap()();
			},
			set : function(aValue){
				aWidget.@com.eas.widgets.BorderPane::setVgap(I)(aValue);
			}
		});
		Object.defineProperty(published, "leftComponent", {
			get : function() {
				var comp = aWidget.@com.eas.widgets.BorderPane::getLeftComponent()();
				return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
			},
			set : function(aChild) {
				aWidget.@com.eas.widgets.BorderPane::setLeftComponent(Lcom/google/gwt/user/client/ui/Widget;D)(aChild.unwrap(), toAdd.width);
			}
		});
		Object.defineProperty(published, "rightComponent", {
			get : function() {
				var comp = aWidget.@com.eas.widgets.BorderPane::getRightComponent()();
				return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
			},
			set : function(aChild) {
				aWidget.@com.eas.widgets.BorderPane::setRightComponent(Lcom/google/gwt/user/client/ui/Widget;D)(aChild.unwrap(), toAdd.width);
			}
		});
		Object.defineProperty(published, "topComponent", {
			get : function() {
				var comp = aWidget.@com.eas.widgets.BorderPane::getTopComponent()();
				return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
			},
			set : function(aChild) {
				aWidget.@com.eas.widgets.BorderPane::setTopComponent(Lcom/google/gwt/user/client/ui/Widget;D)(aChild.unwrap(), toAdd.height);
			}
		});
		Object.defineProperty(published, "bottomComponent", {
			get : function() {
				var comp = aWidget.@com.eas.widgets.BorderPane::getBottomComponent()();
				return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
			},
			set : function(aChild) {
				aWidget.@com.eas.widgets.BorderPane::setBottomComponent(Lcom/google/gwt/user/client/ui/Widget;D)(aChild.unwrap(), toAdd.height);
			}
		});
		Object.defineProperty(published, "centerComponent", {
			get : function() {
				var comp = aWidget.@com.eas.widgets.BorderPane::getCenterComponent()();
				return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
			},
			set : function(aChild) {
			 	aWidget.@com.eas.widgets.BorderPane::setCenterComponent(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());
			}
		});
		published.add = function(toAdd, region, aSize) {
			if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined){
				if(toAdd.parent == published)
					throw 'A widget already added to this container';
				if(!region){
					region = VerticalPosition.CENTER;
				}
				switch (region) {
					case VerticalPosition.CENTER:
						aWidget.@com.eas.widgets.BorderPane::setCenterComponent(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
						break;  
					case VerticalPosition.TOP: 
						if (!aSize) {
							aSize = toAdd.height;
							if (!aSize) {
								aSize = 32;
							}
						}
						aWidget.@com.eas.widgets.BorderPane::setTopComponent(Lcom/google/gwt/user/client/ui/Widget;D)(toAdd.unwrap(), aSize);
						break;  
					case VerticalPosition.BOTTOM: 
						if (!aSize) {
							aSize = toAdd.height;
							if (!aSize) {
								aSize = 32;
							}
						}
						aWidget.@com.eas.widgets.BorderPane::setBottomComponent(Lcom/google/gwt/user/client/ui/Widget;D)(toAdd.unwrap(), aSize);
						break;  
					case HorizontalPosition.LEFT: 
						if (!aSize) {
							aSize = toAdd.width;
							if (!aSize) {
								aSize = 32
							}
						}
						aWidget.@com.eas.widgets.BorderPane::setLeftComponent(Lcom/google/gwt/user/client/ui/Widget;D)(toAdd.unwrap(), aSize);
						break;  
					case HorizontalPosition.RIGHT: 
						if (!aSize) {
							aSize = toAdd.width;
							if (!aSize) {
								aSize = 32
							}
						}
						aWidget.@com.eas.widgets.BorderPane::setRightComponent(Lcom/google/gwt/user/client/ui/Widget;D)(toAdd.unwrap(), aSize);
						break;  
				}
			}
		}
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
