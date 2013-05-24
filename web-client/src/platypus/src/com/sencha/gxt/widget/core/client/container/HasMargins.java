/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import com.sencha.gxt.core.client.util.Margins;

/**
 * A class that implements this interface has a margin specification. This
 * interface provides access to the margin specification without compromising
 * the ability to provide a mock container instance in JRE unit tests.
 */
public interface HasMargins {
  /**
   * Returns the margin specification.
   * 
   * @return the margin specification
   */
  Margins getMargins();

  /**
   * Sets the margin specification.
   * 
   * @param margins the margin specification
   */
  void setMargins(Margins margins);
}
