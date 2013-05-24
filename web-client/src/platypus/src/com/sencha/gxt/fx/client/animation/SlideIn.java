/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.animation;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.sencha.gxt.core.client.Style.Direction;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Util;

public class SlideIn extends Slide {

  public SlideIn(XElement el, Direction dir) {
    super(el, dir);
  }

  @Override
  public void increase(int now) {
    int v = now;
    switch (dir) {
      case LEFT:
        wrapEl.setLeft(oBounds.getWidth() - v);
        wrapEl.setWidth(v);
        break;
      case UP:
        wrapEl.setTop((oBounds.getHeight() - v));
        wrapEl.setHeight(v);
        break;
      case DOWN:
        element.getStyle().setMarginTop(-(oBounds.getHeight() - v), Unit.PX);
        wrapEl.setHeight(v);
        break;
      case RIGHT:
        element.getStyle().setMarginLeft(-(oBounds.getWidth() - v), Unit.PX);
        wrapEl.setWidth(v);
        break;
    }
  }

  @Override
  public void onStart() {
    overflow = Util.parseOverflow(element.getStyle().getOverflow());
    marginTop = Util.parseInt(element.getStyle().getMarginTop(), 0);
    marginLeft = Util.parseInt(element.getStyle().getMarginLeft(), 0);

    wrapEl = XElement.as(DOM.createDiv());
    oBounds = element.wrap(wrapEl);

    int h = oBounds.getHeight();
    int w = oBounds.getWidth();

    wrapEl.setSize(w, h);
    element.setVisible(true);
    wrapEl.setVisible(true);

    switch (dir) {
      case DOWN:
        wrapEl.setHeight(1);
        from = 1;
        to = oBounds.getHeight();
        break;
      case RIGHT:
        wrapEl.setWidth(1);
        from = 1;
        to = oBounds.getWidth();
        break;
      case LEFT:
        wrapEl.setWidth(1);
        from = 1;
        to = oBounds.getWidth();
        break;
      case UP:
        wrapEl.setHeight(1);
        from = 1;
        to = oBounds.getHeight();
    }
  }
}
