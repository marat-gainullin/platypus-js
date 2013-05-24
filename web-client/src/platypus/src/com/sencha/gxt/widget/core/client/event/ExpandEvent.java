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
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;

/**
 * Fires after the source is expanded.
 */
public class ExpandEvent extends GwtEvent<ExpandHandler> {

  /**
   * Handler class for {@link ExpandEvent} events.
   */
  public interface ExpandHandler extends EventHandler {

    /**
     * Called after the source is expanded.
     */
    void onExpand(ExpandEvent event);
  }

  /**
   * A widget that implements this interface is a public source of
   * {@link ExpandEvent} events.
   */
  public interface HasExpandHandlers {

    /**
     * Adds a {@link ExpandHandler} handler for {@link ExpandEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addExpandHandler(ExpandHandler handler);
  }

  /**
   * Handler type.
   */
  private static Type<ExpandHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<ExpandHandler> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<ExpandHandler>());
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<ExpandHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  @Override
  protected void dispatch(ExpandHandler handler) {
    handler.onExpand(this);
  }

}
