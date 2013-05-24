/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import com.sencha.gxt.core.client.util.Margins;

/**
 * Layout parameter that specifies a widget's margins.
 */
public class MarginData implements HasMargins {
  private Margins margins;

  /**
   * Creates a layout parameter that specifies a widget's margins.
   */
  public MarginData() {
  }

  /**
   * Creates a layout parameter with the specified margins.
   * 
   * @param margins the margins
   */
  public MarginData(int margins) {
    this.margins = new Margins(margins);
  }

  /**
   * Creates a layout parameter with the specified margins.
   * 
   * @param top the top margin
   * @param right the right margin
   * @param bottom the bottom margin
   * @param left the left margin
   */
  public MarginData(int top, int right, int bottom, int left) {
    this.margins = new Margins(top, right, bottom, left);
  }

  /**
   * Creates a layout parameter with the specified margins.
   * 
   * @param margins the margins
   */
  public MarginData(Margins margins) {
    this.margins = margins;
  }

  @Override
  public Margins getMargins() {
    return margins;
  }

  @Override
  public void setMargins(Margins margins) {
    this.margins = margins;

  }

}
