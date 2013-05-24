/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.data.shared.loader.LoadExceptionEvent.LoadExceptionHandler;

/**
 * Event type for loader events.
 */
public class LoadExceptionEvent<C> extends GwtEvent<LoadExceptionHandler<C>> {

  /**
   * A loader that implements this interface is a public source of
   * {@link LoadExceptionEvent} events.
   */
  public interface HasLoadExceptionHandlers<C> {

    /**
     * Adds a {@link LoadExceptionEvent} handler.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addLoadExceptionHandler(LoadExceptionHandler<C> handler);

  }

  /**
   * Handler class for {@link LoadExceptionEvent} events.
   */
  public interface LoadExceptionHandler<C> extends EventHandler {

    /**
     * Called when an exception occurs during a load operation.
     */
    void onLoadException(LoadExceptionEvent<C> event);
  }

  /**
   * Handler type.
   */
  private static Type<LoadExceptionHandler<?>> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<LoadExceptionHandler<?>> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<LoadExceptionHandler<?>>());
  }

  private C loadConfig;

  private Throwable exception;

  /**
   * Creates a load exception event for the given load config and exception.
   * 
   * @param loadConfig the load config for this load exception event
   * @param exception the exception for this load exception event
   */
  public LoadExceptionEvent(C loadConfig, Throwable exception) {
    this.loadConfig = loadConfig;
    this.exception = exception;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<LoadExceptionHandler<C>> getAssociatedType() {
    return (Type) TYPE;
  }

  /**
   * Returns the load exception.
   * 
   * @return the load exception
   */
  public Throwable getException() {
    return exception;
  }

  /**
   * Returns the load config.
   * 
   * @return the load config
   */
  public C getLoadConfig() {
    return loadConfig;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Loader<?, C> getSource() {
    return (Loader<?, C>) super.getSource();
  }

  @Override
  protected void dispatch(LoadExceptionHandler<C> handler) {
    handler.onLoadException(this);

  }

}
