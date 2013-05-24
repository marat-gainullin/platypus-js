/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.dnd.core.client;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.shared.event.CancellableEvent;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent.DndDragEnterHandler;
import com.sencha.gxt.fx.client.DragMoveEvent;
import com.sencha.gxt.fx.client.Draggable;

/**
 * Fires when a drag enters a drop target.
 */
public class DndDragEnterEvent extends GwtEvent<DndDragEnterHandler> implements CancellableEvent {

  /**
   * Handler type.
   */
  private static Type<DndDragEnterHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<DndDragEnterHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<DndDragEnterHandler>();
    }
    return TYPE;
  }

  private boolean cancelled;
  private Widget target;
  private NativeEvent nativeEvent;
  private DragSource dragSource;
  private DragMoveEvent dragMoveEvent;
  private StatusProxy statusProxy;

  public DndDragEnterEvent(Widget target, DragSource dragSource, DragMoveEvent event,
      StatusProxy status) {
    this.target = target;
    this.dragSource = dragSource;
    this.dragMoveEvent = event;
    this.statusProxy = status;
  }

  @Override
  public Type<DndDragEnterHandler> getAssociatedType() {
    return TYPE;
  }

  public DragMoveEvent getDragEnterEvent() {
    return dragMoveEvent;
  }

  public DragSource getDragSource() {
    return dragSource;
  }

  /**
   * Returns the native event.
   * 
   * @return the event
   */
  public NativeEvent getNativeEvent() {
    return nativeEvent;
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
  protected void dispatch(DndDragEnterHandler handler) {
    handler.onDragEnter(this);
  }
  
  /**
   * Handler for {@link DndDragEnterEvent} events.
   */
  public interface DndDragEnterHandler extends EventHandler {

    /**
     * Called when a dragged item enters a drop target.
     * 
     * @param event the {@link DndDragEnterEvent} that was fired
     */
    void onDragEnter(DndDragEnterEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link DndDragEnterEvent} events.
   */
  public interface HasDndDragEnterHandlers {

    /**
     * Adds a {@link DndDragEnterHandler} handler for {@link DndDragEnterEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addDragEnterHandler(DndDragEnterHandler handler);

  }

}
