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
import com.sencha.gxt.widget.core.client.event.BeforeQueryEvent.BeforeQueryHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;

/**
 * Fires before a query is executed.
 * 
 * @param <T> the type about to be closed
 */
public class BeforeQueryEvent<T> extends GwtEvent<BeforeQueryHandler<T>> implements CancellableEvent {

  /**
   * Handler class for {@link BeforeQueryEvent} events.
   */
  public interface BeforeQueryHandler<T> extends EventHandler {

    /**
     * Called before query is executed. Listeners can cancel the action by
     * calling {@link BeforeQueryEvent#setCancelled(boolean)}.
     */
    void onBeforeQuery(BeforeQueryEvent<T> event);
  }

  /**
   * A widget that implements this interface is a public source of
   * {@link BeforeQueryEvent} events.
   */
  public interface HasBeforeQueryHandlers<T> {

    /**
     * Adds a {@link BeforeQueryHandler} handler for {@link BeforeQueryEvent}
     * events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addBeforeQueryHandler(BeforeQueryHandler<T> handler);
  }

  /**
   * Handler type.
   */
  private static Type<BeforeQueryHandler<?>> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<BeforeQueryHandler<?>> getType() {
    if (TYPE == null) {
      TYPE = new Type<BeforeQueryHandler<?>>();
    }
    return TYPE;
  }

  private String query;
  private boolean cancelled;

  public BeforeQueryEvent(String query) {
    this.query = query;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<BeforeQueryHandler<T>> getAssociatedType() {
    return (Type) TYPE;
  }

  public String getQuery() {
    return query;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public ComboBox<T> getSource() {
    return (ComboBox) super.getSource();
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  @Override
  protected void dispatch(BeforeQueryHandler<T> handler) {
    handler.onBeforeQuery(this);
  }

}
