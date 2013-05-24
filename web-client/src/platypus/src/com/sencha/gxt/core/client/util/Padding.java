/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.util;

/**
 * Represents 4-side padding.
 */
public class Padding extends Region {
  /**
   * Creates a new padding instance with 0 values for all sides.
   */
  public Padding() {
    this(0);
  }

  /**
   * Creates a new padding instance.
   * 
   * @param padding the padding value for all 4 sides.
   */
  public Padding(int padding) {
    this(padding, padding, padding, padding);
  }

  /**
   * Creates a new padding instance.
   * 
   * @param top the top padding
   * @param right the right padding
   * @param bottom the bottom padding
   * @param left the left padding
   */
  public Padding(int top, int right, int bottom, int left) {
    super(top, right, bottom, left);
  }
}
