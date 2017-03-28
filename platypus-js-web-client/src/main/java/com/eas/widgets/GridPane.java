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
import com.eas.widgets.containers.GridPanel;
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

/**
 * 
 * @author mg
 */
public class GridPane extends GridPanel implements HasJsFacade, HasEnabled, HasComponentPopupMenu, HasEventsExecutor, HasShowHandlers,
		HasHideHandlers, HasResizeHandlers, HasAddHandlers, HasRemoveHandlers, HasChildrenPosition {

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected boolean enabled = true;
	protected String name;
	protected JavaScriptObject published;

	public GridPane() {
		super(1, 1);
		getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		getElement().getStyle().setPosition(Style.Position.RELATIVE);
	}

	public GridPane(int aRows, int aCols) {
		super(aRows, aCols);
		getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		getElement().getStyle().setPosition(Style.Position.RELATIVE);
	}

	public GridPane(int aRows, int aCols, int aVGap, int aHGap) {
		this(aRows, aCols);
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
	public void setWidget(int row, int column, Widget widget) {
		super.setWidget(row, column, widget);
		AddEvent.fire(this, widget);
	}

	@Override
	public boolean remove(Widget widget) {
		boolean res = super.remove(widget);
		if (res) {
			RemoveEvent.fire(this, widget);
		}
		return res;
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
		published.add = function(toAdd, aRow, aCol){
			if(toAdd && toAdd.unwrap){
				if(toAdd.parent == published)
					throw 'A widget already added to this container';
				if(arguments.length < 3)
					throw 'aRow and aCol are required parameters';				
				aWidget.@com.eas.widgets.GridPane::setWidget(IILcom/google/gwt/user/client/ui/Widget;)(1 * aRow, 1 * aCol, toAdd.unwrap());
			}
		}
		published.remove = function(aChild) {
			if (aChild && aChild.unwrap) {
				aWidget.@com.eas.widgets.GridPane::remove(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());				
			}
		};
		published.child = function(aRow, aCol) {
			if (arguments.length > 1) {
				var widget = aWidget.@com.eas.widgets.GridPane::getWidget(II)(1 * aRow, 1 * aCol);
				return !!widget ? @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(widget) : null;
			}else
				return null;
		};
		Object.defineProperty(published, "hgap", {
			get : function(){
				return aWidget.@com.eas.widgets.GridPane::getHgap()();
			},
			set : function(aValue){
				aWidget.@com.eas.widgets.GridPane::setHgap(I)(aValue);
			}
		});
		Object.defineProperty(published, "vgap", {
			get : function(){
				return aWidget.@com.eas.widgets.GridPane::getVgap()();
			},
			set : function(aValue){
				aWidget.@com.eas.widgets.GridPane::setVgap(I)(aValue);
			}
		});
		Object.defineProperty(published, "rows", {
			get : function(){
				return aWidget.@com.eas.widgets.GridPane::getRowCount()();
			}
		});
		Object.defineProperty(published, "columns", {
			get : function(){
				return aWidget.@com.eas.widgets.GridPane::getColumnCount()();
			}
		});
		Object.defineProperty(published, "children", {
			value : function(){
				var ch = [];
				for(var r = 0; r < published.rows; r++){
					for(var c = 0; c < published.columns; c++){
						var index = published.columns * r + c;
						var comp = published.child(r, c);
						if(comp != null){
							ch.push(comp);
						}
					}
				}
				return ch;
			}
		});
		Object.defineProperty(published, "count", {
			get : function(){
				var ch = published.children;
				return ch.length;
			}
		});
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
