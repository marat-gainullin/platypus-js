package com.eas.menu;

import com.eas.core.Utils;
import com.eas.ui.UiReader;
import com.eas.ui.UiWidgetReader;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.xml.client.Element;

public class MenuFactory implements UiWidgetReader{

	public UIObject readWidget(Element anElement, final UiReader aFactory) throws Exception {
		String type = anElement.getTagName();
		switch (type) {
		case "m":
		case "Menu":
			PlatypusMenu menu = new PlatypusMenu();
			MenuPublisher.publish(menu);
			aFactory.readGeneralProps(anElement, menu);
			if (Utils.hasAttribute(anElement, "tx", "text")) {
				menu.setText(Utils.getAttribute(anElement, "tx", "text", null));
			}
			return menu;
		case "mi":
		case "MenuItem":
			PlatypusMenuItemImageText menuitem = new PlatypusMenuItemImageText();
			MenuPublisher.publish(menuitem);
			aFactory.readGeneralProps(anElement, menuitem);
			aFactory.readImageParagraph(anElement, menuitem);
			if (Utils.hasAttribute(anElement, "tx", "text")) {
				menuitem.setText(Utils.getAttribute(anElement, "tx", "text", null));
			}
			return menuitem;
		case "cmi":
		case "CheckMenuItem":
			PlatypusMenuItemCheckBox checkMenuItem = new PlatypusMenuItemCheckBox();
			MenuPublisher.publish(checkMenuItem);
			aFactory.readGeneralProps(anElement, checkMenuItem);
			aFactory.readImageParagraph(anElement, checkMenuItem);
			if (Utils.hasAttribute(anElement, "st", "selected")) {
				boolean selected = Utils.getBooleanAttribute(anElement, "st", "selected", Boolean.FALSE);
				checkMenuItem.setValue(selected);
			}
			if (Utils.hasAttribute(anElement, "tx", "text")) {
				checkMenuItem.setText(Utils.getAttribute(anElement, "tx", "text", null));
			}
			return checkMenuItem;
		case "rmi":
		case "RadioMenuItem":
			PlatypusMenuItemRadioButton radioMenuItem = new PlatypusMenuItemRadioButton();
			MenuPublisher.publish(radioMenuItem);
			aFactory.readGeneralProps(anElement, radioMenuItem);
			aFactory.readImageParagraph(anElement, radioMenuItem);
			if (Utils.hasAttribute(anElement, "st", "selected")) {
				boolean selected = Utils.getBooleanAttribute(anElement, "st", "selected", Boolean.FALSE);
				radioMenuItem.setValue(selected);
			}
			if (Utils.hasAttribute(anElement, "tx", "text")) {
				radioMenuItem.setText(Utils.getAttribute(anElement, "tx", "text", null));
			}
			return radioMenuItem;
		case "ms":
		case "MenuSeparator":
			PlatypusMenuItemSeparator menuSeparator = new PlatypusMenuItemSeparator();
			MenuPublisher.publish(menuSeparator);
			aFactory.readGeneralProps(anElement, menuSeparator);
			return menuSeparator;
		case "mb": 
		case "MenuBar":
			PlatypusMenuBar menuBar = new PlatypusMenuBar();
			MenuPublisher.publish(menuBar);
			aFactory.readGeneralProps(anElement, menuBar);
			return menuBar;
		case "pm":
		case "PopupMenu":
			PlatypusPopupMenu popupMenu = new PlatypusPopupMenu();
			MenuPublisher.publish(popupMenu);
			aFactory.readGeneralProps(anElement, popupMenu);
			return popupMenu;
		default:
			return null;
		}
	}
}
