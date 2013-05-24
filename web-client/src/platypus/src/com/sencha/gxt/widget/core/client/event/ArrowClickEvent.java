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
import com.sencha.gxt.widget.core.client.button.SplitButton;
import com.sencha.gxt.widget.core.client.event.ArrowClickEvent.ArrowClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;

/**
 * Fires after a button's arrow is clicked.
 */
public class ArrowClickEvent extends GwtEvent<ArrowClickHandler> {

  /**
   * Handler type.
   */
  private static Type<ArrowClickHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<ArrowClickHandler> getType() {
    if (TYPE == null) {
      TYPE = new Type<ArrowClickHandler>();
    }
    return TYPE;
  }

  private Menu menu;

  public ArrowClickEvent(Menu menu) {
    this.menu = menu;
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<ArrowClickHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  public Menu getMenu() {
    return menu;
  }

  public SplitButton getSource() {
    return (SplitButton) super.getSource();
  }

  @Override
  protected void dispatch(ArrowClickHandler handler) {
    handler.onArrowClick(this);
  }
  
  /**
   * Handler for {@link ArrowClickEvent} events.
   */
  public interface ArrowClickHandler extends EventHandler {

    /**
     * Called when the button's arrow is clicked.
     * 
     * @param event the {@link ArrowClickEvent} that was fired
     */
    void onArrowClick(ArrowClickEvent event);

  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link ArrowClickEvent} events.
   */
  public interface HasArrowClickHandlers {

    /**
     * Adds a {@link SelectHandler} handler for {@link ArrowClickEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addArrowClickHandler(ArrowClickHandler handler);
  }

}
