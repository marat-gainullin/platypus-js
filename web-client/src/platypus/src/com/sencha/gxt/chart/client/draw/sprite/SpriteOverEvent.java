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
import com.sencha.gxt.chart.client.draw.sprite.SpriteOverEvent.SpriteOverHandler;

/**
 * Fired when an the mouse leaves an item in the {@link Series}.
 */
public class SpriteOverEvent extends GwtEvent<SpriteOverHandler> {

  /**
   * A widget that implements this interface is a public source of
   * {@link SpriteOverEvent} events.
   */
  public interface HasSpriteOverHandlers {

    /**
     * Adds a {@link SpriteOverHandler} handler for {@link SpriteOverEvent}
     * events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addSpriteOverHandler(SpriteOverHandler handler);
  }

  /**
   * Handler class for {@link SpriteOverEvent} events.
   */
  public interface SpriteOverHandler extends EventHandler {

    /**
     * Fired when the mouse moves over the {@link Sprite}.
     * 
     * @param event the fired event
     */
    void onSpriteOver(SpriteOverEvent event);
  }

  /**
   * Handler type.
   */
  private static Type<SpriteOverHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<SpriteOverHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<SpriteOverHandler>();
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
  public SpriteOverEvent(Sprite sprite, Event event) {
    this.sprite = sprite;
    this.event = event;
  }

  @Override
  public Type<SpriteOverHandler> getAssociatedType() {
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
  protected void dispatch(SpriteOverHandler handler) {
    handler.onSpriteOver(this);
  }

}
