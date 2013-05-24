/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.fx.client.DragCancelEvent.DragCancelHandler;

/**
 * Represents the after layout event.
 */
public class DragCancelEvent extends GwtEvent<DragCancelHandler> {

  /**
   * Handler type.
   */
  private static Type<DragCancelHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<DragCancelHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<DragCancelHandler>();
    }
    return TYPE;
  }

  private Widget target;
  private Element startElement;

  public DragCancelEvent(Widget target, Element startElement) {
    this.target = target;
    this.startElement = startElement;
  }

  @Override
  public Type<DragCancelHandler> getAssociatedType() {
    return TYPE;
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

  @Override
  protected void dispatch(DragCancelHandler handler) {
    handler.onDragCancel(this);
  }
  
  /**
   * Handler for {@link DragCancelEvent} events.
   */
  public interface DragCancelHandler extends EventHandler {

    void onDragCancel(DragCancelEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link DragCancelEvent} events.
   */
  public interface HasDragCancelHandlers {

    /**
     * Adds a {@link DragCancelHandler} handler for {@link DragCancelEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addDragCancelHandler(DragCancelHandler handler);

  }

}
