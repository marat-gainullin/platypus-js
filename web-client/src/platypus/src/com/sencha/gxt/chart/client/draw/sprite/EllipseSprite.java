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
 * A {@link Sprite} that represents an ellipse.
 */
public class EllipseSprite extends Sprite {

  private double centerX = Double.NaN;
  private boolean centerXDirty = false;
  private double centerY = Double.NaN;
  private boolean centerYDirty = false;
  private double radiusX = 0;
  private boolean radiusXDirty = false;
  private double radiusY = 0;
  private boolean radiusYDirty = false;

  /**
   * Creates an ellipse with no values.
   */
  public EllipseSprite() {
  }

  /**
   * Creates an ellipse with the given radii.
   * 
   * @param radiusX the radius of the ellipse on its x-axis
   * @param radiusY the radius of the ellipse on its y-axis
   */
  public EllipseSprite(double radiusX, double radiusY) {
    setRadiusX(radiusX);
    setRadiusY(radiusY);
  }

  /**
   * Creates an ellipse with the given radii and center coordinates.
   * 
   * @param radiusX the radius of the ellipse on its x-axis
   * @param radiusY the radius of the ellipse on its y-axis
   * @param centerX the center x-coordinate of the ellipse
   * @param centerY the center y-coordinate of the ellipse
   */
  public EllipseSprite(double radiusX, double radiusY, double centerX, double centerY) {
    setRadiusX(radiusX);
    setRadiusY(radiusY);
    setCenterX(centerX);
    setCenterY(centerY);
  }

  /**
   * Creates a copy of the given ellipse.
   * 
   * @param sprite the sprite to be copied
   */
  public EllipseSprite(EllipseSprite sprite) {
    update(sprite);
  }

  @Override
  public void clearDirtyFlags() {
    super.clearDirtyFlags();
    centerXDirty = false;
    centerYDirty = false;
    radiusXDirty = false;
    radiusYDirty = false;
  }

  @Override
  public EllipseSprite copy() {
    return new EllipseSprite(this);
  }

  /**
   * Returns the center x-coordinate of the ellipse.
   * 
   * @return the center x-coordinate of the ellipse
   */
  public double getCenterX() {
    return centerX;
  }

  /**
   * Returns the center y-coordinate of the ellipse.
   * 
   * @return the center y-coordinate of the ellipse
   */
  public double getCenterY() {
    return centerY;
  }

  @Override
  public PathSprite getPathSprite() {
    return new PathSprite(this);
  }

  /**
   * Returns the radius of the ellipse on its x-axis.
   * 
   * @return the radius of the ellipse on its x-axis.
   */
  public double getRadiusX() {
    return radiusX;
  }

  /**
   * Returns the radius of the ellipse on its y-axis.
   * 
   * @return the radius of the ellipse on its y-axis.
   */
  public double getRadiusY() {
    return radiusY;
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
    return super.isDirty() || centerXDirty || centerYDirty || radiusXDirty || radiusYDirty;
  }

  /**
   * Returns true if the radius x changed since the last render.
   * 
   * @return true if the radius x changed since the last render
   */
  public boolean isRadiusXDirty() {
    return radiusXDirty;
  }

  /**
   * Returns true if the radius y changed since the last render.
   * 
   * @return true if the radius y changed since the last render
   */
  public boolean isRadiusYDirty() {
    return radiusYDirty;
  }

  /**
   * Sets the center x-coordinate of the ellipse.
   * 
   * @param centerX the center x-coordinate of the ellipse
   */
  public void setCenterX(double centerX) {
    if (Double.compare(centerX, this.centerX) != 0) {
      this.centerX = centerX;
      centerXDirty = true;
    }
  }

  /**
   * Sets the center y-coordinate of the ellipse.
   * 
   * @param centerY the center y-coordinate of the ellipse
   */
  public void setCenterY(double centerY) {
    if (Double.compare(centerY, this.centerY) != 0) {
      this.centerY = centerY;
      centerYDirty = true;
    }
  }

  /**
   * Sets the radius of the ellipse on its x-axis. Cannot be negative.
   * 
   * @param radiusX the radius of the ellipse on its x-axis.
   */
  public void setRadiusX(double radiusX) {
    assert Double.compare(radiusX, 0) >= 0 : "Cannot set a negative size.";
    if (Double.compare(radiusX, this.radiusX) != 0) {
      this.radiusX = radiusX;
      radiusXDirty = true;
    }
  }

  /**
   * Sets the radius of the ellipse on its y-axis. Cannot be negative.
   * 
   * @param radiusY the radius of the ellipse on its y-axis.
   */
  public void setRadiusY(double radiusY) {
    assert Double.compare(radiusY, 0) >= 0 : "Cannot set a negative size.";
    if (Double.compare(radiusY, this.radiusY) != 0) {
      this.radiusY = radiusY;
      radiusYDirty = true;
    }
  }

  @Override
  public void update(Sprite sprite) {
    super.update(sprite);
    if (sprite instanceof EllipseSprite) {
      EllipseSprite ellipse = (EllipseSprite) sprite;
      setCenterX(ellipse.centerX);
      setCenterY(ellipse.centerY);
      setRadiusX(ellipse.radiusX);
      setRadiusY(ellipse.radiusY);
    }
  }

}
