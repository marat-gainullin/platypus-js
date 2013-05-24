/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;


/**
 * A <code>LoadResult</code> for paging loaders.
 */
public interface PagingLoadResult<Data> extends ListLoadResult<Data> {

  /**
   * Returns the current offset of the results.
   * 
   * @return the offset
   */
  public int getOffset();

  /**
   * Returns the total count. This value will not equal the number of records
   * being returned when paging is used.
   * 
   * @return the total count
   */
  public int getTotalLength();

  /**
   * Sets the offset.
   * 
   * @param offset the offset
   */
  public void setOffset(int offset);

  /**
   * Sets the total length.
   * 
   * @param totalLength the total length
   */
  public void setTotalLength(int totalLength);
}
