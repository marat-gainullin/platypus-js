/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.dom;

import com.google.gwt.dom.client.Element;

final class ScrollImplHelper {

  public native static int getMaximumHorizontalScrollPosition(Element elem) /*-{
		var c = @com.google.gwt.user.client.ui.ScrollImpl::get()();
		return c.@com.google.gwt.user.client.ui.ScrollImpl::getMaximumHorizontalScrollPosition(Lcom/google/gwt/dom/client/Element;)(elem);
  }-*/;
  
  public native static int getMinimumHorizontalScrollPosition(Element elem) /*-{
  var c = @com.google.gwt.user.client.ui.ScrollImpl::get()();
  return c.@com.google.gwt.user.client.ui.ScrollImpl::getMinimumHorizontalScrollPosition(Lcom/google/gwt/dom/client/Element;)(elem);
}-*/;

}
