/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.series;

import com.sencha.gxt.chart.client.draw.sprite.Sprite;

/**
 * Interface for {@link Series} highlighting effects.
 */
public interface SeriesHighlighter {

  /**
   * Highlights the given sprite.
   * 
   * @param sprite the sprite to be hightlighted
   */
  public void highlight(Sprite sprite);

  /**
   * Remove highlighting from the given sprite.
   * 
   * @param sprite the sprite to have highlighting removed
   */
  public void unHighlight(Sprite sprite);

}
