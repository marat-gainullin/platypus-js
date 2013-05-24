/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.animation;

/**
 * Effect interface defines the lifecyle methods for an effect.
 */
public interface Effect {

  /**
   * Fires after the effect is cancelled.
   */
  public void onCancel();

  /**
   * Fires after the effect is complete.
   */
  public void onComplete();

  /**
   * Fires after the effect is started.
   */
  public void onStart();

  /**
   * Fires after the effect is updated.
   * 
   * @param progress the progress between 0 and 1
   */
  public void onUpdate(double progress);
}
