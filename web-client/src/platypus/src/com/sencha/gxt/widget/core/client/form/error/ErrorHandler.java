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

/**
 * Defines the interface for objects that can display a field's error.
 * 
 * <p />
 * See the following concrete implementations:
 * <ul>
 * <li>{@link SideErrorHandler}</li>
 * <li>{@link ToolTipErrorHandler}</li>
 * <li>{@link TitleErrorHandler}</li>
 * <li>{@link ElementErrorHandler}</li>
 * </ul>
 */
public interface ErrorHandler {

  void clearInvalid();

  void markInvalid(List<EditorError> errors);

}
