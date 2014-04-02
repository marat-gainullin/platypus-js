package com.eas.client.form.published.menu;

import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class PlatypusMenuBar extends MenuBar implements HasJsFacade, HasEnabled {

	protected boolean enabled;
	protected String name;	
	protected JavaScriptObject published;
	
	protected MenuItem parentItem;

	public PlatypusMenuBar() {
		super();
	}

	public PlatypusMenuBar(boolean aVertical) {
		super(aVertical);
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean aValue) {
		enabled = aValue;
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

	public MenuItem getItem(int aIndex) {
		if (aIndex >= 0 && aIndex < getItems().size())
			return getItems().get(aIndex);
		else
			return null;
	}

	public int getItemsCount(){
		return getItems().size();
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
				aWidget.@com.eas.client.form.published.menu.PlatypusMenuBar::addItem(Lcom/google/gwt/user/client/ui/MenuItem;)(toAdd.unwrap());
			}
		};
		published.remove = function(aChild) {
			if (aChild && aChild.unwrap) {
				aWidget.@com.eas.client.form.published.menu.PlatypusMenuBar::removeItem(Lcom/google/gwt/user/client/ui/MenuItem;)(aChild.unwrap());
			}
		};
		published.clear = function() {
			aWidget.@com.eas.client.form.published.menu.PlatypusMenuBar::clearItems()();
		};
		published.child = function(aIndex) {
			var comp = aComponent.@com.eas.client.form.published.menu.PlatypusMenuBar::getItem(I)(aIndex);
			return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
		};
		Object.defineProperty(published, "count", {
			get : function() {
				return aComponent.@com.eas.client.form.published.menu.PlatypusMenuBar::getItemsCount()();
			}
		});
	}-*/;
}
