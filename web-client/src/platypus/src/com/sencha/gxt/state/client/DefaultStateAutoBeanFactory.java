/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.state.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.sencha.gxt.state.client.BorderLayoutStateHandler.BorderLayoutState;
import com.sencha.gxt.state.client.GridStateHandler.GridState;
import com.sencha.gxt.state.client.TreeStateHandler.TreeState;

/**
 * Default <code>AutoBeanFactory</code> used by the {@link StateManager}. The
 * auto bean factory is specified using a module rule:
 * &lt;set-configuration-property name="GXT.state.autoBeanFactory"
 * value="com.sencha.gxt.state.client.DefaultStateAutoBeanFactory" />.
 * 
 * <p />
 * To add additional beans to the factory, this interface should be extended.
 * The new interface should then be specified in your applications module file
 * to 'override' the current rule.
 */
public interface DefaultStateAutoBeanFactory extends AutoBeanFactory {
  
  AutoBean<TreeState> tree();

  AutoBean<BorderLayoutState> borderLayout();

  AutoBean<GridState> grid();
}
