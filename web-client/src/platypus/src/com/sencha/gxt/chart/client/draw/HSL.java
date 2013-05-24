/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw;

/**
 * Represent an HSL color.
 */
public class HSL extends Hue {

  private double lightness = 0;

  /**
   * Creates an HSL instance with default values.
   */
  public HSL() {
  }

  /**
   * Creates an HSV instance using the given hue, saturation and lightness.
   * 
   * @param hue the hue value
   * @param saturation the saturation value
   * @param lightness the lightness value
   */
  public HSL(double hue, double saturation, double lightness) {
    setHue(hue);
    setSaturation(saturation);
    setLightness(lightness);
  }

  /**
   * Creates an HSV instance from the given {@link RGB} color.
   * 
   * @param rgb the RGB color
   */
  public HSL(RGB rgb) {
    double r = rgb.getRed() / 255.0;
    double g = rgb.getGreen() / 255.0;
    double b = rgb.getBlue() / 255.0;
    double max = Math.max(r, Math.max(g, b));
    double min = Math.min(r, Math.min(g, b));
    double delta = max - min;
    double h = 0;
    double s = 0;
    double l = 0.5 * (max + min);

    // min==max means achromatic (hue is undefined)
    if (min != max) {
      s = (l < 0.5) ? delta / (max + min) : delta / (2 - max - min);
      if (r == max) {
        h = 60 * (g - b) / delta;
      } else if (g == max) {
        h = 120 + 60 * (b - r) / delta;
      } else {
        h = 240 + 60 * (r - g) / delta;
      }
      if (h < 0) {
        h += 360;
      }
      if (h >= 360) {
        h -= 360;
      }
    }
    hue = h;
    saturation = s;
    lightness = l;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof HSL)) return false;
    HSL other = (HSL) obj;
    if (hue != other.hue) return false;
    if (saturation != other.saturation) return false;
    if (lightness != other.lightness) return false;
    return true;
  }

  @Override
  public String getColor() {
    return (new RGB(this)).getColor();
  }

  /**
   * Returns the lightness value.
   * 
   * @return the lightness value
   */
  public double getLightness() {
    return lightness;
  }

  /**
   * Sets the lightness value.
   * 
   * @param lightness the lightness value
   */
  public void setLightness(double lightness) {
    this.lightness = lightness;
  }

  @Override
  public String toString() {
    return new StringBuilder().append("hsl(").append(hue).append(", ").append(saturation).append(", ").append(lightness).append(
        ")").toString();
  }

}
