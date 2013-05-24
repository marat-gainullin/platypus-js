/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

import java.io.Serializable;

import com.sencha.gxt.core.client.ValueProvider;

/**
 * Default implementation of the {@link FilterConfig} interface. Provides
 * a convenience method to set field and type in one operation.
 */
public class FilterConfigBean implements FilterConfig, Serializable {

  private String field;
  private String comparison;
  private String type;
  private String value;

  @Override
  public String getComparison() {
    return comparison;
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public String getType() {
    return this.type;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public void setComparison(String comparison) {
    this.comparison = comparison;
  }

  @Override
  public void setField(String field) {
    this.field = field;
  }

  /**
   * Convenience method to set both field name and type in one operation.
   * 
   * @param valueProvider the value provider. The value provider's path supplies
   *          the field name.
   * @param type the field type. The class name supplies the field type.
   */
  public <V> void setFieldAndType(ValueProvider<?, V> valueProvider, Class<? extends V> type) {
    setField(valueProvider.getPath());
    setType(type.getName());
  }

  @Override
  public void setType(String type) {
    this.type = type;
  }

  @Override
  public void setValue(String value) {
    this.value = value;
  }

}
