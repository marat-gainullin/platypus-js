/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client;

import java.util.Set;

import com.sencha.gxt.data.shared.LabelProvider;

public class LabelProviderSafeHtmlCell<T> extends SimpleSafeHtmlCell<T> {

  public LabelProviderSafeHtmlCell(LabelProvider<? super T> labelProvider) {
    super(new LabelProviderSafeHtmlRenderer<T>(labelProvider), new String[]{});
  }
  
  public LabelProviderSafeHtmlCell(LabelProvider<? super T> labelProvider, Set<String> consumedEvents) {
    super(new LabelProviderSafeHtmlRenderer<T>(labelProvider), consumedEvents);
  }

}