/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.shared.event.CancellableEvent;
import com.sencha.gxt.fx.client.DragStartEvent.DragStartHandler;

/**
 * Represents the drag start event.
 */
public class DragStartEvent extends GwtEvent<DragStartHandler> implements CancellableEvent {

  /**
   * Handler type.
   */
  private static Type<DragStartHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<DragStartHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<DragStartHandler>();
    }
    return TYPE;
  }

  private int x;
  private int y;
  private Element startElement;
  private boolean cancelled;
  private Widget target;
  private int width, height;
  private NativeEvent nativeEvent;

  public DragStartEvent(Widget target, Element startElement, int x, int y, Event event) {
    this.target = target;
    this.startElement = startElement;
    this.x = x;
    this.y = y;
    this.nativeEvent = event;
  }

  @Override
  public Type<DragStartHandler> getAssociatedType() {
    return TYPE;
  }

  public int getHeight() {
    return height;
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

  public Element getStartElement() {
    return startElement;
  }

  public Widget getTarget() {
    return target;
  }

  public int getWidth() {
    return width;
  }

  /**
   * Returns the widget's page coordinates.
   * 
   * @return the x-coordinate value
   */
  public int getX() {
    return x;
  }

  /**
   * Returns the widget's page coordinates.
   * 
   * @return the y-coordinate value
   */
  public int getY() {
    return y;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    this.cancelled = cancel;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  @Override
  protected void dispatch(DragStartHandler handler) {
    handler.onDragStart(this);
  }
  
  /**
   * Handler for {@link DragStartEvent} events.
   */
  public interface DragStartHandler extends EventHandler {

    void onDragStart(DragStartEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link DragStartEvent} events.
   */
  public interface HasDragStartHandlers {

    /**
     * Adds a {@link DragStartHandler} handler for {@link DragStartEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addDragStartHandler(DragStartHandler handler);

  }

}
