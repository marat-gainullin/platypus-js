/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.data.shared.ListStore;

public class GroupSummaryView<M> extends GroupingView<M> {

  /**
   * Returns the summary node element.
   * 
   * @param g the group element
   * @return the summary node
   */
  public XElement getSummaryNode(Element g) {
    if (g != null) {
      return fly(g).down(".x-grid-summary-row");
    }
    return null;
  }

  /**
   * Returns true if summaries are visible.
   * 
   * @return true for visible
   */
  public boolean isSummaryVisible() {
    return !grid.getElement().hasClassName("x-grid-hide-summary");
  }

  /**
   * Toggles the summary information visibility.
   * 
   * @param visible true for visible, false to hide
   */
  public void toggleSummaries(boolean visible) {
    XElement el = grid.getElement();
    if (el != null) {
      if (visible) {
        el.removeClassName("x-grid-hide-summary");
      } else {
        el.addClassName("x-grid-hide-summary");
      }
    }
  }

  protected Map<ValueProvider<? super M, ?>, Number> calculate(List<M> models) {
    Map<ValueProvider<? super M, ?>, Number> data = new HashMap<ValueProvider<? super M, ?>, Number>();

    for (int i = 0, len = cm.getColumnCount(); i < len; i++) {
      SummaryColumnConfig<M,?> cf = (SummaryColumnConfig<M,?>) cm.getColumn(i);
      if (cf.getSummaryType() != null) {
        data.put(cf.getValueProvider(), calculate(cf, models));
      }
    }
    return data;
  }
  
  /**
   * Helper to ensure generics are typesafe
   * @param cf
   * @param models
   * @return
   */
  private <N> Number calculate(SummaryColumnConfig<M, N> cf, List<M> models) {
    ValueProvider<? super M, N> vp = cf.getValueProvider();
    SummaryType<N, ?> summaryType = cf.getSummaryType();
    return summaryType.calculate(models, vp);
  }

  @Override
  protected void onRemove(M m, int index, boolean isUpdate) {
    super.onRemove(m, index, isUpdate);
    if (!isUpdate) {
      refreshSummaries();
    }
  }

  @Override
  protected void onUpdate(ListStore<M> store, List<M> models) {
    super.onUpdate(store, models);
    refreshSummaries();
  }

  protected void refreshSummaries() {
    NodeList<Element> groups = getGroups();
    List<GroupingData<M>> groupData = getGroupData();
    for (int i = 0; i < groupData.size(); i++) {
      Element g = groups.getItem(i);
      if (g == null) continue;
      SafeHtml s = renderGroupSummary(groupData.get(i));
      XElement existing = getSummaryNode(g);
      if (existing != null) {
        existing.removeFromParent();
      }
      g.appendChild(XDOM.create(s));
    }
  }

  @Override
  protected void renderGroup(SafeHtmlBuilder buf, GroupingData<M> g, SafeHtml renderedRows) {
    super.renderGroup(buf, g, renderedRows);
    buf.append(renderGroupSummary(g));
  }

  @Override
  protected SafeHtml renderGroupSummary(GroupingData<M> groupInfo) {
    Map<ValueProvider<? super M, ?>, Number> data = calculate(groupInfo.getItems());
    return renderSummary(groupInfo, data);
  };

  protected SafeHtml renderSummary(GroupingData<M> groupInfo, Map<ValueProvider<? super M, ?>, Number> data) {
    int colCount = cm.getColumnCount();
    int last = colCount - 1;

    String unselectableClass = " " + unselectable;

    String cellClass = styles.cell();
    String cellInner = styles.cellInner();
    String cellFirstClass = " x-grid-cell-first";
    String cellLastClass = " x-grid-cell-last";

    SafeHtmlBuilder trBuilder = new SafeHtmlBuilder();
    
    for (int i = 0, len = colCount; i < len; i++) {
      SummaryColumnConfig<M,?> cf = (SummaryColumnConfig<M,?>) cm.getColumn(i);
      ColumnData cd = getColumnData().get(i);

      String cellClasses = cellClass;
      cellClasses += (i == 0 ? cellFirstClass : (i == last ? cellLastClass : ""));

      String id = cf.getColumnClassSuffix();
      if (id != null && !id.equals("")) {
        cellClasses += " x-grid-td-" + id;
      }

      Number n = data.get(cm.getValueProvider(i));
      String value = "";

      if (cf.getSummaryFormat() != null) {
        value = n == null ? "" : cf.getSummaryFormat().format(n.doubleValue());
      } else if (cf.getSummaryRenderer() != null) {
        value = cf.getSummaryRenderer().render(n, data).asString();
      }

      SafeHtml tdContent = tpls.td(i, cellClasses, cd.getStyles(), cellInner, cf.getColumnTextStyle(), SafeHtmlUtils.fromString(value));
      trBuilder.append(tdContent);

    }

    String rowClasses = "x-grid-row-summary";

    if (!selectable) {
      rowClasses += unselectableClass;
    }

    SafeHtml cells = trBuilder.toSafeHtml();

    return tpls.tr(rowClasses, cells);
  }
}
