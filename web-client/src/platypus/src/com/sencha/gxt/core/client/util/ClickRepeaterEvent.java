/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.util;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.core.client.util.ClickRepeaterEvent.ClickRepeaterHandler;

/**
 * Represents the click repeater event.
 */
public class ClickRepeaterEvent extends GwtEvent<ClickRepeaterHandler> {

  /**
   * Handler type.
   */
  private static Type<ClickRepeaterHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<ClickRepeaterHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<ClickRepeaterHandler>();
    }
    return TYPE;
  }

  @Override
  public Type<ClickRepeaterHandler> getAssociatedType() {
    return TYPE;
  }

  public ClickRepeater getSource() {
    return (ClickRepeater) super.getSource();
  }

  @Override
  protected void dispatch(ClickRepeaterHandler handler) {
    handler.onClick(this);
  }

  /**
   * Handler for {@link ClickRepeaterEvent} events.
   */
  public interface ClickRepeaterHandler extends EventHandler {

    /**
     * Called each time a "click" is fired by the click repeater.
     * 
     * @param event the {@link ClickRepeaterEvent} that was fired
     */
    void onClick(ClickRepeaterEvent event);

  }
  
  /**
   * A object that implements this interface is a public source of
   * {@link ClickRepeaterEvent} events.
   */
  public interface HasClickRepeaterHandlers {

    /**
     * Adds a {@link ClickRepeaterEvent} handler.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addClickHandler(ClickRepeaterHandler handler);
  }
}
