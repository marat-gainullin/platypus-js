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
import com.sencha.gxt.core.client.ValueProvider;

public class SummaryColumnConfig<M, N> extends ColumnConfig<M, N> {

  protected SummaryType<N, ?> summaryType;
  protected SummaryRenderer<M> summaryRenderer;
  protected NumberFormat summaryFormat;

  public SummaryColumnConfig(ValueProvider<? super M, N> valueProvider) {
    super(valueProvider);
  }

  public SummaryColumnConfig(ValueProvider<? super M, N> valueProvider, int width) {
    super(valueProvider, width);
  }

  public SummaryColumnConfig(ValueProvider<? super M, N> valueProvider, int width, SafeHtml name) {
    super(valueProvider, width, name);
  }

  public SummaryColumnConfig(ValueProvider<? super M, N> valueProvider, int width, String name) {
    super(valueProvider, width, name);
  }

  public NumberFormat getSummaryFormat() {
    return summaryFormat;
  }

  public SummaryRenderer<M> getSummaryRenderer() {
    return summaryRenderer;
  }

  public SummaryType<N, ?> getSummaryType() {
    return summaryType;
  }

  public void setSummaryFormat(NumberFormat summaryFormat) {
    this.summaryFormat = summaryFormat;
  }

  public void setSummaryRenderer(SummaryRenderer<M> summaryRenderer) {
    this.summaryRenderer = summaryRenderer;
  }

  public void setSummaryType(SummaryType<N, ?> summaryType) {
    this.summaryType = summaryType;
  }

}
