/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart;

import com.google.gwt.i18n.client.NumberFormat;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelProvider;
import com.sencha.gxt.core.client.ValueProvider;

/**
 * A label provider that provider rounded numbers.
 * 
 * @param <M> the type of the number
 */
public class SeriesRoundNumberProvider<M> implements SeriesLabelProvider<M> {
  @Override
  public String getLabel(M item, ValueProvider<? super M, ? extends Number> valueProvider) {
    return NumberFormat.getFormat("0").format(valueProvider.getValue(item).doubleValue());
  }

}
