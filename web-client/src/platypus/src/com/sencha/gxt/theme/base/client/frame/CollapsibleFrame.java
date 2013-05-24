/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.frame;

import com.sencha.gxt.core.client.dom.XElement;

/**
 * Interface for frames that can be expanded / collapsed.
 */
public interface CollapsibleFrame extends Frame {

  /**
   * Returns the element who's visibility will be "toggled" for expanding and
   * collapsing.
   * 
   * @param parent the parent element
   * @return the collapse target element
   */
  XElement getCollapseElem(XElement parent);
}
