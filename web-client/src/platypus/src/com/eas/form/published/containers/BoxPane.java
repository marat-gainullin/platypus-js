package com.eas.form.published.containers;

import com.bearsoft.gwt.ui.Orientation;
import com.bearsoft.gwt.ui.XElement;
import com.bearsoft.gwt.ui.containers.BoxPanel;
import com.bearsoft.gwt.ui.events.HasHideHandlers;
import com.bearsoft.gwt.ui.events.HasShowHandlers;
import com.bearsoft.gwt.ui.events.HideEvent;
import com.bearsoft.gwt.ui.events.HideHandler;
import com.bearsoft.gwt.ui.events.ShowEvent;
import com.bearsoft.gwt.ui.events.ShowHandler;
import com.eas.client.HasPublished;
import com.eas.form.EventsExecutor;
import com.eas.form.events.AddEvent;
import com.eas.form.events.AddHandler;
import com.eas.form.events.HasAddHandlers;
import com.eas.form.events.HasRemoveHandlers;
import com.eas.form.events.RemoveEvent;
import com.eas.form.events.RemoveHandler;
import com.eas.form.published.HasComponentPopupMenu;
import com.eas.form.published.HasEventsExecutor;
import com.eas.form.published.HasJsFacade;
import com.eas.form.published.PublishedComponent;
import com.eas.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

public class BoxPane extends BoxPanel implements HasJsFacade, HasEnabled, HasComponentPopupMenu, HasEventsExecutor, HasShowHandlers, HasHideHandlers, HasResizeHandlers, HasAddHandlers,
        HasRemoveHandlers, HasChildrenPosition {

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected boolean enabled = true;
	protected String name;
	protected JavaScriptObject published;

	public BoxPane() {
		super();
	}

	public BoxPane(int aOrientation, int aHGap, int aVGap) {
		super();
		setHgap(aHGap);
		setVgap(aVGap);
		setOrientation(aOrientation);
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
	public void add(Widget child) {
		super.add(child);
		AddEvent.fire(this, child);
	}

	public void add(Widget child, int size) {
		super.add(child);
		AddEvent.fire(this, child);
		if(orientation == Orientation.HORIZONTAL){
			ajustWidth(child, size);
		}else{
			ajustHeight(child, size);
		}
	}

	@Override
	public boolean remove(Widget w) {
		boolean res = super.remove(w);
		if (res) {
			RemoveEvent.fire(this, w);
		}
		return res;
	}

	@Override
	protected void setAjustedWidth(double aValue) {
		published.<PublishedComponent>cast().setWidth(aValue);
	}

	@Override
	protected void setAjustedHeight(double aValue) {
		published.<PublishedComponent> cast().setHeight(aValue);
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

	private native static void publish(HasPublished aWidget, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "hgap", {
			get : function(){
				return aWidget.@com.eas.form.published.containers.CardPane::getHgap()();
			},
			set : function(aValue){
				aWidget.@com.eas.form.published.containers.CardPane::setHgap(I)(aValue);
			}
		});
		Object.defineProperty(aPublished, "vgap", {
			get : function(){
				return aWidget.@com.eas.form.published.containers.CardPane::getVgap()();
			},
			set : function(aValue){
				aWidget.@com.eas.form.published.containers.CardPane::setVgap(I)(aValue);
			}
		});
	}-*/;

	@Override
	public int getTop(Widget aWidget) {
		assert aWidget.getParent() == this : "widget should be a child of this container";
		return 0;
	}

	@Override
	public int getLeft(Widget aWidget) {
		assert aWidget.getParent() == this : "widget should be a child of this container";
		return aWidget.getElement().getOffsetLeft();
	}
}
