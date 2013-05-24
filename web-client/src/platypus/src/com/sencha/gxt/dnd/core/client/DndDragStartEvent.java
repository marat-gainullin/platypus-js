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
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.DndDragStartHandler;
import com.sencha.gxt.fx.client.DragStartEvent;
import com.sencha.gxt.fx.client.Draggable;

public class DndDragStartEvent extends GwtEvent<DndDragStartHandler> implements CancellableEvent {

  /**
   * Handler type.
   */
  private static Type<DndDragStartHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<DndDragStartHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<DndDragStartHandler>();
    }
    return TYPE;
  }

  private boolean cancelled;
  private Widget target;
  private DragStartEvent dragStartEvent;
  private StatusProxy statusProxy;
  private Object data;
  
  public DndDragStartEvent(Widget target, DragStartEvent event, StatusProxy status, Object data) {
    this.target = target;
    this.dragStartEvent = event;
    this.statusProxy = status;
    this.data = data;
  }

  @Override
  public Type<DndDragStartHandler> getAssociatedType() {
    return TYPE;
  }
  
  public Object getData() {
    return data;
  }

  public DragStartEvent getDragStartEvent() {
    return dragStartEvent;
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

  public void setData(Object data) {
    this.data = data;
  }

  @Override
  protected void dispatch(DndDragStartHandler handler) {
    handler.onDragStart(this);
  }
  
  public interface DndDragStartHandler extends EventHandler {

    void onDragStart(DndDragStartEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link DndDragStartEvent} events.
   */
  public interface HasDndDragStartHandlers {

    /**
     * Adds a {@link DndDragStartHandler} handler for {@link DndDragStartEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addDragStartHandler(DndDragStartHandler handler);

  }

}
