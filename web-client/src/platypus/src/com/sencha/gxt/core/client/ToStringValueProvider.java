/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client;

/**
 * A read-only <code>ValueProvider</code> implementation that simply call
 * {@link #toString()} on the target model. Calls to
 * {@link #setValue(Object, String)} are ignored.
 * 
 * @param <T> the model type
 */
public class ToStringValueProvider<T> implements ValueProvider<T, String> {

  private final String path;

  /**
   * Creates a new value provider with an empty string for the path.
   */
  public ToStringValueProvider() {
    this("");
  }

  /**
   * Creates a new value provider.
   * 
   * @param path the path
   */
  public ToStringValueProvider(String path) {
    this.path = path;
  }

  @Override
  public String getValue(T object) {
    return object.toString();
  }

  @Override
  public void setValue(T object, String value) {

  }

  @Override
  public String getPath() {
    return path;
  }

}
