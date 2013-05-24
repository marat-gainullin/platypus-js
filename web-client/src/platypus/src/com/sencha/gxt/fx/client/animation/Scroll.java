/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.animation;

import com.sencha.gxt.core.client.Style.ScrollDir;
import com.sencha.gxt.core.client.dom.XElement;

public class Scroll extends SingleStyleEffect {

  protected ScrollDir dir;

  public Scroll(XElement element, ScrollDir dir, int value) {
    super(element);
    this.dir = dir;
    if (dir == ScrollDir.HORIZONTAL) {
      from = element.getScrollLeft();
      to = value;
    } else if (dir == ScrollDir.VERTICAL) {
      from = element.getScrollTop();
      to = value;
    }
  }

  @Override
  public void increase(double value) {
    if (dir == ScrollDir.HORIZONTAL) {
      element.setScrollLeft((int) value);
    } else if (dir == ScrollDir.VERTICAL) {
      element.setScrollTop((int) value);
    }

  }

  @Override
  public void onComplete() {
    super.onComplete();
    element.setScrollTop((int) to);
  }

}