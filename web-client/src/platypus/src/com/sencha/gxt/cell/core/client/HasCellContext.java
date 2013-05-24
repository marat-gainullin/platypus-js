/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client;

import com.google.gwt.dom.client.Element;

public interface HasCellContext {

  TargetContext getContext(int row, int column);
  
  Element findTargetElement(int row, int column);
  
}
