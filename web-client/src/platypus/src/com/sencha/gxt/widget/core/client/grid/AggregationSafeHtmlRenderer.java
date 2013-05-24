/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public final class AggregationSafeHtmlRenderer<M> implements AggregationRenderer<M> {
  protected final SafeHtml text;

  public AggregationSafeHtmlRenderer(String text) {
    this(SafeHtmlUtils.fromString(text));
  }

  public AggregationSafeHtmlRenderer(SafeHtml text) {
    this.text = text;
  }

  @Override
  public final SafeHtml render(int colIndex, Grid<M> grid) {
    return text;
  }

}
