/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.ValueProvider;

public class AggregationNumberSummaryRenderer<M, N> extends AggregationNumberFormatRenderer<M> {
  private final SummaryType<N, ? extends Number> summaryType;

  public AggregationNumberSummaryRenderer(NumberFormat format, SummaryType<N, ? extends Number> summaryType) {
    super(format);
    this.summaryType = summaryType;
  }

  public AggregationNumberSummaryRenderer(SummaryType<N, ? extends Number> summaryType) {
    super();
    this.summaryType = summaryType;
  }

  public SummaryType<N, ? extends Number> getSummaryType() {
    return summaryType;
  }

  @Override
  public SafeHtml render(int colIndex, Grid<M> grid) {
    ValueProvider<? super M, N> v = grid.getColumnModel().getValueProvider(colIndex);
    Number n = summaryType.calculate(grid.getStore().getAll(), v);
    return SafeHtmlUtils.fromString(getFormat().format(n));
  }

}
