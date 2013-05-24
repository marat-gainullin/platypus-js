/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

/**
 * Default implementation of the <code>PagingLoadConfig</code> interface.
 */
public class PagingLoadConfigBean extends ListLoadConfigBean implements PagingLoadConfig {

  private int limit;
  private int offset;

  /**
   * Creates a new paging load config.
   */
  public PagingLoadConfigBean() {
    this(0, 50);
  }

  /**
   * Creates a new paging load config.
   * 
   * @param offset the offset
   * @param limit the limit
   */
  public PagingLoadConfigBean(int offset, int limit) {
    setOffset(offset);
    setLimit(limit);
  }

  public int getLimit() {
    return limit;
  }

  public int getOffset() {
    return offset;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

}
