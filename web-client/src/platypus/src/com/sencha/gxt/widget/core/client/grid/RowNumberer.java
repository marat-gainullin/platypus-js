/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreAddEvent.StoreAddHandler;
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.data.shared.event.StoreFilterEvent.StoreFilterHandler;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent.StoreRemoveHandler;
import com.sencha.gxt.widget.core.client.ComponentPlugin;

/**
 * A {@link ColumnConfig} that provides an automatic row numbering column.
 * <p/>
 * Code Snippet:
 * <p/>
 * 
 * <pre>{@code
    List<ColumnConfig<Data, ?>> ccs = new LinkedList<ColumnConfig<Data, ?>>();
    RowNumberer<Data> rn = new RowNumberer<Test.Data>(new IdentityValueProvider<Data>());
    ccs.add(rn);
    ... add more column configs ...
    ColumnModel<Data> cm = new ColumnModel<Test.Data>(ccs);
    Grid<Data> g = new Grid<Data>(s, cm);
    rn.initPlugin(g);
 * }</pre>
 * 
 * @param <M>
 */
public class RowNumberer<M> extends ColumnConfig<M, M> implements ComponentPlugin<Grid<M>> {

  public interface RowNumbererAppearance {

  }

  private class Handler implements StoreAddHandler<M>, StoreFilterHandler<M>, StoreRemoveHandler<M> {

    @Override
    public void onAdd(StoreAddEvent<M> event) {
      doRefresh();
    }

    @Override
    public void onFilter(StoreFilterEvent<M> event) {
      doRefresh();
    }

    @Override
    public void onRemove(StoreRemoveEvent<M> event) {
      doRefresh();
    }

  }

  protected Grid<M> grid;

  private Handler handler = new Handler();
  private DelayedTask updateTask = new DelayedTask() {

    @Override
    public void onExecute() {
      doUpdate();
    }
  };

  /**
   * Creates a row numberer. To use the row numberer, add it to a column model,
   * create a grid with the column model and then invoke
   * {@link RowNumberer#initPlugin(Grid)} on the grid.
   * 
   * @param valueProvider an identity value provider (e.g. new
   *          IdentityValueProvider<M>()).
   */
  public RowNumberer(IdentityValueProvider<M> valueProvider) {
    super(valueProvider);

    setHeader("");
    setWidth(23);
    setColumnClassSuffix("numberer");
    setSortable(false);
    setResizable(false);
    setFixed(true);
    setMenuDisabled(true);

    setCell(new AbstractCell<M>() {
      @Override
      public void render(Context context, M value, SafeHtmlBuilder sb) {
        sb.append(context.getIndex() + 1);
      }
    });
  }

  @Override
  public void initPlugin(Grid<M> component) {
    this.grid = component;

    grid.getStore().addStoreAddHandler(handler);
    grid.getStore().addStoreRemoveHandler(handler);
    grid.getStore().addStoreFilterHandler(handler);

    if (updateTask == null) {
      updateTask = new DelayedTask() {
        @Override
        public void onExecute() {
          doUpdate();
        }
      };
    }
  }

  @SuppressWarnings("unchecked")
  protected void doUpdate() {
    int col = grid.getColumnModel().indexOf(this);
    ModelKeyProvider<M> kp = (ModelKeyProvider<M>) grid.getStore().getKeyProvider();
    for (int i = 0, len = grid.getStore().size(); i < len; i++) {
      Element cell = grid.getView().getCell(i, col);
      if (cell != null) {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        getCell().render(new Context(i, col, kp.getKey(grid.getStore().get(i))), null, sb);
        cell.getFirstChildElement().setInnerHTML(sb.toSafeHtml().asString());
      }
    }
  }

  private void doRefresh() {
    updateTask.delay(50);
  }
}
