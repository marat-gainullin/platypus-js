package com.eas.client.gxtcontrols.wrappers.container;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuBarItem;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public class PlatypusMenu extends Menu {

	protected String text;
	protected MenuBarItem parentMenuBarItem = null;

	public PlatypusMenu() {
		super();
	}

	public String getText() {
		return text;
	}

	public void setText(String aValue) {
		text = aValue;
	}

	@Override
	public void add(Widget aWidget) {
		if (aWidget instanceof PlatypusMenu) {
			PlatypusMenu menu = (PlatypusMenu) aWidget;
			menu.clearParentMenuItem();
			menu.clearParentMenuBarItem();
			MenuItem item = new MenuItem(menu.getText());
			item.setSubMenu(menu);
			super.add(item);
		} else {
			super.add(aWidget);
		}
	}

	@Override
	public boolean remove(Widget aWidget) {
		if (aWidget instanceof PlatypusMenu) {
			((PlatypusMenu) aWidget).clearParentMenuItem();
			return true;
		} else
			return super.remove(aWidget);
	}

	public void clearParentMenuItem() {
		if (parentItem != null) {
			if (parentItem instanceof MenuItem)
				((MenuItem) parentItem).setSubMenu(null);
			parentItem.removeFromParent();
			parentItem = null;
		}
	}

	public void clearParentMenuBarItem() {
		if (parentMenuBarItem != null) {
			parentMenuBarItem.setMenu(null);
			parentMenuBarItem.removeFromParent();
			parentMenuBarItem = null;
		}
	}

	public MenuBarItem getParentMenuBarItem() {
		return parentMenuBarItem;
	}

	public void setParentMenuBarItem(MenuBarItem aItem) {
		parentMenuBarItem = aItem;
	}

}
