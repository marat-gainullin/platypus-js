/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.animation;

import com.sencha.gxt.core.client.dom.XElement;

public class SingleStyleEffect extends BaseEffect {
  /**
   * The css style be adjusted.
   */
  public String style;

  /**
   * The start value.
   */
  public double from;

  /**
   * The end value.
   */
  public double to;

  public SingleStyleEffect(XElement el) {
    super(el);
  }

  public SingleStyleEffect(XElement el, String style, double from, double to) {
    this(el);
    this.style = style;
    this.from = from;
    this.to = to;
  }

  public void increase(double value) {
    element.getStyle().setProperty(style, String.valueOf(value));
  }

  public void onUpdate(double progress) {
    increase(getValue(from, to, progress));
  }

}
