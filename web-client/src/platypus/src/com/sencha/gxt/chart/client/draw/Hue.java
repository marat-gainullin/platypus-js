/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw;

/**
 * Abstract color representing hue and saturation values.
 */
public abstract class Hue extends Color {

  protected double hue = 0;
  protected double saturation = 0;

  /**
   * Returns the hue value.
   * 
   * @return the hue value
   */
  public double getHue() {
    return hue;
  }

  /**
   * Returns the saturation value.
   * 
   * @return the saturation value
   */
  public double getSaturation() {
    return saturation;
  }

  /**
   * Sets the hue value.
   * 
   * @param hue the hue value
   */
  public void setHue(double hue) {
    this.hue = hue;
  }

  /**
   * Sets the saturation value.
   * 
   * @param saturation the saturation value
   */
  public void setSaturation(double saturation) {
    this.saturation = saturation;
  }

}
