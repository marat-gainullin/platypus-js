/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.sencha.gxt.data.shared.Store.Record;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent.StoreUpdateHandler;

/**
 * Indicates that changes to one or more {@link Record}s have been committed, so
 * the changes have affected the underlying model.
 * 
 * @param <M> the model type
 */
public final class StoreUpdateEvent<M> extends StoreEvent<M, StoreUpdateHandler<M>> {

  /**
   * A class that implements this interface is a public source of
   * {@link StoreUpdateEvent}.
   * 
   * @param <M> the type of data contained in the store
   */
  public interface HasStoreUpdateHandlers<M> extends HasHandlers {

    /**
     * Adds a {@link StoreUpdateEvent} handler.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addStoreUpdateHandler(StoreUpdateHandler<M> handler);
  }

  /**
   * Handler interface for {@link StoreUpdateEvent}.
   * 
   * @param <M> the type of data contained in the store
   */
  public interface StoreUpdateHandler<M> extends EventHandler {

    /**
     * Called when {@link StoreUpdateEvent} is fired.
     * 
     * @param event the {@link StoreUpdateEvent} that was fired
     */
    void onUpdate(StoreUpdateEvent<M> event);
  }

  private static GwtEvent.Type<StoreUpdateHandler<?>> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static GwtEvent.Type<StoreUpdateHandler<?>> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<StoreUpdateHandler<?>>();
    }
    return TYPE;
  }

  private List<M> items;

  /**
   * Creates a new store update event.
   * 
   * @param items the items that have been updated
   */
  public StoreUpdateEvent(List<M> items) {
    this.items = items;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public GwtEvent.Type<StoreUpdateHandler<M>> getAssociatedType() {
    return (GwtEvent.Type) getType();
  }

  /**
   * Returns the items that were responsible for firing this store update event.
   * 
   * @return the updated items
   */
  public List<M> getItems() {
    return items;
  }

  @Override
  protected void dispatch(StoreUpdateHandler<M> handler) {
    handler.onUpdate(this);
  }
}