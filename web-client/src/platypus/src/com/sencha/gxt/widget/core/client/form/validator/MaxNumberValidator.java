/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form.validator;

import java.util.List;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.sencha.gxt.messages.client.DefaultMessages;

public class MaxNumberValidator<N extends Number> extends AbstractValidator<N> {

  public interface MaxNumberMessages {
    String numberMaxText(double min);
  }

  protected class DefaultMaxNumberMessages implements MaxNumberMessages {
    public String numberMaxText(double min) {
      return DefaultMessages.getMessages().numberField_maxText(min);
    }
  }
  
  protected N maxNumber;
  private MaxNumberMessages messages;
  
  public MaxNumberValidator(N maxNumber) {
    this.maxNumber = maxNumber;
  }

  public Number getMaxNumber() {
    return maxNumber;
  }

  public MaxNumberMessages getMessages() {
    if (messages == null) {
      messages = new  DefaultMaxNumberMessages();
    }
    return messages;
  }

  public void setMaxNumber(N maxNumber) {
    this.maxNumber = maxNumber;
  }

  public void setMessages(MaxNumberMessages messages) {
    this.messages = messages;
  }

  @Override
  public List<EditorError> validate(Editor<N> field, N value) {
    if (value != null && value.doubleValue() > maxNumber.doubleValue()) {
      return createError(field, getMessages().numberMaxText(maxNumber.doubleValue()), value);
    }
    return null;
  }
}
