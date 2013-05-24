/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.state.client;

import com.google.gwt.core.client.Callback;

/**
 * Abstract base class for state provider implementations. Providers are stores
 * of String->String pairs, so that a given String key may be used to persist a
 * String value, and that same key later can be used to load that value again.
 * 
 * An empty string should be considered no value at all, so setValue(key, "") is
 * equivalent to setValue(key, null), as to clear(key).
 */
public abstract class Provider {

  protected StateManager manager = StateManager.get();

  /**
   * Clears the named value.
   * 
   * @param name the property name
   */
  public void clear(String name) {
    setValue(name, "");
  }

  /**
   * Returns the value asynchronously.
   * 
   * @param name the property name
   * @param callback the callback
   */
  public abstract void getValue(String name, Callback<String, Throwable> callback);

  /**
   * Sets the value.
   * 
   * @param name the property name
   * @param value the value
   */
  public abstract void setValue(String name, String value);

  protected void bind(StateManager manager) {
    this.manager = manager;
  }

}
