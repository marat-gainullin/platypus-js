/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw;

/**
 * Represents a two dimensional rotation.
 */
public class Rotation extends Translation {

  protected double degrees;

  /**
   * Creates a zeroed out rotation.
   */
  public Rotation() {
  }

  /**
   * Creates a rotation using the given degrees.
   * 
   * @param degrees the degree of rotation
   */
  public Rotation(double degrees) {
    x = 0;
    y = 0;
    this.degrees = degrees;
  }

  /**
   * Creates a rotation using the given axis and degrees.
   * 
   * @param x the x-coordinate of the axis of rotation
   * @param y the y-coordinate of the axis of rotation
   * @param degrees the degree of rotation
   */
  public Rotation(double x, double y, double degrees) {
    super(x, y);
    this.degrees = degrees;
  }

  /**
   * Creates a copy of the given rotation.
   * 
   * @param rotation the rotation to be copied
   */
  public Rotation(Rotation rotation) {
    super(rotation);
    if (rotation != null) {
      this.degrees = rotation.degrees;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Rotation)) {
      return false;
    }
    Rotation rotation = (Rotation) obj;
    return (this.x == rotation.x) && (this.y == rotation.y) && (this.degrees == rotation.degrees);
  }

  /**
   * Returns the degree of rotation.
   * 
   * @return the degree of rotation
   */
  public double getDegrees() {
    return degrees;
  }

  /**
   * Sets the degree of rotation.
   * 
   * @param degrees the degree of rotation
   */
  public void setDegrees(double degrees) {
    this.degrees = degrees;
  }

}
