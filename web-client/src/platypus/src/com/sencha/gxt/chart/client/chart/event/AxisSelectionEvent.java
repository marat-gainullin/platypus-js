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
import com.sencha.gxt.chart.client.chart.axis.Axis;
import com.sencha.gxt.chart.client.chart.event.AxisSelectionEvent.AxisSelectionHandler;

/**
 * Fired when the {@link Axis} is clicked.
 */
public class AxisSelectionEvent extends GwtEvent<AxisSelectionHandler> {

  /**
   * Handler class for {@link AxisSelectionEvent} events.
   */
  public interface AxisSelectionHandler extends EventHandler {

    /**
     * Fired when the {@link Axis} is clicked.
     * 
     * @param event the fired event
     */
    void onAxisSelection(AxisSelectionEvent event);
  }

  /**
   * A widget that implements this interface is a public source of
   * {@link AxisSelectionEvent} events.
   */
  public interface HasAxisSelectionHandlers {

    /**
     * Adds a {@link AxisSelectionHandler} handler for
     * {@link AxisSelectionEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addAxisSelectionHandler(AxisSelectionHandler handler);
  }

  /**
   * Handler type.
   */
  private static Type<AxisSelectionHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<AxisSelectionHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<AxisSelectionHandler>();
    }
    return TYPE;
  }

  private final String value;
  private final int index;
  private final Event event;

  /**
   * Creates a new event with the given value and index.
   * 
   * @param value the value of the axis item
   * @param index the index of the axis item
   */
  public AxisSelectionEvent(String value, int index, Event event) {
    this.value = value;
    this.index = index;
    this.event = event;
  }

  @Override
  public Type<AxisSelectionHandler> getAssociatedType() {
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
   * Returns the index of the axis item.
   * 
   * @return the index of the axis item
   */
  public int getIndex() {
    return index;
  }

  /**
   * Returns the value of the axis item.
   * 
   * @return the value of the axis item
   */
  public String getValue() {
    return value;
  }

  @Override
  protected void dispatch(AxisSelectionHandler handler) {
    handler.onAxisSelection(this);
  }

}
