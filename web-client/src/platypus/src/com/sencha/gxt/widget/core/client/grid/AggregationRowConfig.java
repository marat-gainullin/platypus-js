/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the configuration information for an aggregation row.
 * 
 * @param <M> the model type
 */
public class AggregationRowConfig<M> {

  private Map<ColumnConfig<M, ?>, String> cellStyle;
  private Map<ColumnConfig<M, ?>, AggregationRenderer<M>> renderers;

  /**
   * Creates a new aggregation row config.s
   */
  public AggregationRowConfig() {
    renderers = new HashMap<ColumnConfig<M, ?>, AggregationRenderer<M>>();
    cellStyle = new HashMap<ColumnConfig<M, ?>, String>();
  }

  /**
   * Returns the cell style for the given column.
   * 
   * @param id the column id
   * @return the CSS style name
   */
  public String getCellStyle(ColumnConfig<M, ?> id) {
    return cellStyle.get(id);
  }

  /**
   * Returns the aggregation renderer for the given column.
   * 
   * @param config the column
   * @return the aggregation renderer
   */
  public AggregationRenderer<M> getRenderer(ColumnConfig<M, ?> config) {
    return renderers.get(config);
  }

  /**
   * Sets the cell style for the given column.
   * 
   * @param config the column
   * @param style the CSS style name
   */
  public void setCellStyle(ColumnConfig<M, ?> config, String style) {
    cellStyle.put(config, style);
  }

  /**
   * Sets the aggregation renderer for the given column.
   * 
   * @param config the column
   * @param renderer the renderer
   */
  public void setRenderer(ColumnConfig<M, ?> config, AggregationRenderer<M> renderer) {
    renderers.put(config, renderer);
  }

}
