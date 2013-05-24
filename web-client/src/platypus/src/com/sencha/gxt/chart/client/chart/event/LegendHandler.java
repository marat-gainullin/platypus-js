/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.chart.client.chart.event.LegendItemOutEvent.LegendItemOutHandler;
import com.sencha.gxt.chart.client.chart.event.LegendItemOverEvent.LegendItemOverHandler;
import com.sencha.gxt.chart.client.chart.event.LegendSelectionEvent.LegendSelectionHandler;

/**
 * Aggregating handler interface for:
 * 
 * <dl>
 * <dd>{@link LegendSelectionEvent}</b></dd>
 * <dd>{@link LegendItemOutEvent}</b></dd>
 * <dd>{@link LegendItemOverEvent}</b></dd>
 * </dl>
 */
public interface LegendHandler extends LegendSelectionHandler, LegendItemOutHandler, LegendItemOverHandler {

  /**
   * A widget that implements this interface is a public source of
   * {@link LegendSelectionEvent}, {@link LegendItemOutEvent},
   * {@link LegendItemOverEvent} events.
   */
  public interface HasLegendHandlers {

    /**
     * Adds a {@link LegendHandler} handler for {@link LegendSelectionEvent},
     * {@link LegendItemOutEvent}, {@link LegendItemOverEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addLegendHandler(LegendHandler handler);

  }
}
