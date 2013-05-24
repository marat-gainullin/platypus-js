/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.axis;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.RoundNumberProvider;
import com.sencha.gxt.chart.client.chart.series.RadarSeries;
import com.sencha.gxt.chart.client.chart.series.Series;
import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.path.ClosePath;
import com.sencha.gxt.chart.client.draw.path.LineTo;
import com.sencha.gxt.chart.client.draw.path.MoveTo;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.chart.client.draw.sprite.CircleSprite;
import com.sencha.gxt.chart.client.draw.sprite.SpriteList;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite.TextAnchor;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.PreciseRectangle;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;

/**
 * {@link Axis} to be used with {@link RadarSeries}.
 * 
 * @param <M> the data type of the axis
 * @param <V> the variable type of axis
 */
public class RadialAxis<M, V> extends Axis<M, V> {

  /**
   * Enumeration of the display style of the radial axis.
   */
  public enum Display {
    /**
     * Display both the category axis and the scale axis.
     */
    BOTH,
    /**
     * Display the categories around the axis.
     */
    CATEGORIES,
    /**
     * Displays the numerical scale in the center of the axis.
     */
    SCALE
  }

  private CircleSprite circleConfig;

  private SpriteList<CircleSprite> circles = new SpriteList<CircleSprite>();

  protected List<ValueProvider<? super M, ? extends Number>> fields = new ArrayList<ValueProvider<? super M, ? extends Number>>();
  private SpriteList<TextSprite> scales = new SpriteList<TextSprite>();
  private TextSprite scaleConfig = new TextSprite();
  private LabelProvider<Number> scaleLabelProvider = new RoundNumberProvider<Number>();
  protected ValueProvider<? super M, V> categoryField;
  private Display display = Display.BOTH;
  private int radialSteps = 10;

  /**
   * Creates a radial {@link Axis}.
   */
  public RadialAxis() {
    circleConfig = new CircleSprite();
    circleConfig.setStroke(new RGB("#ccc"));
    circleConfig.setFill(Color.NONE);
    axisConfig.setStroke(new RGB("#ccc"));
  }

  @Override
  public void clear() {
    super.clear();
    while (circles.size() > 0) {
      CircleSprite sprite = circles.remove(circles.size() - 1);
      if (sprite != null) {
        sprite.remove();
      }
    }
    while (scales.size() > 0) {
      TextSprite sprite = scales.remove(scales.size() - 1);
      if (sprite != null) {
        sprite.remove();
      }
    }
  }

  @Override
  public void drawAxis(boolean init) {
    PreciseRectangle bbox = chart.getBBox();
    ListStore<M> store = chart.getCurrentStore();
    double length = store.size();
    double pi2 = Math.PI * 2;
    double centerX = bbox.getX() + (bbox.getWidth() / 2);
    double centerY = bbox.getY() + (bbox.getHeight() / 2);
    double rho = Math.min(bbox.getWidth(), bbox.getHeight()) / 2;

    for (int i = 0; i < radialSteps; i++) {
      final CircleSprite circle;
      if (i < circles.size()) {
        circle = circles.get(i);
      } else {
        circle = circleConfig.copy();
        chart.addSprite(circle);
        circles.add(circle);
      }
      circle.setCenterX(centerX);
      circle.setCenterY(centerY);
      circle.setRadius(Math.max(rho * (i + 1) / radialSteps, 0));
      circle.redraw();
    }

    for (int j = (int) length; j < lines.size(); j++) {
      PathSprite extraLine = lines.get(j);
      extraLine.setHidden(true);
      extraLine.redraw();
    }
    for (double i = 0; i < length; i++) {
      final PathSprite line;
      if (i < lines.size()) {
        line = lines.get((int) i);
        line.setHidden(false);
      } else {
        line = axisConfig.copy();
        chart.addSprite(line);
        lines.add(line);
      }
      line.clearCommands();
      line.addCommand(new MoveTo(centerX, centerY));
      line.addCommand(new LineTo(centerX + rho * Math.cos(i / length * pi2), centerY + rho * Math.sin(i / length * pi2)));
      line.addCommand(new ClosePath());
      line.redraw();
    }
    drawLabels();
  }

  @Override
  public void drawLabels() {
    labelNames.clear();
    fields.clear();
    PreciseRectangle bbox = chart.getBBox();
    ListStore<M> store = chart.getCurrentStore();
    double pi2 = Math.PI * 2;
    double centerX = bbox.getX() + (bbox.getWidth() / 2);
    double centerY = bbox.getY() + (bbox.getHeight() / 2);
    double rho = Math.min(bbox.getWidth(), bbox.getHeight()) / 2;
    double maxValue = 0;
    double margin = 10;

    // get all rendered fields
    for (Series<M> series : chart.getSeries()) {
      if (series instanceof RadarSeries) {
        final RadarSeries<M> radar = (RadarSeries<M>) series;
        fields.add(radar.getYField());
      }
    }
    // get maxValue to interpolate
    for (int index = 0; index < store.size(); index++) {
      for (int i = 0; i < fields.size(); i++) {
        maxValue = Math.max(fields.get(i).getValue(store.get(index)).doubleValue(), maxValue);
      }
      if (categoryField != null) {
        labelNames.add(categoryField.getValue(store.get(index)));
      }
    }

    // draw scale
    if (display != Display.CATEGORIES) {
      for (int i = 0; i < radialSteps; i++) {
        final TextSprite scale;
        if (i < scales.size()) {
          scale = scales.get(i);
        } else {
          scale = scaleConfig.copy();
          chart.addSprite(scale);
          scales.add(scale);
        }
        scale.setText(scaleLabelProvider.getLabel((Math.round((i + 1) / (double) radialSteps * maxValue))));
        scale.setX(centerX);
        scale.setY(centerY - rho * (i + 1) / radialSteps);
        scale.redraw();
      }
    }

    for (int j = labelNames.size(); j < labels.size(); j++) {
      TextSprite extraLabel = labels.get(j);
      extraLabel.setHidden(true);
      extraLabel.redraw();
    }
    
    // draw text
    if (display != Display.SCALE && labelNames.size() > 0) {
      double size = labelNames.size();
      for (int i = 0; i < size; i++) {
        final TextSprite label;
        if (i < labels.size()) {
          label = labels.get(i);
          label.setHidden(false);
        } else {
          label = labelConfig.copy();
          chart.addSprite(label);
          labels.add(label);
        }
        double dx = Math.cos(i / size * pi2) * (rho + margin);
        double dy = Math.sin(i / size * pi2) * (rho + margin);
        label.setText(labelProvider.getLabel((labelNames.get(i))));
        label.redraw();
        label.setX(centerX + dx);
        label.setY(centerY + dy - (label.getBBox().getHeight() / 2.0));
        if (dx * dx <= 0.001) {
          label.setTextAnchor(TextAnchor.MIDDLE);
        } else {
          if (dx < 0) {
            label.setTextAnchor(TextAnchor.END);
          } else {
            label.setTextAnchor(TextAnchor.START);
          }
        }
        label.redraw();
      }
    }
  }

  /**
   * Returns the {@link ValueProvider} used for labels around the axis.
   * 
   * @return the value provider used for labels around the axis
   */
  public ValueProvider<? super M, V> getCategoryField() {
    return categoryField;
  }

  /**
   * Returns the configuration for the circles on the axis.
   * 
   * @return the configuration for the circles on the axis
   */
  public CircleSprite getCircleConfig() {
    return circleConfig;
  }

  /**
   * Returns the {@link Display} style of the axis.
   * 
   * @return the display style of the axis
   */
  public Display getDisplay() {
    return display;
  }

  @Override
  public Position getPosition() {
    return null;
  }

  /**
   * Returns the configuration for labels on the numeric scale.
   * 
   * @return the configuration for labels on the numeric scale
   */
  public TextSprite getScaleConfig() {
    return scaleConfig;
  }

  /**
   * Returns the {@link LabelProvider} used to display labels on the scale.
   * 
   * @return the label provider used to display labels on the scale
   */
  public LabelProvider<Number> getScaleLabelProvider() {
    return scaleLabelProvider;
  }

  /**
   * Returns the steps of the axis.
   * 
   * @return the steps of the axis
   */
  public int getSteps() {
    return radialSteps;
  }

  /**
   * Sets the {@link ValueProvider} used to get the values for the category
   * labels.
   * 
   * @param categoryField the value provider used to get the values for the
   *          category labels
   */
  public void setCategoryField(ValueProvider<? super M, V> categoryField) {
    this.categoryField = categoryField;
  }

  /**
   * Sets the configuration for the circles on the axis.
   * 
   * @param circleConfig the configuration for the circles on the axis
   */
  public void setCircleConfig(CircleSprite circleConfig) {
    if (this.circleConfig != circleConfig) {
      this.circleConfig = circleConfig;
      circles.clear();
    }
  }

  /**
   * Sets the {@link Display} style of the axis.
   * 
   * @param display the display style of the axis
   */
  public void setDisplay(Display display) {
    this.display = display;
  }

  /**
   * Sets the configuration for labels on the numeric scale.
   * 
   * @param scaleConfig the configuration for labels on the numeric scale
   */
  public void setScaleConfig(TextSprite scaleConfig) {
    if (this.scaleConfig != scaleConfig) {
      this.scaleConfig = scaleConfig;
      scales.clear();
    }
  }

  /**
   * Sets the {@link LabelProvider} used to display labels on the scale.
   * 
   * @param scaleLabelProvider the label provider used to display labels on the
   *          scale
   */
  public void setScaleLabelProvider(LabelProvider<Number> scaleLabelProvider) {
    this.scaleLabelProvider = scaleLabelProvider;
  }

  /**
   * Sets the number of steps on the axis.
   * 
   * @param steps the number of steps on the axis
   */
  public void setSteps(int steps) {
    radialSteps = steps;
  }

}
