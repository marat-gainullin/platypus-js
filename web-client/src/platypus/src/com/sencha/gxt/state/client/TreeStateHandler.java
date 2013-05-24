/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.state.client;

import java.util.Set;

import com.sencha.gxt.core.shared.FastSet;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent.StoreDataChangeHandler;
import com.sencha.gxt.state.client.TreeStateHandler.TreeState;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent.CollapseItemHandler;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent.ExpandItemHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;

/**
 * Watches the expanded state of the tree, and stores it under the given key.
 * 
 * @param <M> the data model type
 */
public class TreeStateHandler<M> extends ComponentStateHandler<TreeState, Tree<M, ?>> {
  public interface TreeState {
    Set<String> getExpandedKeys();

    void setExpandedKeys(Set<String> keys);
  }

  class Handler implements CollapseItemHandler<M>, ExpandItemHandler<M>, StoreDataChangeHandler<M> {
    @Override
    public void onCollapse(CollapseItemEvent<M> event) {
      TreeStateHandler.this.onCollapse(event.getItem());
    }

    @Override
    public void onDataChange(StoreDataChangeEvent<M> event) {
      applyState();
    }

    @Override
    public void onExpand(ExpandItemEvent<M> event) {
      TreeStateHandler.this.onExpand(event.getItem());
      applyState();
    }
  }

  public TreeStateHandler(Tree<M, ?> component) {
    super(TreeState.class, component);

    Handler h = new Handler();
    component.addCollapseHandler(h);
    component.addExpandHandler(h);
    component.getStore().addStoreDataChangeHandler(h);
  }

  public TreeStateHandler(Tree<M, ?> component, String key) {
    super(TreeState.class, component, key);

    Handler h = new Handler();
    component.addCollapseHandler(h);
    component.addExpandHandler(h);
    component.getStore().addStoreDataChangeHandler(h);
  }

  @Override
  public void applyState() {
    Store<M> store = getObject().getStore();

    // TODO refactor this part of the method (and its callers) to iterate over
    // the elements just made visible, and only consider expanding them
    for (String key : getSet(getState())) {
      M item = store.findModelWithKey(key);
      if (item != null) {
        getObject().setExpanded(item, true);
      }
    }
  }

  protected Set<String> getSet(TreeState state) {
    if (state.getExpandedKeys() == null) {
      state.setExpandedKeys(new FastSet());
    }
    return state.getExpandedKeys();
  }

  protected void onCollapse(M item) {
    String key = getObject().getStore().getKeyProvider().getKey(item);
    getSet(getState()).remove(key);

    saveState();
  }

  protected void onExpand(M item) {
    String key = getObject().getStore().getKeyProvider().getKey(item);
    getSet(getState()).add(key);

    saveState();
  }

}
