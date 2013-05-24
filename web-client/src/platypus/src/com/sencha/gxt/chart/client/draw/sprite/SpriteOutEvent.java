/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw.sprite;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.chart.client.chart.series.Series;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOutEvent.SpriteOutHandler;

/**
 * Fired when an the mouse leaves an item in the {@link Series}.
 */
public class SpriteOutEvent extends GwtEvent<SpriteOutHandler> {

  /**
   * A widget that implements this interface is a public source of
   * {@link SpriteOutEvent} events.
   */
  public interface HasSpriteOutHandlers {

    /**
     * Adds a {@link SpriteOutHandler} handler for {@link SpriteOutEvent}
     * events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addSpriteOutHandler(SpriteOutHandler handler);
  }

  /**
   * Handler class for {@link SpriteOutEvent} events.
   */
  public interface SpriteOutHandler extends EventHandler {

    /**
     * Fired when an the mouse leaves an item in the {@link Series}.
     * 
     * @param event the fired event
     */
    void onSpriteLeave(SpriteOutEvent event);
  }

  /**
   * Handler type.
   */
  private static Type<SpriteOutHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<SpriteOutHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<SpriteOutHandler>();
    }
    return TYPE;
  }

  private final Sprite sprite;
  private final Event event;

  /**
   * Creates a new event with the given sprite.
   * 
   * @param sprite the sprite that caused the event
   */
  public SpriteOutEvent(Sprite sprite, Event event) {
    this.sprite = sprite;
    this.event = event;
  }

  @Override
  public Type<SpriteOutHandler> getAssociatedType() {
    return getType();
  }

  /**
   * Returns the browser event that initiated the selection event.
   * 
   * @return the browser event that initiated the selection event
   */
  public Event getBrowserEvent() {
    return event;
  }

  /**
   * Returns the sprite that caused the event.
   * 
   * @return the sprite that caused the event
   */
  public Sprite getSprite() {
    return sprite;
  }

  @Override
  protected void dispatch(SpriteOutHandler handler) {
    handler.onSpriteLeave(this);
  }

}
