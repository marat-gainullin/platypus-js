/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.widget.core.client.event.CellMouseDownEvent;
import com.sencha.gxt.widget.core.client.event.CellMouseDownEvent.CellMouseDownHandler;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent.ViewReadyHandler;
import com.sencha.gxt.widget.core.client.event.XEvent;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.selection.CellSelection;
import com.sencha.gxt.widget.core.client.selection.CellSelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.CellSelectionChangedEvent.CellSelectionChangedHandler;
import com.sencha.gxt.widget.core.client.selection.CellSelectionChangedEvent.HasCellSelectionChangedHandlers;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

/**
 * Cell based selection model. User {@link #selectCell(int, int)} to select a
 * cell and {@link #deselectAll()} to deselect.
 * 
 * <p />
 * CellSelectionModel extends GridSelectionModel. However, CellSelectionModel
 * only supports cell selections, no row based selections or events are
 * supported. Use
 * {@link #addCellSelectionChangedHandler(CellSelectionChangedHandler)} to be
 * notified of selection changes.
 * 
 * @param <M> the model type
 */
public class CellSelectionModel<M> extends GridSelectionModel<M> implements HasCellSelectionChangedHandlers<M> {

  private class Handler implements ViewReadyHandler, CellMouseDownHandler {

    @Override
    public void onCellMouseDown(CellMouseDownEvent event) {
      handleMouseDown(event);
    }

    @Override
    public void onViewReady(ViewReadyEvent event) {
      if (selection != null) {
        CellSelection<M> sel = selection;
        selection = null;
        selectCell(sel.getRow(), sel.getCell());
      }
    }
  }

  protected CellSelection<M> selection;

  private SelectionModelCallback callback = new SelectionModelCallback(this);
  private Handler handler = new Handler();

  @Override
  public HandlerRegistration addCellSelectionChangedHandler(CellSelectionChangedHandler<M> handler) {
    return ensureHandlers().addHandler(CellSelectionChangedEvent.getType(), handler);
  }
  
  @Override
  public HandlerRegistration addSelectionChangedHandler(SelectionChangedHandler<M> handler) {
    throw new UnsupportedOperationException("CellSelectionModel does not support row based selections");
  }
  
  @Override
  public HandlerRegistration addSelectionHandler(SelectionHandler<M> handler) {
    throw new UnsupportedOperationException("CellSelectionModel does not support row based selections");
  }

  @Override
  public void bindGrid(Grid<M> grid) {
    if (this.grid != null) {
      if (handlerRegistration != null) {
        handlerRegistration.removeHandler();
        handlerRegistration = null;
      }

      keyNav.bind(null);
      bind(null);
    }
    this.grid = grid;
    if (grid != null) {
      grid.view.setTrackMouseOver(false);
      handlerRegistration = new GroupingHandlerRegistration();

      handlerRegistration.add(grid.addViewReadyHandler(handler));
      handlerRegistration.add(grid.addCellMouseDownHandler(handler));
      keyNav.bind(grid);
      bind(grid.getStore());
    }
  }

  @Override
  public void deselect(int index) {
    throw new UnsupportedOperationException("CellSelectionModel does not support row based selections");
  }

  @Override
  public void deselect(int start, int end) {
    throw new UnsupportedOperationException("CellSelectionModel does not support row based selections");
  }

  @Override
  public void deselect(List<M> items) {
    throw new UnsupportedOperationException("CellSelectoinModel does not support row based selections");
  }

  @Override
  public void deselect(M item) {
    if (selection != null && listStore.hasMatchingKey(selection.getModel(), item)) {
      deselectAll();
    }
  }

  @Override
  public void deselect(M... items) {
    throw new UnsupportedOperationException("CellSelectionModel does not support row based selections");
  }

  @Override
  public void deselectAll() {
    if (selection != null) {
      // index may change with tree grid on expand / collapse
      // ask store for current row index
      int row = listStore.indexOf(selection.getModel());
      if (grid.isViewReady()) {
        grid.getView().onCellDeselect(row, selection.getCell());
        fireEvent(new CellSelectionChangedEvent<M>());
      }
      selection = null;
    }
  }

  /**
   * Returns the selected cell or null.
   * 
   * @return the selected cell or null if no selections
   */
  public CellSelection<M> getSelectCell() {
    return selection;
  }

  @Override
  public M getSelectedItem() {
    return selection != null ? selection.getModel() : null;
  }

  @Override
  public List<M> getSelectedItems() {
    return selection != null ? Collections.singletonList(selection.getModel()) : new ArrayList<M>();
  }

  @Override
  public void select(boolean keepExisting, M... items) {
    throw new UnsupportedOperationException("CellSelectionModel does not support row based selections");
  }

  @Override
  public void select(int index, boolean keepExisting) {
    throw new UnsupportedOperationException("CellSelectionModel does not support row based selections");
  }

  @Override
  public void select(int start, int end, boolean keepExisting) {
    throw new UnsupportedOperationException("CellSelectoinModel does not support row based selections");
  }

  @Override
  public void select(List<M> items, boolean keepExisting) {
    throw new UnsupportedOperationException("CellSelectionModel does not support row based selections");
  }

  @Override
  public void select(M item, boolean keepExisting) {
    throw new UnsupportedOperationException("CellSelectionModel does not support row based selections");
  }

  @Override
  public void selectAll() {
    throw new UnsupportedOperationException("CellSelectionModel does not support row based selections");
  }

  /**
   * Selects the cell.
   * 
   * @param row the row index
   * @param cell the cell index
   */
  public void selectCell(int row, int cell) {
    M m = listStore.get(row);
    if (m != null) {
      if (selection != null) {
        deselectAll();
      }

      selection = new CellSelection<M>(m, row, cell);
      if (grid.isViewReady()) {
        grid.getView().onCellSelect(row, cell);
        grid.getView().focusCell(row, cell, true);
        fireEvent(new CellSelectionChangedEvent<M>(selection));
      }
    }
  }

  protected void handleMouseDown(CellMouseDownEvent event) {
    XEvent e = event.getEvent().<XEvent> cast();
    if (e.getButton() != Event.BUTTON_LEFT || isLocked()) {
      return;
    }
    if (selection != null && selection.getRow() == event.getRowIndex() && selection.getCell() == event.getCellIndex()
        && e.getCtrlOrMetaKey()) {
      deselectAll();
      return;
    }
    selectCell(event.getRowIndex(), event.getCellIndex());
  }

  @Override
  protected void onKeyDown(NativeEvent e) {

  }

  @Override
  protected void onKeyPress(NativeEvent e) {
    if (Element.is(e.getEventTarget()) && !grid.getView().isSelectableTarget(Element.as(e.getEventTarget()))) {
      return;
    }

    if (selection == null) {
      e.preventDefault();
      e.stopPropagation();
      GridCell cell = grid.walkCells(0, 0, 1, callback);
      if (cell != null) {
        selectCell(cell.getRow(), cell.getCol());
      }
      return;
    }

    int r = selection.getRow();
    int c = selection.getCell();

    GridCell newCell = null;

    switch (e.getKeyCode()) {
      case KeyCodes.KEY_HOME:
        if (enableNavKeys) {
          newCell = grid.walkCells(selection.getRow(), 0, 1, callback);
        }
        break;
      case KeyCodes.KEY_END:
        if (enableNavKeys) {
          newCell = grid.walkCells(selection.getRow(), grid.getColumnModel().getColumnCount() - 1, -1, callback);
        }
        break;
      case KeyCodes.KEY_PAGEUP:
        if (enableNavKeys) {
          newCell = grid.walkCells(0, 0, 1, callback);
        }
        break;
      case KeyCodes.KEY_PAGEDOWN:
        if (enableNavKeys) {
          int idx = listStore.indexOf(listStore.get(listStore.size() - 1));
          newCell = grid.walkCells(idx, 0, 1, callback);
        }
        break;
      case KeyCodes.KEY_TAB:
        if (e.getShiftKey()) {
          newCell = grid.walkCells(r, c - 1, -1, callback);
        } else {
          newCell = grid.walkCells(r, c + 1, 1, callback);
        }
        break;
      case KeyCodes.KEY_DOWN: {
        newCell = grid.walkCells(r + 1, c, 1, callback);
        break;
      }
      case KeyCodes.KEY_UP: {
        newCell = grid.walkCells(r - 1, c, -1, callback);
        break;
      }
      case KeyCodes.KEY_LEFT:
        newCell = grid.walkCells(r, c - 1, -1, callback);
        break;
      case KeyCodes.KEY_RIGHT:
        newCell = grid.walkCells(r, c + 1, 1, callback);
        break;
    }

    if (newCell != null) {
      selectCell(newCell.getRow(), newCell.getCol());
      e.preventDefault();
      e.stopPropagation();
    }
  }

  @Override
  protected void onKeyUp(NativeEvent e) {

  }

  @Override
  protected void onUpdate(M model) {
    if (selection != null && selection.getModel() == model) {
      grid.getView().onCellSelect(selection.getRow(), selection.getCell());
      grid.getView().focusCell(selection.getRow(), selection.getCell(), true);
    }
  }
}
