package com.eas.menu;

import java.util.ArrayList;
import java.util.List;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.eas.ui.HasCustomParent;
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
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.UIObject;

public class PlatypusMenuBar extends MenuBar implements HasJsFacade, HasEnabled, HasEventsExecutor, HasShowHandlers, HasHideHandlers, HasResizeHandlers, RequiresResize, HasAddHandlers,
        HasRemoveHandlers, HasCustomParent {

	protected EventsExecutor eventsExecutor;
	protected boolean enabled = true;
	protected String name;
	protected JavaScriptObject published;

	protected List<UIObject> allItems = new ArrayList<>();

	protected MenuItem parentItem;

	public PlatypusMenuBar() {
		this(false);
	}

	public PlatypusMenuBar(boolean aVertical) {
		super(aVertical);
		setStylePrimaryName("menu-bar");
	}
	
	@Override
	public UIObject getCustomParent() {
		return parentItem != null ? parentItem : getParent(); 
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

	public MenuItem getParentItem() {
		return parentItem;
	}

	public void setParentItem(MenuItem aItem) {
		parentItem = aItem;
	}

	public UIObject getItem(int aIndex) {
		if (aIndex >= 0 && aIndex < allItems.size()) {
			UIObject item = allItems.get(aIndex);
			if (item instanceof MenuItem && ((MenuItem) item).getSubMenu() != null)
				return ((MenuItem) item).getSubMenu();
			else
				return item;
		} else
			return null;
	}

	public int getCount() {
		return allItems.size();
	}

	public boolean add(UIObject aChild) {
		boolean added;
		if (aChild instanceof MenuItemImageText) {
			addItem((MenuItemImageText) aChild);
			allItems.add(aChild);
			added = true;
		} else if (aChild instanceof PlatypusMenuItemSeparator) {
			addSeparator((PlatypusMenuItemSeparator) aChild);
			allItems.add(aChild);
			added = true;
		} else if (aChild instanceof PlatypusMenu) {
			PlatypusMenu subMenu = (PlatypusMenu) aChild;
			MenuItem item = new MenuItem(subMenu.getText(), false, subMenu);
			item.setStyleName("menu-item");
			subMenu.setParentItem(item);
			addItem(item);
			allItems.add(aChild);
			added = true;
		} else
			added = false;
		if (added) {
			AddEvent.fire(this, aChild);
		}
		return added;
	}

	public boolean remove(UIObject aChild) {
		boolean removed = false;
		if (aChild instanceof MenuItem) {
			removeItem((MenuItem) aChild);
			allItems.remove(aChild);
			removed = true;
		} else if (aChild instanceof PlatypusMenuItemSeparator) {
			removeSeparator((PlatypusMenuItemSeparator) aChild);
			allItems.remove(aChild);
			removed = true;
		} else if (aChild instanceof PlatypusMenu) {
			removeItem(((PlatypusMenu) aChild).getParentItem());
			allItems.remove(aChild);
			removed = true;
		}
		if (removed) {
			RemoveEvent.fire(this, aChild);
		}
		return removed; 
	}

	@Override
	public void clearItems() {
		super.clearItems();
		allItems.clear();
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
		published.add = function(toAdd){
			if(toAdd && toAdd.unwrap){
				if(toAdd.parent == published)
					throw 'A widget already added to this menu';
				aWidget.@com.eas.menu.PlatypusMenuBar::add(Lcom/google/gwt/user/client/ui/UIObject;)(toAdd.unwrap());
			}
		};
		published.remove = function(aChild) {
			if (aChild && aChild.unwrap) {
				aWidget.@com.eas.menu.PlatypusMenuBar::remove(Lcom/google/gwt/user/client/ui/UIObject;)(aChild.unwrap());
			}
		};
		published.clear = function() {
			aWidget.@com.eas.menu.PlatypusMenuBar::clearItems()();
		};
		published.child = function(aIndex) {
			var comp = aWidget.@com.eas.menu.PlatypusMenuBar::getItem(I)(aIndex);
			return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
		};
		Object.defineProperty(published, "count", {
			get : function() {
				return aWidget.@com.eas.menu.PlatypusMenuBar::getCount()();
			}
		});
		Object.defineProperty(published, "children", {
			value : function(){
				var ch = [];
				for(var i=0; i < published.count; i++)
					ch.push(published.child(i));
				return ch;
		    }
		});
	}-*/;
}
