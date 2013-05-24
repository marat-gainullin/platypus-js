/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

import java.util.List;

import com.sencha.gxt.data.shared.ListStore;

/**
 * Event handler for the {@link LoadEvent} fired when a {@link Loader} has
 * finished loading data. This handler takes a {@link ListStore} and uses the
 * List provided by the Loader to re-populate the store.
 * 
 * @param <C> load config object type
 * @param <M> model objects that populate the store
 * @param <D> collection passed from the loader
 */
public class ListStoreBinding<C, M, D extends List<M>> implements LoadHandler<C, D> {
  private ListStore<M> listStore;

  /**
   * Creates a {@link LoadEvent} handler for the given {@link ListStore}. It
   * expects the load result to be a list, which it uses to re-populate the
   * store.
   * 
   * @param listStore the list store
   */
  public ListStoreBinding(ListStore<M> listStore) {
    this.listStore = listStore;
  }

  @Override
  public void onLoad(LoadEvent<C, D> event) {
    List<M> data = event.getLoadResult();

    listStore.replaceAll(data);
  }

}
