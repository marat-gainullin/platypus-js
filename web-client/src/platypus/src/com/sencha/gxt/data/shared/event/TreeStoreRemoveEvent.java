/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.event;

import java.util.List;

/**
 * Indicates that an element that was visible has been removed from the Store.
 * Note that non-visible elements do not get events, as most display widgets are
 * not concerned with data which has been filtered out.
 * 
 * @param <M> the model type
 */
public class TreeStoreRemoveEvent<M> extends StoreRemoveEvent<M> {

  private M parent;
  private List<M> children;

  /**
   * Creates a new tree store remove event.
   * 
   * @param index the index of the removed item in parent
   * @param item the removed item
   * @param parent the parent of the removed item
   * @param children the children of the removed item
   */
  public TreeStoreRemoveEvent(int index, M item, M parent, List<M> children) {
    super(index, item);
    this.parent = parent;
    this.children = children;
  }

  /**
   * Returns the children of the removed item.
   * 
   * @return the children of the removed item
   */
  public List<M> getChildren() {
    return children;
  }

  /**
   * Returns the parent of the removed item.
   * 
   * @return the parent of the removed item
   */
  public M getParent() {
    return parent;
  }

}