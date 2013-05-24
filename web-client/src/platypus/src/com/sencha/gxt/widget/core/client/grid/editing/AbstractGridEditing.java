/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid.editing;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.util.KeyNav;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.data.shared.Converter;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseItemEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseItemEvent.BeforeCollapseItemHandler;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseItemEvent.HasBeforeCollapseItemHandlers;
import com.sencha.gxt.widget.core.client.event.BeforeExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.BeforeExpandItemEvent.BeforeExpandItemHandler;
import com.sencha.gxt.widget.core.client.event.BeforeExpandItemEvent.HasBeforeExpandItemHandlers;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent.BeforeStartEditHandler;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent.CancelEditHandler;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent.HasCollapseItemHandlers;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent.ColumnWidthChangeHandler;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent.HasExpandItemHandlers;
import com.sencha.gxt.widget.core.client.event.HeaderMouseDownEvent;
import com.sencha.gxt.widget.core.client.event.HeaderMouseDownEvent.HeaderMouseDownHandler;
import com.sencha.gxt.widget.core.client.event.ReconfigureEvent;
import com.sencha.gxt.widget.core.client.event.ReconfigureEvent.ReconfigureHandler;
import com.sencha.gxt.widget.core.client.event.StartEditEvent;
import com.sencha.gxt.widget.core.client.event.StartEditEvent.StartEditHandler;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.Callback;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;

public abstract class AbstractGridEditing<M> implements GridEditing<M> {

  protected class AbstractGridEditingKeyNav extends KeyNav {
    @Override
    public void onEnter(NativeEvent evt) {
      AbstractGridEditing.this.onEnter(evt);
    }

    @Override
    public void onEsc(NativeEvent evt) {
      AbstractGridEditing.this.onEsc(evt);
    }
  }

  protected class Handler implements AttachEvent.Handler, ScrollHandler, ClickHandler, DoubleClickHandler,
      MouseDownHandler, MouseUpHandler, BeforeExpandItemHandler<M>, BeforeCollapseItemHandler<M>,
      HeaderMouseDownHandler, ReconfigureHandler, ColumnWidthChangeHandler {

    @Override
    public void onAttachOrDetach(AttachEvent event) {
      AbstractGridEditing.this.onAttachOrDetach(event);

    }

    @Override
    public void onBeforeCollapse(BeforeCollapseItemEvent<M> event) {
      completeEditing();
    }

    @Override
    public void onBeforeExpand(BeforeExpandItemEvent<M> event) {
      completeEditing();
    }

    @Override
    public void onClick(ClickEvent event) {
      AbstractGridEditing.this.onClick(event);

    }

    @Override
    public void onColumnWidthChange(ColumnWidthChangeEvent event) {
      completeEditing();
    }

    @Override
    public void onDoubleClick(DoubleClickEvent event) {
      AbstractGridEditing.this.onDoubleClick(event);

    }

    @Override
    public void onHeaderMouseDown(HeaderMouseDownEvent event) {
      completeEditing();
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
      AbstractGridEditing.this.onMouseDown(event);
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
      AbstractGridEditing.this.onMouseUp(event);
    }

    @Override
    public void onReconfigure(ReconfigureEvent event) {
      AbstractGridEditing.this.onReconfigure(event);
    }

    @Override
    public void onScroll(ScrollEvent event) {
      AbstractGridEditing.this.onScroll(event);
    }

  }

  protected GridCell activeCell;

  protected Callback callback = new Callback() {

    @Override
    public boolean isSelectable(GridCell cell) {
      if (editableGrid != null) {
        ColumnModel<M> cm = editableGrid.getColumnModel();
        return !cm.isHidden(cell.getCol()) && editorMap.containsKey(cm.getColumn(cell.getCol()));
      }
      return false;
    }
  };

  protected ColumnModel<M> columnModel;
  protected Map<ColumnConfig<M, ?>, Converter<?, ?>> converterMap = new HashMap<ColumnConfig<M, ?>, Converter<?, ?>>();
  protected Grid<M> editableGrid;
  protected Map<ColumnConfig<M, ?>, Field<?>> editorMap = new HashMap<ColumnConfig<M, ?>, Field<?>>();
  protected GroupingHandlerRegistration groupRegistration;
  protected Handler handler;
  protected KeyNav keyNav;

  private ClicksToEdit clicksToEdit = ClicksToEdit.ONE;
  private HandlerManager handlerManager;

  @Override
  public HandlerRegistration addBeforeStartEditHandler(BeforeStartEditHandler<M> handler) {
    return ensureHandlers().addHandler(BeforeStartEditEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addCancelEditHandler(CancelEditHandler<M> handler) {
    return ensureHandlers().addHandler(CancelEditEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addCompleteEditHandler(CompleteEditHandler<M> handler) {
    return ensureHandlers().addHandler(CompleteEditEvent.getType(), handler);
  }

  @Override
  public <N, O> void addEditor(ColumnConfig<M, N> columnConfig, Converter<N, O> converter, Field<O> field) {
    assert columnConfig != null && field != null : "You have to defind a columnConfig and a field";
    if (converter != null) {
      converterMap.put(columnConfig, converter);
    } else {
      converterMap.remove(columnConfig);
    }
    editorMap.put(columnConfig, field);
  }

  public <N> void addEditor(ColumnConfig<M, N> columnConfig, Field<N> field) {
    addEditor(columnConfig, null, field);
  }

  @Override
  public HandlerRegistration addStartEditHandler(StartEditHandler<M> handler) {
    return ensureHandlers().addHandler(StartEditEvent.getType(), handler);
  }

  @Override
  public abstract void cancelEditing();

  /**
   * Clears the editors.
   */
  public void clearEditors() {
    editorMap.clear();
    converterMap.clear();
  }

  @Override
  public abstract void completeEditing();

  @Override
  public void fireEvent(GwtEvent<?> event) {
    if (handlerManager != null) {
      handlerManager.fireEvent(event);
    }
  }

  /**
   * Returns the active cell.
   * 
   * @return the active cell or null if no active edit
   */
  public GridCell getActiveCell() {
    return activeCell;
  }

  /**
   * Returns the clicks to edit.
   * 
   * @return the clicks to edit
   */
  public ClicksToEdit getClicksToEdit() {
    return clicksToEdit;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N, O> Converter<N, O> getConverter(ColumnConfig<M, N> columnConfig) {
    return (Converter<N, O>) converterMap.get(columnConfig);
  }

  @Override
  public Grid<M> getEditableGrid() {
    return editableGrid;
  }

  @SuppressWarnings("unchecked")
  public <O> Field<O> getEditor(ColumnConfig<M, ?> columnConfig) {
    return (Field<O>) editorMap.get(columnConfig);
  }

  @Override
  public boolean isEditing() {
    return activeCell != null;
  }

  @Override
  public void removeEditor(ColumnConfig<M, ?> columnConfig) {
    editorMap.remove(columnConfig);
    converterMap.remove(columnConfig);
  }

  /**
   * Sets the number of clicks to edit (defaults to ONE).
   * 
   * @param clicksToEdit the clicks to edit
   */
  public void setClicksToEdit(ClicksToEdit clicksToEdit) {
    this.clicksToEdit = clicksToEdit;
  }

  @Override
  public void setEditableGrid(Grid<M> editableGrid) {
    cancelEditing();
    if (groupRegistration != null) {
      groupRegistration.removeHandler();
      groupRegistration = null;
    }
    this.editableGrid = editableGrid;
    this.columnModel = editableGrid == null ? null : editableGrid.getColumnModel();
    if (keyNav != null && editableGrid == null) {
      keyNav.bind(null);
    } else {
      ensureInternalKeyNav().bind(editableGrid);
    }
    if (editableGrid != null) {
      GroupingHandlerRegistration reg = new GroupingHandlerRegistration();
      reg.add(editableGrid.addDomHandler(ensureInternHandler(), ClickEvent.getType()));
      reg.add(editableGrid.addDomHandler(ensureInternHandler(), MouseDownEvent.getType()));
      reg.add(editableGrid.addDomHandler(ensureInternHandler(), MouseUpEvent.getType()));
      reg.add(editableGrid.addDomHandler(ensureInternHandler(), DoubleClickEvent.getType()));
      reg.add(editableGrid.addDomHandler(ensureInternHandler(), ScrollEvent.getType()));

      reg.add(editableGrid.addHandler(ensureInternHandler(), HeaderMouseDownEvent.getType()));
      reg.add(editableGrid.addHandler(ensureInternHandler(), ReconfigureEvent.getType()));

      reg.add(editableGrid.getColumnModel().addColumnWidthChangeHandler(ensureInternHandler()));

      if (editableGrid instanceof HasExpandItemHandlers) {
        @SuppressWarnings({"rawtypes", "unchecked"})
        HasBeforeExpandItemHandlers<M> hasHandlers = (HasBeforeExpandItemHandlers) editableGrid;
        reg.add(hasHandlers.addBeforeExpandHandler(ensureInternHandler()));
      }
      if (editableGrid instanceof HasCollapseItemHandlers) {
        @SuppressWarnings({"rawtypes", "unchecked"})
        HasBeforeCollapseItemHandlers<M> hasHandlers = (HasBeforeCollapseItemHandlers) editableGrid;
        reg.add(hasHandlers.addBeforeCollapseHandler(ensureInternHandler()));
      }
      groupRegistration = reg;
    }
  }

  @Override
  public abstract void startEditing(GridCell cell);

  protected HandlerManager ensureHandlers() {
    if (handlerManager == null) {
      handlerManager = new HandlerManager(this);
    }
    return handlerManager;
  }

  protected KeyNav ensureInternalKeyNav() {
    if (keyNav == null) {
      keyNav = new AbstractGridEditingKeyNav();
    }
    return keyNav;
  }

  protected Handler ensureInternHandler() {
    if (handler == null) {
      handler = new Handler();
    }
    return handler;

  }

  protected void focusCell(int row, int col) {
    // this could could be executing after the editor has been removed or hidden
    // which can throw an exception in IE
    if (getEditableGrid().isAttached()) {
      try {
        getEditableGrid().getView().focusCell(row, col, true);
      } catch (Exception e) {
      }
    }
  }

  protected void focusGrid() {
    getEditableGrid().focus();
  }

  protected void onAttachOrDetach(AttachEvent event) {
    if (!event.isAttached()) {
      cancelEditing();
    }
  }

  protected void onClick(ClickEvent event) {
    if (clicksToEdit == ClicksToEdit.ONE) {
      if (GXT.isSafari()) {
        // EXTGWT-2019 when starting an edit on the same row of an active edit
        // the active edit value
        // is lost as the active cell does not complete the edit
        // this only happens with TreeGrid, not Grid which could be looked into
        final GridCell cell = findCell(event.getNativeEvent().getEventTarget().<Element> cast());
        if (cell != null && activeCell != null && activeCell.getRow() == cell.getRow()) {
          completeEditing();
        }
        startEditing(cell);
      } else {
        startEditing(event.getNativeEvent());
      }
    }
  }

  protected void onDoubleClick(DoubleClickEvent event) {
    if (clicksToEdit == ClicksToEdit.TWO) {
      startEditing(event.getNativeEvent());
    }
  }

  protected void onEnter(NativeEvent evt) {
    evt.preventDefault();
    GridCell gc = activeCell;
    completeEditing();
    if (gc != null) {
      focusCell(gc.getRow(), gc.getCol());
    }
  }

  protected void onEsc(NativeEvent evt) {
    GridCell gc = activeCell;
    cancelEditing();
    if (gc != null) {
      focusCell(gc.getRow(), gc.getCol());
      focusGrid();
    }
  }

  protected void onMouseDown(MouseDownEvent event) {

  }

  protected void onMouseUp(MouseUpEvent event) {

  }

  @SuppressWarnings("unchecked")
  protected void onReconfigure(ReconfigureEvent event) {
    setEditableGrid((Grid<M>) event.getSource());
  }

  protected void onScroll(ScrollEvent event) {
    cancelEditing();
  }

  protected GridCell findCell(Element target) {
    if (editableGrid != null) {
      if (editableGrid.getView().isSelectableTarget(target) && editableGrid.getView().getBody().isOrHasChild(target)) {
        int row = editableGrid.getView().findRowIndex(target);
        int col = editableGrid.getView().findCellIndex(target, null);
        if (row != -1 && col != -1) {
          return new GridCell(row, col);
        }
      }
    }
    return null;
  }

  protected void startEditing(Element target) {
    GridCell cell = findCell(target);
    if (cell != null) {
      int row = cell.getRow();
      int col = cell.getCol();
      if (row != -1 && col != -1) {
        startEditing(new GridCell(row, col));
      }
    }
  }

  private void startEditing(NativeEvent evt) {
    if (Element.is(evt.getEventTarget())) {
      startEditing(Element.as(evt.getEventTarget()));
    }
  }
}
