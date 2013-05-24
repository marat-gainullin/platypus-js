/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw;

/**
 * Base color class that represents a String color value.
 */
public class Color {

  protected String color;

  /**
   * Represents no color value;
   */
  public static final Color NONE = new Color("none");

  /**
   * Constructs an empty color object.
   */
  public Color() {
  }

  /**
   * Constructs a color using the given literal string.
   * 
   * @param color the color string
   */
  public Color(String color) {
    this.color = color;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof Color)) return false;
    Color other = (Color) obj;
    if (!this.color.equals(other.color)) return false;
    return true;
  }

  /**
   * Returns the literal string of the color.
   * 
   * @return the literal string of the color
   */
  public String getColor() {
    return color;
  }

  /**
   * Manually sets the color string of the color object.
   * 
   * @param color the new literal color string to set
   */
  public void setColor(String color) {
    this.color = color;
  }

  /**
   * Return the color in String format.
   * 
   * @return string value
   */
  @Override
  public String toString() {
    return color;
  }

}
