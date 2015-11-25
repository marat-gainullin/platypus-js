/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.widgets.boxes;

import com.eas.core.Utils;
import com.eas.core.Utils.OnChangeHandler;
import com.eas.ui.CommonResources;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * 
 * @author mg
 */
public class DropDownButton extends ImageButton {

	protected FlowPanel chevron = new FlowPanel();
	protected SimplePanel chevronAnchor = new SimplePanel();
	protected SimplePanel chevronMenu = new SimplePanel();
	protected MenuBar menu;

	public DropDownButton() {
		this("", false, null);
	}

	public DropDownButton(String aTitle, boolean asHtml, MenuBar aMenu) {
		this(aTitle, asHtml, null, aMenu);
	}

	public DropDownButton(String aTitle, boolean asHtml, ImageResource aImage, MenuBar aMenu) {
		super(aTitle, asHtml, aImage);
		menu = aMenu;

		CommonResources.INSTANCE.commons().ensureInjected();

		chevron.getElement().addClassName("dropdown");
		chevronMenu.getElement().addClassName("dropdown-menu");
		chevronMenu.getElement().addClassName(CommonResources.INSTANCE.commons().unselectable());
		chevronAnchor.getElement().addClassName("dropdown-split");
		chevronAnchor.getElement().addClassName(CommonResources.INSTANCE.commons().unselectable());
		chevron.add(chevronAnchor);
		chevron.add(chevronMenu);

		getElement().insertFirst(chevron.getElement());

		chevron.getElement().setPropertyJSO("onclick", Utils.publishOnChangeHandler(new OnChangeHandler() {

			@Override
			public void onChange(JavaScriptObject anEvent) {
				anEvent.<NativeEvent> cast().stopPropagation();
				showMenu();
			}
		}));
	}

	protected void showMenu() {
		if (menu != null) {
			final PopupPanel pp = new PopupPanel();
			pp.setAutoHideEnabled(true);
			pp.setAutoHideOnHistoryEventsEnabled(true);
			pp.setAnimationEnabled(true);
			pp.setWidget(menu);
			pp.showRelativeTo(chevronMenu);
		}
	}

	public MenuBar getMenu() {
		return menu;
	}

	public void setMenu(MenuBar aMenu) {
		if (menu != aMenu) {
			menu = aMenu;
			chevronAnchor.getElement().getStyle().setDisplay(menu != null ? Style.Display.INLINE_BLOCK : Style.Display.NONE);
			chevronMenu.getElement().getStyle().setDisplay(menu != null ? Style.Display.INLINE_BLOCK : Style.Display.NONE);
		}
	}
}
