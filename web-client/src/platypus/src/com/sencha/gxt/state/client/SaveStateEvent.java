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
import com.sencha.gxt.state.client.SaveStateEvent.SaveStateHandler;

/**
 * Fires after state has been saved.
 */
public class SaveStateEvent<S, O> extends GwtEvent<SaveStateHandler<S,O>> implements CancellableEvent{

  /**
   * Handler class for {@link SaveStateEvent} events.
   */
  public interface SaveStateHandler<S, O> extends EventHandler {

    /**
     * Called after state has been saved.
     */
    void onSaveState(SaveStateEvent<S, O> event);
  }

  public interface HasSaveStateHandlers<S, O> {

    /**
     * Adds a {@link SaveStateHandler} handler for {@link SaveStateEvent}
     * events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addSaveStateHandler(SaveStateHandler<S, O> handler);
  }

  /**
   * Handler type.
   */
  private static Type<SaveStateHandler<?, ?>> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<SaveStateHandler<?, ?>> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<SaveStateHandler<?, ?>>());
  }
  
  private S state;
  private O target;
  private boolean cancelled;
  
  public SaveStateEvent(S state, O target) {
    this.state = state;
    this.target = target;
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<SaveStateHandler<S, O>> getAssociatedType() {
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
  protected void dispatch(SaveStateHandler<S, O> handler) {
    handler.onSaveState(this);
  }

}
