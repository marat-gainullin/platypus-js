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
import com.sencha.gxt.fx.client.animation.CancelAnimationEvent.CancelAnimationHandler;

/**
 * Represent the cancelled animation event.
 */
public class CancelAnimationEvent extends GwtEvent<CancelAnimationHandler> {

  /**
   * Handler type.
   */
  private static Type<CancelAnimationHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<CancelAnimationHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<CancelAnimationHandler>();
    }
    return TYPE;
  }

  public CancelAnimationEvent() {
  }

  @Override
  public Type<CancelAnimationHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(CancelAnimationHandler handler) {
    handler.onCancelAnimation(this);
  }
  
  /**
   * Handler for {@link CancelAnimationEvent} events.
   */
  public interface CancelAnimationHandler extends EventHandler {

    /**
     * Called when the animation is cancelled.
     * 
     * @param event the {@link CancelAnimationEvent} that was fired
     */
    void onCancelAnimation(CancelAnimationEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link CancelAnimationEvent} events.
   */
  public interface HasCancelAnimationHandlers {

    /**
     * Adds a {@link CancelAnimationHandler} handler for
     * {@link CancelAnimationEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addCancelAnimationHandler(CancelAnimationHandler handler);

  }

}
