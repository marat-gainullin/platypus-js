/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import com.google.gwt.resources.client.ImageResource;

/**
 * Implemented by objects that have icons. It allows the current value of the
 * icon to be set or retrieved.
 */
public interface HasIcon {

  /**
   * Returns the icon.
   * 
   * @return the icon
   */
  ImageResource getIcon();

  /**
   * Sets the icon.
   * 
   * @param icon the icon
   */
  void setIcon(ImageResource icon);

}
