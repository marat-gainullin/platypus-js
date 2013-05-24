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
import com.sencha.gxt.widget.core.client.event.HeaderMouseDownEvent.HeaderMouseDownHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;

public final class HeaderMouseDownEvent extends GridEvent<HeaderMouseDownHandler> {

  public interface HasHeaderMouseDownHandlers extends HasHandlers {
    HandlerRegistration addHeaderMouseDownHandler(HeaderMouseDownHandler handler);
  }

  public interface HeaderMouseDownHandler extends EventHandler {
    void onHeaderMouseDown(HeaderMouseDownEvent event);
  }

  private static GwtEvent.Type<HeaderMouseDownHandler> TYPE;

  public static GwtEvent.Type<HeaderMouseDownHandler> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<HeaderMouseDownHandler>();
    }
    return TYPE;
  }

  private int columnIndex;
  private Event event;
  private Menu menu;

  public HeaderMouseDownEvent(int columnIndex, Event event, Menu menu) {
    this.columnIndex = columnIndex;
    this.event = event;
    this.menu = menu;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public GwtEvent.Type<HeaderMouseDownHandler> getAssociatedType() {
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
  protected void dispatch(HeaderMouseDownHandler handler) {
    handler.onHeaderMouseDown(this);
  }
}