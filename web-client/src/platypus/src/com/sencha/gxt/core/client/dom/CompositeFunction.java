/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.dom;

import com.google.gwt.dom.client.Element;

/**
 * Interface for functions that can be applied to all the elements of a
 * <code>CompositeElement</code>.
 */
public interface CompositeFunction {

  /**
   * Called for each element in the composite element.
   * 
   * @param elem the child element
   * @param ce the composite element
   * @param index the child index
   */
  public void doFunction(Element elem, CompositeElement ce, int index);

}
