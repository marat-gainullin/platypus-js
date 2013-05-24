/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.series;

import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.draw.DrawFx;
import com.sencha.gxt.chart.client.draw.Scaling;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;

/**
 * Highlighting effects used by {@link ScatterSeries}.
 */
public class ScatterHighlighter implements SeriesHighlighter {

  @Override
  public void highlight(Sprite sprite) {
    if (((Chart<?>)sprite.getComponent()).isAnimated()) {
      DrawFx.createScalingAnimator(sprite, new Scaling(1.2, 1.2, 0, 0)).run(250);
    } else {
      sprite.setScaling(new Scaling(1.2, 1.2, 0, 0));
      sprite.redraw();
    }
  }

  @Override
  public void unHighlight(Sprite sprite) {
    if (((Chart<?>)sprite.getComponent()).isAnimated()) {
      DrawFx.createScalingAnimator(sprite, new Scaling(1, 1, 0, 0)).run(250);
    } else {
      sprite.setScaling(new Scaling(1, 1, 0, 0));
      sprite.redraw();
    }
  }

}
