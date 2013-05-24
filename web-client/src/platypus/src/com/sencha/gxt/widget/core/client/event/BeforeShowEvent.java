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
import com.sencha.gxt.core.shared.event.CancellableEvent;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;

/**
 * Represents the before show event.
 */
public class BeforeShowEvent extends GwtEvent<BeforeShowHandler> implements CancellableEvent {

  /**
   * Handler type.
   */
  private static Type<BeforeShowHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<BeforeShowHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<BeforeShowHandler>();
    }
    return TYPE;
  }

  private boolean cancelled;

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<BeforeShowHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  public Component getSource() {
    return (Component) super.getSource();
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  @Override
  protected void dispatch(BeforeShowHandler handler) {
    handler.onBeforeShow(this);
  }
  
  /**
   * Handler for {@link BeforeShowEvent} events.
   */
  public interface BeforeShowHandler extends EventHandler {

    /**
     * Called after a widget's state is saved. The action can be cancelled
     * using {@link BeforeShowEvent#setCancelled(boolean)}.
     * 
     * @param event the {@link BeforeShowEvent} that was fired
     */
    void onBeforeShow(BeforeShowEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link BeforeShowEvent} events.
   */
  public interface HasBeforeShowHandlers {

    /**
     * Adds a {@link BeforeShowHandler} handler for {@link BeforeShowEvent}
     * events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addBeforeShowHandler(BeforeShowHandler handler);

  }

}
