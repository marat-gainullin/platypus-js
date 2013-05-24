/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid.editing;

import com.sencha.gxt.data.shared.Converter;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent.HasBeforeStartEditHandlers;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent.HasCancelEditHandlers;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.HasCompleteEditHandlers;
import com.sencha.gxt.widget.core.client.event.StartEditEvent.HasStartEditHandlers;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;

/**
 * Defines the interface for classes that support grid editing.
 * 
 * @see GridInlineEditing
 * @see GridRowEditing
 * 
 * @param <M> the model type
 */
public interface GridEditing<M> extends HasBeforeStartEditHandlers<M>, HasStartEditHandlers<M>,
    HasCompleteEditHandlers<M>, HasCancelEditHandlers<M> {

  /**
   * Adds an editor for the give column.
   * 
   * @param columnConfig the column config
   * @param converter the converter
   * @param field the field
   */
  <N, O> void addEditor(ColumnConfig<M, N> columnConfig, Converter<N, O> converter, Field<O> field);

  /**
   * Adds an editor for the given column.
   * 
   * @param columnConfig the column config
   * @param field the field
   */
  <N> void addEditor(ColumnConfig<M, N> columnConfig, Field<N> field);

  /**
   * Cancels an active edit.
   */
  void cancelEditing();

  /**
   * Completes the active edit.
   */
  void completeEditing();

  /**
   * Returns the converter for the given column.
   * 
   * @param columnConfig the column config
   * @return the converter
   */
  <N, O> Converter<N, O> getConverter(ColumnConfig<M, N> columnConfig);

  /**
   * Returns the target grid.
   * 
   * @return the target grid
   */
  Grid<M> getEditableGrid();

  /**
   * Returns the editor for the given column.
   * 
   * @param columnConfig the column config
   * @return the editor
   */
  <O> Field<O> getEditor(ColumnConfig<M, ?> columnConfig);

  /**
   * Returns true if editing is active.
   * 
   * @return true if editing
   */
  boolean isEditing();

  /**
   * Removes the editor for the given column.
   * 
   * @param columnConfig the column config
   */
  void removeEditor(ColumnConfig<M, ?> columnConfig);

  /**
   * Sets the target grid to be edited.
   * 
   * @param editableGrid the editable grid
   */
  void setEditableGrid(Grid<M> editableGrid);

  /**
   * Starts editing for the given cell.
   * 
   * @param cell the cell
   */
  void startEditing(GridCell cell);
}
