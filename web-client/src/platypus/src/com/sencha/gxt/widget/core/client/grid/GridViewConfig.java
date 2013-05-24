/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * The GridViewConfig is used to return a CSS style name for rows in a Grid. See
 * {@link GridView#setViewConfig(GridViewConfig)}.
 */
public interface GridViewConfig<M> {

  /**
   * Returns one to many CSS style names separated by spaces.
   * 
   * @param model the model for the row
   * @param valueProvider the valueProvider for the col
   * @param rowIndex the row index
   * @param colIndex the row index
   * @return the CSS style name(s) separated by spaces.
   */
  public String getColStyle(M model, ValueProvider<? super M, ?> valueProvider, int rowIndex, int colIndex);

  /**
   * Returns one to many CSS style names separated by spaces.
   * 
   * @param model the model for the row
   * @param rowIndex the row index
   * @return the CSS style name(s) separated by spaces.
   */
  public String getRowStyle(M model, int rowIndex);

}
