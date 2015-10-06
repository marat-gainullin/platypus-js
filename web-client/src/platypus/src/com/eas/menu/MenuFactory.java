package com.eas.menu;

import com.eas.predefine.Utils;
import com.eas.ui.UiReader;
import com.eas.ui.UiWidgetReader;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.xml.client.Element;

public class MenuFactory implements UiWidgetReader{

	public UIObject readWidget(Element anElement, final UiReader aFactory) throws Exception {
		String type = anElement.getTagName();
		switch (type) {
		case "Menu":
			PlatypusMenu menu = new PlatypusMenu();
			MenuPublisher.publish(menu);
			aFactory.readGeneralProps(anElement, menu);
			if (anElement.hasAttribute("text")) {
				menu.setText(anElement.getAttribute("text"));
			}
			return menu;
		case "MenuItem":
			PlatypusMenuItemImageText menuitem = new PlatypusMenuItemImageText();
			MenuPublisher.publish(menuitem);
			aFactory.readGeneralProps(anElement, menuitem);
			aFactory.readImageParagraph(anElement, menuitem);
			if (anElement.hasAttribute("text")) {
				menuitem.setText(anElement.getAttribute("text"));
			}
			return menuitem;
		case "CheckMenuItem":
			PlatypusMenuItemCheckBox checkMenuItem = new PlatypusMenuItemCheckBox();
			MenuPublisher.publish(checkMenuItem);
			aFactory.readGeneralProps(anElement, checkMenuItem);
			aFactory.readImageParagraph(anElement, checkMenuItem);
			if (anElement.hasAttribute("selected")) {
				boolean selected = Utils.getBooleanAttribute(anElement, "selected", Boolean.FALSE);
				checkMenuItem.setValue(selected);
			}
			if (anElement.hasAttribute("text")) {
				checkMenuItem.setText(anElement.getAttribute("text"));
			}
			return checkMenuItem;
		case "RadioMenuItem":
			PlatypusMenuItemRadioButton radioMenuItem = new PlatypusMenuItemRadioButton();
			MenuPublisher.publish(radioMenuItem);
			aFactory.readGeneralProps(anElement, radioMenuItem);
			aFactory.readImageParagraph(anElement, radioMenuItem);
			if (anElement.hasAttribute("selected")) {
				boolean selected = Utils.getBooleanAttribute(anElement, "selected", Boolean.FALSE);
				radioMenuItem.setValue(selected);
			}
			if (anElement.hasAttribute("text")) {
				radioMenuItem.setText(anElement.getAttribute("text"));
			}
			return radioMenuItem;
		case "MenuSeparator":
			PlatypusMenuItemSeparator menuSeparator = new PlatypusMenuItemSeparator();
			MenuPublisher.publish(menuSeparator);
			aFactory.readGeneralProps(anElement, menuSeparator);
			return menuSeparator;
		case "MenuBar":
			PlatypusMenuBar menuBar = new PlatypusMenuBar();
			MenuPublisher.publish(menuBar);
			aFactory.readGeneralProps(anElement, menuBar);
			return menuBar;
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
