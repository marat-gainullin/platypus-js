/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.state.client;

import com.google.gwt.core.client.Callback;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.sencha.gxt.state.client.BeforeRestoreStateEvent.BeforeRestoreStateHandler;
import com.sencha.gxt.state.client.BeforeRestoreStateEvent.HasBeforeRestoreStateHandlers;
import com.sencha.gxt.state.client.BeforeSaveStateEvent.HasBeforeSaveStateHandlers;
import com.sencha.gxt.state.client.RestoreStateEvent.HasRestoreStateHandlers;
import com.sencha.gxt.state.client.RestoreStateEvent.RestoreStateHandler;
import com.sencha.gxt.state.client.SaveStateEvent.HasSaveStateHandlers;
import com.sencha.gxt.state.client.SaveStateEvent.SaveStateHandler;

/**
 * Simple class to add support for reading and writing state. Can be subclassed
 * and a concrete type given to O (such as a Store or Component subclass), and a
 * particular interface S can be defined to hold the state for this object.
 * 
 * @param <S> the state interface
 * @param <O> the concrete type of the object state will be applied to
 */
public abstract class AbstractStateHandler<S, O> implements HasBeforeRestoreStateHandlers<S, O>,
    HasRestoreStateHandlers<S, O>, HasBeforeSaveStateHandlers<S, O>, HasSaveStateHandlers<S, O> {

  private final Class<S> stateType;
  private final O object;
  private final String key;
  private S state;
  private SimpleEventBus eventBus;

  protected AbstractStateHandler(Class<S> stateType, O object, String key) {
    assert stateType.isInterface();

    this.stateType = stateType;
    this.object = object;

    this.key = key;

    state = StateManager.get().getDefaultStateInstance(stateType);
  }

  public HandlerRegistration addBeforeRestoreStateHandler(BeforeRestoreStateHandler<S, O> handler) {
    return ensureHandlers().addHandler(BeforeRestoreStateEvent.getType(), handler);
  };

  public HandlerRegistration addBeforeSaveStateHandler(BeforeSaveStateEvent.BeforeSaveStateHandler<S, O> handler) {
    return ensureHandlers().addHandler(BeforeSaveStateEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addRestoreStateHandler(RestoreStateHandler<S, O> handler) {
    return ensureHandlers().addHandler(RestoreStateEvent.getType(), handler);
  };

  @Override
  public HandlerRegistration addSaveStateHandler(SaveStateHandler<S, O> handler) {
    return ensureHandlers().addHandler(SaveStateEvent.getType(), handler);
  }

  /**
   * Applies the currently loaded state to the current stateful object.
   */
  public abstract void applyState();

  /**
   * Returns the target object.
   * 
   * @return the target object
   */
  public O getObject() {
    return object;
  }

  /**
   * Returns the state.
   * 
   * @return the currently loaded state. The state instance may not be set, so
   *         this may return null.
   */
  public S getState() {
    return state;
  }

  /**
   * Starts to load the state for the given object.
   */
  public void loadState() {
    StateManager.get().get(key, stateType, new Callback<S, Throwable>() {
      @Override
      public void onFailure(Throwable reason) {

      }

      public void onSuccess(S result) {
        state = result;
        handleLoadState();
      }
    });
  }

  /**
   * Saves the current state.
   */
  public void saveState() {
    BeforeSaveStateEvent<S, O> e = new BeforeSaveStateEvent<S, O>(state, object);
    ensureHandlers().fireEvent(e);
    if (!e.isCancelled()) {
      StateManager.get().set(key, state);
      ensureHandlers().fireEvent(new SaveStateEvent<S, O>(state, object));
    }
  }

  protected void handleLoadState() {
    BeforeRestoreStateEvent<S, O> e = new BeforeRestoreStateEvent<S, O>(state, object);
    ensureHandlers().fireEvent(e);
    if (!e.isCancelled()) {
      applyState();
      ensureHandlers().fireEvent(new RestoreStateEvent<S, O>(state, object));
    }
  }

  SimpleEventBus ensureHandlers() {
    return eventBus == null ? eventBus = new SimpleEventBus() : eventBus;
  }
}
