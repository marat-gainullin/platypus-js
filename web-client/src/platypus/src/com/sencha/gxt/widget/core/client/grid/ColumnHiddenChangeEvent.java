/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.sencha.gxt.widget.core.client.event.ColumnModelEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnHiddenChangeEvent.ColumnHiddenChangeHandler;

public final class ColumnHiddenChangeEvent extends ColumnModelEvent<ColumnHiddenChangeHandler> {
  public interface ColumnHiddenChangeHandler extends EventHandler {
    void onColumnHiddenChange(ColumnHiddenChangeEvent event);
  }

  public interface HasColumnHiddenChangeHandlers extends HasHandlers {
    HandlerRegistration addColumnHiddenChangeHandler(ColumnHiddenChangeHandler handler);
  }

  private static GwtEvent.Type<ColumnHiddenChangeHandler> TYPE;

  public static GwtEvent.Type<ColumnHiddenChangeHandler> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<ColumnHiddenChangeHandler>();
    }
    return TYPE;
  }

  public ColumnHiddenChangeEvent(int index, ColumnConfig<?, ?> columnConfig) {
    super(index, columnConfig);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public GwtEvent.Type<ColumnHiddenChangeHandler> getAssociatedType() {
    return (GwtEvent.Type) getType();
  }

  @Override
  protected void dispatch(ColumnHiddenChangeHandler handler) {
    handler.onColumnHiddenChange(this);
  }
}