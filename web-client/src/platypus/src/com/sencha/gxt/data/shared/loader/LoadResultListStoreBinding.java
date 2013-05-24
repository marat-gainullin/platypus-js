/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

import com.sencha.gxt.data.shared.ListStore;

/**
 * Event handler for the {@link LoadEvent} fired when a {@link Loader} has
 * finished loading data. This handler takes a {@link ListStore} and uses the
 * contents of the {@link ListLoadResult} provided by the Loader to re-populate
 * the store.
 * 
 * @param <C> load config object type
 * @param <M> model objects that populate the store
 * @param <D> load result passed from the loader
 */
public class LoadResultListStoreBinding<C, M, D extends ListLoadResult<M>> implements LoadHandler<C, D> {
  private final ListStore<M> store;

  /**
   * Creates a load event handler that re-populates the given list store using
   * the list load result provided by the loader via the event.
   * 
   * @param store the list store
   */
  public LoadResultListStoreBinding(ListStore<M> store) {
    this.store = store;
  }

  @Override
  public void onLoad(LoadEvent<C, D> event) {
    ListLoadResult<M> loaded = event.getLoadResult();

    store.replaceAll(loaded.getData());
  }
}
