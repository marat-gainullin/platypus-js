/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author mg
 */
public class RadioGroup implements ValueChangeHandler<Boolean> {

	protected Map<HasValue<Boolean>, HandlerRegistration> groupedHandlers = new HashMap<>();
	protected List<HasValue<Boolean>> grouped = new ArrayList<>();

	protected String groupName = "group-" + DOM.createUniqueId();

	public RadioGroup() {
		super();
	}

	public String getGroupName() {
		return groupName;
	}
	
	public HasValue<Boolean> get(int aIndex){
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
}
