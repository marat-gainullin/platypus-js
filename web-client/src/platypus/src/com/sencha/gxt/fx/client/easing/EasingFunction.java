/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.easing;

/**
 * Interface for classes that contain an easing equation.
 * 
 */
public interface EasingFunction {

  /**
   * Returns the calculated easing of the passed in n value.
   * 
   * @param n the value to be eased
   * @return the calculated easing
   */
  public double func(double n);

}
