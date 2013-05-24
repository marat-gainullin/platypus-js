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
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

public abstract class ColumnModelEvent<H extends EventHandler> extends GwtEvent<H> {
  private final ColumnConfig<?, ?> columnConfig;

  private final int index;

  public ColumnModelEvent(int index, ColumnConfig<?, ?> columnConfig) {
    this.index = index;
    this.columnConfig = columnConfig;
  }

  public ColumnConfig<?, ?> getColumnConfig() {
    return (ColumnConfig<?, ?>) columnConfig;
  }

  public int getIndex() {
    return index;
  }

  @Override
  public ColumnModel<?> getSource() {
    return (ColumnModel<?>) super.getSource();
  }
}