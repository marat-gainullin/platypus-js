/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.HasEditorDelegate;
import com.google.gwt.editor.client.HasEditorErrors;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.InvalidEvent;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.HasInvalidHandlers;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.InvalidHandler;
import com.sencha.gxt.widget.core.client.event.ValidEvent;
import com.sencha.gxt.widget.core.client.event.ValidEvent.HasValidHandlers;
import com.sencha.gxt.widget.core.client.event.ValidEvent.ValidHandler;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.error.ErrorHandler;
import com.sencha.gxt.widget.core.client.form.error.SideErrorHandler;

/**
 * Wraps a {@link Widget} so that it can be used like a {@link Field}.
 * 
 * @param <T> the field's data type
 */
public abstract class AdapterField<T> extends SimpleContainer implements IsField<T>, HasInvalidHandlers,
    HasValidHandlers, HasEditorErrors<T>, HasEditorDelegate<T> {

  protected Widget widget;
  protected String forceInvalidText;
  protected boolean preventMark;

  private List<Validator<T>> validators = new ArrayList<Validator<T>>();
  private EditorDelegate<T> delegate;
  private ErrorHandler errorSupport;

  /**
   * Creates an adapter field that wraps the specified widget so that it can be
   * used like a {@link Field}.
   * 
   * @param widget the widget to wrap
   */
  public AdapterField(Widget widget) {
    setWidget(widget);
    setErrorSupport(new SideErrorHandler(this));
  }

  @Override
  public HandlerRegistration addInvalidHandler(InvalidHandler handler) {
    return addHandler(handler, InvalidEvent.getType());
  }

  /**
   * Adds a validator to be invoked when {@link #validateValue(Object)} is
   * invoked.
   * 
   * @param validator the validator to add
   */
  public void addValidator(Validator<T> validator) {
    validators.add(validator);
  }

  @Override
  public HandlerRegistration addValidHandler(ValidHandler handler) {
    return addHandler(handler, ValidEvent.getType());
  }

  /**
   * Clears the value from the field.
   */
  public void clear() {
    boolean restore = preventMark;
    preventMark = true;
    setValue(null);
    preventMark = restore;
    clearInvalid();
  }

  /**
   * Clear any invalid styles / messages for this field.
   */
  public void clearInvalid() {
    if (forceInvalidText != null) {
      forceInvalidText = null;
    }

    errorSupport.clearInvalid();

    fireEvent(new ValidEvent());
  }

  @Override
  public void disable() {
    super.disable();
    if (widget instanceof Component) {
      ((Component) widget).disable();
    }
  }

  @Override
  public void enable() {
    super.enable();
    if (widget instanceof Component) {
      ((Component) widget).enable();
    }
  }

  /**
   * Forces the field to be invalid using the given error message. When using
   * this feature, {@link #clearInvalid()} must be called to clear the error.
   * Also, no other validation logic will execute.
   * 
   * @param msg the error text
   */
  public void forceInvalid(String msg) {
    forceInvalidText = msg;
    markInvalid(msg);
  }

  /**
   * Returns the field's error support instance.
   * 
   * @return the error support
   */
  public ErrorHandler getErrorSupport() {
    return errorSupport;
  }

  /**
   * Returns the field's validators.
   * 
   * @return the validators
   */
  public List<Validator<T>> getValidators() {
    return validators;
  }

  /**
   * Returns whether or not the field value is currently valid.
   * 
   * @return true if valid
   */
  public boolean isValid() {
    return isValid(false);
  }

  @Override
  public boolean isValid(boolean preventMark) {
    if (disabled) {
      return true;
    }
    boolean restore = this.preventMark;
    this.preventMark = preventMark;
    boolean result = validateValue(getValue());
    if (result) {
      // activeErrorMessage = null;
    }
    this.preventMark = restore;
    return result;
  }

  /**
   * Marks this field as invalid. Validation will still run if called again, and
   * the error message will be changed or cleared based on validation. To set a
   * error message that will not be cleared until manually cleared see
   * {@link #forceInvalid(String)}
   * 
   * Calling this will also register an error in the editor, if any.
   * 
   * @param msg the validation message
   */
  public void markInvalid(String msg) {
    if (delegate != null) {
      delegate.recordError(forceInvalidText, getValue(), null);
    }
    markInvalid(Util.<EditorError> createList(new DefaultEditorError(this, msg, getValue())));
  }

  /**
   * Removes a validator from this list of validators that are run when
   * {@link #validateValue(Object)} is invoked.
   * 
   * @param validator the validator to remove
   */
  public void removeValidator(Validator<T> validator) {
    validators.remove(validator);
  }

  /**
   * Resets the current field value to the originally loaded value and clears
   * any validation messages.
   */
  public void reset() {
    boolean restore = preventMark;
    preventMark = true;
    setValue(null);
    preventMark = restore;
    clearInvalid();
  }

  @Override
  public void setDelegate(EditorDelegate<T> delegate) {
    this.delegate = delegate;
  }

  /**
   * Sets the error handler used to mark and query field errors.
   * 
   * @param error the error handler
   */
  public void setErrorSupport(ErrorHandler error) {
    this.errorSupport = error;
  }

  @Override
  public void showErrors(List<EditorError> errors) {
    for (EditorError error : errors) {
      assert error.getEditor() == this;
      error.setConsumed(true);
    }

    if (errors.size() > 0) {
      errorSupport.markInvalid(errors);
    } else {
      clearInvalid();
    }
  }

  /**
   * Validates the field value.
   * 
   * @return <code>true</code> if valid, otherwise <code>false</code>
   */
  public boolean validate() {
    return validate(false);
  }

  /**
   * Validates the field value.
   * 
   * @param preventMark true to not mark the field valid and fire invalid event
   *          when invalid
   * @return <code>true</code> if valid, otherwise <code>false</code>
   */
  public boolean validate(boolean preventMark) {
    if (disabled) {
      clearInvalid();
      return true;
    }
    boolean restore = this.preventMark;
    this.preventMark = preventMark;
    boolean result = validateValue(getValue());
    this.preventMark = restore;
    if (result) {
      clearInvalid();
    }
    return result;
  }

  /**
   * Actual implementation of markInvalid, which bypasses recording an error in
   * the editor peer.
   * 
   * @param msg the validation message
   */
  protected void markInvalid(List<EditorError> msg) {
    if (preventMark) {
      return;
    }

    errorSupport.markInvalid(msg);

    fireEvent(new InvalidEvent(msg));
  }

  @Override
  protected void onFocus(Event event) {
    super.onFocus(event);
    if (widget instanceof Component) {
      ((Component) widget).focus();
    } else {
      widget.getElement().focus();
    }
  }

  protected boolean validateValue(T value) {
    if (forceInvalidText != null) {
      markInvalid(forceInvalidText);
      return false;
    }
    List<EditorError> errors = new ArrayList<EditorError>();
    for (int i = 0; i < validators.size(); i++) {
      List<EditorError> temp = validators.get(i).validate(this, value);
      if (temp != null && temp.size() > 0) {
        errors.addAll(temp);
      }
    }
    if (errors.size() > 0) {
      markInvalid(errors);
      return false;
    }
    return true;
  }

}
