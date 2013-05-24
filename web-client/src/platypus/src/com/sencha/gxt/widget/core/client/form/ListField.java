/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ListView;

/**
 * Adapts a {@link ListView} for use as a field.
 * 
 * @param <M> the model type
 * @param <T> the field's data type
 */
public class ListField<M, T> extends AdapterField<M> {

  protected ListView<M, T> listView;

  /**
   * Creates a list field that wraps a {@link ListView} for use as a field.
   * 
   * @param view the list view to wrap
   */
  public ListField(ListView<M, T> view) {
    super(view);
    this.listView = view;
  }

  /**
   * Returns the store used by the list view.
   * 
   * @return the store used by the list view
   */
  public ListStore<M> getStore() {
    return listView.getStore();
  }

  @Override
  public M getValue() {
    return listView.getSelectionModel().getSelectedItem();
  }

  /**
   * Sets the list field's list store.
   * 
   * @param store the store
   */
  public void setStore(ListStore<M> store) {
    listView.setStore(store);
  }

  @Override
  public void setValue(M value) {
    listView.getSelectionModel().select(value, false);
  }

}
