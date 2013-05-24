/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.dnd.core.client;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.shared.event.CancellableEvent;
import com.sencha.gxt.dnd.core.client.DndDragCancelEvent.DndDragCancelHandler;
import com.sencha.gxt.dnd.core.client.DndDragCancelEvent.HasDndDragCancelHandlers;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.DndDragStartHandler;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.HasDndDragStartHandlers;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.DndDropEvent.HasDndDropHandlers;
import com.sencha.gxt.fx.client.DragCancelEvent;
import com.sencha.gxt.fx.client.DragEndEvent;
import com.sencha.gxt.fx.client.DragHandler;
import com.sencha.gxt.fx.client.DragMoveEvent;
import com.sencha.gxt.fx.client.DragStartEvent;
import com.sencha.gxt.fx.client.Draggable;

/**
 * Enables a component to act as the source of a drag and drop operation (i.e. a
 * user can drag data from the component).
 * <p />
 * Drag sources must set the data that will be dragged during a drag operation.
 * The data can be specified either by using {@link #setData(Object)} or by
 * setting the data via the DND event when a drag begins.
 * <p />
 * Drag sources are responsible for removing the dragged data from the source
 * widget after a valid drop. Use {@link DropTarget#getOperation()} to determine
 * if the data was copied or moved. The target is accessible via the DNDEvent
 * passed to {@link #onDragDrop(DndDropEvent)} and listeners.
 */
public class DragSource implements HasDndDragStartHandlers, HasDndDragCancelHandlers, HasDndDropHandlers {

  protected Widget widget;
  protected Draggable draggable;
  protected DragHandler handler;
  protected Object data;
  protected StatusProxy statusProxy = StatusProxy.get();

  private String statusText;
  private String group = "";
  private boolean enabled = true;
  private SimpleEventBus eventBus;

  /**
   * Creates a drag source that enables the specified widget to act as the
   * starting point of a drag operation.
   * 
   * @param widget the widget to serve as the starting point of a drag operation
   */
  public DragSource(Widget widget) {
    this.widget = widget;

    handler = new DragHandler() {

      public void onDragCancel(DragCancelEvent event) {
        onDraggableDragCancel(event);
      };

      public void onDragEnd(DragEndEvent event) {
        onDraggableDragEnd(event);
      }

      @Override
      public void onDragMove(DragMoveEvent event) {
        onDraggableDragMove(event);
      }

      @Override
      public void onDragStart(DragStartEvent event) {
        onDraggableDragStart(event);
      };

    };

    draggable = new Draggable(widget);
    draggable.setUseProxy(true);
    draggable.setSizeProxyToSource(false);
    draggable.setMoveAfterProxyDrag(false);
    draggable.addDragHandler(handler);
    draggable.setProxy(statusProxy.getElement());
  }

  @Override
  public HandlerRegistration addDragCancelHandler(DndDragCancelHandler handler) {
    return ensureHandlers().addHandler(DndDragCancelEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addDragStartHandler(DndDragStartHandler handler) {
    return ensureHandlers().addHandler(DndDragStartEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addDropHandler(DndDropHandler handler) {
    return ensureHandlers().addHandler(DndDropEvent.getType(), handler);
  }

  /**
   * Disables the drag source.
   */
  public void disable() {
    enabled = false;
    draggable.setEnabled(false);
  }

  /**
   * Enables the drag source.
   */
  public void enable() {
    enabled = true;
    draggable.setEnabled(true);
  }

  /**
   * Returns the data to be moved or copied.
   * 
   * @return the data
   */
  public Object getData() {
    return data;
  }

  /**
   * Returns the draggable instance.
   * 
   * @return the draggable instance
   */
  public Draggable getDraggable() {
    return draggable;
  }

  /**
   * Returns the source's drag drop group.
   * 
   * @return the group name or null if not specified
   */
  public String getGroup() {
    return group;
  }

  /**
   * Returns the status text.
   * 
   * @return the text
   */
  public String getStatusText() {
    return statusText;
  }

  /**
   * Returns the source widget.
   * 
   * @return the widget
   */
  public Widget getWidget() {
    return widget;
  }

  /**
   * Returns true if the drag source is enabled.
   * 
   * @return true for enabled
   */
  public boolean isEnabled() {
    return enabled && draggable.isEnabled();
  }

  /**
   * Releases the DragSource from the widget.
   */
  public void release() {
    draggable.release();
  }

  /**
   * Sets the data for the drag drop operation.
   * 
   * @param data the data
   */
  public void setData(Object data) {
    this.data = data;
  }

  /**
   * Sets the drag drop group. If specified, drops will only be allowed on drop
   * targets with the same group value.
   * 
   * @param group the group name
   */
  public void setGroup(String group) {
    this.group = group;
  }

  /**
   * Sets the text to be used on the status proxy object. If the drag source
   * supports selection, {0} will be substituted with the selection size.
   * 
   * @param statusText the status text
   */
  public void setStatusText(String statusText) {
    this.statusText = statusText;
  }

  /**
   * Called when a drag operation has been cancelled.
   * 
   * @param event the dnd cancel event
   */
  protected void onDragCancelled(DndDragCancelEvent event) {

  }

  protected void onDragDrop(DndDropEvent event) {

  }

  protected void onDragFail(DndDropEvent event) {

  }

  /**
   * Called when a drag operation begins on the target widget. Subclasses or any
   * listeners can cancel the action by calling
   * {@link CancellableEvent#setCancelled(boolean)}.
   * 
   * @param event the dnd event
   */
  protected void onDragStart(DndDragStartEvent event) {

  }

  SimpleEventBus ensureHandlers() {
    return eventBus == null ? eventBus = new SimpleEventBus() : eventBus;
  }

  private void onDraggableDragCancel(DragCancelEvent de) {
    DndDragCancelEvent e = new DndDragCancelEvent(widget, de);
    DNDManager.get().handleDragCancelled(this, e);
  }

  private void onDraggableDragEnd(DragEndEvent de) {
    DndDropEvent e = new DndDropEvent(widget, de, statusProxy, data);
    if (e.getData() != null) {
      DNDManager.get().handleDragEnd(this, e);
    }
  }

  private void onDraggableDragMove(DragMoveEvent de) {
    Event e = de.getNativeEvent().cast();
    de.setX(e.getClientX() + 12 + XDOM.getBodyScrollLeft());
    de.setY(e.getClientY() + 12 + XDOM.getBodyScrollTop());

    DndDragMoveEvent me = new DndDragMoveEvent(widget, this, de, statusProxy, data);
    DNDManager.get().handleDragMove(this, me);
  }

  private void onDraggableDragStart(DragStartEvent event) {
    DndDragStartEvent e = new DndDragStartEvent(widget, event, statusProxy, data);
    DNDManager.get().handleDragStart(this, e);
    // instruct draggable
    event.setCancelled(e.isCancelled());
  }

}
