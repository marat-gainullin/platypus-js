/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

/**
 * A filter configuration interface. Encapsulates state information needed for
 * filtering, including field name, type, value and comparison.
 */
@ProxyFor(FilterConfigBean.class)
public interface FilterConfig extends ValueProxy{

  /**
   * Returns the type of filter comparison.
   * 
   * @return the type of filter comparison
   */
  String getComparison();

  /**
   * Returns the name of the filter field.
   * 
   * @return the name of the filter field.
   */
  String getField();

  /**
   * Returns the filter type.
   * 
   * @return the filter type
   */
  String getType();

  /**
   * Gets the filter value. To convert from String to native representation, see
   * {@link FilterHandler#convertToObject(String)}.
   * 
   * @return the string representation of the value
   */
  String getValue();

  /**
   * Sets the type of filter comparison (e.g. "after", "before", "on").
   * 
   * @param comparison the type of filter comparison
   */
  void setComparison(String comparison);

  /**
   * Sets the name of the filter field.
   * 
   * @param field the name of the filter field
   */
  void setField(String field);

  /**
   * Sets the filter type (e.g. "boolean", "date", "list", "numeric", "string").
   * 
   * @param type the type name
   */
  void setType(String type);

  /**
   * Sets the filter value. To convert from native to String representation, see
   * {@link FilterHandler#convertToString(Object)}.
   * 
   * @param value the string representation of the value
   */
  void setValue(String value);
}
