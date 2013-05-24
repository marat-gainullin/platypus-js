/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.animation;

import com.sencha.gxt.core.client.dom.XElement;

/**
 * Base <code>Effect</code> implementation for elements.
 */
public class BaseEffect implements Effect {

  protected XElement element;

  protected BaseEffect(XElement element) {
    this.element = element;
  }

  public void onCancel() {

  }

  public void onComplete() {

  }

  public void onStart() {

  }

  public void onUpdate(double progress) {

  }

  protected double getValue(double from, double to, double progress) {
    return (from + ((to - from) * progress));
  }

}
