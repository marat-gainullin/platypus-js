/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.menu;

import com.eas.ui.events.HasHideHandlers;
import com.eas.ui.events.HasShowHandlers;
import com.eas.ui.events.HideEvent;
import com.eas.ui.events.HideHandler;
import com.eas.ui.events.ShowEvent;
import com.eas.ui.events.ShowHandler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * 
 * @author mg
 */
public class MenuItemImageText extends MenuItem implements HasHandlers, HasShowHandlers, HasHideHandlers {

	protected String text;
	protected boolean html;
	protected SafeUri imageUri;
	//
	protected Element leftMark;
	protected Element field;
	//
	private HandlerManager handlerManager;

	public MenuItemImageText(String aText, boolean asHtml, SafeUri aImageUri, Scheduler.ScheduledCommand aCommand) {
		super("", aCommand);
		text = aText;
		html = asHtml;
		imageUri = aImageUri;
	    setStyleName("menu-item");
		regenerate();
	}

	@Override
	public HandlerRegistration addHideHandler(HideHandler handler) {
		return addHandler(handler, HideEvent.getType());
	}

	@Override
	public HandlerRegistration addShowHandler(ShowHandler handler) {
		return addHandler(handler, ShowEvent.getType());
	}

	@Override
	public void setVisible(boolean visible) {
		boolean oldValue = isVisible();
		super.setVisible(visible);
		if (oldValue != visible) {
			if (visible) {
				ShowEvent.fire(this, this);
			} else {
				HideEvent.fire(this, this);
			}
		}
	}

	/**
	 * Adds this handler to the widget.
	 * 
	 * @param <H>
	 *            the type of handler to add
	 * @param type
	 *            the event type
	 * @param handler
	 *            the handler
	 * @return {@link HandlerRegistration} used to remove the handler
	 */
	public final <H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type) {
		return ensureHandlers().addHandler(type, handler);
	}

	/**
	 * Ensures the existence of the handler manager.
	 * 
	 * @return the handler manager
	 * */
	HandlerManager ensureHandlers() {
		return handlerManager == null ? handlerManager = createHandlerManager() : handlerManager;
	}

	HandlerManager getHandlerManager() {
		return handlerManager;
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		ensureHandlers().fireEvent(event);
	}

	/**
	 * Creates the {@link HandlerManager} used by this Widget. You can override
	 * this method to create a custom {@link HandlerManager}.
	 * 
	 * @return the {@link HandlerManager} you want to use
	 */
	protected HandlerManager createHandlerManager() {
		return new HandlerManager(this);
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
		String ltext = text != null ? text : "";
		SafeHtml generated = MenuItemTemplates.INSTANCE.imageText(imageUri != null ? imageUri.asString() : "", html ? SafeHtmlUtils.fromTrustedString(ltext) : SafeHtmlUtils.fromString(ltext));
		getElement().setInnerSafeHtml(generated);
		leftMark = getElement().getFirstChildElement().getFirstChildElement();
		field = (Element) getElement().getFirstChildElement().getLastChild();
	}

}
