/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

/**
 * Aggregates sort field and sort direction.
 */
@ProxyFor(SortInfoBean.class)
public interface SortInfo extends ValueProxy {

  /**
   * Returns the sort direction.
   * 
   * @return the sort direction
   */
  SortDir getSortDir();

  /**
   * Returns the sort field.
   * 
   * @return the sort field
   */
  String getSortField();

  /**
   * Sets the sort direction.
   * 
   * @param sortDir the sort direction
   */
  void setSortDir(SortDir sortDir);

  /**
   * Sets the sort field.
   * 
   * @param sortField the sort field
   */
  void setSortField(String sortField);
}
