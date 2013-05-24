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
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.Shared;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.DomHelper;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.Store.Record;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.data.shared.event.StoreHandlers;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreSortEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.event.BodyScrollEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.ColumnMoveEvent;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent;
import com.sencha.gxt.widget.core.client.event.RefreshEvent;
import com.sencha.gxt.widget.core.client.event.SortChangeEvent;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * This class encapsulates the user interface of an {@link Grid}. Methods of
 * this class may be used to access user interface elements to enable special
 * display effects. Do not change the DOM structure of the user interface. </p>
 * <p />
 * This class does not provide ways to manipulate the underlying data. The data
 * model of a Grid is held in a {@link ListStore}.
 */
public class GridView<M> {

  /**
   * Define the appearance of a Grid.
   */
  public interface GridAppearance {

    Element findCell(Element elem);

    Element findRow(Element elem);

    Element getRowBody(Element row);

    NodeList<Element> getRows(XElement parent);

    void onCellSelect(Element cell, boolean select);

    void onRowHighlight(Element row, boolean highlight);

    void onRowOver(Element row, boolean over);

    void onRowSelect(Element row, boolean select);

    /**
     * Renders the HTML markup for the widget.
     * 
     * @param sb the safe html builder
     */
    void render(SafeHtmlBuilder sb);

    SafeHtml renderEmptyContent(String emptyText);

    GridStyles styles();

  }

  @Shared
  public interface GridDataTableStyles extends CssResource {

    String cell();

    String cellDirty();

    String cellInner();

    String cellInvalid();

    String cellSelected();

    String columnLines();

    String dataTable();

    String headerRow();

    String row();

    String rowAlt();

    String rowBody();

    String rowBodyRow();

    String rowDirty();

    String rowHighlight();

    String rowOver();

    String rowSelected();

    String rowWrap();

  }

  public interface GridStyles extends GridDataTableStyles {

    String empty();

    String footer();

    String grid();

  }

  public interface GridTemplates extends SafeHtmlTemplates {
    @Template("<div class=\"{0}\">{1}</div>")
    SafeHtml div(String classes, SafeHtml contents);

    @Template("<table cellpadding=\"0\" cellspacing=\"0\" class=\"{0}\" style=\"{1};table-layout: fixed\"><tbody>{3}</tbody><tbody>{2}</tbody></table>")
    SafeHtml table(String classes, SafeStyles tableStyles, SafeHtml contents, SafeHtml sizingHeads);

    @Template("<td cellindex=\"{0}\" class=\"{1}\" style=\"{2}\"><div class=\"{3}\" style=\"{4}\">{5}</div></td>")
    SafeHtml td(int cellIndex, String cellClasses, SafeStyles cellStyles, String textClasses, SafeStyles textStyles,
        SafeHtml contents);

    @Template("<td cellindex=\"{0}\" class=\"{1}\" style=\"{2}\" rowspan=\"{3}\">{4}</td>")
    SafeHtml tdRowSpan(int cellIndex, String classes, SafeStyles styles, int rowSpan, SafeHtml contents);

    @Template("<td colspan=\"{0}\" class=\"{1}\"><div class=\"{2}\">{3}</div></td>")
    SafeHtml tdWrap(int colspan, String cellClasses, String textClasses, SafeHtml content);

    @Template("<th class=\"{0}\" style=\"{1}\"></th>")
    SafeHtml th(String classes, SafeStyles cellStyles);

    @Template("<tr class=\"{0}\">{1}</tr>")
    SafeHtml tr(String classes, SafeHtml contents);
  }

  private static Logger logger = Logger.getLogger(GridView.class.getName());

  /**
   * Set to true to auto expand the columns to fit the grid when the grid is
   * created.
   */
  protected boolean autoFill;

  /**
   * The content area inside the scroller.
   */
  protected XElement body;

  /**
   * A border width calculation to be applied for browsers that do not use the
   * old IE box model.
   */
  protected int borderWidth = 2;

  /**
   * The grid's column model.
   */
  protected ColumnModel<M> cm;

  /**
   * The handler for column events such as move, width change and hide.
   */
  protected ColumnModelHandlers columnListener;

  /**
   * The main data table and body.
   */
  protected XElement dataTable, dataTableBody, dataTableSizingHead;

  protected int deferUpdateDelay = 500;

  protected boolean deferUpdates = false;

  /**
   * The list store that provides data for this grid view.
   */
  protected ListStore<M> ds;

  /**
   * The safe HTML value to display when the grid is empty (defaults to &nbsp;).
   */
  protected String emptyText = "&nbsp;";

  /**
   * True to enable a column spanning row body, as used by {@link RowExpander}
   * (defaults to false).
   */
  protected boolean enableRowBody = false;

  protected XElement focusEl;

  protected final FocusImpl focusImpl = FocusImpl.getFocusImplForPanel();

  protected ColumnFooter<M> footer;

  protected boolean forceFit;
  protected Grid<M> grid;
  protected ColumnHeader<M> header;
  protected int headerColumnIndex;
  protected boolean headerDisabled;
  /**
   * The inner head element.
   */
  protected XElement headerElem;
  protected int lastViewWidth;
  protected StoreHandlers<M> listener;
  protected Element overRow;
  protected boolean preventScrollToTopOnRefresh;
  /**
   * The scrollable area that contains the main body.
   */
  protected XElement scroller;
  protected int scrollOffset = XDOM.getScrollBarWidth();
  protected boolean selectable = false;

  protected SortInfo sortState;
  protected int splitterWidth = 5;
  protected StoreSortInfo<M> storeSortInfo;
  protected GridStyles styles;
  protected GridTemplates tpls = GWT.create(GridTemplates.class);
  protected String unselectable = CommonStyles.get().unselectableSingle();
  protected boolean userResized;

  // we first render grid with a vbar, and remove as needed
  protected boolean vbar = true;
  protected GridViewConfig<M> viewConfig;
  private DelayedTask addTask = new DelayedTask() {

    @Override
    public void onExecute() {
      calculateVBar(false);
      refreshFooterData();
    }

  };
  private boolean adjustForHScroll = true;
  private GridAppearance appearance;
  private ColumnConfig<M, ?> autoExpandColumn;
  private int autoExpandMax = 500;

  private int autoExpandMin = 25;
  private HandlerRegistration cmHandlerRegistration;
  /**
   * True to show vertical column lines between cells.
   */
  private boolean columnLines;

  /**
   * True to enable the grid to be focused (defaults to true).
   */
  private boolean focusEnabled = true;

  private DelayedTask removeTask = new DelayedTask() {

    @Override
    public void onExecute() {
      calculateVBar(false);
      applyEmptyText();
      refreshFooterData();
      processRows(0, false);
    }

  };

  /**
   * The numbers of rows the first column should span if row bodies are enabled
   * (defaults to 1).
   */
  private int rowBodyRowSpan = 1;

  private boolean showDirtyCells = true;
  private HandlerRegistration storeHandlerRegistration;
  private boolean stripeRows;

  private boolean trackMouseOver = true;

  /**
   * Creates a new grid view.
   */
  public GridView() {
    this(GWT.<GridAppearance> create(GridAppearance.class));
  }

  /**
   * Creates a new grid view.
   * 
   * @param appearance the grid appearance
   */
  public GridView(GridAppearance appearance) {
    this.appearance = appearance;

    this.styles = appearance.styles();
  }

  /**
   * Ensured the current row and column is visible.
   * 
   * @param row the row index
   * @param col the column index
   * @param hscroll true to scroll horizontally if needed
   * @return the calculated point
   */
  public Point ensureVisible(int row, int col, boolean hscroll) {
    if (grid == null || !grid.isViewReady() || row < 0 || row > ds.size()) {
      return null;
    }

    if (col == -1) {
      col = 0;
    }

    Element rowEl = getRow(row);
    Element cellEl = null;
    if (!(!hscroll && col == 0)) {
      while (cm.isHidden(col)) {
        col++;
      }
      cellEl = getCell(row, col);

    }

    if (rowEl == null) {
      return null;
    }

    Element c = scroller;

    int ctop = 0;
    Element p = rowEl, stope = grid.getElement();
    while (p != null && p != stope) {
      ctop += p.getOffsetTop();
      p = p.getOffsetParent();
    }
    ctop -= headerElem.getOffsetHeight();

    int cbot = ctop + rowEl.getOffsetHeight();

    int ch = c.getOffsetHeight();
    int stop = c.getScrollTop();
    int sbot = stop + ch;

    if (ctop < stop) {
      c.setScrollTop(ctop);
    } else if (cbot > sbot) {
      if ((getTotalWidth() > scroller.getWidth(false) - scrollOffset)) {
        cbot += scrollOffset;
      }
      c.setScrollTop(cbot -= ch);
    }

    if (hscroll && cellEl != null) {
      int cleft = cellEl.getOffsetLeft();
      int cright = cleft + cellEl.getOffsetWidth();
      int sleft = c.getScrollLeft();
      int sright = sleft + c.getOffsetWidth();
      if (cleft < sleft) {
        c.setScrollLeft(cleft);
      } else if (cright > sright) {
        c.setScrollLeft(cright - scroller.getComputedWidth());
      }
    }

    return cellEl != null ? fly(cellEl).getXY() : new Point(c.getAbsoluteLeft() + c.getScrollLeft(), fly(rowEl).getY());
  }

  /**
   * Returns the cell.
   * 
   * @param elem the cell element or a child element
   * @return the cell element
   */
  public Element findCell(Element elem) {
    if (elem == null) {
      return null;
    }
    return appearance.findCell(elem);
  }

  /**
   * Returns the cell index.
   * 
   * @param elem the cell or child element
   * @param requiredStyle an optional required style name
   * @return the cell index or -1 if not found
   */
  public int findCellIndex(Element elem, String requiredStyle) {
    Element cell = findCell(elem);
    if (cell != null && (requiredStyle == null || fly(cell).hasClassName(requiredStyle))) {
      String index = cell.getAttribute("cellindex");
      return index.equals("") ? -1 : Integer.parseInt(index);
    }
    return -1;
  }

  /**
   * Returns the row element.
   * 
   * @param elem the row element or any child element
   * @return the matching row element
   */
  public Element findRow(Element elem) {
    if (elem == null) {
      return null;
    }
    return appearance.findRow(elem);
  }

  /**
   * Returns the row index.
   * 
   * @param elem the row or child of the row element
   * @return the index
   */
  public int findRowIndex(Element elem) {
    Element r = findRow(elem);
    if (r != null) {
      return r.getPropertyInt("rowindex");
    }
    return -1;
  }

  /**
   * Focuses the grid.
   */
  public void focus() {
    if (GXT.isGecko()) {
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        @Override
        public void execute() {
          if (focusEl != null) {
            focusImpl.focus(focusEl);
          }
        }
      });
    } else if (focusEl != null) {
      focusImpl.focus(focusEl);
    }
  }

  /**
   * Focus the cell and scrolls into view.
   * 
   * @param rowIndex the row index
   * @param colIndex the column index
   * @param hscroll true to scroll horizontally
   */
  public void focusCell(int rowIndex, int colIndex, boolean hscroll) {
    Point xy = ensureVisible(rowIndex, colIndex, hscroll);
    if (xy != null) {
      focusEl.setXY(xy);
      if (focusEnabled) {
        focus();
      }
    }
  }

  /**
   * Focus the row and scrolls into view.
   * 
   * @param rowIndex the row index
   */
  public void focusRow(int rowIndex) {
    focusCell(rowIndex, 0, false);
  }

  /**
   * Returns the grid appearance.
   * 
   * @return the grid appearance
   */
  public GridAppearance getAppearance() {
    return appearance;
  }

  /**
   * Returns the auto expand column id.
   * 
   * @return the auto expand column id
   */
  public ColumnConfig<M, ?> getAutoExpandColumn() {
    return autoExpandColumn;
  }

  /**
   * Returns the auto expand maximum width.
   * 
   * @return the max width in pixels
   */
  public int getAutoExpandMax() {
    return autoExpandMax;
  }

  /**
   * Returns the auto expand minimum width.
   * 
   * @return the minimum width in pixels
   */
  public int getAutoExpandMin() {
    return autoExpandMin;
  }

  /**
   * Returns the body element.
   * 
   * @return the body element
   */
  public XElement getBody() {
    return body;
  }

  /**
   * Returns the grid's &lt;TD> HtmlElement at the specified coordinates.
   * 
   * @param row the row index in which to find the cell
   * @param col the column index of the cell
   * @return the &lt;TD> at the specified coordinates
   */
  public Element getCell(int row, int col) {
    Element rowEl = getRow(row);
    if (rowEl == null || !rowEl.hasChildNodes() || col < 0) {
      return null;
    } else if (!enableRowBody) {
      return (Element) rowEl.getChildNodes().getItem(col);
    } else {
      return (Element) rowEl.getFirstChildElement().getFirstChildElement().getFirstChildElement().getFirstChildElement().getNextSiblingElement().getFirstChildElement().getChild(
          col);
    }
  }

  /**
   * Returns the editor parent element.
   * 
   * @return the editor element
   */
  public Element getEditorParent() {
    return scroller;
  }

  /**
   * Returns the empty text.
   * 
   * @return the empty text
   */
  public String getEmptyText() {
    return emptyText;
  }

  /**
   * Returns the grid's column header.
   * 
   * @return the header
   */
  public ColumnHeader<M> getHeader() {
    return header;
  }

  /**
   * Return the &lt;TR> HtmlElement which represents a Grid row for the
   * specified index.
   * 
   * @param row the row index
   * @return the &lt;TR> element
   */
  public Element getRow(int row) {
    if (row < 0) {
      return null;
    }
    return getRows().getItem(row);
  }

  /**
   * Return the &lt;TR> HtmlElement which represents a Grid row for the
   * specified model.
   * 
   * @param m the model
   * @return the &lt;TR> element
   */
  public Element getRow(M m) {
    return getRow(ds.indexOf(m));
  }

  public Element getRowBody(Element row) {
    return appearance.getRowBody(row);
  }

  /**
   * Returns the number of rows the first column should span when row bodies
   * have been enabled.
   * 
   * @return the rowspan
   */
  public int getRowBodyRowSpan() {
    return rowBodyRowSpan;
  }

  /**
   * Returns the scroll element.
   * 
   * @return the scroll element
   */
  public XElement getScroller() {
    return scroller;
  }

  /**
   * Returns the current scroll state.
   * 
   * @return the scroll state
   */
  public Point getScrollState() {
    return new Point(scroller.getScrollLeft(), scroller.getScrollTop());
  }

  /**
   * Returns the view config.
   * 
   * @return the view config
   */
  public GridViewConfig<M> getViewConfig() {
    return viewConfig;
  }

  /**
   * Returns true if the grid width will be adjusted based on visibility of
   * horizontal scroll bar.
   * 
   * @return true if adjusting
   */
  public boolean isAdjustForHScroll() {
    return adjustForHScroll;
  }

  /**
   * Returns true if auto fill is enabled.
   * 
   * @return true for auto fill
   */
  public boolean isAutoFill() {
    return autoFill;
  }

  /**
   * Returns true if column lines are enabled.
   * 
   * @return true if column lines are enabled
   */
  public boolean isColumnLines() {
    return columnLines;
  }

  /**
   * Returns true if rows are updated deferred on updates.
   * 
   * @return true if updates deferred
   */
  public boolean isDeferUpdates() {
    return deferUpdates;
  }

  /**
   * Returns true if row bodies are enabled.
   * 
   * @return true for row bodies
   */
  public boolean isEnableRowBody() {
    return enableRowBody;
  }

  /**
   * Returns true if force fit is enabled.
   * 
   * @return true for force fit
   */
  public boolean isForceFit() {
    return forceFit;
  }

  /**
   * Returns true if the given element is selectable.
   * 
   * @param target the element to check
   * @return true if the given element is selectable
   */
  public boolean isSelectableTarget(Element target) {
    if (target == null) {
      return false;
    }

    String tag = target.getTagName();
    if ("input".equalsIgnoreCase(tag) || "textarea".equalsIgnoreCase(tag)) {
      return false;
    }

    int colIndex = findCellIndex(target, null);
    Element cellParent = getCell(findRowIndex(target), colIndex);

    com.google.gwt.cell.client.Cell<?> cell = grid.getColumnModel().getCell(colIndex);
    if (cell != null && cellParent != null && cellParent.isOrHasChild(target) && cell.handlesSelection()) {
      return false;
    }
    return true;
  }

  /**
   * Returns true if dirty cell markers are enabled.
   * 
   * @return true of dirty cell markers
   */
  public boolean isShowDirtyCells() {
    return showDirtyCells;
  }

  /**
   * Returns true if sorting is enabled.
   * 
   * @return true for sorting
   */
  public boolean isSortingEnabled() {
    return !headerDisabled;
  }

  /**
   * Returns true if row striping is enabled.
   * 
   * @return the strip row state
   */
  public boolean isStripeRows() {
    return stripeRows;
  }

  /**
   * Returns true if rows are highlighted on mouse over.
   * 
   * @return the track mouse state
   */
  public boolean isTrackMouseOver() {
    return trackMouseOver;
  }

  /**
   * Lays out the grid view, adjusting the header and footer width and
   * accounting for force fit and auto fill settings.
   */
  public void layout() {
    layout(false);
  }

  /**
   * Rebuilds the grid using its current configuration and data.
   * 
   * @param headerToo true to refresh the header
   */
  public void refresh(boolean headerToo) {
    if (grid != null && grid.isViewReady()) {

      if (!preventScrollToTopOnRefresh) {
        scrollToTop();
      }

      if (GXT.isIE()) {
        dataTableBody.removeChildren();
        dataTableSizingHead.removeChildren();
      } else {
        dataTableBody.setInnerHTML("");
        dataTableSizingHead.setInnerHTML("");
      }

      DomHelper.insertHtml("afterBegin", dataTableSizingHead, renderHiddenHeaders(getColumnWidths()).asString());
      DomHelper.insertHtml("afterBegin", dataTableBody, renderRows(0, -1).asString());

      dataTable.getStyle().setWidth(getTotalWidth(), Unit.PX);

      if (headerToo) {
        sortState = null;
        header.release();

        initColumnHeader();
        renderHeader();
        if (grid.isAttached()) {
          ComponentHelper.doAttach(header);
        }
        header.setEnableColumnResizing(grid.isColumnResize());
        header.setEnableColumnReorder(grid.isColumnReordering());
      }

      processRows(0, true);

      if (footer != null) {
        ComponentHelper.doDetach(footer);
        footer.getElement().removeFromParent();
      }
      if (cm.getAggregationRows().size() > 0) {
        footer = new ColumnFooter<M>(grid, cm);
        renderFooter();
        if (grid.isAttached()) {
          ComponentHelper.doAttach(footer);
        }
      }

      calculateVBar(true);

      updateHeaderSortState();

      applyEmptyText();

      grid.getElement().repaint();

      grid.fireEvent(new RefreshEvent());
    }
  }

  /**
   * Scrolls the grid to the top.
   */
  public void scrollToTop() {
    scroller.setScrollTop(0);
    scroller.setScrollLeft(0);
  }

  /**
   * True to adjust the grid width when the horizontal scrollbar is hidden and
   * visible (defaults to true).
   * 
   * @param adjustForHScroll true to adjust for horizontal scroll bar
   */
  public void setAdjustForHScroll(boolean adjustForHScroll) {
    this.adjustForHScroll = adjustForHScroll;
  }

  /**
   * The id of a column in this grid that should expand to fill unused space
   * (pre-render). This id can not be 0.
   * 
   * @param autoExpandColumn the auto expand column
   */
  public void setAutoExpandColumn(ColumnConfig<M, ?> autoExpandColumn) {
    this.autoExpandColumn = autoExpandColumn;
  }

  /**
   * The maximum width the autoExpandColumn can have (if enabled) (defaults to
   * 500, pre-render).
   * 
   * @param autoExpandMax the auto expand max
   */
  public void setAutoExpandMax(int autoExpandMax) {
    this.autoExpandMax = autoExpandMax;
  }

  /**
   * The minimum width the autoExpandColumn can have (if enabled)(pre-render).
   * 
   * @param autoExpandMin the auto expand min width
   */
  public void setAutoExpandMin(int autoExpandMin) {
    this.autoExpandMin = autoExpandMin;
  }

  /**
   * True to auto expand the columns to fit the grid <b>when the grid is
   * created</b>.
   * 
   * @param autoFill true to expand
   */
  public void setAutoFill(boolean autoFill) {
    this.autoFill = autoFill;
  }

  /**
   * True to enable column separation lines (defaults to false).
   * 
   * @param columnLines true to enable column separation lines
   */
  public void setColumnLines(boolean columnLines) {
    this.columnLines = columnLines;
  }

  /**
   * True to update rows deferred (defaults to false).
   * 
   * @param deferUpdates true to update deferred
   */
  public void setDeferUpdates(boolean deferUpdates) {
    this.deferUpdates = deferUpdates;
  }

  /**
   * Default text to display in the grid body when no rows are available
   * (defaults to '').
   * 
   * @param emptyText the empty text
   */
  public void setEmptyText(String emptyText) {
    this.emptyText = emptyText;
  }

  /**
   * True to enable a column spanning row body, as used by {@link RowExpander}
   * (defaults to false).
   * 
   * @param enableRowBody true to enable row bodies
   */
  public void setEnableRowBody(boolean enableRowBody) {
    this.enableRowBody = enableRowBody;
  }

  /**
   * True to auto expand/contract the size of the columns to fit the grid width
   * and prevent horizontal scrolling.
   * 
   * @param forceFit true to force fit
   */
  public void setForceFit(boolean forceFit) {
    this.forceFit = forceFit;
  }

  /**
   * Sets the rowspan the first column should span when row bodies have been
   * enabled (defaults to 1).
   * 
   * @param rowBodyRowSpan the rowspan
   */
  public void setRowBodyRowSpan(int rowBodyRowSpan) {
    this.rowBodyRowSpan = rowBodyRowSpan;
  }

  /**
   * True to display a red triangle in the upper left corner of any cells which
   * are "dirty" as defined by any existing records in the data store (defaults
   * to true).
   * 
   * @param showDirtyCells true to display the dirty flag
   */
  public void setShowDirtyCells(boolean showDirtyCells) {
    this.showDirtyCells = showDirtyCells;
  }

  /**
   * True to allow column sorting when the user clicks a column (defaults to
   * true).
   * 
   * @param sortable true for sortable columns
   */
  public void setSortingEnabled(boolean sortable) {
    this.headerDisabled = !sortable;
  }

  /**
   * True to stripe the rows (defaults to false).
   * 
   * @param stripeRows true to strip rows
   */
  public void setStripeRows(boolean stripeRows) {
    this.stripeRows = stripeRows;
  }

  /**
   * True to highlight rows when the mouse is over (defaults to true).
   * 
   * @param trackMouseOver true to highlight rows on mouse over
   */
  public void setTrackMouseOver(boolean trackMouseOver) {
    this.trackMouseOver = trackMouseOver;
  }

  /**
   * Sets the view config.
   * 
   * @param viewConfig the view config
   */
  public void setViewConfig(GridViewConfig<M> viewConfig) {
    this.viewConfig = viewConfig;
  }

  protected void adjustColumnWidths(int[] columnWidths) {
    int clen = cm.getColumnCount();

    NodeList<Element> tables = scroller.select("." + appearance.styles().dataTable());

    for (int t = 0, len = tables.getLength(); t < len; t++) {
      XElement table = tables.getItem(t).cast();

      table.getStyle().setWidth(getTotalWidth(), Unit.PX);

      NodeList<Element> ths = getTableHeads(table);
      if (ths == null) {
        continue;
      }

      for (int i = 0; i < ths.getLength(); i++) {
        ths.getItem(i).getStyle().setPropertyPx("width", cm.isHidden(i) ? 0 : columnWidths[i]);
      }
    }

    for (int i = 0; i < clen; i++) {
      header.updateColumnWidth(i, columnWidths[i]);
      if (footer != null) {
        footer.updateColumnWidth(i, columnWidths[i]);
      }
    }

    // safari cell widths incorrect
    if (GXT.isSafari()) {
      dataTable.getStyle().setProperty("display", "block");

      Scheduler.get().scheduleFinally(new ScheduledCommand() {

        @Override
        public void execute() {
          dataTable.getStyle().clearDisplay();
        }
      });
    }
  }

  /**
   * Invoked after the view element is first attached, performs steps that
   * require that the view element is attached.
   */
  protected void afterRender() {
    DomHelper.insertHtml("afterBegin", dataTableBody, renderRows(0, -1).asString());

    dataTable.getStyle().setWidth(getTotalWidth(), Unit.PX);

    processRows(0, true);

    // chrome overflow: hidden not working on render
    // alignment issues with safari and ie8
    if (GXT.isSafari() || GXT.isChrome() || GXT.isIE8()) {
      dataTable.getStyle().setProperty("display", "block");

      Scheduler.get().scheduleFinally(new ScheduledCommand() {

        @Override
        public void execute() {
          dataTable.getStyle().clearDisplay();
        }
      });
    }

    if (footer != null && grid.getLazyRowRender() > 0) {
      footer.refresh();
    }

    int sh = scroller.getComputedHeight();
    int dh = body.getComputedHeight();
    boolean vbar = dh < sh;
    if (vbar) {
      this.vbar = !vbar;
      lastViewWidth = -1;
      layout();
    }

    applyEmptyText();
  }

  /**
   * Applies the empty text, normalizing it to non-breaking space if necessary,
   * then displaying it if the grid is empty.
   */
  protected void applyEmptyText() {
    if (emptyText == null) {
      emptyText = "&nbsp;";
    }
    if (!hasRows()) {
      if (GXT.isIE()) {
        dataTableBody.removeChildren();
      } else {
        dataTableBody.setInnerHTML("");
      }

      SafeHtml con = appearance.renderEmptyContent(emptyText);
      con = tpls.tr("", tpls.tdWrap(cm.getColumnCount(), "", styles.empty(), con));
      DomHelper.append(dataTableBody, con.asString());
    }
  }

  /**
   * Expands the column that was specified (via {@link #setAutoExpandColumn}) as
   * the column in this grid that should expand to fill unused space.
   * 
   * @param preventUpdate true to update the column model width without updating
   *          the displayed width.
   */
  protected void autoExpand(boolean preventUpdate) {
    if (!userResized && getAutoExpandColumn() != null) {
      int tw = cm.getTotalWidth(false);
      int aw = grid.getOffsetWidth(true) - getScrollAdjust();
      if (tw != aw) {
        int ci = cm.indexOf(getAutoExpandColumn());
        assert ci != Style.DEFAULT : "auto expand column not found";
        if (cm.isHidden(ci)) {
          return;
        }
        int currentWidth = cm.getColumnWidth(ci);
        int cw = Math.min(Math.max(((aw - tw) + currentWidth), getAutoExpandMin()), getAutoExpandMax());
        if (cw != currentWidth) {
          cm.setColumnWidth(ci, cw, true);

          if (!preventUpdate) {
            updateColumnWidth(ci, cw);
          }
        }
      }
    }
  }

  /**
   * Determines whether the need for a vertical scroll bar has changed and if so
   * updates the display.
   * 
   * @param force true to force the display to update regardless of whether a
   *          change has occurred.
   */
  protected void calculateVBar(boolean force) {
    if (force) {
      resize();
    }
    int sh = scroller.getComputedHeight();
    int dh = body.getComputedHeight();
    boolean vbar = dh > sh;
    if (force || this.vbar != vbar) {
      this.vbar = vbar;
      lastViewWidth = -1;
      layout(true);
    }
  }

  /**
   * Creates a context menu for the given column, including sort menu items and
   * column visibility sub-menu.
   * 
   * @param colIndex the column index
   * @return the context menu for the given column
   */
  protected Menu createContextMenu(final int colIndex) {
    final Menu menu = new Menu();

    if (cm.isSortable(colIndex)) {
      MenuItem item = new MenuItem();
      item.setText(DefaultMessages.getMessages().gridView_sortAscText());
      item.setIcon(header.getAppearance().sortAscendingIcon());
      item.addSelectionHandler(new SelectionHandler<Item>() {

        @Override
        public void onSelection(SelectionEvent<Item> event) {
          doSort(colIndex, SortDir.ASC);

        }
      });
      menu.add(item);

      item = new MenuItem();
      item.setText(DefaultMessages.getMessages().gridView_sortDescText());
      item.setIcon(header.getAppearance().sortDescendingIcon());
      item.addSelectionHandler(new SelectionHandler<Item>() {

        @Override
        public void onSelection(SelectionEvent<Item> event) {
          doSort(colIndex, SortDir.DESC);

        }
      });
      menu.add(item);
    }

    MenuItem columns = new MenuItem();
    columns.setText(DefaultMessages.getMessages().gridView_columnsText());
    columns.setIcon(header.getAppearance().columnsIcon());
    columns.setData("gxt-columns", "true");

    final Menu columnMenu = new Menu();

    int cols = cm.getColumnCount();
    for (int i = 0; i < cols; i++) {
      ColumnConfig<M, ?> config = cm.getColumn(i);
      if (shouldNotCount(i, false)) {
        continue;
      }
      final int fcol = i;
      final CheckMenuItem check = new CheckMenuItem();
      check.setHideOnClick(false);
      check.setText(cm.getColumnHeader(i).asString());
      check.setChecked(!cm.isHidden(i));

      if (!config.isHideable()) {
        check.disable();
      }

      check.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {

        @Override
        public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
          cm.setHidden(fcol, !cm.isHidden(fcol));
          restrictMenu(columnMenu);

        }
      });
      columnMenu.add(check);
    }

    restrictMenu(columnMenu);
    columns.setEnabled(columnMenu.getWidgetCount() > 0);
    columns.setSubMenu(columnMenu);
    menu.add(columns);
    return menu;
  }

  /**
   * Helper method that creates a StoreSortInfo from the given ColumnConfig and
   * sort direction. This will use the provided {@link Comparator}, if any,
   * otherwise will fall back to assuming that the data in the column is
   * {@link Comparable}.
   * 
   * @param column the column config
   * @param sortDir the sort direction
   * @return the new store sort info instance
   */
  protected <V> StoreSortInfo<M> createStoreSortInfo(ColumnConfig<M, V> column, SortDir sortDir) {
    if (column.getComparator() == null) {
      // These casts can fail, but in dev mode the exception will be caught by
      // the try/catch in doSort, unless there are no items in the Store
      @SuppressWarnings({"unchecked", "rawtypes"})
      ValueProvider<M, Comparable> vp = (ValueProvider) column.getValueProvider();
      @SuppressWarnings("unchecked")
      StoreSortInfo<M> s = new StoreSortInfo<M>(vp, sortDir);
      return s;
    } else {
      return new StoreSortInfo<M>(column.getValueProvider(), column.getComparator(), sortDir);
    }
  }

  /**
   * Attaches ancillary widgets such as the header and footer to the grid.
   */
  protected void doAttach() {
    ComponentHelper.doAttach(header);
    ComponentHelper.doAttach(footer);
  }

  /**
   * Detaches ancillary widgets such as the header and footer from the grid.
   */
  protected void doDetach() {
    ComponentHelper.doDetach(header);
    ComponentHelper.doDetach(footer);
  }

  /**
   * Renders the grid view into safe HTML.
   * 
   * @param cs the column attributes required for rendering
   * @param rows the data models for the rows to be rendered
   * @param startRow the index of the first row in <code>rows</code>
   */
  protected SafeHtml doRender(List<ColumnData> cs, List<M> rows, int startRow) {
    final int colCount = cm.getColumnCount();
    final int last = colCount - 1;

    int[] columnWidths = getColumnWidths();

    // root builder
    SafeHtmlBuilder buf = new SafeHtmlBuilder();

    final SafeStyles rowStyles = SafeStylesUtils.fromTrustedString("width: " + getTotalWidth() + "px;");

    final String unselectableClass = " " + unselectable;
    final String rowAltClass = " " + styles.rowAlt();
    final String rowDirtyClass = " " + styles.rowDirty();

    final String cellClass = styles.cell();
    final String cellInnerClass = styles.cellInner();
    final String cellFirstClass = " x-grid-cell-first";
    final String cellLastClass = " x-grid-cell-last";
    final String cellDirty = " " + styles.cellDirty();

    final String rowWrap = styles.rowWrap();
    final String rowBody = styles.rowBody();
    final String rowBodyRow = styles.rowBodyRow();

    // loop over all rows
    for (int j = 0; j < rows.size(); j++) {
      M model = rows.get(j);

      ListStore<M>.Record r = ds.hasRecord(model) ? ds.getRecord(model) : null;

      int rowBodyColSpanCount = colCount;
      if (enableRowBody) {
        for (ColumnConfig<M, ?> c : cm.getColumns()) {
          if (c instanceof RowExpander) {
            rowBodyColSpanCount--;
          }
        }
      }

      int rowIndex = (j + startRow);

      String rowClasses = styles.row();

      if (!selectable) {
        rowClasses += unselectableClass;
      }
      if (isStripeRows() && ((rowIndex + 1) % 2 == 0)) {
        rowClasses += rowAltClass;
      }

      if (showDirtyCells && r != null && r.isDirty()) {
        rowClasses += rowDirtyClass;
      }

      if (viewConfig != null) {
        rowClasses += " " + viewConfig.getRowStyle(model, rowIndex);
      }

      SafeHtmlBuilder trBuilder = new SafeHtmlBuilder();

      // loop each cell per row
      for (int i = 0; i < colCount; i++) {
        SafeHtml rv = getRenderedValue(rowIndex, i, model, r);
        ColumnConfig<M, ?> columnConfig = cm.getColumn(i);
        ColumnData columnData = cs.get(i);

        String cellClasses = cellClass;
        if (i == 0) {
          cellClasses += cellFirstClass;
        } else if (i == last) {
          cellClasses += cellLastClass;
        }

        String cellInnerClasses = cellInnerClass;
        if (columnConfig.getColumnTextClassName() != null) {
          cellInnerClasses += " " + columnConfig.getColumnTextClassName();
        }

        String id = columnConfig.getColumnClassSuffix();

        if (columnData.getClassNames() != null) {
          cellClasses += " " + columnData.getClassNames();
        }

        if (id != null && !id.equals("")) {
          cellClasses += " x-grid-td-" + id;
        }

        if (showDirtyCells && r != null && r.getChange(columnConfig.getValueProvider()) != null) {
          cellClasses += cellDirty;
        }

        if (viewConfig != null) {
          cellClasses += " " + viewConfig.getColStyle(model, cm.getValueProvider(i), rowIndex, i);
        }

        final SafeStyles cellStyles = columnData.getStyles();

        final SafeHtml tdContent;
        if (enableRowBody && i == 0) {
          tdContent = tpls.tdRowSpan(i, cellClasses, cellStyles, rowBodyRowSpan, rv);
        } else {
          tdContent = tpls.td(i, cellClasses, cellStyles, cellInnerClasses,
              columnConfig.getColumnTextStyle(), rv);
        }
        trBuilder.append(tdContent);
      }

      if (enableRowBody) {
        String cls = styles.dataTable() + " x-grid-resizer";

        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        sb.append(tpls.tr("", trBuilder.toSafeHtml()));
        sb.appendHtmlConstant("<tr class=" + rowBodyRow + "><td colspan=" + rowBodyColSpanCount + "><div class="
            + rowBody + "></div></td></tr>");

        buf.append(tpls.tr(
            rowClasses,
            tpls.tdWrap(colCount, "", rowWrap,
                tpls.table(cls, rowStyles, sb.toSafeHtml(), renderHiddenHeaders(columnWidths)))));

      } else {
        buf.append(tpls.tr(rowClasses, trBuilder.toSafeHtml()));
      }

    }
    // end row loop
    return buf.toSafeHtml();

  }

  /**
   * Defaults to assume one sort at a time.
   * 
   * @param colIndex the column to sort
   * @param sortDir the sort direction
   */
  protected void doSort(int colIndex, SortDir sortDir) {
    ColumnConfig<M, ?> column = cm.getColumn(colIndex);
    if (!isRemoteSort()) {
      ds.clearSortInfo();

      StoreSortInfo<M> s = createStoreSortInfo(column, sortDir);

      if (sortDir == null && storeSortInfo != null && storeSortInfo.getValueProvider() == column.getValueProvider()) {
        s.setDirection(storeSortInfo.getDirection() == SortDir.ASC ? SortDir.DESC : SortDir.ASC);
      } else if (sortDir == null) {
        s.setDirection(SortDir.ASC);
      }

      if (GWT.isProdMode()) {
        ds.addSortInfo(s);
      } else {
        try {
          // addSortInfo will apply its sort when called, which might trigger an
          // exception if the column passed in's data isn't Comparable
          ds.addSortInfo(s);
        } catch (ClassCastException ex) {
          GWT.log("Column can't be sorted " + column.getValueProvider().getPath()
              + " is not Comparable, and no Comparator was set for that column. ", ex);
          throw ex;
        }
      }

    } else {
      ValueProvider<? super M, ?> vp = column.getValueProvider();

      SortInfoBean bean = new SortInfoBean(vp, sortDir);

      if (sortDir == null && sortState != null && vp.getPath().equals(sortState.getSortField())) {
        bean.setSortDir(sortState.getSortDir() == SortDir.ASC ? SortDir.DESC : SortDir.ASC);

      } else if (sortDir == null) {
        bean.setSortDir(SortDir.ASC);
      }

      grid.getLoader().clearSortInfo();
      grid.getLoader().addSortInfo(bean);
      grid.getLoader().load();
    }
  }

  /**
   * Distribute the width of the columns amongst the available grid width as
   * required by {@link #setAutoFill(boolean)} and {@link #setForceFit(boolean)}
   * .
   * 
   * @param preventRefresh true to perform calculations and update column models
   *          but do not update display
   * @param onlyExpand unused in <code>GridView</code> implementation
   * @param omitColumn index of column to exclude from operation
   */
  // TODO: Consider removing unused parameter onlyExpand or adding support for
  // it
  protected void fitColumns(boolean preventRefresh, boolean onlyExpand, int omitColumn) {
    int tw = getTotalWidth();
    int aw = grid.getElement().getWidth(true) - getScrollAdjust();
    if (aw <= 0) {
      aw = grid.getElement().getComputedWidth();
    }

    if (aw < 20 || aw > 2000) { // not initialized, so don't screw up the
      // default widths
      return;
    }

    int extra = (int) aw - tw;

    if (extra == 0) {
      return;
    }

    int colCount = cm.getColumnCount();
    Stack<Integer> cols = new Stack<Integer>();
    int width = 0;
    int w;

    for (int i = 0; i < colCount; i++) {
      w = cm.getColumnWidth(i);
      if (!cm.isHidden(i) && !cm.isFixed(i) && i != omitColumn) {
        cols.push(i);
        cols.push(w);
        width += w;
      }
    }

    double frac = ((double) (extra)) / width;
    while (cols.size() > 0) {
      w = cols.pop();
      int i = cols.pop();
      int ww = Math.max(grid.getMinColumnWidth(), (int) Math.floor(w + w * frac));
      cm.setColumnWidth(i, ww, true);
    }

    tw = getTotalWidth();
    if (tw > aw) {
      width = 0;
      for (int i = 0; i < colCount; i++) {
        w = cm.getColumnWidth(i);
        if (!cm.isHidden(i) && !cm.isFixed(i) && w > grid.getMinColumnWidth()) {
          cols.push(i);
          cols.push(w);
          width += w;
        }
      }
      frac = ((double) (aw - tw)) / width;
      while (cols.size() > 0) {
        w = cols.pop();
        int i = cols.pop();
        int ww = Math.max(grid.getMinColumnWidth(), (int) Math.floor(w + w * frac));
        cm.setColumnWidth(i, ww, true);
      }
    }

    if (!preventRefresh) {
      updateAllColumnWidths();
    }
  }

  /**
   * Efficiently returns an {@link XElement} representation of the given
   * {@link Element}.
   * 
   * @param elem the <code>Element</code> to cast
   * @return the <code>XElement</code> representation
   */
  protected XElement fly(Element elem) {
    return XElement.as(elem);
  }

  /**
   * Gets the properties required for rendering the columns.
   * 
   * @return a list of the grid's column properties
   */
  protected List<ColumnData> getColumnData() {
    int colCount = cm.getColumnCount();
    List<ColumnData> cs = new ArrayList<ColumnData>();
    for (int i = 0; i < colCount; i++) {
      ColumnData data = new ColumnData();
      data.setStyles(getColumnStyle(i, false));
      cs.add(data);
    }
    return cs;
  }

  /**
   * Returns the CSS styles for the given column.
   * 
   * @param colIndex the column index
   * @param isHeader true to include the column header styles
   * @return the styles
   */
  protected SafeStyles getColumnStyle(int colIndex, boolean isHeader) {
    SafeStylesBuilder builder = new SafeStylesBuilder();
    if (!isHeader) {
      SafeStyles columnStyles = cm.getColumnStyles(colIndex);
      if (columnStyles != null) {
        builder.append(columnStyles);
      }
    }

    HorizontalAlignmentConstant align = cm.getColumnAlignment(colIndex);
    if (align != null) {
      builder.append(SafeStylesUtils.fromTrustedString("text-align:" + align.getTextAlignString() + ";"));
    }
    return builder.toSafeStyles();
  }

  /**
   * Returns the width of the given column
   * 
   * @param col the column index
   * @return the column width
   */
  protected int getColumnWidth(int col) {
    return cm.getColumnWidth(col);
  }

  protected int[] getColumnWidths() {
    int colCount = cm.getColumnCount();
    int[] columnWidths = new int[colCount];
    for (int i = 0; i < colCount; i++) {
      columnWidths[i] = getColumnWidth(i);
    }
    return columnWidths;
  }

  /**
   * Returns the offset width of the grid including the total visible column
   * width and the amount required or reserved for the vertical scroll bar.
   * 
   * @return the grid's offset width
   */
  protected int getOffsetWidth() {
    return (getTotalWidth() + getScrollAdjust());
  }

  /**
   * Renders the value of a cell into safe HTML.
   * 
   * @param rowIndex the row index
   * @param colIndex the column index
   * @param m the data model
   * @param record the optional {@link Record} for this row (may be null)
   * @return the safe HTML representing the cell
   */
  protected <N> SafeHtml getRenderedValue(int rowIndex, int colIndex, M m, ListStore<M>.Record record) {
    ValueProvider<? super M, N> valueProvider = cm.getValueProvider(colIndex);
    N val = null;
    if (record != null) {
      val = record.getValue(valueProvider);
    } else {
      val = valueProvider.getValue(m);
    }
    Cell<N> r = cm.getCell(colIndex);
    if (r != null) {
      SafeHtmlBuilder sb = new SafeHtmlBuilder();
      r.render(new Context(rowIndex, colIndex, ds.getKeyProvider().getKey(m)), val, sb);
      return sb.toSafeHtml();
    }

    String text = null;
    if (val != null) {
      text = val.toString();
    }
    return Util.isEmptyString(text) ? SafeHtmlUtils.fromTrustedString("&#160;") : SafeHtmlUtils.fromString(text);
  }

  /**
   * Returns the HTML elements representing the body of the table.
   * 
   * @return the HTML elements representing the rows in the table (empty if the
   *         table has no rows)
   */
  protected NodeList<Element> getRows() {
    if (!hasRows()) {
      return JavaScriptObject.createArray().cast();
    }
    return appearance.getRows(body);
  }

  /**
   * Returns the number of pixels required or reserved for the vertical scroll
   * bar.
   * 
   * @return the nominal width of the vertical scroll bar
   */
  protected int getScrollAdjust() {
    return adjustForHScroll ? (scroller != null ? (vbar ? scrollOffset + 1 : 2) : scrollOffset) : scrollOffset;
  }

  /**
   * Returns the grid's sort information.
   * 
   * @return the grid's sort information (or null if the grid is not sorted).
   */
  protected StoreSortInfo<M> getSortState() {
    if (ds.getSortInfo().size() > 0) {
      return ds.getSortInfo().get(0);
    }
    return null;
  }

  /**
   * The total width of the visible columns in the grid (for the width including
   * the vertical scroll bar, see {@link #getOffsetWidth()}.
   * 
   * @return the total width of the columns in the grid.
   */
  protected int getTotalWidth() {
    return cm.getTotalWidth();
  }

  /**
   * Handles browser events of interest to the grid view. The default
   * implementation for {@link GridView} includes support for mouse-over
   * tracking (see {@link Grid#setTrackMouseOver(boolean)} and scroll bar
   * synchronization.
   * 
   * @param event the browser event
   */
  protected void handleComponentEvent(Event event) {
    if (trackMouseOver) {
      Element row = Element.is(event.getEventTarget()) ? findRow((Element) event.getEventTarget().cast()) : null;

      switch (event.getTypeInt()) {
        case Event.ONMOUSEMOVE:

          if (overRow != null && row == null) {
            onRowOut(overRow);
          } else if (row != null && overRow != row) {
            if (overRow != null) {
              onRowOut(overRow);
            }
            onRowOver(row);
          }

          break;

        case Event.ONMOUSEOVER:
          EventTarget from = event.getRelatedEventTarget();
          if (from == null
              || (Element.is(from) && !DOM.isOrHasChild(grid.getElement(),
                  (com.google.gwt.user.client.Element) Element.as(from)))) {
            Element r = null;
            if (Element.is(event.getEventTarget())) {
              r = findRow(Element.as(event.getEventTarget()));
            }
            if (r != null) {
              onRowOver(r);
            }
          }
          break;
        case Event.ONMOUSEOUT:
          EventTarget to = event.getRelatedEventTarget();
          if (to == null
              || (Element.is(to) && !DOM.isOrHasChild(grid.getElement(),
                  (com.google.gwt.user.client.Element) Element.as(to)))) {
            if (overRow != null) {
              onRowOut(overRow);
            }
          }
          break;
        case Event.ONMOUSEDOWN:
          onMouseDown(event);
          break;
        case Event.ONSCROLL:
          if (scroller.isOrHasChild(Element.as(event.getEventTarget()))) {
            syncScroll();
          }
          break;
      }
    } else if (overRow != null) {
      trackMouseOver = true;
      onRowOut(overRow);
      trackMouseOver = false;
    }

    if (event.getTypeInt() == Event.ONSCROLL) {
      if (scroller.isOrHasChild(Element.as(event.getEventTarget()))) {
        syncScroll();
      }
    }
  }

  /**
   * Returns true if the grid has rows.
   * 
   * @return true if the grid has rows.
   */
  protected boolean hasRows() {
    if (dataTable == null || dataTableBody == null || dataTableBody.getChildCount() == 0) {
      return false;
    }

    Element emptyRowElement = dataTableBody.getFirstChildElement();
    if (emptyRowElement == null) {
      return false;
    }
    emptyRowElement = emptyRowElement.getFirstChildElement();
    if (emptyRowElement == null) {
      return false;
    }
    emptyRowElement = emptyRowElement.getFirstChildElement();
    if (emptyRowElement == null) {
      return false;
    }
    return !emptyRowElement.getClassName().equals(styles.empty());
  }

  /**
   * Initializes the view.
   * 
   * @param grid the grid
   */
  protected void init(final Grid<M> grid) {
    this.grid = grid;
    this.cm = grid.getColumnModel();
    selectable = grid.isAllowTextSelection();

    initListeners();

    grid.getElement().addClassName(appearance.styles().grid());

    grid.getElement().setClassName(styles.columnLines(), columnLines);

    initData(grid.getStore(), cm);
    initUI(grid);

    initColumnHeader();

    if (cm.getAggregationRows().size() > 0) {
      footer = new ColumnFooter<M>(grid, cm);
    }

    renderUI();
    grid.sinkEvents(Event.ONCLICK | Event.ONDBLCLICK | Event.MOUSEEVENTS);
  }

  /**
   * Creates and initializes the column header and saves reference for future
   * use.
   */
  protected void initColumnHeader() {
    header = new ColumnHeader<M>(grid, cm) {

      @Override
      protected Menu getContextMenu(int column) {
        return createContextMenu(column);
      }

      @Override
      protected void onColumnSplitterMoved(int colIndex, int width) {
        super.onColumnSplitterMoved(colIndex, width);
        GridView.this.onColumnSplitterMoved(colIndex, width);
      }

      @Override
      protected void onHeaderClick(Event ce, int column) {
        super.onHeaderClick(ce, column);
        GridView.this.onHeaderClick(column);
      }

      @Override
      protected void onKeyDown(Event ce, int index) {
        ce.stopPropagation();
        // auto select on key down
        if (grid.getSelectionModel() instanceof CellSelectionModel<?>) {
          CellSelectionModel<?> csm = (CellSelectionModel<?>) grid.getSelectionModel();
          csm.selectCell(0, index);
        } else {
          grid.getSelectionModel().select(0, false);
        }
      }

    };
    header.setSplitterWidth(splitterWidth);
    header.setMinColumnWidth(grid.getMinColumnWidth());
  }

  /**
   * Initializes the data.
   * 
   * @param ds the data store
   * @param cm the column model
   */
  protected void initData(ListStore<M> ds, ColumnModel<M> cm) {
    if (storeHandlerRegistration != null) {
      storeHandlerRegistration.removeHandler();
      storeHandlerRegistration = null;
    }
    if (ds != null) {
      storeHandlerRegistration = ds.addStoreHandlers(listener);
    }
    this.ds = ds;

    if (cmHandlerRegistration != null) {
      cmHandlerRegistration.removeHandler();
      cmHandlerRegistration = null;
    }
    if (cm != null) {
      cmHandlerRegistration = cm.addColumnModelHandlers(columnListener);
    }
    this.cm = cm;
  }

  /**
   * Collects references to the HTML elements of the grid view and saves them in
   * instance variables for future reference.
   */
  protected void initElements() {
    NodeList<Node> cs = grid.getElement().getChildNodes();

    // headerWrap = (XElement) cs.getItem(0);
    // headerInner = headerWrap.getFirstChildElement().cast();

    scroller = (XElement) cs.getItem(1);
    scroller.addEventsSunk(Event.ONSCROLL);

    if (forceFit) {
      scroller.getStyle().setOverflowX(Overflow.HIDDEN);
    }

    body = scroller.getFirstChildElement().cast();

    dataTable = body.getFirstChildElement().cast();
    dataTableSizingHead = dataTable.getFirstChildElement().cast();
    dataTableBody = dataTableSizingHead.getNextSiblingElement().cast();

    focusEl = (XElement) scroller.appendChild(focusImpl.createFocusable());
    focusEl.addClassName(CommonStyles.get().noFocusOutline());
    if (focusEl.hasChildNodes()) {
      focusEl.getFirstChildElement().addClassName(CommonStyles.get().noFocusOutline());
      com.google.gwt.dom.client.Style focusElStyle = focusEl.getFirstChildElement().getStyle();
      focusElStyle.setBorderWidth(0, Unit.PX);
      focusElStyle.setFontSize(1, Unit.PX);
      focusElStyle.setPropertyPx("lineHeight", 1);
    }
    focusEl.setLeft(0);
    focusEl.setTop(0);
    focusEl.makePositionable(true);
    focusEl.addEventsSunk(Event.FOCUSEVENTS);
  }

  /**
   * Creates the grid view listeners, including {@link StoreHandlers} and
   * {@link ColumnModelHandlers}, and saves references for future use.
   */
  protected void initListeners() {
    listener = new StoreHandlers<M>() {

      @Override
      public void onAdd(StoreAddEvent<M> event) {
        GridView.this.onAdd(event.getItems(), event.getIndex());

      }

      @Override
      public void onClear(StoreClearEvent<M> event) {
        GridView.this.onClear(event);

      }

      @Override
      public void onDataChange(StoreDataChangeEvent<M> event) {
        GridView.this.onDataChanged(event);

      }

      @Override
      public void onFilter(StoreFilterEvent<M> event) {
        GridView.this.onDataChanged(null);
      }

      @Override
      public void onRecordChange(StoreRecordChangeEvent<M> event) {
        GridView.this.onUpdate(ds, Collections.singletonList(event.getRecord().getModel()));
      }

      @Override
      public void onRemove(StoreRemoveEvent<M> event) {
        GridView.this.onRemove(event.getItem(), event.getIndex(), false);

      }

      @Override
      public void onSort(StoreSortEvent<M> event) {
        GridView.this.onDataChanged(null);

      }

      @Override
      public void onUpdate(StoreUpdateEvent<M> event) {
        GridView.this.onUpdate(ds, event.getItems());
      }

    };

    columnListener = new ColumnModelHandlers() {
      @Override
      public void onColumnHeaderChange(ColumnHeaderChangeEvent event) {
        GridView.this.onHeaderChange(event.getIndex(), cm.getColumnHeader(event.getIndex()));

      }

      @Override
      public void onColumnHiddenChange(ColumnHiddenChangeEvent event) {
        GridView.this.onHiddenChange(cm, event.getIndex(), cm.isHidden(event.getIndex()));

      }

      @Override
      public void onColumnMove(ColumnMoveEvent event) {
        GridView.this.onColumnMove(event.getIndex());

      }

      @Override
      public void onColumnWidthChange(ColumnWidthChangeEvent event) {
        GridView.this.onColumnWidthChange(event.getIndex(), cm.getColumnWidth(event.getIndex()));

      }
    };
  }

  /**
   * Invoked to perform additional initialization of the grid view's user
   * interface, after the data has been initialized, the default implementation
   * for {@link GridView} does nothing.
   * 
   * @param grid the grid for this grid view
   */
  protected void initUI(final Grid<M> grid) {

  }

  /**
   * Inserts the given rows (already present in the grid's list store) into the
   * grid view.
   * 
   * @param firstRow the first row index
   * @param lastRow the last row index
   * @param isUpdate true if update to existing rows
   */
  protected void insertRows(int firstRow, int lastRow, boolean isUpdate) {
    if (lastRow < firstRow) {
      return;
    }

    if (!hasRows()) {
      if (GXT.isIE()) {
        dataTableBody.removeChildren();
      } else {
        dataTableBody.setInnerHTML("");
      }
    }

    SafeHtml html = renderRows(firstRow, lastRow);
    XElement before = getRow(firstRow).cast();

    if (before != null) {
      DomHelper.insertBefore(before, html.asString());
    } else {
      DomHelper.insertHtml("beforeEnd", dataTableBody, html.asString());
    }
    if (!isUpdate) {
      processRows(firstRow, false);
    }
  }

  /**
   * Return true if configured for remote sorting.
   * 
   * @return if configured for remote sorting.
   */
  protected boolean isRemoteSort() {
    return grid.getLoader() != null && grid.getLoader().isRemoteSort();
  }

  /**
   * Lays out the grid view, adjusting the header and footer width and
   * accounting for force fit and auto fill settings.
   * 
   * @param skipResize true to skip resizing of the grid view
   */
  protected void layout(boolean skipResize) {
    if (body == null) {
      return;
    }

    XElement c = grid.getElement();
    Size csize = c.getStyleSize();

    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("layout() " + csize);
    }

    int vw = csize.getWidth();
    if (vw < 10 || csize.getHeight() < 20) {
      return;
    }

    if (!skipResize) {
      resize();
    }

    if (forceFit || autoFill) {
      if (lastViewWidth != vw) {
        fitColumns(false, false, -1);
        header.updateTotalWidth(getOffsetWidth(), getTotalWidth());
        if (footer != null) {
          footer.updateTotalWidth(getOffsetWidth(), getTotalWidth());
        }
        lastViewWidth = vw;
      }
    } else {
      autoExpand(false);
      header.updateTotalWidth(getOffsetWidth(), getTotalWidth());
      if (footer != null) {
        footer.updateTotalWidth(getOffsetWidth(), getTotalWidth());
      }
      syncHeaderScroll();
    }

  }

  /**
   * Invoked after the grid has been hidden, the default implementation for
   * {@link GridView} does nothing.
   */
  protected void notifyHide() {
  }

  /**
   * Invoked after the grid has been shown, the default implementation for
   * {@link GridView} does nothing.
   */
  protected void notifyShow() {
  }

  /**
   * Handles adding new data models to the store.
   * 
   * @param models the new data models
   * @param index the index of the first model
   */
  protected void onAdd(List<M> models, int index) {
    if (grid != null && grid.isViewReady()) {
      insertRows(index, index + (models.size() - 1), false);
      addTask.delay(10);
    }
  }

  /**
   * Handles the clearing of the selection for the given cell.
   * 
   * @param row the row index
   * @param col the cell index
   */
  protected void onCellDeselect(int row, int col) {
    Element cell = getCell(row, col);
    if (cell != null) {
      appearance.onCellSelect(cell, false);
    }
  }

  /**
   * Handles selecting the given cell.
   * 
   * @param row the row index
   * @param col the cell index
   */
  protected void onCellSelect(int row, int col) {
    Element cell = getCell(row, col);
    if (cell != null) {
      appearance.onCellSelect(cell, true);
    }
  }

  /**
   * Handles clearing the store.
   * 
   * @param se the event that cleared the store
   */
  protected void onClear(StoreClearEvent<M> se) {
    refresh(false);
  }

  /**
   * Handles the click event, the default implementation for {@link GridView}
   * does nothing.
   * 
   * @param ce the click event
   */
  protected void onClick(Event ce) {

  }

  /**
   * Handles the column move request.
   * 
   * @param newIndex the destination column index
   */
  protected void onColumnMove(int newIndex) {
    boolean pScroll = preventScrollToTopOnRefresh;
    preventScrollToTopOnRefresh = true;
    refresh(true);
    preventScrollToTopOnRefresh = pScroll;
  }

  /**
   * Handles the column splitter move request.
   * 
   * @param colIndex the index of the column whose width is being adjusted
   * @param width the new width
   */
  protected void onColumnSplitterMoved(int colIndex, int width) {
    userResized = true;
    width = Math.max(grid.getMinColumnWidth(), width);
    cm.setColumnWidth(colIndex, width);
  }

  /**
   * Handles a change to the column model width (see
   * {@link ColumnModel#setColumnWidth(int, int)});
   * 
   * @param column the index of the column
   * @param width the new width
   */
  protected void onColumnWidthChange(int column, int width) {
    if (forceFit) {
      fitColumns(false, false, column);
      header.updateTotalWidth(getOffsetWidth(), getTotalWidth());
    } else {
      updateColumnWidth(column, width);
      header.updateTotalWidth(getOffsetWidth(), getTotalWidth());
      if (GXT.isIE()) {
        syncHeaderScroll();
      }
    }
  }

  /**
   * Handles a change in the data in the store, including changes to the filter
   * or sort state.
   * 
   * @param se the change (may be null)
   */
  protected void onDataChanged(StoreDataChangeEvent<M> se) {
    if (!grid.viewReady) return;
    refresh(false);
    if (grid != null && grid.isLoadMask()) {
      if (grid.isEnabled()) {
        grid.unmask();
      } else {
        grid.mask();
      }
    }
  }

  /**
   * Handles a change in the safe HTML that represents the header (see
   * {@link ColumnModel#setColumnHeader(int, SafeHtml)}).
   * 
   * @param column the column index
   * @param text the new safe HTML
   */
  protected void onHeaderChange(int column, SafeHtml text) {
    header.setHeader(column, text);
  }

  /**
   * Handles a header click event.
   * 
   * @param column the column that was clicked
   */
  protected void onHeaderClick(int column) {
    this.headerColumnIndex = column;
    if (!headerDisabled && cm.isSortable(column)) {
      doSort(column, null);
    }
  }

  /**
   * Handles a change in the column model's hidden state (see
   * {@link ColumnModel#setHidden(int, boolean)}).
   * 
   * @param cm the column model
   * @param col the column index
   * @param hidden true if the column is hidden
   */
  protected void onHiddenChange(ColumnModel<M> cm, int col, boolean hidden) {
    updateColumnHidden(col, hidden);
  }

  /**
   * Handles a request to change the highlight state of a row.
   * 
   * @param rowIndex the row index
   * @param highlight true to highlight the row
   */
  protected void onHighlightRow(int rowIndex, boolean highlight) {
    Element row = getRow(rowIndex);
    if (row != null) {
      appearance.onRowHighlight(row, highlight);
    }
  }

  /**
   * Invoked when a mouse down event occurs, the default implementation for
   * {@link GridView} does nothing.
   */
  protected void onMouseDown(Event ge) {

  }

  /**
   * Handles removing a data model from the store.
   * 
   * @param m the data model
   * @param index the row index
   * @param isUpdate true to indicate an update an existing row
   */
  protected void onRemove(M m, int index, boolean isUpdate) {
    if (grid != null && grid.isViewReady()) {
      removeRow(index);
      if (!isUpdate) {
        removeTask.delay(10);
      } else {
        removeTask.delay(0);
      }
      constrainFocusElement();
    }
  }

  /**
   * Handles clearing the selection on a row.
   * 
   * @param rowIndex the row index
   */
  protected void onRowDeselect(int rowIndex) {
    Element row = getRow(rowIndex);
    if (row != null) {
      appearance.onRowSelect(row, false);
      appearance.onRowHighlight(row, false);
    }
  }

  /**
   * Handles moving the mouse off a row.
   * 
   * @param row the HTML element for the row
   */
  protected void onRowOut(Element row) {
    if (isTrackMouseOver()) {
      if (overRow != null && overRow != row) {
        appearance.onRowOver(overRow, false);
      }
      appearance.onRowOver(row, false);
      overRow = null;
    }
  }

  /**
   * Handles moving the mouse onto a row.
   * 
   * @param row the HTML element for the row
   */
  protected void onRowOver(Element row) {
    if (isTrackMouseOver()) {
      appearance.onRowOver(row, true);
      overRow = row;
    }
  }

  /**
   * Handles setting the selection on a row.
   * 
   * @param rowIndex the row index
   */
  protected void onRowSelect(int rowIndex) {
    Element row = getRow(rowIndex);
    if (row != null) {
      onRowOut(row);
      appearance.onRowSelect(row, true);
    }
  }

  /**
   * Handles an update to data in the store.
   * 
   * @param store the store
   * @param models the updated data
   */
  protected void onUpdate(final ListStore<M> store, final List<M> models) {
    if (!deferUpdates) {
      for (M m : models) {
        refreshRow(store.indexOf(m));
      }
    } else {
      Timer t = new Timer() {
        @Override
        public void run() {
          for (M m : models) {
            refreshRow(store.indexOf(m));
            grid.getSelectionModel().onUpdate(m);
          }
        }
      };
      t.schedule(deferUpdateDelay);
    }
  }

  /**
   * Makes a pass through the rows in the grid to finalize the appearance, the
   * default implementation in {@link GridView} assigns the row index property
   * and stripes the rows (if striping is enabled).
   * 
   * @param startRow the row index
   * @param skipStripe true to prevent striping (striping is always prevented if
   *          {@link Grid#isStripeRows()} returns false).
   */
  protected void processRows(int startRow, boolean skipStripe) {
    if (ds.size() < 1) {
      return;
    }
    skipStripe = skipStripe || !grid.view.isStripeRows();
    NodeList<Element> rows = getRows();
    String cls = styles.rowAlt();
    for (int i = startRow, len = rows.getLength(); i < len; i++) {
      Element row = rows.getItem(i);
      row.setPropertyInt("rowindex", i);
      if (!skipStripe) {
        boolean isAlt = (i + 1) % 2 == 0;
        boolean hasAlt = row.getClassName() != null && row.getClassName().indexOf(cls) != -1;
        if (isAlt == hasAlt) {
          continue;
        }
        if (isAlt) {
          fly(row).addClassName(cls);
        } else {
          fly(row).removeClassName(cls);
        }
      }
    }
  }

  /**
   * Refreshes the displayed content for the given row.
   * 
   * @param row the row index
   */
  protected void refreshRow(int row) {
    if (grid != null && grid.isViewReady()) {
      M m = ds.get(row);
      if (m != null) {
        // do not change focus on refresh
        // handles situation with changing cell value with field binding
        focusEnabled = false;

        insertRows(row, row, true);
        getRow(row).setPropertyInt("rowindex", row);
        onRemove(m, row + 1, true);

        focusEnabled = true;
      }
    }
  }

  /**
   * Removes the given row.
   * 
   * @param row the row index
   */
  protected void removeRow(int row) {
    Element r = getRow(row);
    if (r != null) {
      fly(r).removeFromParent();
    }
  }

  /**
   * Renders the footer.
   */
  protected void renderFooter() {
    footer.setAllowTextSelection(false);

    grid.getElement().appendChild(footer.getElement());
    footer.refresh();
  }

  /**
   * Renders the header.
   */
  protected void renderHeader() {
    headerElem = header.getElement();
    grid.getElement().insertFirst(headerElem);
    header.refresh();
    if (grid.isHideHeaders()) {
      headerElem.setVisible(false);
    }
  }

  /**
   * Renders the hidden TH elements that keep the column widths.
   * 
   * @param columnWidths the column widths
   * @return
   */
  protected SafeHtml renderHiddenHeaders(int[] columnWidths) {
    SafeHtmlBuilder heads = new SafeHtmlBuilder();
    for (int i = 0; i < columnWidths.length; i++) {
      int w = cm.isHidden(i) ? 0 : columnWidths[i];
      SafeStylesBuilder builder = new SafeStylesBuilder();
      builder.appendTrustedString("height: 0px;");
      builder.appendTrustedString("width:" + w + "px;");
      heads.append(tpls.th("", builder.toSafeStyles()));
    }
    return tpls.tr(appearance.styles().headerRow(), heads.toSafeHtml());
  }

  /**
   * Renders the grid's rows.
   * 
   * @param startRow the index in the store of the first row to render
   * @param endRow the index of the last row to render (may be -1 to indicate
   *          all rows)
   * @return safe HTML representing the rendered rows
   */
  protected SafeHtml renderRows(int startRow, int endRow) {
    if (ds.size() < 1) {
      return SafeHtmlUtils.EMPTY_SAFE_HTML;
    }

    List<ColumnData> cs = getColumnData();

    if (endRow == -1) {
      endRow = ds.size() - 1;
    }

    List<M> rs = ds.subList(startRow, ++endRow);
    return doRender(cs, rs, startRow);
  }

  /**
   * Responsible for rendering all aspects of the grid view.
   */
  protected void renderUI() {
    renderHeader();

    initElements();

    DomHelper.insertHtml("afterBegin", dataTableSizingHead, renderHiddenHeaders(getColumnWidths()).asString());

    header.setEnableColumnResizing(grid.isColumnResize());
    header.setEnableColumnReorder(grid.isColumnReordering());

    if (footer != null) {
      renderFooter();
    }

    updateHeaderSortState();
  }

  /**
   * Resizes the grid view, adjusting the scroll bars and accounting for the
   * footer height (if any).
   */
  protected void resize() {
    if (body == null) {
      return;
    }

    Size csize = grid.getElement().getStyleSize();

    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("resize() " + csize);
    }

    int vw = csize.getWidth();
    int vh = 0;
    if (vw < 10 || csize.getHeight() < 22) {
      return;
    }

    if (grid.isAutoHeight()) {
      scroller.setWidth(vw);
    }

    int hdHeight = headerElem.getHeight(false);
    vh = csize.getHeight() - hdHeight;

    if (footer != null) {
      vh -= footer.getOffsetHeight();
    }

    if (!grid.isAutoHeight()) {
      scroller.setSize(vw, vh);
    }

    if (headerElem != null) {
      headerElem.setWidth(vw);
    }
    if (footer != null) {
      footer.setWidth(vw);
    }

    constrainFocusElement();
  }

  /**
   * Restores the scroll state.
   * 
   * @param state the state as returned from a previous call to
   *          {@link #getScrollState()}.
   */
  protected void restoreScroll(Point state) {
    if (state.getY() < scroller.getWidth(false)) {
      scroller.setScrollLeft(state.getX());
    }
    if (state.getX() < scroller.getHeight(false)) {
      scroller.setScrollTop(state.getY());
    }
  }

  /**
   * Synchronizes the header position (and footer, if present) with the
   * horizontal scroll bar.
   */
  protected void syncHeaderScroll() {
    int sl = scroller.getScrollLeft();
    headerElem.setScrollLeft(sl);
    // second time for IE (1/2 time first fails, other browsers ignore)
    headerElem.setScrollLeft(sl);

    if (footer != null) {
      footer.getElement().setScrollLeft(sl);
      footer.getElement().setScrollLeft(sl);
    }
  }

  /**
   * Synchronizes the grid scroll bars.
   */
  protected void syncScroll() {
    syncHeaderScroll();
    int scrollLeft = scroller.getScrollLeft();
    int scrollTop = scroller.getScrollTop();
    constrainFocusElement();
    grid.fireEvent(new BodyScrollEvent(scrollLeft, scrollTop));
  }

  /**
   * Invoked after all column widths have been updated, the default
   * implementation for {@link GridView} does nothing.
   */
  protected void templateOnAllColumnWidthsUpdated(int[] columnWidths, int tw) {

  }

  /**
   * Invoked after the hidden column status been updated, the default
   * implementation for {@link GridView} does nothing.
   */
  protected void templateOnColumnHiddenUpdated(int col, boolean hidden, int tw) {
    // template method
  }

  /**
   * Invoked after a column width has been updated, the default implementation
   * for {@link GridView} does nothing.
   */
  protected void templateOnColumnWidthUpdated(int col, int w, int tw) {
    // template method
  }

  /**
   * Synchronizes the displayed width of each column with the defined width of
   * each column from its column model.
   */
  protected void updateAllColumnWidths() {
    int tw = getTotalWidth();
    int clen = cm.getColumnCount();

    int[] columnWidths = new int[clen];
    for (int i = 0; i < clen; i++) {
      columnWidths[i] = cm.isHidden(i) ? 0 : getColumnWidth(i);
    }

    adjustColumnWidths(columnWidths);

    templateOnAllColumnWidthsUpdated(columnWidths, tw);
  }

  /**
   * Updates the row width and cell display properties to hide or show the given
   * column.
   * 
   * @param index the column index
   * @param hidden true to hide the column
   */
  protected void updateColumnHidden(int index, boolean hidden) {
    int tw = getTotalWidth();

    header.updateColumnHidden(index, hidden);
    if (footer != null) {
      footer.updateTotalWidth(getOffsetWidth(), tw);
      footer.updateColumnHidden(index, hidden);
    }

    NodeList<Element> tables = scroller.select("." + appearance.styles().dataTable());

    for (int t = 0, len = tables.getLength(); t < len; t++) {
      XElement table = tables.getItem(t).cast();

      table.getStyle().setWidth(getTotalWidth(), Unit.PX);

      NodeList<Element> ths = getTableHeads(table);
      if (ths == null) {
        continue;
      }

      if (index < ths.getLength()) {
        ths.getItem(index).getStyle().setPropertyPx("width", hidden ? 0 : getColumnWidth(index));
      }
    }

    dataTable.getStyle().setWidth(tw, Unit.PX);

    // cell widths incorrect
    if (GXT.isIE7() || GXT.isIE8() || GXT.isSafari()) {
      dataTable.getStyle().setProperty("display", "block");

      Scheduler.get().scheduleFinally(new ScheduledCommand() {

        @Override
        public void execute() {
          dataTable.getStyle().clearDisplay();
        }
      });
    }

    lastViewWidth = -1;
    layout();

    templateOnColumnHiddenUpdated(index, hidden, tw);
  }

  /**
   * Updates the column width to the given value, which should have previously
   * been stored in the column model.
   * 
   * @param col the column index
   * @param width the width of the column
   */
  protected void updateColumnWidth(int col, int width) {
    int tw = getTotalWidth();
    int w = getColumnWidth(col);

    header.updateTotalWidth(-1, tw);
    header.updateColumnWidth(col, width);

    if (footer != null) {
      footer.updateTotalWidth(getOffsetWidth(), tw);
      footer.updateColumnWidth(col, width);
    }

    int clen = cm.getColumnCount();
    int[] columnWidths = new int[clen];
    for (int i = 0; i < clen; i++) {
      columnWidths[i] = cm.isHidden(i) ? 0 : getColumnWidth(i);
    }
    adjustColumnWidths(columnWidths);

    templateOnColumnWidthUpdated(col, w, tw);
  }

  /**
   * Update the header to reflect any changes in the sort state.
   */
  protected void updateHeaderSortState() {
    if (!isRemoteSort()) {
      StoreSortInfo<M> info = getSortState();
      if (info != null) {
        ValueProvider<? super M, ?> vp = info.getValueProvider();
        if (vp != null) {
          String p = vp.getPath();
          if (p != null && !"".equals(p)) {
            ColumnConfig<M, ?> config = cm.findColumnConfig(p);
            if (config != null) {
              if (storeSortInfo == null || (!p.equals(storeSortInfo.getPath()))
                  || storeSortInfo.getDirection() != info.getDirection()) {
                int index = cm.indexOf(config);
                if (index != -1) {
                  updateSortIcon(index, info.getDirection());
                }
                grid.fireEvent(new SortChangeEvent(new SortInfoBean(info.getPath(), info.getDirection())));
              }

            }
          }
          storeSortInfo = info;
        }

      }
    } else {
      List<? extends SortInfo> infos = grid.getLoader().getSortInfo();
      if (infos.size() > 0) {
        SortInfo info = infos.get(0);
        String p = info.getSortField();
        if (p != null && !"".equals(p)) {
          ColumnConfig<M, ?> config = cm.findColumnConfig(p);
          if (config != null) {
            if (sortState == null || (!sortState.getSortField().equals(p))
                || sortState.getSortDir() != info.getSortDir()) {
              int index = cm.indexOf(config);
              if (index != -1) {
                updateSortIcon(index, info.getSortDir());
              }
              grid.fireEvent(new SortChangeEvent(info));
            }
          }
          sortState = info;
        }
      }
    }
  }

  /**
   * Updates the sort icon for the given column and sort direction.
   * 
   * @param colIndex the column index
   * @param dir the sort direction
   */
  protected void updateSortIcon(int colIndex, SortDir dir) {
    header.updateSortIcon(colIndex, dir);
  }

  private void constrainFocusElement() {
    int scrollLeft = scroller.getScrollLeft();
    int scrollTop = scroller.getScrollTop();
    int left = scroller.getWidth(true) / 2 + scrollLeft;
    int top = scroller.getHeight(true) / 2 + scrollTop;
    focusEl.setLeftTop(left, top);
  }

  private NodeList<Element> getTableHeads(Element table) {
    // tbody
    table = table.getFirstChildElement();
    if (table == null) return null;

    // tr
    table = table.getFirstChildElement();
    if (table == null) return null;

    return table.getChildNodes().cast();
  }

  private void refreshFooterData() {
    if (footer != null) {
      footer.refresh();
    }
  }

  private void restrictMenu(Menu columns) {
    int count = 0;
    for (int i = 0, len = cm.getColumnCount(); i < len; i++) {
      if (!shouldNotCount(i, true)) {
        count++;
      }
    }

    if (count == 1) {
      for (Widget item : columns) {
        CheckMenuItem ci = (CheckMenuItem) item;
        if (ci.isChecked()) {
          ci.disable();
        }
      }
    } else {
      for (int i = 0, len = columns.getWidgetCount(); i < len; i++) {
        Widget item = columns.getWidget(i);
        ColumnConfig<M, ?> config = cm.getColumn(i);
        if (item instanceof Component && config.isHideable()) {
          ((Component) item).enable();
        }
      }
    }
  }

  private boolean shouldNotCount(int columnIndex, boolean includeHidden) {
    return cm.getColumnHeader(columnIndex) == null || cm.getColumnHeader(columnIndex).asString().equals("")
        || (includeHidden && cm.isHidden(columnIndex)) || cm.isFixed(columnIndex);
  }

}
