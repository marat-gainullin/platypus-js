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
 * A {@link ListLoadResultBean} that adds support for paging properties as
 * described by {@link PagingLoadResult}.
 * 
 * @param <Data> the type of data for this list load result
 */
public class PagingLoadResultBean<Data> extends ListLoadResultBean<Data> implements PagingLoadResult<Data> {

  private int totalLength;
  private int offset;

  /**
   * Creates an empty paging load result bean.
   */
  public PagingLoadResultBean() {

  }

  /**
   * Creates a new paging list load result.
   * 
   * @param list the data
   * @param totalLength the total length
   * @param offset the paging offset
   */
  public PagingLoadResultBean(List<Data> list, int totalLength, int offset) {
    super(list);
    this.totalLength = totalLength;
    this.offset = offset;
  }

  @Override
  public int getOffset() {
    return offset;
  }

  @Override
  public int getTotalLength() {
    return totalLength;
  }

  @Override
  public void setOffset(int offset) {
    this.offset = offset;
  }

  @Override
  public void setTotalLength(int totalLength) {
    this.totalLength = totalLength;
  }

}
