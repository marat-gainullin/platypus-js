/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.util.List;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

/**
 * Defines the interface for object that can validate an editor's value.
 *
 * @param <T> the value type
 */
public interface Validator<T> {

  /**
   * Validates the value.
   * 
   * @param editor the editor
   * @param value the value
   * @return the errors if any, otherwise, empty list or null
   */
  List<EditorError> validate(Editor<T> editor, T value);

}