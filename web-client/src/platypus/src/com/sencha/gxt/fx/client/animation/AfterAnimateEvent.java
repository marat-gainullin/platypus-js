/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.animation;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.fx.client.animation.AfterAnimateEvent.AfterAnimateHandler;

/**
 * Represent the after animate event.
 */
public class AfterAnimateEvent extends GwtEvent<AfterAnimateHandler> {

  /**
   * Handler type.
   */
  private static Type<AfterAnimateHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<AfterAnimateHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<AfterAnimateHandler>();
    }
    return TYPE;
  }

  public AfterAnimateEvent() {
  }

  @Override
  public Type<AfterAnimateHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(AfterAnimateHandler handler) {
    handler.onAfterAnimate(this);
  }
  
  /**
   * Handler for {@link AfterAnimateEvent} events.
   */
  public interface AfterAnimateHandler extends EventHandler {

    /**
     * Called when the animation is complete.
     * 
     * @param event the {@link AfterAnimateEvent} that was fired
     */
    void onAfterAnimate(AfterAnimateEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link AfterAnimateEvent} events.
   */
  public interface HasAfterAnimateHandlers {

    /**
     * Adds a {@link AfterAnimateHandler} handler for {@link AfterAnimateEvent}
     * events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addAfterAnimateHandler(AfterAnimateHandler handler);

  }

}
