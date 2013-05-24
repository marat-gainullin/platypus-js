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

import com.sencha.gxt.chart.client.chart.Legend;
import com.sencha.gxt.chart.client.draw.path.ClosePath;
import com.sencha.gxt.chart.client.draw.path.EllipticalArc;
import com.sencha.gxt.chart.client.draw.path.LineTo;
import com.sencha.gxt.chart.client.draw.path.MoveTo;
import com.sencha.gxt.chart.client.draw.path.PathCommand;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.PrecisePoint;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.fx.client.animation.Animator;

/**
 * Abstract class representing the core pie series functionality.
 * 
 * @param <M> data type used by this series
 */
public abstract class AbstractPieSeries<M> extends MultipleColorSeries<M> {

  protected PrecisePoint center = new PrecisePoint();
  protected double donut = 0;
  protected double radius = 0;
  protected double margin = 0;
  protected ValueProvider<? super M, ? extends Number> angleField = null;
  protected ArrayList<Slice> slices = new ArrayList<Slice>();
  private LabelProvider<? super M> legendLabelProvider = new StringLabelProvider<M>();

  /**
   * Returns the value provider of the pie data.
   * 
   * @return the value provider of the pie data
   */
  public ValueProvider<? super M, ? extends Number> getAngleField() {
    return angleField;
  }

  /**
   * Returns the degree of the pie's donut.
   * 
   * @return the degree of the pie's donut
   */
  public double getDonut() {
    return donut;
  }

  /**
   * Sets the {@link LabelProvider} used for the legend.
   * 
   * @return the label provider used for the legend
   */
  public LabelProvider<? super M> getLegendLabelProvider() {
    return legendLabelProvider;
  }

  @Override
  public ArrayList<String> getLegendTitles() {
    ArrayList<String> titles = new ArrayList<String>();
    ListStore<M> store = chart.getCurrentStore();
    for (int j = 0; j < store.size(); j++) {
      if (legendLabelProvider != null) {
        titles.add(legendLabelProvider.getLabel(store.get(j)));
      } else {
        titles.add(store.getKeyProvider().getKey(store.get(j)));
      }
    }
    return titles;
  }

  /**
   * Sets the value provider of the pie data.
   * 
   * @param angleField the value provider of the pie data
   */
  public void setAngleField(ValueProvider<? super M, ? extends Number> angleField) {
    this.angleField = angleField;
  }

  /**
   * Sets the degree of the pie's donut.
   * 
   * @param donut the degree of the pie's donut
   */
  public void setDonut(double donut) {
    this.donut = donut;
  }

  /**
   * Sets the {@link LabelProvider} used for the legend.
   * 
   * @param legendLabelProvider the label provider used for the legend
   */
  public void setLegendLabelProvider(LabelProvider<? super M> legendLabelProvider) {
    this.legendLabelProvider = legendLabelProvider;

    if (chart != null) {
      Legend<M> legend = chart.getLegend();
      if (legend != null) {
        legend.create();
        legend.updatePosition();
      }
    }
  }

  /**
   * Sets the {@link ValueProvider} and {@link LabelProvider} used for the
   * legend.
   * 
   * @param valueProvider the value provider
   * @param labelProvider the legend provider
   */
  public <V> void setLegendValueProvider(final ValueProvider<? super M, V> valueProvider,
      final LabelProvider<? super V> labelProvider) {
    setLegendLabelProvider(new LabelProvider<M>() {
      @Override
      public String getLabel(M item) {
        return labelProvider.getLabel(valueProvider.getValue(item));
      }
    });
  }

  /**
   * Calculates the {@link PathCommand}s for a pie slice using the given
   * attributes.
   * 
   * @param slice the properties of the slice
   * @return the list of path commands for the slice
   */
  protected List<PathCommand> calculateSegment(Slice slice) {
    double startAngle = slice.getStartAngle();
    double endAngle = slice.getEndAngle();
    double margin = slice.getMargin();
    double startRho = slice.getStartRho();
    double endRho = slice.getEndRho();
    double midAngle = Math.toRadians((startAngle + endAngle) / 2);
    int flag = (Math.abs(endAngle - startAngle) > 180) ? 1 : 0;

    double a1 = Math.toRadians(Math.min(startAngle, endAngle));
    double a2 = Math.toRadians(Math.max(startAngle, endAngle));

    double x = center.getX() + margin * Math.cos(midAngle);
    double y = center.getY() + margin * Math.sin(midAngle);

    double x1 = x + startRho * Math.cos(a1);
    double y1 = y + startRho * Math.sin(a1);

    double x2 = x + endRho * Math.cos(a1);
    double y2 = y + endRho * Math.sin(a1);

    double x3 = x + startRho * Math.cos(a2);
    double y3 = y + startRho * Math.sin(a2);

    double x4 = x + endRho * Math.cos(a2);
    double y4 = y + endRho * Math.sin(a2);

    List<PathCommand> commands = new ArrayList<PathCommand>();
    commands.add(new MoveTo(x1, y1));
    commands.add(new LineTo(x2, y2));
    // Solves mysterious clipping bug with IE
    if (Math.abs(x1 - x3) <= 0.01 && Math.abs(y1 - y3) <= 0.01) {
      commands.add(new EllipticalArc(endRho, endRho, 0, flag, 1, x4, y4));
      commands.add(new ClosePath());
    } else {
      commands.add(new EllipticalArc(endRho, endRho, 0, flag, 1, x4, y4));
      commands.add(new LineTo(x3, y3));
      commands.add(new EllipticalArc(startRho, startRho, 0, flag, 0, x1, y1));
      commands.add(new ClosePath());
    }

    return commands;
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
  protected Animator createSegmentAnimator(final PathSprite sprite, final Slice start, final Slice end) {
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
        sprite.setCommands(calculateSegment(origin));
        sprite.redraw();
      }
    };
  }

  @Override
  protected int getStoreIndex(int index) {
    return index;
  }
}
