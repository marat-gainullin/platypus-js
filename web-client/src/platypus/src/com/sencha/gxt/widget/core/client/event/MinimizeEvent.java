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
import com.sencha.gxt.widget.core.client.event.MinimizeEvent.MinimizeHandler;

/**
 * Fires after a widget is minimized.
 */
public class MinimizeEvent extends GwtEvent<MinimizeHandler> {

  /**
   * Handler type.
   */
  private static Type<MinimizeHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<MinimizeHandler> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<MinimizeHandler>());
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<MinimizeHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  @Override
  public Component getSource() {
    return (Component) super.getSource();
  }

  @Override
  protected void dispatch(MinimizeHandler handler) {
    handler.onMinimize(this);
  }
  
  /**
   * Handler class for {@link MinimizeEvent} events.
   */
  public interface MinimizeHandler extends EventHandler {

    /**
     * Called when a window is minimized.
     */
    void onMinimize(MinimizeEvent event);
  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link MinimizeEvent} events.
   */
  public interface HasMinimizeHandlers {

    /**
     * Adds a {@link MinimizeEvent} handler.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addMinimizeHandler(MinimizeHandler handler);

  }

}
