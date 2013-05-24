/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.SortInfoBean;

/**
 * Default <code>ListLoadConfig</code> implementation.
 * 
 * @see ListLoadConfig
 */
@SuppressWarnings("serial")
public class ListLoadConfigBean implements ListLoadConfig {

  private List<SortInfoBean> sortInfo = new ArrayList<SortInfoBean>();

  /**
   * Create a new load config instance.
   */
  public ListLoadConfigBean() {

  }

  /**
   * Create a new load config instance.
   */
  public ListLoadConfigBean(SortInfoBean info) {
    getSortInfo().add(info);
  }

  /**
   * Creates a new load config instance.
   * 
   * @param info the sort information
   */
  public ListLoadConfigBean(List<SortInfo> info) {
    setSortInfo(info);
  }

  @Override
  public List<SortInfoBean> getSortInfo() {
    return sortInfo;
  }

  @Override
  public void setSortInfo(List<? extends SortInfo> info) {
    sortInfo.clear();
    for (SortInfo i : info) {
      if (i instanceof SortInfoBean) {
        sortInfo.add((SortInfoBean) i);
      } else {
        sortInfo.add(new SortInfoBean(i.getSortField(), i.getSortDir()));
      }
    }
  }


}
