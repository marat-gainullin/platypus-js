package com.eas.widgets;

import java.util.HashMap;
import java.util.Map;

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
import com.eas.widgets.containers.CardsPanel;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class CardPane extends CardsPanel implements HasJsFacade, HasEnabled, HasComponentPopupMenu, HasEventsExecutor, HasShowHandlers,
		HasHideHandlers, HasResizeHandlers, HasAddHandlers, HasRemoveHandlers, HasSelectionHandlers<Widget>, HasChildrenPosition {

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected boolean enabled = true;
	protected String name;
	protected JavaScriptObject published;

	private Map<String, Widget> cards = new HashMap<String, Widget>();

	public CardPane(int aVGap, int aHGap) {
		super();
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
	public HandlerRegistration addSelectionHandler(SelectionHandler<Widget> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

	@Override
	public void showWidget(Widget newWidget) {
		Widget oldWidget = visibleWidget;
		super.showWidget(newWidget);
		if (oldWidget != newWidget) {
			SelectionEvent.fire(this, newWidget);
		}
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

	public void show(String aCardName) {
		if (cards.containsKey(aCardName)) {
			Widget toShow = cards.get(aCardName);
			showWidget(toShow);
		}
	}

	public void add(Widget aWidget, String aCardName) {
		if (cards.containsKey(aCardName))
			super.remove(cards.get(aCardName));
		super.add(aWidget);
		cards.put(aCardName, aWidget);
		AddEvent.fire(this, aWidget);
	}

	@Override
	public boolean remove(Widget aWidget) {
		for (String cardName : cards.keySet()) {
			if (cards.get(cardName) == aWidget) {
				cards.remove(cardName);
				break;
			}
		}
		boolean res = super.remove(aWidget);
		if (res)
			RemoveEvent.fire(this, aWidget);
		return res;
	}

	@Override
	public void clear() {
		cards.clear();
		super.clear();
	}

	public Widget getWidget(String aCardName) {
		return cards.get(aCardName);
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
		Object.defineProperty(published, "hgap", {
			get : function(){
				return aWidget.@com.eas.widgets.CardPane::getHgap()();
			},
			set : function(aValue){
				aWidget.@com.eas.widgets.CardPane::setHgap(I)(aValue);
			}
		});
		Object.defineProperty(published, "vgap", {
			get : function(){
				return aWidget.@com.eas.widgets.CardPane::getVgap()();
			},
			set : function(aValue){
				aWidget.@com.eas.widgets.CardPane::setVgap(I)(aValue);
			}
		});
		published.add = function(toAdd, aCardName){
			if(toAdd && toAdd.unwrap){
				if(toAdd.parent == published)
					throw 'A widget already added to this container';
				aWidget.@com.eas.widgets.CardPane::add(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;)(toAdd.unwrap(), aCardName);
			}
		};
		published.child = function(aIndex_or_aCardName) {
			var widget;
			if (isNaN(aIndex_or_aCardName)) {
				widget = aWidget.@com.eas.widgets.CardPane::getWidget(Ljava/lang/String;)(aIndex_or_aCardName);
			}else {
				var index = parseInt(aIndex_or_aCardName, 10);
				widget = aWidget.@com.eas.widgets.CardPane::getWidget(I)(index);
			}
			return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(widget);
		};
		published.show = function(aCardName) {
			aWidget.@com.eas.widgets.CardPane::show(Ljava/lang/String;)(aCardName);
		};			
	}-*/;

	@Override
	public int getTop(Widget aWidget) {
		assert aWidget.getParent() == this : "widget should be a child of this container";
		return 0;
	}

	@Override
	public int getLeft(Widget aWidget) {
		assert aWidget.getParent() == this : "widget should be a child of this container";
		return 0;
	}
}
