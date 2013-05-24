/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.box;

import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.form.Field;

/**
 * Abstract base class for message boxes containing an input field.
 */
public abstract class AbstractInputMessageBox extends MessageBox {

  protected Field<String> field;

  protected AbstractInputMessageBox(Field<String> field, String title, String message) {
    super(title, message);

    ComponentHelper.setParent(this, field);

    this.field = field;
    setFocusWidget(field);

    contentAppearance.getContentElement(getElement()).appendChild(field.getElement());
    setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
  }

  /**
   * Returns the input field.
   * 
   * @return the input field
   */
  public Field<String> getField() {
    return field;
  }

  /**
   * Returns the current value of the input field.
   * 
   * @return the value of the input field
   */
  public String getValue() {
    return field.getValue();
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    ComponentHelper.doAttach(field);
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    ComponentHelper.doDetach(field);
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    field.setWidth(getContainerTarget().getWidth(true));
  }

}
