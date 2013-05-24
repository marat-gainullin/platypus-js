/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadExceptionEvent;
import com.sencha.gxt.data.shared.loader.LoaderHandler;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.GridDropTarget;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.event.BodyScrollEvent;
import com.sencha.gxt.widget.core.client.event.BodyScrollEvent.BodyScrollHandler;
import com.sencha.gxt.widget.core.client.event.BodyScrollEvent.HasBodyScrollHandlers;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.event.CellClickEvent.CellClickHandler;
import com.sencha.gxt.widget.core.client.event.CellClickEvent.HasCellClickHandlers;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.HasCellDoubleClickHandlers;
import com.sencha.gxt.widget.core.client.event.CellMouseDownEvent;
import com.sencha.gxt.widget.core.client.event.CellMouseDownEvent.CellMouseDownHandler;
import com.sencha.gxt.widget.core.client.event.CellMouseDownEvent.HasCellMouseDownHandlers;
import com.sencha.gxt.widget.core.client.event.HeaderClickEvent;
import com.sencha.gxt.widget.core.client.event.HeaderClickEvent.HasHeaderClickHandlers;
import com.sencha.gxt.widget.core.client.event.HeaderClickEvent.HeaderClickHandler;
import com.sencha.gxt.widget.core.client.event.HeaderContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.HeaderContextMenuEvent.HasHeaderContextMenuHandlers;
import com.sencha.gxt.widget.core.client.event.HeaderContextMenuEvent.HeaderContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.HeaderDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.HeaderDoubleClickEvent.HasHeaderDoubleClickHandlers;
import com.sencha.gxt.widget.core.client.event.HeaderDoubleClickEvent.HeaderDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.HeaderMouseDownEvent;
import com.sencha.gxt.widget.core.client.event.HeaderMouseDownEvent.HasHeaderMouseDownHandlers;
import com.sencha.gxt.widget.core.client.event.HeaderMouseDownEvent.HeaderMouseDownHandler;
import com.sencha.gxt.widget.core.client.event.ReconfigureEvent;
import com.sencha.gxt.widget.core.client.event.ReconfigureEvent.HasReconfigureHandlers;
import com.sencha.gxt.widget.core.client.event.ReconfigureEvent.ReconfigureHandler;
import com.sencha.gxt.widget.core.client.event.RefreshEvent;
import com.sencha.gxt.widget.core.client.event.RefreshEvent.HasRefreshHandlers;
import com.sencha.gxt.widget.core.client.event.RefreshEvent.RefreshHandler;
import com.sencha.gxt.widget.core.client.event.RowClickEvent;
import com.sencha.gxt.widget.core.client.event.RowClickEvent.HasRowClickHandlers;
import com.sencha.gxt.widget.core.client.event.RowClickEvent.RowClickHandler;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent.HasRowDoubleClickHandlers;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent.RowDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.RowMouseDownEvent;
import com.sencha.gxt.widget.core.client.event.RowMouseDownEvent.HasRowMouseDownHandlers;
import com.sencha.gxt.widget.core.client.event.RowMouseDownEvent.RowMouseDownHandler;
import com.sencha.gxt.widget.core.client.event.SortChangeEvent;
import com.sencha.gxt.widget.core.client.event.SortChangeEvent.HasSortChangeHandlers;
import com.sencha.gxt.widget.core.client.event.SortChangeEvent.SortChangeHandler;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent.HasViewReadyHandlers;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent.ViewReadyHandler;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;

/**
 * A {@link Grid} provides support for displaying and editing two-dimensional
 * tables of cells. The grid gets its data from a {@link ListStore} and its
 * column definitions from a {@link ColumnModel}. Each model in the store is
 * rendered as a row in the grid. The fields in the model provide the data for
 * each column in the row. Any updates to the store are automatically pushed to
 * the grid. This includes inserting, removing, sorting and filter.
 * <p/>
 * In GXT version 3, {@link ModelKeyProvider}s and {@link ValueProvider}s
 * provide the interface between your data model and the list store and
 * {@link ColumnConfig} classes. This enables a grid to work with data of any
 * object type.
 * <p/>
 * You can provide your own implementation of these interfaces, or you can use a
 * Sencha supplied generator to create them for you automatically. A generator
 * runs at compile time to create a Java class that is compiled to JavaScript.
 * The Sencha supplied generator can create classes for interfaces that extend
 * the {@link PropertyAccess} interface. The generator transparently creates the
 * class at compile time and the {@link GWT#create(Class)} method returns an
 * instance of that class at run time. The generated class is managed by GWT and
 * GXT and you generally do not need to worry about what the class is called,
 * where it is located, or other similar details.
 * <p/>
 * Each grid has a {@link GridView}. The grid view provides many options for
 * customizing the grid's appearance (e.g. striping, mouse-over tracking, empty
 * text). To set these options, get the current grid view using
 * {@link Grid#getView()} and then set the desired option on the grid view.
 * <p/>
 * To customize the appearance of a column in a grid, provide a cell
 * implementation using {@link ColumnConfig#setCell(Cell)}.
 * <p />
 * Grids support several ways to manage column widths:
 * <ol>
 * <li>The most basic approach is to simply give pixel widths to each column.
 * Columns widths will match the specified values.</li>
 * <li>A column can be identified as an auto-expand column. As the width of the
 * grid changes, or columns are resized, the specified column's width is
 * adjusted so that the column fills the available width with no horizontal
 * scrolling. See {@link GridView#setAutoExpandColumn(ColumnConfig)}.</li>
 * <li>The grid can resize columns based on relative weights, determined by the
 * pixel width assigned to each column. As the width of the grid or columns
 * change, the weight is used to allocate the available space. Use
 * {@link GridView#setAutoFill(boolean)} or
 * {@link GridView#setForceFit(boolean)} to enable this feature:</li>
 * <ul>
 * <li>With auto fill, the calculations are run when the grid is created (or
 * reconfigured). After the grid is rendered, the column widths will not be
 * adjusted when the available width changes.</li>
 * <li>With force fit the width calculations are run every time there are
 * changes to the available width or column sizes.</li>
 * </ul>
 * <li>To prevent a column from participating in auto fill or force fit, use
 * {@link ColumnConfig#setFixed(boolean)}.</li>
 * </ol>
 * </p>
 * The following code snippet illustrates the creation of a simple grid with
 * local data for test purposes. For more practical examples that show how to
 * load data from remote sources, see the Json Grid, Live Grid, Paging Grid,
 * RequestFactory Grid and Xml Grid examples in the online Explorer demo.</p>
 * 
 * <pre>{@code 
    // Create an instance of the generated key and value providers for the Data class
    DataProperties dp = GWT.create(DataProperties.class);

    // Create the configurations for each column in the grid
    List<ColumnConfig<Data, ?>> ccs = new LinkedList<ColumnConfig<Data, ?>>();
    ccs.add(new ColumnConfig<Data, String>(dp.name(), 200, "Name"));
    ccs.add(new ColumnConfig<Data, String>(dp.value(), 200, "Value"));
    ColumnModel<Data> cm = new ColumnModel<Test.Data>(ccs);

    // Create the store that the contains the data to display in the grid
    ListStore<Data> s = new ListStore<Test.Data>(dp.key());
    s.add(new Data("name1", "value1"));
    s.add(new Data("name2", "value2"));
    s.add(new Data("name3", "value3"));
    s.add(new Data("name4", "value4"));

    // Create the grid using the store and column configurations
    Grid<Data> g = new Grid<Data>(s, cm);

    // Add the grid to a container
    RootPanel.get().add(g);
 * }</pre>
 * <p/>
 * To use the Sencha supplied generator to create model key providers and value
 * providers, extend the <code>PropertyAccess</code> interface, parameterized
 * with the type of data you want to access (as shown below) and invoke the
 * <code>GWT.create</code> method on its <code>class</code> member (as shown in
 * the code snippet above). This creates an instance of the class that can be
 * used to initialize the column configuration and list store. In the following
 * code snippet we define a new interface called <code>DataProperties</code>
 * that extends the <code>PropertyAccess</code> interface and is parameterized
 * with <code>Data</code>, a Plain Old Java Object (POJO).
 * <p/>
 * 
 * <pre>
  public interface DataProperties extends PropertyAccess<Data> {
    &#64;Path("name")
    ModelKeyProvider<Data> key();
    ValueProvider&lt;Data, String> name();
    ValueProvider&lt;Data, String> value();
  }

  public class Data {
    private String name;
    private String value;

    public Data(String name, String value) {
      super();
      this.name = name;
      this.value = value;
    }
    public String getName() {
      return name;
    }
    public String getValue() {
      return value;
    }
    public void setName(String name) {
      this.name = name;
    }
    public void setValue(String value) {
      this.value = value;
    }
  }
 * </pre>
 * <p/>
 * To enable drag and drop for a grid, add the following:
 * <p/>
 * 
 * <pre>
    new GridDragSource<Data>(g);
    GridDropTarget<Data> dt = new GridDropTarget<Data>(g);
    dt.setFeedback(Feedback.BOTH);
 * </pre>
 * <p/>
 * To add reordering support to the drag and drop, include:
 * 
 * <pre>
    dt.setAllowSelfAsSource(true);
 * </pre>
 * 
 * @param <M> the model type
 * @see ListStore
 * @see ColumnModel
 * @see ColumnConfig
 * @see GridView
 * @see GridDragSource
 * @see GridDropTarget
 * @see GridInlineEditing
 * @see CellSelectionModel
 */
public class Grid<M> extends Component implements HasViewReadyHandlers, HasSortChangeHandlers, HasRowClickHandlers,
    HasRowDoubleClickHandlers, HasRowMouseDownHandlers, HasCellClickHandlers, HasCellDoubleClickHandlers,
    HasCellMouseDownHandlers, HasHeaderClickHandlers, HasHeaderDoubleClickHandlers, HasHeaderContextMenuHandlers,
    HasHeaderMouseDownHandlers, HasRefreshHandlers, HasReconfigureHandlers, HasBodyScrollHandlers {

  /**
   * Provides a mechanism by which other components can report whether a cell is
   * selectable. This decouples components that need to know if a cell is
   * selectable from components that know if a cell is selectable.
   */
  public static interface Callback {

    /**
     * Returns true to indicate the given cell is selectable.
     * 
     * @param cell the cell to check
     * @return true to indicate the cell is editable
     */
    public boolean isSelectable(GridCell cell);
  }

  /**
   * A reference to a cell in the grid that can be used for a variety of
   * purposes, including, for example, whether it is active or selected.
   */
  public static class GridCell {
    private final int col;
    private final int row;

    /**
     * Creates a reference to a cell in the grid.
     * 
     * @param row the row index of the cell
     * @param col the column index of the cell
     */
    public GridCell(int row, int col) {
      this.row = row;
      this.col = col;
    }

    /**
     * Returns the cell column index.
     * 
     * @return the cell column index
     */
    public int getCol() {
      return col;
    }

    /**
     * Returns the cell row index.
     * 
     * @return the cell row index
     */
    public int getRow() {
      return row;
    }

  }

  /**
   * The current column model for this grid. Should be considered read-only in
   * derived classes unless the class is tightly integrated with the grid's
   * construction and reconfiguration process.
   */
  protected ColumnModel<M> cm;
  /**
   * The current selection model for this grid. Should be considered read-only
   * in derived classes unless the class is tightly integrated with the grid's
   * construction and reconfiguration process.
   */
  protected GridSelectionModel<M> sm;
  /**
   * The current store for this grid. Should be considered read-only in derived
   * classes unless the class is tightly integrated with the grid's construction
   * and reconfiguration process.
   */
  protected ListStore<M> store;
  /**
   * The current view for this grid. Should be considered read-only in derived
   * classes unless the class is tightly integrated with the grid's construction
   * and reconfiguration process.
   */
  protected GridView<M> view;
  /**
   * The current view for this grid. Should be considered read-only in derived
   * classes.
   */
  protected boolean viewReady;

  private ListLoader<?, ?> loader;

  private boolean enableColumnReorder;
  private boolean enableColumnResize = true;
  private boolean hideHeaders;
  private int lazyRowRender = 10;
  private HandlerRegistration loaderRegistration;
  private LoaderHandler<ListLoadConfig, ListLoadResult<?>> loadHandler = new LoaderHandler<ListLoadConfig, ListLoadResult<?>>() {

    @Override
    public void onBeforeLoad(BeforeLoadEvent<ListLoadConfig> event) {
      Grid.this.onLoaderBeforeLoad();
    }

    @Override
    public void onLoad(LoadEvent<ListLoadConfig, ListLoadResult<?>> event) {
      Grid.this.onLoadLoader();
    }

    @Override
    public void onLoadException(LoadExceptionEvent<ListLoadConfig> event) {
      Grid.this.onLoaderLoadException();
    }
  };

  private boolean loadMask;
  private int minColumnWidth = 25;

  /**
   * Creates a new grid with the given data store and column model.
   * 
   * @param store the data store
   * @param cm the column model
   */
  public Grid(ListStore<M> store, ColumnModel<M> cm) {
    this(store, cm, new GridView<M>());
  }

  /**
   * Creates a new grid with the given data store and column model.
   * 
   * @param store the data store
   * @param cm the column model
   */
  @UiConstructor
  public Grid(ListStore<M> store, ColumnModel<M> cm, GridView<M> view) {
    this.store = store;
    this.cm = cm;
    this.view = view;

    disabledStyle = null;
    setSelectionModel(new GridSelectionModel<M>());

    setAllowTextSelection(false);

    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    view.getAppearance().render(builder);

    setElement(XDOM.create(builder.toSafeHtml()));
    getElement().makePositionable();

    sinkCellEvents();
  }

  /**
   * Creates a grid to be initialized by derived classes.
   */
  protected Grid() {

  }

  @Override
  public HandlerRegistration addBodyScrollHandler(BodyScrollHandler handler) {
    return addHandler(handler, BodyScrollEvent.getType());
  }

  @Override
  public HandlerRegistration addCellClickHandler(CellClickHandler handler) {
    return addHandler(handler, CellClickEvent.getType());
  }

  @Override
  public HandlerRegistration addCellDoubleClickHandler(CellDoubleClickHandler handler) {
    return addHandler(handler, CellDoubleClickEvent.getType());
  }

  @Override
  public HandlerRegistration addCellMouseDownHandler(CellMouseDownHandler handler) {
    return addHandler(handler, CellMouseDownEvent.getType());
  }

  @Override
  public HandlerRegistration addHeaderClickHandler(HeaderClickHandler handler) {
    return addHandler(handler, HeaderClickEvent.getType());
  }

  @Override
  public HandlerRegistration addHeaderContextMenuHandler(HeaderContextMenuHandler handler) {
    return addHandler(handler, HeaderContextMenuEvent.getType());
  }

  @Override
  public HandlerRegistration addHeaderDoubleClickHandler(HeaderDoubleClickHandler handler) {
    return addHandler(handler, HeaderDoubleClickEvent.getType());
  }

  @Override
  public HandlerRegistration addHeaderMouseDownHandler(HeaderMouseDownHandler handler) {
    return addHandler(handler, HeaderMouseDownEvent.getType());
  }

  @Override
  public HandlerRegistration addReconfigureHandler(ReconfigureHandler handler) {
    return addHandler(handler, ReconfigureEvent.getType());
  }

  @Override
  public HandlerRegistration addRefreshHandler(RefreshHandler handler) {
    return addHandler(handler, RefreshEvent.getType());
  }

  @Override
  public HandlerRegistration addRowClickHandler(RowClickHandler handler) {
    return addHandler(handler, RowClickEvent.getType());
  }

  @Override
  public HandlerRegistration addRowDoubleClickHandler(RowDoubleClickHandler handler) {
    return addHandler(handler, RowDoubleClickEvent.getType());
  }

  @Override
  public HandlerRegistration addRowMouseDownHandler(RowMouseDownHandler handler) {
    return addHandler(handler, RowMouseDownEvent.getType());
  }

  @Override
  public HandlerRegistration addSortChangeHandler(SortChangeHandler handler) {
    return addHandler(handler, SortChangeEvent.getType());
  }

  @Override
  public HandlerRegistration addViewReadyHandler(ViewReadyHandler handler) {
    return addHandler(handler, ViewReadyEvent.getType());
  }

  @Override
  public void focus() {
    view.focus();
  }

  /**
   * Returns the column model.
   * 
   * @return the column model
   */
  public ColumnModel<M> getColumnModel() {
    return cm;
  }

  /**
   * Returns the time in ms after the rows get rendered.
   * 
   * @return the lazy row rendering time
   */
  public int getLazyRowRender() {
    return lazyRowRender;
  }

  /**
   * Returns the loader.
   * 
   * @return the loader
   */
  public ListLoader<?, ?> getLoader() {
    return loader;
  }

  /**
   * Returns the minimum column width.
   * 
   * @return the min width in pixels
   */
  public int getMinColumnWidth() {
    return minColumnWidth;
  }

  /**
   * Returns the grid's selection model.
   * 
   * @return the selection model
   */
  public GridSelectionModel<M> getSelectionModel() {
    return sm;
  }

  /**
   * Returns the grid's store.
   * 
   * @return the store
   */
  public ListStore<M> getStore() {
    return store;
  }

  /**
   * Returns the grid's view.
   * 
   * @return the grid view
   */
  public GridView<M> getView() {
    return view;
  }

  /**
   * Returns true if column reordering is enabled.
   * 
   * @return true if enabled
   */
  public boolean isColumnReordering() {
    return enableColumnReorder;
  }

  /**
   * Returns true if column resizing is enabled.
   * 
   * @return true if resizing is enabled
   */
  public boolean isColumnResize() {
    return enableColumnResize;
  }

  /**
   * Returns true if the header is hidden.
   * 
   * @return true for hidden
   */
  public boolean isHideHeaders() {
    return hideHeaders;
  }

  /**
   * Returns true if the load mask in enabled.
   * 
   * @return the load mask state
   */
  public boolean isLoadMask() {
    return loadMask;
  }

  /**
   * Returns true if the view is ready.
   * 
   * @return the view ready state
   */
  public boolean isViewReady() {
    return viewReady;
  }

  @Override
  public void onBrowserEvent(Event ce) {
    int type = ce.getTypeInt();

    CellWidgetImplHelper.onBrowserEvent(this, ce);

    Cell<?> cell = handleEventForCell(ce);

    // we dont want selection model to get click event initiated from cells that
    // handle selection
    // for example, a combo box we dont want row selected when trigger is
    // clicked
    if (type == Event.ONCLICK && cell != null && cell.handlesSelection()) {
      return;
    }

    super.onBrowserEvent(ce);
    switch (type) {
      case Event.ONCLICK:
        onClick(ce);
        break;
      case Event.ONDBLCLICK:
        onDoubleClick(ce);
        break;
      case Event.ONMOUSEDOWN:
        onMouseDown(ce);
        break;
      case Event.ONMOUSEUP:
        onMouseUp(ce);
        break;
      case Event.ONFOCUS:
        onFocus(ce);
        break;
      case Event.ONBLUR:
        onBlur(ce);
        break;
    }
    view.handleComponentEvent(ce);

  }

  /**
   * Reconfigures the grid to use a different Store and Column Model. The View
   * will be bound to the new objects and refreshed.
   * 
   * @param store the new store
   * @param cm the new column model
   */
  public void reconfigure(ListStore<M> store, ColumnModel<M> cm) {
    if (!viewReady) {
      this.store = store;
      this.cm = cm;
      return;
    }
    if (loadMask) {
      mask(DefaultMessages.getMessages().loadMask_msg());
    }
    view.initData(store, cm);

    this.store = store;
    this.cm = cm;
    sinkCellEvents();
    // rebind the sm
    setSelectionModel(sm);
    if (isViewReady()) {
      view.refresh(true);
    }
    if (loadMask) {
      unmask();
    }
    fireEvent(new ReconfigureEvent());
  }

  @Override
  public void setAllowTextSelection(boolean enable) {
    allowTextSelection = enable;
  }

  /**
   * True to enable column reordering via drag and drop (defaults to false).
   * 
   * @param enableColumnReorder true to enable
   */
  public void setColumnReordering(boolean enableColumnReorder) {
    this.enableColumnReorder = enableColumnReorder;
  }

  /**
   * Sets whether columns may be resized (defaults to true).
   * 
   * @param enableColumnResize true to allow column resizing
   */
  public void setColumnResize(boolean enableColumnResize) {
    this.enableColumnResize = enableColumnResize;
  }

  /**
   * Sets whether the header should be hidden (defaults to false).
   * 
   * @param hideHeaders true to hide the header
   */
  public void setHideHeaders(boolean hideHeaders) {
    this.hideHeaders = hideHeaders;
  }

  /**
   * Sets the time in ms after the row gets rendered (defaults to 10). 0 means
   * that the rows get rendered as soon as the grid gets rendered.
   * 
   * @param lazyRowRender the time in ms after the rows get rendered.
   */
  public void setLazyRowRender(int lazyRowRender) {
    this.lazyRowRender = lazyRowRender;
  }

  /**
   * Sets the loader.
   * 
   * @param loader the loader
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void setLoader(ListLoader<?, ?> loader) {
    if (this.loaderRegistration != null) {
      loaderRegistration.removeHandler();
      loaderRegistration = null;
    }
    this.loader = loader;
    if (loader != null) {
      loaderRegistration = loader.addLoaderHandler((LoaderHandler) loadHandler);
    }
  }

  /**
   * Sets whether a load mask should be displayed during load operations
   * (defaults to false).
   * 
   * @param loadMask true to show a mask
   */
  public void setLoadMask(boolean loadMask) {
    this.loadMask = loadMask;
  }

  /**
   * The minimum width a column can be resized to (defaults to 25).
   * 
   * @param minColumnWidth the min column width
   */
  public void setMinColumnWidth(int minColumnWidth) {
    this.minColumnWidth = minColumnWidth;
  }

  /**
   * Sets the grid selection model.
   * 
   * @param sm the selection model
   */
  public void setSelectionModel(GridSelectionModel<M> sm) {
    if (this.sm != null) {
      this.sm.bindGrid(null);
    }
    this.sm = sm;
    if (sm != null) {
      sm.bindGrid(this);
    }
  }

  /**
   * Sets the view's grid (pre-render).
   * 
   * @param view the view
   */
  public void setView(GridView<M> view) {
    this.view = view;

    // rebind the sm
    if (getSelectionModel() != sm) {
      setSelectionModel(sm);
    }
  }

  /**
   * Navigate in the requested direction to the next selectable cell, given the
   * row, column and step.
   * 
   * @param row the starting row index
   * @param col the starting column index
   * @param step the step size and direction
   * @param callback a callback that determines whether the given cell is
   *          selectable
   * @return the next cell or <code>null</code> if no cell matches the criteria
   */
  public GridCell walkCells(int row, int col, int step, Callback callback) {
    boolean first = true;
    int clen = cm.getColumnCount();
    int rlen = store.size();
    if (step < 0) {
      if (col < 0) {
        row--;
        first = false;
      }
      while (row >= 0) {
        if (!first) {
          col = clen - 1;
        }
        first = false;
        while (col >= 0) {
          GridCell cell = new GridCell(row, col);
          if (callback.isSelectable(cell)) {
            return cell;
          }
          col--;
        }
        row--;
      }
    } else {
      if (col >= clen) {
        row++;
        first = false;
      }
      while (row < rlen) {
        if (!first) {
          col = 0;
        }
        first = false;
        while (col < clen) {
          GridCell cell = new GridCell(row, col);
          if (callback.isSelectable(cell)) {
            return cell;
          }
          col++;
        }
        row++;
      }
    }
    return null;
  }

  /**
   * Invoked after the view element is first attached, performs the final steps
   * before the grid is completely initialized.
   */
  protected void afterRenderView() {
    view.afterRender();
    viewReady = true;
    onAfterRenderView();

    fireEvent(new ViewReadyEvent());
  }

  /**
   * Returns true if the given cell consumes the given event.
   * 
   * @param cell the cell to inspect
   * @param eventType the event
   * @return true if the cell consumes the given event
   */
  protected boolean cellConsumesEventType(Cell<?> cell, String eventType) {
    Set<String> consumedEvents = cell.getConsumedEvents();
    return consumedEvents != null && consumedEvents.contains(eventType);
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    view.doAttach();
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    view.doDetach();
  }

  /**
   * Fires an event to the cell specified by the given record data model and
   * column.
   * 
   * @param event the event to fire
   * @param eventType the type of event
   * @param cellParent the containing parent element
   * @param m the record data model containing the cell
   * @param context the context of the cell (row, column and key)
   * @param column the column containing the cell
   */
  protected <N> Cell<?> fireEventToCell(Event event, String eventType, Element cellParent, final M m, Context context,
      final ColumnConfig<M, N> column) {
    Cell<N> cell = column.getCell();
    if (cell != null && cellConsumesEventType(cell, eventType)) {
      N cellValue = null;
      if (store.hasRecord(m)) {
        cellValue = store.getRecord(m).getValue(column.getValueProvider());
      } else {
        cellValue = column.getValueProvider().getValue(m);
      }
      cell.onBrowserEvent(context, cellParent, cellValue, event, new ValueUpdater<N>() {
        @Override
        public void update(N value) {
          Grid.this.getStore().getRecord(m).addChange(column.getValueProvider(), value);
        }
      });
      return cell;
    }
    return null;
  }

  /**
   * Inspects the given event and fires it to a cell if possible.
   * 
   * @param event the event to handle
   */
  protected Cell<?> handleEventForCell(Event event) {
    // Get the event target.
    EventTarget eventTarget = event.getEventTarget();
    if (!Element.is(eventTarget)) {
      return null;
    }
    final Element target = event.getEventTarget().cast();

    int rowIndex = getView().findRowIndex(target);
    int colIndex = getView().findCellIndex(target, null);

    M value = getStore().get(rowIndex);
    ColumnConfig<M, ?> config = cm.getColumn(colIndex);
    Element cellParent = getView().getCell(rowIndex, colIndex);
    if (value != null && config != null && cellParent != null) {
      Context context = new Context(rowIndex, colIndex, getStore().getKeyProvider().getKey(value));
      return fireEventToCell(event, event.getType(), cellParent.getFirstChildElement(), value, context, config);
    }
    return null;
  }

  @Override
  protected void notifyHide() {
    super.notifyHide();
    view.notifyHide();
  }

  @Override
  protected void notifyShow() {
    super.notifyShow();
    view.notifyShow();
  }

  @Override
  protected void onAfterFirstAttach() {
    super.onAfterFirstAttach();
    if (lazyRowRender > 0) {
      Timer t = new Timer() {
        @Override
        public void run() {
          afterRenderView();
        }
      };
      t.schedule(lazyRowRender);
    } else {
      afterRenderView();
    }
  }

  /**
   * Invoked after the view has been rendered, may be overridden to perform any
   * activities that require a rendered view.
   */
  protected void onAfterRenderView() {
  }

  @Override
  protected void onAttach() {
    if (!isOrWasAttached()) {
      view.init(this);
    }
    super.onAttach();
  }

  /**
   * Handles the browser click event. Propagates the event to the cell and row
   * if possible.
   * 
   * @param e the click event
   */
  protected void onClick(Event e) {
    Element target = Element.as(e.getEventTarget());
    int rowIndex = view.findRowIndex(target);
    if (rowIndex != -1) {
      int colIndex = view.findCellIndex(target, null);
      if (colIndex != -1) {
        fireEvent(new CellClickEvent(rowIndex, colIndex, e));
      }
      fireEvent(new RowClickEvent(rowIndex, colIndex, e));
    }
  }

  @Override
  protected void onDisable() {
    super.onDisable();
    mask();
  }

  /**
   * Handles the browser double click event. Propagates the event to the cell
   * and row if possible.
   * 
   * @param e the double click event
   */
  protected void onDoubleClick(Event e) {
    Element target = Element.as(e.getEventTarget());
    int rowIndex = view.findRowIndex(target);
    if (rowIndex != -1) {
      int colIndex = view.findCellIndex(target, null);
      if (colIndex != -1) {
        fireEvent(new CellDoubleClickEvent(rowIndex, colIndex, e));
      }
      fireEvent(new RowDoubleClickEvent(rowIndex, colIndex, e));
    }
  }

  @Override
  protected void onEnable() {
    super.onEnable();
    unmask();
  }

  /**
   * Invoked before the loader loads new data, displays the Loading... mask if
   * it is enabled.
   */
  protected void onLoaderBeforeLoad() {
    if (isLoadMask()) {
      mask(DefaultMessages.getMessages().loadMask_msg());
    }
  }

  /**
   * Invoked if the loader encounters an exception, cancels the Loading... mask
   * if it is enabled.
   */
  protected void onLoaderLoadException() {
    if (isLoadMask()) {
      unmask();
    }
  }

  /**
   * Invoked after the loader loads new data, cancels the Loading... mask if it
   * is enabled.
   */
  protected void onLoadLoader() {
    if (isLoadMask()) {
      unmask();
    }
  }

  /**
   * Handles the browser mouse down event. Propagates the event to the cell and
   * row if possible.
   * 
   * @param e the mouse down event
   */
  protected void onMouseDown(Event e) {
    if (!isAllowTextSelection() && GXT.isWebKit()) {
      String tagName = e.getEventTarget().<Element> cast().getTagName();
      if (!"input".equalsIgnoreCase(tagName) && !"textarea".equalsIgnoreCase(tagName)) {
        e.preventDefault();
      }
    }
    Element target = Element.as(e.getEventTarget());
    int rowIndex = view.findRowIndex(target);
    if (rowIndex != -1) {
      int colIndex = view.findCellIndex(target, null);
      if (colIndex != -1) {
        fireEvent(new CellMouseDownEvent(rowIndex, colIndex, e));
      }
      fireEvent(new RowMouseDownEvent(rowIndex, colIndex, e));
    }
  }

  /**
   * Handles the browser mouse up event.
   * 
   * @param e the mouse up event
   */
  protected void onMouseUp(Event e) {

  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    if (viewReady) {
      view.calculateVBar(true);
    } else {
      view.layout();
    }
  }

  /**
   * Sinks all the events consumed by the cells in the column configs.
   */
  protected void sinkCellEvents() {
    Set<String> consumedEvents = new HashSet<String>();
    for (int i = 0, len = cm.getColumnCount(); i < len; i++) {
      ColumnConfig<M, ?> c = cm.getColumn(i);
      Cell<?> cell = c.getCell();
      if (cell != null) {
        Set<String> cellEvents = cell.getConsumedEvents();
        if (cellEvents != null) {
          consumedEvents.addAll(cellEvents);
        }
      }
    }

    CellWidgetImplHelper.sinkEvents(this, consumedEvents);
  }

}
