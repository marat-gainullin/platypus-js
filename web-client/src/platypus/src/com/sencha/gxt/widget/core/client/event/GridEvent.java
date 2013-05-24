/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.sencha.gxt.widget.core.client.grid.Grid;

public abstract class GridEvent<H extends EventHandler> extends GwtEvent<H> {
  @Override
  public Grid<?> getSource() {
    return (Grid<?>) super.getSource();
  }
}