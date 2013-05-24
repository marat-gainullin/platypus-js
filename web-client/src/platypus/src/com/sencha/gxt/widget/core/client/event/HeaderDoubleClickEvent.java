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
import com.sencha.gxt.widget.core.client.event.HeaderDoubleClickEvent.HeaderDoubleClickHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;

public final class HeaderDoubleClickEvent extends GridEvent<HeaderDoubleClickHandler> {

  public interface HasHeaderDoubleClickHandlers extends HasHandlers {
    HandlerRegistration addHeaderDoubleClickHandler(HeaderDoubleClickHandler handler);
  }

  public interface HeaderDoubleClickHandler extends EventHandler {
    void onHeaderDoubleClick(HeaderDoubleClickEvent event);
  }

  private static GwtEvent.Type<HeaderDoubleClickHandler> TYPE;

  public static GwtEvent.Type<HeaderDoubleClickHandler> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<HeaderDoubleClickHandler>();
    }
    return TYPE;
  }

  private int columnIndex;
  private Event event;
  private Menu menu;

  public HeaderDoubleClickEvent(int columnIndex, Event event, Menu menu) {
    this.columnIndex = columnIndex;
    this.event = event;
    this.menu = menu;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public GwtEvent.Type<HeaderDoubleClickHandler> getAssociatedType() {
    return (GwtEvent.Type) TYPE;
  }
  
  public int getColumnIndex() {
    return columnIndex;
  }

  public Event getEvent() {
    return event;
  }

  public Menu getMenu() {
    return menu;
  }

  @Override
  protected void dispatch(HeaderDoubleClickHandler handler) {
    handler.onHeaderDoubleClick(this);
  }
}