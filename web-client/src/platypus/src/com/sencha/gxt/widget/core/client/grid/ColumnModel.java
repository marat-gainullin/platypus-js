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

import com.google.gwt.cell.client.Cell;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.Rectangle;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.widget.core.client.event.ColumnMoveEvent;
import com.sencha.gxt.widget.core.client.event.ColumnMoveEvent.ColumnMoveHandler;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent.ColumnWidthChangeHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnHeaderChangeEvent.ColumnHeaderChangeHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnHiddenChangeEvent.ColumnHiddenChangeHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnModelHandlers.HasColumnModelHandlers;

/**
 * This is the default implementation of a ColumnModel.
 */
public class ColumnModel<M> implements HasColumnModelHandlers {
  protected List<ColumnConfig<M, ?>> configs;
  protected final List<HeaderGroupConfig> groups = new ArrayList<HeaderGroupConfig>();
  protected final List<AggregationRowConfig<M>> rows = new ArrayList<AggregationRowConfig<M>>();
  private HandlerManager handlerManager;

  /**
   * Creates a new column model.
   * 
   * @param list the columns
   */
  public ColumnModel(List<ColumnConfig<M, ?>> list) {
    this.configs = Collections.unmodifiableList(list);
  }

  /**
   * Adds an aggregation row config to the column model.
   * 
   * @param row the aggregation row
   */
  public void addAggregationRow(AggregationRowConfig<M> row) {
    rows.add(row);
  }

  @Override
  public HandlerRegistration addColumnHeaderChangeHandler(ColumnHeaderChangeHandler handler) {
    return ensureHandlers().addHandler(ColumnHeaderChangeEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addColumnHiddenChangeHandler(ColumnHiddenChangeHandler handler) {
    return ensureHandlers().addHandler(ColumnHiddenChangeEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addColumnModelHandlers(ColumnModelHandlers handlers) {
    GroupingHandlerRegistration reg = new GroupingHandlerRegistration();
    reg.add(addColumnWidthChangeHandler(handlers));
    reg.add(addColumnMoveHandler(handlers));
    reg.add(addColumnHeaderChangeHandler(handlers));
    reg.add(addColumnHiddenChangeHandler(handlers));
    return reg;
  }

  @Override
  public HandlerRegistration addColumnMoveHandler(ColumnMoveHandler handler) {
    return ensureHandlers().addHandler(ColumnMoveEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addColumnWidthChangeHandler(ColumnWidthChangeHandler handler) {
    return ensureHandlers().addHandler(ColumnWidthChangeEvent.getType(), handler);
  }

  /**
   * Adds a group to the column model.
   * 
   * @param row the row
   * @param column the column
   * @param config the header group config
   */
  public void addHeaderGroup(int row, int column, HeaderGroupConfig config) {
    config.setRow(row);
    config.setColumn(column);
    groups.add(config);
  }

  /**
   * Returns the matching column based on value provider path.
   * 
   * @param path the path
   * @return the matching column or null if no match
   */
  public ColumnConfig<M, ?> findColumnConfig(String path) {
    for (ColumnConfig<M, ?> c : configs) {
      if (c.getValueProvider().getPath().equals(path)) {
        return c;
      }
    }
    return null;
  }

  @Override
  public void fireEvent(GwtEvent<?> event) {
    if (handlerManager != null) {
      handlerManager.fireEvent(event);
    }
  }

  /**
   * Returns the aggregation row.
   * 
   * @param rowIndex the row index
   * @return the aggregation row
   */
  public AggregationRowConfig<M> getAggregationRow(int rowIndex) {
    return rowIndex >= 0 && rowIndex < rows.size() ? rows.get(rowIndex) : null;
  }

  /**
   * Returns the aggregation rows.
   * 
   * @return the aggregation rows
   */
  public List<AggregationRowConfig<M>> getAggregationRows() {
    return Collections.unmodifiableList(rows);
  }

  /**
   * Returns the cell renderer.
   * 
   * @param colIndex the column index
   * @return the cell renderer
   */
  public <N> Cell<N> getCell(int colIndex) {
    ColumnConfig<M, N> c = getColumn(colIndex);
    return c != null ? c.getCell() : null;
  }

  /**
   * Returns the column at the given index.
   * 
   * @param colIndex the column index
   * @return the column or null
   */
  @SuppressWarnings("unchecked")
  public <N> ColumnConfig<M, N> getColumn(int colIndex) {
    return (ColumnConfig<M, N>) (colIndex >= 0 && colIndex < configs.size() ? configs.get(colIndex) : null);
  }

  /**
   * Returns the column's alignment.
   * 
   * @param colIndex the column index
   * @return the alignment
   */
  public HorizontalAlignmentConstant getColumnAlignment(int colIndex) {
    ColumnConfig<M, ?> c = getColumn(colIndex);
    return c != null ? c.getAlignment() : null;
  }

  /**
   * Returns the column count.
   * 
   * @return the column count
   */
  public int getColumnCount() {
    return getColumnCount(false);
  }

  /**
   * Returns the number of visible columns.
   * 
   * @return the visible column count
   */
  public int getColumnCount(boolean visibleOnly) {
    if (visibleOnly) {
      int count = 0;
      for (ColumnConfig<M, ?> c : configs) {
        if (!c.isHidden()) {
          count++;
        }
      }
      return count;
    }
    return configs.size();
  }

  /**
   * Returns the header for the specified column.
   * 
   * @param colIndex the column index
   * @return the header
   */
  public SafeHtml getColumnHeader(int colIndex) {
    ColumnConfig<M, ?> c = getColumn(colIndex);
    return c != null ? c.getHeader() : null;
  }

  /**
   * Returns the column configs.
   * 
   * @return the column configs
   */
  public List<ColumnConfig<M, ?>> getColumns() {
    return configs;
  }

  /**
   * Returns the column's styles.
   * 
   * @param colIndex the column index
   * @return the column styles
   */
  public SafeStyles getColumnStyles(int colIndex) {
    ColumnConfig<M, ?> c = getColumn(colIndex);
    return c.getColumnStyle();
  }

  /**
   * Returns the tooltip for the specified column.
   * 
   * @param colIndex the column index
   * @return the tooltip
   */
  public SafeHtml getColumnToolTip(int colIndex) {
    ColumnConfig<M, ?> c = getColumn(colIndex);
    return c != null ? c.getToolTip() : null;
  }

  /**
   * Returns the column width.
   * 
   * @param colIndex the column index
   * @return the width
   */
  public int getColumnWidth(int colIndex) {
    ColumnConfig<M, ?> c = getColumn(colIndex);
    return c != null ? c.getWidth() : -1;
  }

  /**
   * Returns the header groups.
   * 
   * @return the header groups
   */
  public List<HeaderGroupConfig> getHeaderGroups() {
    return Collections.unmodifiableList(groups);
  }

  /**
   * Returns the total width of all columns.
   * 
   * @return the total width
   */
  public int getTotalWidth() {
    return getTotalWidth(false);
  }

  /**
   * Returns the total width of all columns.
   * 
   * @param includeHidden true to include hidden column widths
   * @return the total
   */
  public int getTotalWidth(boolean includeHidden) {
    int w = 0;
    for (ColumnConfig<M, ?> c : configs) {
      if (includeHidden || !c.isHidden()) {
        w += c.getWidth();
      }
    }
    return w;
  }

  /**
   * Returns the cell value provider.
   * 
   * @param colIndex the column index
   * @return the value provider
   */
  public <N> ValueProvider<? super M, N> getValueProvider(int colIndex) {
    @SuppressWarnings("unchecked")
    ColumnConfig<? super M, N> c = (ColumnConfig<? super M, N>) getColumn(colIndex);
    return c != null ? c.getValueProvider() : null;
  }

  /**
   * Returns the index of the column.
   * 
   * @param column the column
   * @return the column index
   */
  public int indexOf(ColumnConfig<M, ?> column) {
    return configs.indexOf(column);
  }

  /**
   * Returns true if the column can be resized.
   * 
   * @param colIndex the column index
   * @return true if fixed
   */
  public boolean isFixed(int colIndex) {
    ColumnConfig<M, ?> c = getColumn(colIndex);
    return c != null && c.isFixed();
  }

  /**
   * Returns true if the column is groupable. Applies when using a
   * {@link GroupingView}.
   * 
   * @param colIndex the column index
   * @return true if the column is groupable.
   */
  public boolean isGroupable(int colIndex) {
    ColumnConfig<M, ?> c = getColumn(colIndex);
    return c != null && c.isGroupable();
  }

  /**
   * Returns true if the column is hidden.
   * 
   * @param colIndex the column index
   * @return true if hidden
   */
  public boolean isHidden(int colIndex) {
    ColumnConfig<M, ?> c = getColumn(colIndex);
    return c != null && c.isHidden();
  }

  /**
   * Returns true if the specified column menu is disabled.
   * 
   * @param colIndex the column index
   * @return true if disabled
   */
  public boolean isMenuDisabled(int colIndex) {
    ColumnConfig<M, ?> c = getColumn(colIndex);
    return c != null && c.isMenuDisabled();
  }

  /**
   * Returns true if the column can be resized.
   * 
   * @param colIndex the column index
   * @return true if resizable
   */
  public boolean isResizable(int colIndex) {
    ColumnConfig<M, ?> c = getColumn(colIndex);
    return c != null && c.isResizable();
  }

  /**
   * Returns true if the specified column is sortable.
   * 
   * @param colIndex the column index
   * @return true if the column is sortable
   */
  public boolean isSortable(int colIndex) {
    ColumnConfig<M, ?> c = getColumn(colIndex);
    return c != null && c.isSortable();
  }

  /**
   * Moves a column.
   * 
   * @param oldIndex the column index
   * @param newIndex the new column index
   */
  public void moveColumn(int oldIndex, int newIndex) {
    if (oldIndex < newIndex) {
      newIndex--;
    }
    List<ColumnConfig<M, ?>> temp = new ArrayList<ColumnConfig<M, ?>>(configs);
    ColumnConfig<M, ?> c = temp.remove(oldIndex);
    if (c != null) {
      temp.add(newIndex, c);
      configs = Collections.unmodifiableList(temp);
      fireEvent(new ColumnMoveEvent(newIndex, c));
    }
  }

  /**
   * Sets the header for a column.
   * 
   * @param colIndex the column index
   * @param header the header
   */
  public void setColumnHeader(int colIndex, SafeHtml header) {
    ColumnConfig<M, ?> c = getColumn(colIndex);
    if (c != null) {
      c.setHeader(header);
      fireEvent(new ColumnHeaderChangeEvent(colIndex, c));
    }
  }

  /**
   * Sets the column's width.
   * 
   * @param colIndex the column index
   * @param width the width
   */
  public void setColumnWidth(int colIndex, int width) {
    setColumnWidth(colIndex, width, false);
  }

  /**
   * Sets the column's width.
   * 
   * @param colIndex the column index
   * @param width the width
   * @param suppressEvent true to suppress width change event
   */
  public void setColumnWidth(int colIndex, int width, boolean suppressEvent) {
    ColumnConfig<M, ?> c = getColumn(colIndex);
    if (c != null) {
      c.setWidth(width);
      if (!suppressEvent) {
        fireEvent(new ColumnWidthChangeEvent(colIndex, c));
      }
    }

  }

  /**
   * Sets if a column is hidden.
   * 
   * @param colIndex the column index
   * @param hidden true to hide the column
   */
  public void setHidden(int colIndex, boolean hidden) {
    ColumnConfig<M, ?> c = getColumn(colIndex);
    if (c != null && c.isHidden() != hidden) {
      c.setHidden(hidden);
      fireEvent(new ColumnHiddenChangeEvent(colIndex, c));
    }
  }

  protected HandlerManager ensureHandlers() {
    if (handlerManager == null) {
      handlerManager = new HandlerManager(this);
    }
    return handlerManager;
  }

  protected HeaderGroupConfig getGroup(int row, int column) {
    for (HeaderGroupConfig config : getHeaderGroups()) {
      Rectangle r = new Rectangle();
      r.setX(config.getColumn());
      r.setY(config.getRow());

      r.setWidth(config.getColspan());
      r.setHeight(config.getRowspan());

      if (r.contains(column, row)) {
        return config;
      }
    }
    return null;
  }

  protected boolean hasGroup(int row, int column) {
    return getGroup(row, column) != null;
  }

}
