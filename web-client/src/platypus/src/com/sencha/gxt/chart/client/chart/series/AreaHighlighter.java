/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.series;

import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;

/**
 * Highlighting effects used by {@link AreaSeries}.
 */
public class AreaHighlighter implements SeriesHighlighter {

  @Override
  public void highlight(Sprite sprite) {
    Color fill = sprite.getFill();
    if (fill instanceof RGB) {
      RGB rgb = (RGB) fill;
      sprite.setFill(rgb.getLighter(0.2));
      sprite.redraw();
    }
  }

  @Override
  public void unHighlight(Sprite sprite) {
    Color fill = sprite.getFill();
    if (fill instanceof RGB) {
      RGB rgb = (RGB) fill;
      sprite.setFill(rgb.getDarker(0.2));
      sprite.redraw();
    }
  }

}
