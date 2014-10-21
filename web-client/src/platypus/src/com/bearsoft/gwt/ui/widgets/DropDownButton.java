/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets;

import com.bearsoft.gwt.ui.CommonResources;
import com.bearsoft.gwt.ui.HasImageResource;
import com.bearsoft.gwt.ui.XElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * 
 * @author mg
 */
public class DropDownButton extends Composite implements HasText, HasHTML, RequiresResize, HasClickHandlers, HasDoubleClickHandlers, HasEnabled, HasAllMouseHandlers, HasAllTouchHandlers,
        HasImageResource {

	protected FlowPanel container = new FlowPanel();
	protected SimplePanel contentWrapper = new SimplePanel();
	protected ImageLabel content;
	protected SimplePanel chevron = new SimplePanel();
	protected MenuBar menu;

	public DropDownButton(String aTitle, boolean asHtml, MenuBar aMenu) {
		this(aTitle, asHtml, null, aMenu);
	}

	public DropDownButton(String aTitle, boolean asHtml, ImageResource aImage, MenuBar aMenu) {
		initWidget(container);
		container.getElement().addClassName("gwt-Button");
		container.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		container.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		menu = aMenu;
		
		contentWrapper.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		contentWrapper.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		contentWrapper.getElement().getStyle().setTop(0, Style.Unit.PX);
		contentWrapper.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		contentWrapper.getElement().getStyle().setLeft(0, Style.Unit.PX);
		contentWrapper.getElement().getStyle().setPadding(0, Style.Unit.PX);
		contentWrapper.getElement().getStyle().setMargin(0, Style.Unit.PX);
		
		content = new ImageLabel(aTitle, asHtml, aImage);
		content.getElement().addClassName("dropdown-button");
		content.setHorizontalTextPosition(ImageParagraph.RIGHT);
		content.setVerticalTextPosition(ImageParagraph.CENTER);
		content.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		content.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		content.getElement().getStyle().setTop(0, Style.Unit.PX);
		content.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		content.getElement().getStyle().setLeft(0, Style.Unit.PX);
		content.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		content.getElement().getStyle().setPadding(0, Style.Unit.PX);
		contentWrapper.setWidget(content);

		chevron.getElement().addClassName("dropdown-menu");
		chevron.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		chevron.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		chevron.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);
		chevron.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		chevron.getElement().getStyle().setPadding(0, Style.Unit.PX);
		chevron.getElement().setInnerHTML("&nbsp;");

		CommonResources.INSTANCE.commons().ensureInjected();
		chevron.getElement().addClassName(CommonResources.INSTANCE.commons().unselectable());

		container.add(contentWrapper);
		container.add(chevron);
		chevron.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showMenu();
			}
		}, ClickEvent.getType());

		content.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				DropDownButton.this.fireEvent(event);
			}
		});
		organizeContentWrapperRight();
		getElement().<XElement>cast().addResizingTransitionEnd(this);
	}

	protected void organizeContentWrapperRight(){
		contentWrapper.getElement().getStyle().setRight(chevron.getElement().getOffsetWidth(), Style.Unit.PCT);
	}
	
	protected void showMenu() {
		if (menu != null) {
			final PopupPanel pp = new PopupPanel();
			pp.setAutoHideEnabled(true);
			pp.setAutoHideOnHistoryEventsEnabled(true);
			pp.setAnimationEnabled(true);
			pp.setWidget(menu);
			pp.showRelativeTo(chevron);
		}
	}

	public MenuBar getMenu() {
		return menu;
	}

	public void setMenu(MenuBar aMenu) {
		if (menu != null && menu.getParent() != null) {
			menu.removeFromParent();
		}
		menu = aMenu;
	}

	@Override
	public void onResize() {
		organizeContentWrapperRight();
		if (content instanceof RequiresResize) {
			((RequiresResize) content).onResize();
		}
	}

	public int getVerticalAlignment() {
		return content.getVerticalAlignment();
	}

	public void setVerticalAlignment(int aValue) {
		content.setVerticalAlignment(aValue);
	}

	public int getHorizontalAlignment() {
		return content.getHorizontalAlignment();
	}

	public void setHorizontalAlignment(int aValue) {
		content.setHorizontalAlignment(aValue);
	}

	@Override
	public String getText() {
		return content.getText();
	}

	@Override
	public void setText(String aValue) {
		content.setText(aValue);
	}

	@Override
	public String getHTML() {
		return content.getHTML();
	}

	@Override
	public void setHTML(String aValue) {
		content.setHTML(aValue);
	}

	public int getIconTextGap() {
		return content.getIconTextGap();
	}

	public void setIconTextGap(int aValue) {
		content.setIconTextGap(aValue);
	}

	public int getHorizontalTextPosition() {
		return content.getHorizontalTextPosition();
	}

	public void setHorizontalTextPosition(int aValue) {
		content.setHorizontalTextPosition(aValue);
	}

	public int getVerticalTextPosition() {
		return content.getVerticalTextPosition();
	}

	public void setVerticalTextPosition(int aValue) {
		content.setVerticalTextPosition(aValue);
	}

	@Override
	public ImageResource getImageResource() {
		return content.getImageResource();
	}

	@Override
	public void setImageResource(ImageResource aValue) {
		content.setImageResource(aValue);
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		organizeContentWrapperRight();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		// We shouldn't use addDomHandler here because of event redirecting
		return super.addHandler(handler, ClickEvent.getType());
	}

	@Override
	public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
		return super.addDomHandler(handler, DoubleClickEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return super.addDomHandler(handler, MouseDownEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return super.addDomHandler(handler, MouseMoveEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return super.addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return super.addDomHandler(handler, MouseOverEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return super.addDomHandler(handler, MouseUpEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		return super.addDomHandler(handler, MouseWheelEvent.getType());
	}

	@Override
	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
		return super.addDomHandler(handler, TouchStartEvent.getType());
	}

	@Override
	public HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler) {
		return super.addDomHandler(handler, TouchMoveEvent.getType());
	}

	@Override
	public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
		return super.addDomHandler(handler, TouchEndEvent.getType());
	}

	@Override
	public HandlerRegistration addTouchCancelHandler(TouchCancelHandler handler) {
		return super.addDomHandler(handler, TouchCancelEvent.getType());
	}

	@Override
	public boolean isEnabled() {
		return content.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		content.setEnabled(enabled);
		chevron.getElement().setPropertyBoolean("disabled", !enabled);
		getElement().setPropertyBoolean("disabled", !enabled);
	}
}
