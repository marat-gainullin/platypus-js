/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOutEvent.HasSeriesItemOutHandlers;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOutEvent.SeriesItemOutHandler;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOverEvent.HasSeriesItemOverHandlers;
import com.sencha.gxt.chart.client.chart.event.SeriesItemOverEvent.SeriesItemOverHandler;
import com.sencha.gxt.chart.client.chart.event.SeriesSelectionEvent.HasSeriesSelectionHandlers;
import com.sencha.gxt.chart.client.chart.event.SeriesSelectionEvent.SeriesSelectionHandler;

/**
 * Aggregating handler interface for:
 * 
 * <dl>
 * <dd>{@link SeriesSelectionEvent}</b></dd>
 * <dd>{@link SeriesItemOutEvent}</b></dd>
 * <dd>{@link SeriesItemOverEvent}</b></dd>
 * </dl>
 */
public interface SeriesHandler<M> extends SeriesSelectionHandler<M>, SeriesItemOutHandler<M>, SeriesItemOverHandler<M> {

  /**
   * A widget that implements this interface is a public source of
   * {@link SeriesSelectionEvent}, {@link SeriesItemOutEvent} and
   * {@link SeriesItemOverEvent} events.
   */
  public interface HasSeriesHandlers<M> extends HasSeriesSelectionHandlers<M>, HasSeriesItemOutHandlers<M>,
      HasSeriesItemOverHandlers<M> {

    /**
     * Adds a {@link SeriesHandler} handler for {@link SeriesSelectionEvent},
     * {@link SeriesItemOutEvent}, {@link SeriesItemOverEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addSeriesHandler(SeriesHandler<M> handler);

  }
}
