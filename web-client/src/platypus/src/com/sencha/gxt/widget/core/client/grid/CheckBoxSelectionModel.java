/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.widget.core.client.event.HeaderClickEvent;
import com.sencha.gxt.widget.core.client.event.HeaderClickEvent.HeaderClickHandler;
import com.sencha.gxt.widget.core.client.event.RefreshEvent;
import com.sencha.gxt.widget.core.client.event.RefreshEvent.RefreshHandler;
import com.sencha.gxt.widget.core.client.event.RowClickEvent;
import com.sencha.gxt.widget.core.client.event.RowMouseDownEvent;

/**
 * A grid selection model. To use, add the column config to the column model
 * using {@link #getColumn()}.
 * 
 * <p>
 * This selection mode defaults to SelectionMode.MULTI and also supports
 * SelectionMode.SIMPLE. With SIMPLE, the control and shift keys do not need to
 * be pressed for multiple selections.
 * 
 * @param <M> the model data type
 */
public class CheckBoxSelectionModel<M> extends GridSelectionModel<M> {

  public interface CheckBoxColumnAppearance<M> {

    boolean isHeaderChecked(XElement header);

    void onHeaderChecked(XElement header, boolean checked);

    void renderCheckBox(Context context, M value, SafeHtmlBuilder sb);
  }

  protected ColumnConfig<M, M> config;

  private final CheckBoxColumnAppearance<M> appearance = GWT.create(CheckBoxColumnAppearance.class);

  private GroupingHandlerRegistration handlerRegistration = new GroupingHandlerRegistration();

  public CheckBoxSelectionModel(IdentityValueProvider<M> valueProvider) {
    config = newColumnConfig(valueProvider);
    config.setColumnClassSuffix("checker");
    config.setWidth(20);
    config.setSortable(false);
    config.setResizable(false);
    config.setFixed(true);
    config.setMenuDisabled(true);

    config.setCell(new AbstractCell<M>() {
      @Override
      public void render(Context context, M value, SafeHtmlBuilder sb) {
        appearance.renderCheckBox(context, value, sb);
      }
    });
  }

  @Override
  public void bindGrid(Grid<M> grid) {
    if (this.grid != null) {
      handlerRegistration.removeHandler();
    }
    super.bindGrid(grid);

    if (grid != null) {
      handlerRegistration.add(grid.addHeaderClickHandler(new HeaderClickHandler() {
        @Override
        public void onHeaderClick(HeaderClickEvent event) {
          handleHeaderClick(event);
        }
      }));

      handlerRegistration.add(grid.addRefreshHandler(new RefreshHandler() {
        @Override
        public void onRefresh(RefreshEvent event) {
          CheckBoxSelectionModel.this.onRefresh(event);
        }
      }));
    }

  }

  /**
   * Returns the column config.
   * 
   * @return the column config
   */
  public ColumnConfig<M, M> getColumn() {
    return config;
  }

  /**
   * Returns true if the header checkbox is selected.
   * 
   * @return true if selected
   */
  public boolean isSelectAllChecked() {
    if (grid != null && grid.isViewReady()) {
      XElement hd = grid.getView().headerElem.child(".x-grid-hd-checker");
      return appearance.isHeaderChecked(hd);
    }
    return false;
  }

  /**
   * Sets the select all checkbox in the grid header and selects / deselects all
   * rows.
   * 
   * @param select true to select all
   */
  public void setSelectAllChecked(boolean select) {
    assert grid.isViewReady() : "cannot call this method before grid has been rendered";
    if (!select) {
      setChecked(false);
      deselectAll();
    } else {
      setChecked(true);
      selectAll();
    }
  }

  protected void handleHeaderClick(HeaderClickEvent event) {
    ColumnConfig<M, ?> c = grid.getColumnModel().getColumn(event.getColumnIndex());
    if (c == config) {
      XElement hd = event.getEvent().getEventTarget().<Element> cast().getParentElement().cast();
      boolean isChecked = appearance.isHeaderChecked(hd);
      if (isChecked) {
        setChecked(false);
        deselectAll();
      } else {
        setChecked(true);
        selectAll();
      }
    }
  }

  @Override
  protected void handleRowClick(RowClickEvent event) {
    Element target = event.getEvent().getEventTarget().cast();
    if (target.getClassName().equals("x-grid-row-checker")) {
      return;
    }
    super.handleRowClick(event);
  }

  @Override
  protected void handleRowMouseDown(RowMouseDownEvent event) {
    boolean left = event.getEvent().getButton() == Event.BUTTON_LEFT;
    Element target = event.getEvent().getEventTarget().cast();

    if (left && target.getClassName().equals("x-grid-row-checker")) {
      M model = listStore.get(event.getRowIndex());
      if (model != null) {
        if (isSelected(model)) {
          deselect(model);
        } else {
          select(model, true);
        }
      }
    } else {
      super.handleRowMouseDown(event);
    }
  }

  protected ColumnConfig<M, M> newColumnConfig(IdentityValueProvider<M> valueProvider) {
    return new ColumnConfig<M, M>(valueProvider);
  }

  @Override
  protected void onAdd(List<? extends M> models) {
    super.onAdd(models);
    updateHeaderCheckBox();
  }

  @Override
  protected void onClear(StoreClearEvent<M> event) {
    super.onClear(event);
    updateHeaderCheckBox();
  };

  protected void onRefresh(RefreshEvent event) {
    updateHeaderCheckBox();
  }

  protected void onRemove(M model) {
    super.onRemove(model);
    updateHeaderCheckBox();
  };

  @Override
  protected void onSelectChange(M model, boolean select) {
    super.onSelectChange(model, select);
    updateHeaderCheckBox();
  }

  protected void setChecked(boolean checked) {
    if (grid.isViewReady()) {
      XElement hd = grid.getView().headerElem.child(".x-grid-hd-checker");
      if (hd != null) {
        appearance.onHeaderChecked(hd.getParentElement().<XElement> cast(), checked);
      }
    }
  }

  private void updateHeaderCheckBox() {
    setChecked(getSelection().size() == listStore.size());
  }

}
