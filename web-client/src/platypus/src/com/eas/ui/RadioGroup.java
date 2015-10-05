/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.ui;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.UIObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author mg
 */
public class RadioGroup extends UIObject implements ValueChangeHandler<Boolean> {

	protected Map<HasValue<Boolean>, HandlerRegistration> groupedHandlers = new HashMap<>();
	protected List<HasValue<Boolean>> grouped = new ArrayList<>();

	protected String groupName = "group-" + DOM.createUniqueId();
	//
	private HandlerManager handlerManager;

	public RadioGroup() {
		super();
	}

	public String getGroupName() {
		return groupName;
	}

	public HasValue<Boolean> get(int aIndex) {
		return grouped.get(aIndex);
	}

	public void add(HasValue<Boolean> aItem) {
		if (!grouped.contains(aItem)) {
			if (aItem instanceof HasName) {
				((HasName) aItem).setName(groupName);
			}
			groupedHandlers.put(aItem, aItem.addValueChangeHandler(this));
			grouped.add(aItem);
		}
	}

	public boolean remove(HasValue<Boolean> aItem) {
		HandlerRegistration handler = groupedHandlers.get(aItem);
		if (handler != null) {
			handler.removeHandler();
		}
		if (aItem instanceof HasName) {
			((HasName) aItem).setName("");
		}
		return grouped.remove(aItem);
	}

	public void clear() {
		for (HandlerRegistration handler : groupedHandlers.values()) {
			if (handler != null) {
				handler.removeHandler();
			}
		}
		groupedHandlers.clear();
		for (HasValue<Boolean> item : grouped) {
			if (item instanceof HasName) {
				((HasName) item).setName("");
			}
		}
		grouped.clear();
	}

	public int size() {
		return grouped.size();
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		if (Boolean.TRUE.equals(event.getValue())) {
			for (HasValue<Boolean> hv : grouped) {
				if (hv != event.getSource()) {
					hv.setValue(Boolean.FALSE, false);
				}
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

}
