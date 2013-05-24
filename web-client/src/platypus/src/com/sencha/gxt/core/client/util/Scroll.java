/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.util;

/**
 * Represents the left and top scroll values.
 */
public class Scroll {

  private int scrollLeft;
  private int scrollTop;

  public Scroll(int scrollLeft, int scrollTop) {
    this.scrollLeft = scrollLeft;
    this.scrollTop = scrollTop;
  }

  /**
   * Returns the scroll left value.
   * 
   * @return the scroll left value
   */
  public int getScrollLeft() {
    return scrollLeft;
  }

  /**
   * Returns the scroll top value.
   * 
   * @return the scroll top value
   */
  public int getScrollTop() {
    return scrollTop;
  }

  /**
   * Sets the scroll left value.
   * 
   * @param scrollLeft the scroll left value
   */
  public void setScrollLeft(int scrollLeft) {
    this.scrollLeft = scrollLeft;
  }

  /**
   * Sets the scroll top value.
   * 
   * @param scrollTop the scroll top value
   */
  public void setScrollTop(int scrollTop) {
    this.scrollTop = scrollTop;
  }
}
