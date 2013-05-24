/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.util;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

public class ToggleGroup extends AbstractSet<HasValue<Boolean>> implements HasValue<HasValue<Boolean>> {
  protected ValueChangeHandler<Boolean> handler = new ValueChangeHandler<Boolean>() {

    @SuppressWarnings("unchecked")
    @Override
    public void onValueChange(ValueChangeEvent<Boolean> event) {
      if (event.getValue() && getValue() != event.getSource()) {
        setValue((HasValue<Boolean>) event.getSource(), true);
      }

    }
  };
  
  protected Map<HasValue<Boolean>, HandlerRegistration> toggles = new HashMap<HasValue<Boolean>, HandlerRegistration>();
  protected HasValue<Boolean> value;

  private HandlerManager handlerManager;

  @Override
  public boolean add(HasValue<Boolean> toggle) {
    if (toggles.containsKey(toggle)) {
      return false;
    } else {
      return toggles.put(toggle, toggle.addValueChangeHandler(handler)) == null;
    }
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<HasValue<Boolean>> handler) {
    return ensureHandlers().addHandler(ValueChangeEvent.getType(), handler);
  }

  @Override
  public void fireEvent(GwtEvent<?> event) {
    if (handlerManager != null) {
      handlerManager.fireEvent(event);
    }
  }

  @Override
  public HasValue<Boolean> getValue() {
    return value;
  }

  @Override
  public Iterator<HasValue<Boolean>> iterator() {
    return toggles.keySet().iterator();
  }

  @Override
  public boolean remove(Object toggle) {
    HandlerRegistration reg = toggles.remove(toggle);
    if (reg != null) {
      reg.removeHandler();
      return true;
    }
    return false;

  }

  @Override
  public void setValue(HasValue<Boolean> value) {
    setValue(value, false);
  }

  @Override
  public void setValue(HasValue<Boolean> value, boolean fireEvents) {
    if (!contains(value)) {
      value = null;
    }
    HasValue<Boolean> oldValue = getValue();
    this.value = value;
    if (value != null && !value.getValue()) {
      value.setValue(true, fireEvents);
    }

    for (HasValue<Boolean> toggle : ToggleGroup.this) {
      if (toggle != value) {
        toggle.setValue(false, fireEvents);
      }
    }
    if (fireEvents) {
      ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
    }
  }

  @Override
  public int size() {
    return toggles.size();
  }

  protected HandlerManager ensureHandlers() {
    return handlerManager == null ? handlerManager = new HandlerManager(this) : handlerManager;
  }

}
