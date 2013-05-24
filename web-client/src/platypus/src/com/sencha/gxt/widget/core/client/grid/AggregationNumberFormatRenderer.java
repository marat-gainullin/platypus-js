/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.i18n.client.NumberFormat;

public abstract class AggregationNumberFormatRenderer<M> implements AggregationRenderer<M> {
  private final NumberFormat format;

  public AggregationNumberFormatRenderer() {
    this(NumberFormat.getDecimalFormat());
  }

  public AggregationNumberFormatRenderer(NumberFormat format) {
    this.format = format;
  }

  protected NumberFormat getFormat() {
    return format;
  }
}
