/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.util;


/**
 * Instances of this class represent a rectangle's size.
 */
public class PreciseSize {

  private double width;
  private double height;

  /**
   * Creates a new size instance.
   * 
   * @param width the width
   * @param height the height
   */
  public PreciseSize(double width, double height) {
    this.width = width;
    this.height = height;
  }

  public String toString() {
    return "height: " + height + ", width: " + width;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (int) Math.round(prime * result + height);
    result = (int) Math.round(prime * result + width);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(this instanceof PreciseSize)) return false;
    PreciseSize other = (PreciseSize) obj;
    if (Double.compare(this.getHeight(), other.getHeight()) != 0) return false;
    if (Double.compare(this.getWidth(), other.getWidth()) != 0) return false;
    return true;
  }

  public double getWidth() {
    return width;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight(double height) {
    this.height = height;
  }

}
