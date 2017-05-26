package com.eas.menu;

/**
 *
 * @author mg
 */
public class MenuItemRadioButton extends MenuItemCheckBox {

    public MenuItemRadioButton(Boolean aValue, String aText, boolean asHtml) {
        super(aValue, aText, asHtml);
        input.setAttribute("type", "radio");
    }
}
