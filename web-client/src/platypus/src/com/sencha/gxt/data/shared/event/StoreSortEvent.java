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
import com.google.gwt.event.shared.HasHandlers;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.event.StoreSortEvent.StoreSortHandler;

/**
 * Indicates that the store sort properties have changed. To inspect the
 * current sort properties, use {@link Store#getSortInfo()}.
 * 
 * @param <M> the model type
 */
public final class StoreSortEvent<M> extends StoreEvent<M, StoreSortHandler<M>> {

  /**
   * A class that implements this interface is a public source of
   * {@link StoreSortEvent}.
   * 
   * @param <M> the type of data contained in the store
   */
  public interface HasStoreSortHandler<M> extends HasHandlers {

    /**
     * Adds a {@link StoreSortEvent} handler.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addStoreSortHandler(StoreSortHandler<M> handler);
  }

  /**
   * Handler interface for {@link StoreSortEvent}.
   * 
   * @param <M> the type of data contained in the store
   */
  public interface StoreSortHandler<M> extends EventHandler {
    
    /**
     * Called when {@link StoreSortEvent} is fired.
     * 
     * @param event the {@link StoreSortEvent} that was fired
     */
    void onSort(StoreSortEvent<M> event);
  }

  private static GwtEvent.Type<StoreSortHandler<?>> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static GwtEvent.Type<StoreSortHandler<?>> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<StoreSortEvent.StoreSortHandler<?>>();
    }
    return TYPE;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public GwtEvent.Type<StoreSortHandler<M>> getAssociatedType() {
    return (GwtEvent.Type) getType();
  }

  @Override
  protected void dispatch(StoreSortHandler<M> handler) {
    handler.onSort(this);
  }
}
