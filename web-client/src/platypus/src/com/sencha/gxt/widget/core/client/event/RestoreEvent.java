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
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.RestoreEvent.RestoreHandler;

/**
 * Fires after a window is restored.
 */
public class RestoreEvent extends GwtEvent<RestoreHandler> {

  public interface HasRestoreHandlers {

    /**
     * Adds a {@link RestoreHandler} handler for {@link RestoreEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addRestoreHandler(RestoreHandler handler);
  }

  /**
   * Handler class for {@link RestoreEvent} events.
   */
  public interface RestoreHandler extends EventHandler {

    /**
     * Called when a window is restored.
     */
    void onRestore(RestoreEvent event);
  }

  /**
   * Handler type.
   */
  private static Type<RestoreHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<RestoreHandler> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<RestoreHandler>());
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<RestoreHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  @Override
  public Window getSource() {
    return (Window) super.getSource();
  }

  @Override
  protected void dispatch(RestoreHandler handler) {
    handler.onRestore(this);
  }

}
