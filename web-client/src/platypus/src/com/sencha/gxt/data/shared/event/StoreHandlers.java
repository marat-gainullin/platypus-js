/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.data.shared.event.StoreAddEvent.HasStoreAddHandlers;
import com.sencha.gxt.data.shared.event.StoreAddEvent.StoreAddHandler;
import com.sencha.gxt.data.shared.event.StoreClearEvent.HasStoreClearHandler;
import com.sencha.gxt.data.shared.event.StoreClearEvent.StoreClearHandler;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent.HasStoreDataChangeHandlers;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent.StoreDataChangeHandler;
import com.sencha.gxt.data.shared.event.StoreFilterEvent.HasStoreFilterHandlers;
import com.sencha.gxt.data.shared.event.StoreFilterEvent.StoreFilterHandler;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent.HasStoreRecordChangeHandlers;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent.StoreRecordChangeHandler;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent.HasStoreRemoveHandler;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent.StoreRemoveHandler;
import com.sencha.gxt.data.shared.event.StoreSortEvent.HasStoreSortHandler;
import com.sencha.gxt.data.shared.event.StoreSortEvent.StoreSortHandler;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent.HasStoreUpdateHandlers;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent.StoreUpdateHandler;

/**
 * Handler interface for common store events.
 * 
 * @param <M> the type of data contained in the store
 */
public interface StoreHandlers<M> extends StoreAddHandler<M>, StoreRemoveHandler<M>, StoreFilterHandler<M>,
    StoreClearHandler<M>, StoreUpdateHandler<M>, StoreDataChangeHandler<M>, StoreRecordChangeHandler<M>,
    StoreSortHandler<M> {

  /**
   * A class that implements this interface is a public source of
   * common store events.
   * 
   * @param <M> the type of data contained in the store
   */
  public interface HasStoreHandlers<M> extends HasStoreAddHandlers<M>, HasStoreRemoveHandler<M>,
      HasStoreUpdateHandlers<M>, HasStoreRecordChangeHandlers<M>, HasStoreFilterHandlers<M>, HasStoreClearHandler<M>,
      HasStoreDataChangeHandlers<M>, HasStoreSortHandler<M> {

    /**
     * Adds a common store event handler.
     * 
     * @param handlers the handlers
     * @return the registration for the event
     */
    HandlerRegistration addStoreHandlers(StoreHandlers<M> handlers);
  }
}