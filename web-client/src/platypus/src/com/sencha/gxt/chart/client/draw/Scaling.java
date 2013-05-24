/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw;

/**
 * Represents a two dimensional scaling.
 */
public class Scaling extends Translation {

  protected double centerX;
  protected double centerY;

  /**
   * Creates a zeroed out scaling
   */
  public Scaling() {
  }

  /**
   * Creates a scaling at the given x and y scale.
   * 
   * @param x the scale on the x axis
   * @param y the scale on the y axis
   */
  public Scaling(double x, double y) {
    super(x, y);
  }

  /**
   * Creates a scaling using the given x and y scale and the given origin.
   * 
   * @param x the scale on the x axis
   * @param y the scale on the y axis
   * @param centerX x-coordinate of the origin
   * @param centerY y-coordinate of the origin
   */
  public Scaling(double x, double y, double centerX, double centerY) {
    super(x, y);
    this.centerX = centerX;
    this.centerY = centerY;
  }

  /**
   * Creates a copy of the given scaling.
   * 
   * @param scaling the scaling to be copied
   */
  public Scaling(Scaling scaling) {
    super(scaling);
    if (scaling != null) {
      this.centerX = scaling.centerX;
      this.centerY = scaling.centerY;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Scaling)) {
      return false;
    }
    Scaling scale = (Scaling) obj;
    return (this.x == scale.x) && (this.y == scale.y) && (this.centerX == scale.centerX)
        && (this.centerY == scale.centerY);
  }

  /**
   * Returns the x-coordinate of the origin of the scaling.
   * 
   * @return the x-coordinate of the origin of the scaling
   */
  public double getCenterX() {
    return centerX;
  }

  /**
   * Returns the y-coordinate of the origin of the scaling.
   * 
   * @return the y-coordinate of the origin of the scaling
   */
  public double getCenterY() {
    return centerY;
  }

  /**
   * Sets the x-coordinate of the origin of the scaling.
   * 
   * @param centerX the x-coordinate of the origin of the scaling
   */
  public void setCenterX(double centerX) {
    this.centerX = centerX;
  }

  /**
   * Sets the y-coordinate of the origin of the scaling.
   * 
   * @param centerY the y-coordinate of the origin of the scaling
   */
  public void setCenterY(double centerY) {
    this.centerY = centerY;
  }

}
