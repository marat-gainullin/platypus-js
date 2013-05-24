/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.animation;

import com.google.gwt.animation.client.Animation;
import com.sencha.gxt.fx.client.easing.Default;
import com.sencha.gxt.fx.client.easing.EasingFunction;

/**
 * Adds additional features and functionality to the {@link Animation} class.
 * 
 * The {@link EasingFunction} replaces the interpolation of the
 * {@link Animation}'s progress.
 */
public abstract class Animator extends Animation {

  private EasingFunction easing = new Default();

  /**
   * Returns the {@link EasingFunction} used in this animation.
   * 
   * @return the {@link EasingFunction} used in this animation
   */
  public EasingFunction getEasing() {
    return easing;
  }

  /**
   * Calls {@link Animation#run(int, double)} and sets the
   * {@link EasingFunction} to be used in the animation.
   */
  public void run(int duration, double startTime, EasingFunction easing) {
    this.easing = easing;
    run(duration, startTime);
  }

  /**
   * Calls {@link Animation#run(int)} and sets the {@link EasingFunction} to be
   * used in the animation.
   */
  public void run(int duration, EasingFunction easing) {
    this.easing = easing;
    run(duration);
  }

  /**
   * Sets the {@link EasingFunction} that the animation will use.
   * 
   * @param easing the {@link EasingFunction} that the animation will use
   */
  public void setEasing(EasingFunction easing) {
    this.easing = easing;
  }

  @Override
  protected double interpolate(double progress) {
    return easing.func(progress);
  }

}
