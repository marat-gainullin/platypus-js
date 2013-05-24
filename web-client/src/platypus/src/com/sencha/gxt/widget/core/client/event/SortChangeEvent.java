/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.widget.core.client.event.SortChangeEvent.SortChangeHandler;

public final class SortChangeEvent extends GridEvent<SortChangeHandler> {

  public interface HasSortChangeHandlers extends HasHandlers {
    HandlerRegistration addSortChangeHandler(SortChangeHandler handler);
  }

  public interface SortChangeHandler extends EventHandler {
    void onSortChange(SortChangeEvent event);
  }

  private static GwtEvent.Type<SortChangeHandler> TYPE;

  public static GwtEvent.Type<SortChangeHandler> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<SortChangeHandler>();
    }
    return TYPE;
  }

  private SortInfo sortInfo;

  public SortChangeEvent(SortInfo sortInfo) {
    this.sortInfo = sortInfo;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public GwtEvent.Type<SortChangeHandler> getAssociatedType() {
    return (GwtEvent.Type) TYPE;
  }

  public SortInfo getSortInfo() {
    return sortInfo;
  }

  @Override
  protected void dispatch(SortChangeHandler handler) {
    handler.onSortChange(this);
  }
}