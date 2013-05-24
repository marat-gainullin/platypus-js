/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

import java.util.List;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;

/**
 * Event handler for the {@link LoadEvent} fired when a {@link Loader} has
 * finished loading data. It expects {@link LoadEvent#getLoadConfig} to return a
 * parent and {@link LoadEvent#getLoadResult} to return a list of new children.
 * It replaces the children of the given parent with the list of new children.
 * The children are expected to be of type {@code <M>}. For children of type
 * {@link TreeNode} see {@link SubTreeStoreBinding}.
 * 
 * @param <M> the type of objects that populate the store
 */
public class ChildTreeStoreBinding<M> implements LoadHandler<M, List<M>> {
  private final TreeStore<M> store;

  /**
   * Creates a {@link LoadEvent} handler for the given {@link TreeStore}.
   * 
   * @param store the store whose events will be handled
   */
  public ChildTreeStoreBinding(TreeStore<M> store) {
    this.store = store;
  }

  @Override
  public void onLoad(LoadEvent<M, List<M>> event) {
    M parent = event.getLoadConfig();

    store.replaceChildren(parent, event.getLoadResult());
  }
}
