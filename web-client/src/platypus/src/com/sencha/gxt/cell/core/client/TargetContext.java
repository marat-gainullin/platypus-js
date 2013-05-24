/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Element;

public class TargetContext extends Context {

  protected Element parent;

  public TargetContext(Element parent, int index, int column, Object key) {
    super(index, column, key);
    this.parent = parent;
  }

  public Element getParent() {
    return parent;
  }

}
