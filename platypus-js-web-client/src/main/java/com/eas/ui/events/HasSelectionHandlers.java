package com.eas.ui.events;


import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * @author mgainullin
 */
public interface HasSelectionHandlers<T> {
  /**
   * Adds a {@link SelectionEvent} handler.
   * 
   * @param handler the handler
   * @return the registration for the event
   */
  HandlerRegistration addSelectionHandler(SelectionHandler<T> handler);
}
