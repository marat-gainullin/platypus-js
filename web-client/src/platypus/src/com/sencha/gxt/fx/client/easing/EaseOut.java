/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.easing;

/**
 * {@link EasingFunction} that produces an easing at the end of the animation.
 */
public class EaseOut implements EasingFunction {

  @Override
  public double func(double n) {
    return Math.pow(n, 0.48);
  }

}
