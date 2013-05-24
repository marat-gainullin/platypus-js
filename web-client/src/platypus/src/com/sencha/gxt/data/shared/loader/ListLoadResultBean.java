/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

import java.util.List;

/**
 * Default implementation of the <code>ListLoadResult</code> interface.
 * 
 * @param <Data> the result data type
 */
public class ListLoadResultBean<Data> implements ListLoadResult<Data> {

  /**
   * The remote data.
   */
  protected List<Data> list;

  /**
   * Creates a new list load result.
   */
  public ListLoadResultBean() {

  }

  /**
   * Creates a new list load result.
   * 
   * @param list the data
   */
  public ListLoadResultBean(List<Data> list) {
    this.list = list;
  }

  @Override
  public List<Data> getData() {
    return list;
  }

  /**
   * Sets the data for the list load result.
   * 
   * @param list the data for this list load result
   */
  public void setData(List<Data> list) {
    this.list = list;
  }

}
