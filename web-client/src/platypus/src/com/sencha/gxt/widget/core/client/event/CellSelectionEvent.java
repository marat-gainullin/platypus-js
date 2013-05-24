/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.event;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Fires after a selection occurs.
 * 
 * @param <T> the type about to be selected
 */
public class CellSelectionEvent<T> extends SelectionEvent<T> {

  public static <T> CellSelectionEvent<T> fire(HandlerManager manager, Context context, T item) {
    if (manager.isEventHandled(SelectionEvent.getType())) {
      CellSelectionEvent<T> event = new CellSelectionEvent<T>(context, item);
      manager.fireEvent(event);
      return event;
    }
    return null;
  }
  
  public static <T> CellSelectionEvent<T> fire(HasSelectionHandlers<T> handler, Context context, T item) {
    CellSelectionEvent<T> event = new CellSelectionEvent<T>(context, item);
    handler.fireEvent(event);
    return event;
  }

  private Context context;

  /**
   * Creates a new selection event.
   * 
   * @param context the cell context
   * @param item the selected item
   */
  protected CellSelectionEvent(Context context, T item) {
    super(item);
    this.context = context;
  }

  /**
   * Returns the cell context.
   * 
   * @return the cell context
   */
  public Context getContext() {
    return context;
  }

}
