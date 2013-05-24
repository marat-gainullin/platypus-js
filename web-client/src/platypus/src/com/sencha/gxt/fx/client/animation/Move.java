/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.animation;

import com.sencha.gxt.core.client.dom.XElement;

public class Move extends BaseEffect {

  private int fromX, toX;
  private int fromY, toY;

  public Move(XElement el, int x, int y) {
    super(el);
    el.makePositionable();
    fromX = el.getX();
    fromY = el.getY();

    toX = x;
    toY = y;
  }

  @Override
  public void onComplete() {
    super.onComplete();
    element.setXY(toX, toY);
  }

  @Override
  public void onUpdate(double progress) {
    int x = (int) getValue(fromX, toX, progress);
    int y = (int) getValue(fromY, toY, progress);

    element.setXY(x, y);
  }
}
