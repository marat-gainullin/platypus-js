/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client.form;

import com.google.gwt.cell.client.ValueUpdater;

/**
 * The {@code ViewData} for this cell.
 */
public class ViewData {
  /**
   * The last value that was updated.
   */
  private String lastValue;

  /**
   * The current value.
   */
  private String curValue;

  /**
   * Construct a ViewData instance containing a given value.
   * 
   * @param value a String value
   */
  public ViewData(String value) {
    this.lastValue = value;
    this.curValue = value;
  }

  /**
   * Return true if the last and current values of this ViewData object are
   * equal to those of the other object.
   */
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof ViewData)) {
      return false;
    }
    ViewData vd = (ViewData) other;
    return equalsOrNull(lastValue, vd.lastValue) && equalsOrNull(curValue, vd.curValue);
  }

  /**
   * Return the current value of the input element.
   * 
   * @return the current value String
   * @see #setCurrentValue(String)
   */
  public String getCurrentValue() {
    return curValue;
  }

  /**
   * Return the last value sent to the {@link ValueUpdater}.
   * 
   * @return the last value String
   * @see #setLastValue(String)
   */
  public String getLastValue() {
    return lastValue;
  }

  /**
   * Return a hash code based on the last and current values.
   */
  @Override
  public int hashCode() {
    return (lastValue + "_*!@HASH_SEPARATOR@!*_" + curValue).hashCode();
  }

  /**
   * Set the current value.
   * 
   * @param curValue the current value
   * @see #getCurrentValue()
   */
  protected void setCurrentValue(String curValue) {
    this.curValue = curValue;
  }

  /**
   * Set the last value.
   * 
   * @param lastValue the last value
   * @see #getLastValue()
   */
  protected void setLastValue(String lastValue) {
    this.lastValue = lastValue;
  }

  private boolean equalsOrNull(Object a, Object b) {
    return (a != null) ? a.equals(b) : ((b == null) ? true : false);
  }
}