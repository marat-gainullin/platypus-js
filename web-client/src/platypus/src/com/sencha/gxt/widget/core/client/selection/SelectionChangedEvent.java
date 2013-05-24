/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.selection;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

/**
 * Fires after the selection changes.
 */
public class SelectionChangedEvent<M> extends GwtEvent<SelectionChangedHandler<M>> {

  /**
   * A widget that implements this interface is a public source of
   * {@link SelectionChangedEvent} events.
   * 
   * @param <M> the model type
   */
  public interface HasSelectionChangedHandlers<M> {

    /**
     * Adds a {@link SelectionChangedHandler} handler for
     * {@link SelectionChangedEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addSelectionChangedHandler(SelectionChangedHandler<M> handler);
  }

  /**
   * Handler class for {@link SelectionChangedEvent} events.
   */
  public interface SelectionChangedHandler<M> extends EventHandler {

    /**
     * Called after a widget's selections are changed. 
     */
    void onSelectionChanged(SelectionChangedEvent<M> event);
  }

  /**
   * Handler type.
   */
  private static Type<SelectionChangedHandler<?>> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<SelectionChangedHandler<?>> getType() {
    if (TYPE == null) {
      TYPE = new Type<SelectionChangedHandler<?>>();
    }
    return TYPE;
  }

  private List<M> selection;

  public SelectionChangedEvent(List<M> selection) {
    this.selection = selection;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<SelectionChangedHandler<M>> getAssociatedType() {
    return (Type) TYPE;
  }

  /**
   * Returns the selection.
   * 
   * @return the selection
   */
  public List<M> getSelection() {
    return selection;
  }
  
  @SuppressWarnings("unchecked")
  public StoreSelectionModel<M> getSource() {
    return (StoreSelectionModel<M>) super.getSource();
  }
  
  @Override
  protected void dispatch(SelectionChangedHandler<M> handler) {
    handler.onSelectionChanged(this);
  }

}
