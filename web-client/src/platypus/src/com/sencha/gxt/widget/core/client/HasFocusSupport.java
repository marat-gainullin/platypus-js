/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

/**
 * Implemented by objects that have focus support. It provides access to
 * {@link FocusManagerSupport}, which defines the action taken on tab and back
 * tab and allows components such as containers to be ignored when identifying
 * the next component to receive focus.
 */
public interface HasFocusSupport {
  /**
   * Returns the focus manager support, which defines the action taken on tab
   * and back tab and allows components such as containers to be ignored when
   * identifying the next component to receive focus.
   * 
   * @return the focus manager support
   */
  FocusManagerSupport getFocusSupport();
}
