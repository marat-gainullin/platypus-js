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
import com.sencha.gxt.chart.client.draw.sprite.Sprite;

public class LineHighlighter implements SeriesHighlighter {

  @Override
  public void highlight(Sprite sprite) {
    if (((Chart<?>)sprite.getComponent()).isAnimated()) {
      DrawFx.createStrokeWidthAnimator(sprite, 3).run(250);
    } else {
      sprite.setStrokeWidth(3);
      sprite.redraw();
    }
  }

  @Override
  public void unHighlight(Sprite sprite) {
    if (((Chart<?>)sprite.getComponent()).isAnimated()) {
      DrawFx.createStrokeWidthAnimator(sprite, 1).run(250);
    } else {
      sprite.setStrokeWidth(1);
      sprite.redraw();
    }
  }

}
