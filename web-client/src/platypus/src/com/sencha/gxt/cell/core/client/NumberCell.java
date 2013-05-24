/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;

public class NumberCell<N extends Number> extends AbstractCell<N> {

  /**
   * The {@link NumberFormat} used to render the number.
   */
  private final NumberFormat format;

  /**
   * The {@link SafeHtmlRenderer} used to render the formatted number as HTML.
   */
  private final SafeHtmlRenderer<String> renderer;

  /**
   * Construct a new {@link NumberCell} using decimal format and a
   * {@link SimpleSafeHtmlRenderer}.
   */
  public NumberCell() {
    this(NumberFormat.getDecimalFormat(), SimpleSafeHtmlRenderer.getInstance());
  }

  /**
   * Construct a new {@link NumberCell} using the given {@link NumberFormat} and
   * a {@link SimpleSafeHtmlRenderer}.
   *
   * @param format the {@link NumberFormat} used to render the number
   */
  public NumberCell(NumberFormat format) {
    this(format, SimpleSafeHtmlRenderer.getInstance());
  }

  /**
   * Construct a new {@link NumberCell} using decimal format and the given
   * {@link SafeHtmlRenderer}.
   *
   * @param renderer the {@link SafeHtmlRenderer} used to render the formatted
   *          number as HTML
   */
  public NumberCell(SafeHtmlRenderer<String> renderer) {
    this(NumberFormat.getDecimalFormat(), renderer);
  }

  /**
   * Construct a new {@link NumberCell} using the given {@link NumberFormat} and
   * a {@link SafeHtmlRenderer}.
   *
   * @param format the {@link NumberFormat} used to render the number
   * @param renderer the {@link SafeHtmlRenderer} used to render the formatted
   *          number as HTML
   */
  public NumberCell(NumberFormat format, SafeHtmlRenderer<String> renderer) {
    if (format == null) {
      throw new IllegalArgumentException("format == null");
    }
    if (renderer == null) {
      throw new IllegalArgumentException("renderer == null");
    }
    this.format = format;
    this.renderer = renderer;
  }

  @Override
  public void render(Context context, Number value, SafeHtmlBuilder sb) {
    if (value != null) {
      sb.append(renderer.render(format.format(value)));
    }
  }
}

