/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import java.util.Map;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.core.client.ValueProvider;

/**
 * Returns the rendered content for a summary row.
 */
public interface SummaryRenderer<M> {

  /**
   * Returns the html content for the summary row.
   * 
   * @param value the summary calculation
   * @param data the data for the group
   * @return the html content
   */
  public SafeHtml render(Number value, Map<ValueProvider<? super M, ?>, Number> data);

}
