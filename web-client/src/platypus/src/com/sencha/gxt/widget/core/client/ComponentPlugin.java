/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

/**
 * Defines the interface for component plugins. The
 * {@link #initPlugin(Component)} must be called to pass the target component to
 * the plugin.
 * 
 * @param <C> the target component type
 */
public interface ComponentPlugin<C extends Component> {

  /**
   * Initializes the plugin.
   * 
   * @param component the target component
   */
  void initPlugin(C component);

}
