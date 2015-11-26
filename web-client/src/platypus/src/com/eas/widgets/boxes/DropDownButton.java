/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.widgets.boxes;

import com.eas.ui.CommonResources;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
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
		super(Document.get().createDivElement(), aTitle, asHtml, aImage);
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

		addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
			}

		});
		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
			}

		});
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (event.getTypeInt() == Event.ONCLICK || event.getTypeInt() == Event.ONMOUSEDOWN) {
			Element target = Element.as(event.getEventTarget());
			if (target == chevron.getElement() || target == chevronAnchor.getElement() || target == chevronMenu.getElement()) {
				event.preventDefault();
				event.stopPropagation();
				showMenu();
			} else {
				super.onBrowserEvent(event);
			}
		} else
			super.onBrowserEvent(event);
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
