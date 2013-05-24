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
import com.sencha.gxt.chart.client.chart.Legend;
import com.sencha.gxt.chart.client.chart.LegendItem;
import com.sencha.gxt.chart.client.chart.event.LegendSelectionEvent.LegendSelectionHandler;

/**
 * Fired when the {@link Legend} is clicked.
 */
public class LegendSelectionEvent extends GwtEvent<LegendSelectionHandler> {

  /**
   * A widget that implements this interface is a public source of
   * {@link LegendSelectionEvent} events.
   */
  public interface HasLegendSelectionHandlers {

    /**
     * Adds a {@link LegendSelectionHandler} handler for
     * {@link LegendSelectionEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addLegendSelectionHandler(LegendSelectionHandler handler);
  }

  /**
   * Handler class for {@link LegendSelectionEvent} events.
   */
  public interface LegendSelectionHandler extends EventHandler {

    /**
     * Fired when the {@link Legend} is clicked.
     * 
     * @param event the fired event
     */
    void onLegendSelection(LegendSelectionEvent event);
  }

  /**
   * Handler type.
   */
  private static Type<LegendSelectionHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<LegendSelectionHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<LegendSelectionHandler>();
    }
    return TYPE;
  }

  private final int index;
  private final LegendItem<?> item;
  private final Event event;

  /**
   * Creates a new event with the given legend item.
   * 
   * @param index the index of the legend item that fired the event
   * @param item the legend item that fired the event
   */
  public LegendSelectionEvent(int index, LegendItem<?> item, Event event) {
    this.index = index;
    this.item = item;
    this.event = event;
  }

  @Override
  public Type<LegendSelectionHandler> getAssociatedType() {
    return getType();
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
   * Returns the index of the legend item that fired the event.
   * 
   * @return the index of the legend item that fired the event
   */
  public int getIndex() {
    return index;
  }

  /**
   * Returns the legend item that fired the event.
   * 
   * @return the legend item that fired the event
   */
  public LegendItem<?> getItem() {
    return item;
  }

  @Override
  protected void dispatch(LegendSelectionHandler handler) {
    handler.onLegendSelection(this);
  }

}
