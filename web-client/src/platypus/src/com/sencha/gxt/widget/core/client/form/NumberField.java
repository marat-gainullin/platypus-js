/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiConstructor;
import com.sencha.gxt.cell.core.client.form.NumberInputCell;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.event.ParseErrorEvent;
import com.sencha.gxt.widget.core.client.form.validator.MinNumberValidator;

/**
 * A numeric text field that provides automatic keystroke filtering to disallow
 * non-numeric characters, and numeric validation to limit the value to a range
 * of valid numbers.
 * 
 * <p >
 * A NumberPropertyEditor must be passed at construction which is used to
 * convert strings to typed number and from typed numbers to strings.
 * 
 * @param <N> the number type
 */
public class NumberField<N extends Number> extends TwinTriggerField<N> {

  /**
   * Creates a new number field.
   * 
   * @param cell the number input cell
   * @param editor the property editor
   */
  public NumberField(NumberInputCell<N> cell, NumberPropertyEditor<N> editor) {
    super(cell, editor);
    setPropertyEditor(editor);

    setHideTrigger(true);
    redraw();
  }

  /**
   * Creates a new number field.
   * 
   * @param editor the property editor
   */
  @UiConstructor
  public NumberField(NumberPropertyEditor<N> editor) {
    this(new NumberInputCell<N>(editor), editor);
  }

  /**
   * Returns the base characters.
   * 
   * @return the base characters
   */
  public String getBaseChars() {
    return getCell().getBaseChars();
  }

  @Override
  public NumberInputCell<N> getCell() {
    return (NumberInputCell<N>) super.getCell();
  }

  /**
   * Returns the decimal separator.
   * 
   * @return the decimal separator
   */
  public String getDecimalSeparator() {
    return getCell().getDecimalSeparator();
  }

  @Override
  public NumberPropertyEditor<N> getPropertyEditor() {
    return (NumberPropertyEditor<N>) getCell().getPropertyEditor();
  }

  /**
   * Returns true of decimal values are allowed.
   * 
   * @return the allow decimal state
   */
  public boolean isAllowDecimals() {
    return getCell().isAllowDecimals();
  }

  /**
   * Returns true if negative values are allowed.
   * 
   * @return the allow negative value state
   */
  public boolean isAllowNegative() {
    return getCell().isAllowNegative();
  }

  /**
   * Sets whether decimal value are allowed (defaults to true).
   * 
   * @param allowDecimals true to allow decimal values
   */
  public void setAllowDecimals(boolean allowDecimals) {
    getCell().setAllowDecimals(allowDecimals);
  }

  /**
   * Sets whether negative value are allowed to be entered into the field
   * (defaults to true).
   * 
   * <p />
   * Setting this does not add validation for negative values, use
   * {@link MinNumberValidator} for that.
   * 
   * @param allowNegative true to allow negative values
   */
  public void setAllowNegative(boolean allowNegative) {
    getCell().setAllowNegative(allowNegative);
  }

  /**
   * Sets the base set of characters to evaluate as valid numbers (defaults to
   * '0123456789').
   * 
   * @param baseChars the base characters
   */
  public void setBaseChars(String baseChars) {
    getCell().setBaseChars(baseChars);
  }

  /**
   * Sets the decimal separator (defaults to
   * LocaleInfo.getCurrentLocale().getNumberConstants().decimalSeparator()).
   * 
   * @param decimalSeparator the decimal separator
   */
  public void setDecimalSeparator(String decimalSeparator) {
    getCell().setDecimalSeparator(decimalSeparator);
  }

  /**
   * Sets the cell's number formatter.
   * 
   * @param format the format
   */
  public void setFormat(NumberFormat format) {
    getPropertyEditor().setFormat(format);
  }

  @Override
  protected void onCellParseError(ParseErrorEvent event) {
    super.onCellParseError(event);
    String value = event.getException().getMessage();
    String msg = DefaultMessages.getMessages().numberField_nanText(value);
    parseError = msg;
    forceInvalid(msg);
  }

}
