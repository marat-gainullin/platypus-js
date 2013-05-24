/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw.sprite;

import java.util.AbstractList;
import java.util.ArrayList;

import com.sencha.gxt.chart.client.draw.Surface;
import com.sencha.gxt.core.client.util.PreciseRectangle;

/**
 * Handles a group of sprites with common methods to a sprite. These methods are
 * applied to the set of sprites added to the group.
 * 
 * @param <S> the type of {@link Sprite} this composite contains
 */
public class SpriteList<S extends Sprite> extends AbstractList<S> {

  private Surface surface;
  private ArrayList<S> sprites = new ArrayList<S>();

  @Override
  public void add(int index, S sprite) {
    sprites.add(index, sprite);
  }

  @Override
  public boolean add(S sprite) {
    return sprites.add(sprite);
  }

  /**
   * Clears all items from the composite and removes them their surface.
   */
  @Override
  public void clear() {
    while (size() > 0) {
      remove(0).remove();
    }
  }

  @Override
  public S get(int index) {
    if (index >= size()) {
      return null;
    }
    return sprites.get(index);
  }

  /**
   * Returns the calculated bounding box of all sprites contained in the
   * composite.
   * 
   * @return the calculated bounding box of all sprites contained in the
   *         composite
   */
  public PreciseRectangle getBBox() {
    double minX = Double.POSITIVE_INFINITY;
    double minY = Double.POSITIVE_INFINITY;
    double maxHeight = Double.NEGATIVE_INFINITY;
    double maxWidth = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < size(); i++) {
      S sprite = get(i);
      PreciseRectangle box = sprite.getBBox();
      minX = Math.min(minX, box.getX());
      minY = Math.min(minY, box.getY());
      maxHeight = Math.max(maxHeight, box.getHeight() + box.getY());
      maxWidth = Math.max(maxWidth, box.getWidth() + box.getX());
    }

    return new PreciseRectangle(minX, minY, maxWidth - minX, maxHeight - minY);
  }

  /**
   * Returns the surface that the composite is attached.
   * 
   * @return the surface that the composite is attached
   */
  public Surface getSurface() {
    return surface;
  }

  /**
   * Hides all the sprites contained in the composite.
   */
  public void hide() {
    for (S sprite : sprites) {
      sprite.setHidden(true);
    }
  }

  @Override
  public S remove(int index) {
    return sprites.remove(index);
  }

  @Override
  public S set(int index, S element) {
    return sprites.set(index, element);
  }

  /**
   * Sets the surface that the composite is attached.
   * 
   * @param surface the surface that the composite is attached
   */
  public void setSurface(Surface surface) {
    this.surface = surface;
  }

  /**
   * Shows all of the sprites contained in the composite.
   */
  public void show() {
    for (S sprite : sprites) {
      sprite.setHidden(false);
    }
  }

  @Override
  public int size() {
    return sprites.size();
  }

}
