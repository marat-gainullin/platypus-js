/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart;

import com.google.gwt.i18n.client.NumberFormat;
import com.sencha.gxt.data.shared.LabelProvider;

/**
 * A label provider that provider rounded numbers.
 * 
 * @param <V> the type of the number
 */
public class RoundNumberProvider<V extends Number> implements LabelProvider<V> {
  @Override
  public String getLabel(Number item) {
    return NumberFormat.getFormat("0").format(item.doubleValue());
  }

}
