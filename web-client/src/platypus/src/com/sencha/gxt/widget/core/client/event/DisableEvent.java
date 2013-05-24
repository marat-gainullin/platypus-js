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
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.event.DisableEvent.DisableHandler;

/**
 * Fires after a widget is disabled.
 */
public class DisableEvent extends GwtEvent<DisableHandler> {

  /**
   * Handler type.
   */
  private static Type<DisableHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<DisableHandler> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<DisableHandler>());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public Type<DisableHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  public Component getSource() {
    return (Component) super.getSource();
  }

  @Override
  protected void dispatch(DisableHandler handler) {
    handler.onDisable(this);
  }
  
  /**
   * Handler for {@link DisableEvent} events.
   */
  public interface DisableHandler extends EventHandler {

    void onDisable(DisableEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link DisableEvent} events.
   * 
   */
  public interface HasDisableHandlers {

    /**
     * Adds a {@link DisableHandler} handler for {@link DisableEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addDisableHandler(DisableHandler handler);

  }

}
