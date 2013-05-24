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
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent.StoreRecordChangeHandler;

/**
 * Indicates that a Record object in the Store has been changed in some way.
 * 
 * @param <M> the model type
 */
public final class StoreRecordChangeEvent<M> extends StoreEvent<M, StoreRecordChangeHandler<M>> {

  /**
   * A class that implements this interface is a public source of
   * {@link StoreRecordChangeEvent}.
   * 
   * @param <M> the type of data contained in the store
   */
  public interface HasStoreRecordChangeHandlers<M> extends HasHandlers {

    /**
     * Adds a {@link StoreRecordChangeEvent} handler.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addStoreRecordChangeHandler(StoreRecordChangeHandler<M> handler);
  }

  /**
   * Handler interface for {@link StoreRecordChangeEvent}.
   * 
   * @param <M> the type of data contained in the store
   */
  public interface StoreRecordChangeHandler<M> extends EventHandler {

    /**
     * Called when {@link StoreRecordChangeEvent} is fired.
     * 
     * @param event the {@link StoreRecordChangeEvent} that was fired
     */
    void onRecordChange(StoreRecordChangeEvent<M> event);
  }

  private static GwtEvent.Type<StoreRecordChangeHandler<?>> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<StoreRecordChangeHandler<?>> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<StoreRecordChangeHandler<?>>();
    }
    return TYPE;
  }

  private final Store<M>.Record record;

  private final ValueProvider<? super M, ?> property;// is this useful?

  /**
   * Creates a new store record change event.
   * 
   * @param record the changed record
   * @param property the changed property
   */
  public StoreRecordChangeEvent(Store<M>.Record record, ValueProvider<? super M, ?> property) {
    this.record = record;
    this.property = property;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public GwtEvent.Type<StoreRecordChangeHandler<M>> getAssociatedType() {
    return (GwtEvent.Type) getType();
  }

  /**
   * Returns the property that was responsible for firing this record change event.
   * 
   * @return the changed property
   */
  public ValueProvider<? super M, ?> getProperty() {
    return property;
  }

  /**
   * Returns the record that was responsible for firing this record change
   * event.
   * 
   * @return the changed record
   */
  public Store<M>.Record getRecord() {
    return record;
  }

  @Override
  protected void dispatch(StoreRecordChangeHandler<M> handler) {
    handler.onRecordChange(this);
  }
}