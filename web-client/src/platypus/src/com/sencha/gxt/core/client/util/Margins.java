/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.util;

/**
 * Represents 4-side margins.
 */
public class Margins extends Region {

  /**
   * Creates a new margins instance with 0 values for all sides.
   */
  public Margins() {
    this(0);
  }

  /**
   * Creates a new margins instance.
   * 
   * @param margin the margin value for all 4 sides.
   */
  public Margins(int margin) {
    this(margin, margin, margin, margin);
  }

  /**
   * Creates a new margin instance.
   * 
   * @param top the top margin
   * @param right the right margin
   * @param bottom the bottom margin
   * @param left the left margin
   */
  public Margins(int top, int right, int bottom, int left) {
    super(top, right, bottom, left);
  }

}
