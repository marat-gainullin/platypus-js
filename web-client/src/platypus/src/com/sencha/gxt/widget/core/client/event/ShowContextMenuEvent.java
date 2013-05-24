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
import com.sencha.gxt.core.shared.event.CancellableEvent;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.event.ShowContextMenuEvent.ShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;

/**
 * Fires after a widget's context menu is shown.
 */
public class ShowContextMenuEvent extends GwtEvent<ShowContextMenuHandler> implements CancellableEvent {

  /**
   * Handler type.
   */
  private static Type<ShowContextMenuHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<ShowContextMenuHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<ShowContextMenuHandler>();
    }
    return TYPE;
  }

  private Menu menu;
  private boolean cancelled;

  public ShowContextMenuEvent(Menu menu) {
    this.menu = menu;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<ShowContextMenuHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  /**
   * Returns the context menu.
   * 
   * @return the context menu
   */
  public Menu getMenu() {
    return menu;
  }

  @Override
  public Component getSource() {
    return (Component)super.getSource();
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  @Override
  protected void dispatch(ShowContextMenuHandler handler) {
    handler.onShowContextMenu(this);
  }
  
  /**
   * Handler for {@link ShowContextMenuEvent} events.
   */
  public interface ShowContextMenuHandler extends EventHandler {

    /**
     * Called after a widget's context menu is shown.
     * 
     * @param event the {@link ShowContextMenuEvent} that was fired
     */
    public void onShowContextMenu(ShowContextMenuEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link ShowContextMenuEvent} events.
   */
  public interface HasShowContextMenuHandler {

    /**
     * Adds a {@link ShowContextMenuHandler} handler for
     * {@link ShowContextMenuEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addShowContextMenuHandler(ShowContextMenuHandler handler);

  }

}
