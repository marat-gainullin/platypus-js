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
import com.sencha.gxt.core.shared.event.CancellableEvent;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.Resizable;
import com.sencha.gxt.widget.core.client.event.ResizeStartEvent.ResizeStartHandler;

/**
 * Fires when a resize starts.
 */
public class ResizeStartEvent extends GwtEvent<ResizeStartHandler> implements CancellableEvent {

  /**
   * Handler type.
   */
  private static Type<ResizeStartHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<ResizeStartHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<ResizeStartHandler>();
    }
    return TYPE;
  }

  private boolean cancelled;
  private Component target;
  private NativeEvent nativeEvent;

  public ResizeStartEvent(Component target, Event event) {
    this.target = target;
    this.nativeEvent = event;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<ResizeStartHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  public NativeEvent getNativeEvent() {
    return nativeEvent;
  }

  public Resizable getSource() {
    return (Resizable) super.getSource();
  }

  public Component getTarget() {
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
  protected void dispatch(ResizeStartHandler handler) {
    handler.onResizeStart(this);
  }
  
  /**
   * Handler class for {@link ResizeStartEvent} events.
   */
  public interface ResizeStartHandler extends EventHandler {

    /**
     * Called when a widget is starting to be resized.
     */
    void onResizeStart(ResizeStartEvent event);
  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link ResizeStartEvent} events.
   */
  public interface HasResizeStartHandlers {

    /**
     * Adds a {@link ResizeStartHandler} handler for {@link ResizeStartEvent}
     * events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addResizeStartHandler(ResizeStartHandler handler);

  }

}
