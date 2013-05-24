/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.widget.core.client.Portlet;
import com.sencha.gxt.widget.core.client.container.PortalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.PortalDropEvent.PortalDropHandler;

/**
 * Fires after a portlet is dropped.
 */
public class PortalDropEvent extends GwtEvent<PortalDropHandler> {

  /**
   * Handler type.
   */
  private static Type<PortalDropHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<PortalDropHandler> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<PortalDropHandler>());
  }

  private Portlet portlet;
  private int startColumn, startRow;
  private int column, row;

  public PortalDropEvent(Portlet portlet, int startColumn, int startRow, int column, int row) {
    this.portlet = portlet;
    this.startColumn = startColumn;
    this.startRow = startRow;
    this.column = column;
    this.row = row;
  }
  
  public int getStartColumn() {
    return startColumn;
  }

  public int getStartRow() {
    return startRow;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<PortalDropHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  public int getColumn() {
    return column;
  }

  public Portlet getPortlet() {
    return portlet;
  }

  public int getRow() {
    return row;
  }

  @Override
  public PortalLayoutContainer getSource() {
    return (PortalLayoutContainer) super.getSource();
  }

  @Override
  protected void dispatch(PortalDropHandler handler) {
    handler.onDrop(this);
  }
  
  /**
   * Handler class for {@link PortalDropEvent} events.
   */
  public interface PortalDropHandler extends EventHandler {

    /**
     * Called after a portlet is dropped.
     */
    void onDrop(PortalDropEvent event);
  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link PortalDropEvent} events.
   */
  public interface HasPortalDropHandlers {

    /**
     * Adds a {@link PortalDropHandler} handler for {@link PortalDropEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addDropHandler(PortalDropHandler handler);
  }

}
