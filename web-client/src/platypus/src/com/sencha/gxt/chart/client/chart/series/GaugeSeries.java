/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.series;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Event;
import com.sencha.gxt.chart.client.chart.axis.GaugeAxis;
import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.path.EllipticalArc;
import com.sencha.gxt.chart.client.draw.path.LineTo;
import com.sencha.gxt.chart.client.draw.path.MoveTo;
import com.sencha.gxt.chart.client.draw.path.PathCommand;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.PrecisePoint;
import com.sencha.gxt.core.client.util.PreciseRectangle;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.fx.client.animation.Animator;

/**
 * Creates a Gauge Chart. Gauge Charts are used to show progress in a certain
 * variable.
 * 
 * Here is an example gauge configuration:
 * 
 * <pre>
    GaugeSeries<Data> gauge = new GaugeSeries<Data>();
    gauge.setAngleField(provider);
    gauge.addColor(RGB.GREEN);
    gauge.addColor(new RGB("#ddd"));
    chart.addSeries(gauge);
 * </pre>
 * 
 * First the series is created with its associated data type. Next the value
 * provider field is set, which provides the value of the gauge. Two colors are
 * added the first representing the slider color and the second representing the
 * background. Finally the series is added to the chart where it will be
 * displayed.
 * 
 * @param <M> the data type used by this series
 */
public class GaugeSeries<M> extends AbstractPieSeries<M> {

  private boolean needle = false;
  private PathSprite needleSprite;
  private Slice needleSlice;
  private double value = Double.NaN;

  /**
   * Creates a gauge {@link Series}.
   */
  public GaugeSeries() {
  }

  @Override
  public void drawSeries() {

    ListStore<M> store = chart.getCurrentStore();

    if (store == null || store.size() == 0) {
      this.clear();
      return;
    }

    PreciseRectangle chartBBox = chart.getBBox();
    @SuppressWarnings("unchecked")
    GaugeAxis<M> axis = (GaugeAxis<M>) chart.getAxis(null);
    ArrayList<Slice> oldSlices = slices;
    slices = new ArrayList<Slice>();
    double minimum = axis.getMinimum();
    double maximum = axis.getMaximum();

    center.setX(chartBBox.getX() + (chartBBox.getWidth() / 2.0));
    center.setY(chartBBox.getY() + chartBBox.getHeight());
    radius = Math.min(center.getX() - chartBBox.getX(), center.getY() - chartBBox.getY());

    M record = store.get(0);
    value = angleField.getValue(record).doubleValue();

    double splitAngle = -180.0 * (1.0 - (value - minimum) / (maximum - minimum));
    if (needle) {
      Slice sliceA = new Slice(value, -179.99999, 0.00001, radius);
      slices.add(sliceA);
    } else {
      Slice sliceB = new Slice(value, -179.99999, splitAngle, radius);
      slices.add(sliceB);
      Slice sliceC = new Slice(maximum - value, splitAngle, 0.00001, radius);
      slices.add(sliceC);
    }

    // do pie slices after.
    for (int i = 0; i < slices.size(); i++) {
      Slice slice = slices.get(i);
      final PathSprite sprite;

      if (i < sprites.size()) {
        sprite = (PathSprite) sprites.get(i);
      } else {
        sprite = new PathSprite();
        chart.addSprite(sprite);
        sprites.add(sprite);
      }

      // set pie slice properties
      sprite.setHidden(false);
      if (i == 0) {
        sprite.setFill(colors.get(0));
      } else {
        sprite.setFill(colors.get(1));
      }
      slice.setMargin(margin);
      slice.setStartRho(slice.getRho() * donut / 100.0);
      slice.setEndRho(slice.getRho());
      if (stroke != null) {
        sprite.setStroke(stroke);
      }
      if (!Double.isNaN(strokeWidth)) {
        sprite.setStrokeWidth(strokeWidth);
      }
      if (chart.isAnimated() && oldSlices.size() == slices.size()) {
        createSegmentAnimator(sprite, oldSlices.get(i), slice).run(chart.getAnimationDuration(),
            chart.getAnimationEasing());
      } else {
        List<PathCommand> commands = calculateSegment(slice);
        sprite.setCommands(commands);
        sprite.redraw();
      }
      if (renderer != null) {
        renderer.spriteRenderer(sprite, i, chart.getCurrentStore());
      }
    }

    if (needle) {
      splitAngle = splitAngle * Math.PI / 180;

      if (needleSprite == null) {
        needleSprite = new PathSprite();
        needleSprite.setStrokeWidth(4);
        needleSprite.setStroke(new Color("#222"));
        chart.addSprite(needleSprite);
      }

      if (chart.isAnimated() && needleSlice != null) {
        Slice old = needleSlice;
        needleSlice = new Slice(value, -180, splitAngle * 180 / Math.PI, radius);
        needleSlice.setMargin(margin);
        needleSlice.setStartRho(needleSlice.getRho() * donut / 100.0);
        needleSlice.setEndRho(needleSlice.getRho());
        createNeedleAnimator(needleSprite, old, needleSlice).run(chart.getAnimationDuration(),
            chart.getAnimationEasing());
      } else {
        // store the slice representing the needle
        needleSlice = new Slice(value, -180, splitAngle * 180 / Math.PI, radius);
        needleSlice.setMargin(margin);
        needleSlice.setStartRho(needleSlice.getRho() * donut / 100.0);
        needleSlice.setEndRho(needleSlice.getRho());
        ArrayList<PathCommand> needleCommands = new ArrayList<PathCommand>();
        needleCommands.add(new MoveTo(center.getX() + (radius * donut / 100.0) * Math.cos(splitAngle), center.getY()
            + -Math.abs((radius * donut / 100.0)) * Math.sin(splitAngle)));
        needleCommands.add(new LineTo(center.getX() + radius * Math.cos(splitAngle), center.getY()
            + -Math.abs(radius * Math.sin(splitAngle))));
        needleSprite.setCommands(needleCommands);
        needleSprite.redraw();
      }
    }
  }

  @Override
  public void hide(int yFieldIndex) {
  }

  @Override
  public void highlight(int yFieldIndex) {
    if (highlighter != null) {
      highlighter.highlight(sprites.get(0));
    }
  }

  @Override
  public void highlightAll(int index) {
  }

  /**
   * Returns whether or not the series uses a needle in place of a wedge.
   * 
   * @return true if needle
   */
  public boolean isNeedle() {
    return needle;
  }

  @Override
  public int onMouseMove(PrecisePoint point, Event event) {
    return -1;
  }

  @Override
  public void onMouseOut(PrecisePoint point, Event event) {
  }

  /**
   * Sets whether or not the series uses a needle in place of a wedge.
   * 
   * @param needle true if needle
   */
  public void setNeedle(boolean needle) {
    this.needle = needle;
    if (!needle) {
      needleSlice = null;
      if (needleSprite != null) {
        chart.remove(needleSprite);
        needleSprite = null;
      }
    } else if (sprites.size() > 1) {
      chart.remove(sprites.remove(1));
    }
  }

  @Override
  public void show(int yFieldIndex) {
  }

  @Override
  public void unHighlight(int yFieldIndex) {
    if (highlighter != null) {
      highlighter.unHighlight(sprites.get(0));
    }
  }

  @Override
  public void unHighlightAll(int index) {
  }

  @Override
  public boolean visibleInLegend(int index) {
    return false;
  }

  @Override
  protected int getIndex(PrecisePoint point) {
    return 0;
  }

  @Override
  protected ValueProvider<M, ? extends Number> getValueProvider(int index) {
    return null;
  }

  /**
   * Creates an animator that animates for the starting {@link Slice} to the
   * ending slice on the given sprite.
   * 
   * @param sprite the sprite to be animated
   * @param start the starting slice
   * @param end the ending slice
   * @return the animation to be run
   */
  private Animator createNeedleAnimator(final PathSprite sprite, final Slice start, final Slice end) {
    // find the delta
    final Slice delta = new Slice(0, start.getStartAngle() - end.getStartAngle(), start.getEndAngle()
        - end.getEndAngle(), start.getRho() - end.getRho());
    delta.setMargin(start.getMargin() - end.getMargin());
    delta.setStartRho(start.getStartRho() - end.getStartRho());
    delta.setEndRho(start.getEndRho() - end.getEndRho());
    final Slice origin = new Slice(start);
    return new Animator() {
      @Override
      protected void onUpdate(double progress) {
        origin.setStartAngle(start.getStartAngle() - (delta.getStartAngle() * progress));
        origin.setEndAngle(start.getEndAngle() - (delta.getEndAngle() * progress));
        origin.setRho(start.getRho() - (delta.getRho() * progress));
        origin.setMargin(start.getMargin() - (delta.getMargin() * progress));
        origin.setStartRho(start.getStartRho() - (delta.getStartRho() * progress));
        origin.setEndRho(start.getEndRho() - (delta.getEndRho() * progress));
        List<PathCommand> commands = calculateSegment(origin);
        if (commands.get(0) instanceof MoveTo) {
          sprite.setCommand(0, commands.get(0));
        }
        if (commands.get(2) instanceof EllipticalArc) {
          EllipticalArc arc = (EllipticalArc) commands.get(2);
          sprite.setCommand(1, new LineTo(arc.getX(), arc.getY()));
        }
        sprite.redraw();
      }
    };
  }
}
