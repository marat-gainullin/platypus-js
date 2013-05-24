/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.easing;

/**
 * {@link EasingFunction} that produces a {@link Bounce} at the start of the
 * animation.
 */
public class BounceIn extends Bounce {

  @Override
  public double func(double n) {
    return 1 - super.func(1 - n);
  }

}
