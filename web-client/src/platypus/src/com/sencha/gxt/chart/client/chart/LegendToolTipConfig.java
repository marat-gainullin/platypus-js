/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.chart;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

/**
 * Configuration for a {@link Legend} tooltip.
 * 
 * @param <M> the data type used by the config
 */
public class LegendToolTipConfig<M> extends ToolTipConfig {

  private LabelProvider<LegendItem<M>> customLabelProvider;

  /**
   * Returns the custom label provider.
   * 
   * @return the custom label provider
   */
  public LabelProvider<LegendItem<M>> getCustomLabelProvider() {
    return customLabelProvider;
  }

  /**
   * Returns the custom label provider. Overrides numeric label provider.
   * 
   * @param customLabelProvider the custom label provider
   */
  public void setCustomLabelProvider(LabelProvider<LegendItem<M>> customLabelProvider) {
    this.customLabelProvider = customLabelProvider;
  }

  /**
   * Sets a custom {@link ValueProvider} for the tooltip.
   * 
   * @param valueProvider the custom value provider
   * @param labelProvider the label provider used on the value provider
   */
  public <V> void setValueProvider(final ValueProvider<LegendItem<M>, V> valueProvider,
      final LabelProvider<V> labelProvider) {
    this.customLabelProvider = new LabelProvider<LegendItem<M>>() {
      @Override
      public String getLabel(LegendItem<M> item) {
        return labelProvider.getLabel(valueProvider.getValue(item));
      }
    };
  }
}
