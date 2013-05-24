/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.widget.core.client.event.AfterLayoutEvent.AfterLayoutHandler;
import com.sencha.gxt.widget.core.client.event.BeforeLayoutEvent.BeforeLayoutHandler;


public abstract class LayoutHandler implements BeforeLayoutHandler, AfterLayoutHandler {

  @Override
  public void onAfterLayout(AfterLayoutEvent event) {
  }

  @Override
  public void onBeforeLayout(BeforeLayoutEvent event) {
  }
  
  /**
   * A layout that implements this interface is a public source of
   * {@link BeforeLayoutEvent} and {@link AfterLayoutEvent} events.
   */
  public interface HasLayoutHandlers {

    /**
     * Adds a {@link LayoutHandler} handler for {@link AfterLayoutEvent} and
     * {@link BeforeLayoutEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    public HandlerRegistration addLayoutHandler(LayoutHandler handler);
  }
}
