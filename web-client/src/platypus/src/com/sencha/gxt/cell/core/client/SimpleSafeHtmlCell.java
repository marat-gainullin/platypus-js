/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client;

import java.util.Set;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;

public class SimpleSafeHtmlCell<C> extends AbstractSafeHtmlCell<C> {

  public SimpleSafeHtmlCell(SafeHtmlRenderer<C> renderer, Set<String> consumedEvents) {
    super(renderer, consumedEvents);
  }

  public SimpleSafeHtmlCell(SafeHtmlRenderer<C> renderer, String... consumedEvents) {
    super(renderer, consumedEvents);
  }

  @Override
  protected void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
    if (data != null) {
      sb.append(data);
    }
  }

}
