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
import com.sencha.gxt.widget.core.client.event.MaximizeEvent.MaximizeHandler;

/**
 * Fires after a widget is maximized.
 */
public class MaximizeEvent extends GwtEvent<MaximizeHandler> {

  /**
   * Handler type.
   */
  private static Type<MaximizeHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<MaximizeHandler> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<MaximizeHandler>());
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<MaximizeHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  @Override
  public Component getSource() {
    return (Component) super.getSource();
  }

  @Override
  protected void dispatch(MaximizeHandler handler) {
    handler.onMaximize(this);
  }
  
  /**
   * Handler class for {@link MaximizeEvent} events.
   */
  public interface MaximizeHandler extends EventHandler {

    /**
     * Called when a window is maximized.
     */
    void onMaximize(MaximizeEvent event);
  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link MaximizeEvent} events.
   */
  public interface HasMaximizeHandlers {

    /**
     * Adds a {@link MaximizeEvent} handler.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addMaximizeHandler(MaximizeHandler handler);

  }

}
