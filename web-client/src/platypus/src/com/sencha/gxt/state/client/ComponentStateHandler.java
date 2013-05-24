/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.state.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.widget.core.client.Component;

/**
 * Abstract state handler for Components, capable of using the widget's stateId
 * property as a key instead of being given one.
 * 
 * Will emit a warning in hosted mode (and if enabled, in production mode) if
 * the stateId is generated, as this might change between page loads, or as the
 * application changes.
 * 
 * @param <S> the state type
 * @param <C> the component
 */
public abstract class ComponentStateHandler<S, C extends Component> extends AbstractStateHandler<S, C> {

  private static Logger logger = Logger.getLogger(ComponentStateHandler.class.getName());

  public ComponentStateHandler(Class<S> stateType, C component) {
    super(stateType, component, component.getStateId());

    if (!GWT.isProdMode() && component.getStateId().startsWith("x-widget-")) {
      logger.warning(component.getStateId() + " State handler given a widget with a generated state id, this can result in state being incorrectly applied as generated ids change");
    }
  }

  public ComponentStateHandler(Class<S> stateType, C component, String key) {
    super(stateType, component, key);
  }

}
