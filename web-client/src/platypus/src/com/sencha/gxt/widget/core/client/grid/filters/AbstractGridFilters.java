/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent.BeforeLoadHandler;
import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.Loader;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.ComponentPlugin;
import com.sencha.gxt.widget.core.client.event.ActivateEvent;
import com.sencha.gxt.widget.core.client.event.ActivateEvent.ActivateHandler;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent.DeactivateHandler;
import com.sencha.gxt.widget.core.client.event.HeaderContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.HeaderContextMenuEvent.HeaderContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.ReconfigureEvent;
import com.sencha.gxt.widget.core.client.event.ReconfigureEvent.ReconfigureHandler;
import com.sencha.gxt.widget.core.client.event.UpdateEvent;
import com.sencha.gxt.widget.core.client.event.UpdateEvent.UpdateHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.Head;
import com.sencha.gxt.widget.core.client.grid.ColumnHiddenChangeEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnHiddenChangeEvent.ColumnHiddenChangeHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem;

/**
 * Provides an abstract base class that applies filters to the rows in a grid.
 * <p/>
 * A filter is applied to a grid to reduce the amount of information that is
 * displayed, thus highlighting the information of interest to the user. A
 * filter is generally invoked from a header menu.
 * <p/>
 * The filters can be applied locally using {@link Store} filtering or passed to
 * a remote data source using a {@link FilterPagingLoadConfig}. To enable local
 * filtering, use <code>setLocal(true)</code>. To enable remote filtering, use
 * the {@link AbstractGridFilters#AbstractGridFilters(Loader)} form of the
 * constructor.
 * <p/>
 * To add a filter to a {@link Grid} column, create an instance of a concrete
 * subclass of {@link Filter}, passing to the constructor the
 * {@link ValueProvider} for the column, then add the filter to a
 * {@link GridFilters} using {@link GridFilters#addFilter(Filter)} and invoke
 * {@link GridFilters#initPlugin(Grid)}. The filter then appears in the grid
 * header menu item for any column that uses the supplied value provider.
 * <p/>
 * Derived classes must provide an implementation of {@link #getStore()}.
 * 
 * @param <M> the model type
 */
public abstract class AbstractGridFilters<M> implements ComponentPlugin<Grid<M>> {

  /**
   * The default locale-sensitive messages used by this class.
   */
  public class DefaultGridFilterMessages implements GridFilterMessages {

    @Override
    public String filterText() {
      return DefaultMessages.getMessages().gridFilters_filterText();
    }

  }

  /**
   * The locale-sensitive messages used by this class.
   */
  public static interface GridFilterMessages {

    String filterText();
  }

  private class Handler implements UpdateHandler, ActivateHandler<Filter<M, ?>>, DeactivateHandler<Filter<M, ?>> {

    @Override
    public void onActivate(ActivateEvent<Filter<M, ?>> event) {
      onStateChange(event.getItem());
    }

    @Override
    public void onDeactivate(DeactivateEvent<Filter<M, ?>> event) {
      onStateChange(event.getItem());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onUpdate(UpdateEvent event) {
      onStateChange((Filter<M, ?>) event.getSource());
    }

  }

  @SuppressWarnings("rawtypes")
  private class LoaderHandler implements BeforeLoadHandler<FilterPagingLoadConfig>, LoadHandler {

    @Override
    public void onBeforeLoad(BeforeLoadEvent<FilterPagingLoadConfig> event) {
      handleBeforeLoad(event);
    }

    @Override
    public void onLoad(LoadEvent event) {
      handleLoad(event);
    }

  }

  private ColumnHiddenChangeHandler columnHandler = new ColumnHiddenChangeHandler() {

    @Override
    public void onColumnHiddenChange(ColumnHiddenChangeEvent event) {
      updateColumnHeadings();
    }
  };

  protected ColumnModel<M> columnModel;
  protected StoreFilter<M> currentFilter;
  protected Grid<M> grid;
  protected Store<M> store;

  protected Loader<FilterPagingLoadConfig, ?> loader;
  private boolean autoReload = true;
  private CheckMenuItem checkFilterItem;

  private DelayedTask deferredUpdate = new DelayedTask() {

    @Override
    public void onExecute() {
      reload();
    }
  };

  private Map<String, Filter<M, ?>> filters;
  private Map<Filter<M, ?>, HandlerRegistration> registrations;
  private HandlerRegistration columnHandlerRegistration;

  private Menu filterMenu;
  private String filterStyle = "x-filtered-column";
  private Handler handler = new Handler();
  private LoaderHandler loadHandler = new LoaderHandler();
  private boolean local = false;
  private GridFilterMessages messages;
  private SeparatorMenuItem separatorItem;
  private int updateBuffer = 500;

  /**
   * Creates grid filters that are applied locally. Caller must also invoke
   * <code>setLocal(true)</code>.
   */
  public AbstractGridFilters() {
    filters = new HashMap<String, Filter<M, ?>>();
    registrations = new HashMap<Filter<M, ?>, HandlerRegistration>();
  }

  /**
   * Creates grid filters to be applied remotely. The grid filters are
   * automatically added to the {@link FilterPagingLoadConfig} during the
   * loading processing and passed by the {@link Loader} to the
   * {@link DataProxy} so that they are available for the remote data source to
   * use as needed.
   * 
   * @param loader the remote loader
   */
  @SuppressWarnings("unchecked")
  public AbstractGridFilters(Loader<FilterPagingLoadConfig, ?> loader) {
    this();
    this.loader = loader;

    loader.addBeforeLoadHandler(loadHandler);
    loader.addLoadHandler(loadHandler);
  }

  /**
   * Adds a filter.
   * 
   * @param filter the filter
   */
  public void addFilter(Filter<M, ?> filter) {
    filters.put(filter.getValueProvider().getPath(), filter);

    GroupingHandlerRegistration r = new GroupingHandlerRegistration();
    r.add(filter.addUpdateHandler(handler));
    r.add(filter.addActivateHandler(handler));
    r.add(filter.addDeactivateHandler(handler));
    registrations.put(filter, r);
  }

  /**
   * Builds a query consisting of a list of loader filter configurations from a
   * list of grid filters.
   * 
   * @param filters
   * @return a query consisting of a list of loader filter configurations
   */
  public List<FilterConfig> buildQuery(List<Filter<M, ?>> filters) {
    List<FilterConfig> configs = new ArrayList<FilterConfig>();
    for (Filter<M, ?> f : filters) {
      List<FilterConfig> temp = f.getFilterConfig();
      for (FilterConfig tempConfig : temp) {
        tempConfig.setField(f.getValueProvider().getPath());
        configs.add(tempConfig);
      }
    }
    return configs;
  }

  /**
   * Removes filter related query parameters from the provided object.
   * 
   * @param config the load config
   */
  public void cleanParams(FilterPagingLoadConfig config) {
    config.setFilters(new ArrayList<FilterConfig>());
  }

  /**
   * Turns all filters off. This does not clear the configuration information
   * (see {@link #removeAll}).
   */
  public void clearFilters() {
    for (Filter<M, ?> f : filters.values()) {
      f.setActive(false, false);
    }
  }

  /**
   * Returns the filter based on the value provider path.
   * 
   * @param path the path
   * @return the matching filter or null
   */
  public Filter<M, ?> getFilter(String path) {
    return filters.get(path);
  }

  /**
   * Returns a list of the currently active filters.
   * 
   * @return the list of active filters
   */
  public List<Filter<M, ?>> getFilterData() {
    List<Filter<M, ?>> configs = new ArrayList<Filter<M, ?>>();
    for (Filter<M, ?> f : filters.values()) {
      if (f.isActive()) {
        configs.add(f);
      }
    }
    return configs;
  }

  /**
   * Returns the locale-sensitive messages used by this class.
   * 
   * @return the locale-sensitive messages used by this class
   */
  public GridFilterMessages getMessages() {
    if (messages == null) {
      messages = new DefaultGridFilterMessages();
    }
    return messages;
  }

  @Override
  public void initPlugin(Grid<M> component) {
    this.grid = component;

    grid.addHeaderContextMenuHandler(new HeaderContextMenuHandler() {

      @Override
      public void onHeaderContextMenu(HeaderContextMenuEvent event) {
        onContextMenu(event);
      }
    });

    grid.addReconfigureHandler(new ReconfigureHandler() {

      @Override
      public void onReconfigure(ReconfigureEvent event) {

      }
    });

    bindStore(getStore());
    bindColumnModel(grid.getColumnModel());
  }

  /**
   * Returns true if auto load is enabled.
   * 
   * @return the auto load state
   */
  public boolean isAutoReload() {
    return autoReload;
  }

  /**
   * Removes all filters.
   */
  public void removeAll() {
    List<Filter<M, ?>> temp = new ArrayList<Filter<M, ?>>(filters.values());
    for (Filter<M, ?> f : temp) {
      removeFilter(f);
    }
  }

  /**
   * Removes the given filter.
   * 
   * @param filter the filter to be removed
   */
  public void removeFilter(Filter<M, ?> filter) {
    filters.remove(filter.getValueProvider().getPath());

    HandlerRegistration r = registrations.get(filter);
    if (r != null) {
      r.removeHandler();
    }
  }

  /**
   * Tree to reload the datasource when a filter change happens (defaults to
   * true). Set this to false to prevent the datastore from being reloaded if
   * there are changes to the filters.
   * 
   * @param autoLoad true to enable auto reload
   */
  public void setAutoReload(boolean autoLoad) {
    this.autoReload = autoLoad;
  }

  /**
   * Number of milliseconds to defer store updates since the last filter change
   * (defaults to 500).
   * 
   * @param updateBuffer the buffer in milliseconds
   */
  public void setUpdateBuffer(int updateBuffer) {
    this.updateBuffer = updateBuffer;
  }

  @SuppressWarnings("rawtypes")
  public void updateColumnHeadings() {
    int cols = grid.getColumnModel().getColumnCount();
    for (int i = 0; i < cols; i++) {
      ColumnConfig<M, ?> config = grid.getColumnModel().getColumn(i);
      if (!config.isHidden()) {
        ColumnHeader<M> header = grid.getView().getHeader();
        if (header != null) {
          Head h = header.getHead(i);
          if (h != null && h.isRendered()) {
            Filter<M, ?> f = getFilter(config.getValueProvider().getPath());
            if (f != null) {
              h.getElement().setClassName(filterStyle, f.isActive());
            }
          }
        }
      }
    }
  }

  protected void bindColumnModel(ColumnModel<M> columnModel) {
    if (this.columnModel != null) {
      columnHandlerRegistration = this.columnModel.addColumnHiddenChangeHandler(columnHandler);
    }
    if (columnModel != null && columnHandlerRegistration != null) {
      columnHandlerRegistration.removeHandler();
    }
    this.columnModel = columnModel;

  }

  protected void bindStore(Store<M> store) {
    this.store = store;
  }

  protected Filter<M, ?> getMenuFilter(CheckChangeEvent<CheckMenuItem> event) {
    CheckMenuItem item = event.getItem();
    ColumnConfig<M, ?> config = grid.getColumnModel().getColumn((Integer) item.getData("index"));
    return getFilter(config.getPath());
  }

  protected StoreFilter<M> getModelFilter() {
    StoreFilter<M> storeFilter = new StoreFilter<M>() {
      @Override
      public boolean select(Store<M> store, M parent, M item) {
        for (Filter<M, ?> f : filters.values()) {
          if (f.isActivatable() && f.isActive() && !f.validateModel(item)) {
            return false;
          }
        }
        return true;
      }
    };
    return storeFilter;
  }

  /**
   * Returns the store used by the grid filters.
   * 
   * @return the store used by the grid filters
   */
  protected abstract Store<M> getStore();

  protected void handleBeforeLoad(BeforeLoadEvent<FilterPagingLoadConfig> event) {
    FilterPagingLoadConfig config = event.getLoadConfig();
    cleanParams(config);
    List<FilterConfig> filterConfigs = buildQuery(getFilterData());
    config.setFilters(filterConfigs);
  }

  @SuppressWarnings("rawtypes")
  protected void handleLoad(LoadEvent event) {

  }

  /**
   * Returns true if the grid filters are to be applied locally.
   * 
   * @return true if the grid filters are to be applied locally
   */
  protected boolean isLocal() {
    return local;
  }

  protected void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
    getMenuFilter(event).setActive(event.getItem().isChecked(), false);
  }

  protected void onContextMenu(HeaderContextMenuEvent event) {
    int column = event.getColumnIndex();

    if (separatorItem == null) {
      separatorItem = new SeparatorMenuItem();
    }
    separatorItem.removeFromParent();

    if (checkFilterItem == null) {
      checkFilterItem = new CheckMenuItem(DefaultMessages.getMessages().gridFilters_filterText());
      checkFilterItem.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {

        @Override
        public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
          AbstractGridFilters.this.onCheckChange(event);
        }
      });
    }

    checkFilterItem.setData("index", column);

    Filter<M, ?> f = getFilter(grid.getColumnModel().getColumn(column).getValueProvider().getPath());
    if (f != null) {
      filterMenu = f.getMenu();
      checkFilterItem.setChecked(f.isActive(), true);
      checkFilterItem.setSubMenu(filterMenu);

      Menu menu = event.getMenu();
      menu.add(separatorItem);
      menu.add(checkFilterItem);
    }
  }

  protected void onStateChange(Filter<M, ?> filter) {
    if (checkFilterItem != null && checkFilterItem.isAttached()) {
      checkFilterItem.setChecked(filter.isActive(), true);
    }
    if ((autoReload || local)) {
      deferredUpdate.delay(updateBuffer);
    }
    updateColumnHeadings();
  }

  protected void reload() {
    if (local) {
      if (currentFilter != null) {
        store.removeFilter(currentFilter);
      }
      currentFilter = getModelFilter();
      store.addFilter(currentFilter);
      if (!store.isFiltered()) {
        store.setEnableFilters(true);
      }
    } else {
      deferredUpdate.cancel();

      if (loader != null) {
        loader.load();
      }
    }
  }

  /**
   * True to use Store filter functions (local filtering) instead of the default
   * server side filtering (defaults to false).
   * 
   * @param local true for local
   */
  protected void setLocal(boolean local) {
    this.local = local;
  }

}
