/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared;

import com.google.gwt.resources.client.ImageResource;

/**
 * Provides a icon for the given object.
 * 
 * @param <M> the target object type
 */
public interface IconProvider<M> {

  /**
   * Returns the icon for the given model.
   * 
   * @param model the target model
   * @return the icon
   */
  ImageResource getIcon(M model);

}
