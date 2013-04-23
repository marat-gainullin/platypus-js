package com.eas.client.gxtcontrols.wrappers.container;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuBar;
import com.sencha.gxt.widget.core.client.menu.MenuBarItem;

public class PlatypusMenuBar extends MenuBar {

	public PlatypusMenuBar() {
		super();
	}

	@Override
	public void add(Widget aWidget) {
		if (aWidget instanceof PlatypusMenu) {
			PlatypusMenu menu = (PlatypusMenu) aWidget;
			menu.clearParentMenuItem();
			menu.clearParentMenuBarItem();
			MenuBarItem item = new MenuBarItem(menu.getText(), menu);
			menu.setParentMenuBarItem(item);
			super.add(item);
		} else
			super.add(aWidget);
	}

	@Override
	public boolean remove(Widget aWidget) {
		if (aWidget instanceof PlatypusMenu) {
			((PlatypusMenu) aWidget).clearParentMenuBarItem();
			return true;
		} else
			return super.remove(aWidget);
	}

	@Override
	public void clear() {
		for (int i = getWidgetCount() - 1; i >= 0; i--) {
			Widget w = getWidget(i);
			assert w instanceof MenuBarItem;
			MenuBarItem item = (MenuBarItem) w;
			if (item.getMenu() instanceof PlatypusMenu) {
				((PlatypusMenu) item.getMenu()).clearParentMenuBarItem();
			}
		}
		super.clear();
	}

	public Menu getMenu(int aIndex) {
		Widget item = super.getWidget(aIndex);
		assert item instanceof MenuBarItem;
		return ((MenuBarItem) item).getMenu();
	}
}
