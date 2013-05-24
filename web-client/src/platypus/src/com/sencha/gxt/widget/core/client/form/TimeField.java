/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.sencha.gxt.cell.core.client.form.TimeFieldCell;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.event.ParseErrorEvent;

/**
 * Provides a time input field with a time dropdown and automatic time
 * validation.
 */
public class TimeField extends ComboBox<Date> {

  /**
   * Creates a new time field.
   */
  public TimeField() {
    this(new TimeFieldCell());
  }

  /**
   * Creates a new time field.
   * 
   * @param cell the time field cell
   */
  public TimeField(TimeFieldCell cell) {
    super(cell);
  }

  @Override
  public TimeFieldCell getCell() {
    return (TimeFieldCell) super.getCell();
  }

  /**
   * Returns the date time format.
   * 
   * @return the date time format
   */
  public DateTimeFormat getFormat() {
    return getCell().getFormat();
  }

  /**
   * Returns the number of minutes between each time value.
   * 
   * @return the increment
   */
  public int getIncrement() {
    return getCell().getIncrement();
  }

  /**
   * Returns the field's max value.
   * 
   * @return the max value
   */
  public Date getMaxValue() {
    return getCell().getMaxValue();
  }

  /**
   * Returns the fields minimum value.
   * 
   * @return the min value
   */
  public Date getMinValue() {
    return getCell().getMinValue();
  }

  /**
   * Sets the date time format used to format each entry (defaults to
   * {@link PredefinedFormat#TIME_SHORT}).
   * 
   * @param format the date time format
   */
  public void setFormat(DateTimeFormat format) {
    getCell().setFormat(format);
  }

  /**
   * Sets the number of minutes between each time value in the list (defaults to
   * 15).
   * 
   * @param increment the increment
   */
  public void setIncrement(int increment) {
    getCell().setIncrement(increment);
  }

  /**
   * Sets the field's max value.
   * 
   * @param value the max value
   */
  public void setMaxValue(Date value) {
    getCell().setMaxValue(value);
  }

  /**
   * The minimum allowed time (no default value).
   * 
   * @param value the minimum date
   */
  public void setMinValue(Date value) {
    getCell().setMinValue(value);
  }

  @Override
  protected void onCellParseError(ParseErrorEvent event) {
    super.onCellParseError(event);
    String value = event.getException().getMessage();
    String f = getFormat().getPattern();
    String msg = DefaultMessages.getMessages().dateField_invalidText(value, f);
    parseError = msg;
    forceInvalid(msg);
  }

}
