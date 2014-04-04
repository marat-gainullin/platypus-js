/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;

/**
 *
 * @author mg
 */
public class StyledListBox<T> extends ListBox implements HasValue<T> {

    protected List<T> associatedValues = new ArrayList<>();

    public StyledListBox() {
        super();
    }

    public StyledListBox(boolean isMultipleSelect) {
        super(isMultipleSelect);
    }

    public void addItem(String aLabel, String aKey, T aAssociatedValue, String aClassName) {
        super.addItem(aLabel, aKey);
        associatedValues.set(getItemCount() - 1, aAssociatedValue);
        setItemStyleName(getItemCount() - 1, aClassName);
    }

    public void addItem(String aLabel, HasDirection.Direction dir, String aKey, T aAssociatedValue, String aClassName) {
        super.addItem(aLabel, dir, aKey);
        associatedValues.set(getItemCount() - 1, aAssociatedValue);
        setItemStyleName(getItemCount() - 1, aClassName);
    }

    @Override
    public void insertItem(String item, Direction dir, String value, int index) {
        super.insertItem(item, dir, value, index);
        if (index == -1) {
            associatedValues.add(null);
        } else {
            associatedValues.add(index, null);
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= getItemCount()) {
            throw new IndexOutOfBoundsException();
        }
    }

    public OptionElement getItem(int aIndex) {
        checkIndex(aIndex);
        return getElement().<SelectElement>cast().getOptions().getItem(aIndex);
    }

    public T getAssociatedValue(int aIndex) {
        checkIndex(aIndex);
        return associatedValues.get(aIndex);
    }

    public void setAssociatedValue(int aIndex, T aValue) {
        checkIndex(aIndex);
        associatedValues.set(aIndex, aValue);
    }

    public String getKey(int aIndex) {
        OptionElement option = getItem(aIndex);
        return option.getValue();
    }

    public String getItemStyleName(int aIndex) {
        checkIndex(aIndex);
        OptionElement option = getElement().<SelectElement>cast().getOptions().getItem(aIndex);
        return option.getClassName();
    }

    public void setItemStyleName(int aIndex, String aValue) {
        checkIndex(aIndex);
        OptionElement option = getElement().<SelectElement>cast().getOptions().getItem(aIndex);
        option.setClassName(aValue);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public T getValue() {
        int selectedIndex = getSelectedIndex();
        if (selectedIndex == -1) {
            return null;
        } else {
            return getAssociatedValue(selectedIndex);
        }
    }

    @Override
    public void setValue(T value) {
        setValue(value, false);
    }

    @Override
    public void setValue(T value, boolean fireEvents) {
        int index = -1;
        for (int i = 0; i < getItemCount(); i++) {
            T association = getAssociatedValue(i);
            if (association == value) {
                index = i;
            }
        }
        setSelectedIndex(index);
        if (fireEvents) {
            ValueChangeEvent.fire(StyledListBox.this, getValue());
        }
    }
}
