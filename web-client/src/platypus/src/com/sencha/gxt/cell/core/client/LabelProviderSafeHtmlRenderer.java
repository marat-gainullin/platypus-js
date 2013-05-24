/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.sencha.gxt.data.shared.LabelProvider;

public class LabelProviderSafeHtmlRenderer<T> extends AbstractSafeHtmlRenderer<T> {

  private final LabelProvider<? super T> labelProvider;

  public LabelProviderSafeHtmlRenderer(LabelProvider<? super T> labelProvider) {
    this.labelProvider = labelProvider;
  }

  @Override
  public SafeHtml render(T object) {
    SafeHtmlBuilder sb = new SafeHtmlBuilder();
    sb.appendEscaped(labelProvider.getLabel(object));
    return sb.toSafeHtml();
  }

}
