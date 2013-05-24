/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an RGB color.
 */
public class RGB extends Color {

  private int red;
  private int green;
  private int blue;

  /**
   * 0, 0, 0
   */
  public static final RGB BLACK = new RGB(0, 0, 0);

  /**
   * 0, 0, 255
   */
  public static final RGB BLUE = new RGB(0, 0, 255);

  /**
   * 0, 255, 255
   */
  public static final RGB CYAN = new RGB(0, 255, 255);

  /**
   * 169, 169, 169
   */
  public static final RGB DARKGRAY = new RGB(169, 169, 169);

  /**
   * 128, 128, 128
   */
  public static final RGB GRAY = new RGB(128, 128, 128);

  /**
   * 0, 128, 0
   */
  public static final RGB GREEN = new RGB(0, 128, 0);

  /**
   * 211, 211, 211
   */
  public static final RGB LIGHTGRAY = new RGB(211, 211, 211);

  /**
   * 255, 0, 255
   */
  public static final RGB MAGENTA = new RGB(255, 0, 255);

  /**
   * 255, 165, 0
   */
  public static final RGB ORANGE = new RGB(255, 165, 0);

  /**
   * 252, 192, 203
   */
  public static final RGB PINK = new RGB(252, 192, 203);

  /**
   * 128, 0, 128
   */
  public static final RGB PURPLE = new RGB(128, 0, 128);

  /**
   * 255, 0, 0
   */
  public static final RGB RED = new RGB(255, 0, 0);

  /**
   * 255, 255, 0
   */
  public static final RGB YELLOW = new RGB(255, 255, 0);

  /**
   * 255, 255, 255
   */
  public static final RGB WHITE = new RGB(255, 255, 255);

  /**
   * Creates an instance of RGB with default values.
   */
  public RGB() {
  }

  /**
   * Creates an RGB instance using the given {@link HSL} color.
   * 
   * @param hsl the HSL color
   */
  public RGB(HSL hsl) {
    double r = 0;
    double g = 0;
    double b = 0;
    double h = hsl.getHue();
    double s = hsl.getSaturation();
    double l = hsl.getLightness();
    double m = 0;

    if (h == 0 || s == 0) {
      // achromatic
      r = l;
      g = l;
      b = l;
    } else {
      // http://en.wikipedia.org/wiki/HSL_and_HSV#From_HSL
      // C is the chroma
      // X is the second largest widget
      // m is the lightness adjustment
      h /= 60;
      double C = s * (1 - Math.abs(2 * l - 1));
      double X = C * (1 - Math.abs(h - 2 * Math.floor(h / 2) - 1));
      m = l - C / 2;
      switch ((int) Math.floor(h)) {
        case 0:
          r = C;
          g = X;
          b = 0;
          break;
        case 1:
          r = X;
          g = C;
          b = 0;
          break;
        case 2:
          r = 0;
          g = C;
          b = X;
          break;
        case 3:
          r = 0;
          g = X;
          b = C;
          break;
        case 4:
          r = X;
          g = 0;
          b = C;
          break;
        case 5:
          r = C;
          g = 0;
          b = X;
          break;
      }
      r += m;
      g += m;
      b += m;
    }
    setRed((int) Math.round(r * 255));
    setGreen((int) Math.round(g * 255));
    setBlue((int) Math.round(b * 255));
  }

  /**
   * Creates an RGB instance using the given {@link HSV} color.
   * 
   * @param hsv the HSV color
   */
  public RGB(HSV hsv) {
    double h = hsv.getHue() / 360.0;
    double s = hsv.getSaturation() / 100.0;
    double v = hsv.getValue() / 100.0;
    double r, g, b;

    if (s == 0) {
      v *= 255.0;
      setRed((int) Math.round(v));
      setGreen((int) Math.round(v));
      setBlue((int) Math.round(v));
    } else {
      double vh = h * 6.0;
      int vi = (int) vh;
      double v1 = v * (1.0 - s);
      double v2 = v * (1.0 - s * (vh - vi));
      double v3 = v * (1.0 - s * (1 - (vh - vi)));

      switch (vi) {
        case 0:
          r = v;
          g = v3;
          b = v1;
          break;
        case 1:
          r = v2;
          g = v;
          b = v1;
          break;
        case 2:
          r = v1;
          g = v;
          b = v3;
          break;
        case 3:
          r = v1;
          g = v2;
          b = v;
          break;
        case 4:
          r = v3;
          g = v1;
          b = v;
          break;
        default:
          r = v;
          g = v1;
          b = v2;
      }
      setRed((int) Math.round(r * 255.0));
      setGreen((int) Math.round(g * 255.0));
      setBlue((int) Math.round(b * 255.0));
    }
  }

  /**
   * Creates an RGB instance using the given RGB values.
   * 
   * @param red the red value (0..255)
   * @param green the green value (0..255)
   * @param blue the blue value (0..255)
   */
  public RGB(int red, int green, int blue) {
    setRed(red);
    setGreen(green);
    setBlue(blue);
  }

  /**
   * Creates an RGB instance using the given RGB color.
   * 
   * @param rgb the RGB color
   */
  public RGB(RGB rgb) {
    this(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
  }

  /**
   * Creates an RGB instance using the given hexadecimal string.
   * 
   * @param hex supports "#xxx" or "#xxxxxx"
   */
  public RGB(String hex) {
    if (hex.length() > 0 && hex.charAt(0) == '#') {
      if (hex.length() == 7) {
        setRed(Integer.valueOf(hex.substring(1, 3), 16));
        setGreen(Integer.valueOf(hex.substring(3, 5), 16));
        setBlue(Integer.valueOf(hex.substring(5, 7), 16));
      }
      if (hex.length() == 4) {
        setRed(Integer.valueOf(hex.substring(1, 2) + hex.substring(1, 2), 16));
        setGreen(Integer.valueOf(hex.substring(2, 3) + hex.substring(2, 3), 16));
        setBlue(Integer.valueOf(hex.substring(3, 4) + hex.substring(3, 4), 16));
      }
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof RGB)) return false;
    RGB other = (RGB) obj;
    if (blue != other.blue) return false;
    if (green != other.green) return false;
    if (red != other.red) return false;
    return true;
  }

  /**
   * Get the blue widget of the color, in the range 0..255.
   * 
   * @return the blue widget
   */
  public int getBlue() {
    return blue;
  }

  @Override
  public String getColor() {
    return toString();
  }

  /**
   * Return a new color that is darker than this color.
   * 
   * @param factor Darker factor (0..1), default to 0.2
   * @return darker color
   */
  public RGB getDarker(double factor) {
    return this.getLighter(-factor);
  }

  /**
   * Returns the gray value (0 to 255) of the color.
   * 
   * The gray value is calculated using the formula r*0.3 + g*0.59 + b*0.11.
   * 
   * @return the gray value of the color
   */
  public double getGrayScale() {
    // http://en.wikipedia.org/wiki/Grayscale#Converting_color_to_grayscale
    return this.red * 0.3 + this.green * 0.59 + this.blue * 0.11;
  }

  /**
   * Get the green widget of the color, in the range 0..255.
   * 
   * @return the green widget
   */
  public int getGreen() {
    return green;
  }

  /**
   * Return a new color that is lighter than this color.
   * 
   * @param factor Lighter factor (0..1), default to 0.2
   * @return lighter color
   */
  public RGB getLighter(double factor) {
    HSL hsl = new HSL(this);
    hsl.setLightness(Math.min(1.0, Math.max(0.0, hsl.getLightness() + factor)));
    return new RGB(hsl);
  }

  /**
   * Get the red widget of the color, in the range 0..255.
   * 
   * @return the red widget
   */
  public int getRed() {
    return red;
  }

  /**
   * Get the RGB values.
   * 
   * @return list of the rgb values
   */
  public List<Integer> getRGB() {
    List<Integer> rgb = new ArrayList<Integer>();
    rgb.add(red);
    rgb.add(green);
    rgb.add(blue);
    return rgb;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + blue;
    result = prime * result + green;
    result = prime * result + red;
    return result;
  }

  /**
   * Sets the blue value of the RGB color
   * 
   * @param blue the blue value of the RGB color
   */
  public void setBlue(int blue) {
    this.blue = Math.min(255, Math.max(0, blue));
  }

  /**
   * Sets the green value of the RGB color
   * 
   * @param green the green value of the RGB color
   */
  public void setGreen(int green) {
    this.green = Math.min(255, Math.max(0, green));
  }

  /**
   * Sets the red value of the RGB color
   * 
   * @param red the red value of the RGB color
   */
  public void setRed(int red) {
    this.red = Math.min(255, Math.max(0, red));
  }

  @Override
  public String toString() {
    return new StringBuilder().append("rgb(").append(red).append(", ").append(green).append(", ").append(blue).append(
        ")").toString();
  }

}
