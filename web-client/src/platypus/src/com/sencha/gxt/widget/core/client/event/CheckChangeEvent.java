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
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;

/**
 * Fires after an objects check state is changed.
 */
public class CheckChangeEvent<T> extends GwtEvent<CheckChangeHandler<T>> {

  /**
   * Handler type.
   */
  private static Type<CheckChangeHandler<?>> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<CheckChangeHandler<?>> getType() {
    if (TYPE == null) {
      TYPE = new Type<CheckChangeHandler<?>>();
    }
    return TYPE;
  }

  private CheckState state;
  private T item;

  public CheckChangeEvent(T item, CheckState state) {
    this.item = item;
    this.state = state;
  }

  public T getItem() {
    return item;
  }

  /**
   * Returns the item's check state.
   * 
   * @return the check state
   */
  public CheckState getChecked() {
    return state;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<CheckChangeHandler<T>> getAssociatedType() {
    return (Type) TYPE;
  }

  public Component getSource() {
    return (Component) super.getSource();
  }

  @Override
  protected void dispatch(CheckChangeHandler<T> handler) {
    handler.onCheckChange(this);
  }

  /**
   * Handler class for {@link CheckChangeEvent} events.
   */
  public interface CheckChangeHandler<T> extends EventHandler {

    /**
     * Called after an item's check state changes.
     */
    void onCheckChange(CheckChangeEvent<T> event);
  }

  /**
   * A widget that implements this interface is a public source of
   * {@link CheckChangeEvent} events.
   */
  public interface HasCheckChangeHandlers<T> {

    /**
     * Adds a {@link CheckChangeHandler} handler for {@link CheckChangeEvent}
     * events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addCheckChangeHandler(CheckChangeHandler<T> handler);

  }

}
