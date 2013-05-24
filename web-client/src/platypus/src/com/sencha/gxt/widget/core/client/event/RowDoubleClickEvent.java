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
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent.RowDoubleClickHandler;

public final class RowDoubleClickEvent extends GridEvent<RowDoubleClickHandler> {

  public interface HasRowDoubleClickHandlers extends HasHandlers {
    HandlerRegistration addRowDoubleClickHandler(RowDoubleClickHandler handler);
  }

  public interface RowDoubleClickHandler extends EventHandler {
    void onRowDoubleClick(RowDoubleClickEvent event);
  }

  private static GwtEvent.Type<RowDoubleClickHandler> TYPE;

  public static GwtEvent.Type<RowDoubleClickHandler> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<RowDoubleClickHandler>();
    }
    return TYPE;
  }

  private int rowIndex;
  private int columnIndex = -1;
  private Event event;

  public RowDoubleClickEvent(int rowIndex, int columnIndex, Event event) {
    this.rowIndex = rowIndex;
    this.columnIndex = columnIndex;
    this.event = event;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public GwtEvent.Type<RowDoubleClickHandler> getAssociatedType() {
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
  protected void dispatch(RowDoubleClickHandler handler) {
    handler.onRowDoubleClick(this);
  }
}