/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.widgets.boxes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eas.ui.HasDecorationsWidth;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;

/**
 * 
 * @author mg
 * @param <T>
 */
public class StyledListBox<T> extends ListBox implements HasValue<T>, HasDecorationsWidth {

	protected List<T> associatedValues = new ArrayList<>();
	protected Map<T, Integer> indicies;
	protected T value;

	public StyledListBox() {
		this(false);
	}

	public StyledListBox(boolean isMultipleSelect) {
		super();
		setStyleName("form-control");
		setMultipleSelect(isMultipleSelect);
		addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				if (!isMultipleSelect()) {
					int selected = getSelectedIndex();
					if (selected == -1) {
						setValue(null, true);
					} else {
						setValue(associatedValues.get(selected), true);
					}
				}
			}

		});
	}
	
	@Override
	public void setDecorationsWidth(int aDecorationsWidth) {
		getElement().getStyle().setPaddingRight(aDecorationsWidth, Style.Unit.PX);
	}

	public void addItem(String aLabel, String aKey, T aAssociatedValue, String aClassName) {
		super.addItem(aLabel != null ? aLabel : "", aKey);
		associatedValues.set(getItemCount() - 1, aAssociatedValue);
		setItemStyleName(getItemCount() - 1, aClassName);
	}

	public void addItem(String aLabel, HasDirection.Direction dir, String aKey, T aAssociatedValue, String aClassName) {
		super.addItem(aLabel != null ? aLabel : "", dir, aKey);
		associatedValues.set(getItemCount() - 1, aAssociatedValue);
		setItemStyleName(getItemCount() - 1, aClassName);
	}

	@Override
	public void insertItem(String aLabel, HasDirection.Direction dir, String aKey, int index) {
		boolean wasUnselected = getSelectedIndex() == -1;
		super.insertItem(aLabel != null ? aLabel : "", dir, aKey, index);
		if (wasUnselected)
			super.setSelectedIndex(-1);
		if (index == -1)// crazy gwt ListBox treats -1 as adding to the end
			associatedValues.add(null);
		else
			associatedValues.add(index, null);
		indicies = null;
	}

	@Override
	public void removeItem(int index) {
		super.removeItem(index);
		associatedValues.remove(index);
		indicies = null;
	}

	private void checkIndex(int index) {
		if (index < 0 || index >= getItemCount()) {
			throw new IndexOutOfBoundsException();
		}
	}

	public OptionElement getItem(int aIndex) {
		checkIndex(aIndex);
		return getElement().<SelectElement> cast().getOptions().getItem(aIndex);
	}

	public T getAssociatedValue(int aIndex) {
		checkIndex(aIndex);
		return associatedValues.get(aIndex);
	}

	public void setAssociatedValue(int aIndex, T aValue) {
		checkIndex(aIndex);
		associatedValues.set(aIndex, aValue);
		indicies = null;
	}

	public String getKey(int aIndex) {
		OptionElement option = getItem(aIndex);
		return option.getValue();
	}

	public String getItemStyleName(int aIndex) {
		checkIndex(aIndex);
		OptionElement option = getElement().<SelectElement> cast().getOptions().getItem(aIndex);
		return option.getClassName();
	}

	public void setItemStyleName(int aIndex, String aValue) {
		checkIndex(aIndex);
		OptionElement option = getElement().<SelectElement> cast().getOptions().getItem(aIndex);
		option.setClassName(aValue);
	}

	@Override
	public void clear() {
		super.clear();
		associatedValues.clear();
		indicies = null;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public void setValue(T aValue) {
		setValue(aValue, false);
	}

	public String getText() {
		int idx = indexOf(value);
		if (idx != -1) {
			return getItemText(idx);
		} else {
			return null;
		}
	}

	public String getText(T aValue) {
		validateLocator();
		Integer idx = indicies.get(aValue);
		if (idx != null) {
			return getItemText(idx);
		} else {
			return null;
		}
	}

	public int indexOf(T aValue) {
		validateLocator();
		Integer idx = indicies.get(aValue);
		if (idx != null) {
			return idx;
		} else {
			return -1;
		}
	}

	@Override
	public void setSelectedIndex(int index) {
		super.setSelectedIndex(index);
	}

	@Override
	public void setValue(T aValue, boolean fireEvents) {
		if (value != aValue) {
			value = aValue;
			int index = indexOf(aValue);
			setSelectedIndex(index);
			if (fireEvents) {
				ValueChangeEvent.fire(StyledListBox.this, value);
			}
		}
	}

	private void validateLocator() {
		if (indicies == null) {
			indicies = new HashMap<>();
			for (int i = 0; i < associatedValues.size(); i++) {
				T association = associatedValues.get(i);
				indicies.put(association, i);
			}
		}
	}
}
