/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.Shared;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.dom.DomQuery;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Region;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.dnd.core.client.StatusProxy;
import com.sencha.gxt.fx.client.DragCancelEvent;
import com.sencha.gxt.fx.client.DragEndEvent;
import com.sencha.gxt.fx.client.DragHandler;
import com.sencha.gxt.fx.client.DragMoveEvent;
import com.sencha.gxt.fx.client.DragStartEvent;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.event.HeaderClickEvent;
import com.sencha.gxt.widget.core.client.event.HeaderContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.HeaderDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.HeaderMouseDownEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

/**
 * A column header component.
 */
public class ColumnHeader<M> extends Component {

  @Shared
  public interface ColumnHeaderStyles extends CssResource {

    String header();

    String headerInner();

    String head();

    String headInner();

    String headMenuOpen();

    String sortIcon();

    String sortAsc();

    String sortDesc();

    String columnMoveTop();

    String columnMoveBottom();

    String headRow();

    String headOver();

    String headButton();
  }

  public interface ColumnHeaderAppearance {

    /**
     * Returns the icon to use for the "Columns" (column selection) header menu
     * item.
     * 
     * @return the columns menu icon
     */
    ImageResource columnsIcon();

    String columnsWrapSelector();

    void render(SafeHtmlBuilder sb);

    /**
     * Returns the icon to use for the "Sort Ascending" header menu item.
     * 
     * @return the sort ascending menu icon
     */
    ImageResource sortAscendingIcon();

    /**
     * Returns the icon to use for the "Sort Descending" header menu item.
     * 
     * @return the sort descending menu icon
     */
    ImageResource sortDescendingIcon();

    ColumnHeaderStyles styles();
  }

  public class GridSplitBar extends Component {

    protected int colIndex;
    protected Draggable d;
    protected boolean dragging;
    protected DragHandler listener = new DragHandler() {

      @Override
      public void onDragCancel(DragCancelEvent event) {
      }

      @Override
      public void onDragEnd(DragEndEvent event) {
        GridSplitBar.this.onDragEnd(event);

      }

      @Override
      public void onDragMove(DragMoveEvent event) {
      }

      @Override
      public void onDragStart(DragStartEvent event) {
        GridSplitBar.this.onDragStart(event);

      }

    };

    protected int startX;

    public GridSplitBar() {
      setElement(DOM.createDiv());
      if (GXT.isOpera()) {
        getElement().getStyle().setProperty("cursor", "w-resize");
      } else {
        getElement().getStyle().setProperty("cursor", "col-resize");
      }
      getElement().makePositionable(true);
      setWidth(5);

      getElement().setVisibility(false);
      getElement().getStyle().setProperty("backgroundColor", "white");
      getElement().setOpacity(0);

      d = new Draggable(this);
      d.setUseProxy(false);
      d.setConstrainVertical(true);
      d.setStartDragDistance(0);
      d.addDragHandler(listener);
    }

    protected void onDragEnd(DragEndEvent e) {
      dragging = false;
      headerDisabled = false;
      getElement().getStyle().setProperty("borderLeft", "none");
      getElement().setOpacity(0);
      getElement().setWidth(splitterWidth);
      bar.getElement().setVisibility(false);

      int endX = e.getX();
      int diff = endX - startX;
      onColumnSplitterMoved(colIndex, cm.getColumnWidth(colIndex) + diff);
    }

    protected void onDragStart(DragStartEvent e) {
      headerDisabled = true;
      dragging = true;
      getElement().getStyle().setProperty("borderLeft", "1px solid black");
      getElement().getStyle().setCursor(Cursor.DEFAULT);
      getElement().setOpacity(1);
      getElement().setWidth(1);

      startX = e.getX();

      int cols = cm.getColumnCount();
      for (int i = 0, len = cols; i < len; i++) {
        if (cm.isHidden(i) || !cm.isResizable(i)) continue;
        Element hd = getHead(i).getElement();
        if (hd != null) {
          Region rr = XElement.as(hd).getRegion();
          if (startX > rr.getRight() - 5 && startX < rr.getRight() + 5) {
            colIndex = heads.indexOf(getHead(i));
            if (colIndex != -1) break;
          }
        }
      }
      if (colIndex > -1) {
        Element c = getHead(colIndex).getElement();
        int x = startX;
        int minx = x - XElement.as(c).getX() - minColumnWidth;
        int maxx = (XElement.as(container.getElement()).getX() + XElement.as(container.getElement()).getWidth(false))
            - e.getNativeEvent().getClientX();
        d.setXConstraint(minx, maxx);
      }
    }

    protected void onMouseMove(Head header, Event event) {
      int activeHdIndex = heads.indexOf(header);

      if (dragging || !header.config.isResizable()) {
        return;
      }

      // find the previous column which is not hidden
      int before = -1;
      for (int i = activeHdIndex - 1; i >= 0; i--) {
        if (!cm.isHidden(i)) {
          before = i;
          break;
        }
      }
      int x = event.getClientX();
      Region r = header.getElement().getRegion();
      int hw = splitterWidth;

      getElement().setY(XElement.as(container.getElement()).getY());
      getElement().setHeight(container.getOffsetHeight());

      Style ss = getElement().getStyle();

      if (x - r.getLeft() <= hw && before != -1 && cm.isResizable(before) && !cm.isFixed(before)) {
        bar.getElement().setVisibility(true);
        getElement().setX(r.getLeft() - (hw / 2));
        ss.setProperty("cursor", GXT.isSafari() ? "e-resize" : "col-resize");
      } else if (r.getRight() - x <= hw && cm.isResizable(activeHdIndex) && !cm.isFixed(activeHdIndex)) {
        bar.getElement().setVisibility(true);
        getElement().setX(r.getRight() - (hw / 2));
        ss.setProperty("cursor", GXT.isSafari() ? "w-resize" : "col-resize");
      } else {
        bar.getElement().setVisibility(false);
        ss.setProperty("cursor", "");
      }
    }
  }

  public class Group extends Component {

    private HeaderGroupConfig config;

    public Group(HeaderGroupConfig config) {
      this.config = config;
      groups.add(this);

      setElement(DOM.createDiv());
      setStyleName(styles.headInner());

      if (config.getWidget() != null) {
        getElement().appendChild(config.getWidget().getElement());
      } else {
        getElement().setInnerHTML(
            config.getHtml() != null ? config.getHtml().asString() : SafeHtmlUtils.fromString("").asString());
      }
    }

    @Override
    protected void doAttachChildren() {
      ComponentHelper.doAttach(config.getWidget());
    }

    @Override
    protected void doDetachChildren() {
      ComponentHelper.doDetach(config.getWidget());
    }

    public void setText(String text) {
      getElement().setInnerHTML(text);
    }
  }

  public class Head extends Component {

    protected int column;
    protected ColumnConfig<M, ?> config;

    private AnchorElement btn;
    private ImageElement img;
    private InlineHTML text;
    private Widget widget;
    private int row;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Head(ColumnConfig column) {
      this.config = column;
      this.column = cm.indexOf(column);

      setElement(DOM.createDiv());

      btn = Document.get().createAnchorElement();
      btn.setHref("#");
      btn.setClassName(styles.headButton());

      img = Document.get().createImageElement();
      img.setSrc(GXT.getBlankImageUrl());
      img.setClassName(styles.sortIcon());

      getElement().appendChild(btn);

      if (config.getWidget() != null) {
        Element span = Document.get().createSpanElement().cast();
        widget = config.getWidget();
        span.appendChild(widget.getElement());
        getElement().appendChild(span);
      } else {
        text = new InlineHTML(config.getHeader() != null ? config.getHeader() : SafeHtmlUtils.fromString(""));
        getElement().appendChild(text.getElement());
      }

      getElement().appendChild(img);

      SafeHtml tip = config.getToolTip();
      if (tip != null) {
        getElement().setAttribute("qtip", tip.asString());
      }

      sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.FOCUSEVENTS | Event.ONKEYPRESS);

      String s = config.getColumnClassSuffix() == null ? "" : " x-grid-hd-" + config.getColumnClassSuffix();
      addStyleName(styles.headInner() + s);
      if (column.getColumnHeaderClassName() != null) {
        addStyleName(column.getColumnHeaderClassName());
      }
      heads.add(this);

    }

    protected void activate() {
      if (!cm.isMenuDisabled(indexOf(this))) {
        XElement td = getElement().findParent("td", 3);
        int h = td.getHeight(true);
        getElement().setHeight(h, true);
        if (btn != null) {
          XElement.as(btn).setHeight(h, true);
        }
        td.addClassName(styles.headOver());
      }
    }

    public void activateTrigger(boolean activate) {
      XElement e = getElement().findParent("td", 3);
      if (e != null) {
        if (activate) {
          e.addClassName(styles.headMenuOpen());
        } else {
          e.removeClassName(styles.headMenuOpen());
        }
      }
    }

    protected void deactivate() {
      getElement().findParent("td", 3).removeClassName(styles.headOver());
    }

    @Override
    protected void doAttachChildren() {
      super.doAttachChildren();
      ComponentHelper.doAttach(widget);
    }

    @Override
    protected void doDetachChildren() {
      super.doDetachChildren();
      ComponentHelper.doDetach(widget);
    }

    public Element getTrigger() {
      return (Element) btn.cast();
    }

    @Override
    public void onBrowserEvent(Event ce) {
      super.onBrowserEvent(ce);

      int type = ce.getTypeInt();
      switch (type) {
        case Event.ONMOUSEOVER:
          onMouseOver(ce);
          break;
        case Event.ONMOUSEOUT:
          onMouseOut(ce);
          break;
        case Event.ONMOUSEMOVE:
          onMouseMove(ce);
          break;
        case Event.ONMOUSEDOWN:
          onHeaderMouseDown(ce, column);
          break;
        case Event.ONCLICK:
          onClick(ce);
          break;
        case Event.ONDBLCLICK:
          onDoubleClick(ce);
          break;
      }
    }

    private void onClick(Event ce) {
      ce.preventDefault();
      if (ce.getEventTarget().<Element> cast() == (Element) btn.cast()) {
        onDropDownClick(ce, column);
      } else {
        onHeaderClick(ce, column);
      }
    }

    private void onDoubleClick(Event ce) {
      onHeaderDoubleClick(ce, column);
    }

    private void onMouseMove(Event ce) {
      if (bar != null) bar.onMouseMove(this, ce);
    }

    private void onMouseOut(Event ce) {
      deactivate();
    }

    private void onMouseOver(Event ce) {
      if (headerDisabled) {
        return;
      }
      activate();
    }

    public void setHeader(SafeHtml header) {
      if (text != null) text.setHTML(header);
    }

    public void updateWidth(int width) {
      if (!config.isHidden()) {
        XElement td = getElement().findParent("td", 3);
        td.setWidth(width, true);
        getElement().setWidth(width - td.getFrameWidth(Side.LEFT, Side.RIGHT), true);
      }
    }
  }

  protected GridSplitBar bar;
  protected ColumnModel<M> cm;
  protected Widget container;
  protected List<Group> groups = new ArrayList<Group>();
  protected boolean headerDisabled;
  protected List<Head> heads = new ArrayList<Head>();
  protected Menu menu;
  protected int minColumnWidth = 10;
  protected Draggable reorderer;
  protected int rows;
  protected int splitterWidth = 5;
  protected FlexTable table = new FlexTable();

  private QuickTip quickTip;
  private boolean enableColumnReorder;
  private final ColumnHeaderAppearance appearance;
  private ColumnHeaderStyles styles;

  /**
   * Creates a new column header.
   * 
   * @param container the containing widget
   * @param cm the column model
   */
  public ColumnHeader(Widget container, ColumnModel<M> cm) {
    this(container, cm, GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class));
  }

  /**
   * Creates a new column header.
   * 
   * @param container the containing widget
   * @param cm the column model
   * @param appearance the column header appearance
   */
  public ColumnHeader(Widget container, ColumnModel<M> cm, ColumnHeaderAppearance appearance) {
    this.container = container;
    this.cm = cm;
    this.appearance = appearance;
    setAllowTextSelection(false);

    styles = appearance.styles();

    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    this.appearance.render(builder);

    setElement(XDOM.create(builder.toSafeHtml()));

    table.setCellPadding(0);
    table.setCellSpacing(0);

    getElement().selectNode(appearance.columnsWrapSelector()).appendChild(table.getElement());

    List<HeaderGroupConfig> configs = cm.getHeaderGroups();
    rows = 0;
    for (HeaderGroupConfig config : configs) {
      rows = Math.max(rows, config.getRow() + 1);
    }
    rows++;

    quickTip = new QuickTip(this);
  }

  private void adjustWidths() {
    if (rows == 1) {
      return;
    }
    cleanCells();
    for (int i = 0; i < rows; i++) {
      int columns = table.getCellCount(i);
      int mark = 0;
      for (int j = 0; j < columns; j++) {
        TableCellElement cell = table.getCellFormatter().getElement(i, j).cast();
        int colspan = cell.getColSpan();
        int w = 0;
        for (int k = mark; k < (mark + colspan); k++) {
          ColumnConfig<M, ?> c = cm.getColumn(k);
          if (c.isHidden()) {
            continue;
          }
          w += cm.getColumnWidth(k);
        }
        mark += colspan;

        cell.getStyle().setPropertyPx("width", w);
        int adj = cell.<XElement> cast().getFrameWidth(Side.LEFT, Side.RIGHT);
        XElement inner = cell.getFirstChildElement().cast();
        inner.setWidth(w - adj, true);
      }
    }
  }

  protected void cleanCells() {
    NodeList<Element> tds = DomQuery.select("tr." + styles.headRow() + " > td", table.getElement());
    for (int i = 0; i < tds.getLength(); i++) {
      Element td = tds.getItem(i);
      if (!td.hasChildNodes()) {
        XElement.as(td).removeFromParent();
      }
    }
  }

  protected Group createNewGroup(HeaderGroupConfig config) {
    return new Group(config);
  }

  @SuppressWarnings("rawtypes")
  protected Head createNewHead(ColumnConfig config) {
    return new Head(config);
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    ComponentHelper.doAttach(table);
    ComponentHelper.doAttach(bar);
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    ComponentHelper.doDetach(table);
    ComponentHelper.doDetach(bar);
  }

  /**
   * Returns the column header appearance.
   * 
   * @return the column header appearance
   */
  public ColumnHeaderAppearance getAppearance() {
    return appearance;
  }

  protected int getColumnWidths(int start, int end) {
    int w = 0;
    for (int i = start; i < end; i++) {
      if (!cm.isHidden(i)) {
        w += cm.getColumnWidth(i);
      }
    }
    return w;
  }

  /**
   * Returns the header's container widget.
   * 
   * @return the container widget
   */
  public Widget getContainer() {
    return container;
  }

  protected Menu getContextMenu(int column) {
    return menu;
  }

  /**
   * Returns the head at the current index.
   * 
   * @param column the column index
   * @return the column or null if no match
   */
  public Head getHead(int column) {
    return (column > -1 && column < heads.size()) ? heads.get(column) : null;
  }

  /**
   * Returns the minimum column width.
   * 
   * @return the column width
   */
  public int getMinColumnWidth() {
    return minColumnWidth;
  }

  /**
   * Returns the splitter width.
   * 
   * @return the splitter width in pixels.
   */
  public int getSplitterWidth() {
    return splitterWidth;
  }

  /**
   * Returns the index of the given column head.
   * 
   * @param head the column head
   * @return the index
   */
  public int indexOf(Head head) {
    return heads.indexOf(head);
  }

  /**
   * Returns true if column reordering is enabled.
   * 
   * @return the column reorder state
   */
  public boolean isEnableColumnReorder() {
    return enableColumnReorder;
  }

  @Override
  protected void onAttach() {
    super.onAttach();
    refresh();
  }

  protected void onColumnSplitterMoved(int colIndex, int width) {

  }

  protected void onDropDownClick(Event ce, int column) {
    ce.stopPropagation();
    ce.preventDefault();
    showColumnMenu(column);
  }

  protected void onHeaderClick(Event event, int column) {
    container.fireEvent(new HeaderClickEvent(column, event, menu));
  }

  protected void onHeaderDoubleClick(Event event, int column) {
    container.fireEvent(new HeaderDoubleClickEvent(column, event, menu));
  }

  protected void onHeaderMouseDown(Event ce, int column) {
    container.fireEvent(new HeaderMouseDownEvent(column, ce, menu));
  }

  protected void onKeyDown(Event ce, int index) {

  }

  /**
   * Refreshes the columns.
   */
  public void refresh() {
    groups.clear();
    heads.clear();

    int cnt = table.getRowCount();
    for (int i = 0; i < cnt; i++) {
      table.removeRow(0);
    }

    table.setWidth(cm.getTotalWidth() + "px");

    List<HeaderGroupConfig> configs = cm.getHeaderGroups();

    FlexCellFormatter cf = table.getFlexCellFormatter();
    RowFormatter rf = table.getRowFormatter();

    rows = 0;
    for (HeaderGroupConfig config : configs) {
      rows = Math.max(rows, config.getRow() + 1);
    }
    rows += 1;

    for (int i = 0; i < rows; i++) {
      rf.setStyleName(i, styles.headRow());
    }

    int cols = cm.getColumnCount();

    String cellClass = styles.header() + " " + styles.head();

    for (HeaderGroupConfig config : cm.getHeaderGroups()) {
      int col = config.getColumn();
      int row = config.getRow();
      int rs = config.getRowspan();
      int cs = config.getColspan();

      Group group = createNewGroup(config);

      boolean hide = true;
      if (rows > 1) {
        for (int i = col; i < (col + cs); i++) {
          if (!cm.isHidden(i)) {
            hide = false;
          }
        }
      }
      if (hide) {
        continue;
      }

      table.setWidget(row, col, group);

      cf.setStyleName(row, col, cellClass);

      HorizontalAlignmentConstant align = config.getHorizontalAlignment();
      cf.setHorizontalAlignment(row, col, align);

      int ncs = cs;
      if (cs > 1) {
        for (int i = col; i < (col + cs); i++) {
          if (cm.isHidden(i)) {
            ncs -= 1;
          }
        }
      }

      cf.setRowSpan(row, col, rs);
      cf.setColSpan(row, col, ncs);
    }

    for (int i = 0; i < cols; i++) {
      Head h = createNewHead(cm.getColumn(i));
      if (cm.isHidden(i)) {
        continue;
      }
      int rowspan = 1;
      if (rows > 1) {
        for (int j = rows - 2; j >= 0; j--) {
          if (!cm.hasGroup(j, i)) {
            rowspan += 1;
          }
        }
      }

      int row;
      if (rowspan > 1) {
        row = (rows - 1) - (rowspan - 1);
      } else {
        row = rows - 1;
      }

      h.row = row;

      if (rowspan > 1) {
        table.setWidget(row, i, h);
        table.getFlexCellFormatter().setRowSpan(row, i, rowspan);
      } else {
        table.setWidget(row, i, h);
      }
      ColumnConfig<M, ?> cc = cm.getColumn(i);
      String s = cc.getColumnClassSuffix() == null ? "" : " x-grid-hd-" + cc.getColumnClassSuffix();
      cf.setStyleName(row, i, cellClass + s);
      cf.getElement(row, i).setPropertyInt("gridColumnIndex", i);

      HorizontalAlignmentConstant align = cm.getColumnAlignment(i);
      if (align != null) {
        table.getCellFormatter().setHorizontalAlignment(row, i, align);
        if (align == HasHorizontalAlignment.ALIGN_RIGHT) {
          table.getCellFormatter().getElement(row, i).getFirstChildElement().getStyle().setPropertyPx("paddingRight",
              16);
        }
      }
      updateColumnWidth(i, cm.getColumnWidth(i));
    }

    if (container instanceof Grid) {
      @SuppressWarnings("unchecked")
      Grid<M> grid = (Grid<M>) container;
      if (grid.getView().isRemoteSort()) {
        List<? extends SortInfo> sortInfos = grid.getLoader().getSortInfo();
        if (sortInfos.size() > 0) {
          SortInfo sortInfo = sortInfos.get(0);
          String sortField = sortInfo.getSortField();
          if (sortField != null && !"".equals(sortField)) {
            ColumnConfig<M, ?> column = cm.findColumnConfig(sortField);
            if (column != null) {
              int index = cm.indexOf(column);
              if (index != -1) {
                updateSortIcon(index, sortInfo.getSortDir());
              }
            }
          }
        }
      } else {
        List<StoreSortInfo<M>> sortInfos = grid.getStore().getSortInfo();
        if (sortInfos.size() > 0) {
          StoreSortInfo<M> sortInfo = sortInfos.get(0);
          if (sortInfo != null && sortInfo.getValueProvider() != null) {
            ColumnConfig<M, ?> column = grid.getColumnModel().findColumnConfig(sortInfo.getPath());
            if (column != null) {
              updateSortIcon(grid.getColumnModel().indexOf(column), sortInfo.getDirection());
            }
          }
        }
      }
    }

    adjustWidths();
  }

  /**
   * Do not call.
   */
  public void release() {
    ComponentHelper.doDetach(this);
    getElement().removeFromParent();
    if (bar != null) {
      bar.getElement().removeFromParent();
    }
  }

  /**
   * True to enable column reordering.
   * 
   * @param enable true to enable
   */
  public void setEnableColumnReorder(boolean enable) {
    this.enableColumnReorder = enable;

    if (enable && reorderer == null) {
      reorderer = new Draggable(this);
      reorderer.setUseProxy(true);
      reorderer.setSizeProxyToSource(false);
      reorderer.setProxy(StatusProxy.get().getElement());
      reorderer.setMoveAfterProxyDrag(false);

      reorderer.addDragHandler(new DragHandler() {
        private Head active;
        private int newIndex = -1;
        private Head start;
        private XElement statusIndicatorBottom;
        private XElement statusIndicatorTop;
        private StatusProxy statusProxy = StatusProxy.get();

        private Element adjustTargetElement(Element target) {
          return (Element) (target.getFirstChildElement() != null ? target.getFirstChildElement() : target);
        }

        private void afterDragEnd() {
          start = null;
          active = null;
          newIndex = -1;
          removeStatusIndicator();

          headerDisabled = false;

          if (bar != null) {
            bar.show();
          }

          quickTip.enable();
        }

        @SuppressWarnings("unchecked")
        private Head getHeadFromElement(Element element) {
          Widget head = ComponentHelper.getWidgetWithElement(element);
          Head h = null;
          if (head instanceof ColumnHeader.Head) {
            h = (Head) head;
          }
          return h;
        }

        @Override
        public void onDragCancel(DragCancelEvent event) {
          afterDragEnd();
        }

        @Override
        public void onDragEnd(DragEndEvent event) {
          if (statusProxy.getStatus()) {
            cm.moveColumn(start.column, newIndex);
          }
          afterDragEnd();
        }

        @Override
        public void onDragMove(DragMoveEvent event) {
          event.setX(event.getNativeEvent().getClientX() + 12 + XDOM.getBodyScrollLeft());
          event.setY(event.getNativeEvent().getClientY() + 12 + XDOM.getBodyScrollTop());

          Element target = event.getNativeEvent().getEventTarget().cast();

          Head h = getHeadFromElement(adjustTargetElement(target));

          if (h != null && !h.equals(start)) {
            HeaderGroupConfig g = cm.getGroup(h.row - 1, h.column);
            HeaderGroupConfig s = cm.getGroup(start.row - 1, start.column);
            if ((g == null && s == null) || (g != null && g.equals(s))) {
              active = h;
              boolean before = event.getNativeEvent().getClientX() < active.getAbsoluteLeft() + active.getOffsetWidth()
                  / 2;
              showStatusIndicator(true);

              if (before) {
                statusIndicatorTop.alignTo(active.getElement(), new AnchorAlignment(Anchor.BOTTOM, Anchor.TOP_LEFT),
                    new int[] {-1, 0});
                statusIndicatorBottom.alignTo(active.getElement(), new AnchorAlignment(Anchor.TOP, Anchor.BOTTOM_LEFT),
                    new int[] {-1, 0});
              } else {
                statusIndicatorTop.alignTo(active.getElement(), new AnchorAlignment(Anchor.BOTTOM, Anchor.TOP_RIGHT),
                    new int[] {1, 0});
                statusIndicatorBottom.alignTo(active.getElement(),
                    new AnchorAlignment(Anchor.TOP, Anchor.BOTTOM_RIGHT), new int[] {1, 0});
              }

              int i = active.column;
              if (!before) {
                i++;
              }

              int aIndex = i;

              if (start.column < active.column) {
                aIndex--;
              }
              newIndex = i;
              if (aIndex != start.column) {
                statusProxy.setStatus(true);
              } else {
                showStatusIndicator(false);
                statusProxy.setStatus(false);
              }
            } else {
              active = null;
              showStatusIndicator(false);
              statusProxy.setStatus(false);
            }

          } else {
            active = null;
            showStatusIndicator(false);
            statusProxy.setStatus(false);
          }
        }

        @Override
        public void onDragStart(DragStartEvent event) {
          Element target = event.getNativeEvent().getEventTarget().cast();

          Head h = getHeadFromElement(target);
          if (h != null && !h.config.isFixed()) {
            headerDisabled = true;
            quickTip.disable();
            if (bar != null) {
              bar.hide();
            }

            if (statusIndicatorBottom == null) {
              statusIndicatorBottom = XElement.createElement("div");
              statusIndicatorBottom.addClassName(styles.columnMoveBottom());
              statusIndicatorTop = XElement.createElement("div");
              statusIndicatorTop.addClassName(styles.columnMoveTop());
            }

            Document.get().getBody().appendChild(statusIndicatorTop);
            Document.get().getBody().appendChild(statusIndicatorBottom);

            start = h;
            statusProxy.setStatus(false);
            statusProxy.update(start.config.getHeader());
          } else {
            event.setCancelled(true);
          }
        }

        private void removeStatusIndicator() {
          if (statusIndicatorBottom != null) {
            statusIndicatorBottom.removeFromParent();
            statusIndicatorTop.removeFromParent();
          }
        }

        private void showStatusIndicator(boolean show) {
          if (statusIndicatorBottom != null) {
            statusIndicatorBottom.setVisibility(show);
            statusIndicatorTop.setVisibility(show);
          }
        }
      });
    }

    if (reorderer != null && !enable) {
      reorderer.release();
      reorderer = null;
    }
  }

  /**
   * True to enable column resizing.
   * 
   * @param enable true to enable, otherwise false
   */
  public void setEnableColumnResizing(boolean enable) {
    if (bar == null && enable) {
      bar = new GridSplitBar();
      container.getElement().appendChild(bar.getElement());
      if (isAttached()) {
        ComponentHelper.doAttach(bar);
      }
      bar.show();
    } else if (bar != null && !enable) {
      ComponentHelper.doDetach(bar);
      bar.getElement().removeFromParent();
      bar = null;
    }
  }

  /**
   * Sets the column's header text.
   * 
   * @param column the column index
   * @param header the header text
   */
  public void setHeader(int column, SafeHtml header) {
    getHead(column).setHeader(header);
  }

  /**
   * Sets the header's context menu.
   * 
   * @param menu the context menu
   */
  public void setMenu(Menu menu) {
    this.menu = menu;
  }

  /**
   * Sets the minimum column width.
   * 
   * @param minColumnWidth the minimum column width
   */
  public void setMinColumnWidth(int minColumnWidth) {
    this.minColumnWidth = minColumnWidth;
  }

  /**
   * Sets the splitter width.
   * 
   * @param splitterWidth the splitter width
   */
  public void setSplitterWidth(int splitterWidth) {
    this.splitterWidth = splitterWidth;
  }

  /**
   * Shows the column's header context menu.
   * 
   * @param column the column index
   */
  public void showColumnMenu(final int column) {
    menu = getContextMenu(column);

    HeaderContextMenuEvent e = new HeaderContextMenuEvent(column, menu);
    container.fireEvent(e);
    if (e.isCancelled()) {
      return;
    }

    if (menu != null) {
      final Head h = getHead(column);
      menu.setId(h.getId() + "-menu");
      h.activateTrigger(true);
      menu.addHideHandler(new HideHandler() {

        @Override
        public void onHide(HideEvent event) {
          h.activateTrigger(false);
          if (container instanceof Component) {
            ((Component) container).focus();
          }
        }
      });
      menu.show(h.getTrigger(), new AnchorAlignment(Anchor.TOP_LEFT, Anchor.BOTTOM_LEFT, true));
    }
  }

  /**
   * Updates the visibility of a column.
   * 
   * @param index the column index
   * @param hidden true to hide, otherwise false
   */
  public void updateColumnHidden(int index, boolean hidden) {
    refresh();
    cleanCells();
  }

  /**
   * Updates the column width.
   * 
   * @param column the column index
   * @param width the new width
   */
  public void updateColumnWidth(int column, int width) {
    Head h = getHead(column);
    if (h != null) {
      h.updateWidth(width);
    }
    adjustWidths();
  }

  /**
   * Updates the sort icon of a column.
   * 
   * @param colIndex the column index
   * @param dir the sort direction
   */
  public void updateSortIcon(int colIndex, SortDir dir) {
    String desc = styles.sortDesc();
    String asc = styles.sortAsc();
    for (int i = 0; i < heads.size(); i++) {
      Head h = heads.get(i);
      if (i == colIndex) {
        h.addStyleName(dir == SortDir.DESC ? desc : asc);
        h.removeStyleName(dir != SortDir.DESC ? desc : asc);
      } else {
        h.getElement().removeClassName(asc, desc);
      }
    }
  }

  /**
   * Updates the total width of the header.
   * 
   * @param offset the offset
   * @param width the new width
   */
  public void updateTotalWidth(int offset, int width) {
    if (offset != -1) table.getElement().getParentElement().getStyle().setWidth(++offset, Unit.PX);
    table.getElement().getStyle().setWidth(width, Unit.PX);
  }

}
