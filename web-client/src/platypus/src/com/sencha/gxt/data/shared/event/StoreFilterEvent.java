/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.data.shared.event.StoreFilterEvent.StoreFilterHandler;

/**
 * Indicates that the Store has had its filtering properties changed, resulting
 * in changes in what data is visible.
 * 
 * @param <M> The type of data in the Store
 */
public final class StoreFilterEvent<M> extends StoreEvent<M, StoreFilterHandler<M>> {

  /**
   * A class that implements this interface is a public source of
   * {@link StoreFilterEvent}.
   * 
   * @param <M> the type of data contained in the store
   */
  public interface HasStoreFilterHandlers<M> extends EventHandler {

    /**
     * Adds a {@link StoreFilterEvent} handler.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addStoreFilterHandler(StoreFilterHandler<M> handler);
  }

  /**
   * Handler interface for {@link StoreFilterEvent}.
   * 
   * @param <M> the type of data contained in the store
   */
  public interface StoreFilterHandler<M> extends EventHandler {

    /**
     * Called when {@link StoreFilterEvent} is fired.
     * 
     * @param event the {@link StoreFilterEvent} that was fired
     */
    void onFilter(StoreFilterEvent<M> event);
  }

  private static GwtEvent.Type<StoreFilterHandler<?>> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<StoreFilterHandler<?>> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<StoreFilterHandler<?>>();
    }
    return TYPE;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public GwtEvent.Type<StoreFilterHandler<M>> getAssociatedType() {
    return (GwtEvent.Type) getType();
  }

  @Override
  protected void dispatch(StoreFilterHandler<M> handler) {
    handler.onFilter(this);
  }
}