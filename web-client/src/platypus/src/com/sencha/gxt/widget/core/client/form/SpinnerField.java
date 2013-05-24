/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.cell.core.client.form.SpinnerFieldCell;
import com.sencha.gxt.messages.client.DefaultMessages;

/**
 * A single line input field with up / down arrows that enable incrementing /
 * decrementing a numeric value. A spinner field uses a number property editor
 * to increment, decrement and format the value. See the nested classes at
 * {@link NumberPropertyEditor} for number property editors you can use with
 * spinner field.
 * 
 * @param <N> the numeric field type
 */
public class SpinnerField<N extends Number> extends TwinTriggerField<N> implements HasBeforeSelectionHandlers<N>,
    HasSelectionHandlers<N> {

  /**
   * The locale-sensitive messages used by this class.
   */
  public interface SpinnerMessages {

    String maxValue(double doubleValue);

    String minValue(double doubleValue);

  }

  protected class DefaultSpinnerMessages implements SpinnerMessages {

    public String maxValue(double max) {
      return DefaultMessages.getMessages().numberField_maxText(max);
    }

    public String minValue(double min) {
      return DefaultMessages.getMessages().numberField_minText(min);
    }

  }

  protected SpinnerMessages messages;

  /**
   * Creates a spinner field with the specified property editor.
   * 
   * @param editor the property editor that increments, decrements and formats
   *          the value.
   */
  public SpinnerField(NumberPropertyEditor<N> editor) {
    this(new SpinnerFieldCell<N>(editor));
  }

  /**
   * Creates a spinner field with the specified cell.
   * 
   * @param cell a numeric cell with up / down arrows that increment / decrement
   *          the value
   */
  public SpinnerField(SpinnerFieldCell<N> cell) {
    super(cell, cell.getPropertyEditor());
    redraw();
  }

  @Override
  public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<N> handler) {
    return addHandler(handler, BeforeSelectionEvent.getType());
  }

  @Override
  public HandlerRegistration addSelectionHandler(SelectionHandler<N> handler) {
    return addHandler(handler, SelectionEvent.getType());
  }

  @Override
  public SpinnerFieldCell<N> getCell() {
    return (SpinnerFieldCell<N>) super.getCell();
  }

  /**
   * Sets the increment value.
   * 
   * @return the increment
   */
  public Number getIncrement(Context context) {
    return getCell().getIncrement(null);
  }

  /**
   * Returns the fields max value.
   * 
   * @return the max value
   */
  public Number getMaxValue() {
    return getCell().getMaxValue(createContext());
  }

  /**
   * Returns the locale-sensitive messages used by this class.
   * 
   * @return the local-sensitive messages used by this class.
   */
  public SpinnerMessages getMessages() {
    if (messages == null) {
      messages = new DefaultSpinnerMessages();
    }
    return messages;
  }

  /**
   * Returns the field's minimum value.
   * 
   * @return the min value
   */
  public Number getMinValue() {
    return getCell().getMinValue(createContext());
  }

  @Override
  public NumberPropertyEditor<N> getPropertyEditor() {
    return (NumberPropertyEditor<N>) super.getPropertyEditor();
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
   * @param allowDecimals true to allow negative values
   */
  public void setAllowDecimals(boolean allowDecimals) {
    getCell().setAllowDecimals(allowDecimals);
  }

  /**
   * Sets whether negative value are allowed.
   * 
   * @param allowNegative true to allow negative values
   */
  public void setAllowNegative(boolean allowNegative) {
    getCell().setAllowNegative(allowNegative);
  }

  /**
   * Sets the increment that should be used (defaults to 1d).
   * 
   * @param increment the increment to set.
   */
  public void setIncrement(N increment) {
    getCell().setIncrement(increment);
  }

  /**
   * Sets the field's max allowable value.
   * 
   * @param maxValue the max value
   */
  public void setMaxValue(N maxValue) {
    getCell().setMaxValue(maxValue);
  }

  /**
   * Sets the spinner field messages.
   * 
   * @param messages the messages
   */
  public void setMessages(SpinnerMessages messages) {
    this.messages = messages;
  }

  /**
   * Sets the field's minimum allowed value.
   * 
   * @param minValue the minimum value
   */
  public void setMinValue(Number minValue) {
    getCell().setMinValue(minValue);
  }

  @Override
  protected boolean validateValue(N value) {
    if (!super.validateValue(value)) {
      return false;
    }

    if (value != null) {
      Number minValue = getCell().getMinValue(createContext());
      Number maxValue = getCell().getMaxValue(createContext());
      assert minValue.doubleValue() <= maxValue.doubleValue();
      if (value.doubleValue() < minValue.doubleValue()) {
        markInvalid(getMessages().minValue(minValue.doubleValue()));
        return false;
      }
      if (value.doubleValue() > maxValue.doubleValue()) {
        markInvalid(getMessages().maxValue(maxValue.doubleValue()));
        return false;
      }
    }
    return true;
  }

}
