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
 * A widget that implements this interface contains a widget in the "east"
 * position. This interface provides access to that widget, if it exists,
 * without compromising the ability to provide a mock container instance in JRE
 * unit tests.
 * 
 * @see BorderLayoutContainer
 */
public interface HasEastWidget {
  /**
   * Returns the east widget or null if one has not been set.
   * 
   * @return the east widget or null if one has not been set
   */
  Widget getEastWidget();

  /**
   * Sets the east widget, replacing any existing east widget.
   * 
   * @param widget the new widget to place in the east position of the container
   */
  void setEastWidget(IsWidget widget);
}
