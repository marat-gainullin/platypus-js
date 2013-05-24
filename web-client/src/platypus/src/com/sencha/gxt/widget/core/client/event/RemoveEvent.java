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
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.event.RemoveEvent.RemoveHandler;

/**
 * Fires after a widget is removed from a container.
 */
public class RemoveEvent extends GwtEvent<RemoveHandler> {

  /**
   * Handler type.
   */
  private static Type<RemoveHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<RemoveHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<RemoveHandler>();
    }
    return TYPE;
  }

  private Widget widget;

  public RemoveEvent(Widget widget) {
    this.widget = widget;
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Type<RemoveHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  public Container getSource() {
    return (Container) super.getSource();
  }

  /**
   * Returns the widget that was removed.
   * 
   * @return the removed widget
   */
  public Widget getWidget() {
    return widget;
  }

  @Override
  protected void dispatch(RemoveHandler handler) {
    handler.onRemove(this);
  }
  
  /**
   * Handler for {@link RemoveEvent} events.
   */
  public interface RemoveHandler extends EventHandler {

    /**
     * Called before a widget is removed to a container.
     * 
     * @param event the {@link RemoveEvent} that was fired
     */
    void onRemove(RemoveEvent event);

  }
  
  public interface HasRemoveHandlers {

    /**
     * Adds a {@link RemoveHandler} handler for
     * {@link RemoveEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addRemoveHandler(RemoveHandler handler);
  }

}
