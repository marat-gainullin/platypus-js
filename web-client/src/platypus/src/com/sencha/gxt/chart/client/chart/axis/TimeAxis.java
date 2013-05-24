/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.axis;

import java.util.Date;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;

/**
 * A type of axis whose units are measured in time values. Use this axis for
 * listing dates that you will want to group or dynamically change. If you just
 * want to display dates as categories then use the {@link CategoryAxis} for
 * axis instead.
 * 
 * @param <M> the data type of the axis
 */
public class TimeAxis<M> extends CategoryAxis<M, Date> {

  private Date startDate;
  private Date endDate;
  private ListStore<M> substore;
  private StoreSortInfo<M> sort;
  private StoreFilter<M> filter;

  /**
   * Creates a time axis.
   */
  public TimeAxis() {
  }

  /**
   * Returns the ending date of the axis.
   * 
   * @return the ending date of the axis
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * Returns the starting date of the axis.
   * 
   * @return the starting date of the axis
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * Sets the ending date of the axis.
   * 
   * @param endDate the ending date of the axis
   */
  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  /**
   * Sets the starting date of the axis.
   * 
   * @param startDate the starting date of the axis
   */
  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  @Override
  protected void applyData() {
    if (sort == null) {
      sort = new StoreSortInfo<M>(field, SortDir.ASC);
      filter = new StoreFilter<M>() {
        @Override
        public boolean select(Store<M> store, M parent, M item) {
          Date value = field.getValue(item);
          boolean result = value.after(startDate) && value.before(endDate) || value.equals(startDate)
              || value.equals(endDate);
          return result;
        }
      };
    }
    ListStore<M> store = chart.getStore();
    substore = new ListStore<M>(store.getKeyProvider());
    substore.addSortInfo(sort);
    substore.addFilter(filter);
    substore.setEnableFilters(true);
    substore.addAll(store.getAll());
    chart.setSubstore(substore);
    super.applyData();
  }

  @Override
  protected void createLabels() {
    labelNames.clear();
    for (int i = 0; i < substore.size(); i++) {
      labelNames.add(field.getValue(substore.get(i)));
    }
  }
}
