/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.series;

import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.data.shared.ListStore;

/**
 * Interface used to create a custom renderer for a series.
 * 
 * @param <M> the data type used by the renderer
 */
public interface SeriesRenderer<M> {

  /**
   * Customizes the given sprite using the given index and store.
   * 
   * @param sprite the sprite to be customized
   * @param index the store index
   * @param store the data store
   */
  public void spriteRenderer(Sprite sprite, int index, ListStore<M> store);

}
