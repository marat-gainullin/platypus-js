/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid.filters;

import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.Loader;

/**
 * Applies filters to the rows in a grid. For more information, see
 * {@link AbstractGridFilters}.
 * <p/>
 * 
 * @param <M> the model type
 */
public class GridFilters<M> extends AbstractGridFilters<M> {

  /**
   * Creates grid filters that are applied locally. See
   * {@link AbstractGridFilters#AbstractGridFilters()} for more information.
   */
  public GridFilters() {
    super();
  }

  /**
   * Creates grid filters to be applied remotely. See
   * {@link AbstractGridFilters#AbstractGridFilters(Loader)} for more
   * information.
   * 
   * @param loader the remote loader
   */
  public GridFilters(Loader<FilterPagingLoadConfig, ?> loader) {
    super(loader);
  }

  @Override
  public boolean isLocal() {
    return super.isLocal();
  }

  @Override
  public void setLocal(boolean local) {
    super.setLocal(local);
  }

  @Override
  protected Store<M> getStore() {
    return grid.getStore();
  }

}
