/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.series;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.Legend;
import com.sencha.gxt.chart.client.chart.axis.Axis;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.DrawFx;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.Translation;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.chart.client.draw.sprite.CircleSprite;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.SpriteList;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.PrecisePoint;
import com.sencha.gxt.core.client.util.PreciseRectangle;
import com.sencha.gxt.data.shared.ListStore;

/**
 * Creates a Scatter Chart. The scatter plot is useful when trying to display
 * more than two variables in the same visualization. These variables can be
 * mapped into x, y coordinates and also to an element's radius/size, color,
 * etc.
 * 
 * Here is an example scatter configuration:
 * 
 * <pre>
    ScatterSeries<Data> series = new ScatterSeries<Data>();
    series.setYAxisPosition(Position.LEFT);
    series.setYField(dataAccess.data1());
    Sprite marker = Primitives.cross(0, 0, 8);
    marker.setFill(RGB.GREEN);
    series.setMarkerConfig(marker);
    chart.addSeries(series);
 * </pre>
 * 
 * First the series is created with its associated data type. The y-axis
 * position is set to tell the series the scale of the displayed axis. Otherwise
 * the series will use its own internal scale. Next the value provider field is
 * set, which provides the value of each point. A sprite is added that
 * determines the representation of each point in the scatter. Finally the
 * series is added to the chart where it will be displayed.
 * 
 * @param <M> the data type used by this series
 */
public class ScatterSeries<M> extends Series<M> {

  // The display style for the scatter series markers.
  protected Sprite markerConfig;
  protected double selectionTolerance = 20;

  protected ValueProvider<? super M, ? extends Number> yField;
  private String title;

  protected Map<Integer, PrecisePoint> coordinates = new HashMap<Integer, PrecisePoint>();
  protected boolean hidden = false;

  protected Position yAxisPosition;
  protected Position xAxisPosition;
  protected PrecisePoint min;
  protected PrecisePoint max;
  protected PrecisePoint scale;

  /**
   * Creates a scatter {@link Series}.
   */
  public ScatterSeries() {
    // setup shadow attributes
    Sprite config = new PathSprite();
    config.setStrokeWidth(6);
    config.setStrokeOpacity(0.05);
    config.setStroke(RGB.BLACK);
    shadowAttributes.add(config);
    config = new PathSprite();
    config.setStrokeWidth(4);
    config.setStrokeOpacity(0.1);
    config.setStroke(RGB.BLACK);
    shadowAttributes.add(config);
    config = new PathSprite();
    config.setStrokeWidth(2);
    config.setStrokeOpacity(0.15);
    config.setStroke(RGB.BLACK);
    shadowAttributes.add(config);

    // initialize the shadow groups
    if (shadowGroups.size() == 0) {
      for (int i = 0; i < shadowAttributes.size(); i++) {
        shadowGroups.add(new SpriteList<Sprite>());
      }
    }

    setHighlighter(new ScatterHighlighter());

    legendTitles.add("");

    CircleSprite circle = new CircleSprite();
    circle.setRadius(8);
    circle.setZIndex(11);
    markerConfig = circle;
  }

  public void calculateBounds() {
    PreciseRectangle chartBBox = chart.getBBox();
    ListStore<M> store = chart.getCurrentStore();
    min = new PrecisePoint(Double.NaN, Double.NaN);
    max = new PrecisePoint(Double.NaN, Double.NaN);
    scale = new PrecisePoint();

    bbox.setX(chartBBox.getX() + chart.getMaxGutter()[0]);
    bbox.setY(chartBBox.getY() + chart.getMaxGutter()[1]);
    bbox.setWidth(chartBBox.getWidth() - (chart.getMaxGutter()[0] * 2));
    bbox.setHeight(chartBBox.getHeight() - (chart.getMaxGutter()[1] * 2));

    Axis<M, ?> axis = chart.getAxis(yAxisPosition);
    if (axis != null) {
      if (axis.getPosition() == Position.TOP || axis.getPosition() == Position.BOTTOM) {
        min.setX(axis.getFrom());
        max.setX(axis.getTo());
      } else {
        min.setY(axis.getFrom());
        max.setY(axis.getTo());
      }
    } else if (yField != null) {
      NumericAxis<M> numAxis = new NumericAxis<M>();
      numAxis.setChart(chart);
      numAxis.addField(yField);
      numAxis.calcEnds();
      min.setY(numAxis.getFrom());
      max.setY(numAxis.getTo());
    }
    axis = chart.getAxis(xAxisPosition);
    if (axis != null) {
      if (axis.getPosition() == Position.TOP || axis.getPosition() == Position.BOTTOM) {
        min.setX(axis.getFrom());
        max.setX(axis.getTo());
      } else {
        min.setY(axis.getFrom());
        max.setY(axis.getTo());
      }
    } else if (xField != null) {
      NumericAxis<M> numAxis = new NumericAxis<M>();
      numAxis.setChart(chart);
      numAxis.addField(xField);
      numAxis.calcEnds();
      min.setX(numAxis.getFrom());
      max.setX(numAxis.getTo());
    }

    if (Double.isNaN(min.getX())) {
      min.setX(0);
      scale.setX(bbox.getWidth() / (store.size() - 1));
    } else {
      scale.setX(bbox.getWidth() / (max.getX() - min.getX()));
    }
    if (Double.isNaN(min.getY())) {
      min.setY(0);
      scale.setY(bbox.getHeight() / (store.size() - 1));
    } else {
      scale.setY(bbox.getHeight() / (max.getY() - min.getY()));
    }

    coordinates.clear();
    for (int i = 0; i < store.size(); i++) {
      M model = store.get(i);
      final double xValue;
      final double yValue;
      // Ensure a value
      if (xField == null) {
        xValue = i;
      } else if (xField.getValue(model) != null) {
        xValue = xField.getValue(model).doubleValue();
      } else {
        xValue = Double.NaN;
      }

      if (yField == null) {
        yValue = i;
      } else if (yField.getValue(model) != null) {
        yValue = yField.getValue(model).doubleValue();
      } else {
        yValue = Double.NaN;
      }

      double x = bbox.getX() + (xValue - min.getX()) * scale.getX();
      double y = bbox.getY() + bbox.getHeight() - (yValue - min.getY()) * scale.getY();
      if (!Double.isNaN(x) && !Double.isNaN(y)) {
        coordinates.put(i, new PrecisePoint(x, y));
      }
    }

    if (this instanceof LineSeries && coordinates.size() > bbox.getWidth()) {
      coordinates = shrink(bbox.getWidth());
    }
  }

  @Override
  public void drawSeries() {
    ListStore<M> store = chart.getCurrentStore();

    // if the store is empty then there's nothing to be rendered
    if (store == null || store.size() == 0) {
      this.clear();
      return;
    }

    calculateBounds();

    if (!chart.hasShadows()) {
      hideShadows();
    }
    drawMarkers();
    drawLabels();
  }

  /**
   * Returns the marker configuration.
   * 
   * @return the marker configuration
   */
  public Sprite getMarkerConfig() {
    return markerConfig;
  }

  /**
   * Returns the selection tolerance of markers.
   * 
   * @return the selection tolerance of markers
   */
  public double getSelectionTolerance() {
    return selectionTolerance;
  }

  /**
   * Returns the series title used in the legend.
   * 
   * @return the series title used in the legend
   */
  public String getTitle() {
    return title;
  }

  /**
   * Returns the x axis position of the series.
   * 
   * @return the x axis position of the series
   */
  public Position getXAxisPosition() {
    return xAxisPosition;
  }

  /**
   * Returns the y axis position of the series.
   * 
   * @return the y axis position of the series
   */
  public Position getYAxisPosition() {
    return yAxisPosition;
  }

  /**
   * Returns the {@link ValueProvider} used for the y axis of the series.
   * 
   * @return the value provider used for the y axis of the series
   */
  public ValueProvider<? super M, ? extends Number> getYField() {
    return yField;
  }

  @Override
  public void hide(int yFieldIndex) {
    toggle(true);
  }

  @Override
  public void highlight(int yFieldIndex) {
    Sprite sprite = sprites.get(yFieldIndex);
    if (highlighter != null) {
      highlighter.highlight(sprite);
    }
  }

  @Override
  public void highlightAll(int index) {
    for (int i = 0; i < sprites.size(); i++) {
      highlighter.highlight(sprites.get(i));
    }
  }

  /**
   * Set the series title used in the legend.
   * 
   * @param title the series title used in the legend
   */
  public void setLegendTitle(String title) {
    if (title != null) {
      legendTitles.set(0, title);
    } else {
      legendTitles.set(0, getValueProviderName(yField, 0));
    }
    this.title = title;
    if (chart != null) {
      Legend<M> legend = chart.getLegend();
      if (legend != null) {
        legend.create();
        legend.updatePosition();
      }
    }
  }

  /**
   * Sets the marker configuration.
   * 
   * @param markerConfig the marker configuration
   */
  public void setMarkerConfig(Sprite markerConfig) {
    if (this.markerConfig != markerConfig) {
      this.markerConfig = markerConfig;
      clear();
    }
  }

  /**
   * Sets the selection tolerance of markers.
   * 
   * @param selectionTolerance the selection tolerance of markers
   */
  public void setSelectionTolerance(double selectionTolerance) {
    this.selectionTolerance = selectionTolerance;
  }

  /**
   * Sets the position of the x axis on the chart to be used by the series.
   * 
   * @param xAxisPosition the position of the x axis on the chart to be used by
   *          the series
   */
  public void setXAxisPosition(Position xAxisPosition) {
    this.xAxisPosition = xAxisPosition;
  }

  /**
   * Sets the position of the y axis on the chart to be used by the series.
   * 
   * @param yAxisPosition the position of the y axis on the chart to be used by
   *          the series
   */
  public void setYAxisPosition(Position yAxisPosition) {
    this.yAxisPosition = yAxisPosition;
  }

  /**
   * Sets the {@link ValueProvider} used for the y axis of the series.
   * 
   * @param yField the value provider
   */
  public void setYField(ValueProvider<? super M, ? extends Number> yField) {
    this.yField = yField;
    if (title == null) {
      legendTitles.set(0, getValueProviderName(yField, 0));
    }
  }

  @Override
  public void show(int yFieldIndex) {
    toggle(false);
  }

  @Override
  public void unHighlight(int yFieldIndex) {
    if (highlighter != null) {
      highlighter.unHighlight(sprites.get(yFieldIndex));
    }
  }

  @Override
  public void unHighlightAll(int index) {
    for (int i = 0; i < sprites.size(); i++) {
      highlighter.unHighlight(sprites.get(i));
    }
  }

  @Override
  public boolean visibleInLegend(int index) {
    if (sprites.size() == 0) {
      return true;
    } else {
      return !sprites.get(0).isHidden();
    }
  }

  /**
   * Draws the labels on the series.
   */
  protected void drawLabels() {
    if (labelConfig != null) {
      for (int j = coordinates.size(); j < labels.size(); j++) {
        Sprite unusedLabel = labels.get(j);
        unusedLabel.setHidden(true);
        unusedLabel.redraw();
      }
      for (int i = 0; i < chart.getStore().size(); i++) {
        final Sprite sprite;
        if (labels.get(i) != null) {
          sprite = labels.get(i);
          if (!hidden) {
            sprite.setHidden(false);
          }
        } else {
          sprite = labelConfig.getSpriteConfig().copy();
          sprite.setTranslation((bbox.getX() + bbox.getWidth()) / 2, (bbox.getY() + bbox.getHeight()) / 2);
          labels.put(i, sprite);
          chart.addSprite(sprite);
        }
        if (chart.isResizing()) {
          sprite.setTranslation((bbox.getX() + bbox.getWidth()) / 2, (bbox.getY() + bbox.getHeight()) / 2);
        }
        setLabelText(sprite, i);
        sprite.redraw();
        if (labelConfig.isLabelContrast()) {
          final Sprite back = sprites.get(i);
          if (chart.isAnimated()) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
              @Override
              public void execute() {
                setLabelContrast(sprite, labelConfig, back);
              }
            });
          } else {
            setLabelContrast(sprite, labelConfig, back);
          }
        }
        PreciseRectangle textBox = sprite.getBBox();
        PrecisePoint point = coordinates.get(i);
        if (point != null) {
          double x = coordinates.get(i).getX();
          double y = coordinates.get(i).getY();

          y -= textBox.getHeight() / 2.0;

          if (chart.isAnimated() && sprite.getTranslation() != null) {
            DrawFx.createTranslationAnimator(sprite, x, y).run(chart.getAnimationDuration(), chart.getAnimationEasing());
          } else {
            sprite.setTranslation(x, y);
            sprite.redraw();
          }
        } else {
          sprite.setHidden(true);
          sprite.redraw();
        }
      }
    }
  }

  protected void drawMarkers() {
    ListStore<M> store = chart.getCurrentStore();

    // hide unused sprites
    for (int j = coordinates.size(); j < sprites.size(); j++) {
      Sprite unusedSprite = sprites.get(j);
      unusedSprite.setHidden(true);
      unusedSprite.redraw();

      for (int shindex = 0; shindex < shadowGroups.size(); shindex++) {
        SpriteList<Sprite> shadows = shadowGroups.get(shindex);
        Sprite unusedShadow = shadows.get(j);
        unusedShadow.setHidden(true);
        unusedShadow.redraw();
      }
    }

    // Create new or reuse sprites and animate/display
    for (int i = 0; i < chart.getStore().size(); i++) {
      final Sprite sprite;
      if (i < sprites.size()) {
        sprite = sprites.get(i);
        if (!hidden) {
          sprite.setHidden(false);
        }
      } else {
        sprite = markerConfig.copy();
        sprite.setTranslation((bbox.getX() + bbox.getWidth()) / 2, (bbox.getY() + bbox.getHeight()) / 2);
        sprites.add(sprite);
        chart.addSprite(sprite);
      }
      if (chart.isResizing()) {
        sprite.setTranslation((bbox.getX() + bbox.getWidth()) / 2, (bbox.getY() + bbox.getHeight()) / 2);
      }
      if (chart.hasShadows()) {
        drawShadows(i);
      }
      PrecisePoint point = coordinates.get(i);
      if (point != null) {
        double x = point.getX();
        double y = point.getY();
        if (chart.isAnimated()) {
          DrawFx.createTranslationAnimator(sprite, x, y).run(chart.getAnimationDuration(), chart.getAnimationEasing());
        } else {
          sprite.setTranslation(x, y);
          sprite.redraw();
        }
      } else {
        sprite.setHidden(true);
        sprite.redraw();
      }
      if (renderer != null) {
        renderer.spriteRenderer(sprite, i, store);
      }
    }
  }

  protected void drawShadows(int i) {

    for (int shindex = 0; shindex < shadowGroups.size(); shindex++) {
      SpriteList<Sprite> shadows = shadowGroups.get(shindex);
      Sprite shadowAttr = shadowAttributes.get(shindex);
      final Sprite shadowSprite;
      if (i < shadows.size()) {
        shadowSprite = shadows.get(i);
        if (!hidden) {
          shadowSprite.setHidden(false);
        }
      } else {
        shadowSprite = markerConfig.copy();
        shadowSprite.setTranslation((bbox.getX() + bbox.getWidth()) / 2, (bbox.getY() + bbox.getHeight()) / 2);
        shadowSprite.setStrokeWidth(shadowAttr.getStrokeWidth());
        shadowSprite.setStroke(shadowAttr.getStroke());
        shadowSprite.setStrokeOpacity(shadowAttr.getStrokeOpacity());
        shadowSprite.setFill(Color.NONE);
        shadows.add(shadowSprite);
        chart.addSprite(shadowSprite);
      }
      if (chart.isResizing()) {
        shadowSprite.setTranslation((bbox.getX() + bbox.getWidth()) / 2, (bbox.getY() + bbox.getHeight()) / 2);
      }
      PrecisePoint point = coordinates.get(i);
      if (point != null) {
        if (chart.isAnimated()) {
          DrawFx.createTranslationAnimator(shadowSprite, point.getX(), point.getY()).run(chart.getAnimationDuration(),
              chart.getAnimationEasing());
        } else {
          shadowSprite.setTranslation(point.getX(), point.getY());
          shadowSprite.redraw();
        }
      } else {
        shadowSprite.setHidden(true);
        shadowSprite.redraw();
      }
      if (shadowRenderer != null) {
        shadowRenderer.spriteRenderer(shadowSprite, i, chart.getCurrentStore());
      }
    }
    shadowed = true;
  }

  @Override
  protected int getIndex(PrecisePoint point) {
    for (int i = 0; i < sprites.size(); i++) {
      Translation trans = sprites.get(i).getTranslation();
      if (point.equalsNoPrecision(new PrecisePoint(trans.getX(), trans.getY()), selectionTolerance)) {
        return i;
      }
    }
    return -1;
  }

  @Override
  protected int getStoreIndex(int index) {
    return index;
  }

  @Override
  protected ValueProvider<? super M, ? extends Number> getValueProvider(int index) {
    return yField;
  }

  /**
   * Shrinks the number of coordinates to fit the screen.
   * 
   * @param width the maximum width of the chart
   * @return the new shrunk coordinates
   */
  private Map<Integer, PrecisePoint> shrink(double width) {
    Map<Integer, PrecisePoint> result = new HashMap<Integer, PrecisePoint>();
    result.put(0, coordinates.get(0));
    double xSum = 0;
    double ySum = 0;
    final double ratio = Math.ceil(coordinates.size() / width);

    for (int i = 1; i < coordinates.size(); i++) {
      PrecisePoint point = coordinates.get(i);
      if (point != null) {
        xSum += point.getX();
        ySum += point.getY();
      }
      if (i % ratio == 0) {
        result.put(result.size(), new PrecisePoint(xSum / ratio, ySum / ratio));
        xSum = 0;
        ySum = 0;
      }
    }
    return result;
  }

  /**
   * Toggles all the sprites in the series to be hidden or shown.
   * 
   * @param hide if true hides
   */
  private void toggle(boolean hide) {
    if (sprites.size() > 0) {
      hidden = hide;
      for (int i = 0; i < sprites.size(); i++) {
        sprites.get(i).setHidden(hide);
        sprites.get(i).redraw();
      }
      if (chart.hasShadows()) {
        for (int i = 0; i < shadowGroups.size(); i++) {
          SpriteList<Sprite> shadows = shadowGroups.get(i);
          for (int j = 0; j < shadows.size(); j++) {
            shadows.get(j).setHidden(hide);
            shadows.get(j).redraw();
          }
        }
      }
    }
  }
}
