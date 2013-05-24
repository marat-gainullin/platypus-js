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
import com.sencha.gxt.chart.client.chart.event.LegendItemOutEvent.LegendItemOutHandler;

/**
 * Fired when the {@link Legend} is moused out.
 */
public class LegendItemOutEvent extends GwtEvent<LegendItemOutHandler> {

  /**
   * A widget that implements this interface is a public source of
   * {@link LegendItemOutEvent} events.
   */
  public interface HasLegendItemOutHandlers {

    /**
     * Adds a {@link LegendItemOutHandler} handler for
     * {@link LegendItemOutEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addLegendItemOutHandler(LegendItemOutHandler handler);
  }

  /**
   * Handler class for {@link LegendItemOutEvent} events.
   */
  public interface LegendItemOutHandler extends EventHandler {

    /**
     * Fired when a {@link Legend} item is moused out.
     * 
     * @param event the fired event
     */
    void onLegendItemOut(LegendItemOutEvent event);
  }

  /**
   * Handler type.
   */
  private static Type<LegendItemOutHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<LegendItemOutHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<LegendItemOutHandler>();
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
  public LegendItemOutEvent(int index, LegendItem<?> item, Event event) {
    this.index = index;
    this.item = item;
    this.event = event;
  }

  @Override
  public Type<LegendItemOutHandler> getAssociatedType() {
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
  protected void dispatch(LegendItemOutHandler handler) {
    handler.onLegendItemOut(this);
  }

}
