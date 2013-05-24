/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.animation;

import com.sencha.gxt.core.client.dom.XElement;

public class FadeOut extends BaseEffect {

  public FadeOut(XElement el) {
    super(el);
  }

  public void onComplete() {
    element.setVisible(false);
    element.getStyle().setProperty("opacity", "");
  }

  @Override
  public void onUpdate(double progress) {
    element.getStyle().setOpacity(Math.max(1 - progress, 0));
  }

}