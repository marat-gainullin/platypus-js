/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.HasEditorDelegate;
import com.google.gwt.editor.client.HasEditorErrors;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.Shared;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasValue;
import com.sencha.gxt.cell.core.client.form.FieldCell;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.core.client.util.KeyNav;
import com.sencha.gxt.widget.core.client.cell.CellComponent;
import com.sencha.gxt.widget.core.client.event.BlurEvent;
import com.sencha.gxt.widget.core.client.event.BlurEvent.BlurHandler;
import com.sencha.gxt.widget.core.client.event.FocusEvent;
import com.sencha.gxt.widget.core.client.event.FocusEvent.FocusHandler;
import com.sencha.gxt.widget.core.client.event.InvalidEvent;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.HasInvalidHandlers;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.InvalidHandler;
import com.sencha.gxt.widget.core.client.event.ValidEvent;
import com.sencha.gxt.widget.core.client.event.ValidEvent.HasValidHandlers;
import com.sencha.gxt.widget.core.client.event.ValidEvent.ValidHandler;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.error.ElementErrorHandler;
import com.sencha.gxt.widget.core.client.form.error.ErrorHandler;
import com.sencha.gxt.widget.core.client.form.error.SideErrorHandler;
import com.sencha.gxt.widget.core.client.form.error.TitleErrorHandler;
import com.sencha.gxt.widget.core.client.form.error.ToolTipErrorHandler;

/**
 * Base class for all cell based fields. Adapts these fields for use as widgets, adding validation
 * features, and the ability to interact with the GWT Editor framework.
 * <p>
 * The Field instance implements several Editor interfaces to participate in the full editing
 * lifecycle.
 * <ul>
 * <li>{@link LeafValueEditor} indicates that this class will have no sub-editors, and can have
 * new values set on it, and retrieved when the driver is flushed.</li>
 * <li>{@link HasEditorDelegate} provides access to the delegate to report validation and parse
 * errors to the driver</li>
 * <li>{@link HasEditorErrors} indicates that the field should be informed when the driver is given
 * an error that applies to the data the field contains, so that it can present it to the user.</li>
 * <li>{@link ValueAwareEditor} is used to learn when the editor driver is flushing values so that
 * current errors can be reported to the error delegate, and collected at the driver</li>
 * </ul>
 * </p>
 * <p>
 * All errors are displayed through a {@link ErrorHandler} instance, whether they arrive from the
 * Cell's parsing, the Field's validation, or from validation errors given to an editor driver.
 * Events are also dispatched when the validity of the field changes.
 * </p>
 * 
 * @param <T> the field's data type
 */
public abstract class Field<T> extends CellComponent<T> implements IsField<T>, HasValue<T>, HasValueChangeHandlers<T>,
    HasName, HasInvalidHandlers, HasValidHandlers, LeafValueEditor<T>, HasEditorErrors<T>, HasEditorDelegate<T>,
    ValueAwareEditor<T> {

  @Shared
  public interface FieldStyles extends CssResource {
    String focus();

    String invalid();
  }

  protected String forceInvalidText;
  protected DelayedTask validationTask;
  protected boolean preventMark;
  protected T originalValue;
  protected boolean hasFocus;
  protected String parseError;
  protected String name;

  private boolean autoValidate;
  private List<Validator<T>> validators = new ArrayList<Validator<T>>();
  private int validationDelay = 250;
  private boolean validateOnBlur = true;
  private EditorDelegate<T> delegate;
  private ErrorHandler errorSupport;
  private List<EditorError> errors;
  private boolean valid = true;

  /**
   * Creates a new field.
   * 
   * @param cell the field cell
   */
  protected Field(FieldCell<T> cell) {
    super(cell);
    addBlurHandler(new BlurHandler() {

      @Override
      public void onBlur(BlurEvent event) {
        onLogicalBlur(event);
      }
    });
    addFocusHandler(new FocusHandler() {

      @Override
      public void onFocus(FocusEvent event) {
        onLogicalFocus(event);
      }
    });

    setErrorSupport(new SideErrorHandler(this));
  }

  @Override
  public HandlerRegistration addInvalidHandler(InvalidHandler handler) {
    return addHandler(handler, InvalidEvent.getType());
  }

  public void addValidator(Validator<T> validator) {
    validators.add(validator);
  }

  @Override
  public HandlerRegistration addValidHandler(ValidHandler handler) {
    return addHandler(handler, ValidEvent.getType());
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }

  /**
   * Clears the value from the field.
   */
  public void clear() {
    boolean restore = preventMark;
    preventMark = true;
    setValue(null, false);
    preventMark = restore;
    clearInvalid();
  }

  /**
   * Clear any invalid styles / messages for this field.
   */
  public void clearInvalid() {
    valid = true;
    getCell().onValid(getElement(), true);
    forceInvalidText = null;
    if (errorSupport != null) {
      errorSupport.clearInvalid();
    }

    fireEvent(new ValidEvent());
  }

  public void finishEditing() {
    getCell().finishEditing(getElement(), getValue(), createContext().getKey(), valueUpdater);
  }

  @Override
  public void flush() {
    if (delegate == null) {
      return;
    }
    if (parseError != null) {
      delegate.recordError(parseError, "", this);
    } else if (forceInvalidText != null) {
      delegate.recordError(forceInvalidText, "", this);
    } else {
      validate();
      if (errors != null) {
        for (EditorError e : errors) {
          delegate.recordError(e.getMessage(), e.getValue(), this);
        }
      }
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

  @Override
  public FieldCell<T> getCell() {
    return (FieldCell<T>) super.getCell();
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
   * Gets the field's name.
   * 
   * @return the field's new name
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Returns true if the value is validate on blur.
   * 
   * @return the validate on blur state
   */
  public boolean getValidateOnBlur() {
    return validateOnBlur;
  }

  /**
   * Returns the field's validation delay in milliseconds.
   * 
   * @return the delay in milliseconds
   */
  public int getValidationDelay() {
    return validationDelay;
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
   * Returns true if the field value is validated on each key press.
   * 
   * @return the auto validate state
   */
  public boolean isAutoValidate() {
    return autoValidate;
  }

  public boolean isEditing() {
    return getCell().isEditing(createContext(), getElement(), getValue());
  }

  /**
   * Returns the read only state.
   * 
   * @return <code>true</code> if read only, otherwise <code>false</code>
   */
  public boolean isReadOnly() {
    return getCell().isReadOnly();
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
      errors = null;
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
   * @param msg the validation message
   */
  public void markInvalid(String msg) {
    markInvalid(Collections.<EditorError>singletonList(new DefaultEditorError(this, msg, getValue())));
  }

  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);
    switch (event.getTypeInt()) {
      case Event.ONKEYUP:
        onKeyUp(event);
        break;
      case Event.ONKEYDOWN:
        onKeyDown(event);
        if (KeyNav.getKeyEvent() == Event.ONKEYDOWN) {
          onNavigationKey(event);
        }
        break;
      case Event.ONKEYPRESS:
        onKeyPress(event);
        if (KeyNav.getKeyEvent() == Event.ONKEYPRESS) {
          onNavigationKey(event);
        }
        break;
    }
  }

  @Override
  public void onPropertyChange(String... paths) {
    // no-op, this is a leaf value editor. Sub-editors could listen though
  }

  /**
   * Removes the validator.
   * 
   * @param validator the validator to remove
   */
  public void removeValidator(Validator<T> validator) {
    validators.remove(validator);
  }

  @Override
  public void reset() {
    boolean restore = preventMark;
    preventMark = true;
    setValue(originalValue, false);
    preventMark = restore;
    clearInvalid();
  }

  /**
   * Sets whether the value is validated on each key press (defaults to false).
   * 
   * @param autoValidate true to validate on each key press
   */
  public void setAutoValidate(boolean autoValidate) {
    if (!this.autoValidate && autoValidate) {
      validationTask = new DelayedTask() {
        @Override
        public void onExecute() {
          doAutoValidate();
        }
      };
    } else if (!autoValidate && validationTask != null) {
      validationTask.cancel();
      validationTask = null;
    }
    this.autoValidate = autoValidate;
  }

  @Override
  public void setDelegate(EditorDelegate<T> delegate) {
    this.delegate = delegate;
  }

  /**
   * Sets the field's error support instance.
   * 
   * <p />
   * See the following concrete implementations:
   * <ul>
   * <li>{@link SideErrorHandler}</li>
   * <li>{@link ToolTipErrorHandler}</li>
   * <li>{@link TitleErrorHandler}</li>
   * <li>{@link ElementErrorHandler}</li>
   * </ul>
   * 
   * @param error the error support
   */
  public void setErrorSupport(ErrorHandler error) {
    this.errorSupport = error;
  }

  @Override
  public void setName(String name) {
    this.name = name;
    getCell().setName(getElement(), name);
  }

  public void setOriginalValue(T value) {
    originalValue = value;
  }

  /**
   * Sets the field's read only state.
   * 
   * @param readOnly the read only state
   */
  public void setReadOnly(boolean readOnly) {
    getCell().setReadOnly(readOnly);
  }

  /**
   * Sets whether the field should validate when it loses focus (defaults to
   * true).
   * 
   * @param validateOnBlur true to validate on blur, otherwise false
   */
  public void setValidateOnBlur(boolean validateOnBlur) {
    this.validateOnBlur = validateOnBlur;
  }

  /**
   * Sets length of time in milliseconds after user input begins until
   * validation is initiated (defaults to 250).
   * 
   * @param validationDelay the delay in milliseconds
   */
  public void setValidationDelay(int validationDelay) {
    this.validationDelay = validationDelay;
  }

  @Override
  public void setValue(T value, boolean fireEvents, boolean redraw) {
    if (parseError != null) {
      parseError = null;
      forceInvalidText = null;
      validate();
    }
    super.setValue(value, fireEvents, redraw);
  }

  @Override
  public void showErrors(List<EditorError> errors) {
    // all errors should apply to this element only

    for (EditorError error : errors) {
      assert error.getEditor() == this;
      // skip the error if sent by a field (this field)
      if (error.getUserData() == this) {
        continue;
      }
      error.setConsumed(true);
    }

    if (errors.size() > 0) {
      markInvalid(errors);
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
   * @return true if valid, otherwise false
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
      errors = null;
    }
    return result;
  }

  protected void doAutoValidate() {
    validate();
  }

  /**
   * Actual implementation of markInvalid, which bypasses recording an error in
   * the editor peer.
   * 
   * @param msg the validation message
   */
  protected void markInvalid(List<EditorError> msg) {
    errors = msg;
    if (preventMark) {
      return;
    }
    valid = false;
    getCell().onValid(getElement(), false);

    if (errorSupport != null) {
      errorSupport.markInvalid(msg);
    }

    fireEvent(new InvalidEvent(msg));
  }

  @Override
  protected void onBlur(Event event) {
    // do nothing
  }

  @Override
  protected void onDetach() {
    super.onDetach();
    getCell().clearViewData(getKey(getValue()));
  }

  @Override
  protected void onDisable() {
    super.onDisable();
    getCell().disable(getElement());
  }

  @Override
  protected void onEnable() {
    super.onEnable();
    getCell().enable(getElement());
  }

  @Override
  protected void onFocus(Event event) {
    // do nothing
  }

  protected void onKeyDown(Event event) {

  }

  protected void onKeyPress(Event event) {

  }

  protected void onKeyUp(Event event) {
    if (autoValidate && validationTask != null) {
      validationTask.delay(validationDelay);
    }
  }

  protected void onLogicalBlur(BlurEvent event) {
    if (hasFocus) {
      hasFocus = false;
      if (validateOnBlur) {
        if (parseError != null) {
          forceInvalidText = null;
          return;
        }
        validate();
      }
    }
  }

  protected void onLogicalFocus(FocusEvent event) {
    if (!hasFocus) {
      hasFocus = true;
      parseError = null;
    }
  }

  /**
   * Called when a "navigation" key is pressed. The navigation keys fire on
   * different events (keydown and keypress) for different browers, this method
   * normailizes this behavior as {@link KeyNav} does.
   * 
   * @param event the event
   */
  protected void onNavigationKey(Event event) {

  }

  @Override
  protected void onRedraw() {
    super.onRedraw();
    if (!valid) {
      getCell().onValid(getElement(), false);
    }
  }

  protected boolean validateValue(T value) {
    if (forceInvalidText != null) {
      markInvalid(forceInvalidText);
      valid = false;
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
      valid = false;
      return false;
    }
    valid = true;
    return true;
  }
}
