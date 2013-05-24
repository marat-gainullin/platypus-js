/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.cell.core.client.form.TriggerFieldCell;
import com.sencha.gxt.cell.core.client.form.TriggerFieldCell.TriggerFieldAppearance;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent.TriggerClickHandler;

/**
 * An abstract base class for an input field that can be bound to one or more
 * stores to filter values, thus affecting the values displayed in any widgets
 * associated with those stores. Store filter fields generally consist of one
 * line of input with a trigger button for clearing the filter value. Derived
 * classes must override {@link #doSelect} and return true if the item is
 * visible.
 * 
 * @param <T> the field type
 */
public abstract class StoreFilterField<T> extends TriggerField<T> {

  public interface StoreFilterFieldAppearance extends TriggerFieldAppearance {

  }
  static class StoreFilterFieldCell<T> extends TriggerFieldCell<T> {
    public StoreFilterFieldCell() {
      super(GWT.<StoreFilterFieldAppearance> create(StoreFilterFieldAppearance.class));
    }
  }

  protected List<Store<T>> stores = new ArrayList<Store<T>>();

  protected StoreFilter<T> filter;

  /**
   * Creates a store filter field. Use {@link #bind(Store)} to bind the filter
   * to a store.
   */
  public StoreFilterField() {
    super(new StoreFilterFieldCell<T>());
    setAutoValidate(true);
    setValidateOnBlur(false);

    filter = new StoreFilter<T>() {
      @Override
      public boolean select(Store<T> store, T parent, T item) {
        String v = getText();
        return doSelect(store, parent, item, v);
      }
    };

    redraw();

    addTriggerClickHandler(new TriggerClickHandler() {

      @Override
      public void onTriggerClick(TriggerClickEvent event) {
        StoreFilterField.this.onTriggerClick(event);
      }
    });
  }

  /**
   * Adds the specified store to the list of stores filtered by this store
   * filter field.
   * 
   * @param store the store to filter
   */
  public void bind(Store<T> store) {
    stores.add(store);
  };

  /**
   * Removes the specified store from the list of stores filtered by this store
   * filter field.
   * 
   * @param store the store to remove
   */
  public void unbind(Store<T> store) {
    stores.remove(store);
  }

  protected void applyFilters(Store<T> store) {
    if (getText().length() > 0) {
      store.addFilter(filter);
      store.setEnableFilters(true);
    } else {
      store.removeFilter(filter);
    }
  }

  protected abstract boolean doSelect(Store<T> store, T parent, T item, String filter);

  protected void onFilter() {
    for (Store<T> s : stores) {
      applyFilters(s);
    }
    focus();
  }

  protected void onTriggerClick(TriggerClickEvent event) {
    setValue(null);
    // value may not have been updated if no blur so force text change
    // as filters work against current text, not the actual value
    setText("");
    onFilter();
  }

  protected boolean validateValue(T value) {
    boolean ret = super.validateValue(value);
    onFilter();
    return ret;
  }

}
