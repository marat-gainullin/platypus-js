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
import com.sencha.gxt.state.client.RestoreStateEvent.RestoreStateHandler;

/**
 * Fires after state has been restored.
 */
public class RestoreStateEvent<S, O> extends GwtEvent<RestoreStateHandler<S, O>> {

  public interface HasRestoreStateHandlers<S, O> {

    /**
     * Adds a {@link RestoreStateHandler} handler for {@link RestoreStateEvent}
     * events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addRestoreStateHandler(RestoreStateHandler<S, O> handler);
  }

  /**
   * Handler class for {@link RestoreStateEvent} events.
   */
  public interface RestoreStateHandler<S, O> extends EventHandler {

    /**
     * Called after state has been restored.
     */
    void onRestoreState(RestoreStateEvent<S, O> event);
  }

  /**
   * Handler type.
   */
  private static Type<RestoreStateHandler<?, ?>> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<RestoreStateHandler<?, ?>> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<RestoreStateHandler<?, ?>>());
  }
  
  private S state;
  private O target;
  
  public RestoreStateEvent(S state, O target) {
    this.state = state;
    this.target = target;
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<RestoreStateHandler<S, O>> getAssociatedType() {
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
  protected void dispatch(RestoreStateHandler<S, O> handler) {
    handler.onRestoreState(this);
  }

}
