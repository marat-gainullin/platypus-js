/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw.sprite;

import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.DrawComponent;
import com.sencha.gxt.chart.client.draw.Matrix;
import com.sencha.gxt.chart.client.draw.Rotation;
import com.sencha.gxt.chart.client.draw.Scaling;
import com.sencha.gxt.chart.client.draw.Surface;
import com.sencha.gxt.chart.client.draw.Translation;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.core.client.util.PreciseRectangle;
import com.sencha.gxt.core.client.util.Util;

/**
 * A sprite is an object rendered on a {@link Surface} in a
 * {@link DrawComponent}.
 * 
 * To be rendered a sprite must first be added to a draw component. Then to
 * update the rendered version after the initial attachment call the sprite's
 * {@link #redraw()}:
 * 
 * <pre>
    RectangleSprite rectangle = new RectangleSprite(100, 100);
    rectangle.setFill(RGB.RED);
    
    DrawComponent draw = new DrawComponent(100,100);
    draw.addSprite(rectangle);
 * </pre>
 * 
 * Sprites can also be redrawn by using the
 * {@link DrawComponent#redrawSurface()} to tell the surface render all sprites
 * currently attached to it.
 */
public abstract class Sprite {

  protected double strokeWidth = Double.NaN;
  private boolean strokeWidthDirty = false;
  protected double fillOpacity = Double.NaN;
  private boolean fillOpacityDirty = false;
  protected double strokeOpacity = Double.NaN;
  private boolean strokeOpacityDirty = false;
  protected double opacity = Double.NaN;
  private boolean opacityDirty = false;
  protected Surface surface;
  protected DrawComponent component;
  protected Color stroke;
  private boolean strokeDirty = false;
  protected Color fill;
  private boolean fillDirty = false;
  private Matrix matrix = new Matrix();
  protected Translation translation = null;
  protected Rotation rotation = null;
  protected Scaling scaling = null;
  protected PreciseRectangle clipRectangle = null;
  private boolean clipRectangleDirty = false;
  protected int zIndex;
  private boolean zIndexDirty = false;
  private boolean transformDirty = false;
  private boolean hiddenDirty = false;
  protected boolean hidden = false;

  /**
   * Creates a sprite and generates its id.
   */
  public Sprite() {
    setZIndex(10);
  }

  /**
   * Creates a copy of the given sprite.
   * 
   * @param sprite the sprite to be copied
   */
  public Sprite(Sprite sprite) {
    update(sprite);
  }

  /**
   * Clears all of the dirty flags on the sprite.
   */
  public void clearDirtyFlags() {
    strokeWidthDirty = false;
    fillOpacityDirty = false;
    strokeOpacityDirty = false;
    opacityDirty = false;
    strokeDirty = false;
    fillDirty = false;
    zIndexDirty = false;
    transformDirty = false;
    clipRectangleDirty = false;
    hiddenDirty = false;
  }

  /**
   * Returns a copy of the sprite.
   * 
   * @return copy of the sprite
   */
  public abstract Sprite copy();

  /**
   * Returns the bounding box of the sprite.
   * 
   * @return the bounding box of the sprite
   */
  public PreciseRectangle getBBox() {
    if (surface != null) {
      return surface.getBBox(this);
    } else {
      return new PreciseRectangle();
    }
  }

  /**
   * Returns the {@link PreciseRectangle} that represents the clipping element.
   * 
   * @return the rectangle that represents the clipping element
   */
  public PreciseRectangle getClipRectangle() {
    return clipRectangle;
  }

  /**
   * Returns the draw component that the sprite is attached.
   * 
   * @return the draw component that the sprite is attached
   */
  public DrawComponent getComponent() {
    return component;
  }

  /**
   * Returns the {@link Color} of the sprite's fill.
   * 
   * @return the color of the sprite's fill
   */
  public Color getFill() {
    return fill;
  }

  /**
   * Returns the opacity of the sprite's fill.
   * 
   * @return the opacity of the sprite's fill
   */
  public double getFillOpacity() {
    return fillOpacity;
  }

  /**
   * Returns the opacity of the sprite.
   * 
   * @return the opacity of the sprite
   */
  public double getOpacity() {
    return opacity;
  }

  /**
   * Returns the {@link Sprite} as a path.
   * 
   * @return the sprite as a path
   */
  public abstract PathSprite getPathSprite();

  /**
   * Returns the {@link Rotation} of the sprite.
   * 
   * @return the rotation of the sprite
   */
  public Rotation getRotation() {
    return rotation;
  }

  /**
   * Returns the {@link Scaling} of the sprite.
   * 
   * @return the scaling of the sprite
   */
  public Scaling getScaling() {
    return scaling;
  }

  /**
   * Returns the {@link Color} of the stroke.
   * 
   * @return the color of the stroke
   */
  public Color getStroke() {
    return stroke;
  }

  /**
   * Returns the opacity of the stroke.
   * 
   * @return the opacity of the stroke
   */
  public double getStrokeOpacity() {
    return strokeOpacity;
  }

  /**
   * Returns the stroke width.
   * 
   * @return the stroke width
   */
  public double getStrokeWidth() {
    return strokeWidth;
  }

  /**
   * Returns the sprite's surface.
   * 
   * @return the sprite's surface
   */
  public Surface getSurface() {
    return surface;
  }

  /**
   * Returns the {@link Translation} of the sprite.
   * 
   * @return the translation of the sprite
   */
  public Translation getTranslation() {
    return translation;
  }

  /**
   * Returns the z-index of the sprite. Determines the order of drawing sprites
   * to the surface.
   * 
   * @return the z-index of the sprite
   */
  public int getZIndex() {
    return zIndex;
  }

  /**
   * Returns true if the clip rectangle changed since the last render.
   * 
   * @return true if the clip rectangle changed since the last render
   */
  public boolean isClipRectangleDirty() {
    return clipRectangleDirty;
  }

  /**
   * Returns true if the sprite changed since the last render.
   * 
   * @return true if the sprite changed since the last render
   */
  public boolean isDirty() {
    return strokeWidthDirty || fillOpacityDirty || strokeOpacityDirty || opacityDirty || strokeDirty || fillDirty
        || zIndexDirty || transformDirty || clipRectangleDirty || hiddenDirty;
  }

  /**
   * Returns true if the fill changed since the last render.
   * 
   * @return true if the fill changed since the last render
   */
  public boolean isFillDirty() {
    return fillDirty;
  }

  /**
   * Returns true if the fill opacity changed since the last render.
   * 
   * @return true if the fill opacity changed since the last render
   */
  public boolean isFillOpacityDirty() {
    return fillOpacityDirty;
  }

  /**
   * Returns true if the sprite is hidden.
   * 
   * @return true if the sprite is hidden
   */
  public boolean isHidden() {
    return hidden;
  }

  /**
   * Returns true if the sprite's hidden value is dirty. A value is dirty if it
   * has been changed since the previous rendering of the sprite.
   * 
   * @return true if the sprite's hidden value is dirty
   */
  public boolean isHiddenDirty() {
    return hiddenDirty;
  }

  /**
   * Returns true if the opacity changed since the last render.
   * 
   * @return true if the opacity changed since the last render
   */
  public boolean isOpacityDirty() {
    return opacityDirty;
  }

  /**
   * Returns true if the stroke changed since the last render.
   * 
   * @return true if the stroke changed since the last render
   */
  public boolean isStrokeDirty() {
    return strokeDirty;
  }

  /**
   * Returns true if the stroke opacity changed since the last render.
   * 
   * @return true if the stroke opacity changed since the last render
   */
  public boolean isStrokeOpacityDirty() {
    return strokeOpacityDirty;
  }

  /**
   * Returns true if the stroke width changed since the last render.
   * 
   * @return true if the stroke widht changed since the last render
   */
  public boolean isStrokeWidthDirty() {
    return strokeWidthDirty;
  }

  /**
   * Returns true if one of the sprite's transform values is dirty. A value is
   * dirty if it has been changed since the previous rendering of the sprite.
   * 
   * @return true if one of the sprite's transform values is dirty
   */
  public boolean isTransformDirty() {
    return transformDirty;
  }

  /**
   * Returns true if the sprite's z-index value is dirty. A value is dirty if it
   * has been changed since the previous rendering of the sprite.
   * 
   * @return true if the sprite's z-index value is dirty
   */
  public boolean isZIndexDirty() {
    return zIndexDirty;
  }

  /**
   * Renders the sprite to its surface.
   */
  public void redraw() {
    if (surface != null) {
      surface.renderSprite(this);
    }
  }

  /**
   * Removes the sprite from its surface.
   */
  public void remove() {
    if (surface != null) {
      surface.deleteSprite(this);
    }
  }

  /**
   * Sets the {@link PreciseRectangle} that represents the clipping element.
   * 
   * @param clipRectangle the rectangle that represents the clipping element
   */
  public void setClipRectangle(PreciseRectangle clipRectangle) {
    if (clipRectangle != this.clipRectangle) {
      this.clipRectangle = clipRectangle;
      clipRectangleDirty = true;
    }
  }

  /**
   * Sets the draw component that the sprite is attached.
   * 
   * @param component the draw component that the sprite is attached
   */
  public void setComponent(DrawComponent component) {
    this.component = component;
  }

  /**
   * Sets the cursor property of the sprite.
   * 
   * @param property cursor property
   */
  public void setCursor(String property) {
    assert surface != null : "Cannot set cursor property unless sprite has been rendered to surface.";
    surface.setCursor(this, property);
  }

  /**
   * Sets the {@link Color} of the sprite's fill.
   * 
   * @param fill the color of the sprite's fill
   */
  public void setFill(Color fill) {
    if (!Util.equalWithNull(this.fill, fill)) {
      this.fill = fill;
      this.fillDirty = true;
    }
  }

  /**
   * Sets the opacity of the sprite's fill.
   * 
   * @param fillOpacity the opacity of the sprite's fill
   */
  public void setFillOpacity(double fillOpacity) {
    if (Double.compare(this.fillOpacity, fillOpacity) != 0) {
      this.fillOpacity = fillOpacity;
      this.fillOpacityDirty = true;
    }
  }

  /**
   * Sets true if the sprite is hidden.
   * 
   * @param hidden true if the sprite is hidden
   */
  public void setHidden(boolean hidden) {
    if (this.hidden != hidden) {
      this.hidden = hidden;
      hiddenDirty = true;
    }
  }

  /**
   * Sets the opacity of the sprite.
   * 
   * @param opacity the opacity of the sprite
   */
  public void setOpacity(double opacity) {
    if (Double.compare(opacity, this.opacity) != 0) {
      this.opacity = opacity;
      opacityDirty = true;
    }
  }

  /**
   * Sets the {@link Rotation} of the sprite using the given angle.
   * 
   * @param degrees the angle of rotation
   */
  public void setRotation(double degrees) {
    setRotation(new Rotation(degrees));
  }

  /**
   * Sets the {@link Rotation} of the sprite using the given coordinates and
   * angle.
   * 
   * @param x the x-coordinate of rotation
   * @param y the y-coordinate of rotation
   * @param degrees the angle of rotation
   */
  public void setRotation(double x, double y, double degrees) {
    setRotation(new Rotation(x, y, degrees));
  }

  /**
   * Sets the {@link Rotation} of the sprite.
   * 
   * @param rotation the rotation of the sprite
   */
  public void setRotation(Rotation rotation) {
    if (!Util.equalWithNull(this.rotation, rotation)) {
      this.rotation = rotation;
      transformed();
    }
  }

  /**
   * Sets the {@link Scaling} using the given scale value.
   * 
   * @param scale the scale value
   */
  public void setScaling(double scale) {
    setScaling(new Scaling(scale, scale));
  }

  /**
   * Sets the {@link Scaling} using the given x and y scale and the given
   * origin.
   * 
   * @param x the scale on the x axis
   * @param y the scale on the y axis
   * @param centerX x-coordinate of the origin
   * @param centerY y-coordinate of the origin
   */
  public void setScaling(double x, double y, double centerX, double centerY) {
    setScaling(new Scaling(x, y, centerX, centerY));
  }

  /**
   * Sets the {@link Scaling} of the sprite.
   * 
   * @param scaling the scaling of the sprite
   */
  public void setScaling(Scaling scaling) {
    if (!Util.equalWithNull(this.scaling, scaling)) {
      this.scaling = scaling;
      transformed();
    }
  }

  /**
   * Sets the {@link Color} of the stroke.
   * 
   * @param stroke the color of the stroke
   */
  public void setStroke(Color stroke) {
    if (!Util.equalWithNull(this.stroke, stroke)) {
      this.stroke = stroke;
      strokeDirty = true;
    }
  }

  /**
   * Sets the opacity of the stroke.
   * 
   * @param strokeOpacity the opacity of the stroke
   */
  public void setStrokeOpacity(double strokeOpacity) {
    if (Double.compare(strokeOpacity, this.strokeOpacity) != 0) {
      this.strokeOpacity = strokeOpacity;
      strokeOpacityDirty = true;
    }
  }

  /**
   * Sets the stroke width.
   * 
   * @param strokeWidth the stroke width
   */
  public void setStrokeWidth(double strokeWidth) {
    if (Double.compare(strokeWidth, this.strokeWidth) != 0) {
      this.strokeWidth = strokeWidth;
      strokeWidthDirty = true;
    }
  }

  /**
   * Sets the sprite's surface.
   * 
   * @param surface the sprite's surface
   */
  public void setSurface(Surface surface) {
    this.surface = surface;
  }

  /**
   * Sets the {@link Translation} of the sprite using the given x and y.
   * 
   * @param x the translation on the x-axis
   * @param y the translation on the y-axis
   */
  public void setTranslation(double x, double y) {
    setTranslation(new Translation(x, y));
  }

  /**
   * Sets the {@link Translation} of the sprite.
   * 
   * @param translation the translation of the sprite
   */
  public void setTranslation(Translation translation) {
    if (!Util.equalWithNull(this.translation, translation)) {
      this.translation = translation;
      transformed();
    }
  }

  /**
   * Sets the z-index of the sprite.
   * 
   * @param zIndex the z-index of the sprite
   */
  public void setZIndex(int zIndex) {
    if (this.zIndex != zIndex) {
      this.zIndex = zIndex;
      zIndexDirty = true;
    }
  }

  /**
   * Returns and caches the calculated transformation matrix.
   * 
   * @return the calculated transformation matrix
   */
  public Matrix transformMatrix() {
    if (rotation == null && scaling == null && translation == null) {
      return null;
    }
    if (isTransformDirty()) {
      matrix = new Matrix();
      if (rotation != null) {
        matrix.rotate(rotation.getDegrees(), rotation.getX(), rotation.getY());
      }
      if (scaling != null) {
        matrix.scale(scaling.getX(), scaling.getY(), scaling.getCenterX(), scaling.getCenterY());
      }
      if (translation != null) {
        matrix.translate(translation.getX(), translation.getY());
      }
    }

    return matrix;
  }

  /**
   * Updates the attributes of the sprite using the given sprite.
   * 
   * @param sprite the sprite attributes to use
   */
  public void update(Sprite sprite) {
    setStrokeWidth(sprite.strokeWidth);
    setFillOpacity(sprite.fillOpacity);
    setStrokeOpacity(sprite.strokeOpacity);
    setOpacity(sprite.opacity);
    setSurface(sprite.surface);
    setStroke(sprite.stroke);
    setFill(sprite.fill);
    if (sprite.translation != null) {
      setTranslation(new Translation(sprite.translation));
    }
    if (sprite.rotation != null) {
      setRotation(new Rotation(sprite.rotation));
    }
    if (sprite.scaling != null) {
      setScaling(new Scaling(sprite.scaling));
    }
    setClipRectangle(sprite.clipRectangle);
    setZIndex(sprite.zIndex);
    setHidden(sprite.hidden);
  }

  protected void setDirty(boolean bool) {
    bool = true;
  }

  /**
   * Called any of the transforms are changed on the sprite.
   */
  private void transformed() {
    this.transformDirty = true;
  }

}
