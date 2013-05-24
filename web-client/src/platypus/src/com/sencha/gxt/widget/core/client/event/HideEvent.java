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
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

/**
 * Fires after a widget is hidden.
 */
public class HideEvent extends GwtEvent<HideHandler> {

  /**
   * Handler type.
   */
  private static Type<HideHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<HideHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<HideHandler>();
    }
    return TYPE;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<HideHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  public Component getSource() {
    return (Component) super.getSource();
  }

  @Override
  protected void dispatch(HideHandler handler) {
    handler.onHide(this);
  }
  
  /**
   * Handler for {@link HideEvent} events.
   */
  public interface HideHandler extends EventHandler {

    /**
     * Called after a widget is hidden.
     * 
     * @param event the {@link HideEvent} that was fired
     */
    void onHide(HideEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link HideEvent} events.
   */
  public interface HasHideHandlers {

    /**
     * Adds a {@link HideHandler} handler for {@link HideEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addHideHandler(HideHandler handler);

  }

}
