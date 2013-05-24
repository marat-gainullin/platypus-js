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
import com.sencha.gxt.widget.core.client.event.RefreshEvent.RefreshHandler;

/**
 * Fires after a widget's data is refreshed.
 */
public class RefreshEvent extends GwtEvent<RefreshHandler> {

  /**
   * Handler type.
   */
  private static Type<RefreshHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<RefreshHandler> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<RefreshHandler>());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public Type<RefreshHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  @Override
  public Component getSource() {
    return (Component) super.getSource();
  }

  @Override
  protected void dispatch(RefreshHandler handler) {
    handler.onRefresh(this);
  }
  
  /**
   * Handler for {@link RefreshEvent} events.
   */
  public interface RefreshHandler extends EventHandler {

    /**
     * Called after a widget's data is refreshed.
     * 
     * @param event the {@link RefreshEvent} that was fired
     */
    void onRefresh(RefreshEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link RefreshEvent} events.
   */
  public interface HasRefreshHandlers {

    /**
     * Adds a {@link RefreshHandler} handler for
     * {@link RefreshEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addRefreshHandler(RefreshHandler handler);

  }

}
