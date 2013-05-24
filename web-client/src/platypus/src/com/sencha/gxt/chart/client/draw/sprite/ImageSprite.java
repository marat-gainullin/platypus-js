/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw.sprite;

import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.chart.client.draw.path.PathSprite;

/**
 * A {@link Sprite} that represents an image.
 */
public class ImageSprite extends Sprite {

  private double x = Double.NaN;
  private boolean xDirty = false;
  private double y = Double.NaN;
  private boolean yDirty = false;
  private double width = Double.NaN;
  private boolean widthDirty = false;
  private double height = Double.NaN;
  private boolean heightDirty = false;
  private ImageResource resource;
  private boolean resourceDirty = false;

  /**
   * Creates an empty image sprite.
   */
  public ImageSprite() {
  }

  /**
   * Creates an image sprite using the given {@link ImageResource}.
   * 
   * @param resource the image resource
   */
  public ImageSprite(ImageResource resource) {
    setResource(resource);
  }

  /**
   * Creates a copy of the given image sprite.
   * 
   * @param sprite the sprite to be copied
   */
  public ImageSprite(ImageSprite sprite) {
    update(sprite);
  }

  @Override
  public void clearDirtyFlags() {
    xDirty = false;
    yDirty = false;
    widthDirty = false;
    heightDirty = false;
    resourceDirty = false;
  }

  @Override
  public ImageSprite copy() {
    return new ImageSprite(this);
  }

  /**
   * Returns the height of the image sprite.
   * 
   * @return the height of the image sprite
   */
  public double getHeight() {
    return height;
  }

  @Override
  public PathSprite getPathSprite() {
    return new PathSprite(new RectangleSprite(width, height, x, y));
  }

  /**
   * Returns the {@link ImageResource} used by the image sprite.
   * 
   * @return the image resource
   */
  public ImageResource getResource() {
    return resource;
  }

  /**
   * Returns the width of the image sprite.
   * 
   * @return the width of the image sprite
   */
  public double getWidth() {
    return width;
  }

  /**
   * Returns the x-coordinate of the image sprite.
   * 
   * @return the x-coordinate of the image sprite
   */
  public double getX() {
    return x;
  }

  /**
   * Returns the y-coordinate of the image sprite.
   * 
   * @return the y-coordinate of the image sprite
   */
  public double getY() {
    return y;
  }

  @Override
  public boolean isDirty() {
    return super.isDirty() || xDirty || yDirty || widthDirty || heightDirty || resourceDirty;
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
   * Returns true if the resource changed since the last render.
   * 
   * @return true if the resource changed since the last render
   */
  public boolean isResourceDirty() {
    return resourceDirty;
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
   * Sets the height of the image sprite.
   * 
   * @param height the height of the image sprite
   */
  public void setHeight(double height) {
    assert Double.compare(height, 0) >= 0 : "Cannot set a negative size.";
    if (Double.compare(height, this.height) != 0) {
      this.height = height;
      heightDirty = true;
    }
  }

  /**
   * Sets the {@link ImageResource} used by the image sprite.
   * 
   * @param resource the image resource
   */
  public void setResource(ImageResource resource) {
    if (resource != this.resource) {
      this.resource = resource;
      if (resource != null) {
        this.setWidth(resource.getWidth());
        this.setHeight(resource.getHeight());
      } else {
        this.setWidth(Double.NaN);
        this.setHeight(Double.NaN);
      }
      resourceDirty = true;
    }
  }

  /**
   * Sets the width of the image sprite.
   * 
   * @param width the width of the image sprite
   */
  public void setWidth(double width) {
    assert Double.compare(width, 0) >= 0 : "Cannot set a negative size.";
    if (Double.compare(width, this.width) != 0) {
      this.width = width;
      widthDirty = true;
    }
  }

  /**
   * Sets the x-coordinate of the image sprite.
   * 
   * @param x the x-coordinate of the image sprite
   */
  public void setX(double x) {
    if (Double.compare(x, this.x) != 0) {
      this.x = x;
      xDirty = true;
    }
  }

  /**
   * Sets the y-coordinate of the image sprite.
   * 
   * @param y the y-coordinate of the image sprite
   */
  public void setY(double y) {
    if (Double.compare(y, this.y) != 0) {
      this.y = y;
      yDirty = true;
    }
  }

  @Override
  public void update(Sprite sprite) {
    super.update(sprite);
    if (sprite instanceof ImageSprite) {
      ImageSprite image = (ImageSprite) sprite;
      setX(image.x);
      setY(image.y);
      setWidth(image.width);
      setHeight(image.height);
      setResource(image.resource);
    }
  }

}
