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
import com.google.gwt.user.client.Event;
import com.sencha.gxt.widget.core.client.event.BodyScrollEvent.BodyScrollHandler;

public final class BodyScrollEvent extends GridEvent<BodyScrollHandler> {

  public interface BodyScrollHandler extends EventHandler {
    void onBodyScroll(BodyScrollEvent event);
  }

  public interface HasBodyScrollHandlers extends HasHandlers {
    HandlerRegistration addBodyScrollHandler(BodyScrollHandler handler);
  }

  private static GwtEvent.Type<BodyScrollHandler> TYPE;

  public static GwtEvent.Type<BodyScrollHandler> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<BodyScrollHandler>();
    }
    return TYPE;
  }

  private Event event;
  private int scrollLeft, scrollTop;

  public BodyScrollEvent(int scrollLeft, int scrollTop) {
    this.scrollLeft = scrollLeft;
    this.scrollTop = scrollTop;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public GwtEvent.Type<BodyScrollHandler> getAssociatedType() {
    return (GwtEvent.Type) TYPE;
  }

  public Event getEvent() {
    return event;
  }

  public int getScrollLeft() {
    return scrollLeft;
  }

  public int getScrollTop() {
    return scrollTop;
  }

  @Override
  protected void dispatch(BodyScrollHandler handler) {
    handler.onBodyScroll(this);
  }
}