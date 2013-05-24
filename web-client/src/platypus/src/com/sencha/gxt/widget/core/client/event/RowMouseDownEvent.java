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
import com.google.gwt.user.client.Event;
import com.sencha.gxt.widget.core.client.event.RowMouseDownEvent.RowMouseDownHandler;

public final class RowMouseDownEvent extends GridEvent<RowMouseDownHandler> {

  public interface HasRowMouseDownHandlers extends HasHandlers {
    HandlerRegistration addRowMouseDownHandler(RowMouseDownHandler handler);
  }

  public interface RowMouseDownHandler extends EventHandler {
    void onRowMouseDown(RowMouseDownEvent event);
  }

  private static GwtEvent.Type<RowMouseDownHandler> TYPE;

  public static GwtEvent.Type<RowMouseDownHandler> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<RowMouseDownHandler>();
    }
    return TYPE;
  }

  private int rowIndex;
  private int columnIndex;
  private Event event;

  public RowMouseDownEvent(int rowIndex, int columnIndex, Event event) {
    this.rowIndex = rowIndex;
    this.columnIndex = columnIndex;
    this.event = event;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public GwtEvent.Type<RowMouseDownHandler> getAssociatedType() {
    return (GwtEvent.Type) TYPE;
  }

  public int getRowIndex() {
    return rowIndex;
  }
  
  public int getColumnIndex() {
    return columnIndex;
  }

  public Event getEvent() {
    return event;
  }

  @Override
  protected void dispatch(RowMouseDownHandler handler) {
    handler.onRowMouseDown(this);
  }
}