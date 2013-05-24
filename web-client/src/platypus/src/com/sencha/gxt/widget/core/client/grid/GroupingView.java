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
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent.CollapseItemHandler;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent.HasCollapseItemHandlers;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent.ExpandItemHandler;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent.HasExpandItemHandlers;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem;

/**
 * <code>GridView</code> that groups data based on a given grouping column.
 * 
 * @param <M> the model type
 */
public class GroupingView<M> extends GridView<M> implements HasCollapseItemHandlers<List<M>>,
    HasExpandItemHandlers<List<M>> {

  public static class GroupingData<M> {
    private final Object value;
    private final int startRow;
    private final List<M> items = new ArrayList<M>();
    private boolean collapsed;

    public GroupingData(Object value, int startRow) {
      this.value = value;
      this.startRow = startRow;
    }

    @Override
    public boolean equals(Object other) {
      if (other instanceof GroupingData) {
        return Util.equalWithNull(((GroupingData<?>) other).value, value);
      }
      return false;
    }

    public List<M> getItems() {
      return items;
    }

    public int getStartRow() {
      return startRow;
    }

    public Object getValue() {
      return value;
    }

    @Override
    public int hashCode() {
      return value == null ? 0 : value.hashCode();
    }

    public boolean isCollapsed() {
      return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
      this.collapsed = collapsed;
    }

  }

  public interface GroupingViewAppearance {

    XElement findHead(XElement element);

    XElement getGroup(XElement head);

    ImageResource groupByIcon();

    boolean isCollapsed(XElement group);

    void onGroupExpand(XElement group, boolean expanded);

    SafeHtml renderGroupHeader(GroupingData<?> groupInfo);

    GroupingViewStyle style();
  }

  public interface GroupingViewStyle extends CssResource, GridDataTableStyles {

    String gridGroup();

    String gridGroupBody();

    String gridGroupCollapsed();

    String gridGroupHead();

    String gridGroupHeadInner();
  }

  public interface GroupSummaryTemplate<M> {
    SafeHtml renderGroupSummary(GroupingData<M> groupInfo);
  }

  protected ColumnConfig<M, ?> lastGroupField;
  protected ColumnConfig<M, ?> groupingColumn;

  protected boolean enableGrouping;

  private GroupSummaryTemplate<M> groupSummaryTemplate;
  private final GroupingViewAppearance groupAppearance;

  private boolean enableGroupingMenu = true;
  private boolean enableNoGroups = true;
  private boolean showGroupedColumn = true;
  private boolean startCollapsed;

  private boolean isUpdating = false;
  private StoreSortInfo<M> lastStoreSort;
  private SortInfo lastSort;
  private HandlerManager handlerManager = new HandlerManager(this);

  /**
   * Creates a new grouping view instance.
   */
  public GroupingView() {
    this(GWT.<GridAppearance> create(GridAppearance.class), GWT.<GroupingViewAppearance> create(GroupingViewAppearance.class));
  }

  /**
   * Creates a new grouping view instance.
   * 
   * @param appearance the grid appearance
   * @param groupingAppearance the grouping appearance
   */
  public GroupingView(GridAppearance appearance, GroupingViewAppearance groupingAppearance) {
    super(appearance);
    this.groupAppearance = groupingAppearance;
  }

  /**
   * Creates a new grouping view instance.
   * 
   * @param groupAppearance the group appearance
   */
  public GroupingView(GroupingViewAppearance groupAppearance) {
    this(GWT.<GridAppearance> create(GridAppearance.class), groupAppearance);
  }

  @Override
  public HandlerRegistration addCollapseHandler(CollapseItemHandler<List<M>> handler) {
    return handlerManager.addHandler(CollapseItemEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addExpandHandler(ExpandItemHandler<List<M>> handler) {
    return handlerManager.addHandler(ExpandItemEvent.getType(), handler);
  }

  @Override
  protected void adjustColumnWidths(int[] columnWidths) {
    super.adjustColumnWidths(columnWidths);
  }

  @Override
  protected void afterRender() {
    ColumnConfig<M, ?> column = groupingColumn;

    // set groupingColumn to null to force regrouping only if grouping
    // hasn't already occurred
    if (lastStoreSort == null && lastSort == null && column != null) {
      groupingColumn = null;
    }
    groupBy(column);
    super.afterRender();
  }

  /**
   * Collapses all groups.
   */
  public void collapseAllGroups() {
    toggleAllGroups(false);
  }

  protected Menu createContextMenu(final int colIndex) {
    Menu menu = super.createContextMenu(colIndex);

    if (menu != null && enableGroupingMenu) {
      if (cm.isGroupable(colIndex)) {
        MenuItem groupBy = new MenuItem(DefaultMessages.getMessages().groupingView_groupByText());
        groupBy.setIcon(groupAppearance.groupByIcon());
        groupBy.addSelectionHandler(new SelectionHandler<Item>() {
          @Override
          public void onSelection(SelectionEvent<Item> event) {
            groupBy(cm.getColumn(colIndex));
          }
        });
        menu.add(new SeparatorMenuItem());
        menu.add(groupBy);
      }
      if (enableGrouping && enableNoGroups) {
        final CheckMenuItem showInGroups = new CheckMenuItem(
            DefaultMessages.getMessages().groupingView_showGroupsText());
        showInGroups.setChecked(true);
        showInGroups.addSelectionHandler(new SelectionHandler<Item>() {
          @Override
          public void onSelection(SelectionEvent<Item> event) {
            if (showInGroups.isChecked()) {
              groupBy(cm.getColumn(colIndex));
            } else {
              groupBy(null);
            }
          }
        });
        menu.add(showInGroups);
      }
    }

    return menu;
  }

  @Override
  protected SafeHtml doRender(List<ColumnData> cs, List<M> rows, int startRow) {
    enableGrouping = groupingColumn != null;

    if (!enableGrouping || isUpdating) {
      return super.doRender(cs, rows, startRow);
    }

    GroupingData<M> curGroup = null;

    List<GroupingData<M>> groups = new ArrayList<GroupingData<M>>();

    // iterate through each item, creating a new group as needed. Assumes the
    // list is sorted
    for (int j = 0; j < rows.size(); j++) {
      M model = rows.get(j);

      int rowIndex = (j + startRow);

      // the value for the group field
      Object gvalue = groupingColumn.getValueProvider().getValue(model);

      if (curGroup == null || !Util.equalWithNull(curGroup.getValue(), gvalue)) {
        curGroup = new GroupingData<M>(gvalue, rowIndex);
        curGroup.setCollapsed(startCollapsed);
        curGroup.getItems().add(model);
        assert !groups.contains(curGroup);
        groups.add(curGroup);

      } else {
        curGroup.getItems().add(model);
      }
    }

    // TODO support a grouping renderer, perhaps in the
    // for (GroupingData<M> group : groups) {
    // if (groupRenderer != null) {
    // String g = groupRenderer.render(group);
    // if (g == null || g.equals("")) {
    // g = "&nbsp;";
    // }
    // group.group = g;
    // }
    // }

    SafeHtmlBuilder buf = new SafeHtmlBuilder();

    String styles = "width:" + getTotalWidth() + "px;";
    SafeStyles tableStyles = SafeStylesUtils.fromTrustedString(styles);

    for (int i = 0, len = groups.size(); i < len; i++) {
      GroupingData<M> g = groups.get(i);
      SafeHtml renderedRows = tpls.table(getAppearance().styles().dataTable(), tableStyles,
          super.doRender(cs, g.getItems(), g.getStartRow()), renderHiddenHeaders(getColumnWidths()));
      renderGroup(buf, g, renderedRows);
    }

    return buf.toSafeHtml();
  }

  @Override
  protected void doSort(int colIndex, SortDir sortDir) {
    ColumnConfig<M, ?> column = cm.getColumn(colIndex);
    if (groupingColumn != null) {
      if (grid.getLoader() == null || !grid.getLoader().isRemoteSort()) {
        // first sort is lastStoreSort
        assert lastStoreSort != null;
        ds.getSortInfo().clear();

        StoreSortInfo<M> sortInfo = createStoreSortInfo(column, sortDir);

        if (sortDir == null && storeSortInfo != null && storeSortInfo.getValueProvider() == column.getValueProvider()) {
          sortInfo.setDirection(storeSortInfo.getDirection() == SortDir.ASC ? SortDir.DESC : SortDir.ASC);
        } else if (sortDir == null) {
          sortInfo.setDirection(SortDir.ASC);
        }

        ds.getSortInfo().add(0, lastStoreSort);
        ds.getSortInfo().add(1, sortInfo);

        if (GWT.isProdMode()) {
          ds.applySort(false);
        } else {
          try {
            // applySort will apply its sort when called, which might trigger an
            // exception if the column passed in's data isn't Comparable
            ds.applySort(false);
          } catch (ClassCastException ex) {
            GWT.log("Column can't be sorted " + column.getValueProvider().getPath() + " is not Comparable. ", ex);
            throw ex;
          }
        }
      } else {
        assert lastSort != null;
        ValueProvider<? super M, ?> vp = column.getValueProvider();
        grid.getLoader().clearSortInfo();
        grid.getLoader().addSortInfo(0, lastSort);
        grid.getLoader().addSortInfo(1, new SortInfoBean(vp, sortDir));
        grid.getLoader().load();
      }

    } else {
      super.doSort(colIndex, sortDir);
    }
  }

  /**
   * Expands all groups.
   */
  public void expandAllGroups() {
    toggleAllGroups(true);
  }

  protected List<GroupingData<M>> getGroupData() {
    List<GroupingData<M>> groups = new ArrayList<GroupingData<M>>();

    GroupingData<M> curGroup = null;
    for (int i = 0, len = ds.size(); i < len; i++) {
      M m = ds.get(i);
      Object gvalue = groupingColumn.getValueProvider().getValue(m);
      if (curGroup == null || !Util.equalWithNull(curGroup.getValue(), gvalue)) {
        curGroup = new GroupingData<M>(gvalue, i);
        curGroup.setCollapsed(true);
        curGroup.getItems().add(m);
        assert !groups.contains(curGroup);
        groups.add(curGroup);
      } else {
        curGroup.getItems().add(m);
      }
    }

    return groups;
  }

  /**
   * Returns the grouping view appearance.
   * 
   * @return the grouping appearance
   */
  public GroupingViewAppearance getGroupingAppearance() {
    return groupAppearance;
  }

  protected NodeList<Element> getGroups() {
    if (!enableGrouping) {
      return JsArray.createArray().cast();
    }
    return dataTable.<XElement> cast().select("." + groupAppearance.style().gridGroup());
  }

  @Override
  protected NodeList<Element> getRows() {
    if (!enableGrouping || !hasRows()) {
      return super.getRows();
    }
    return dataTable.<XElement> cast().select("." + styles.row());
  }

  @Override
  protected StoreSortInfo<M> getSortState() {
    if (groupingColumn != null) {
      if (ds.getSortInfo().size() > 1) {
        return ds.getSortInfo().get(1);
      }
    }
    return super.getSortState();
  }

  public void groupBy(ColumnConfig<M, ?> column) {
    if (grid == null) {
      // if still being configured, save the grouping column for later
      groupingColumn = column;
    }
    if (column != groupingColumn) {
      // remove the existing group, if any
      if (groupingColumn != null) {
        if (grid.getLoader() == null || !grid.getLoader().isRemoteSort()) {
          assert lastStoreSort != null && ds.getSortInfo().contains(lastStoreSort);
          // remove the lastStoreSort from the listStore
          ds.getSortInfo().remove(lastStoreSort);
        } else {
          assert lastSort != null;
          grid.getLoader().removeSortInfo(lastSort);
        }
      } else {// groupingColumn == null;
        assert lastStoreSort == null && lastSort == null;
      }

      // set the new one
      groupingColumn = column;
      if (column != null) {
        if (grid.getLoader() == null || !grid.getLoader().isRemoteSort()) {
          lastStoreSort = createStoreSortInfo(column, SortDir.ASC);
          ds.addSortInfo(0, lastStoreSort);// this triggers the sort
        } else {
          lastSort = new SortInfoBean(column.getValueProvider(), SortDir.ASC);
          grid.getLoader().addSortInfo(0, lastSort);
          grid.getLoader().load();
        }
      } else {// new column == null
        lastStoreSort = null;
        lastSort = null;
        // full redraw without groups
        refresh(false);
      }
    }
  }

  /**
   * Returns true if the user can turn off grouping.
   * 
   * @return the enable no groups state
   */
  public boolean isEnabledNoGroups() {
    return enableNoGroups;
  }

  /**
   * Returns true if the grouping menu is enabled.
   * 
   * @return the enable grouping state
   */
  public boolean isEnableGroupingMenu() {
    return enableGroupingMenu;
  }

  /**
   * Returns true if the group is expanded.
   * 
   * @param group the group
   * @return true if expanded
   */
  public boolean isExpanded(Element group) {
    return groupAppearance.isCollapsed(group.<XElement> cast());
  }

  /**
   * Returns true if the grouped column is visible.
   * 
   * @return the show grouped column
   */
  public boolean isShowGroupedColumn() {
    return showGroupedColumn;
  }

  /**
   * Returns true if start collapsed is enabled.
   * 
   * @return the start collapsed state
   */
  public boolean isStartCollapsed() {
    return startCollapsed;
  }

  @Override
  protected void onAdd(List<M> models, int index) {
    if (enableGrouping) {
      Point ss = getScrollState();
      refresh(false);
      restoreScroll(ss);
    } else {
      super.onAdd(models, index);
    }
  }

  @Override
  protected void onMouseDown(Event ge) {
    super.onMouseDown(ge);
    XElement head = ge.getEventTarget().cast();
    head = groupAppearance.findHead(head);
    if (head != null) {
      ge.stopPropagation();
      XElement group = groupAppearance.getGroup(head);
      toggleGroup(group, groupAppearance.isCollapsed(group));
    }
  }

  @Override
  protected void onRemove(M m, int index, boolean isUpdate) {
    Element parentToRemove = null;
    if (enableGrouping) {
      Element row = getRow(index).cast();

      // TODO appearance this
      Element groupContainer = row.getParentElement().cast();
      if (groupContainer.getChildCount() == 1) {
        parentToRemove = groupContainer.getParentElement().cast();
      }
    }
    super.onRemove(m, index, isUpdate);
    if (parentToRemove != null) {
      parentToRemove.removeFromParent();
    }
  }

  @Override
  protected void refreshRow(int row) {
    isUpdating = true;
    super.refreshRow(row);
    isUpdating = false;
  }

  protected void renderGroup(SafeHtmlBuilder buf, GroupingData<M> g, SafeHtml renderedRows) {
    String groupClass = groupAppearance.style().gridGroup();
    String headClass = groupAppearance.style().gridGroupHead();
    buf.append(tpls.tr(groupClass,
        tpls.tdWrap(cm.getColumnCount(), headClass + " " + styles.cell(), styles.cellInner(), renderGroupHeader(g))));
    buf.append(tpls.tr("", tpls.tdWrap(cm.getColumnCount(), "", "", renderedRows)));
  }

  protected SafeHtml renderGroupHeader(GroupingData<M> groupInfo) {
    return groupAppearance.renderGroupHeader(groupInfo);
  }

  protected SafeHtml renderGroupSummary(GroupingData<M> groupInfo) {
    if (groupSummaryTemplate != null) {
      return groupSummaryTemplate.renderGroupSummary(groupInfo);
    } else {
      return null;
    }
  }

  @Override
  protected SafeHtml renderRows(int startRow, int endRow) {
    boolean eg = groupingColumn != null;
    if (!showGroupedColumn) {
      int colIndex = cm.indexOf(groupingColumn);
      if (!eg && lastGroupField != null) {
        dataTableBody.removeChildren();
        cm.setHidden(cm.indexOf(lastGroupField), false);
        cm.getColumn(cm.indexOf(lastGroupField)).setHideable(true);
        lastGroupField = groupingColumn;
      } else if (eg && (lastGroupField == null || lastGroupField == groupingColumn)) {
        lastGroupField = groupingColumn;
        cm.setHidden(colIndex, true);
        cm.getColumn(colIndex).setHideable(false);
      } else if (eg && lastGroupField != null && !groupingColumn.equals(lastGroupField)) {
        dataTableBody.removeChildren();
        int oldIndex = cm.indexOf(lastGroupField);
        cm.setHidden(oldIndex, false);
        cm.getColumn(oldIndex).setHideable(true);
        lastGroupField = groupingColumn;
        cm.setHidden(colIndex, true);
        cm.getColumn(colIndex).setHideable(false);
      }
    }
    return super.renderRows(startRow, endRow);
  }

  /**
   * True to enable the the grouping menu items in the header context menu
   * (defaults to true).
   * 
   * @param enableGroupingMenu true to enable the grouping menu items
   */
  public void setEnableGroupingMenu(boolean enableGroupingMenu) {
    this.enableGroupingMenu = enableGroupingMenu;
  }

  /**
   * True to enable the no groups menu item in the header context menu (defaults
   * to true).
   * 
   * @param enableNoGroups true to enable no groups menu item
   */
  public void setEnableNoGroups(boolean enableNoGroups) {
    this.enableNoGroups = enableNoGroups;
  }

  /**
   * Sets whether the grouped column is visible (defaults to true).
   * 
   * @param showGroupedColumn true to show the grouped column
   */
  public void setShowGroupedColumn(boolean showGroupedColumn) {
    this.showGroupedColumn = showGroupedColumn;
  }

  /**
   * Sets whether the groups should start collapsed (defaults to false).
   * 
   * @param startCollapsed true to start collapsed
   */
  public void setStartCollapsed(boolean startCollapsed) {
    this.startCollapsed = startCollapsed;
  }

  /**
   * Toggles all groups.
   * 
   * @param expanded true to expand
   */
  public void toggleAllGroups(boolean expanded) {
    NodeList<Element> groups = getGroups();
    for (int i = 0; i < groups.getLength(); i++) {
      toggleGroup(groups.getItem(i), expanded);
    }
  }

  protected void toggleGroup(Element group, boolean expanded) {
    assert group != null;
    groupAppearance.onGroupExpand(group.<XElement> cast(), expanded);
    calculateVBar(false);
    if (expanded) {
      handlerManager.fireEvent(new ExpandItemEvent<List<M>>(null));
    } else {
      handlerManager.fireEvent(new CollapseItemEvent<List<M>>(null));
    }
  }
}
