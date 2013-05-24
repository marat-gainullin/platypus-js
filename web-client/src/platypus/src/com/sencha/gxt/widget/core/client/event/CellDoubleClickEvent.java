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
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;

public final class CellDoubleClickEvent extends GridEvent<CellDoubleClickHandler> {

  public interface HasCellDoubleClickHandlers extends HasHandlers {
    HandlerRegistration addCellDoubleClickHandler(CellDoubleClickHandler handler);
  }

  public interface CellDoubleClickHandler extends EventHandler {
    void onCellClick(CellDoubleClickEvent event);
  }

  private static GwtEvent.Type<CellDoubleClickHandler> TYPE;

  public static GwtEvent.Type<CellDoubleClickHandler> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<CellDoubleClickHandler>();
    }
    return TYPE;
  }

  private int rowIndex;
  private int cellIndex;
  private Event event;

  public CellDoubleClickEvent(int rowIndex, int cellIndex, Event event) {
    this.rowIndex = rowIndex;
    this.cellIndex = cellIndex;
    this.event = event;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public GwtEvent.Type<CellDoubleClickHandler> getAssociatedType() {
    return (GwtEvent.Type) TYPE;
  }

  public int getCellIndex() {
    return cellIndex;
  }

  public int getRowIndex() {
    return rowIndex;
  }

  public Event getEvent() {
    return event;
  }

  @Override
  protected void dispatch(CellDoubleClickHandler handler) {
    handler.onCellClick(this);
  }
}