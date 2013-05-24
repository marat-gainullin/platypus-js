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
import com.sencha.gxt.fx.client.DragEndEvent.DragEndHandler;

/**
 * Represents the drag end event.
 */
public class DragEndEvent extends GwtEvent<DragEndHandler> {

  /**
   * Handler type.
   */
  private static Type<DragEndHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<DragEndHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<DragEndHandler>();
    }
    return TYPE;
  }

  private int x;
  private int y;
  private Element startElement;
  private Widget target;
  private NativeEvent nativeEvent;

  public DragEndEvent(Widget target, Element startElement, int x, int y, Event event) {
    this.target = target;
    this.startElement = startElement;
    this.x = x;
    this.y = y;
    this.nativeEvent = event;
  }

  @Override
  public Type<DragEndHandler> getAssociatedType() {
    return TYPE;
  }

  public NativeEvent getNativeEvent() {
    return nativeEvent;
  }

  public Draggable getSource() {
    return (Draggable) super.getSource();
  }

  public Element getStartElement() {
    return startElement;
  }

  /**
   * Returns the widget being dragged.
   * 
   * @return the dragged widget
   */
  public Widget getTarget() {
    return target;
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
  protected void dispatch(DragEndHandler handler) {
    handler.onDragEnd(this);
  }
  
  /**
   * Handler for {@link DragEndEvent} events.
   */
  public interface DragEndHandler extends EventHandler {

    void onDragEnd(DragEndEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link DragEndEvent} events.
   */
  public interface HasDragEndHandlers {

    /**
     * Adds a {@link DragEndHandler} handler for {@link DragEndEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addDragEndHandler(DragEndHandler handler);

  }

}
