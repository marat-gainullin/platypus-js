/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.series;

import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.DrawFx;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.sprite.RectangleSprite;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;

/**
 * Highlighting effects used by {@link BarSeries}.
 */
public class BarHighlighter implements SeriesHighlighter {

  @Override
  public void highlight(Sprite sprite) {
    if (sprite instanceof RectangleSprite) {
      RectangleSprite bar = (RectangleSprite) sprite;
      bar.setStroke(new RGB(85, 85, 204));
      if (((Chart<?>)sprite.getComponent()).isAnimated()) {
        DrawFx.createStrokeWidthAnimator(bar, 3).run(250);
        DrawFx.createOpacityAnimator(bar, 0.8).run(250);
      } else {
        bar.setStrokeWidth(3);
        bar.setOpacity(0.8);
        bar.redraw();
      }
    }
  }

  @Override
  public void unHighlight(Sprite sprite) {
    if (sprite instanceof RectangleSprite) {
      RectangleSprite bar = (RectangleSprite) sprite;
      bar.setStroke(Color.NONE);
      if (((Chart<?>)sprite.getComponent()).isAnimated()) {
        DrawFx.createStrokeWidthAnimator(bar, 0).run(250);
        DrawFx.createOpacityAnimator(bar, 1).run(250);
      } else {
        bar.setStrokeWidth(0);
        bar.setOpacity(1);
        bar.redraw();
      }
    }
  }

}
