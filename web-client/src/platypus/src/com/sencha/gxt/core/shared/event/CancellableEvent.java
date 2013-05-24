/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.shared.event;

/**
 * Interface for events that can be cancelled.
 */
public interface CancellableEvent {

  /**
   * Returns true if the event is cancelled.
   * 
   * @return true for cancelled
   */
  public boolean isCancelled();

  /**
   * True to cancel the event.
   * 
   * @param cancel true to cancel
   */
  public void setCancelled(boolean cancel);

}