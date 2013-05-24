/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * A widget that implements this interface contains a widget in the "west"
 * position. This interface provides access to that widget, if it exists,
 * without compromising the ability to provide a mock container instance in JRE
 * unit tests.
 * 
 * @see BorderLayoutContainer
 */
public interface HasWestWidget {
  /**
   * Returns the west widget or null if one has not been set.
   * 
   * @return the west widget or null if one has not been set
   */
  Widget getWestWidget();

  /**
   * Sets the west widget, replacing any existing est widget.
   * 
   * @param widget the new widget to place in the west position of the container
   */
  void setWestWidget(IsWidget widget);
}
