/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.core.client.BindingPropertySet.PropertyName;

/**
 * Configures Sencha GXT client side logging controlled by the gxt.logging.enabled
 * property.
 */
public class GXTLogConfiguration {

  @PropertyName("gxt.logging.enabled")
  interface LogConfiguration extends BindingPropertySet {
    @PropertyValue("true")
    boolean loggingIsEnabled();
  }

  /**
   * Returns true if GXT framework logging is enabled.
   * 
   * @return true if enabled
   */
  public static boolean loggingIsEnabled() {
    return impl.loggingIsEnabled();
  }

  private static LogConfiguration impl = GWT.create(LogConfiguration.class);
}
