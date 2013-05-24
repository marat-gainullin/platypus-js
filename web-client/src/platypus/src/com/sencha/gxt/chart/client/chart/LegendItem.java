/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart;

import com.google.gwt.canvas.dom.client.Context2d.LineJoin;
import com.google.gwt.dom.client.Style.FontWeight;
import com.sencha.gxt.chart.client.chart.series.LineSeries;
import com.sencha.gxt.chart.client.chart.series.MultipleColorSeries;
import com.sencha.gxt.chart.client.chart.series.RadarSeries;
import com.sencha.gxt.chart.client.chart.series.ScatterSeries;
import com.sencha.gxt.chart.client.chart.series.Series;
import com.sencha.gxt.chart.client.draw.path.LineTo;
import com.sencha.gxt.chart.client.draw.path.MoveTo;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.chart.client.draw.sprite.RectangleSprite;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.SpriteList;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.core.client.util.PrecisePoint;
import com.sencha.gxt.core.client.util.PreciseRectangle;

/**
 * Item representing a legend element.
 * 
 * @param <M> the data type of the legend item
 */
public class LegendItem<M> extends SpriteList<Sprite> {

  private Legend<M> legend;
  private Series<M> series;
  private int yFieldIndex;
  private double x;
  private double y;
  private TextSprite label;
  private RectangleSprite mask;
  private Sprite marker;
  private PathSprite line;
  private boolean on = true;
  private boolean highlight = false;

  /**
   * Creates a legend item.
   * 
   * @param legend the legend that the item is attached
   * @param series the series represented by the item
   * @param yFieldIndex the index of the y field the item represents
   */
  public LegendItem(Legend<M> legend, Series<M> series, int yFieldIndex) {
    this.legend = legend;
    this.series = series;
    this.yFieldIndex = yFieldIndex;
    createLegend();
  }

  /**
   * Returns the legend that the item is attached.
   * 
   * @return the legend that the item is attached
   */
  public Legend<M> getLegend() {
    return legend;
  }

  /**
   * Returns the series that the item represents.
   * 
   * @return the series that the item represents
   */
  public Series<M> getSeries() {
    return series;
  }

  /**
   * Returns the x-coordinate of the legend item.
   * 
   * @return the x-coordinate of the legend item
   */
  public double getX() {
    return x;
  }

  /**
   * Returns the y-coordinate of the legend item.
   * 
   * @return the y-coordinate of the legend item
   */
  public double getY() {
    return y;
  }

  /**
   * Returns the index of the y field of the series that the item represents.
   * 
   * @return the index of the y field of the series that the item represents
   */
  public int getYFieldIndex() {
    return yFieldIndex;
  }

  /**
   * Returns true if the legend item is currently enabled.
   * 
   * @return true if the legend item is currently enabled
   */
  public boolean isOn() {
    return on;
  }

  /**
   * Method used when the legend item is clicked.
   */
  public void onMouseDown() {
    if (legend.isItemHiding()) {
      if (on) {
        label.setOpacity(0.5);
        onMouseOut();
        series.hide(yFieldIndex);
      } else {
        label.setOpacity(1);
        series.show(yFieldIndex);
      }
      label.redraw();
      on = !on;
    }
  }

  /**
   * Method used when mouse over the legend item.
   */
  public void onMouseMove() {
    if (!highlight && on) {
      label.setFontWeight(FontWeight.BOLD);
      label.redraw();
      if (legend.isItemHighlighting()) {
        series.highlightAll(yFieldIndex);
      }
      highlight = true;
    }
  }

  /**
   * Method used when the mouse leaves the legend item.
   */
  public void onMouseOut() {
    if (highlight && on) {
      label.setFontWeight(FontWeight.NORMAL);
      label.redraw();
      if (legend.isItemHighlighting()) {
        series.unHighlightAll(yFieldIndex);
      }
      highlight = false;
    }
  }

  /**
   * Sets the legend that the item is attached.
   * 
   * @param legend the legend that the item is attached
   */
  public void setLegend(Legend<M> legend) {
    this.legend = legend;
  }

  /**
   * Sets the series that the item represents.
   * 
   * @param series the series that the item represents
   */
  public void setSeries(Series<M> series) {
    this.series = series;
  }

  /**
   * Sets the x-coordinate of the legend item.
   * 
   * @param x the x-coordinate of the legend item
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * Sets the y-coordinate of the legend item.
   * 
   * @param y the y-coordinate of the legend item
   */
  public void setY(double y) {
    this.y = y;
  }

  /**
   * Sets the index of the y field of the series that the item represents.
   * 
   * @param yFieldIndex the index of the y field of the series that the item
   *          represents
   */
  public void setYFieldIndex(int yFieldIndex) {
    this.yFieldIndex = yFieldIndex;
  }

  /**
   * Updates the position of the legend item based on the given relative point.
   * 
   * @param relativeX the relative x-coordinate
   * @param relativeY the relative y-coordinate
   */
  public void updatePosition(double relativeX, double relativeY) {
    PrecisePoint relative = new PrecisePoint(relativeX, relativeY);
    if (label != null) {
      label.setX(20 + relative.getX() + x);
      label.setY(relative.getY() + y - (label.getBBox().getHeight() / 2.0));
      if (legend.getLabelRenderer() != null) {
        legend.getLabelRenderer().spriteRenderer(label, yFieldIndex, legend.getChart().getCurrentStore());
      }
      label.redraw();
    }
    if (mask != null) {
      mask.setTranslation(relative.getX() + x + 1, relative.getY() + y - 1);
      mask.redraw();
    }
    if (marker != null) {
      double width = marker.getBBox().getWidth();
      marker.setTranslation(relative.getX() + x + width / 2.0, relative.getY() + y);
      if (legend.getMarkerRenderer() != null) {
        legend.getMarkerRenderer().spriteRenderer(marker, yFieldIndex, legend.getChart().getCurrentStore());
      }
      marker.redraw();
    }
    if (line != null) {
      line.setTranslation(relative.getX() + x, relative.getY() + y);
      if (legend.getLineRenderer() != null) {
        legend.getLineRenderer().spriteRenderer(line, yFieldIndex, legend.getChart().getCurrentStore());
      }
      line.redraw();
    }
  }

  /**
   * Creates all the individual sprites for this legend item.
   */
  private void createLegend() {
    String name = legend.getCurrentLegendTitles().get(yFieldIndex);
    Chart<M> chart = legend.getChart();
    setSurface(chart.getSurface());

    label = new TextSprite();
    add(label);
    chart.addSprite(label);
    label.setText(name);
    label.setX(20);
    label.setY(0);
    label.setFontSize(12);
    if (!series.visibleInLegend(yFieldIndex)) {
      label.setOpacity(0.5);
      on = false;
    }
    label.redraw();
    double height = label.getBBox().getHeight();
    label.setY(label.getY() - (height / 2.0));
    label.redraw();

    if (series instanceof ScatterSeries) {
      boolean markers = true;
      // Line series - display as short line with optional marker in the middle
      if (series instanceof LineSeries) {
        LineSeries<M> lineSeries = (LineSeries<M>) series;
        line = new PathSprite();
        add(line);
        chart.addSprite(line);
        line.addCommand(new MoveTo(-5.5, 0.5));
        line.addCommand(new LineTo(17.5, 0.5));
        line.setStrokeWidth(lineSeries.getStrokeWidth());
        line.setStrokeLineJoin(LineJoin.ROUND);
        line.setStroke(lineSeries.getStroke());
        markers = lineSeries.isShowMarkers();
      } else if (series instanceof RadarSeries) {
        RadarSeries<M> radar = (RadarSeries<M>) series;
        line = new PathSprite();
        add(line);
        chart.addSprite(line);
        line.addCommand(new MoveTo(0.5, 0.5));
        line.addCommand(new LineTo(16.5, 0.5));
        line.setStrokeWidth(radar.getStrokeWidth());
        line.setStrokeLineJoin(LineJoin.ROUND);
        line.setStroke(radar.getStroke());
        markers = radar.isShowMarkers();
      }
      if (markers) {
        ScatterSeries<M> scatter = (ScatterSeries<M>) series;
        marker = scatter.getMarkerConfig().copy();
        add(marker);
        chart.addSprite(marker);
      }
    } else if (series.getLabelConfig() != null && series.getLabelConfig().getSpriteConfig() != null
        && !(series.getLabelConfig().getSpriteConfig() instanceof TextSprite)) {
      marker = series.getLabelConfig().getSpriteConfig().copy();
      add(marker);
      chart.addSprite(marker);
    } else {
      // All other series types - display as filled box
      marker = new RectangleSprite(12, 12, -6, -6);
      add(marker);
      chart.addSprite(marker);
      if (series instanceof MultipleColorSeries) {
        MultipleColorSeries<M> multi = (MultipleColorSeries<M>) series;
        marker.setFill(multi.getColor(yFieldIndex));
      }
    }

    PreciseRectangle bbox = getBBox();
    mask = new RectangleSprite();
    mask.setX(bbox.getX());
    mask.setY(bbox.getY());
    mask.setWidth(bbox.getWidth());
    mask.setHeight(bbox.getHeight());
    mask.setOpacity(0);
    mask.setFillOpacity(0);
    mask.setStrokeOpacity(0);
    add(mask);
    chart.addSprite(mask);
    mask.redraw();
    mask.setCursor("pointer");

    updatePosition(0, 0);
  }

}
