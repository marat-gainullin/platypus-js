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
import com.sencha.gxt.core.shared.event.CancellableEvent;
import com.sencha.gxt.widget.core.client.Portlet;
import com.sencha.gxt.widget.core.client.container.PortalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.PortalValidateDropEvent.PortalValidateDropHandler;

/**
 * Fires while a portlet is dragged to a new location.
 */
public class PortalValidateDropEvent extends GwtEvent<PortalValidateDropHandler> implements CancellableEvent {

  /**
   * Handler type.
   */
  private static Type<PortalValidateDropHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<PortalValidateDropHandler> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<PortalValidateDropHandler>());
  }

  private boolean cancelled;
  private Portlet portlet;
  private int startColumn, startRow;
  private int column, row;

  public PortalValidateDropEvent(Portlet portlet, int startColumn, int startRow, int column, int row) {
    this.portlet = portlet;
    this.startColumn = startColumn;
    this.startRow = startRow;
    this.column = column;
    this.row = row;
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<PortalValidateDropHandler> getAssociatedType() {
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
    return (PortalLayoutContainer)super.getSource();
  }

  public int getStartColumn() {
    return startColumn;
  }

  public int getStartRow() {
    return startRow;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    this.cancelled = cancel;
  }

  @Override
  protected void dispatch(PortalValidateDropHandler handler) {
    handler.onValidateDrop(this);
  }
  
  /**
   * Handler class for {@link PortalValidateDropEvent} events.
   */
  public interface PortalValidateDropHandler extends EventHandler {

    /**
     * Called before a portlet is dropped.
     */
    void onValidateDrop(PortalValidateDropEvent event);
  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link PortalValidateDropEvent} events.
   */
  public interface HasPortalValidateDropHandlers {

    /**
     * Adds a {@link PortalValidateDropHandler} handler for {@link PortalValidateDropEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addValidateDropHandler(PortalValidateDropHandler handler);
  }

}
