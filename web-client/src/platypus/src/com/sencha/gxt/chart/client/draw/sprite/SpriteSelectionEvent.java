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
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent.SpriteSelectionHandler;

/**
 * Fired when an the mouse leaves an item in the {@link Series}.
 */
public class SpriteSelectionEvent extends GwtEvent<SpriteSelectionHandler> {

  /**
   * A widget that implements this interface is a public source of
   * {@link SpriteSelectionEvent} events.
   */
  public interface HasSpriteSelectionHandlers {

    /**
     * Adds a {@link SpriteSelectionHandler} handler for
     * {@link SpriteSelectionEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addSpriteSelectionHandler(SpriteSelectionHandler handler);
  }

  /**
   * Handler class for {@link SpriteSelectionEvent} events.
   */
  public interface SpriteSelectionHandler extends EventHandler {

    /**
     * Fired when an the {@link Sprite} is selected.
     * 
     * @param event the fired event
     */
    void onSpriteSelect(SpriteSelectionEvent event);
  }

  /**
   * Handler type.
   */
  private static Type<SpriteSelectionHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<SpriteSelectionHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<SpriteSelectionHandler>();
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
  public SpriteSelectionEvent(Sprite sprite, Event event) {
    this.sprite = sprite;
    this.event = event;
  }

  @Override
  public Type<SpriteSelectionHandler> getAssociatedType() {
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
  protected void dispatch(SpriteSelectionHandler handler) {
    handler.onSpriteSelect(this);
  }

}
