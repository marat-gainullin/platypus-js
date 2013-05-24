/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw;

/**
 * Represents a two dimensional translation.
 */
public class Translation {
  protected double x;
  protected double y;

  /**
   * Creates a zeroed out translation.
   */
  public Translation() {
  }

  /**
   * Creates a translation using the given x and y values.
   * 
   * @param x translation on the x axis
   * @param y translation on the y axis
   */
  public Translation(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Creates a copy of the given translation.
   * 
   * @param translation the translation to be copied
   */
  public Translation(Translation translation) {
    if (translation != null) {
      this.x = translation.x;
      this.y = translation.y;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Translation)) {
      return false;
    }
    Translation translation = (Translation) obj;
    return (this.x == translation.x) && (this.y == translation.y);
  }

  /**
   * Returns the translation on the x axis.
   * 
   * @return the translation on the x axis
   */
  public double getX() {
    return x;
  }

  /**
   * Returns the translation on the y axis.
   * 
   * @return the translation on the y axis
   */
  public double getY() {
    return y;
  }

  /**
   * Sets the translation on the x axis.
   * 
   * @param x the translation on the x axis
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * Sets the translation on the y axis.
   * 
   * @param y the translation on the y axis
   */
  public void setY(double y) {
    this.y = y;
  }

  @Override
  public String toString() {
    return new StringBuilder().append("x:").append(x).append(", y:").append(y).toString();
  }

}
