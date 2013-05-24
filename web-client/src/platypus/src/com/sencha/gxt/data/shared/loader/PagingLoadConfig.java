/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;


/**
 * A <code>ListLoadConfig</code> with support for limit and offset values.
 */
public interface PagingLoadConfig extends ListLoadConfig {

  /**
   * Sets the limit.
   * 
   * @param limit the limit
   */
  public void setLimit(int limit);

  /**
   * Sets the offset.
   * 
   * @param offset the offset
   */
  public void setOffset(int offset);

  /**
   * The number of records being requested.
   */
  public int getLimit();

  /**
   * The offset for the first record to retrieve.
   */
  public int getOffset();
}
