/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw;

/**
 * Represents a gradient stop.
 */
public class Stop {

  private int offset = 10;
  private Color color = new Color("#fff");
  private double opacity = 1;

  /**
   * Creates a stop with the default values.
   */
  public Stop() {
  }

  /**
   * Creates a stop using the given offset and color.
   * 
   * @param offset the offset of the stop
   * @param color the color of the stop
   */
  public Stop(int offset, Color color) {
    this.offset = offset;
    this.color = color;
  }

  /**
   * Creates a stop using the given offset, color and opacity.
   * 
   * @param offset the offset of the stop
   * @param color the color of the stop
   * @param opacity the opacity of the stop
   */
  public Stop(int offset, Color color, double opacity) {
    this.offset = offset;
    this.color = color;
    this.opacity = opacity;
  }

  /**
   * Returns the color of the stop.
   * 
   * @return the color of the stop
   */
  public Color getColor() {
    return color;
  }

  /**
   * Returns the offset of the stop.
   * 
   * @return the offset of the stop
   */
  public int getOffset() {
    return offset;
  }

  /**
   * Returns the opacity of the stop.
   * 
   * @return the opacity of the stop
   */
  public double getOpacity() {
    return opacity;
  }

  /**
   * Sets the color of the stop.
   * 
   * @param color the color of the stop
   */
  public void setColor(Color color) {
    this.color = color;
  }

  /**
   * Sets the offset of the stop.
   * 
   * @param offset the offset of the stop
   */
  public void setOffset(int offset) {
    this.offset = offset;
  }

  /**
   * Sets the opacity of the stop
   * 
   * @param opacity the opacity of the stop
   */
  public void setOpacity(double opacity) {
    this.opacity = opacity;
  }

}
