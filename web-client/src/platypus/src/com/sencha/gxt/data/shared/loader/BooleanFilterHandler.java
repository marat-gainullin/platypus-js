/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

/**
 * A {@link FilterHandler} that provides support for <code>Boolean</code> values.
 */
public class BooleanFilterHandler extends FilterHandler<Boolean> {

  @Override
  public Boolean convertToObject(String value) {
    return new Boolean(value);
  }

  @Override
  public String convertToString(Boolean object) {
    return object.toString();
  }
}
