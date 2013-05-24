/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.tree;

import java.util.Set;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

final class CellWidgetImplHelper {
  public native static boolean isFocusable(Element elem) /*-{
		var c = @com.google.gwt.user.cellview.client.CellBasedWidgetImpl::get()();
		return c.@com.google.gwt.user.cellview.client.CellBasedWidgetImpl::isFocusable(Lcom/google/gwt/dom/client/Element;)(element);
  }-*/;

  public native static void onBrowserEvent(Widget widget, Event event) /*-{
		var c = @com.google.gwt.user.cellview.client.CellBasedWidgetImpl::get()();
		c.@com.google.gwt.user.cellview.client.CellBasedWidgetImpl::onBrowserEvent(Lcom/google/gwt/user/client/ui/Widget;Lcom/google/gwt/user/client/Event;)(widget, event);
  }-*/;

  public native static SafeHtml processHtml(SafeHtml html)/*-{
		var c = @com.google.gwt.user.cellview.client.CellBasedWidgetImpl::get()();
		return c.@com.google.gwt.user.cellview.client.CellBasedWidgetImpl::processHtml(Lcom/google/gwt/safehtml/shared/SafeHtml;)(html);
  }-*/;

  public native static void resetFocus(ScheduledCommand command)/*-{
		var c = @com.google.gwt.user.cellview.client.CellBasedWidgetImpl::get()();
		c.@com.google.gwt.user.cellview.client.CellBasedWidgetImpl::resetFocus(Lcom/google/gwt/core/client/Scheduler$ScheduledCommand;)(command);
  }-*/;

  public native static void sinkEvents(Widget widget, Set<String> typeNames)/*-{
		var c = @com.google.gwt.user.cellview.client.CellBasedWidgetImpl::get()();
		c.@com.google.gwt.user.cellview.client.CellBasedWidgetImpl::sinkEvents(Lcom/google/gwt/user/client/ui/Widget;Ljava/util/Set;)(widget, typeNames);
  }-*/;

}
