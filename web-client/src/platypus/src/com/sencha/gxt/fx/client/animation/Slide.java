/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.animation;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.sencha.gxt.core.client.Style.Direction;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Rectangle;

public abstract class Slide extends BaseEffect {

  protected Direction dir;
  protected double from, to;
  protected Rectangle oBounds;
  protected Overflow overflow;
  protected XElement wrapEl;
  protected int marginTop;
  protected int marginLeft;

  public Slide(XElement el, Direction dir) {
    super(el);
    this.dir = dir;
  }

  public abstract void increase(int v);

  @Override
  public void onComplete() {
    wrapEl.unwrap(element, oBounds);
    element.getStyle().setMarginTop(marginTop, Unit.PX);
    element.getStyle().setMarginLeft(marginLeft, Unit.PX);
    if (overflow != null) {
      element.getStyle().setOverflow(overflow);
    }
    super.onComplete();
  }

  @Override
  public void onUpdate(double progress) {
    int v = (int) getValue(from, to, progress);
    increase(v);
  }

}
