/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.treegrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.data.shared.event.StoreHandlers;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreSortEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.data.shared.event.TreeStoreRemoveEvent;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.messages.client.DefaultMessages;
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
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.tree.Tree.Joint;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeAppearance;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;
import com.sencha.gxt.widget.core.client.tree.TreeStyle;

/**
 * A {@link TreeGrid} provides support for displaying and editing hierarchical
 * data where each item may contain multiple properties. The tree grid gets its
 * data from a {@link TreeStore} and its column definitions from a
 * {@link ColumnModel}. Each model in the store is rendered as an item in the
 * tree. The fields in the model provide the data for each column associated
 * with the item. Any updates to the store are automatically pushed to the tree
 * grid. This includes inserting, removing, sorting and filter.
 * <p/>
 * In GXT version 3, {@link ModelKeyProvider}s and {@link ValueProvider}s
 * provide the interface between your data model and the list store and
 * {@link ColumnConfig} classes. This enables a tree grid to work with data of
 * any object type.
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
 * Each tree grid has a {@link GridView}. The grid view provides many options
 * for customizing the grid's appearance (e.g. striping, mouse-over tracking,
 * empty text). To set these options, get the current grid view using
 * {@link TreeGrid#getView()} and then set the desired option on the grid view.
 * <p/>
 * To customize the appearance of a column in a tree grid, provide a cell
 * implementation using {@link ColumnConfig#setCell(Cell)}.
 * <p/>
 * Tree grids support several ways to manage column widths:
 * <ol>
 * <li>The most basic approach is to simply give pixel widths to each column.
 * Columns widths will match the specified values.</li>
 * <li>A column can be identified as an auto-expand column. As the width of the
 * tree grid changes, or columns are resized, the specified column's width is
 * adjusted so that the column fills the available width with no horizontal
 * scrolling. See @link {@link GridView#setAutoExpandColumn(ColumnConfig)}.</li>
 * <li>The tree grid can resize columns based on relative weights, determined by
 * the pixel width assigned to each column. As the width of the tree grid or
 * columns change, the weight is used to allocate the available space. Use
 * {@link GridView#setAutoFill(boolean)} or
 * {@link GridView#setForceFit(boolean)} to enable this feature:</li>
 * <ul>
 * <li>With auto fill, the calculations are run when the tree grid is created
 * (or reconfigured). After the tree grid is rendered, the column widths will
 * not be adjusted when the available width changes.</li>
 * <li>With force fit the width calculations are run every time there are
 * changes to the available width or column sizes.</li>
 * </ul>
 * <li>To prevent a column from participating in auto fill or force fit, use
 * {@link ColumnConfig#setFixed(boolean)}.</li>
 * </ol>
 * </p>
 * The following code snippet illustrates the creation of a simple tree grid
 * with local data for test purposes. For more practical examples that show how
 * to load data from remote sources, see the Async TreeGrid example in the
 * online Explorer demo.</p>
 * 
 * <pre>{@code 
    // Generate the key provider and value provider for the Data class
    DataProperties dp = GWT.create(DataProperties.class);

    // Create the configurations for each column in the tree grid
    List<ColumnConfig<Data, ?>> ccs = new LinkedList<ColumnConfig<Data, ?>>();
    ccs.add(new ColumnConfig<Data, String>(dp.name(), 200, "Name"));
    ccs.add(new ColumnConfig<Data, String>(dp.value(), 200, "Value"));
    ColumnModel<Data> cm = new ColumnModel<Test.Data>(ccs);

    // Create the store that the contains the data to display in the tree grid
    TreeStore<Data> s = new TreeStore<Test.Data>(dp.key());

    Data r1 = new Data("Parent 1", "value1");
    s.add(r1);
    s.add(r1, new Data("Child 1.1", "value2"));
    s.add(r1, new Data("Child 1.2", "value3"));

    Data r2 = new Data("Parent 2", "value4");
    s.add(r2);
    s.add(r2, new Data("Child 2.1", "value5"));
    s.add(r2, new Data("Child 2.2", "value6"));

    // Create the tree grid using the store, column model and column config for the tree column
    TreeGrid<Data> tg = new TreeGrid<Data>(s, cm, ccs.get(0));

    // Add the tree to a container
    RootPanel.get().add(tg);
 * }</pre>
 * <p/>
 * To use the Sencha supplied generator to create model key providers and value
 * providers, extend the <code>PropertyAccess</code> interface, parameterized
 * with the type of data you want to access (as shown below) and invoke the
 * <code>GWT.create</code> method on its <code>class</code> member (as shown in
 * the code snippet above). This creates an instance of the class that can be
 * used to initialize the tree and tree store. In the following code snippet we
 * define a new interface called <code>DataProperties</code> that extends the
 * <code>PropertyAccess</code> interface and is parameterized with
 * <code>Data</code>, a Plain Old Java Object (POJO).
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
 * To enable drag and drop for a tree grid, add the following:
 * <p/>
 * 
 * <pre>
    new TreeGridDragSource<Data>(tg);
    TreeGridDropTarget<Data> dt = new TreeGridDropTarget<Data>(tg);
    dt.setFeedback(Feedback.BOTH);
 * </pre>
 * <p/>
 * To add reordering support to the drag and drop, include:
 * 
 * <pre>
    dt.setAllowSelfAsSource(true);
 * </pre>
 * <p/>
 * 
 * @param <M> the model type
 */
public class TreeGrid<M> extends Grid<M> implements HasBeforeCollapseItemHandlers<M>, HasCollapseItemHandlers<M>,
    HasBeforeExpandItemHandlers<M>, HasExpandItemHandlers<M> {

  public static class TreeGridNode<M> extends TreeNode<M> {

    protected TreeGridNode(String id, M m) {
      super(id, m);
    }

    @Override
    public void clearElements() {
      super.clearElements();
      setContainerElement(null);
      setElContainer(null);
      element = null;
    }

  }

  protected boolean filtering;
  protected TreeLoader<M> loader;
  protected Map<String, TreeNode<M>> nodes = new FastMap<TreeNode<M>>();
  protected StoreHandlers<M> storeHandler = new StoreHandlers<M>() {

    @Override
    public void onAdd(StoreAddEvent<M> event) {
      TreeGrid.this.onAdd(event);

    }

    @Override
    public void onClear(StoreClearEvent<M> event) {
      TreeGrid.this.onClear(event);

    }

    @Override
    public void onDataChange(StoreDataChangeEvent<M> event) {
      TreeGrid.this.onDataChange(event.getParent());

    }

    @Override
    public void onFilter(StoreFilterEvent<M> event) {
      TreeGrid.this.onFilter(event);

    }

    @Override
    public void onRecordChange(StoreRecordChangeEvent<M> event) {
      TreeGrid.this.onRecordChange(event);

    }

    @Override
    public void onRemove(StoreRemoveEvent<M> event) {
      TreeGrid.this.onRemove((TreeStoreRemoveEvent<M>) event);

    }

    @Override
    public void onSort(StoreSortEvent<M> event) {
      TreeGrid.this.onSort(event);

    }

    @Override
    public void onUpdate(StoreUpdateEvent<M> event) {
      TreeGrid.this.onUpdate(event);

    }
  };

  protected HandlerRegistration storeHandlerRegistration;
  protected TreeGridView<M> treeGridView;
  protected TreeStore<M> treeStore;
  private GridAppearance appearance;

  private boolean autoLoad, autoExpand;
  private boolean caching = true;
  private boolean expandOnFilter = true;
  private IconProvider<M> iconProvider;
  private TreeStyle style = new TreeStyle();
  private TreeAppearance treeAppearance;
  private ColumnConfig<M, ?> treeColumn;

  /**
   * Creates a new tree grid.
   * 
   * @param store the tree store
   * @param cm the column model
   * @param treeColumn the tree column
   */
  public TreeGrid(TreeStore<M> store, ColumnModel<M> cm, ColumnConfig<M, ?> treeColumn) {
    this(store, cm, treeColumn, GWT.<GridAppearance> create(GridAppearance.class));
  }

  /**
   * Creates a new tree grid.
   * 
   * @param store the tree store
   * @param cm the column model
   * @param treeColumn the tree column
   * @param appearance the grid appearance
   */
  public TreeGrid(TreeStore<M> store, ColumnModel<M> cm, ColumnConfig<M, ?> treeColumn, GridAppearance appearance) {
    this(store, cm, treeColumn, appearance, GWT.<TreeAppearance> create(TreeAppearance.class));
  }

  /**
   * Creates a new tree grid.
   * 
   * @param store the tree store
   * @param cm the column model
   * @param treeColumn the tree column
   * @param appearance the grid appearance
   * @param treeAppearance the tree appearance
   */
  public TreeGrid(TreeStore<M> store, ColumnModel<M> cm, ColumnConfig<M, ?> treeColumn, GridAppearance appearance,
      TreeAppearance treeAppearance) {
    this.appearance = appearance;
    this.treeAppearance = treeAppearance;

    disabledStyle = null;

    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    this.appearance.render(builder);

    setElement(XDOM.create(builder.toSafeHtml()));
    getElement().makePositionable();

    // Do not remove, this is being used in Grid.css
    addStyleName("x-treegrid");

    getElement().setTabIndex(0);
    getElement().setAttribute("hideFocus", "true");

    this.cm = cm;
    setTreeColumn(treeColumn);
    this.treeStore = store;
    this.store = createListStore();

    setSelectionModel(new TreeGridSelectionModel<M>());

    disabledStyle = null;
    storeHandlerRegistration = treeStore.addStoreHandlers(storeHandler);

    setView(new TreeGridView<M>());
    setAllowTextSelection(false);

    sinkCellEvents();
  }

  @Override
  public HandlerRegistration addBeforeCollapseHandler(BeforeCollapseItemHandler<M> handler) {
    return addHandler(handler, BeforeCollapseItemEvent.getType());
  }

  @Override
  public HandlerRegistration addBeforeExpandHandler(BeforeExpandItemHandler<M> handler) {
    return addHandler(handler, BeforeExpandItemEvent.getType());
  }

  @Override
  public HandlerRegistration addCollapseHandler(CollapseItemHandler<M> handler) {
    return addHandler(handler, CollapseItemEvent.getType());
  }

  @Override
  public HandlerRegistration addExpandHandler(ExpandItemHandler<M> handler) {
    return addHandler(handler, ExpandItemEvent.getType());
  }

  /**
   * Collapses all nodes.
   */
  public void collapseAll() {
    for (M child : treeStore.getRootItems()) {
      setExpanded(child, false, true);
    }
  }

  /**
   * Expands all nodes.
   */
  public void expandAll() {
    for (M child : treeStore.getRootItems()) {
      setExpanded(child, true, true);
    }
  }

  /**
   * Returns the tree node for the given target.
   * 
   * @param target the target element
   * @return the tree node or null if no match
   */
  public TreeNode<M> findNode(Element target) {
    Element row = (Element) getView().findRow(target);
    if (row != null) {
      XElement item = XElement.as(row).selectNode(treeAppearance.itemSelector());
      if (item != null) {
        String id = item.getId();
        TreeNode<M> node = nodes.get(id);
        return node;
      }
    }
    return null;
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
   * Returns the model icon provider.
   * 
   * @return the icon provider
   */
  public IconProvider<M> getIconProvider() {
    return iconProvider;
  }

  /**
   * Returns the tree style.
   * 
   * @return the tree style
   */
  public TreeStyle getStyle() {
    return style;
  }

  /**
   * Returns the tree appearance.
   * 
   * @return the tree appearance
   */
  public TreeAppearance getTreeAppearance() {
    return treeAppearance;
  }

  /**
   * Returns the column that represents the tree nodes.
   * 
   * @return the tree column
   */
  public ColumnConfig<M, ?> getTreeColumn() {
    return treeColumn;
  }

  /**
   * Returns the tree loader.
   * 
   * @return the tree loader or null if not specified
   */
  public TreeLoader<M> getTreeLoader() {
    return loader;
  }

  /**
   * Returns the tree's tree store.
   * 
   * @return the tree store
   */
  public TreeStore<M> getTreeStore() {
    return treeStore;
  }

  /**
   * Returns the tree's view.
   * 
   * @return the view
   */
  public TreeGridView<M> getTreeView() {
    return treeGridView;
  }

  /**
   * Returns true if auto expand is enabled.
   * 
   * @return the auto expand state
   */
  public boolean isAutoExpand() {
    return autoExpand;
  }

  /**
   * Returns true if auto load is enabled.
   * 
   * @return the auto load state
   */
  public boolean isAutoLoad() {
    return autoLoad;
  }

  /**
   * Returns true when a loader is queried for it's children each time a node is
   * expanded. Only applies when using a loader with the tree store.
   * 
   * @return true if caching
   */
  public boolean isCaching() {
    return caching;
  }

  /**
   * Returns true if the model is expanded.
   * 
   * @param model the model
   * @return true if expanded
   */
  public boolean isExpanded(M model) {
    TreeNode<M> node = findNode(model);
    return node != null && node.isExpanded();
  }

  /**
   * Returns the if expand all and collapse all is enabled on filter changes.
   * 
   * @return the expand all collapse all state
   */
  public boolean isExpandOnFilter() {
    return expandOnFilter;
  }

  /**
   * Returns true if the model is a leaf node. The leaf state allows a tree item
   * to specify if it has children before the children have been realized.
   * 
   * @param model the model
   * @return the leaf state
   */
  public boolean isLeaf(M model) {
    return !hasChildren(model);
  }

  @Override
  public void reconfigure(ListStore<M> store, ColumnModel<M> cm) {
    throw new UnsupportedOperationException("Please call the other reconfigure method");
  }

  public void reconfigure(TreeStore<M> store, ColumnModel<M> cm, ColumnConfig<M, ?> treeColumn) {
    if (isLoadMask()) {
      mask(DefaultMessages.getMessages().loadMask_msg());
    }
    this.store.clear();

    nodes.clear();

    this.store = createListStore();

    if (storeHandlerRegistration != null) {
      storeHandlerRegistration.removeHandler();
    }

    treeStore = store;
    if (treeStore != null) {
      storeHandlerRegistration = treeStore.addStoreHandlers(storeHandler);
    }

    treeGridView.initData(this.store, cm);

    this.cm = cm;
    setTreeColumn(treeColumn);
    // rebind the sm
    setSelectionModel(sm);
    if (isViewReady()) {
      view.refresh(true);
      doInitialLoad();
    }

    if (isLoadMask()) {
      unmask();
    }
  }

  /**
   * Refreshes the data for the given model.
   * 
   * @param model the model to be refreshed
   */
  public void refresh(M model) {
    TreeNode<M> node = findNode(model);
    if (viewReady && node != null) {
      treeGridView.onIconStyleChange(node, calculateIconStyle(model));
      treeGridView.onJointChange(node, calculateJoint(model));
    }
  }

  /**
   * If set to true, all non leaf nodes will be expanded automatically (defaults
   * to false).
   * 
   * @param autoExpand the auto expand state to set.
   */
  public void setAutoExpand(boolean autoExpand) {
    this.autoExpand = autoExpand;
  }

  /**
   * Sets whether all children should automatically be loaded recursively
   * (defaults to false). Useful when the tree must be fully populated when
   * initially rendered.
   * 
   * @param autoLoad true to auto load
   */
  public void setAutoLoad(boolean autoLoad) {
    this.autoLoad = autoLoad;
  }

  /**
   * Sets whether the children should be cached after first being retrieved from
   * the store (defaults to true). When <code>false</code>, a load request will
   * be made each time a node is expanded.
   * 
   * @param caching the caching state
   */
  public void setCaching(boolean caching) {
    this.caching = caching;
  }

  /**
   * Sets the item's expand state.
   * 
   * @param model the model
   * @param expand true to expand
   */
  public void setExpanded(M model, boolean expand) {
    setExpanded(model, expand, false);
  }

  /**
   * Sets the item's expand state.
   * 
   * @param model the model
   * @param expand true to expand
   * @param deep true to expand all children recursively
   */
  public void setExpanded(M model, boolean expand, boolean deep) {
    TreeNode<M> node = findNode(model);
    if (node != null) {
      if (expand) {
        // make parents visible
        List<M> list = new ArrayList<M>();
        M p = model;
        while ((p = treeStore.getParent(p)) != null) {
          if (!findNode(p).isExpanded()) {
            list.add(p);
          }
        }
        for (int i = list.size() - 1; i >= 0; i--) {
          M item = list.get(i);
          setExpanded(item, expand, false);
        }
      }

      if (expand) {
        if (!isLeaf(model)) {
          // if we are loading, ignore it
          if (node.isLoading()) {
            return;
          }
          // if we have a loader and node is not loaded make
          // load request and exit method
          if (!node.isExpanded() && loader != null && (!node.isLoaded() || !caching) && !filtering) {
            treeStore.removeChildren(model);
            node.setExpand(true);
            node.setExpandDeep(deep);
            node.setLoading(true);
            treeGridView.onLoading(node);
            loader.loadChildren(model);
            return;
          }
          if (!node.isExpanded() && fireCancellableEvent(new BeforeExpandItemEvent<M>(model))) {
            node.setExpanded(true);

            if (!node.isChildrenRendered()) {
              renderChildren(model, false);
              node.setChildrenRendered(true);
            }
            // expand
            treeGridView.expand(node);
            fireEvent(new ExpandItemEvent<M>(model));
          }

          if (deep) {
            setExpandChildren(model, true);
          }
        }
      } else {
        if (node.isExpanded() && fireCancellableEvent(new BeforeCollapseItemEvent<M>(model))) {
          node.setExpanded(false);
          // collapse
          treeGridView.collapse(node);
          fireEvent(new CollapseItemEvent<M>(model));
        }
        if (deep) {
          setExpandChildren(model, false);
        }
      }
    }
  }

  /**
   * Sets whether the tree should expand all and collapse all when filters are
   * applied (defaults to true).
   * 
   * @param expandOnFilter true to expand and collapse on filter changes
   */
  public void setExpandOnFilter(boolean expandOnFilter) {
    this.expandOnFilter = expandOnFilter;
  }

  /**
   * Sets the tree's model icon provider which provides the icon style for each
   * model.
   * 
   * @param iconProvider the icon provider
   */
  public void setIconProvider(IconProvider<M> iconProvider) {
    this.iconProvider = iconProvider;
  }

  /**
   * Sets the item's leaf state. The leaf state allows control of the expand
   * icon before the children have been realized.
   * 
   * @param model the model
   * @param leaf the leaf state
   */
  public void setLeaf(M model, boolean leaf) {
    TreeNode<M> t = findNode(model);
    if (t != null) {
      t.setLeaf(leaf);
    }
  }

  /**
   * Sets the tree loader.
   * 
   * @param treeLoader the tree loader
   */
  public void setTreeLoader(TreeLoader<M> treeLoader) {
    this.loader = treeLoader;
  }

  @Override
  public void setView(GridView<M> view) {
    assert view instanceof TreeGridView : "The view for a TreeGrid has to be an instance of TreeGridView";
    super.setView(view);
    treeGridView = (TreeGridView<M>) view;
  }

  /**
   * Toggles the model's expand state.
   * 
   * @param model the model
   */
  public void toggle(M model) {
    TreeNode<M> node = findNode(model);
    if (node != null) {
      setExpanded(model, !node.isExpanded());
    }
  }

  protected ImageResource calculateIconStyle(M model) {
    ImageResource style = null;
    if (iconProvider != null) {
      ImageResource iconStyle = iconProvider.getIcon(model);
      if (iconStyle != null) {
        return iconStyle;
      }
    }
    TreeStyle ts = getStyle();
    if (!isLeaf(model)) {
      if (isExpanded(model)) {
        style = ts.getNodeOpenIcon() != null ? ts.getNodeOpenIcon() : treeAppearance.openNodeIcon();
      } else {
        style = ts.getNodeCloseIcon() != null ? ts.getNodeCloseIcon() : treeAppearance.closeNodeIcon();
      }
    } else {
      style = ts.getLeafIcon();
    }
    return style;
  }

  protected Joint calculateJoint(M model) {
    if (model == null) {
      return Joint.NONE;
    }
    TreeNode<M> node = findNode(model);
    Joint joint = Joint.NONE;
    if (node == null) {
      return joint;
    }
    if (!isLeaf(model)) {
      boolean children = true;

      if (node.isExpanded()) {
        joint = children ? Joint.EXPANDED : Joint.NONE;
      } else {
        joint = children ? Joint.COLLAPSED : Joint.NONE;
      }
    }
    return joint;
  }

  protected ListStore<M> createListStore() {
    return new ListStore<M>(treeStore.getKeyProvider()) {
      @Override
      public Record getRecord(M model) {
        return treeStore.getRecord(model);
      }

      @Override
      public boolean hasRecord(M model) {
        return treeStore.hasRecord(model);
      }
    };
  }

  protected int findLastOpenChildIndex(M model) {
    TreeNode<M> mark = findNode(model);
    M lc = model;
    while (mark != null && mark.isExpanded()) {
      M m = treeStore.getLastChild(mark.getModel());
      if (m != null) {
        lc = m;
        mark = findNode(lc);
      } else {
        break;
      }
    }
    return store.indexOf(lc);
  }

  protected TreeNode<M> findNode(M m) {
    if (m == null) return null;
    return nodes.get(generateModelId(m));
  }

  protected String generateModelId(M m) {
    return getId() + "_" + (treeStore.getKeyProvider().getKey(m));
  }

  protected boolean hasChildren(M model) {
    TreeNode<M> node = findNode(model);
    if (loader != null && node != null && !node.isLoaded()) {
      return loader.hasChildren(node.getModel());
    }
    if (node != null && (!node.isLeaf() || treeStore.hasChildren(node.getModel()))) {
      return true;
    }
    return false;
  }

  protected void onAdd(StoreAddEvent<M> se) {
    if (viewReady) {
      M p = treeStore.getParent(se.getItems().get(0));
      if (p == null) {
        for (M child : se.getItems()) {
          register(child);
        }
        if (se.getIndex() > 0) {
          M prev = treeStore.getChild(se.getIndex() - 1);
          int index = findLastOpenChildIndex(prev);
          store.addAll(index + 1, se.getItems());
        } else {
          store.addAll(se.getIndex(), se.getItems());
        }
      } else {
        TreeNode<M> node = findNode(p);
        if (node != null) {
          for (M child : se.getItems()) {
            register(child);
          }
          if (!node.isExpanded()) {
            refresh(p);
            return;
          }
          int index = se.getIndex();
          if (index == 0) {
            int pindex = store.indexOf(p);
            store.addAll(pindex + 1, se.getItems());
          } else {
            index = store.indexOf(treeStore.getChildren(p).get(index - 1));
            TreeNode<M> mark = findNode(store.get(index));
            index = findLastOpenChildIndex(mark.getModel());
            store.addAll(index + 1, se.getItems());
          }
          refresh(p);
        }
      }
    }
  }

  @Override
  protected void onAfterRenderView() {
    super.onAfterRenderView();
    doInitialLoad();
  }

  protected void onClear(StoreClearEvent<M> event) {
    onDataChange(null);
  }

  @Override
  protected void onClick(Event event) {
    EventTarget eventTarget = event.getEventTarget();
    if (Element.is(eventTarget)) {

      M m = store.get(getView().findRowIndex(Element.as(eventTarget)));
      if (m != null) {
        TreeNode<M> node = findNode(m);
        if (node != null) {
          Element jointEl = treeGridView.getJointElement(node);
          if (jointEl != null
              && DOM.isOrHasChild((com.google.gwt.user.client.Element) jointEl.cast(),
                  (com.google.gwt.user.client.Element) (Element.as(eventTarget)).cast())) {
            toggle(m);
          } else {
            super.onClick(event);
          }
        }
      }
    } else {
      super.onClick(event);
    }
  }

  protected void onDataChange(M parent) {
    if (!viewReady) {
      return;
    }

    if (parent == null) {
      store.clear();
      nodes.clear();
      renderChildren(null, autoLoad);
    } else {
      TreeNode<M> n = findNode(parent);
      if (n != null) {
        n.setLoaded(true);
        n.setLoading(false);
        renderChildren(parent, autoLoad);

        if (n.isExpand() && !isLeaf(parent)) {
          n.setExpand(false);
          boolean deep = n.isExpandDeep();
          n.setExpandDeep(false);
          boolean c = caching;
          caching = true;
          setExpanded(parent, true, deep);
          caching = c;
        } else {
          refresh(parent);
        }
      }
    }
  }

  @Override
  protected void onDoubleClick(Event e) {
    super.onDoubleClick(e);
    if (Element.is(e.getEventTarget())) {
      int i = getView().findRowIndex(Element.as(e.getEventTarget()));
      M m = store.get(i);
      if (m != null) {
        toggle(m);
      }
    }
  }

  protected void onFilter(StoreFilterEvent<M> se) {
    onDataChange(null);
    if (expandOnFilter && treeStore.isFiltered()) {
      expandAll();
    }
  }

  protected void onRecordChange(StoreRecordChangeEvent<M> event) {
    store.update(event.getRecord().getModel());

  }

  protected void onRemove(TreeStoreRemoveEvent<M> event) {
    if (viewReady) {
      unregister(event.getItem());
      store.remove(event.getItem());
      for (M child : event.getChildren()) {
        unregister(child);
        store.remove(child);
      }
      TreeNode<M> p = findNode(event.getParent());
      if (p != null && p.isExpanded() && treeStore.getChildCount(p.getModel()) == 0) {
        setExpanded(p.getModel(), false);
      } else if (p != null && treeStore.getChildCount(p.getModel()) == 0) {
        refresh(event.getParent());
      }
    }
  }

  protected void onSort(StoreSortEvent<M> event) {
    onDataChange(null);

  }

  protected void onUpdate(StoreUpdateEvent<M> se) {
    for (M m : se.getItems()) {
      if (store.indexOf(m) != -1) {
        store.update(m);
      }
    }
  }

  protected String register(M m) {
    String id = generateModelId(m);
    if (!nodes.containsKey(id)) {
      nodes.put(id, new TreeGridNode<M>(id, m));
    }
    return id;
  }

  protected void renderChildren(M parent, boolean auto) {
    List<M> children = parent == null ? treeStore.getRootItems() : treeStore.getChildren(parent);

    for (M child : children) {
      register(child);
    }

    if (parent == null && children.size() > 0) {
      store.addAll(children);
    }

    for (M child : children) {
      if (autoExpand) {
        final M c = child;
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

          @Override
          public void execute() {
            setExpanded(c, true);

          }
        });
      } else if (loader != null) {
        if (autoLoad) {
          if (store.isFiltered() || (!auto)) {
            renderChildren(child, auto);
          } else {
            loader.loadChildren(child);
          }
        }
      }
    }
  }

  protected void setTreeColumn(ColumnConfig<M, ?> treeColumn) {
    assert (treeColumn != null && cm.indexOf(treeColumn) != -1) : "treeColumn not found in ColumnModel";
    this.treeColumn = treeColumn;
  }

  protected void unregister(M m) {
    TreeNode<M> node = findNode(m);
    if (node != null) {
      node.clearElements();

      nodes.remove(generateModelId(m));
    }
  }

  private void doInitialLoad() {
    if (treeStore.getRootItems().size() == 0 && loader != null) {
      loader.load();
    } else {
      renderChildren(null, false);
      if (autoExpand) {
        expandAll();
      }
    }

  }

  private void setExpandChildren(M m, boolean expand) {
    for (M child : treeStore.getChildren(m)) {
      setExpanded(child, expand, true);
    }
  }

}