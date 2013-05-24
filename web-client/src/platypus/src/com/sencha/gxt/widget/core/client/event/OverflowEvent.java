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
import com.sencha.gxt.widget.core.client.event.OverflowEvent.OverflowHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;

/**
 * Fires before the overflow menu is displayed.
 */
public class OverflowEvent extends GwtEvent<OverflowHandler> {

  /**
   * Handler class for {@link OverflowEvent} events.
   */
  public interface OverflowHandler extends EventHandler {

    /**
     * Fires before the overflow menu is displayed.
     */
    void onOverflow(OverflowEvent event);
  }

  /**
   * A widget that implements this interface is a public source of
   * {@link OverflowEvent} events.
   */
  public interface HasOverflowHandlers {

    /**
     * Adds a {@link OverflowEvent} handler.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addOverflowHandler(OverflowHandler handler);

  }

  /**
   * Handler type.
   */
  private static Type<OverflowHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<OverflowHandler> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<OverflowHandler>());
  }

  private Menu menu;

  public OverflowEvent(Menu menu) {
    this.menu = menu;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<OverflowHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  public Menu getMenu() {
    return menu;
  }

  @Override
  public Component getSource() {
    return (Component) super.getSource();
  }

  @Override
  protected void dispatch(OverflowHandler handler) {
    handler.onOverflow(this);

  }

}
