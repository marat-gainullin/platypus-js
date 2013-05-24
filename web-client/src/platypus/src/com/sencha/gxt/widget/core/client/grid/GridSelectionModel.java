/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import java.util.Collections;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.KeyNav;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent;
import com.sencha.gxt.widget.core.client.event.RefreshEvent;
import com.sencha.gxt.widget.core.client.event.RefreshEvent.RefreshHandler;
import com.sencha.gxt.widget.core.client.event.RowClickEvent;
import com.sencha.gxt.widget.core.client.event.RowClickEvent.RowClickHandler;
import com.sencha.gxt.widget.core.client.event.RowMouseDownEvent;
import com.sencha.gxt.widget.core.client.event.RowMouseDownEvent.RowMouseDownHandler;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent.ViewReadyHandler;
import com.sencha.gxt.widget.core.client.event.XEvent;
import com.sencha.gxt.widget.core.client.grid.Grid.Callback;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.selection.AbstractStoreSelectionModel;

/**
 * Grid selection model.
 * 
 * <dl>
 * <dt>Inherited Events:</dt>
 * <dd>AbstractStoreSelectionModel BeforeSelect</dd>
 * <dd>AbstractStoreSelectionModel SelectionChange</dd>
 * </dl>
 */
public class GridSelectionModel<M> extends AbstractStoreSelectionModel<M> {

  /**
   * Determines whether a given cell is selectable.
   */
  public static class SelectionModelCallback implements Callback {

    private GridSelectionModel<?> sm;

    /**
     * Creates a selection model callback that determines whether a given cell
     * is selectable.
     * 
     * @param sm the selection model
     */
    public SelectionModelCallback(GridSelectionModel<?> sm) {
      this.sm = sm;
    }

    public boolean isSelectable(GridCell cell) {
      return sm.isSelectable(cell.getRow(), cell.getCol());
    }
  }

  private class Handler implements RowMouseDownHandler, RowClickHandler, ViewReadyHandler, RefreshHandler {

    @Override
    public void onRefresh(RefreshEvent event) {
      refresh();
      if (getLastFocused() != null) {
        grid.getView().onHighlightRow(listStore.indexOf(getLastFocused()), true);
      }
    }

    @Override
    public void onRowClick(RowClickEvent event) {
      handleRowClick(event);
    }

    @Override
    public void onRowMouseDown(RowMouseDownEvent event) {
      handleRowMouseDown(event);
    }

    @Override
    public void onViewReady(ViewReadyEvent event) {
      refresh();
    }
  }

  /**
   * True if this grid selection model supports keyboard navigation (defaults to
   * true).
   */
  protected boolean enableNavKeys = true;
  /**
   * The grid associated with this selection model.
   */
  protected Grid<M> grid;
  /**
   * True if the selection is group (defaults to false).
   */
  // TODO: Consider removing this, there are no references in our source code.
  protected boolean grouped = false;
  /**
   * The current keyboard navigator.
   */
  protected KeyNav keyNav = new KeyNav() {

    @Override
    public void onDown(NativeEvent e) {
      GridSelectionModel.this.onKeyDown(e);
    }

    @Override
    public void onKeyPress(NativeEvent ce) {
      GridSelectionModel.this.onKeyPress(ce);
    }

    public void onLeft(NativeEvent e) {
      GridSelectionModel.this.onKeyLeft(e);
    }

    public void onRight(NativeEvent e) {
      GridSelectionModel.this.onKeyRight(e);
    }

    @Override
    public void onUp(NativeEvent e) {
      GridSelectionModel.this.onKeyUp(e);
    }

  };

  /**
   * The list store for this selection model.
   */
  protected ListStore<M> listStore;
  /**
   * A group of handler registrations for this selection model.
   */
  protected GroupingHandlerRegistration handlerRegistration;

  private Handler handler = new Handler();

  @Override
  public void bind(Store<M> store) {
    super.bind(store);
    if (store instanceof ListStore) {
      listStore = (ListStore<M>) store;
    } else {
      listStore = null;
    }
  }

  /**
   * Binds the given grid to this selection model.
   * 
   * @param grid the grid to bind to this selection model
   */
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
      if (handlerRegistration == null) {
        handlerRegistration = new GroupingHandlerRegistration();
      }
      handlerRegistration.add(grid.addRowMouseDownHandler(handler));
      handlerRegistration.add(grid.addRowClickHandler(handler));
      handlerRegistration.add(grid.addViewReadyHandler(handler));
      handlerRegistration.add(grid.addRefreshHandler(handler));
      keyNav.bind(grid);
      bind(grid.getStore());
    }
  }

  /**
   * Selects the next row.
   * 
   * @param keepexisting true to keep existing selections
   */
  public void selectNext(boolean keepexisting) {
    if (hasNext()) {
      int idx = listStore.indexOf(lastSelected) + 1;
      select(idx, keepexisting);
      grid.getView().focusRow(idx);
    }
  }

  /**
   * Selects the previous row.
   * 
   * @param keepexisting true to keep existing selections
   */
  public void selectPrevious(boolean keepexisting) {
    if (hasPrevious()) {
      int idx = listStore.indexOf(lastSelected) - 1;
      select(idx, keepexisting);
      grid.getView().focusRow(idx);
    }
  }

  /**
   * Handles a row click event. The row click event is responsible for adding to
   * a selection in multiple selection mode.
   * 
   * @param event the row click event
   */
  protected void handleRowClick(RowClickEvent event) {
    if (Element.is(event.getEvent().getEventTarget())
        && !grid.getView().isSelectableTarget(Element.as(event.getEvent().getEventTarget()))) {
      return;
    }
    if (isLocked()) {
      return;
    }
    
    if (fireSelectionChangeOnClick) {
      fireSelectionChange();
      fireSelectionChangeOnClick = false;
    }

    XEvent xe = event.getEvent().<XEvent> cast();

    int rowIndex = event.getRowIndex();
    int colIndex = event.getColumnIndex();
    if (rowIndex == -1) {
      deselectAll();
      return;
    }
    if (selectionMode == SelectionMode.MULTI) {
      M sel = listStore.get(rowIndex);
      if (xe.getCtrlOrMetaKey() && isSelected(sel)) {
        doDeselect(Collections.singletonList(sel), false);
      } else if (xe.getCtrlOrMetaKey()) {
        grid.getView().focusCell(rowIndex, colIndex, false);
        doSelect(Collections.singletonList(sel), true, false);
      } else if (isSelected(sel) && !event.getEvent().getShiftKey() && !xe.getCtrlOrMetaKey() && selected.size() > 1) {
        grid.getView().focusCell(rowIndex, colIndex, false);
        doSelect(Collections.singletonList(sel), false, false);
      }
    }

  }

  /**
   * Handles a row mouse down event. The row mouse down event is responsible for
   * initiating a selection.
   * 
   * @param event the row mouse down event
   */
  protected void handleRowMouseDown(RowMouseDownEvent event) {
    if (Element.is(event.getEvent().getEventTarget())
        && !grid.getView().isSelectableTarget(Element.as(event.getEvent().getEventTarget()))) {
      return;
    }
    if (isLocked()) {
      return;
    }
    int rowIndex = event.getRowIndex();
    int colIndex = event.getColumnIndex();
    if (rowIndex == -1) {
      return;
    }
    
    mouseDown = true;

    XEvent e = event.getEvent().<XEvent> cast();

    if (event.getEvent().getButton() == Event.BUTTON_RIGHT) {
      if (selectionMode != SelectionMode.SINGLE && isSelected(listStore.get(rowIndex))) {
        return;
      }
      grid.getView().focusCell(rowIndex, colIndex, false);
      select(rowIndex, false);
    } else {
      M sel = listStore.get(rowIndex);
      if (selectionMode == SelectionMode.SIMPLE) {
        if (!isSelected(sel)) {
          grid.getView().focusCell(rowIndex, colIndex, false);
          select(sel, true);
        }

      } else if (selectionMode == SelectionMode.SINGLE) {
        if (e.getCtrlOrMetaKey() && isSelected(sel)) {
          deselect(sel);
        } else if (!isSelected(sel)) {
          grid.getView().focusCell(rowIndex, colIndex, false);
          select(sel, false);
        }
      } else if (!e.getCtrlOrMetaKey()) {
        if (event.getEvent().getShiftKey() && lastSelected != null) {
          int last = listStore.indexOf(lastSelected);
          int index = rowIndex;
          grid.getView().focusCell(index, colIndex, false);
          select(last, index, e.getCtrlOrMetaKey());
        } else if (!isSelected(sel)) {
          grid.getView().focusCell(rowIndex, colIndex, false);
          doSelect(Collections.singletonList(sel), false, false);
        }
      } else {
        // EXTGWT-2019 when inline editing for grid and tree grid with row based
        // selection model focus is not
        // being moved to grid when clicking on another cell in same row as
        // active edit and therefore
        // field is not bluring and firing change event
        grid.getView().focus();
      }
    }
    
    mouseDown = false;
  }

  /**
   * Returns true if there is an item in the list store following the most
   * recently selected item (i.e. the selected item is not the last item in the
   * list store).
   * 
   * @return true if there is an item following the most recently selected item
   */
  protected boolean hasNext() {
    return lastSelected != null && listStore.indexOf(lastSelected) < (listStore.size() - 1);
  }

  /**
   * Returns true if there is an item in the list store preceding the most
   * recently selected item (i.e. the selected item is not the first item in the
   * list store).
   * 
   * @return true if there is an item preceding the most recently selected item
   */
  protected boolean hasPrevious() {
    return lastSelected != null && listStore.indexOf(lastSelected) > 0;
  }

  /**
   * Returns true if the given cell is selectable.
   * 
   * @param row the cell's row
   * @param cell the cell's column
   * @return true if the cell is selectable
   */
  // TODO: Consider changing name, parameters or implementation (i.e. implies
  // row participates in check, which it doesn't)
  protected boolean isSelectable(int row, int cell) {
    return !grid.getColumnModel().isHidden(cell);
  }

  /**
   * Handles a key down event (e.g. as fired by the key navigator).
   * 
   * @param ne the key down event
   */
  protected void onKeyDown(NativeEvent ne) {
    XEvent e = ne.<XEvent> cast();
    if (Element.is(ne.getEventTarget()) && !grid.getView().isSelectableTarget(Element.as(ne.getEventTarget()))) {
      return;
    }
    if (!e.getCtrlOrMetaKey() && selected.size() == 0 && getLastFocused() == null) {
      select(0, false);
    } else {
      int idx = listStore.indexOf(getLastFocused());
      if (idx >= 0 && (idx + 1) < listStore.size()) {
        if (e.getCtrlOrMetaKey() || (e.getShiftKey() && isSelected(listStore.get(idx + 1)))) {
          if (!e.getCtrlOrMetaKey()) {
            deselect(idx);
          }

          M lF = listStore.get(idx + 1);
          if (lF != null) {
            setLastFocused(lF);
            grid.getView().focusCell(idx + 1, 0, false);
          }

        } else {
          if (e.getShiftKey() && lastSelected != getLastFocused()) {
            grid.getView().focusCell(idx + 1, 0, false);
            select(listStore.indexOf(lastSelected), idx + 1, true);
          } else {
            if (idx + 1 < listStore.size()) {
              grid.getView().focusCell(idx + 1, 0, false);
              selectNext(e.getShiftKey());
            }
          }
        }
      }
    }

    e.preventDefault();
  }

  /**
   * Handles a key left event (e.g. as fired by the key navigator).
   * 
   * @param e the key left event
   */
  protected void onKeyLeft(NativeEvent e) {

  }

  /**
   * Handles a key press event (e.g. as fired by the key navigator).
   * 
   * @param ne the key press event
   */
  protected void onKeyPress(NativeEvent ne) {
    if (Element.is(ne.getEventTarget()) && !grid.getView().isSelectableTarget(Element.as(ne.getEventTarget()))) {
      return;
    }

    int kc = ne.getKeyCode();

    XEvent e = ne.<XEvent> cast();

    if (lastSelected != null && enableNavKeys) {
      if (kc == KeyCodes.KEY_PAGEUP) {
        e.stopPropagation();
        e.preventDefault();
        select(0, false);
        grid.getView().focusRow(0);
      } else if (kc == KeyCodes.KEY_PAGEDOWN) {
        e.stopPropagation();
        e.preventDefault();
        int idx = listStore.indexOf(listStore.get(listStore.size() - 1));
        select(idx, false);
        grid.getView().focusRow(idx);
      }
    }
    // if space bar is pressed
    if (e.getKeyCode() == 32) {
      if (getLastFocused() != null) {
        if (e.getShiftKey() && lastSelected != null) {
          int last = listStore.indexOf(lastSelected);
          int i = listStore.indexOf(getLastFocused());
          grid.getView().focusCell(i, 0, false);
          select(last, i, e.getCtrlOrMetaKey());
        } else {
          if (isSelected(getLastFocused())) {
            deselect(getLastFocused());
          } else {
            grid.getView().focusCell(listStore.indexOf(getLastFocused()), 0, false);
            select(getLastFocused(), true);
          }
        }
      }
    }
  }

  /**
   * Handles a key right event (e.g. as fired by the key navigator).
   * 
   * @param e the key right event
   */
  protected void onKeyRight(NativeEvent e) {

  }

  /**
   * Handles a key up event (e.g. as fired by the key navigator).
   * 
   * @param e the key up event
   */
  protected void onKeyUp(NativeEvent e) {
    XEvent xe = e.<XEvent> cast();
    if (Element.is(e.getEventTarget()) && !grid.getView().isSelectableTarget(Element.as(e.getEventTarget()))) {
      return;
    }
    int idx = listStore.indexOf(getLastFocused());
    if (idx >= 1) {
      if (xe.getCtrlOrMetaKey() || (e.getShiftKey() && isSelected(listStore.get(idx - 1)))) {
        if (!xe.getCtrlOrMetaKey()) {
          deselect(idx);
        }

        M lF = listStore.get(idx - 1);
        if (lF != null) {
          setLastFocused(lF);
          grid.getView().focusCell(idx - 1, 0, false);
        }

      } else {
        if (e.getShiftKey() && lastSelected != getLastFocused()) {
          grid.getView().focusCell(idx - 1, 0, false);
          select(listStore.indexOf(lastSelected), idx - 1, true);
        } else {
          if (idx > 0) {
            grid.getView().focusCell(idx - 1, 0, false);
            selectPrevious(e.getShiftKey());
          }
        }

      }
    }
    e.preventDefault();
  }

  @Override
  protected void onLastFocusChanged(M oldFocused, M newFocused) {
    int i;
    if (oldFocused != null) {
      i = listStore.indexOf(oldFocused);
      if (i >= 0) {
        grid.getView().onHighlightRow(i, false);
      }
    }
    if (newFocused != null) {
      i = listStore.indexOf(newFocused);
      if (i >= 0) {
        grid.getView().onHighlightRow(i, true);
      }
    }
  }

  @Override
  protected void onRecordChange(StoreRecordChangeEvent<M> event) {
    super.onRecordChange(event);
    refreshRowSelection(event.getRecord().getModel());
  }

  @Override
  protected void onSelectChange(M model, boolean select) {
    int idx = listStore.indexOf(model);
    if (idx != -1) {

      if (select) {
        grid.getView().onRowSelect(idx);
      } else {
        grid.getView().onRowDeselect(idx);
      }
    }
  }

  protected void onUpdate(final M model) {
    super.onUpdate(model);
    refreshRowSelection(model);
  }

  private void refreshRowSelection(final M model) {
    Scheduler.get().scheduleFinally(new ScheduledCommand() {

      @Override
      public void execute() {
        if (isSelected(model)) {
          onSelectChange(model, true);
        }
        if (getLastFocused() == model) {
          setLastFocused(getLastFocused());
        }

      }
    });

  }
}
