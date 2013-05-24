/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.state.client;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.core.shared.event.CancellableEvent;
import com.sencha.gxt.state.client.BeforeSaveStateEvent.BeforeSaveStateHandler;

/**
 * Fires before state has been saved.
 */
public class BeforeSaveStateEvent<S, O> extends GwtEvent<BeforeSaveStateHandler<S,O>> implements CancellableEvent{

  /**
   * Handler class for {@link BeforeSaveStateEvent} events.
   */
  public interface BeforeSaveStateHandler<S, O> extends EventHandler {

    /**
     * Called before state has been saved.
     */
    void onBeforeSaveState(BeforeSaveStateEvent<S, O> event);
  }

  public interface HasBeforeSaveStateHandlers<S, O> {

    /**
     * Adds a {@link BeforeSaveStateHandler} handler for {@link BeforeSaveStateEvent}
     * events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addBeforeSaveStateHandler(BeforeSaveStateHandler<S, O> handler);
  }

  /**
   * Handler type.
   */
  private static Type<BeforeSaveStateHandler<?, ?>> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<BeforeSaveStateHandler<?, ?>> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<BeforeSaveStateHandler<?, ?>>());
  }
  
  private S state;
  private O target;
  private boolean cancelled;
  
  public BeforeSaveStateEvent(S state, O target) {
    this.state = state;
    this.target = target;
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<BeforeSaveStateHandler<S, O>> getAssociatedType() {
    return (Type) TYPE;
  }
  
  /**
   * Returns the state.
   * 
   * @return the state
   */
  public S getState() {
    return state;
  }

  /**
   * Returns the target object.
   * 
   * @return the target
   */
  public O getTarget() {
    return target;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    this.cancelled = cancel;
  }

  @Override
  protected void dispatch(BeforeSaveStateHandler<S, O> handler) {
    handler.onBeforeSaveState(this);
  }

}
