/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOutEvent.SeriesItemOutHandler;
import com.sencha.gxt.chart.client.chart.series.Series;
import com.sencha.gxt.core.client.ValueProvider;

/**
 * Fired when an the mouse leaves an item in the {@link Series}.
 */
public class SeriesItemOutEvent<M> extends GwtEvent<SeriesItemOutHandler<M>> {

  /**
   * A widget that implements this interface is a public source of
   * {@link SeriesItemOutEvent} events.
   */
  public interface HasSeriesItemOutHandlers<M> {

    /**
     * Adds a {@link SeriesItemOutHandler} handler for
     * {@link SeriesItemOutEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addSeriesItemOutHandler(SeriesItemOutHandler<M> handler);
  }

  /**
   * Handler class for {@link SeriesItemOutEvent} events.
   */
  public interface SeriesItemOutHandler<M> extends EventHandler {

    /**
     * Fired when an the mouse leaves an item in the {@link Series}.
     * 
     * @param event the fired event
     */
    void onSeriesLeaveItem(SeriesItemOutEvent<M> event);
  }

  /**
   * Handler type.
   */
  private static Type<SeriesItemOutHandler<?>> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<SeriesItemOutHandler<?>> getType() {
    if (TYPE == null) {
      TYPE = new Type<SeriesItemOutHandler<?>>();
    }
    return TYPE;
  }

  private final M item;
  private final ValueProvider<? super M, ? extends Number> valueProvider;
  private final int index;
  private final Event event;

  /**
   * Creates a new event.
   * 
   * @param item the store item
   * @param valueProvider the value provider
   * @param index the series index
   * @param event the source browser event
   */
  public SeriesItemOutEvent(M item, ValueProvider<? super M, ? extends Number> valueProvider, int index, Event event) {
    this.item = item;
    this.valueProvider = valueProvider;
    this.index = index;
    this.event = event;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<SeriesItemOutHandler<M>> getAssociatedType() {
    return (Type) getType();
  }

  /**
   * Returns the browser event that initiated the selection event.
   * 
   * @return the browser event that initiated the selection event
   */
  public Event getBrowserEvent() {
    return event;
  }

  /**
   * Returns the index of the series item.
   * 
   * @return the index of the series item
   */
  public int getIndex() {
    return index;
  }

  /**
   * Returns the store item associated with the event.
   * 
   * @return the store item associated with the event
   */
  public M getItem() {
    return item;
  }

  /**
   * Returns the value provider associated with the event.
   * 
   * @return the value provider associated with the event
   */
  public ValueProvider<? super M, ? extends Number> getValueProvider() {
    return valueProvider;
  }

  @Override
  protected void dispatch(SeriesItemOutHandler<M> handler) {
    handler.onSeriesLeaveItem(this);
  }

}
