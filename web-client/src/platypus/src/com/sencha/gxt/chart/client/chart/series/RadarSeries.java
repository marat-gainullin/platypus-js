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

import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.DrawFx;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.Translation;
import com.sencha.gxt.chart.client.draw.path.ClosePath;
import com.sencha.gxt.chart.client.draw.path.LineTo;
import com.sencha.gxt.chart.client.draw.path.MoveTo;
import com.sencha.gxt.chart.client.draw.path.PathCommand;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.SpriteList;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.PrecisePoint;
import com.sencha.gxt.data.shared.ListStore;

/**
 * Creates a Radar Chart. A Radar Chart is a useful visualization technique for
 * comparing different quantitative values for a constrained number of
 * categories.
 * 
 * Here is an example radar configuration:
 * 
 * <pre>
    RadarSeries<Data> radar = new RadarSeries<Data>();
    radar.setYField(dataAccess.data1());
    radar.setStroke(RGB.GREEN);
    chart.addSeries(radar);
 * </pre>
 * 
 * First the series is created with its associated data type. Next the value
 * provider field is set, which provides the value of each point on the line.
 * The stroke is set to represent the color of the line. Finally the series is
 * added to the chart where it will be displayed.
 * 
 * @param <M> the data type used by this series
 */
public class RadarSeries<M> extends ScatterSeries<M> {

  private double centerX;
  private double centerY;
  private double radius;
  private List<ValueProvider<? super M, ? extends Number>> fields = new ArrayList<ValueProvider<? super M, ? extends Number>>();
  private PathSprite radar;
  private SpriteList<PathSprite> radarShadows = new SpriteList<PathSprite>();
  private boolean showMarkers = false;
  private Color fill = Color.NONE;
  private SeriesRenderer<M> lineRenderer;

  /**
   * Creates a radar series.
   */
  public RadarSeries() {
    // setup shadow attributes
    shadowAttributes = new ArrayList<Sprite>();
    Sprite config = new PathSprite();
    config.setStrokeWidth(6);
    config.setStrokeOpacity(0.05);
    config.setStroke(RGB.BLACK);
    config.setTranslation(1, 1);
    shadowAttributes.add(config);
    config = new PathSprite();
    config.setStrokeWidth(4);
    config.setStrokeOpacity(0.1);
    config.setStroke(RGB.BLACK);
    config.setTranslation(1, 1);
    shadowAttributes.add(config);
    config = new PathSprite();
    config.setStrokeWidth(2);
    config.setStrokeOpacity(0.15);
    config.setStroke(RGB.BLACK);
    config.setTranslation(1, 1);
    shadowAttributes.add(config);

    // initialize shadow groups
    if (shadowGroups.size() == 0) {
      for (int i = 0; i < shadowAttributes.size(); i++) {
        shadowGroups.add(new SpriteList<Sprite>());
      }
    }
    setHighlighter(new LineHighlighter());
    strokeWidth = 0.5;
  }

  @Override
  public void clear() {
    super.clear();
    if (radar != null) {
      radar.remove();
      radar = null;
    }
    radarShadows.clear();
  }

  @Override
  public void drawSeries() {
    bbox = chart.getBBox();
    ListStore<M> store = chart.getCurrentStore();
    double maxValue = 0;
    double rho = 0;
    double x = 0;
    double y = 0;
    double len = store.size();
    List<PathCommand> commands = new ArrayList<PathCommand>();
    coordinates.clear();

    if (store == null || store.size() == 0) {
      this.clear();
      return;
    }

    centerX = bbox.getX() + (bbox.getWidth() / 2);
    centerY = bbox.getY() + (bbox.getHeight() / 2);
    radius = Math.min(bbox.getWidth(), bbox.getHeight()) / 2;

    // get all renderer fields
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
    }

    // create path and items
    for (int i = 0; i < len; i++) {
      M model = store.get(i);
      rho = radius * yField.getValue(model).doubleValue() / maxValue;
      x = rho * Math.cos(i / len * Math.PI * 2);
      y = rho * Math.sin(i / len * Math.PI * 2);
      if (i == 0) {
        commands.add(new MoveTo(x + centerX, y + centerY));
      } else {
        commands.add(new LineTo(x + centerX, y + centerY));
      }
      coordinates.put(i, new PrecisePoint(x + centerX, y + centerY));
    }
    commands.add(new ClosePath());

    // create path sprite
    if (radar == null) {
      radar = new PathSprite();
      radar.addCommand(new MoveTo(centerX, centerY));
      for (int i = 1; i < commands.size(); i++) {
        radar.addCommand(new LineTo(centerX, centerY));
      }
      chart.addSprite(radar);
      if (chart.hasShadows()) {
        // create shadows
        for (int i = 0; i < shadowGroups.size(); i++) {
          PathSprite shadow = new PathSprite();
          Sprite shadowAttr = shadowAttributes.get(i);
          shadow.setStrokeWidth(shadowAttr.getStrokeWidth());
          shadow.setStrokeOpacity(shadowAttr.getStrokeOpacity());
          shadow.setStroke(shadowAttr.getStroke());
          shadow.setTranslation(new Translation(shadowAttr.getTranslation()));
          shadow.setFill(Color.NONE);
          shadow.setCommands(radar.getCommands());
          chart.addSprite(shadow);
          radarShadows.add(shadow);
        }
      }
    } else if (chart.isResizing() && chart.isAnimated()) {
      radar.clearCommands();
      radar.addCommand(new MoveTo(centerX, centerY));
      for (int i = 1; i < commands.size(); i++) {
        radar.addCommand(new LineTo(centerX, centerY));
      }
      for (int i = 0; i < radarShadows.size(); i++) {
        radarShadows.get(i).setCommands(radar.getCommands());
      }
    }
    if (!Double.isNaN(strokeWidth)) {
      radar.setStrokeWidth(strokeWidth);
    }
    if (fill != null) {
      radar.setFill(fill);
    }
    if (stroke != null) {
      radar.setStroke(stroke);
    }
    if (chart.isAnimated() && radar.size() > 0) {
      DrawFx.createCommandsAnimator(radar, commands).run(500);
    } else {
      radar.setCommands(commands);
      radar.redraw();
    }
    if (lineRenderer != null) {
      lineRenderer.spriteRenderer(radar, 0, chart.getStore());
    }
    if (chart.hasShadows()) {
      for (int i = 0; i < radarShadows.size(); i++) {
        PathSprite shadow = radarShadows.get(i);
        if (!hidden) {
          shadow.setHidden(false);
        }
        if (chart.isAnimated()) {
          DrawFx.createCommandsAnimator(shadow, commands).run(500);
        } else {
          shadow.setCommands(commands);
          shadow.redraw();
        }
      }
      shadowed = true;
    } else {
      hideShadows();
    }
    if (showMarkers) {
      drawMarkers();
    }
    drawLabels();
  }

  /**
   * Returns the fill of the line in the series.
   * 
   * @return the fill of the line in the series
   */
  public Color getFill() {
    return fill;
  }

  /**
   * Returns the {@link SeriesRenderer} used on the line sprite.
   * 
   * @return the series renderer used on the line sprite
   */
  public SeriesRenderer<M> getLineRenderer() {
    return lineRenderer;
  }

  @Override
  public void hide(int yFieldIndex) {
    toggle(true);
  }

  @Override
  public void highlight(int yFieldIndex) {
    highlighter.highlight(sprites.get(yFieldIndex));
  }

  @Override
  public void highlightAll(int index) {
    for (int i = 0; i < sprites.size(); i++) {
      highlighter.highlight(sprites.get(i));
    }
  }

  /**
   * Returns whether or not the series shows markers.
   * 
   * @return true if markers shown
   */
  public boolean isShowMarkers() {
    return showMarkers;
  }

  /**
   * Sets the fill of the line in the series.
   * 
   * @param fill the fill of the line in the series
   */
  public void setFill(Color fill) {
    this.fill = fill;
  }

  /**
   * Sets the {@link SeriesRenderer} used on the line sprite
   * 
   * @param lineRenderer the series renderer used on the line sprite
   */
  public void setLineRenderer(SeriesRenderer<M> lineRenderer) {
    this.lineRenderer = lineRenderer;
  }

  /**
   * Sets whether or not the series shows markers.
   * 
   * @param showMarkers true if markers shown
   */
  public void setShowMarkers(boolean showMarkers) {
    if (this.showMarkers != showMarkers) {
      this.showMarkers = showMarkers;
      sprites.clear();
      for (int i = 0; i < shadowGroups.size(); i++) {
        shadowGroups.get(i).clear();
      }
    }
  }

  @Override
  public void show(int yFieldIndex) {
    toggle(false);
  }

  @Override
  public void unHighlight(int yFieldIndex) {
    highlighter.unHighlight(sprites.get(yFieldIndex));
  }

  @Override
  public void unHighlightAll(int index) {
    for (int i = 0; i < sprites.size(); i++) {
      highlighter.unHighlight(sprites.get(i));
    }
  }

  @Override
  public boolean visibleInLegend(int index) {
    if (radar == null) {
      return true;
    } else {
      return !radar.isHidden();
    }
  }

  @Override
  protected int getIndex(PrecisePoint point) {
    for (int i = 0; i < radar.size() - 1; i++) {
      PathCommand command = radar.getCommand(i);
      PrecisePoint bound = getPointFromCommand(command);
      if (point.equalsNoPrecision(bound, selectionTolerance)) {
        return i;
      }
    }
    return -1;
  }

  @Override
  protected void hideShadows() {
    if (shadowed) {
      for (int i = 0; i < radarShadows.size(); i++) {
        radarShadows.get(i).setHidden(true);
        radarShadows.get(i).redraw();
      }
    }
    super.hideShadows();
  }

  /**
   * Toggles all the sprites in the series to be hidden or shown.
   * 
   * @param hide if true hides
   */
  private void toggle(boolean hide) {
    if (radar != null) {
      radar.setHidden(hide);
      radar.redraw();
      for (int i = 0; i < radarShadows.size(); i++) {
        Sprite shadow = radarShadows.get(i);
        shadow.setHidden(hide);
        shadow.redraw();
      }
    }
    for (int i = 0; i < sprites.size(); i++) {
      sprites.get(i).setHidden(hide);
      sprites.get(i).redraw();
    }
  }

}
