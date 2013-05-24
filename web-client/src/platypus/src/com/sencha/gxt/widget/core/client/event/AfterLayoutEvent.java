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
import com.sencha.gxt.widget.core.client.container.HasLayout;
import com.sencha.gxt.widget.core.client.event.AfterLayoutEvent.AfterLayoutHandler;

/**
 * Fires after a layout executes.
 */
public class AfterLayoutEvent extends GwtEvent<AfterLayoutHandler> {

  /**
   * Handler type.
   */
  private static Type<AfterLayoutHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<AfterLayoutHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<AfterLayoutHandler>();
    }
    return TYPE;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<AfterLayoutHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  /**
   * Returns the target layout.
   * 
   * @return the layout
   */

  public HasLayout getSource() {
    return (HasLayout) super.getSource();
  }

  @Override
  protected void dispatch(AfterLayoutHandler handler) {
    handler.onAfterLayout(this);
  }
  
  /**
   * Handler for {@link AfterLayoutEvent} events.
   */
  public interface AfterLayoutHandler extends EventHandler {

    /**
     * Called after a container's layout is executed.
     * 
     * @param event the {@link AfterLayoutEvent} that was fired
     */
    void onAfterLayout(AfterLayoutEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link AfterLayoutEvent} events.
   */
  public interface HasAfterLayoutHandlers {

    /**
     * Adds a {@link AfterLayoutEvent} handler.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addAfterLayoutHandler(AfterLayoutHandler handler);

  }

}
