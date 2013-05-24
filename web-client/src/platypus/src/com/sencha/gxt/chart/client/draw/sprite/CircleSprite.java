/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw.sprite;

import com.sencha.gxt.chart.client.draw.path.PathSprite;

/**
 * A {@link Sprite} that represents a circle.
 */
public class CircleSprite extends Sprite {

  private double centerX = Double.NaN;
  private boolean centerXDirty = false;
  private double centerY = Double.NaN;
  private boolean centerYDirty = false;
  private double radius = 0;
  private boolean radiusDirty = false;

  /**
   * Creates a circle with no values.
   */
  public CircleSprite() {
  }

  /**
   * Creates a copy of the given circle.
   * 
   * @param sprite the sprite to be copied
   */
  public CircleSprite(CircleSprite sprite) {
    update(sprite);
  }

  /**
   * Creates a circle with the given radius.
   * 
   * @param radius the radius of the circle
   */
  public CircleSprite(double radius) {
    setRadius(radius);
  }

  /**
   * Creates a circle with the given radius and center coordinates.
   * 
   * @param radius the radius of the circle
   * @param centerX the center x-coordinate of the circle
   * @param centerY the center y-coordinate of the circle
   */
  public CircleSprite(double radius, double centerX, double centerY) {
    setCenterX(centerX);
    setCenterY(centerY);
    setRadius(radius);
  }

  @Override
  public void clearDirtyFlags() {
    super.clearDirtyFlags();
    centerXDirty = false;
    centerYDirty = false;
    radiusDirty = false;
  }

  @Override
  public CircleSprite copy() {
    return new CircleSprite(this);
  }

  /**
   * Returns the center x-coordinate of the circle.
   * 
   * @return the center x-coordinate of the circle
   */
  public double getCenterX() {
    return centerX;
  }

  /**
   * Returns the center y-coordinate of the circle.
   * 
   * @return the center y-coordinate of the circle
   */
  public double getCenterY() {
    return centerY;
  }

  @Override
  public PathSprite getPathSprite() {
    return new PathSprite(this);
  }

  /**
   * Returns the radius of the circle.
   * 
   * @return the radius of the circle
   */
  public double getRadius() {
    return radius;
  }

  /**
   * Returns true if the center x changed since the last render.
   * 
   * @return true if the center x changed since the last render
   */
  public boolean isCenterXDirty() {
    return centerXDirty;
  }

  /**
   * Returns true if the center y changed since the last render.
   * 
   * @return true if the center y changed since the last render
   */
  public boolean isCenterYDirty() {
    return centerYDirty;
  }

  @Override
  public boolean isDirty() {
    return super.isDirty() || centerXDirty || centerYDirty || radiusDirty;
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
   * Sets the center x-coordinate of the circle.
   * 
   * @param centerX the center x-coordinate of the circle
   */
  public void setCenterX(double centerX) {
    if (Double.compare(centerX, this.centerX) != 0) {
      this.centerX = centerX;
      centerXDirty = true;
    }
  }

  /**
   * Sets the center y-coordinate of the circle.
   * 
   * @param centerY the center y-coordinate of the circle
   */
  public void setCenterY(double centerY) {
    if (Double.compare(centerY, this.centerY) != 0) {
      this.centerY = centerY;
      centerYDirty = true;
    }
  }

  /**
   * Sets the radius of the circle. Cannot be negative.
   * 
   * @param radius the radius of the circle
   */
  public void setRadius(double radius) {
    assert Double.compare(radius, 0) >= 0 : "Cannot set a negative size.";
    if (Double.compare(radius, this.radius) != 0) {
      this.radius = radius;
      radiusDirty = true;
    }
  }

  @Override
  public void update(Sprite sprite) {
    super.update(sprite);
    if (sprite instanceof CircleSprite) {
      CircleSprite circle = (CircleSprite) sprite;
      setCenterX(circle.centerX);
      setCenterY(circle.centerY);
      setRadius(circle.radius);
    }
  }

}
