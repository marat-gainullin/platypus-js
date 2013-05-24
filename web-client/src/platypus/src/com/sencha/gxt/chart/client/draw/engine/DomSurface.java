/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw.engine;

import java.util.HashMap;
import java.util.Map;

import com.sencha.gxt.chart.client.draw.Surface;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.core.client.dom.XElement;

public abstract class DomSurface extends Surface {

  protected Map<Sprite, XElement> elements = new HashMap<Sprite, XElement>();
  protected Map<Sprite, XElement> clipElements = new HashMap<Sprite, XElement>();
  protected Map<Sprite, String> spriteIds = new HashMap<Sprite, String>();

  public void setId(Sprite sprite, String id) {
    spriteIds.put(sprite, id);
  }

  /**
   * Returns the clip element associated with the given sprite.
   * 
   * @param sprite the sprite
   * @return the clip element
   */
  protected XElement getClipElement(Sprite sprite) {
    return clipElements.get(sprite);
  }

  /**
   * Returns the DOM element associated with the given sprite.
   * 
   * @param sprite the sprite
   * @return the DOM element
   */
  protected XElement getElement(Sprite sprite) {
    return elements.get(sprite);
  }

  /**
   * Associates the given sprite with the given clip element.
   * 
   * @param sprite the sprite
   * @param element the clip element
   */
  protected void setClipElement(Sprite sprite, XElement element) {
    clipElements.put(sprite, element);
  }

  /**
   * Associates the given sprite with the given dom element.
   * 
   * @param sprite the sprite
   * @param element the dom element
   */
  protected void setElement(Sprite sprite, XElement element) {
    elements.put(sprite, element);
  }
}
