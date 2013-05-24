/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

/**
 * A class that implements this interface has a size specification. This
 * interface provides access to the size specification without compromising the
 * ability to provide a mock container instance in JRE unit tests.
 */
public interface HasSize {
  /**
   * Returns the size specification. Values greater than 1 represent height in
   * pixels. Values between 0 and 1 (inclusive) represent a percent of the
   * height of the container.
   * 
   * @return the size specification
   */
  double getSize();

  /**
   * Sets the size specification. Values greater than 1 represent height in
   * pixels. Values between 0 and 1 (inclusive) represent a percent of the
   * height of the container.
   * 
   * @param size the size specification
   */
  void setSize(double size);
}
