/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.event.ActivateEvent.ActivateHandler;

/**
 * Fires after a widget is activated.
 */
public class ActivateEvent<T> extends GwtEvent<ActivateHandler<T>> {

  /**
   * Handler type.
   */
  private static Type<ActivateHandler<?>> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<ActivateHandler<?>> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<ActivateHandler<?>>());
  }

  private T item;
  
  public ActivateEvent(T item) {
    this.item = item;
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<ActivateHandler<T>> getAssociatedType() {
    return (Type) TYPE;
  }
  
  public T getItem() {
    return item;
  }

  @Override
  public Component getSource() {
    return (Component) super.getSource();
  }

  @Override
  protected void dispatch(ActivateHandler<T> handler) {
    handler.onActivate(this);

  }
  
  /**
   * Handler class for {@link ActivateEvent} events.
   */
  public interface ActivateHandler<T> extends EventHandler {

    /**
     * Called after the window has been activated.
     */
    void onActivate(ActivateEvent<T> event);
  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link ActivateEvent} events.
   */
  public interface HasActivateHandlers<T> {

    /**
     * Adds a {@link ActivateEvent} handler.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addActivateHandler(ActivateHandler<T> handler);

  }

}
