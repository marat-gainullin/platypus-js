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
import com.sencha.gxt.widget.core.client.event.StartEditEvent.StartEditHandler;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;

public final class StartEditEvent<M> extends GridEditingEvent<M, StartEditHandler<M>> {
  public interface HasStartEditHandlers<M> extends HasHandlers {
    HandlerRegistration addStartEditHandler(StartEditHandler<M> handler);
  }

  public interface StartEditHandler<M> extends EventHandler {
    void onStartEdit(StartEditEvent<M> event);
  }

  private static GwtEvent.Type<StartEditHandler<?>> TYPE;

  public static GwtEvent.Type<StartEditHandler<?>> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<StartEditHandler<?>>();
    }
    return TYPE;
  }

  public StartEditEvent(GridCell editCell) {
    super(editCell);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public GwtEvent.Type<StartEditHandler<M>> getAssociatedType() {
    return (GwtEvent.Type) getType();
  }

  @Override
  protected void dispatch(StartEditHandler<M> handler) {
    handler.onStartEdit(this);
  }
}
