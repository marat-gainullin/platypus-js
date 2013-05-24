/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.event;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.Resizable;
import com.sencha.gxt.widget.core.client.event.ResizeEndEvent.ResizeEndHandler;

/**
 * Represents the source is resized.
 */
public class ResizeEndEvent extends GwtEvent<ResizeEndHandler> {

  /**
   * A widget that implements this interface is a public source of
   * {@link ResizeEndEvent} events.
   */
  public interface HasResizeEndHandlers {

    /**
     * Adds a {@link ResizeEndHandler} handler for {@link ResizeEndEvent}
     * events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addResizeEndHandler(ResizeEndHandler handler);

  }

  /**
   * Handler class for {@link ResizeEndEvent} events.
   */
  public interface ResizeEndHandler extends EventHandler {

    /**
     * Called when a widget is resized.
     */
    void onResizeEnd(ResizeEndEvent event);
  }

  /**
   * Handler type.
   */
  private static Type<ResizeEndHandler> TYPE;
  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<ResizeEndHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<ResizeEndHandler>();
    }
    return TYPE;
  }

  private Component target;

  private NativeEvent nativeEvent;

  public ResizeEndEvent(Component target, Event event) {
    this.target = target;
    this.nativeEvent = event;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<ResizeEndHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  /**
   * Returns the DOM native event.
   * 
   * @return the event
   */
  public NativeEvent getNativeEvent() {
    return nativeEvent;
  }

  public Resizable getSource() {
    return (Resizable) super.getSource();
  }

  /**
   * Returns the target component.
   * 
   * @return the component
   */
  public Component getTarget() {
    return target;
  }

  @Override
  protected void dispatch(ResizeEndHandler handler) {
    handler.onResizeEnd(this);
  }

}
