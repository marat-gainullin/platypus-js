/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.selection;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.widget.core.client.selection.CellSelectionChangedEvent.CellSelectionChangedHandler;

/**
 * Fires after the cell selection changes.
 */
public class CellSelectionChangedEvent<M> extends GwtEvent<CellSelectionChangedHandler<M>> {

  /**
   * A widget that implements this interface is a public source of
   * {@link CellSelectionChangedEvent} events.
   * 
   * @param <M> the model type
   */
  public interface HasCellSelectionChangedHandlers<M> {

    /**
     * Adds a {@link CellSelectionChangedHandler} handler for
     * {@link CellSelectionChangedEvent} events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addCellSelectionChangedHandler(CellSelectionChangedHandler<M> handler);
  }

  /**
   * Handler class for {@link CellSelectionChangedEvent} events.
   */
  public interface CellSelectionChangedHandler<M> extends EventHandler {

    /**
     * Called after a widget's selections are changed. 
     */
    void onCellSelectionChanged(CellSelectionChangedEvent<M> event);
  }

  /**
   * Handler type.
   */
  private static Type<CellSelectionChangedHandler<?>> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<CellSelectionChangedHandler<?>> getType() {
    if (TYPE == null) {
      TYPE = new Type<CellSelectionChangedHandler<?>>();
    }
    return TYPE;
  }

  private List<CellSelection<M>> selection;
  
  public CellSelectionChangedEvent() {
    this.selection = new ArrayList<CellSelection<M>>();
  }
  
  public CellSelectionChangedEvent(CellSelection<M> selectedCell) {
    this();
    this.selection.add(selectedCell);
  }

  public CellSelectionChangedEvent(List<CellSelection<M>> selection) {
    this.selection = selection;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<CellSelectionChangedHandler<M>> getAssociatedType() {
    return (Type) TYPE;
  }

  /**
   * Returns the selection.
   * 
   * @return the selections
   */
  public List<CellSelection<M>> getSelection() {
    return selection;
  }
  
  @SuppressWarnings("unchecked")
  public StoreSelectionModel<M> getSource() {
    return (StoreSelectionModel<M>) super.getSource();
  }
  
  @Override
  protected void dispatch(CellSelectionChangedHandler<M> handler) {
    handler.onCellSelectionChanged(this);
  }

}
