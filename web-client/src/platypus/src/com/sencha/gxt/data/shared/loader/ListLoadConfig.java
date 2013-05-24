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

import com.sencha.gxt.data.shared.SortInfo;

/**
 * Load config interface for list based data. Adds support for sort information.
 */
public interface ListLoadConfig extends Serializable {


  /**
   * Returns the sort info.
   */
  List<? extends SortInfo> getSortInfo();

  /**
   * Sets the sort info.
   */
  void setSortInfo(List<? extends SortInfo> info);
}
