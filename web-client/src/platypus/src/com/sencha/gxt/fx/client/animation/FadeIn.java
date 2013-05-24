/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.animation;

import com.sencha.gxt.core.client.dom.XElement;

public class FadeIn extends BaseEffect {

  public FadeIn(XElement el) {
    super(el);
  }

  public void onComplete() {
    element.getStyle().setProperty("opacity", "");
  }

  public void onStart() {
    element.getStyle().setOpacity(0);
    element.setVisible(true);
  }

  @Override
  public void onUpdate(double progress) {
    element.getStyle().setOpacity(progress);
  }

}