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
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent.BeforeShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;

/**
 * Fires before a widget's context menu is shown.
 */
public class BeforeShowContextMenuEvent extends GwtEvent<BeforeShowContextMenuHandler> implements CancellableEvent {

  /**
   * Handler type.
   */
  private static Type<BeforeShowContextMenuHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<BeforeShowContextMenuHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<BeforeShowContextMenuHandler>();
    }
    return TYPE;
  }

  private Menu menu;
  private boolean cancelled;

  public BeforeShowContextMenuEvent(Menu menu) {
    this.menu = menu;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public Type<BeforeShowContextMenuHandler> getAssociatedType() {
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
    return (Component) super.getSource();
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
  protected void dispatch(BeforeShowContextMenuHandler handler) {
    handler.onBeforeShowContextMenu(this);
  }
  
  /**
   * Handler for {@link BeforeShowContextMenuEvent} events.
   */
  public interface BeforeShowContextMenuHandler extends EventHandler {

    /**
     * Called before a widget's context menu is shown.The action can be
     * cancelled using {@link BeforeShowContextMenuEvent#setCancelled(boolean)}.
     * 
     * @param event the {@link BeforeShowContextMenuEvent} that was fired
     */
    public void onBeforeShowContextMenu(BeforeShowContextMenuEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link BeforeShowContextMenuEvent} events.
   */
  public interface HasBeforeShowContextMenuHandler {

    /**
     * Adds a {@link BeforeShowContextMenuHandler} handler for
     * {@link BeforeShowContextMenuEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addBeforeShowContextMenuHandler(BeforeShowContextMenuHandler handler);

  }

}
