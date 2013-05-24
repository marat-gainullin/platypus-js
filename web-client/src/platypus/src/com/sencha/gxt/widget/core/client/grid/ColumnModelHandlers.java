/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.widget.core.client.event.ColumnMoveEvent.ColumnMoveHandler;
import com.sencha.gxt.widget.core.client.event.ColumnMoveEvent.HasColumnMoveHandlers;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent.ColumnWidthChangeHandler;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent.HasColumnWidthChangeHandlers;
import com.sencha.gxt.widget.core.client.grid.ColumnHeaderChangeEvent.ColumnHeaderChangeHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnHeaderChangeEvent.HasColumnHeaderChangeHandlers;
import com.sencha.gxt.widget.core.client.grid.ColumnHiddenChangeEvent.ColumnHiddenChangeHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnHiddenChangeEvent.HasColumnHiddenChangeHandlers;

public interface ColumnModelHandlers extends ColumnWidthChangeHandler, ColumnMoveHandler, ColumnHiddenChangeHandler,
    ColumnHeaderChangeHandler {

  public interface HasColumnModelHandlers extends HasColumnHiddenChangeHandlers, HasColumnWidthChangeHandlers,
  HasColumnMoveHandlers, HasColumnHeaderChangeHandlers {

    HandlerRegistration addColumnModelHandlers(ColumnModelHandlers handlers);
    
  }
}
