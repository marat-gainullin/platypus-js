/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.menu;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * 
 * @author mg
 */
public class MenuItemImageText extends MenuItem {

	protected String text;
	protected boolean html;
	protected SafeUri imageUri;
	//
	protected Element leftMark;
	protected Element field;

	public MenuItemImageText(String aText, boolean asHtml, SafeUri aImageUri, Scheduler.ScheduledCommand aCommand) {
		super("", aCommand);
		text = aText;
		html = asHtml;
		imageUri = aImageUri;
		regenerate();
	}

	@Override
	public String getText() {
		return text;
	}

	public boolean isAsHtml() {
		return html;
	}

	public void setText(String aText, boolean asHtml) {
		text = aText;
		html = asHtml;
		regenerate();
	}

	@Override
	public void setText(String text) {
		setText(text, false);
	}

	@Override
	public void setHTML(String html) {
		setText(text, true);
	}

	public SafeUri getImageUri() {
		return imageUri;
	}

	public void setImageUri(SafeUri aValue) {
		imageUri = aValue;
		regenerate();
	}

	protected void regenerate() {
		SafeHtml generated = MenuItemTemplates.INSTANCE.imageText(imageUri != null ? imageUri.asString() : "", html ? SafeHtmlUtils.fromTrustedString(text) : SafeHtmlUtils.fromString(text));
		getElement().setInnerSafeHtml(generated);
		leftMark = getElement().getFirstChildElement().getFirstChildElement();
		field = (Element) getElement().getFirstChildElement().getLastChild();
	}

}
