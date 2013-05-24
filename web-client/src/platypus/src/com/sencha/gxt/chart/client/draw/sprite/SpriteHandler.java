/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.chart.client.draw.sprite;

import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOutEvent.SpriteOutHandler;
import com.sencha.gxt.chart.client.draw.sprite.SpriteOverEvent.SpriteOverHandler;
import com.sencha.gxt.chart.client.draw.sprite.SpriteSelectionEvent.SpriteSelectionHandler;

/**
 * Aggregating handler interface for:
 * 
 * <dl>
 * <dd>{@link SpriteSelectionEvent}</b></dd>
 * <dd>{@link SpriteOutEvent}</b></dd>
 * <dd>{@link SpriteOverEvent}</b></dd>
 * </dl>
 */
public interface SpriteHandler extends SpriteSelectionHandler, SpriteOutHandler, SpriteOverHandler {

  /**
   * A widget that implements this interface is a public source of
   * {@link SpriteSelectionEvent}, {@link SpriteOutEvent} and
   * {@link SpriteOverEvent} events.
   */
  public interface HasSpriteHandlers {

    /**
     * Adds a {@link SpriteHandler} handler for {@link SpriteSelectionEvent},
     * {@link SpriteOutEvent}, {@link SpriteOverEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addSpriteHandler(SpriteHandler handler);

  }
}
