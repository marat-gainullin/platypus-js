/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.text.ParseException;
import java.util.Date;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;

public class DateTimePropertyEditor extends PropertyEditor<Date>{

  protected DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);
  protected boolean parseStrict = true;
  
  /**
   * Creates a new date time property editor.
   */
  public DateTimePropertyEditor() {

  }

  /**
   * Creates a new date time property editor.
   * 
   * @param format the date time format
   */
  public DateTimePropertyEditor(DateTimeFormat format) {
    this.format = format;
  }
  
  /**
   * Creates a new date time property editor.
   * 
   * @param pattern the pattern used to create a new @link
   *          {@link DateTimeFormat}.
   */
  public DateTimePropertyEditor(String pattern) {
    this.format = DateTimeFormat.getFormat(pattern);
  }
  
  /**
   * Returns the date time format.
   * 
   * @return the date time format
   */
  public DateTimeFormat getFormat() {
    return format;
  }

  /**
   * Returns true if parsing strictly.
   * 
   * @return the parse strict state
   */
  public boolean isParseStrict() {
    return parseStrict;
  }

  @Override
  public Date parse(CharSequence text) throws ParseException {
    try {
      if (parseStrict) {
        return format.parseStrict(text.toString());
      } else {
        return format.parse(text.toString());
      }
    } catch (Exception ex) {
      throw new ParseException(ex.getMessage(), 0);
    }
  }

  @Override
  public String render(Date value) {
    return format.format(value);
  }

  /**
   * True to parse dates strictly (defaults to true). See @link
   * {@link DateTimeFormat#parseStrict(String)}.
   * 
   * @param parseStrict true to parse strictly
   */
  public void setParseStrict(boolean parseStrict) {
    this.parseStrict = parseStrict;
  }

}
