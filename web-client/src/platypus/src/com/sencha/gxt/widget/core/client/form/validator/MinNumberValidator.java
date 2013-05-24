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

public class MinNumberValidator<N extends Number> extends AbstractValidator<N> {

  public interface MinNumberMessages {
    String numberMinText(double min);
  }

  protected class DefaultMinNumberMessages implements MinNumberMessages {
    public String numberMinText(double min) {
      return DefaultMessages.getMessages().numberField_minText(min);
    }
  }

  protected N minNumber;
  private MinNumberMessages messages;

  public MinNumberValidator(N minNumber) {
    this.minNumber = minNumber;
  }

  public MinNumberMessages getMessages() {
    if (messages == null) {
      messages = new DefaultMinNumberMessages();
    }
    return messages;
  }

  public Number getMinNumber() {
    return minNumber;
  }

  public void setMessages(MinNumberMessages messages) {
    this.messages = messages;
  }

  public void setMinNumber(N minNumber) {
    this.minNumber = minNumber;
  }

  @Override
  public List<EditorError> validate(Editor<N> field, N value) {
    if (value != null && (value.doubleValue() < minNumber.doubleValue())) {
      return createError(field, getMessages().numberMinText(minNumber.doubleValue()), value);
    }
    return null;
  }
}
