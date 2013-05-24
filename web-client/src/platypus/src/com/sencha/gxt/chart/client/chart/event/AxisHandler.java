/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.chart.client.chart.event.AxisItemOutEvent.AxisItemOutHandler;
import com.sencha.gxt.chart.client.chart.event.AxisItemOverEvent.AxisItemOverHandler;
import com.sencha.gxt.chart.client.chart.event.AxisSelectionEvent.AxisSelectionHandler;

/**
 * Aggregating handler interface for:
 * 
 * <dl>
 * <dd>{@link AxisSelectionEvent}</b></dd>
 * <dd>{@link AxisItemOutEvent}</b></dd>
 * <dd>{@link AxisItemOverEvent}</b></dd>
 * </dl>
 */
public interface AxisHandler extends AxisSelectionHandler, AxisItemOutHandler, AxisItemOverHandler {

  /**
   * A widget that implements this interface is a public source of
   * {@link AxisSelectionEvent}, {@link AxisItemOutEvent} and
   * {@link AxisItemOverEvent} events.
   */
  public interface HasAxisHandlers {

    /**
     * Adds a {@link AxisHandler} handler for {@link AxisSelectionEvent},
     * {@link AxisItemOutEvent}, {@link AxisItemOverEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addAxisHandler(AxisHandler handler);

  }
}
