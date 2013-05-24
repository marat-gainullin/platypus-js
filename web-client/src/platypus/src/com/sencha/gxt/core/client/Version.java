/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client;

import com.google.gwt.i18n.client.Constants;

/**
 * Contains the current codes release information. Use {@link GXT#getVersion()} to get
 * an instance of this class.
 */
public interface Version extends Constants {
  /**
   * Returns the release name.
   * 
   * @return the release name
   */
  String getRelease();

  /**
   * Returns the build time.
   * 
   * @return the build time
   */
  String getBuildTime();

}
