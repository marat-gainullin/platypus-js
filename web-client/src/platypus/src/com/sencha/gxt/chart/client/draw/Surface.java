/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.chart.client.draw.engine.SVG;
import com.sencha.gxt.chart.client.draw.engine.VML;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.chart.client.draw.sprite.RectangleSprite;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.SpriteList;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.PreciseRectangle;

/**
 * A Surface is an interface to render methods inside a draw
 * {@link DrawComponent}. A Surface contains methods to render {@link Sprite}s,
 * get bounding boxes of sprites, add sprites to the canvas, initialize other
 * graphic components, etc. One of the most used methods for this class is the
 * {@link #add(Sprite)} method, to add sprites to the surface.
 * 
 * Most of the Surface methods are abstract and they have a concrete
 * implementation in {@link VML} or {@link SVG} engines.
 */
public abstract class Surface {

  /**
   * Instantiates a surface instance via deferred binding. The default engine
   * used is {@link SVG}. If the user agent is IE 6, 7, or 8 {@link VML} will be
   * used.
   * 
   * @param component the element that the surface will be attached
   * @return the instantiated surface
   */
  public static Surface create(DrawComponent component) {
    Surface surface = GWT.create(Surface.class);
    surface.component = component;
    surface.container = component.getElement();
    return surface;
  }

  /**
   * Instantiates a surface instance via deferred binding. The default engine
   * used is {@link SVG}. If the user agent is IE 6, 7, or 8 {@link VML} will be
   * used.
   * 
   * @param component the element that the surface will be attached
   * @param width the width of the surface
   * @param height the height of the surface
   * @return the instantiated surface
   */
  public static Surface create(DrawComponent component, int width, int height) {
    Surface surface = GWT.create(Surface.class);
    surface.component = component;
    surface.container = component.getElement();
    surface.height = height;
    surface.width = width;
    return surface;
  }

  protected XElement surfaceElement;
  protected XElement container;
  protected DrawComponent component;
  protected SpriteList<Sprite> sprites = new SpriteList<Sprite>();
  protected SpriteList<Sprite> spritesToDelete = new SpriteList<Sprite>();
  protected List<Gradient> gradients = new ArrayList<Gradient>();
  protected int width = 500;
  protected int height = 500;
  private RectangleSprite backgroundSprite;

  /**
   * Adds a {@link Sprite} to the surface. The sprite is not rendered to the DOM
   * until {@link #draw()} or {@link Sprite#redraw()} is called.
   * 
   * @param sprite the sprite to be added
   */
  public void add(Sprite sprite) {
    this.sprites.add(sprite);
    sprite.setSurface(this);
  }

  /**
   * Adds the passed {@link Gradient} to the surface.
   * 
   * @param gradient the gradient to be added
   */
  public abstract void addGradient(Gradient gradient);

  /**
   * Removes all sprites from the surface.
   */
  public void clear() {
    sprites.clear();
  }

  /**
   * Deletes the given sprite from the surface.
   * 
   * @param sprite the sprite to be deleted
   */
  public abstract void deleteSprite(Sprite sprite);

  /**
   * Renders the surface to the DOM as well as any sprites already added.
   */
  public void draw() {
    // setup the background of the surface
    if (component.getBackground() != null) {
      if (backgroundSprite == null) {
        backgroundSprite = new RectangleSprite();
        backgroundSprite.setZIndex(0);
        this.sprites.add(0, backgroundSprite);
        backgroundSprite.setSurface(null);
      }
      backgroundSprite.setX(0);
      backgroundSprite.setY(0);
      backgroundSprite.setWidth(Math.max(width, 0));
      backgroundSprite.setHeight(Math.max(height, 0));
      backgroundSprite.setFill(component.getBackground());
    } else if (backgroundSprite != null) {
      deleteSprite(backgroundSprite);
      backgroundSprite = null;
    }
  }
  
  /**
   * Calculates the bounding box of the given sprite.
   * 
   * @param sprite the sprite to be used in the calculation
   * @return bounding box of the sprite
   */
  public PreciseRectangle getBBox(Sprite sprite) {
    PreciseRectangle bbox = null;

    if (sprite instanceof TextSprite) {
      bbox = getBBoxText((TextSprite) sprite);
    } else {
      PathSprite realPath = sprite.getPathSprite();
      bbox = realPath.dimensions();
    }

    if (sprite.getRotation() != null || sprite.getScaling() != null || sprite.getTranslation() != null) {
      PathSprite transPath = new PathSprite(new RectangleSprite(bbox));
      transPath = transPath.map(sprite.transformMatrix());
      bbox = transPath.dimensions();
    }

    return bbox;
  }

  /**
   * Returns the draw component that the surface is attached.
   * 
   * @return the draw component that the surface is attached
   */
  public DrawComponent getComponent() {
    return component;
  }

  /**
   * Returns the height of the surface.
   * 
   * @return the height of the surface
   */
  public int getHeight() {
    return height;
  }

  /**
   * Returns an immutable {@link List} containing all the sprites in the
   * surface. Ideally sprites should be kept track of separate from the surface.
   * This list can differ and may not be in the order added depending on the
   * surface implementation.
   * 
   * @return an immutable {@link List} containing all the sprites in the surface
   */
  public List<Sprite> getSprites() {
    return Collections.unmodifiableList(sprites);
  }

  /**
   * Returns the element of the surface.
   * 
   * @return the element of the surface
   */
  public XElement getSurfaceElement() {
    return surfaceElement;
  }

  /**
   * Returns the width of the surface.
   * 
   * @return the width of the surface
   */
  public int getWidth() {
    return width;
  }

  /**
   * Renders the given sprite to the DOM.
   * 
   * @param sprite the sprite to be rendered
   */
  public abstract void renderSprite(Sprite sprite);

  /**
   * Set the background color of the surface.
   * 
   * @param background the background color
   */
  public void setBackground(Color background) {
    if (backgroundSprite != null) {
      backgroundSprite.setFill(background);
      backgroundSprite.redraw();
    }
  }

  /**
   * Sets the cursor property for the given sprite.
   * 
   * @param sprite the sprite to be set
   * @param property the property to use
   */
  public abstract void setCursor(Sprite sprite, String property);

  /**
   * Sets the height of the surface.
   * 
   * @param height the height of the surface.
   */
  public void setHeight(int height) {
    this.height = height;
    if (surfaceElement != null) {
      surfaceElement.setHeight(height);
    }
  }

  /**
   * Sets the view box of the surface. The view box scales the sprites in the
   * surface.
   * 
   * @param x the x coordinate of the viewbox
   * @param y the y coordinate of the viewbox
   * @param width the width of the viewbox
   * @param height the height of the viewbox
   */
  public abstract void setViewBox(double x, double y, double width, double height);

  /**
   * Sets the width of the surface.
   * 
   * @param width the width of the surface.
   */
  public void setWidth(int width) {
    this.width = width;
    if (surfaceElement != null) {
      surfaceElement.setWidth(width);
    }
  }

  /**
   * Calculates the bounding box of the given text sprite.
   * 
   * @param sprite the text sprite to be used in the calculation
   * @return bounding box of the text sprite
   */
  protected abstract PreciseRectangle getBBoxText(TextSprite sprite);

  /**
   * Renders all of the sprites added to the surface using
   * {@link #renderSprite(Sprite)}.
   */
  protected void renderAll() {
    for (Sprite item : sprites) {
      renderSprite(item);
    }
    if (component.isViewBox() && surfaceElement != null) {
      int temp = sprites.indexOf(backgroundSprite);
      sprites.remove(temp);
      PreciseRectangle bbox = sprites.getBBox();
      sprites.add(temp, backgroundSprite);

      setViewBox(bbox.getX(), bbox.getY(), bbox.getWidth(), bbox.getHeight());
      backgroundSprite.setWidth(bbox.getWidth());
      backgroundSprite.setHeight(bbox.getHeight());

    }
  }

}
