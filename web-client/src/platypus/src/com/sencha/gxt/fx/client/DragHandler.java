/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client;

import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.fx.client.DragCancelEvent.DragCancelHandler;
import com.sencha.gxt.fx.client.DragEndEvent.DragEndHandler;
import com.sencha.gxt.fx.client.DragMoveEvent.DragMoveHandler;
import com.sencha.gxt.fx.client.DragStartEvent.DragStartHandler;

public interface DragHandler extends DragStartHandler, DragEndHandler, DragCancelHandler, DragMoveHandler {

  /**
   * A widget that implements this interface is a public source of
   * {@link DragStartEvent}, {@link DragEndEvent}, {@link DragCancelEvent},
   * {@link DragMoveEvent} events.
   */
  public interface HasDragHandlers {

    /**
     * Adds a {@link DragHandler} handler for {@link DragStartEvent} ,
     * {@link DragEndEvent}, {@link DragCancelEvent}, {@link DragMoveEvent}
     * events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addDragHandler(DragHandler handler);
  }
}
