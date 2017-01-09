/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.widgets.boxes;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasValue;

/**
 *
 * @author mg
 */
public class ImageToggleButton extends ImageButton implements HasValue<Boolean>, HasValueChangeHandlers<Boolean> {

    protected HandlerManager handlerManager = new HandlerManager(this);
    protected Boolean selected;

    public ImageToggleButton(String aTitle, boolean asHtml) {
        this(aTitle, asHtml, null);
    }

    public ImageToggleButton(String aTitle, boolean asHtml, ImageResource aImage) {
        super(aTitle, asHtml, aImage);
        addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                boolean val = getValue() != null ? getValue() : false;
                setValue(!val, true);
            }
        });
    }

    @Override
    public Boolean getValue() {
        return selected;
    }

    @Override
    public void setValue(Boolean value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Boolean aValue, boolean fireEvents) {
        if (aValue == null) {
            aValue = Boolean.FALSE;
        }
        Boolean oldValue = getValue();
        selected = aValue;
        setStyleDependentName("active", selected);
        setStyleDependentName("default", !selected);
        if (fireEvents && !aValue.equals(oldValue)) {
            ValueChangeEvent.fire(this, aValue);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return handlerManager.addHandler(ValueChangeEvent.getType(), handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (event instanceof ValueChangeEvent) {
            handlerManager.fireEvent(event);
        } else {
            super.fireEvent(event);
        }
    }

}
