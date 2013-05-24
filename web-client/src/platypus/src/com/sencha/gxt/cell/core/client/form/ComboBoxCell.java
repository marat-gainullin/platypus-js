/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client.form;

import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.cell.core.client.LabelProviderSafeHtmlCell;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.core.client.util.BaseEventPreview;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.core.client.util.KeyNav;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent.StoreDataChangeHandler;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent.StoreUpdateHandler;
import com.sencha.gxt.data.shared.loader.Loader;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.cell.HandlerManagerContext;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeQueryEvent;
import com.sencha.gxt.widget.core.client.event.CellBeforeSelectionEvent;
import com.sencha.gxt.widget.core.client.event.CellSelectionEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.XEvent;
import com.sencha.gxt.widget.core.client.form.PropertyEditor;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;

public class ComboBoxCell<T> extends TriggerFieldCell<T> implements HasBeforeSelectionHandlers<T>,
    HasSelectionHandlers<T> {

  public enum QueryMode {
    REMOTE, LOCAL
  }

  /**
   * TriggerAction enum.
   */
  public enum TriggerAction {
    ALL, QUERY;
  }

  private class ComboPropertyEditor extends PropertyEditor<T> {

    @Override
    public T parse(CharSequence text) throws ParseException {
      return selectByValue(text == null ? "" : text.toString());
    }

    @Override
    public String render(T object) {
      if (object == null) {
        return "";
      }
      LabelProvider<? super T> provider = getLabelProvider();
      return provider == null ? object.toString() : provider.getLabel(object);
    }

  }
  private class Handler implements StoreDataChangeHandler<T>, StoreUpdateHandler<T> {

    @Override
    public void onDataChange(StoreDataChangeEvent<T> event) {
      if (lastContext != null) {
        onResultsLoad(lastContext, lastParent, lastValueUpdater, lastValue);
      }
    }

    @Override
    public void onUpdate(StoreUpdateEvent<T> event) {
      onStoreUpdate(event);
    }
  }

  protected String lastQuery;
  protected ListStore<T> store;
  protected Loader<?, ?> loader;
  protected int pageSize;
  protected PagingToolBar pagingToolBar;
  private final LabelProvider<? super T> labelProvider;
  private TriggerAction triggerAction = TriggerAction.QUERY;
  private boolean expanded;
  private String allQuery = "";
  private VerticalLayoutContainer listContainer;
  private ListView<T, ?> listView;
  private QueryMode mode = QueryMode.LOCAL;
  private int minChars = 0;
  private boolean useQueryCache = true;
  private T selectedItem;
  private int minListWidth = 70;
  private int maxHeight = 300;
  private boolean typeAhead;
  private int typeAheadDelay = 250;
  private StoreFilter<T> currentFilter;
  private BaseEventPreview eventPreview;
  private boolean forceSelection;
  private DelayedTask taTask, dqTask;
  private T lastSelectedValue;
  private int queryDelay = 500;
  private GroupingHandlerRegistration storeRegistration;
  private Handler storeHandler = new Handler();
  private boolean ignoreNextEnter;

  private static Logger logger = Logger.getLogger(ComboBoxCell.class.getName());

  /**
   * Creates a combo box cell that renders all items with the given label
   * provider.
   * 
   * @param store the store containing the data that can be selected
   * @param labelProvider converts the current model type into a string value to
   *          display in the text box
   */
  public ComboBoxCell(ListStore<T> store, LabelProvider<? super T> labelProvider) {
    this(store, labelProvider, GWT.<TriggerFieldAppearance> create(TriggerFieldAppearance.class));
  }

  /**
   * Creates a combo box cell that renders the input value with the label
   * provider.
   * 
   * @param store the store containing the data that can be selected
   * @param labelProvider converts the current model type into a string value to
   *          display in the text box
   * @param view the list view
   */
  public ComboBoxCell(ListStore<T> store, LabelProvider<? super T> labelProvider, ListView<T, ?> view) {
    this(store, labelProvider, view, GWT.<TriggerFieldAppearance> create(TriggerFieldAppearance.class));
  }

  /**
   * Creates a combo box cell that renders the input value with the label
   * provider.
   * 
   * @param store the store containing the data that can be selected
   * @param labelProvider converts the current model type into a string value to
   *          display in the text box
   * @param view the list view
   * @param appearance the appearance
   */
  public ComboBoxCell(ListStore<T> store, LabelProvider<? super T> labelProvider, ListView<T, ?> view,
      TriggerFieldAppearance appearance) {
    super(appearance);
    this.labelProvider = labelProvider;

    setPropertyEditor(new ComboPropertyEditor());

    initView(view);
    init(store);
  }

  /**
   * Creates a combo box cell that renders the input value with the label
   * provider and the drop down values with the renderer.
   * 
   * @param store the store containing the data that can be selected
   * @param labelProvider converts the current model type into a string value to
   *          display in the text box
   * @param renderer draws the current model as html in the drop down
   */
  public ComboBoxCell(ListStore<T> store, LabelProvider<? super T> labelProvider, final SafeHtmlRenderer<T> renderer) {
    this(store, labelProvider, renderer, GWT.<TriggerFieldAppearance> create(TriggerFieldAppearance.class));
  }

  /**
   * Creates a combo box cell that renders the input value with the label
   * provider and the drop down values with the renderer.
   * 
   * @param store the store containing the data that can be selected
   * @param labelProvider converts the current model type into a string value to
   *          display in the text box
   * @param renderer draws the current model as html in the drop down
   * @param appearance the appearance
   */
  public ComboBoxCell(ListStore<T> store, LabelProvider<? super T> labelProvider, final SafeHtmlRenderer<T> renderer,
      TriggerFieldAppearance appearance) {
    super(appearance);
    this.labelProvider = labelProvider;

    ListView<T, T> listView = new ListView<T, T>(store, new IdentityValueProvider<T>());
    listView.setCell(new SimpleSafeHtmlCell<T>(renderer));

    setPropertyEditor(new ComboPropertyEditor());

    initView(listView);
    init(store);
  }

  /**
   * Creates a combo box cell that renders both the input value and drop down
   * values with the given label provider.
   * 
   * @param store the store containing the data that can be selected
   * @param labelProvider converts the current model type into a string value to
   *          display in the text box and the drop down values
   * @param appearance the appearance
   */
  public ComboBoxCell(ListStore<T> store, LabelProvider<? super T> labelProvider, TriggerFieldAppearance appearance) {
    super(appearance);
    this.labelProvider = labelProvider;

    ListView<T, T> listView = new ListView<T, T>(store, new IdentityValueProvider<T>());
    listView.setCell(new LabelProviderSafeHtmlCell<T>(labelProvider));

    setPropertyEditor(new ComboPropertyEditor());

    initView(listView);
    init(store);
  }

  /**
   * Adds a {@link BeforeSelectionEvent} handler. The handler will be passed an
   * instance of {@link CellBeforeSelectionEvent} which can be cast to.
   * 
   * @param handler the handler
   * @return the registration for the event
   */
  @Override
  public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<T> handler) {
    return addHandler(handler, BeforeSelectionEvent.getType());
  }

  /**
   * Adds a {@link SelectionEvent} handler. The handler will be passed an
   * instance of {@link CellSelectionEvent} which can be cast to.
   * 
   * @param handler the handler
   * @return the registration for the event
   */
  @Override
  public HandlerRegistration addSelectionHandler(SelectionHandler<T> handler) {
    return addHandler(handler, SelectionEvent.getType());
  }

  public void collapse(Context context, final XElement parent) {
    if (!expanded) {
      return;
    }

    eventPreview.remove();
    expanded = false;

    listView.getSelectionModel().deselectAll();

    RootPanel.get().remove(listContainer);
    fireEvent(context, new CollapseEvent());
  }

  public void doQuery(Context context, XElement parent, ValueUpdater<T> updater, T value, String query, boolean force) {
    if (query == null) {
      query = "";
    }

    // fire before query
    BeforeQueryEvent<T> event = new BeforeQueryEvent<T>(query);
    fireEvent(context, event);

    if (event.isCancelled()) {
      return;
    }

    query = event.getQuery();

    if (force || query.length() >= minChars) {
      if (!useQueryCache || !query.equals(lastQuery)) {
        lastQuery = query;
        if (mode == QueryMode.LOCAL) {
          selectedItem = null;

          // get rid of other filters, in anticipation of adding the new one -
          // this will not cause the filters to be re-applied
          listView.getStore().setEnableFilters(false);
          if (currentFilter != null) {
            listView.getStore().getFilters().remove(currentFilter);
          }

          final String fq = query;

          // add a new filter
          currentFilter = new StoreFilter<T>() {
            public boolean select(Store<T> store, T parent, T item) {
              return itemMatchesQuery(item, fq);
            }
          };
          listView.getStore().addFilter(currentFilter);
          listView.getStore().setEnableFilters(true);

          onResultsLoad(context, parent, updater, value);
        } else {
          expand(context, parent, updater, value);
          @SuppressWarnings("unchecked")
          PagingLoader<PagingLoadConfig, PagingLoadResult<?>> l = (PagingLoader<PagingLoadConfig, PagingLoadResult<?>>) loader;
          l.load(getParams(query));
        }
      } else {
        selectedItem = null;
        onResultsLoad(context, parent, updater, value);
      }
    }
  }

  public void expand(Context context, final XElement parent, ValueUpdater<T> updater, T value) {
    if (expanded) {
      return;
    }

    // expand may be called without the cell being focused
    // saveContext sets focusedCell so we clear if cell
    // not currently focused
    boolean focused = focusedCell != null;
    saveContext(context, parent, null, updater, value);
    if (!focused) {
      focusedCell = null;
    }

    RootPanel.get().add(listContainer);
    listContainer.getElement().updateZIndex(0);

    eventPreview.add();
    expanded = true;
    restrict(parent);

    Scheduler.get().scheduleDeferred(new ScheduledCommand() {

      @Override
      public void execute() {
        restrict(parent);
      }
    });

    fireEvent(context, new ExpandEvent());
  }

  /**
   * Returns the all query.
   * 
   * @return the all query
   */
  public String getAllQuery() {
    return allQuery;
  }

  /**
   * Returns the label provider.
   * 
   * @return the label provider
   */
  public LabelProvider<? super T> getLabelProvider() {
    return labelProvider;
  }

  /**
   * Returns the list view.
   * 
   * @return the list view
   */
  public ListView<T, ?> getListView() {
    return listView;
  }

  public Loader<?, ?> getLoader() {
    return loader;
  }

  /**
   * Returns the min characters used for autocompete and typeahead.
   * 
   * @return the minimum number of characters
   */
  public int getMinChars() {
    return minChars;
  }

  /**
   * Returns the dropdown list's min width.
   * 
   * @return the min width
   */
  public int getMinListWidth() {
    return minListWidth;
  }

  public QueryMode getMode() {
    return mode;
  }

  /**
   * Returns the page size.
   * 
   * @return the page size
   */
  public int getPageSize() {
    return pageSize;
  }

  /**
   * Returns the combo's paging tool bar.
   * 
   * @return the tool bar
   */
  public PagingToolBar getPagingToolBar() {
    return pagingToolBar;
  }

  /**
   * Returns the combo's list store.
   * 
   * @return the store
   */
  public ListStore<T> getStore() {
    return store;
  }

  /**
   * Returns the trigger action.
   * 
   * @return the trigger action
   */
  public TriggerAction getTriggerAction() {
    return triggerAction;
  }

  /**
   * Returns the type ahead delay in milliseconds.
   * 
   * @return the type ahead delay
   */
  public int getTypeAheadDelay() {
    return typeAheadDelay;
  }

  /**
   * Returns the length of time in milliseconds to delay between the start of
   * typing and sending the query to filter the dropdown list.
   * 
   * @return the query delay
   */
  public int getQueryDelay() {
    return queryDelay;
  }

  /**
   * Returns <code>true</code> if the dropdown is expanded.
   * 
   * @return the expand state
   */
  public boolean isExpanded() {
    return expanded;
  }

  /**
   * Returns true if the field's value is forced to one of the value in the
   * list.
   * 
   * @return the force selection state
   */
  public boolean isForceSelection() {
    return forceSelection;
  }

  /**
   * Returns true if type ahead is enabled.
   * 
   * @return the type ahead state
   */
  public boolean isTypeAhead() {
    return typeAhead;
  }

  public void onBrowserEvent(Cell.Context context, Element parent, T value, NativeEvent event,
      ValueUpdater<T> valueUpdater) {
    
    Element target = event.getEventTarget().cast();
    if (!parent.isOrHasChild(target)) {
      return;
    }
    super.onBrowserEvent(context, parent, value, event, valueUpdater);
  }

  @Override
  public void render(Context context, T value, SafeHtmlBuilder sb) {
    String v = "";

    if (value != null) {
      v = getPropertyEditor().render(value);
    }

    FieldViewData viewData = checkViewData(context, v);
    String s = (viewData != null) ? viewData.getCurrentValue() : v;

    FieldAppearanceOptions options = new FieldAppearanceOptions(width, height, isReadOnly());
    options.setName(name);
    options.setEmptyText(getEmptyText());
    options.setHideTrigger(isHideTrigger());
    options.setEditable(isEditable());
    options.setDisabled(isDisabled());

    appearance.render(sb, s == null ? "" : s, options);
  }

  /**
   * Select an item in the dropdown list by its numeric index in the list. This
   * function does NOT cause the select event to fire. The list must expanded
   * for this function to work, otherwise use #setValue.
   * 
   * @param index the index of the item to select
   */
  public void select(int index) {
    if (index != -1 && index < getListView().getElements().size()) {
      T item = getStore().get(index);
      selectedItem = item;
      getListView().getSelectionModel().select(item, false);
      getListView().getElement(index).scrollIntoView();
    }
  }

  /**
   * Select an item in the dropdown list. This function does NOT cause the
   * select event to fire. The list must expanded for this function to work,
   * otherwise use #setValue.
   * 
   * @param item the item to select
   */
  public void select(T item) {
    if (item != null) {
      selectedItem = item;
      int index = getStore().indexOf(item);
      if (index != -1) {
        select(index);
      }
    }
  }

  /**
   * The text query to send to the server to return all records for the list
   * with no filtering (defaults to '').
   * 
   * @param allQuery the all query
   */
  public void setAllQuery(String allQuery) {
    this.allQuery = allQuery;
  }

  /**
   * Sets whether the combo's value is restricted to one of the values in the
   * list, false to allow the user to set arbitrary text into the field
   * (defaults to false).
   * 
   * @param forceSelection true to force selection
   */
  public void setForceSelection(boolean forceSelection) {
    this.forceSelection = forceSelection;
  }

  /**
   * Sets the loader for use with remote queries.
   * 
   * @param loader the loader
   */
  public void setLoader(Loader<?, ?> loader) {
    setMode(loader == null ? QueryMode.LOCAL : QueryMode.REMOTE);
    this.loader = loader;
  }

  /**
   * Sets the minimum number of characters the user must type before
   * autocomplete and typeahead active (defaults to 4 if remote, or 0 if local).
   * 
   * @param minChars minimum number of characters before activating autocomplete
   *          and typeahead
   */
  public void setMinChars(int minChars) {
    this.minChars = minChars;
  }

  /**
   * Sets the minimum width of the dropdown list in pixels (defaults to 70, will
   * be ignored if listWidth has a higher value).
   * 
   * @param minListWidth the min width
   */
  public void setMinListWidth(int minListWidth) {
    this.minListWidth = minListWidth;
  }

  /**
   * Sets the page size. Only applies when using a paging toolbar.
   * 
   * @param pageSize the page size
   */
  @SuppressWarnings({"unchecked"})
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
    if (pageSize > 0) {
      if (pagingToolBar != null) {
        pagingToolBar.setPageSize(pageSize);
      } else {
        pagingToolBar = createPagingToolBar(pageSize);
        pagingToolBar.addStyleName(ThemeStyles.getStyle().borderTop());
        pagingToolBar.bind((PagingLoader<PagingLoadConfig, ?>) loader);
        listContainer.add(pagingToolBar, new VerticalLayoutData(1, -1));
        listContainer.setShadow(false);
      }
    } else {
      pagingToolBar = null;
    }
  }

  /**
   * Sets the combo's store.
   * 
   * @param store the store
   */
  public void setStore(ListStore<T> store) {
    bindStore(store);
  }

  public void setTriggerAction(TriggerAction triggerAction) {
    this.triggerAction = triggerAction;
  }

  public void setTypeAhead(boolean typeAhead) {
    this.typeAhead = typeAhead;
    if (typeAhead && taTask == null) {
      taTask = new DelayedTask() {
        @Override
        public void onExecute() {
          onTypeAhead(lastParent);
        }
      };
    } else if (!typeAhead && taTask != null) {
      taTask.cancel();
      taTask = null;
    }
  }

  public void setTypeAheadDelay(int typeAheadDelay) {
    this.typeAheadDelay = typeAheadDelay;
  }

  /**
   * The length of time in milliseconds to delay between the start of typing and
   * sending the query to filter the dropdown list.
   * 
   * @param queryDelay the query delay
   */
  public void setQueryDelay(int queryDelay) {
    this.queryDelay = queryDelay;
  }

  protected void bindStore(ListStore<T> store) {
    if (this.store != null) {
      storeRegistration.removeHandler();
      this.store = null;
      if (listView != null) {
        listView.setStore(null);
      }
    }
    if (store != null) {
      this.store = store;
      if (listView != null) {
        listView.setStore(store);
      }
      if (storeRegistration == null) {
        storeRegistration = new GroupingHandlerRegistration();
      }
      storeRegistration.add(store.addStoreDataChangeHandler(storeHandler));
      storeRegistration.add(store.addStoreUpdateHandler(storeHandler));
    }
  }

  protected void collapseIf(NativePreviewEvent pe) {
    Element target = pe.getNativeEvent().getEventTarget().cast();
    if (!listContainer.getElement().isOrHasChild(target) && !lastParent.isOrHasChild(target)) {
      collapse(lastContext, lastParent);
    }
    // if (!list.el().isOrHasChild(pe.getTarget()) &&
    // !el().isOrHasChild(pe.getTarget())) {
    // collapse();
    // }
  }

  protected PagingToolBar createPagingToolBar(int pageSize) {
    return new PagingToolBar(pageSize);
  }

  protected void doForce(Context context, XElement parent, T value, ValueUpdater<T> valueUpdater) {
    if (forceSelection) {
      boolean f = forceSelection;
      forceSelection = false;
      String rv = getText(parent);
      if (isAllowBlank() && (rv == null || rv.equals(""))) {
        forceSelection = f;
        return;
      }

      T v = selectByValue(rv);

      if (v == null) {
        if (lastSelectedValue != null) {
          setText(parent, getRenderedValue(lastSelectedValue));
          valueUpdater.update(lastSelectedValue);
          if (getMode() == QueryMode.LOCAL) {
            // we need to filter the store here, so that the store only contains
            // the items needed

            // we adjust the lastQuery, so doLoad does not use an old value
            lastQuery = getPropertyEditor().render(lastSelectedValue);
            // store.filter(getDisplayField(), getRawValue());
          }

        } else {
          setText(parent, value != null ? getRenderedValue(value) : "");
          valueUpdater.update(value);
          applyEmptyText(context, parent);
        }
      }
      forceSelection = f;
    }
  }

  protected PagingLoadConfig getParams(String query) {
    PagingLoadConfig config = null;
    if (loader.isReuseLoadConfig()) {
      config = (PagingLoadConfig) loader.getLastLoadConfig();
    } else {
      config = new PagingLoadConfigBean();
    }
    config.setLimit(pageSize);
    config.setOffset(0);
    // config.set("query", query);
    return config;
  }

  protected void init(ListStore<T> store) {
    listContainer = new VerticalLayoutContainer();
    listContainer.getElement().makePositionable(true);
    listContainer.setBorders(true);
    listContainer.setShadow(true);
    listContainer.add(listView, new VerticalLayoutData(1, -1));

    bindStore(store);

    eventPreview = new BaseEventPreview() {
      protected boolean onPreview(NativePreviewEvent pe) {
        Element target = pe.getNativeEvent().getEventTarget().cast();

        switch (pe.getTypeInt()) {
          case Event.ONSCROLL:
          case Event.ONMOUSEWHEEL:
            collapseIf(pe);
            break;
          case Event.ONMOUSEDOWN:
            if (listView.getElement().isOrHasChild(target)) {
              if (pagingToolBar == null || (!pagingToolBar.getElement().isOrHasChild(target))) {
                onViewClick(lastParent, pe.getNativeEvent(), true, false);
              }
            } else {
              collapseIf(pe);
            }
            break;
        }

        NativeEvent e = pe.getNativeEvent();

        if (pe.getTypeInt() == KeyNav.getKeyEvent() && expanded) {
          if (e.getKeyCode() == KeyCodes.KEY_ENTER) {
            if (pagingToolBar != null && pagingToolBar.getElement().isOrHasChild(target)) {
              return true;
            }
            e.stopPropagation();
            e.preventDefault();

            if (GXT.isIE()) {
              ignoreNextEnter = true;
            }

            onViewClick(lastParent, e, false, true);
          }
        }
        return true;
      }
    };
    eventPreview.setAutoHide(false);

    dqTask = new DelayedTask() {

      @Override
      public void onExecute() {
        doQuery(lastContext, lastParent, lastValueUpdater, lastValue, getText(lastParent), false);
      }
    };

  }

  protected void initView(ListView<T, ?> listView) {
    this.listView = listView;

    // we add x-ignore to handle use case where combo is in a menu and auto hide
    // of the menu
    // needs to be prevented when clicking on the listview and scroll bars
    listView.addStyleName("x-ignore");

    listView.setBorders(false);
    listView.setSelectOnOver(true);
    listView.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<T>() {
      @Override
      public void onSelectionChanged(SelectionChangedEvent<T> event) {
        List<T> sel = event.getSelection();
        if (sel.size() > 0) {
          selectedItem = sel.get(0);
        } else {
          selectedItem = null;
        }

      }
    });

    listView.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
  }

  @Override
  protected boolean isFocusClick(XElement parent, XElement target) {
    boolean result = parent.isOrHasChild(target) || listView.getElement().isOrHasChild(target);
    if (!result && pagingToolBar != null) {
      if (pagingToolBar.getElement().isOrHasChild(target)) {
        return true;
      }
    }
    return result;
  }

  protected boolean itemMatchesQuery(T item, String query) {
    String value = getRenderedValue(item);
    if (value != null) {
      return value.toLowerCase().startsWith(query.toLowerCase());
    }
    return false;
  }

  protected void onEmptyResults(Context context, XElement parent) {
    collapse(context, parent);
  }

  protected void onEnterKeyDown(Context context, Element parent, T value, NativeEvent event,
      ValueUpdater<T> valueUpdater) {
    if (ignoreNextEnter) {
      ignoreNextEnter = false;
      return;
    }
    if (isExpanded() && GXT.isOpera()) {
      // Suppress blur on enter in ComboBox ListView drop-down
      return;
    }
    mimicking = false;
    super.onEnterKeyDown(context, parent, value, event, valueUpdater);
  }

  @Override
  protected void onKeyUp(Context context, Element parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater) {
    super.onKeyUp(context, parent, value, event, valueUpdater);

    int kc = event.getKeyCode();

    if (!isReadOnly() && isEditable()
        && (!event.<XEvent> cast().isSpecialKey() || kc == KeyCodes.KEY_BACKSPACE || kc == 46)) {
      dqTask.delay(getQueryDelay());
    }
  }

  @Override
  protected void onNavigationKey(Context context, Element parent, T value, NativeEvent event,
      ValueUpdater<T> valueUpdater) {

    if (isReadOnly()) {
      return;
    }

    switch (event.getKeyCode()) {
      case KeyCodes.KEY_DOWN:

        event.stopPropagation();
        event.preventDefault();
        if (!isExpanded()) {
          onTriggerClick(context, parent.<XElement> cast(), event, value, valueUpdater);
        } else {
          selectNext();
        }
        break;
      case KeyCodes.KEY_UP:
        if (isExpanded()) {
          event.stopPropagation();
          selectPrev();
        }
        break;
      case KeyCodes.KEY_ESCAPE:
        if (isExpanded()) {
          event.stopPropagation();
          collapse(lastContext, lastParent);
        }
        break;
      case KeyCodes.KEY_TAB:
        if (isExpanded()) {
          if (GXTLogConfiguration.loggingIsEnabled()) {
            logger.finest("onTab");
          }

          onViewClick(lastParent, event, false, true);
        }

        break;
    }

  }

  protected void onResultsLoad(Context context, XElement parent, ValueUpdater<T> updater, T value) {
    if (!hasFocus(context, parent)) {
      return;
    }

    if (listView.getStore().size() == 0) {
      onEmptyResults(context, parent);
    } else {

      if (expanded) {
        restrict(parent);
      } else {
        expand(context, parent, updater, value);
      }

      if (lastQuery != null && lastQuery.equals(allQuery)) {
        if (isEditable()) {
          selectAll(parent);
        }
      } else {
        if (typeAhead) {
          taTask.delay(typeAheadDelay);
        }
      }

      // select an element in the listview based on the current text
      if (selectByValue(getText(parent)) == null) {
        select(0);
      }
    }
  }

  protected void onSelect(T item) {
    FieldViewData viewData = ensureViewData(lastContext, lastParent);

    String rv = getRenderedValue(item);

    boolean cancelled = false;
    if (lastContext instanceof HandlerManagerContext) {
      HandlerManager manager = ((HandlerManagerContext) lastContext).getHandlerManager();
      CellBeforeSelectionEvent<T> event = CellBeforeSelectionEvent.fire(manager, lastContext, item);
      if (event != null && event.isCanceled()) {
        cancelled = true;
      }
    } else {
      CellBeforeSelectionEvent<T> event = CellBeforeSelectionEvent.fire(this, lastContext, item);
      if (event.isCanceled()) {
        cancelled = true;
      }
    }

    if (!cancelled) {
      this.lastSelectedValue = item;

      if (viewData != null) {
        viewData.setCurrentValue(rv);
      }
      getInputElement(lastParent).setValue(rv);

      if (lastContext instanceof HandlerManagerContext) {
        HandlerManager manager = ((HandlerManagerContext) lastContext).getHandlerManager();
        CellSelectionEvent.fire(manager, lastContext, item);
      } else {
        CellSelectionEvent.fire(this, lastContext, item);
      }
    }

    // collapsing non deferred causes trigger field mouse down preview
    // to think a focus click has occurred which causing the field to be
    // blurred after value changed
    if (GXT.isIE()) {
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {

        @Override
        public void execute() {
          collapse(lastContext, lastParent.<XElement> cast());
        }
      });

    } else {
      collapse(lastContext, lastParent.<XElement> cast());
    }

  }

  protected void onStoreUpdate(StoreUpdateEvent<T> event) {

  }

  @Override
  protected void onTriggerClick(Context context, XElement parent, NativeEvent event, T value, ValueUpdater<T> updater) {
    super.onTriggerClick(context, parent, event, value, updater);
    if (expanded) {
      collapse(context, parent);
    } else {
      onFocus(context, parent, value, event, updater);
      if (triggerAction == TriggerAction.ALL) {
        doQuery(context, parent, updater, value, allQuery, true);
      } else {
        doQuery(context, parent, updater, value, getText(parent.<XElement> cast()), true);
      }
    }
    getInputElement(parent).focus();
  }

  protected void onTypeAhead(XElement parent) {
    if (store.size() > 0) {
      T m = store.get(0);
      String newValue = getRenderedValue(m);
      int len = newValue.length();
      int selStart = getText(parent).length();
      if (selStart != len) {
        setText(parent, newValue);
        select(parent, selStart, newValue.length() - selStart);
      }
    }
  }

  protected void onViewClick(final XElement parent, NativeEvent event, boolean focus, boolean takeSelected) {
    int idx = -1;

    Element elem = listView.findElement((Element) event.getEventTarget().cast());
    if (elem != null) {
      idx = listView.indexOf(elem);
    } else if (elem == null && !takeSelected) {
      return;

    } else {
      T sel = listView.getSelectionModel().getSelectedItem();
      if (sel != null) {
        idx = store.indexOf(sel);
      }
    }
    if (idx != -1) {
      T sel = store.get(idx);
      onSelect(sel);
    }

    if (focus) {
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {

        @Override
        public void execute() {
          if (GXTLogConfiguration.loggingIsEnabled()) {
            logger.finest("onViewClick parent.focus()");
          }

          getInputElement(parent).focus();
        }
      });
    }

  }

  protected void restrict(XElement parent) {
    XElement wrapper = parent.getFirstChildElement().cast();
    int width = Math.max(wrapper.getWidth(true), minListWidth);

    listContainer.getElement().setVisibility(false);
    listView.setHeight("auto");
    listContainer.setHeight("auto");

    int fh = 0;
    int h = fh + listContainer.getOffsetHeight();
    int mH = Math.min(maxHeight, Window.getClientHeight() - 10);
    h = Math.min(h, mH);

    listContainer.getElement().makePositionable(true);
    listContainer.setPixelSize(width, h);
    listContainer.getElement().alignTo(wrapper, new AnchorAlignment(Anchor.TOP_LEFT, Anchor.BOTTOM_LEFT, true), null);
    listContainer.getElement().setVisibility(true);

    listView.setHeight(h - 2 - (pagingToolBar != null ? pagingToolBar.getOffsetHeight() : 0));

    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      @Override
      public void execute() {
        listContainer.sync(true);
      }
    });

  }

  protected T selectByValue(String value) {
    int count = store.size();
    for (int i = 0; i < count; i++) {
      T item = store.get(i);
      String v = getRenderedValue(item);
      if (v != null && v.equals(value)) {
        select(item);
        return item;
      }
    }
    return null;
  }

  protected void selectNext() {
    int count = getStore().size();
    if (count > 0) {
      int selectedIndex = getStore().indexOf(selectedItem);
      if (selectedIndex == -1) {
        select(0);
      } else if (selectedIndex < count - 1) {
        select(selectedIndex + 1);
      }
    }
  }

  protected void selectPrev() {
    int count = store.size();
    if (count > 0) {
      int selectedIndex = store.indexOf(selectedItem);
      if (selectedIndex == -1) {
        select(0);
      } else if (selectedIndex != 0) {
        select(selectedIndex - 1);
      }
    }
  }

  /**
   * Sets the query mode.
   * 
   * @param mode the query mode
   */
  protected void setMode(QueryMode mode) {
    this.mode = mode;
    if (mode == QueryMode.REMOTE) {
      minChars = 4;
    }
  }

  @Override
  protected void triggerBlur(Context context, XElement parent, T value, ValueUpdater<T> valueUpdater) {
    doForce(context, parent, value, valueUpdater);
    dqTask.cancel();
    collapse(context, parent);

    super.triggerBlur(context, parent, value, valueUpdater);
  }

  private String getRenderedValue(T item) {
    return getPropertyEditor().render(item);
  }

}
