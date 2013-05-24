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
import com.sencha.gxt.core.shared.event.CancellableEvent;
import com.sencha.gxt.dnd.core.client.DndDragMoveEvent.DndDragMoveHandler;
import com.sencha.gxt.fx.client.DragMoveEvent;
import com.sencha.gxt.fx.client.Draggable;

public class DndDragMoveEvent extends GwtEvent<DndDragMoveHandler> implements CancellableEvent {

  /**
   * Handler type.
   */
  private static Type<DndDragMoveHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<DndDragMoveHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<DndDragMoveHandler>();
    }
    return TYPE;
  }

  private boolean cancelled;
  private Widget target;
  private DragSource dragSource;
  private DragMoveEvent dragMoveEvent;
  private StatusProxy statusProxy;
  private Object data;
  private DropTarget dropTarget;

  public DndDragMoveEvent(Widget target, DragSource dragSource, DragMoveEvent event, StatusProxy status, Object data) {
    this.target = target;
    this.dragSource = dragSource;
    this.dragMoveEvent = event;
    this.statusProxy = status;
    this.data = data;
  }

  @Override
  public Type<DndDragMoveHandler> getAssociatedType() {
    return TYPE;
  }

  public Object getData() {
    return data;
  }

  public DragMoveEvent getDragMoveEvent() {
    return dragMoveEvent;
  }

  public DragSource getDragSource() {
    return dragSource;
  }

  public DropTarget getDropTarget() {
    return dropTarget;
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
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    this.cancelled = cancel;
  }

  @Override
  protected void dispatch(DndDragMoveHandler handler) {
    handler.onDragMove(this);
  }

  void setDropTarget(DropTarget dropTarget) {
    this.dropTarget = dropTarget;
  }
  
  /**
   * Handler for {@link DndDragMoveEvent} events.
   */
  public interface DndDragMoveHandler extends EventHandler {

    /**
     * Called when the animation is complete.
     * 
     * @param event the {@link DndDragMoveEvent} that was fired
     */
    void onDragMove(DndDragMoveEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link DndDragMoveEvent} events.
   */
  public interface HasDndDragMoveHandlers {

    /**
     * Adds a {@link DndDragMoveHandler} handler for {@link DndDragMoveEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addDragMoveHandler(DndDragMoveHandler handler);

  }

}
