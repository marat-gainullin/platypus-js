/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.menu;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 *
 * @author mg
 */
public class MenuItemRadioButton extends MenuItemCheckBox {

    public MenuItemRadioButton(Boolean aValue, String aText, boolean asHtml) {
        super(aValue, aText, asHtml);
    }

    @Override
    protected SafeHtml generateHtml() {
    	String ltext = text != null ? text : "";
        return MenuItemTemplates.INSTANCE.radioButton(html ? SafeHtmlUtils.fromTrustedString(ltext) : SafeHtmlUtils.fromString(ltext));
    }
}
