/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.ComponentPlugin;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseItemEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseItemEvent.BeforeCollapseItemHandler;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseItemEvent.HasBeforeCollapseItemHandlers;
import com.sencha.gxt.widget.core.client.event.BeforeExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.BeforeExpandItemEvent.BeforeExpandItemHandler;
import com.sencha.gxt.widget.core.client.event.BeforeExpandItemEvent.HasBeforeExpandItemHandlers;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent.CollapseItemHandler;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent.HasCollapseItemHandlers;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent.ExpandItemHandler;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent.HasExpandItemHandlers;
import com.sencha.gxt.widget.core.client.event.RowClickEvent;
import com.sencha.gxt.widget.core.client.event.RowClickEvent.RowClickHandler;

/**
 * A {@link ColumnConfig} subclass and a {@link ComponentPlugin} that adds the
 * ability for each row to be expanded, showing custom content that spans all
 * the rows columns.
 * 
 * <p />
 * As with all component plugins, {@link #initPlugin(Grid)} must be called to
 * initialize the row expander.
 * 
 * @param <M> the model type
 */
public class RowExpander<M> extends ColumnConfig<M, M> implements ComponentPlugin<Grid<M>>,
    HasBeforeExpandItemHandlers<M>, HasExpandItemHandlers<M>, HasBeforeCollapseItemHandlers<M>,
    HasCollapseItemHandlers<M> {

  public interface RowExpanderAppearance<M> {
    String getRowStyles(M model, int rowIndex);

    boolean isExpanded(XElement row);

    boolean isExpandElement(XElement target);

    void onExpand(XElement row, boolean expand);

    void renderExpander(Context context, M value, SafeHtmlBuilder sb);
  }

  protected Grid<M> grid;
  protected Cell<M> contentCell;

  private SimpleEventBus eventBus;
  private final RowExpanderAppearance<M> appearance;

  /**
   * Creates a new row expander.
   * 
   * @param valueProvider the value provider
   * @param contentCell the content cell
   */
  public RowExpander(IdentityValueProvider<M> valueProvider, Cell<M> contentCell) {
    this(valueProvider, contentCell, GWT.<RowExpanderAppearance<M>> create(RowExpanderAppearance.class));
  }

  /**
   * Creates a new row expander.
   * 
   * @param valueProvider the value provider
   * @param contentCell the content cell
   * @param appearance the appearance
   */
  public RowExpander(IdentityValueProvider<M> valueProvider, Cell<M> contentCell,
      final RowExpanderAppearance<M> appearance) {
    super(valueProvider);

    this.contentCell = contentCell;
    this.appearance = appearance;

    setHeader("");
    setColumnClassSuffix("expander");
    setWidth(20);
    setSortable(false);
    setResizable(false);
    setFixed(true);
    setMenuDisabled(true);

    setCell(new AbstractCell<M>() {
      @Override
      public void render(Context context, M value, SafeHtmlBuilder sb) {
        assert grid != null : "RowExpander grid null, must call initPlugin";
        appearance.renderExpander(context, value, sb);
      }
    });
  }

  @Override
  public HandlerRegistration addBeforeCollapseHandler(BeforeCollapseItemHandler<M> handler) {
    return ensureHandlers().addHandler(BeforeCollapseItemEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addBeforeExpandHandler(BeforeExpandItemHandler<M> handler) {
    return ensureHandlers().addHandler(BeforeExpandItemEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addCollapseHandler(CollapseItemHandler<M> handler) {
    return ensureHandlers().addHandler(CollapseItemEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addExpandHandler(ExpandItemHandler<M> handler) {
    return ensureHandlers().addHandler(ExpandItemEvent.getType(), handler);
  }

  /**
   * Collapses the given row.
   * 
   * @param rowIndex the rowIndex
   */
  public void collapseRow(int rowIndex) {
    XElement row = XElement.as(grid.getView().getRow(rowIndex));
    if (row != null && isExpanded(row)) {
      collapseRow(row);
    }
  }

  /**
   * Expands the given row.
   * 
   * @param rowIndex the rowIndex
   */
  public void expandRow(int rowIndex) {
    XElement row = XElement.as(grid.getView().getRow(rowIndex));
    if (row != null && !isExpanded(row)) {
      expandRow(row);
    }
  }

  /**
   * Returns the content cell.
   * 
   * @return the content cell
   */
  public Cell<M> getContentCell() {
    return contentCell;
  }

  @Override
  public void initPlugin(Grid<M> component) {
    grid = component;
    grid.getView().setEnableRowBody(true);
    grid.getView().setRowBodyRowSpan(2);

    grid.addStyleName("x-expander");

    GridView<M> view = grid.getView();

    final GridViewConfig<M> config = view.getViewConfig();
    view.viewConfig = new GridViewConfig<M>() {

      @Override
      public String getColStyle(M model, ValueProvider<? super M, ?> valueProvider, int rowIndex, int colIndex) {
        return "";
      }

      @Override
      public String getRowStyle(M model, int rowIndex) {
        String s = appearance.getRowStyles(model, rowIndex);
        if (config != null) {
          return s + " " + config.getRowStyle(model, rowIndex);
        } else {
          return s;
        }
      }
    };

    grid.addRowClickHandler(new RowClickHandler() {

      @Override
      public void onRowClick(RowClickEvent event) {
        onMouseDown(event);
      }
    });
  }

  /**
   * Sets the content cell.
   * 
   * @param contentCell the content cell
   */
  public void setContentCell(Cell<M> contentCell) {
    this.contentCell = contentCell;
  }

  protected boolean beforeExpand(M model, Element body, XElement row, int rowIndex) {
    BeforeExpandItemEvent<M> e = new BeforeExpandItemEvent<M>(model);
    ensureHandlers().fireEvent(e);

    if (!e.isCancelled()) {
      body.setInnerHTML(getBodyContent(model, rowIndex));
      return true;
    }
    return false;
  }

  protected void collapseRow(XElement row) {
    int idx = row.getPropertyInt("rowindex");
    M model = grid.getStore().get(idx);

    BeforeCollapseItemEvent<M> e = new BeforeCollapseItemEvent<M>(model);
    ensureHandlers().fireEvent(e);

    if (!e.isCancelled()) {
      appearance.onExpand(row, false);
      // expander row not visible in ie6 ie7 without repaint
      if (GXT.isIE6() || GXT.isIE7()) {
        grid.getElement().repaint();
      }
      ensureHandlers().fireEvent(new CollapseItemEvent<M>(model));
    }

  }

  protected void expandRow(XElement row) {
    int idx = row.getPropertyInt("rowindex");
    M model = grid.getStore().get(idx);
    Element rowBody = grid.getView().getRowBody(row);
    if (beforeExpand(model, rowBody, row, idx)) {
      appearance.onExpand(row, true);
      // expander row not visible in ie6 ie7 without repaint
      if (GXT.isIE6() || GXT.isIE7()) {
        grid.getElement().repaint();
      }
      ensureHandlers().fireEvent(new ExpandItemEvent<M>(model));
    }
  }

  protected String getBodyContent(M model, int rowIndex) {
    SafeHtmlBuilder sb = new SafeHtmlBuilder();
    contentCell.render(new Context(rowIndex, 0, grid.getStore().getKeyProvider().getKey(model)), model, sb);
    return sb.toSafeHtml().asString();
  }

  protected boolean isExpanded(XElement row) {
    return appearance.isExpanded(row);
  }

  protected void onMouseDown(RowClickEvent event) {
    Event e = event.getEvent();
    XElement target = e.getEventTarget().cast();
    if (appearance.isExpandElement(target)) {
      e.stopPropagation();
      e.preventDefault();
      XElement row = grid.getView().findRow(target).cast();
      toggleRow(row);
    }
  }

  protected void toggleRow(XElement row) {
    if (!appearance.isExpanded(row)) {
      expandRow(row);
    } else {
      collapseRow(row);
    }
    grid.getView().calculateVBar(false);
  }

  SimpleEventBus ensureHandlers() {
    return eventBus == null ? eventBus = new SimpleEventBus() : eventBus;
  }

}
