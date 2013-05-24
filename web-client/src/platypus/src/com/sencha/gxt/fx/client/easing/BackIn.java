/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.easing;

/**
 * {@link EasingFunction} that at first moves back from the origin.
 */
public class BackIn implements EasingFunction {

  @Override
  public double func(double n) {
    return n * n * ((1.70158 + 1) * n - 1.70158);
  }

}
