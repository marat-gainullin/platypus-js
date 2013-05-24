/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * Renderer for aggregation rows in a grid.
 * 
 * @param <M> the model type
 */
public interface AggregationRenderer<M> {

  /**
   * Returns the rendered HTML or Widget for the given cell.
   * 
   * @param colIndex the column index
   * @param grid the containing grid
   */
  public SafeHtml render(int colIndex, Grid<M> grid);

}
