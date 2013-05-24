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
import com.sencha.gxt.widget.core.client.event.ValidEvent.ValidHandler;
import com.sencha.gxt.widget.core.client.form.Field;

/**
 * Fires after a field value is marked valid.
 */
public class ValidEvent extends GwtEvent<ValidHandler> {

  /**
   * Handler type.
   */
  private static Type<ValidHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<ValidHandler> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<ValidHandler>());
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<ValidHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  @Override
  public Field<?> getSource() {
    return (Field<?>) super.getSource();
  }

  @Override
  protected void dispatch(ValidHandler handler) {
    handler.onValid(this);

  }
  
  /**
   * Handler class for {@link ValidEvent} events.
   */
  public interface ValidHandler extends EventHandler {

    /**
     * Called when a field becomes valid.
     */
    void onValid(ValidEvent event);
  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link ValidEvent} events.
   */
  public interface HasValidHandlers {

    /**
     * Adds a {@link ValidEvent} handler for {@link ValidEvent}
     * events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addValidHandler(ValidHandler handler);

  }

}
