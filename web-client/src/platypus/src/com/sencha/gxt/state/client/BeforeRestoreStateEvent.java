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
import com.sencha.gxt.state.client.BeforeRestoreStateEvent.BeforeRestoreStateHandler;

/**
 * Fires before state has been restored.
 */
public class BeforeRestoreStateEvent<S, O> extends GwtEvent<BeforeRestoreStateHandler<S,O>> implements CancellableEvent{

  /**
   * Handler class for {@link BeforeRestoreStateEvent} events.
   */
  public interface BeforeRestoreStateHandler<S, O> extends EventHandler {

    /**
     * Called before state has been restored.
     */
    void onBeforeRestoreState(BeforeRestoreStateEvent<S, O> event);
  }

  public interface HasBeforeRestoreStateHandlers<S, O> {

    /**
     * Adds a {@link BeforeRestoreStateHandler} handler for {@link BeforeRestoreStateEvent}
     * events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addBeforeRestoreStateHandler(BeforeRestoreStateHandler<S, O> handler);
  }

  /**
   * Handler type.
   */
  private static Type<BeforeRestoreStateHandler<?, ?>> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<BeforeRestoreStateHandler<?, ?>> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<BeforeRestoreStateHandler<?, ?>>());
  }
  
  private S state;
  private O target;
  private boolean cancelled;
  
  public BeforeRestoreStateEvent(S state, O target) {
    this.state = state;
    this.target = target;
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<BeforeRestoreStateHandler<S, O>> getAssociatedType() {
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
  protected void dispatch(BeforeRestoreStateHandler<S, O> handler) {
    handler.onBeforeRestoreState(this);
  }

}
