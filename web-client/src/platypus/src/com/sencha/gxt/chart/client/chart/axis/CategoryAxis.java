/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart.axis;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;

/**
 * A type of axis that displays items in categories. This axis is generally used
 * to display categorical information like names of items, month names,
 * quarters, etc. but no quantitative values. For the other types of information
 * {@link NumericAxis} is more suitable.
 * 
 * @param <M> the data type of the axis
 * @param <V> the variable type of axis
 */
public class CategoryAxis<M, V> extends CartesianAxis<M, V> {

  protected ValueProvider<? super M, V> field;

  /**
   * Returns the {@link ValueProvider} used for labels on the axis.
   * 
   * @return the value provider used for labels on the axis
   */
  public ValueProvider<? super M, V> getField() {
    return field;
  }

  /**
   * Sets the {@link ValueProvider} used for labels on the axis.
   * 
   * @param field the value provider used for labels on the axis
   */
  public void setField(ValueProvider<? super M, V> field) {
    this.field = field;
  }

  @Override
  protected void applyData() {
    from = 0;
    to = chart.getCurrentStore().size();
    power = 1;
    step = 1;
    steps = (int) (to - 1);
  }

  @Override
  protected void createLabels() {
    labelNames.clear();
    ListStore<M> store = chart.getCurrentStore();
    for (int i = 0; i < store.size(); i++) {
      labelNames.add(field.getValue(store.get(i)));
    }
  }

}
