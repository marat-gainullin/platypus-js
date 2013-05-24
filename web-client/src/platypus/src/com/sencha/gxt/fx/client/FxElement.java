/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client;

import com.sencha.gxt.core.client.Style.Direction;
import com.sencha.gxt.core.client.Style.ScrollDir;
import com.sencha.gxt.core.client.Style.ScrollDirection;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.fx.client.animation.Blink;
import com.sencha.gxt.fx.client.animation.FadeIn;
import com.sencha.gxt.fx.client.animation.FadeOut;
import com.sencha.gxt.fx.client.animation.Fx;
import com.sencha.gxt.fx.client.animation.Move;
import com.sencha.gxt.fx.client.animation.SlideIn;
import com.sencha.gxt.fx.client.animation.SlideOut;

public class FxElement extends XElement {

  /**
   * Not directly instantiable. Subclasses should also define a protected no-arg
   * constructor to prevent client code from directly instantiating the class.
   */
  protected FxElement() {
  }
  
  /**
   * Blinks the element.
   */
  public final void blink() {
    blink(null, 50);
  }

  /**
   * Blinks the element.
   * 
   * @param fx the fx instance
   * @param interval the rate of blink
   */
  public final void blink(Fx fx, int interval) {
    if (fx == null) {
      fx = new Fx();
    }
    fx.run(new Blink(this, interval));
  }

  /**
   * Blinks the element.
   * 
   * @param interval the rate of blink
   */
  public final void blink(int interval) {
    blink(null, interval);
  }
  

  /**
   * Toggles the element visibility using a fade effect. The passed {@link Fx}
   * object will run the animation and fire applicable events.
   */
  public final void fadeToggle() {
    fadeToggle(null);
  }

  /**
   * Toggles the element visibility using a fade effect. The passed {@link Fx}
   * object will run the animation and fire applicable events.
   * 
   * @param fx the {@link Fx} object
   */
  public final void fadeToggle(Fx fx) {
    if (fx == null) {
      fx = new Fx();
    }
    if (!isVisible()) {
      fx.run(new FadeIn(this));
    } else {
      fx.run(new FadeOut(this));
    }
  }
  
  /**
   * Scrolls this element the specified scroll point.
   * 
   * @param side side either "left" for scrollLeft values or "top" for scrollTop
   *          values.
   * @param value the new scroll value
   * @param animate true to animate
   */
  public final void scrollTo(ScrollDirection side, int value, boolean animate) {
    scrollTo(side, value, animate, null);
  }
  

  /**
   * Scrolls this element the specified scroll point.
   * 
   * @param side side either "left" for scrollLeft values or "top" for scrollTop
   *          values.
   * @param value the new scroll value
   * @param animate true to animate
   */
  public final void scrollTo(ScrollDirection side, int value, boolean animate, Fx fx) {
    if (!animate) {
      scrollTo(side, value);
      return;
    }
    ScrollDir dir = ScrollDir.VERTICAL;
    if (side == ScrollDirection.LEFT) {
      dir = ScrollDir.HORIZONTAL;
    }
    
    if(fx == null) {
      fx = new Fx();
    }

    fx.run(new com.sencha.gxt.fx.client.animation.Scroll(this, dir, value));
  }

  /**
   * Sets the elements position in page coordinates and animates using
   * {@link Move}.
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   * @param fx the fx instance
   */
  public final void setXY(int x, int y, Fx fx) {
    if (fx == null) {
      setXY(new Point(x, y));
    } else {
      fx.run(new Move(this, x, y));
    }
  }
  

  /**
   * Slides the element in.
   * 
   * @param direction the direction
   */
  public final void slideIn(Direction direction) {
    slideIn(direction, null);
  }

  /**
   * Slides the element in.
   * 
   * @param direction the direction
   * @param fx the fx config
   */
  public final void slideIn(Direction direction, Fx fx) {
    if (fx == null) {
      fx = new Fx();
    }
    fx.run(new SlideIn(this, direction));
  }

  /**
   * Slides the element out.
   * 
   * @param direction the direction
   */
  public final void slideOut(Direction direction) {
    slideOut(direction, null);
  }

  /**
   * Slides the element out.
   * 
   * @param direction the direction
   * @param fx the fx
   */
  public final void slideOut(Direction direction, Fx fx) {
    if (fx == null) {
      fx = new Fx();
    }
    fx.run(new SlideOut(this, direction));
  }
  
}
