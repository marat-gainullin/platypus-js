/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared;

/**
 * <code>LabelProvider</code>'s are responsible for returning a label for a given object.
 * 
 * @see PropertyAccess
 * @param <T> the type of the object for which a label will be created
 */
public interface LabelProvider<T> {

  /**
   * Returns a label for the given object. The return value is treated as plain
   * text, and will be escaped before it is drawn.
   * 
   * @param item the object to get a label from
   * @return a string value for the label, to be rendered as plain text.
   */
  String getLabel(T item);
}