/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.series;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * <code>SeriesLabelProvider</code>'s are responsible for returning a label for
 * a given object.
 * 
 * @param <M> the type of the object from
 */
public interface SeriesLabelProvider<M> {

  /**
   * Returns a label for the given object and {@link ValueProvider}. The return
   * value is treated as plain text, and will be escaped before it is drawn.
   * 
   * @param item the store item
   * @param valueProvider the value provider
   * @return the label
   */
  String getLabel(M item, ValueProvider<? super M, ? extends Number> valueProvider);
}
