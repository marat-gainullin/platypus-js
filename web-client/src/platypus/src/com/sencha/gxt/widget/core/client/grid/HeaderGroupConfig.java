/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.Widget;

/**
 * Defines the configuration for a header group. Header groups support rowspan
 * and colspan and horizontal alignment. Groups support both HTML and widget for
 * their content.
 */
public class HeaderGroupConfig {

  private int colspan = 1;
  private int column;
  private HorizontalAlignmentConstant horizontalAlignment = HasHorizontalAlignment.ALIGN_CENTER;
  private SafeHtml html;
  private int row;
  private int rowspan = 1;
  private Widget widget;

  /**
   * Creates a new header group without rowspan and colspan.
   * 
   * @param html the group text
   */
  public HeaderGroupConfig(String html) {
    this(SafeHtmlUtils.fromString(html));
  }

  /**
   * Creates a new header group without rowspan and colspan.
   * 
   * @param html the group text
   */
  public HeaderGroupConfig(SafeHtml html) {
    this.html = html;
  }

  /**
   * Creates a header group.
   * 
   * @param text the group text
   * @param rowspan the rowspan
   * @param colspan the colspan
   */
  public HeaderGroupConfig(String text, int rowspan, int colspan) {
    this(SafeHtmlUtils.fromString(text), rowspan, colspan);
  }

  /**
   * Creates a header group.
   * 
   * @param text the group text
   * @param rowspan the rowspan
   * @param colspan the colspan
   */
  public HeaderGroupConfig(SafeHtml text, int rowspan, int colspan) {
    this(text);
    this.rowspan = rowspan;
    this.colspan = colspan;
  }

  /**
   * Creates a header group.
   * 
   * @param widget the group's widget
   * @param rowspan the rowspan
   * @param colspan the colspan
   */
  public HeaderGroupConfig(Widget widget, int rowspan, int colspan) {
    this.widget = widget;
    this.rowspan = rowspan;
    this.colspan = colspan;
  }

  /**
   * Returns the colspan.
   * 
   * @return the colspan
   */
  public int getColspan() {
    return colspan;
  }

  /**
   * Returns the column.
   * 
   * @return the column
   */
  public int getColumn() {
    return column;
  }

  /**
   * Returns the horizontal alignment.
   * 
   * @return the alignment
   */
  public HorizontalAlignmentConstant getHorizontalAlignment() {
    return horizontalAlignment;
  }

  /**
   * Returns the html.
   * 
   * @return the html
   */
  public SafeHtml getHtml() {
    return html;
  }

  /**
   * Returns the row.
   * 
   * @return the row
   */
  public int getRow() {
    return row;
  }

  /**
   * Returns the rowspan.
   * 
   * @return the rowspan
   */
  public int getRowspan() {
    return rowspan;
  }

  /**
   * Returns the widget.
   * 
   * @return the widget
   */
  public Widget getWidget() {
    return widget;
  }

  /**
   * Sets the colspan (defaults to 1).
   * 
   * @param colspan the colspan
   */
  public void setColspan(int colspan) {
    this.colspan = colspan;
  }

  /**
   * Sets the 0-indexed column
   * 
   * @param column the column
   */
  public void setColumn(int column) {
    this.column = column;
  }

  /**
   * Sets the horizontal alignment
   * 
   * @param horizontalAlignment the alignment
   */
  public void setHorizontalAlignment(HorizontalAlignmentConstant horizontalAlignment) {
    this.horizontalAlignment = horizontalAlignment;
  }

  /**
   * Sets the group's html.
   * 
   * @param html the html text
   */
  public void setHtml(SafeHtml html) {
    this.html = html;
  }

  /**
   * Sets the 0-indexed row.
   * 
   * @param row the row
   */
  public void setRow(int row) {
    this.row = row;
  }

  /**
   * Sets the rowspan (defaults to 1).
   * 
   * @param rowspan the rowspan
   */
  public void setRowspan(int rowspan) {
    this.rowspan = rowspan;
  }

  /**
   * Sets the group's widget.
   * 
   * @param widget the widget
   */
  public void setWidget(Widget widget) {
    this.widget = widget;
  }

}
