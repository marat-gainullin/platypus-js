package com.eas.widgets.boxes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eas.ui.HasDecorationsWidth;
import com.eas.ui.HasJsValue;
import com.eas.ui.Widget;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * @author mg
 * @param <T>
 */
public class StyledListBox<T> extends Widget implements HasJsValue, HasDecorationsWidth {

    protected Map<T, Integer> indicies;
    protected T value;

    public StyledListBox() {
        this(false);
    }

    public StyledListBox(boolean multipleSelect) {
        super(Document.get().createSelectElement());
        element.setClassName("form-control");
        element.<SelectElement>cast().setMultiple(multipleSelect);
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
        element.getStyle().setPaddingRight(aDecorationsWidth, Style.Unit.PX);
    }

    public int getCount() {
        return element.<SelectElement>cast().getOptions().getLength();
    }

    public void addItem(String aLabel, String aKey, T aAssociatedValue, String aClassName) {
        addItem(getCount(), aLabel, aKey, aAssociatedValue, aClassName);
    }

    public void addItem(int index, String aLabel, String aKey, T aAssociatedValue, String aClassName) {
        if (index >= 0 && index <= getCount()) {
            OptionElement item = Document.get().createOptionElement();
            item.setClassName(aClassName);
            item.setInnerText(aLabel);
            item.setValue(aKey);
            item.setPropertyObject("js-value", aAssociatedValue);
            boolean wasUnselected = element.<SelectElement>cast().getSelectedIndex() == -1;
            if (index == getCount()) {
                element.appendChild(item);
            } else {
                element.insertBefore(getItem(index), item);
            }
            if (wasUnselected) {
                element.<SelectElement>cast().setSelectedIndex(-1);
            }
            indicies = null;
        }
    }

    public void removeItem(int index) {
        if (index >= 0 && index < getCount()) {
            OptionElement item = element.<SelectElement>cast().getOptions().getItem(index);
            item.removeFromParent();
        }
    }

    public OptionElement getItem(int index) {
        if (index >= 0 && index < getCount()) {
            return element.<SelectElement>cast().getOptions().getItem(index);
        } else {
            return null;
        }
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
        OptionElement option = getElement().<SelectElement>cast().getOptions().getItem(aIndex);
        return option.getClassName();
    }

    public void setItemStyleName(int aIndex, String aValue) {
        checkIndex(aIndex);
        OptionElement option = getElement().<SelectElement>cast().getOptions().getItem(aIndex);
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
