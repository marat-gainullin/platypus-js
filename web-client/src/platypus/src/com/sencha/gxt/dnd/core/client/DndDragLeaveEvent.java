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
import com.sencha.gxt.dnd.core.client.DndDragLeaveEvent.DndDragLeaveHandler;
import com.sencha.gxt.fx.client.DragMoveEvent;
import com.sencha.gxt.fx.client.Draggable;

public class DndDragLeaveEvent extends GwtEvent<DndDragLeaveHandler> {

  /**
   * Handler type.
   */
  private static Type<DndDragLeaveHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<DndDragLeaveHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<DndDragLeaveHandler>();
    }
    return TYPE;
  }

  private Widget target;
  private DragSource dragSource;
  private DragMoveEvent dragMoveEvent;
  private StatusProxy statusProxy;

  public DndDragLeaveEvent(Widget target, DragSource dragSource, DragMoveEvent event,
      StatusProxy status) {
    this.target = target;
    this.dragSource = dragSource;
    this.dragMoveEvent = event;
    this.statusProxy = status;
  }

  @Override
  public Type<DndDragLeaveHandler> getAssociatedType() {
    return TYPE;
  }

  public DragMoveEvent getDragEnterEvent() {
    return dragMoveEvent;
  }

  public DragSource getDragSource() {
    return dragSource;
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
  protected void dispatch(DndDragLeaveHandler handler) {
    handler.onDragLeave(this);
  }
  
  /**
   * Handler for {@link DndDragLeaveEvent} events.
   */
  public interface DndDragLeaveHandler extends EventHandler {

    /**
     * Called when a dragged item leaves a drop target.
     * 
     * @param event the {@link DndDragLeaveEvent} that was fired
     */
    void onDragLeave(DndDragLeaveEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link DndDragLeaveEvent} events.
   */
  public interface HasDndDragLeaveHandlers {

    /**
     * Adds a {@link DndDragLeaveHandler} handler for {@link DndDragLeaveEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addDragLeaveHandler(DndDragLeaveHandler handler);

  }

}
