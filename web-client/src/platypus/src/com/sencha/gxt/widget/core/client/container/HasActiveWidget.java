/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import com.google.gwt.user.client.ui.Widget;

/**
 * Implemented by containers that support an active child widget.
 */
public interface HasActiveWidget {

  /**
   * Returns the active widget.
   * 
   * @return the active widget
   */
  Widget getActiveWidget();

  /**
   * Sets the active widget.
   * 
   * @param active the widget
   */
  void setActiveWidget(Widget active);

}
