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
import com.sencha.gxt.widget.core.client.event.ReconfigureEvent.ReconfigureHandler;

public final class ReconfigureEvent extends GridEvent<ReconfigureHandler> {

  public interface HasReconfigureHandlers extends HasHandlers {
    HandlerRegistration addReconfigureHandler(ReconfigureHandler handler);
  }

  public interface ReconfigureHandler extends EventHandler {
    void onReconfigure(ReconfigureEvent event);
  }

  private static GwtEvent.Type<ReconfigureHandler> TYPE;

  public static GwtEvent.Type<ReconfigureHandler> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<ReconfigureHandler>();
    }
    return TYPE;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public GwtEvent.Type<ReconfigureHandler> getAssociatedType() {
    return (GwtEvent.Type) TYPE;
  }

  @Override
  protected void dispatch(ReconfigureHandler handler) {
    handler.onReconfigure(this);
  }
}