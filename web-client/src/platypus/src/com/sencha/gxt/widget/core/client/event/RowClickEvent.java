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
import com.sencha.gxt.widget.core.client.event.RowClickEvent.RowClickHandler;

public final class RowClickEvent extends GridEvent<RowClickHandler> {

  public interface HasRowClickHandlers extends HasHandlers {
    HandlerRegistration addRowClickHandler(RowClickHandler handler);
  }

  public interface RowClickHandler extends EventHandler {
    void onRowClick(RowClickEvent event);
  }

  private static GwtEvent.Type<RowClickHandler> TYPE;

  public static GwtEvent.Type<RowClickHandler> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<RowClickHandler>();
    }
    return TYPE;
  }

  private int rowIndex;
  private int columnIndex = -1;
  private Event event;

  public RowClickEvent(int rowIndex, int columnIndex, Event event) {
    this.rowIndex = rowIndex;
    this.columnIndex = columnIndex;
    this.event = event;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public GwtEvent.Type<RowClickHandler> getAssociatedType() {
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
  protected void dispatch(RowClickHandler handler) {
    handler.onRowClick(this);
  }
}