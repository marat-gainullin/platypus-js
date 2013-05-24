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
import com.sencha.gxt.chart.client.chart.event.SeriesSelectionEvent.SeriesSelectionHandler;
import com.sencha.gxt.chart.client.chart.series.Series;
import com.sencha.gxt.core.client.ValueProvider;

/**
 * Fired when the {@link Series} is clicked.
 */
public class SeriesSelectionEvent<M> extends GwtEvent<SeriesSelectionHandler<M>> {

  /**
   * A widget that implements this interface is a public source of
   * {@link SeriesSelectionEvent} events.
   */
  public interface HasSeriesSelectionHandlers<M> {

    /**
     * Adds a {@link SeriesSelectionHandler} handler for
     * {@link SeriesSelectionEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addSeriesSelectionHandler(SeriesSelectionHandler<M> handler);
  }

  /**
   * Handler class for {@link SeriesSelectionEvent} events.
   */
  public interface SeriesSelectionHandler<M> extends EventHandler {

    /**
     * Fired when the {@link Series} is clicked.
     * 
     * @param event the fired event
     */
    void onSeriesSelection(SeriesSelectionEvent<M> event);
  }

  /**
   * Handler type.
   */
  private static Type<SeriesSelectionHandler<?>> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<SeriesSelectionHandler<?>> getType() {
    if (TYPE == null) {
      TYPE = new Type<SeriesSelectionHandler<?>>();
    }
    return TYPE;
  }

  private final M item;
  private final ValueProvider<? super M, ? extends Number> valueProvider;
  private final int index;
  private final Event event;

  /**
   * Creates a new event with the given value and index.
   * 
   * @param item the store item associated with the series
   * @param valueProvider the valueProvider associated with the series
   * @param index the series index
   * @param event the source event
   */
  public SeriesSelectionEvent(M item, ValueProvider<? super M, ? extends Number> valueProvider, int index, Event event) {
    this.item = item;
    this.valueProvider = valueProvider;
    this.index = index;
    this.event = event;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<SeriesSelectionHandler<M>> getAssociatedType() {
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
  protected void dispatch(SeriesSelectionHandler<M> handler) {
    handler.onSeriesSelection(this);
  }

}
