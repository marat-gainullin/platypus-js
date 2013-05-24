/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.sencha.gxt.cell.core.client.form.TextInputCell;

/**
 * A single line input field.
 */
public class TextField extends ValueBaseField<String> {

  /**
   * Creates a new text field.
   */
  public TextField() {
    this(new TextInputCell());
  }

  /**
   * Creates a new text field with the specified cell
   * 
   * @param cell a text input cell that renders the text field
   */
  public TextField(TextInputCell cell) {
    super(cell);
  }

  /**
   * Creates a new text field.
   * 
   * @param cell the input cell
   * @param propertyEditor the property editor
   */
  public TextField(TextInputCell cell, PropertyEditor<String> propertyEditor) {
    this(cell);
    setPropertyEditor(propertyEditor);
  }

}
