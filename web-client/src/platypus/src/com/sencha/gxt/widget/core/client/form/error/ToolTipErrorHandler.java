/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form.error;

import java.util.List;

import com.google.gwt.editor.client.EditorError;
import com.sencha.gxt.widget.core.client.Component;

public class ToolTipErrorHandler implements ErrorHandler {

  protected Component target;
  
  public ToolTipErrorHandler(Component target) {
    this.target = target;
  }

  @Override
  public void clearInvalid() {
    target.hideToolTip();
    if (target.getToolTip() != null) {
      target.getToolTip().disable();
    }
  }

  @Override
  public void markInvalid(List<EditorError> errors) {
    target.setToolTip(errors.get(0).getMessage());
    target.getToolTip().addStyleName("x-form-invalid-tip");
    target.getToolTip().enable();
  }

}