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
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;

public abstract class GridEditingEvent<M, H extends EventHandler> extends GwtEvent<H> {
  private final GridCell editCell;

  public GridEditingEvent(GridCell editCell) {
    this.editCell = editCell;
  }

  public GridCell getEditCell() {
    return editCell;
  }

  @SuppressWarnings("unchecked")
  @Override
  public GridEditing<M> getSource() {
    return (GridEditing<M>) super.getSource();
  }
}