package com.eas.menu;

import com.eas.core.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.UIObject;

public class PlatypusMenu extends PlatypusMenuBar {

	protected String text;

	public PlatypusMenu() {
		super(true);
		setAutoOpen(true);
	}

	@Override
	public boolean add(UIObject aChild) {
		if (aChild instanceof PlatypusMenu) {
			PlatypusMenu subMenu = (PlatypusMenu) aChild;
			MenuItem item = new PlatypusMenuItemImageText();
			item.setSubMenu(subMenu);
			item.setText(subMenu.getText());
			subMenu.setParentItem(item);
			addItem(item);
			allItems.add(aChild);
			return true;
		} else
			return super.add(aChild);
	}

	public String getText() {
		return text;
	}

	public void setText(String aValue) {
		text = aValue;
		if (parentItem != null)
			parentItem.setText(text);
	}

	@Override
	public void setParentItem(MenuItem aItem) {
		super.setParentItem(aItem);
		if (parentItem != null)
			parentItem.setText(text);
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			super.setPublished(aValue);
			if (published != null) {
				publish(this, aValue);
			}
		}
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		Object.defineProperty(published, "text", {
			get : function() {
				return aWidget.@com.eas.menu.PlatypusMenu::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.menu.PlatypusMenu::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
			}
		});
	}-*/;
}
