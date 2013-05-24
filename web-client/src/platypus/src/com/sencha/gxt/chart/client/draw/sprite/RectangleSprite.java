/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw.sprite;

import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.core.client.util.PreciseRectangle;

/**
 * A {@link Sprite} that represents a rectangle.
 */
public class RectangleSprite extends Sprite {

  private double width = 0;
  private boolean widthDirty = false;
  private double height = 0;
  private boolean heightDirty = false;
  private double x = Double.NaN;
  private boolean xDirty = false;
  private double y = Double.NaN;
  private boolean yDirty = false;
  private double radius = Double.NaN;
  private boolean radiusDirty = false;

  /**
   * Creates a rectangle with no values.
   */
  public RectangleSprite() {
  }

  /**
   * Creates a rectangle using the given width and height.
   * 
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   */
  public RectangleSprite(double width, double height) {
    setWidth(width);
    setHeight(height);
  }

  /**
   * Creates a rectangle using the given width, height and coordinates.
   * 
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param x the x-coordinate of the rectangle
   * @param y the y-coordinate of the rectangle
   */
  public RectangleSprite(double width, double height, double x, double y) {
    setWidth(width);
    setHeight(height);
    setX(x);
    setY(y);
  }

  /**
   * Creates a rectangle using the given width, height, coordinates and corner
   * radius.
   * 
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param x the x-coordinate of the rectangle
   * @param y the y-coordinate of the rectangle
   * @param radius the radius of the corners of the rectangle
   */
  public RectangleSprite(double width, double height, double x, double y, double radius) {
    setWidth(width);
    setHeight(height);
    setX(x);
    setY(y);
    setRadius(radius);
  }

  /**
   * Creates a rectangle using the given {@link PreciseRectangle}.
   * 
   * @param rectangle the rectangle to be used by the sprite
   */
  public RectangleSprite(PreciseRectangle rectangle) {
    setWidth(rectangle.getWidth());
    setHeight(rectangle.getHeight());
    setX(rectangle.getX());
    setY(rectangle.getY());
  }

  /**
   * Creates a copy of the given rectangle.
   * 
   * @param sprite the sprite to be copied
   */
  public RectangleSprite(RectangleSprite sprite) {
    update(sprite);
  }

  @Override
  public void clearDirtyFlags() {
    super.clearDirtyFlags();
    widthDirty = false;
    heightDirty = false;
    xDirty = false;
    yDirty = false;
    radiusDirty = false;
  }

  @Override
  public RectangleSprite copy() {
    return new RectangleSprite(this);
  }

  /**
   * Returns the height of the rectangle
   * 
   * @return the height of the rectangle
   */
  public double getHeight() {
    return height;
  }

  @Override
  public PathSprite getPathSprite() {
    return new PathSprite(this);
  }

  /**
   * Returns the radius of the corners of the rectangle
   * 
   * @return the radius of the corners of the rectangle
   */
  public double getRadius() {
    return radius;
  }

  /**
   * Returns the width of the rectangle
   * 
   * @return the width of the rectangle
   */
  public double getWidth() {
    return width;
  }

  /**
   * Returns the x-coordinate of the rectangle.
   * 
   * @return the x-coordinate of the rectangle
   */
  public double getX() {
    return x;
  }

  /**
   * Returns the y-coordinate of the rectangle.
   * 
   * @return the y-coordinate of the rectangle
   */
  public double getY() {
    return y;
  }

  @Override
  public boolean isDirty() {
    return super.isDirty() || widthDirty || heightDirty || xDirty || yDirty || radiusDirty;
  }

  /**
   * Returns true if the height changed since the last render.
   * 
   * @return true if the height changed since the last render
   */
  public boolean isHeightDirty() {
    return heightDirty;
  }

  /**
   * Returns true if the radius changed since the last render.
   * 
   * @return true if the radius changed since the last render
   */
  public boolean isRadiusDirty() {
    return radiusDirty;
  }

  /**
   * Returns true if the width changed since the last render.
   * 
   * @return true if the width changed since the last render
   */
  public boolean isWidthDirty() {
    return widthDirty;
  }

  /**
   * Returns true if the x changed since the last render.
   * 
   * @return true if the x changed since the last render
   */
  public boolean isXDirty() {
    return xDirty;
  }

  /**
   * Returns true if the y changed since the last render.
   * 
   * @return true if the y changed since the last render
   */
  public boolean isYDirty() {
    return yDirty;
  }

  /**
   * Sets the height of the rectangle. Cannot be negative.
   * 
   * @param height the height of the rectangle
   */
  public void setHeight(double height) {
    assert Double.compare(height, 0) >= 0 : "Cannot set a negative size.";
    if (Double.compare(this.height, height) != 0) {
      this.height = height;
      heightDirty = true;
    }
  }

  /**
   * Sets the radius of the corners of the rectangle
   * 
   * @param radius the radius of the corners
   */
  public void setRadius(double radius) {
    if (Double.compare(this.radius, radius) != 0) {
      this.radius = radius;
      radiusDirty = true;
    }
  }

  /**
   * Sets the width of the rectangle. Cannot be negative.
   * 
   * @param width the width of the rectangle
   */
  public void setWidth(double width) {
    assert Double.compare(width, 0) >= 0 : "Cannot set a negative size.";
    if (Double.compare(this.width, width) != 0) {
      this.width = width;
      widthDirty = true;
    }
  }

  /**
   * Sets the x-coordinate of the rectangle.
   * 
   * @param x the x-coordinate of the rectangle
   */
  public void setX(double x) {
    if (Double.compare(this.x, x) != 0) {
      this.x = x;
      xDirty = true;
    }
  }

  /**
   * Sets the y-coordinate of the rectangle.
   * 
   * @param y the y-coordinate of the rectangle
   */
  public void setY(double y) {
    if (Double.compare(this.y, y) != 0) {
      this.y = y;
      yDirty = true;
    }
  }

  /**
   * Returns the rectangle values of the sprite as a {@link PreciseRectangle}.
   * 
   * @return the rectangle values of the sprite as a precise rectangle
   */
  public PreciseRectangle toRectangle() {
    return new PreciseRectangle(x, y, width, height);
  }

  @Override
  public String toString() {
    return new StringBuilder().append(x).append(", ").append(y).append(", ").append(width).append(", ").append(height).toString();
  }

  @Override
  public void update(Sprite sprite) {
    super.update(sprite);
    if (sprite instanceof RectangleSprite) {
      RectangleSprite rect = (RectangleSprite) sprite;
      setWidth(rect.width);
      setHeight(rect.height);
      setX(rect.x);
      setY(rect.y);
      setRadius(rect.radius);
    }
  }

}
