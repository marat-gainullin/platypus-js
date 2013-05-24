/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw;

/**
 * Represents an HSV color.
 */
public class HSV extends Hue {

  private double value = 0;

  /**
   * Creates an instance of HSV with default values.
   */
  public HSV() {
  }

  /**
   * Creates an instance of HSV with the given hue, saturation and value
   * 
   * @param hue the hue value
   * @param saturation the saturation value
   * @param value the value value
   */
  public HSV(int hue, int saturation, int value) {
    setHue(hue);
    setSaturation(saturation);
    setValue(value);
  }

  /**
   * Creates an instance of HSV with the given RGB color.
   * 
   * @param rgb the RGB color
   */
  public HSV(RGB rgb) {
    double h = 0, s = 0, v = 0;
    double r = rgb.getRed() / 255.0;
    double g = rgb.getGreen() / 255.0;
    double b = rgb.getBlue() / 255.0;
    double minVal = Math.min(r, Math.min(g, b));
    double maxVal = Math.max(r, Math.max(g, b));
    double delta = maxVal - minVal;

    v = maxVal;
    if (delta == 0) {
      setValue((int) Math.round(v));
    } else {
      s = delta / maxVal;
      double deltaR = (((maxVal - r) / 6) + (delta / 2)) / delta;
      double deltaG = (((maxVal - g) / 6) + (delta / 2)) / delta;
      double deltaB = (((maxVal - b) / 6) + (delta / 2)) / delta;
      if (r == maxVal) {
        h = deltaB - deltaG;
      } else if (g == maxVal) {
        h = (1.0 / 3.0) + deltaR - deltaB;
      } else if (b == maxVal) {
        h = (2.0 / 3.0) + deltaG - deltaR;
      }
      // handle edge cases for hue
      if (h < 0) {
        h += 1;
      }
      if (h > 1) {
        h -= 1;
      }
      setHue((int) Math.round(h * 360));
      setSaturation((int) Math.round(s * 100));
      setValue((int) Math.round(v * 100));
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof HSV)) return false;
    HSV other = (HSV) obj;
    if (hue != other.hue) return false;
    if (saturation != other.saturation) return false;
    if (value != other.value) return false;
    return true;
  }

  @Override
  public String getColor() {
    return (new RGB(this)).getColor();
  }

  /**
   * Returns the value.
   * 
   * @return the value
   */
  public double getValue() {
    return value;
  }

  /**
   * Sets the value.
   * 
   * @param value the value
   */
  public void setValue(double value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return new StringBuilder().append("hsv(").append(hue).append(", ").append(saturation).append(", ").append(value).append(
        ")").toString();
  }

}
