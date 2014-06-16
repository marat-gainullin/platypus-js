package com.eas.client.form.published.widgets;

import java.util.ArrayList;
import java.util.List;

import com.bearsoft.gwt.ui.XElement;
import com.bearsoft.gwt.ui.containers.window.WindowUI;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.events.HasHideHandlers;
import com.eas.client.form.events.HasShowHandlers;
import com.eas.client.form.events.HideEvent;
import com.eas.client.form.events.HideHandler;
import com.eas.client.form.events.ShowEvent;
import com.eas.client.form.events.ShowHandler;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasEventsExecutor;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class DesktopPane extends FlowPanel implements RequiresResize, ProvidesResize, HasJsFacade, HasEnabled, HasComponentPopupMenu, HasEventsExecutor, HasShowHandlers, HasHideHandlers, HasResizeHandlers {

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected boolean enabled = true;
	protected String name;	
	protected JavaScriptObject published;

	public static final int DEFAULT_WINDOWS_SPACING_X = 15;
	public static final int DEFAULT_WINDOWS_SPACING_Y = 10;
	protected List<WindowUI> managed = new ArrayList<>();
	protected Point consideredPosition = new Point(DEFAULT_WINDOWS_SPACING_X, DEFAULT_WINDOWS_SPACING_Y);

	public DesktopPane() {
		super();
		getElement().getStyle().setOverflow(Style.Overflow.AUTO);
	}

	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
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
		if(!oldValue && enabled){
			getElement().<XElement>cast().unmask();
		}else if(oldValue && !enabled){
			getElement().<XElement>cast().disabledMask();
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

	public List<WindowUI> getManaged() {
		return managed;
	}

	public List<HasPublished> getPublishedManaged() {
		List<HasPublished> res = new ArrayList<>();
		for(WindowUI wui : managed)
			if(wui instanceof HasPublished)
				res.add((HasPublished)wui);
		return res;
	}

	public void minimizeAll() {
		for (WindowUI wd : managed) {
			wd.minimize();
		}
	}

	public void maximizeAll() {
		for (WindowUI wd : managed) {
			wd.maximize();
		}
	}

	public void restoreAll() {
		for (WindowUI wd : managed) {
			wd.restore();
		}
	}

	public void closeAll() {
		for (WindowUI wd : managed) {
			wd.close();
		}
		managed.clear();
	}

	public Point getConsideredPosition() {
		return consideredPosition;
	}

	@Override
	public void add(Widget child) {
		super.add(child);
		if (child instanceof WindowUI) {
			child.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
			refreshConsideredPosition();
		}
	}

	@Override
	public void onResize() {
		refreshConsideredPosition();
		if(isAttached()){
			ResizeEvent.fire(this, getElement().getOffsetWidth(), getElement().getOffsetHeight());
		}
	}

	private void refreshConsideredPosition() {
		if (consideredPosition.getX() > getElement().getClientWidth() / 2) {
			consideredPosition = consideredPosition.plus(new Point(-consideredPosition.getX(), 0));// setX(0)
		}
		if (consideredPosition.getY() > getElement().getClientHeight() / 2) {
			consideredPosition = consideredPosition.plus(new Point(0, -consideredPosition.getY()));// setY(0)
		}
		consideredPosition = consideredPosition.plus(new Point(DEFAULT_WINDOWS_SPACING_X, DEFAULT_WINDOWS_SPACING_Y));
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
		Object.defineProperty(published, "forms", {
			get : function(){
				var managed = aWidget.@com.eas.client.form.published.widgets.DesktopPane::getPublishedManaged()();
				var res = [];
				for(var i = 0; i < managed.@java.util.List::size()(); i++){
					var m = managed.@java.util.List::get(I)(i);
					res[res.length] = m.@com.eas.client.form.published.HasPublished::getPublished()();
				}
				return res;
			} 
		});
		published.closeAll = function(){
			aWidget.@com.eas.client.form.published.widgets.DesktopPane::closeAll()();				
		}
		published.minimizeAll = function(){
			aWidget.@com.eas.client.form.published.widgets.DesktopPane::minimizeAll()();				
		}
		published.maximizeAll = function(){
			aWidget.@com.eas.client.form.published.widgets.DesktopPane::maximizeAll()();				
		}
		published.restoreAll = function(){
			aWidget.@com.eas.client.form.published.widgets.DesktopPane::restoreAll()();				
		}
	}-*/;
}
