/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event type for loader events.
 */
public class LoadEvent<C, M> extends GwtEvent<LoadHandler<C, M>> {

  /**
   * Handler type.
   */
  private static Type<LoadHandler<?, ?>> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<LoadHandler<?, ?>> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<LoadHandler<?, ?>>());
  }

  private C loadConfig;
  private M loadResult;

  /**
   * Creates a load event with given load config and load result.
   * 
   * @param loadConfig the load config for this load event
   * @param loadResult the load result for this load event
   */
  public LoadEvent(C loadConfig, M loadResult) {
    this.loadConfig = loadConfig;
    this.loadResult = loadResult;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<LoadHandler<C, M>> getAssociatedType() {
    return (Type) TYPE;
  }

  /**
   * Returns the load config.
   * 
   * @return the load config
   */
  public C getLoadConfig() {
    return loadConfig;
  }

  /**
   * Returns the load result.
   * 
   * @return the load result
   */
  public M getLoadResult() {
    return loadResult;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Loader<M, C> getSource() {
    return (Loader<M, C>) super.getSource();
  }

  @Override
  protected void dispatch(LoadHandler<C, M> handler) {
    handler.onLoad(this);

  }

}
