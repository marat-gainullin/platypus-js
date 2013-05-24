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
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent.ViewReadyHandler;

public final class ViewReadyEvent extends GridEvent<ViewReadyHandler> {
  public interface HasViewReadyHandlers extends HasHandlers {
    HandlerRegistration addViewReadyHandler(ViewReadyHandler handler);
  }

  public interface ViewReadyHandler extends EventHandler {
    void onViewReady(ViewReadyEvent event);
  }

  private static GwtEvent.Type<ViewReadyHandler> TYPE;

  public static GwtEvent.Type<ViewReadyHandler> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<ViewReadyHandler>();
    }
    return TYPE;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public GwtEvent.Type<ViewReadyHandler> getAssociatedType() {
    return (GwtEvent.Type) TYPE;
  }

  @Override
  protected void dispatch(ViewReadyHandler handler) {
    handler.onViewReady(this);
  }
}