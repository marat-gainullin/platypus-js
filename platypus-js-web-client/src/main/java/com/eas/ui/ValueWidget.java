package com.eas.ui;

import com.eas.ui.events.HasValueChangeHandlers;
import com.eas.ui.events.ValueChangeEvent;
import com.eas.ui.events.ValueChangeHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mgainullin
 */
public abstract class ValueWidget extends Widget implements HasValue, HasValueChangeHandlers {

    protected Object value;

    public ValueWidget() {
        super();
    }

    public ValueWidget(Element element) {
        super(element);
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object aValue) {
        Object oldValue = value;
        value = aValue;
        fireValueChange(oldValue);
    }

    protected final Set<ValueChangeHandler> valueChangeHandlers = new Set();

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
        ValueChangeEvent event = new ValueChangeEvent(this, oldValue, getValue());
        for (ValueChangeHandler h : valueChangeHandlers) {
            h.onValueChange(event);
        }
    }
}
