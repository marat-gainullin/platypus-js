/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.menu;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HasValue;

/**
 *
 * @author mg
 */
public class MenuItemCheckBox extends MenuItemImageText implements HasValue<Boolean>, HasValueChangeHandlers<Boolean> {

    protected Boolean value;
    protected InputElement inputElem;

    public MenuItemCheckBox(Boolean aValue, String aText, boolean asHtml) {
        super(aText, asHtml, null, null);
        setValue(aValue);
        setScheduledCommand(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                setValue(!getValue(), true);
            }

        });
    }

    @Override
    protected void regenerate() {
        getElement().setInnerSafeHtml(generateHtml());
        leftMark = getElement().getFirstChildElement().getFirstChildElement();
        inputElem = (InputElement) leftMark.getFirstChildElement();
        field = (Element) getElement().getFirstChildElement().getLastChild();
    }

    protected SafeHtml generateHtml() {
    	String ltext = text != null ? text : "";
        return MenuItemTemplates.INSTANCE.checkBox(html ? SafeHtmlUtils.fromTrustedString(ltext) : SafeHtmlUtils.fromString(ltext));
    }

    @Override
    public Boolean getValue() {
        return value;
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
        value = aValue;
        inputElem.setChecked(aValue);
        inputElem.setDefaultChecked(aValue);
        if (fireEvents && !aValue.equals(oldValue)) {
            ValueChangeEvent.fire(this, aValue);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

}
