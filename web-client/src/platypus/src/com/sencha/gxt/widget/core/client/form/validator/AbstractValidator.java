/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form.validator;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;

public abstract class AbstractValidator<T> implements Validator<T> {
  
  protected static List<EditorError> createError(EditorError... errors) {
    List<EditorError> list = new ArrayList<EditorError>();
    for (EditorError error : errors) {
      list.add(error);
    }
    return list;
  }
  
  protected static List<EditorError> createError(Editor<?> editor, String message, Object value) {
    return createError(new DefaultEditorError(editor, message, value));
  }
  
  protected static String encodeMessage(String message) {
    return Format.htmlEncode(message == null ? "Invalid Message" : message);
  }
}
