package com.eas.menu;

import com.eas.core.Utils;
import com.eas.ui.UiReader;
import com.eas.ui.UiWidgetReader;
import com.eas.ui.Widget;
import com.google.gwt.xml.client.Element;

public class MenuFactory implements UiWidgetReader {

    public Widget readWidget(Element anElement, final UiReader aFactory) throws Exception {
        String type = anElement.getTagName();
        switch (type) {
            case "m":
            case "Menu":
                Menu menu = new Menu();
                MenuPublisher.publish(menu);
                aFactory.readGeneralProps(anElement, menu);
                if (Utils.hasAttribute(anElement, "tx", "text")) {
                    menu.setText(Utils.getAttribute(anElement, "tx", "text", null));
                }
                return menu;
            case "mi":
            case "MenuItem":
                MenuItemImageText menuitem = new MenuItemImageText();
                MenuPublisher.publish(menuitem);
                aFactory.readGeneralProps(anElement, menuitem);
                aFactory.readImageParagraph(anElement, menuitem);
                if (Utils.hasAttribute(anElement, "tx", "text")) {
                    menuitem.setText(Utils.getAttribute(anElement, "tx", "text", null));
                }
                return menuitem;
            case "cmi":
            case "CheckMenuItem":
                MenuItemCheckBox checkMenuItem = new MenuItemCheckBox();
                MenuPublisher.publish(checkMenuItem);
                aFactory.readGeneralProps(anElement, checkMenuItem);
                aFactory.readImageParagraph(anElement, checkMenuItem);
                if (Utils.hasAttribute(anElement, "st", "selected")) {
                    boolean selected = Utils.getBooleanAttribute(anElement, "st", "selected", Boolean.FALSE);
                    checkMenuItem.setJsValue(selected);
                }
                if (Utils.hasAttribute(anElement, "tx", "text")) {
                    checkMenuItem.setText(Utils.getAttribute(anElement, "tx", "text", null));
                }
                return checkMenuItem;
            case "rmi":
            case "RadioMenuItem":
                MenuItemRadioButton radioMenuItem = new MenuItemRadioButton();
                MenuPublisher.publish(radioMenuItem);
                aFactory.readGeneralProps(anElement, radioMenuItem);
                aFactory.readImageParagraph(anElement, radioMenuItem);
                if (Utils.hasAttribute(anElement, "st", "selected")) {
                    boolean selected = Utils.getBooleanAttribute(anElement, "st", "selected", Boolean.FALSE);
                    radioMenuItem.setJsValue(selected);
                }
                if (Utils.hasAttribute(anElement, "tx", "text")) {
                    radioMenuItem.setText(Utils.getAttribute(anElement, "tx", "text", null));
                }
                return radioMenuItem;
            case "ms":
            case "MenuSeparator":
                MenuItemSeparator menuSeparator = new MenuItemSeparator();
                MenuPublisher.publish(menuSeparator);
                aFactory.readGeneralProps(anElement, menuSeparator);
                return menuSeparator;
            case "mb":
            case "MenuBar":
                MenuBar menuBar = new MenuBar();
                MenuPublisher.publish(menuBar);
                aFactory.readGeneralProps(anElement, menuBar);
                return menuBar;
            case "pm":
            case "PopupMenu":
                PopupMenu popupMenu = new PopupMenu();
                MenuPublisher.publish(popupMenu);
                aFactory.readGeneralProps(anElement, popupMenu);
                return popupMenu;
            default:
                return null;
        }
    }
}
