/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

/**
 * A class that implements this interface has a width specification. This
 * interface provides access to the width specification without compromising the
 * ability to provide a mock container instance in JRE unit tests.
 */
public interface HasWidth {
  /**
   * Returns the width specification. Values greater than 1 represent width in
   * pixels. Values between 0 and 1 (inclusive) represent a percent of the width
   * of the container. A value of -1 represents the default width of the
   * associated widget. Values less than -1 represent the width of the container
   * minus the absolute value of the widget width.
   * 
   * @return the width specification
   */
  double getWidth();

  /**
   * Sets the width specification. Values greater than 1 represent width in
   * pixels. Values between 0 and 1 (inclusive) represent a percent of the width
   * of the container. A value of -1 represents the default width of the
   * associated widget. Values less than -1 represent the width of the container
   * minus the absolute value of the widget width.
   * 
   * @param width the width specification
   */
  void setWidth(double width);
}
