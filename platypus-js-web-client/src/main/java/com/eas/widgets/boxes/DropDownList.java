package com.eas.widgets.boxes;

import com.eas.core.XElement;

import com.eas.ui.HasDecorationsWidth;
import com.eas.ui.HasJsValue;
import com.eas.ui.Widget;
import com.eas.ui.events.ActionEvent;
import com.eas.ui.events.ActionHandler;
import com.eas.ui.events.HasActionHandlers;
import com.eas.ui.events.HasValueChangeHandlers;
import com.eas.ui.events.ValueChangeEvent;
import com.eas.ui.events.ValueChangeHandler;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 */
public abstract class DropDownList extends Widget implements HasJsValue, HasValueChangeHandlers, HasActionHandlers, HasDecorationsWidth {

    protected Object value;

    public DropDownList() {
        super(Document.get().createSelectElement());
        element.setClassName("form-control");
        element.<SelectElement>cast().setMultiple(false);
        element.<XElement>cast().addEventListener(BrowserEvents.CHANGE, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                Object oldValue = value;
                if (oldValue != value) {
                    int selectedIndex = getSelectedIndex();
                    if (selectedIndex == -1) {
                        value = null;
                    } else {
                        value = getAssociatedValue(selectedIndex);
                    }
                    fireValueChange(oldValue);
                    fireActionPerformed();
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

    public void addItem(String aLabel, String aKey, Object aAssociatedValue, String aClassName) {
        addItem(getCount(), aLabel, aKey, aAssociatedValue, aClassName);
    }

    public void addItem(int index, String aLabel, String aKey, Object aAssociatedValue, String aClassName) {
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

    public Object getAssociatedValue(int index) {
        OptionElement item = getItem(index);
        return item != null ? item.getPropertyObject("js-value") : null;
    }

    public void setAssociatedValue(int index, Object aValue) {
        OptionElement item = getItem(index);
        if (item != null) {
            item.setPropertyObject("js-value", aValue);
        }
    }

    public int indexOf(Object aValue) {
        for (int i = 0; i < getCount(); i++) {
            if (getAssociatedValue(i) == aValue) {
                return i;
            }
        }
        return -1;
    }

    public String getKey(int aIndex) {
        OptionElement option = getItem(aIndex);
        return option.getValue();
    }

    public String getItemStyleName(int aIndex) {
        OptionElement item = getItem(aIndex);
        return item != null ? item.getClassName() : null;
    }

    public void setItemStyleName(int aIndex, String aValue) {
        OptionElement item = getItem(aIndex);
        if (item != null) {
            item.setClassName(aValue);
        }
    }

    public void clear() {
        for (int i = getCount(); i >= 0; i--) {
            removeItem(i);
        }
    }

    public int getSelectedIndex() {
        return element.<SelectElement>cast().getSelectedIndex();
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < getCount()) {
            element.<SelectElement>cast().setSelectedIndex(index);
        } else {
            element.<SelectElement>cast().setSelectedIndex(-1);
        }
    }

    protected final Set<ValueChangeHandler> valueChangeHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler handler) {
        valueChangeHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                valueChangeHandlers.remove(handler);
            }

        };
    }

    protected void fireValueChange(Object oldValue) {
        ValueChangeEvent event = new ValueChangeEvent(this, oldValue, value);
        for (ValueChangeHandler h : valueChangeHandlers) {
            h.onValueChange(event);
        }
    }

    protected Set<ActionHandler> actionHandlers = new HashSet<>();

    @Override
    public HandlerRegistration addActionHandler(ActionHandler handler) {
        actionHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                actionHandlers.remove(handler);
            }

        };
    }

    protected void fireActionPerformed() {
        ActionEvent event = new ActionEvent(this);
        for (ActionHandler h : actionHandlers) {
            h.onAction(event);
        }
    }

    @Override
    public Object getJsValue() {
        return value;
    }

    @Override
    public void setJsValue(Object aValue) {
        if (value != aValue) {
            Object oldValue = value;
            value = aValue;
            int index = indexOf(aValue);
            setSelectedIndex(index);
            fireValueChange(oldValue);
        }
    }

}
