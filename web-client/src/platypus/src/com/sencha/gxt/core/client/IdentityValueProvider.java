/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client;

/**
 * A read-only <code>ValueProvider</code> that simply returns the model as the
 * property value. Useful when wanting to work directly with the model, rather
 * than a property.
 * 
 * @param <T> the property type
 */
public class IdentityValueProvider<T> implements ValueProvider<T, T> {
  private final String path;

  /**
   * Creates a new value provider with an empty string for the path.
   */
  public IdentityValueProvider() {
    this("");
  }

  /**
   * Creates a new value provider.
   * 
   * @param path the path
   */
  public IdentityValueProvider(String path) {
    this.path = path;
  }

  @Override
  public void setValue(T object, T value) {

  }

  @Override
  public T getValue(T object) {
    return object;
  }

  @Override
  public String getPath() {
    return path;
  }

}
