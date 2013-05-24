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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent.ColumnWidthChangeHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;

public final class ColumnWidthChangeEvent extends ColumnModelEvent<ColumnWidthChangeHandler> {
  public interface ColumnWidthChangeHandler extends EventHandler {
    void onColumnWidthChange(ColumnWidthChangeEvent event);
  }

  public interface HasColumnWidthChangeHandlers extends HasHandlers {
    HandlerRegistration addColumnWidthChangeHandler(ColumnWidthChangeHandler handler);
  }

  private static GwtEvent.Type<ColumnWidthChangeHandler> TYPE;

  public static GwtEvent.Type<ColumnWidthChangeHandler> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<ColumnWidthChangeHandler>();
    }
    return TYPE;
  }

  public ColumnWidthChangeEvent(int index, ColumnConfig<?, ?> columnConfig) {
    super(index, columnConfig);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public GwtEvent.Type<ColumnWidthChangeHandler> getAssociatedType() {
    return (GwtEvent.Type) getType();
  }

  @Override
  protected void dispatch(ColumnWidthChangeHandler handler) {
    handler.onColumnWidthChange(this);
  }
}