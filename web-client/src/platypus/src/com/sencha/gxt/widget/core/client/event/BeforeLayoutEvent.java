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
import com.sencha.gxt.widget.core.client.container.HasLayout;
import com.sencha.gxt.widget.core.client.event.BeforeLayoutEvent.BeforeLayoutHandler;

/**
 * Fires before a layout is executed.
 */
public class BeforeLayoutEvent extends GwtEvent<BeforeLayoutHandler> implements CancellableEvent {

  /**
   * Handler type.
   */
  private static Type<BeforeLayoutHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<BeforeLayoutHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<BeforeLayoutHandler>();
    }
    return TYPE;
  }

  private boolean cancelled;

  public BeforeLayoutEvent() {
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<BeforeLayoutHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  public HasLayout getSource() {
    return (HasLayout) super.getSource();
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
  protected void dispatch(BeforeLayoutHandler handler) {
    handler.onBeforeLayout(this);
  }
  
  /**
   * Handler for {@link BeforeLayoutEvent} events.
   */
  public interface BeforeLayoutHandler extends EventHandler {

    /**
     * Called before a container's layout is executed. The action can be cancelled
     * using {@link BeforeShowContextMenuEvent#setCancelled(boolean)}.
     * 
     * @param event the {@link BeforeLayoutEvent} that was fired
     */
    void onBeforeLayout(BeforeLayoutEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link BeforeLayoutEvent} events.
   */
  public interface HasBeforeLayoutHandlers {

    /**
     * Adds a {@link BeforeLayoutEvent} handler.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addBeforeLayoutHandler(BeforeLayoutHandler handler);

  }

}
