/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import java.util.Comparator;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XDOM;

/**
 * A column config for a column in a column model.
 * 
 * <p />
 * The column config is a configuration object that should only be used when
 * creating a column model. After the column model is created, any changes
 * should be made to the column model, not the column config.
 */
public class ColumnConfig<M, N> {

  /**
   * True to disable keyboard column navigation of this column.
   */
  protected boolean ariaIgnore;

  private HorizontalAlignmentConstant alignment;
  private Cell<N> cell;

  private boolean fixed;
  private boolean groupable = true;
  private SafeHtml header;
  private boolean hidden;
  private boolean menuDisabled;
  private boolean resizable = true;
  private boolean hideable = true;
  private boolean rowHeader;
  private boolean sortable = true;

  private SafeHtml toolTip;
  private final ValueProvider<? super M, N> valueProvider;
  private Comparator<N> comparator;
  private Widget widget;
  private int width = 100;

  private String columnHeaderClassName;
  private String columnClassSuffix;
  private String columnTextClassName;
  private SafeStyles columnStyle = XDOM.EMPTY_SAFE_STYLE;
  private SafeStyles columnTextStyle = XDOM.EMPTY_SAFE_STYLE;

  /**
   * Creates a new column config.
   */
  public ColumnConfig(ValueProvider<? super M, N> valueProvider) {
    this.valueProvider = valueProvider;
  }

  /**
   * Creates a new column config.
   * 
   * @param valueProvider the value provider
   * @param width the column width
   */
  public ColumnConfig(ValueProvider<? super M, N> valueProvider, int width) {
    this(valueProvider);
    this.width = width;
  }

  /**
   * Creates a new column config.
   * 
   * @param valueProvider the value provider
   * @param width the column width
   * @param header the column header content
   */
  public ColumnConfig(ValueProvider<? super M, N> valueProvider, int width, SafeHtml header) {
    this(valueProvider, width);
    this.header = header;
  }

  /**
   * Creates a new column config.
   * 
   * @param valueProvider thevalueProvider
   * @param width the column width
   * @param header the heading text
   */
  public ColumnConfig(ValueProvider<? super M, N> valueProvider, int width, String header) {
    this(valueProvider, width, SafeHtmlUtils.fromString(header));
  }

  /**
   * Returns the column's horizontal alignment.
   * 
   * @return the horizontal alignment
   */
  public HorizontalAlignmentConstant getAlignment() {
    return alignment;
  }

  /**
   * Returns the column's cell renderer.
   * 
   * @return the renderer
   */
  public Cell<N> getCell() {
    return cell;
  }

  /**
   * Returns the columns class name.
   * 
   * @return the column class name
   */
  public String getColumnClassSuffix() {
    return columnClassSuffix;
  }

  /**
   * Returns the column class names(s).
   * 
   * @return the column class names(s)
   */
  public String getColumnHeaderClassName() {
    return columnHeaderClassName;
  }

  /**
   * Returns the column cells styles.
   * 
   * @return the styles
   */
  public SafeStyles getColumnStyle() {
    return columnStyle;
  }

  /**
   * Returns the column text class name(s).
   * 
   * @return the class names(s)
   */
  public String getColumnTextClassName() {
    return columnTextClassName;
  }

  /**
   * Returns the column text styles.
   * 
   * @return the styles
   */
  public SafeStyles getColumnTextStyle() {
    return columnTextStyle;
  }

  /**
   * Returns the comparator used to compare the items in this column when
   * grouping or sorting. If {@code N} implements {@link Comparable}, this is
   * not required to sort or group.
   * 
   * @return the current comparator used or null if none has been set.
   */
  public Comparator<N> getComparator() {
    return comparator;
  }

  /**
   * Returns the column's header text.
   * 
   * @return the header text
   */
  public SafeHtml getHeader() {
    return header;
  }

  /**
   * Returns the value provider path.
   * 
   * @return the path
   */
  public String getPath() {
    return valueProvider.getPath();
  }

  /**
   * Returns the column's tool tip.
   * 
   * @return the tool tip
   */
  public SafeHtml getToolTip() {
    return toolTip;
  }

  /**
   * Returns the column's valueProvider.
   * 
   * @return the valueProvider
   */
  public ValueProvider<? super M, N> getValueProvider() {
    return valueProvider;
  }

  /**
   * Returns the column's widget.
   * 
   * @return the widget
   */
  public Widget getWidget() {
    return widget;
  }

  /**
   * Returns the column's width.
   * 
   * @return the column width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Returns true if the column width cannot be changed. Applies to both column
   * width calculations (auto fill, force fit, auto expand column) and user
   * resizing.
   * 
   * @return the fixed state
   */
  public boolean isFixed() {
    return fixed;
  }

  /**
   * Returns true if the column can be grouped.
   * 
   * @return true if groupable
   */
  public boolean isGroupable() {
    return groupable;
  }

  /**
   * Returns true if the column is hidden.
   * 
   * @return the hidden state
   */
  public boolean isHidden() {
    return hidden;
  }

  /**
   * Returns true if the column can be hidden.
   * 
   * @return true if column can be hidden
   */
  public boolean isHideable() {
    return hideable;
  }

  /**
   * Returns true if the column's menu is disabled.
   * 
   * @return the menu disabled state
   */
  public boolean isMenuDisabled() {
    return menuDisabled;
  }

  /**
   * Returns true if the column is resizable.
   * 
   * @return the resizable state
   */
  public boolean isResizable() {
    return resizable;
  }

  /**
   * Returns true if the column is marked as the row header.
   * 
   * @return true if row header
   */
  public boolean isRowHeader() {
    return rowHeader;
  }

  /**
   * Returns <code>true</code> if the column is sortable (pre-render).
   * 
   * @return the sortable state
   */
  public boolean isSortable() {
    return sortable;
  }

  /**
   * Sets the column's horizontal alignment.
   * 
   * @param alignment the alignment
   */
  public void setAlignment(HorizontalAlignmentConstant alignment) {
    this.alignment = alignment;
  }

  /**
   * Sets the column's cell renderer (pre-render).
   * 
   * @param cell the cell renderer
   */
  public void setCell(Cell<N> cell) {
    this.cell = cell;
  }

  /**
   * If provided, this value will be appended to 'x-grid3-td-' and be added to
   * all table cells for the column.
   * 
   * @param columnClassSuffix the column class name
   */
  public void setColumnClassSuffix(String columnClassSuffix) {
    this.columnClassSuffix = columnClassSuffix;
  }

  /**
   * Sets the CSS class name(s) to be applied to the header element (defaults to
   * null). The class name(s) is only applied to the header cell, not the data
   * rows.
   * 
   * @param classNames the space separated list of class names
   */
  public void setColumnHeaderClassName(String classNames) {
    this.columnHeaderClassName = classNames;
  }

  /**
   * Sets the CSS class name(s) to be applied to the cell in the column
   * (defaults to null).
   * 
   * @param columnStyles the column styles
   */
  public void setColumnStyle(SafeStyles columnStyles) {
    this.columnStyle = columnStyles;
  }

  /**
   * Sets the CSS class name(s) to be applied to the text element of each cell
   * in the column (defaults to null).
   * 
   * @param columnTextClassName the space separated list of class names
   */
  public void setColumnTextClassName(String columnTextClassName) {
    this.columnTextClassName = columnTextClassName;
  }

  /**
   * Sets one to many CSS styles to be applied to the text element of each cell
   * in the column (defaults to null).
   * 
   * @param textStyles the styles
   */
  public void setColumnTextStyle(SafeStyles textStyles) {
    this.columnTextStyle = textStyles;
  }

  /**
   * Sets the comparator used to compare the items in this column when grouping
   * or sorting. If {@code N} implements {@link Comparable}, this is not
   * required to sort or group.
   * 
   * @param comparator the Comparator to use when locally sorting or grouping
   */
  public void setComparator(Comparator<N> comparator) {
    this.comparator = comparator;
  }

  /**
   * True if the column width cannot be changed either by column model or user
   * resizing (defaults to false, pre-render).
   * 
   * @param fixed true for fixed column width
   */
  public void setFixed(boolean fixed) {
    this.fixed = fixed;
  }

  /**
   * Sets if the column is groupable (defaults to true). Only applies when using
   * a GroupingView.
   * 
   * @param groupable true to allow grouping
   */
  public void setGroupable(boolean groupable) {
    this.groupable = groupable;
  }

  /**
   * Sets the column's header text.
   * 
   * @see #setHeader(String)
   * @param header the header text
   */
  public void setHeader(SafeHtml header) {
    this.header = header;
  }

  /**
   * Sets the column's header text, to be displayed as escaped html.
   * 
   * @see #setHeader(SafeHtml)
   * @param header the header text to be displayed as escaped html
   */
  public void setHeader(String header) {
    setHeader(SafeHtmlUtils.fromString(header));
  }

  /**
   * Sets whether the column is hidden.
   * 
   * @param hidden true to hide
   */
  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  /**
   * True to allow the column to be hidden via the context menu (defaults to
   * true).
   * 
   * <p />
   * The setting only controls whether the check menu item is enabled / disabled
   * in the grid context menu, controlling whether the user can hide / show a
   * column. All programmatic calls to hide / show are unaffected by this
   * setting.
   * 
   * @param hideable true to allow hiding
   */
  public void setHideable(boolean hideable) {
    this.hideable = hideable;
  }

  /**
   * Sets whether the column's menu is disabled (pre-render).
   * 
   * @param menuDisabled true to disable the menu
   */
  public void setMenuDisabled(boolean menuDisabled) {
    this.menuDisabled = menuDisabled;
  }

  /**
   * Specifies if the column may be resized (defaults to true, pre-render).
   * 
   * @param resizable the resizable state
   */
  public void setResizable(boolean resizable) {
    this.resizable = resizable;
  }

  /**
   * True to mark this column as the row header. Only applicable when ARIA is
   * enabled for accessibility. Screen reader will annouce the columns value
   * when using a cell selection model.
   * 
   * @param rowHeader true to mark the column as the row header
   */
  public void setRowHeader(boolean rowHeader) {
    this.rowHeader = rowHeader;
  }

  /**
   * Sets if the column can be sorted (defaults to true, pre-render).
   * 
   * @param sortable the sortable state
   */
  public void setSortable(boolean sortable) {
    this.sortable = sortable;
  }

  /**
   * Sets the column's tool tip.
   * 
   * @param toolTip the tool tip
   */
  public void setToolTip(SafeHtml toolTip) {
    this.toolTip = toolTip;
  }

  /**
   * Sets the column's widget.
   * 
   * @param widget the widget
   * @param header the text used for the column context menu
   */
  public void setWidget(Widget widget, SafeHtml header) {
    this.widget = widget;
    this.header = header;
  }

  /**
   * Sets the column's width in pixels.
   * 
   * @param width the width
   */
  public void setWidth(int width) {
    this.width = width;
  }
}
