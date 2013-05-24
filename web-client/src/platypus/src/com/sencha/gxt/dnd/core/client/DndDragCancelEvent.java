/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.dnd.core.client;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.dnd.core.client.DndDragCancelEvent.DndDragCancelHandler;
import com.sencha.gxt.fx.client.DragCancelEvent;
import com.sencha.gxt.fx.client.Draggable;

/**
 * Fires after a drag is cancelled.
 */
public class DndDragCancelEvent extends GwtEvent<DndDragCancelHandler> {

  public interface DndDragCancelHandler extends EventHandler {

    void onDragCancel(DndDragCancelEvent event);

  }

  /**
   * A widget that implements this interface is a public source of
   * {@link DndDragCancelEvent} events.
   */
  public interface HasDndDragCancelHandlers {

    /**
     * Adds a {@link DndDragCancelHandler} handler for
     * {@link DndDragCancelEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addDragCancelHandler(DndDragCancelHandler handler);

  }

  /**
   * Handler type.
   */
  private static Type<DndDragCancelHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<DndDragCancelHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<DndDragCancelHandler>();
    }
    return TYPE;
  }

  private Widget target;

  private DragCancelEvent dragCancelEvent;

  private StatusProxy statusProxy;

  public DndDragCancelEvent(Widget target, DragCancelEvent event) {
    this.target = target;
    this.dragCancelEvent = event;
  }

  @Override
  public Type<DndDragCancelHandler> getAssociatedType() {
    return TYPE;
  }

  public DragCancelEvent getDragCancelEvent() {
    return dragCancelEvent;
  }

  public Draggable getSource() {
    return (Draggable) super.getSource();
  }

  public StatusProxy getStatusProxy() {
    return statusProxy;
  }

  public Widget getTarget() {
    return target;
  }

  @Override
  protected void dispatch(DndDragCancelHandler handler) {
    handler.onDragCancel(this);
  }

}
