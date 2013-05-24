/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.easing;

/**
 * A one to one {@link EasingFunction}.
 * 
 */
public class Linear implements EasingFunction {

  @Override
  public double func(double n) {
    return n;
  }

}
