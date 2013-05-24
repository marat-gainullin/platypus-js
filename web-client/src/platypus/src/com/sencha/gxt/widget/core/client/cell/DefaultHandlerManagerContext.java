/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.cell;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.event.shared.HandlerManager;

public class DefaultHandlerManagerContext extends Context implements HandlerManagerContext {

  protected HandlerManager handlerManager;
  
  public DefaultHandlerManagerContext(int index, int column, Object key, HandlerManager handlerManager) {
    super(index, column, key);
    this.handlerManager = handlerManager;
  }
  
  @Override
  public HandlerManager getHandlerManager() {
    return handlerManager;
  }

}
