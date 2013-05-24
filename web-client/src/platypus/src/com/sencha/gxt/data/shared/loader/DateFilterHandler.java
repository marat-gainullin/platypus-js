/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

import java.util.Date;

/**
 * A {@link FilterHandler} that provides support for <code>Date</code> values.
 */
public class DateFilterHandler extends FilterHandler<Date> {
  @Override
  public Date convertToObject(String value) {
    return new Date(Long.parseLong(value));
  }

  @Override
  public String convertToString(Date object) {
    return "" + object.getTime();
  }
}
