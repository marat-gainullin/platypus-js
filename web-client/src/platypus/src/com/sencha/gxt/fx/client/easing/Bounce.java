/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.easing;

/**
 * {@link EasingFunction} to simulate a bounce.
 */
public abstract class Bounce implements EasingFunction {
  @Override
  public double func(double n) {
    double s = 7.5625, p = 2.75, l;
    if (n < (1 / p)) {
      l = s * n * n;
    } else {
      if (n < (2 / p)) {
        n -= (1.5 / p);
        l = s * n * n + 0.75;
      } else {
        if (n < (2.5 / p)) {
          n -= (2.25 / p);
          l = s * n * n + 0.9375;
        } else {
          n -= (2.625 / p);
          l = s * n * n + 0.984375;
        }
      }
    }
    return l;
  }
}
