/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.shared.event;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Groups multiple handler registrations allowing them to be removed as a group.
 */
public class GroupingHandlerRegistration implements HandlerRegistration {

  private Set<HandlerRegistration> registrations;

  /**
   * Creates a new grouping handler registration.
   */
  public GroupingHandlerRegistration() {
    registrations = new HashSet<HandlerRegistration>();
  }

  /**
   * Adds a hander registration.
   * 
   * @param registration the handler registration
   */
  public void add(HandlerRegistration registration) {
    registrations.add(registration);
  }

  /**
   * Returns the list of handler registrations.
   * 
   * @return the list of registrations
   */
  public Set<HandlerRegistration> getRegistrations() {
    return registrations;
  }

  public void removeHandler() {
    for (HandlerRegistration r : registrations) {
      r.removeHandler();
    }
    registrations.clear();
  }

}
