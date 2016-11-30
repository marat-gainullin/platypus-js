/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.menu;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 *
 * @author mg
 */
public interface MenuItemTemplates extends SafeHtmlTemplates {

    public static final MenuItemTemplates INSTANCE = GWT.create(MenuItemTemplates.class);

    @Template("<div><div style=\"display: inline; position: relative; height: 100%; background-image:url('{0}'); background-repeat: no-repeat;\" class=\"menu-left-mark\">&nbsp;&nbsp;&nbsp;&nbsp;</div><div style=\"display: inline; white-space: nowrap;\" class=\"menu-field\">{1}</div></div>")
    public SafeHtml imageText(String aImageUrl, SafeHtml aHtml);
    
    @Template("<div><div style=\"display: inline; position: relative; height: 100%;\" class=\"menu-left-mark\"><input type = \"checkbox\" class=\"menu-left-check-radio\" style=\"position: absolute; width: 100%;\"></input>&nbsp;&nbsp;&nbsp;&nbsp;</div><div style=\"display: inline; white-space: nowrap;\" class=\"menu-field\">{0}</div></div>")
    public SafeHtml checkBox(SafeHtml aHtml);

    @Template("<div><div style=\"display: inline; position: relative; height: 100%;\" class=\"menu-left-mark\"><input type = \"radio\" class=\"menu-left-check-radio\" style=\"position: absolute; width: 100%;\"></input>&nbsp;&nbsp;&nbsp;&nbsp;</div><div style=\"display: inline; white-space: nowrap;\" class=\"menu-field\">{0}</div></div>")
    public SafeHtml radioButton(SafeHtml aHtml);
}
