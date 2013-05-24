/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

import java.io.Serializable;
import java.util.List;

/**
 * Load result interface for list based load results.
 * 
 * @param <D> the data type being returned from the server
 */
public interface ListLoadResult<D> extends Serializable {

  /**
   * Returns the remote data.
   * 
   * @return the data
   */
  public List<D> getData();

}
