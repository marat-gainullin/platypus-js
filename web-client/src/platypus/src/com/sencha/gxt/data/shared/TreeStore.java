/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreSortEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.data.shared.event.TreeStoreRemoveEvent;

/**
 * A {@link Store} for hierarchical data. Parent-Child relationships are tracked
 * internally, and can be accessed through the {@link #getParent(Object)} and
 * {@link #getChildren(Object)} calls, and modified through the many add()
 * overrides or the {@link #remove(Object)} method.
 * 
 * As with {@link ListStore}, all changes and data are relative to what is
 * currently visible, due to the {@link Store.StoreFilter}s. As such, if filters
 * are active and structural changes are required, it might be necessary to
 * disable filters to make the change, re-enabling them when finished.
 * 
 * @param <M> the model type
 */
public class TreeStore<M> extends Store<M> {
  /**
   * Simple interface to allow data to be imported/exported from a TreeStore,
   * complete with the structure of the tree.
   * 
   * @param <T> type of data wrapped by this node
   */
  public interface TreeNode<T> {
    /**
     * Gets the list of child nodes. The data in each node can be modified, but
     * will not automatically fire store events.
     * 
     * Note: Some implementations may make this list immutable, such as those
     * provided by {@link TreeStore#getSubTree(Object)}.
     * 
     * @return the list of child nodes
     */
    List<? extends TreeNode<T>> getChildren();

    /**
     * Gets the data represented by this node.
     * 
     * @return the data represented by this node
     */
    T getData();
  }
  private class TreeModel implements TreeNode<M> {
    // data
    private M data;
    // tree traversal
    private TreeModel parent;
    private final List<TreeModel> children = new ArrayList<TreeModel>();
    private boolean root;

    // filtering
    private boolean isVisible = true;
    private List<TreeModel> visibleChildren;

    public TreeModel(M data) {
      if (data == null) {
        root = true;
      }
      this.data = data;
    }

    /**
     * Adds the given element at the indicated position in the visible child
     * list.
     * 
     * TODO this needs to be redone to have two code paths, one for filtered,
     * one for normal
     * 
     * @param index the insert index
     * @param child the child to be added
     */
    public void addChild(int index, TreeModel child) {
      final int actualIndex;
      if (isSorted()) {
        // if sorted, ignore the index we are given, and insert it where it
        // belongs
        int position = Collections.binarySearch(this.children, child, buildWrappedFullComparator());
        actualIndex = position < 0 ? (-position - 1) : position;
      } else {
        // otherwise, figure out where the given index falls in the visible set
        // of children, and use that to translate the index
        if (isFiltered()) {
          List<TreeModel> children = getChildren();
          actualIndex = index == 0 ? 0 : (children.indexOf(children.get(index - 1)) + 1);
        } else {
          actualIndex = index;
        }
      }
      children.add(actualIndex, child);
      child.parent = this;

      if (isFiltered()) {
        // this may not be a safe assumption, but if it is not true, this filter
        // traversal is not appropriate
        assert child.children.size() == 0;

        // made invisible before the check, as this is the state before it was
        // added - it was not visible before it was added
        child.isVisible = false;
        child.visibleChildren = new ArrayList<TreeModel>();
        child.applyFiltersToParents();

        // verify the position of the elements
        assert (!visibleChildren.contains(child)) || visibleChildren.indexOf(child) == index;
      }
    }

    /**
     * Added all of the children at the given index in the visible child list
     * 
     * TODO this needs to be redone to have two code paths, one for filtered,
     * one for normal
     * 
     * @param index the insert index
     * @param children the children to be added
     */
    public void addChildren(int index, List<TreeModel> children) {
      if (isSorted()) {
        // if sorted we should rebuild the local list, index is ignored
        this.children.addAll(children);
        Collections.sort(this.children, buildWrappedFullComparator());
      } else {
        // translate the index for the full list
        int actualIndex = index == 0 ? 0 : (this.children.indexOf(getChildren().get(index - 1)) + 1);
        this.children.addAll(actualIndex, children);
      }
      for (TreeModel child : children) {
        child.parent = this;
      }

      // if the treestore is filtered, check if the newly added items change
      // visibility of this (and parents)
      if (isFiltered()) {
        boolean alreadyVisible = isVisible;
        // check this and the newly added children. this goes a little too far,
        // but better than not far enough (no need to recheck existing children)
        applyFiltersToChildren();

        // if this node's visibility changed, we need to update parent's records
        // both options contain a sanity check to confirm that the records make sense
        if (alreadyVisible != isVisible) {
          if (isVisible) {
            assert !parent.visibleChildren.contains(this);
            parent.visibleChildren.add(this);
          } else {
            assert parent.visibleChildren.contains(this);
            parent.visibleChildren.remove(this);
          }
        }

        // check up the parent chain, see if changes need to happen
        applyFiltersToParents();
      }
    }

    /**
     * Recursively runs runs filters on children to see if each element should
     * be visible. Checks if each element is visible, due to its children or
     * filters, in a DFS manner.
     * 
     * Should be used when filters change, or large amounts of data is added, in
     * conjunction with {@link #applyFiltersToParents()} if needed.
     * 
     * @return isVisible, making the recursion easier to write
     */
    public boolean applyFiltersToChildren() {
      visibleChildren = new ArrayList<TreeModel>();
      isVisible = false;

      // look at children first, if anything is visible, this is visible.
      // don't break early, because otherwise we won't run the filter on all children
      for (TreeModel child : children) {
        if (child.applyFiltersToChildren()) {
          visibleChildren.add(child);
          isVisible = true;
        }
      }
      // if there are no visible children, run the filters
      if (!isVisible) {
        isVisible = !isFilteredOut();
      }
      return isVisible;
    }

    /**
     * Recursively runs filters on parents to see if each element should be
     * visible. Checks if each element has any children or is itself selected by
     * filters, and if a change has occurred, calls parents until root.
     * 
     * Should be used when data is deleted, or individual elements are added.
     * Should be called on a new element added, assuming it has no children, or
     * on the parent of a deleted element.
     */
    public void applyFiltersToParents() {
      assert visibleChildren != null;
      boolean visible = visibleChildren.size() != 0 || !isFilteredOut();
      if (visible != isVisible) {
        isVisible = visible;
        if (isVisible) {
          assert !parent.visibleChildren.contains(this);
          parent.visibleChildren.add(this);
        } else {
          assert parent.visibleChildren.contains(this);
          parent.visibleChildren.remove(this);
        }
        if (!parent.isRoot()) {
          parent.applyFiltersToParents();
        }
      }
    }

    public void clear() {
      children.clear();
      if (isFiltered()) {
        visibleChildren.clear();
      }
    }

    @Override
    public List<TreeModel> getChildren() {
      List<TreeModel> children;
      if (isFiltered()) {
        assert visibleChildren != null;
        children = this.visibleChildren;
      } else {
        children = this.children;
      }
      // if running in a jvm (test, or dev mode), make sure no changes are made
      if (GWT.isProdMode() == false) {
        return Collections.unmodifiableList(children);
      }
      return children;
    }

    @Override
    public M getData() {
      return data;
    }

    public TreeModel getParent() {
      return parent;
    }

    public boolean isRoot() {
      return root;
    }

    /**
     * Removes the given wrapper element from this node. Returns true if the
     * element was visible
     * 
     * @param wrapper the wrapper to remove
     */
    public void remove(TreeModel wrapper) {
      children.remove(wrapper);
      // if filtering is enabled and the child was visible, re-filter this
      // element, as well as parents
      // TODO refiltering probably isnt necessary unless visible count is now 0
      if (isFiltered() && visibleChildren.remove(wrapper)) {
        applyFiltersToParents();
      }
    }

    /**
     * Checks the the filtered status of this node only by running the filters.
     * Makes no changes, but returns true if this should be visible.
     * 
     * @return
     */
    private boolean isFilteredOut() {
      if (isRoot()) {
        return true;
      }
      for (StoreFilter<M> filter : getFilters()) {
        if (!filter.select(TreeStore.this, parent.data, data)) {
          return true;
        }
      }
      return false;
    }

  }

  private final TreeModel roots = new TreeModel(null);
  private final Map<String, TreeModel> modelMap = new FastMap<TreeModel>();

  /**
   * Creates a tree store with the given key provider. The key provider is
   * responsible for returning a unique key for a given model
   * 
   * @param keyProvider the key provider
   */
  public TreeStore(ModelKeyProvider<? super M> keyProvider) {
    super(keyProvider);
  }

  /**
   * Adds the data models as roots of the tree.
   * 
   * @param rootNodes the items to add
   */
  public void add(List<M> rootNodes) {
    insert(roots, roots.getChildren().size(), rootNodes, false);
  }

  /**
   * Adds the given data model as a root of the tree.
   * 
   * @param root the data model to add as a root
   */
  public void add(M root) {
    insert(roots, roots.getChildren().size(), root);
  }

  /**
   * Adds the list of children to the end of the visible children of the given
   * parent model
   * 
   * @param parent the parent item
   * @param children the items to insert
   */
  public void add(M parent, List<M> children) {
    TreeModel parentModel = getWrapper(parent);
    insert(parentModel, parentModel.getChildren().size(), children, false);
  }

  /**
   * Adds the child to the end of the visible children of the parent model
   * 
   * @param parent the parent data model
   * @param child the child data model
   */
  public void add(M parent, M child) {
    TreeModel parentModel = getWrapper(parent);
    insert(parentModel, parentModel.getChildren().size(), child);
  }

  /**
   * Imports a list of subtrees at the given position in the root of the tree.
   * 
   * @param index the insert location of the new subtree
   * @param children the list of subtrees
   */
  public void addSubTree(int index, List<? extends TreeNode<M>> children) {
    List<M> addedElements = new ArrayList<M>();
    List<TreeModel> wrapped = convertTreeNodes(children, addedElements);
    roots.addChildren(index, wrapped);

    List<M> elts = new ArrayList<M>();
    for (TreeNode<M> child : children) {
      elts.add(child.getData());
    }
    fireEvent(new StoreAddEvent<M>(index, elts));
  }

  /**
   * Imports a list of subtrees to append to the given parent object already
   * present in the tree.
   * 
   * @param parent the parent data model
   * @param index the child index
   * @param children the list of subtrees
   */
  public void addSubTree(M parent, int index, List<? extends TreeNode<M>> children) {
    List<M> addedElements = new ArrayList<M>();
    List<TreeModel> wrapped = convertTreeNodes(children, addedElements);
    getWrapper(parent).addChildren(index, wrapped);
    // fireEvent(new StoreAddEvent<M>(index, addedElements));

    List<M> elts = new ArrayList<M>();
    for (TreeNode<M> child : children) {
      elts.add(child.getData());
    }
    fireEvent(new StoreAddEvent<M>(index, elts));
  }

  @Override
  public void applySort(boolean suppressEvent) {
    Collections.sort(roots.children, buildWrappedFullComparator());

    for (TreeModel model : modelMap.values()) {
      Collections.sort(model.children, buildWrappedFullComparator());
    }
    if (!suppressEvent) {
      fireEvent(new StoreSortEvent<M>());
    }
  }

  @Override
  public void clear() {
    super.clear();
    modelMap.clear();
    roots.clear();

    fireEvent(new StoreClearEvent<M>());
  }

  @Override
  public M findModelWithKey(String key) {
    // deliberately not using getWrapper, as this is allowed to be null
    TreeModel wrapped = modelMap.get(key);
    if (wrapped == null) {
      return null;
    }
    return wrapped.getData();
  }

  /**
   * Gets all visible items in the tree
   * 
   * @return all visible items in the tree
   */
  @Override
  public List<M> getAll() {
    List<TreeModel> allChildren = new LinkedList<TreeStore<M>.TreeModel>(roots.getChildren());
    for (int i = 0; i < allChildren.size(); i++) {
      allChildren.addAll(allChildren.get(i).getChildren());
    }
    return unwrap(allChildren);
  }

  /**
   * Recursively builds a list of all of the visible elements below the given
   * one in the tree
   * 
   * @param parent the parent data model
   * @return list of all of the visible elements
   */
  public List<M> getAllChildren(M parent) {
    List<TreeModel> allChildren = new LinkedList<TreeStore<M>.TreeModel>(getWrapper(parent).getChildren());
    for (int i = 0; i < allChildren.size(); i++) {
      allChildren.addAll(allChildren.get(i).getChildren());
    }
    return unwrap(allChildren);
  }

  /**
   * Gets the total count of all visible items in the tree
   * 
   * @return count of all visible items
   */
  public int getAllItemsCount() {
    List<TreeModel> allChildren = new LinkedList<TreeStore<M>.TreeModel>(roots.getChildren());
    for (int i = 0; i < allChildren.size(); i++) {
      allChildren.addAll(allChildren.get(i).getChildren());
    }
    assert isFiltered() || allChildren.size() == modelMap.size();
    return allChildren.size();
  }

  /**
   * Returns the root level child.
   * 
   * @param index the index
   * @return the child
   */
  public M getChild(int index) {
    return getRootItems().get(index);
  }

  /**
   * Gets the number of visible children in the given node
   * 
   * @param parent the parent of the children
   * @return number of visible children
   */
  public int getChildCount(M parent) {
    return getWrapper(parent).getChildren().size();
  }

  /**
   * Gets the list of visible children attached to the given element
   * 
   * @param parent given element
   * @return list of visible children
   */
  public List<M> getChildren(M parent) {
    return unwrap(getWrapper(parent).getChildren());
  }

  /**
   * Gets the depth of the given element in the tree, where 1 indicates that it
   * is a root element of the tree.
   * 
   * @param child given element
   * @return depth of the given element
   */
  public int getDepth(M child) {
    int depth = 0;
    while (child != null) {
      depth++;
      child = getParent(child);
    }
    return depth;
  }

  /**
   * Returns the fist child of the parent.
   * 
   * @param parent the parent
   * @return the first child or null if no children
   */
  public M getFirstChild(M parent) {
    TreeModel wrapper = parent == null ? roots : getWrapper(parent);
    if (wrapper.getChildren().size() != 0) {
      return wrapper.getChildren().get(0).getData();
    }
    return null;
  }

  /**
   * Returns the last child of the parent.
   * 
   * @param parent the parent
   * @return the last child of null if no children
   */
  public M getLastChild(M parent) {
    TreeModel wrapper = parent == null ? roots : getWrapper(parent);
    List<TreeModel> children = wrapper.getChildren();
    if (children.size() != 0) {
      return children.get(children.size() - 1).getData();
    }
    return null;
  }

  /**
   * Returns the next sibling of the model.
   * 
   * @param item the model
   * @return the next sibling or null
   */
  public M getNextSibling(M item) {
    M parent = getParent(item);
    List<M> children = parent == null ? getRootItems() : getChildren(parent);
    int index = children.indexOf(item);
    if (children.size() > (index + 1)) {
      return children.get(index + 1);
    }
    return null;
  }

  /**
   * Returns the parent of the given child.
   * 
   * @param child the given child
   * @return parent of the given child
   */
  public M getParent(M child) {
    TreeModel m = getWrapper(child).getParent();
    return (m != null && !m.isRoot()) ? m.getData() : null;
  }

  /**
   * Returns the item's previous sibling.
   * 
   * @param item the item
   * @return the previous sibling
   */
  public M getPreviousSibling(M item) {
    M parent = getParent(item);
    List<M> children = parent == null ? getRootItems() : getChildren(parent);
    int index = children.indexOf(item);
    if (index > 0) {
      return children.get(index - 1);
    }
    return null;
  }

  /**
   * Gets the number of items at the root of the tree, that is, the number of
   * visible items that have been added without specifying a parent.
   * 
   * @return number of items at the root of the tree
   */
  public int getRootCount() {
    return roots.getChildren().size();
  }

  /**
   * Gets the visible items at the root of the tree.
   * 
   * @return visible items at the root of the tree
   */
  public List<M> getRootItems() {
    return unwrap(roots.getChildren());
  }

  /**
   * Gets the full subtree model from the given parent node. Note that this
   * model will be modified if the store changes, and the children lists are
   * immutable except by going through the TreeStore.
   * 
   * @param parent the model data representing the parent of the subtree
   * @return the tree node representing the parent of the subtree
   */
  public TreeNode<M> getSubTree(M parent) {
    return getWrapper(parent);
  }

  /**
   * Returns true if the given node has visible children
   * 
   * @param item given node
   * @return true if the given node has visible children
   */
  public boolean hasChildren(M item) {
    return getChildCount(item) != 0;
  }

  /**
   * Returns the item's index in it's parent including root level items.
   * 
   * @param item the item
   * @return the index
   */
  public int indexOf(M item) {
    M parent = getParent(item);
    if (parent == null) {
      return getRootItems().indexOf(item);
    } else {
      return getChildren(parent).indexOf(item);
    }
  }

  /**
   * Inserts the given models at the given index in the list of root nodes
   * 
   * @param index the insert index
   * @param rootNodes the items to insert
   */
  public void insert(int index, List<M> rootNodes) {
    insert(roots, index, rootNodes, false);
  }

  /**
   * Inserts the given model at the given index in the list of root nodes
   * 
   * @param index the insert index
   * @param root the root item
   */
  public void insert(int index, M root) {
    insert(roots, index, root);
  }

  /**
   * Inserts the child models at the given position in the parent's list of
   * visible children.
   * 
   * @param parent the parent item
   * @param index the insert index
   * @param children the items to insert
   */
  public void insert(M parent, int index, List<M> children) {
    insert(getWrapper(parent), index, children, false);
  }

  /**
   * Inserts the child model at the given position in the parent's list of
   * visible children
   * 
   * @param parent the parent item
   * @param index the insert index
   * @param child the child item
   */
  public void insert(M parent, int index, M child) {
    insert(getWrapper(parent), index, child);
  }

  /**
   * Removes the given model from the store. Only fires the
   * {@link StoreRemoveEvent} if the element was visible.
   */
  @Override
  public boolean remove(M model) {
    super.remove(model);
    // deliberately using the modelMap directly to allow this to be null, which allows
    // items not present to be removed, keeping with Store#remove(M)'s intent.
    TreeModel wrapper = modelMap.get(getKeyProvider().getKey(model));
    if (wrapper != null) {
      M parent = getParent(model);
      List<M> children = getAllChildren(model);
      int visibleIndex = wrapper.getParent().getChildren().indexOf(wrapper);
      wrapper.getParent().remove(wrapper);
      List<TreeModel> models = new LinkedList<TreeModel>();
      models.add(wrapper);
      for (int i = 0; i < models.size(); i++) {
        wrapper = models.get(i);
        models.addAll(wrapper.getChildren());

        modelMap.remove(getKeyProvider().getKey(wrapper.getData()));
      }

      if (visibleIndex != -1) {
        fireEvent(new TreeStoreRemoveEvent<M>(visibleIndex, model, parent, children));
      }
      return true;
    }
    return false;
  }

  /**
   * Removed the children of the given model from the store. Only fires
   * {@link StoreRemoveEvent} for the models which were visible
   * 
   * @param parent the parent of the children to remove
   */
  public void removeChildren(M parent) {
    removeChildren(getWrapper(parent));
  }

  /**
   * Replaces the children of the given parent with a list of new child models.
   * 
   * @param parent the parent data model
   * @param children the list of child models
   */
  public void replaceChildren(M parent, List<M> children) {
    if (parent == null) {
      super.clear();
      roots.clear();
      modelMap.clear();

      roots.addChildren(0, wrap(children));
    } else {
      TreeModel parentNode = getWrapper(parent);
      List<TreeModel> models = new LinkedList<TreeModel>();
      models.addAll(parentNode.children);
      for (int i = 0; i < models.size(); i++) {
        TreeModel wrapper = models.get(i);
        models.addAll(wrapper.children);
        modelMap.remove(getKeyProvider().getKey(wrapper.getData()));
        super.remove(wrapper.getData());
      }
      parentNode.clear();

      parentNode.addChildren(0, wrap(children));
    }
    fireEvent(new StoreDataChangeEvent<M>(parent));
  }

  /**
   * Replaces the children of the given parent with a list of subtrees.
   * 
   * @param parent the parent data model
   * @param children the list of subtrees
   */
  public void replaceSubTree(M parent, List<? extends TreeNode<M>> children) {
    List<TreeModel> wrapped = convertTreeNodes(children, new ArrayList<M>());
    if (parent == null) {
      super.clear();
      roots.clear();
      modelMap.clear();

      roots.addChildren(0, wrapped);
    } else {
      TreeModel parentNode = getWrapper(parent);
      parentNode.clear();
      List<TreeModel> models = new LinkedList<TreeModel>();
      models.addAll(parentNode.children);
      for (int i = 0; i < models.size(); i++) {
        TreeModel wrapper = models.get(i);
        models.addAll(wrapper.children);
        modelMap.remove(getKeyProvider().getKey(wrapper.getData()));
        super.remove(wrapper.getData());
      }

      parentNode.addChildren(0, wrapped);
    }
    fireEvent(new StoreDataChangeEvent<M>(parent));
  }

  public void update(M item) {
    TreeModel wrapper = modelMap.get(getKeyProvider().getKey(item));

    // remove references to the old model
    super.remove(wrapper.data);
    // replace the existing model with the incoming one
    wrapper.data = item;

    fireEvent(new StoreUpdateEvent<M>(Collections.singletonList(item)));
  }

  /*
   * (non-javadoc)
   * 
   * Filtering happens recursively, as needed. Nodes are visible if the
   * available filters select them or any recursive child.
   */
  @Override
  protected void applyFilters() {
    if (isFiltered()) {
      // Full recursive dfs, from the root nodes
      roots.applyFiltersToChildren();
    }
    // TODO consider creating else block to clean up children sets. This causes
    // a perf hit, but saves some space

    // always fire the event
    fireEvent(new StoreFilterEvent<M>());
  }

  /**
   * @return a comparator for the tree
   */
  protected Comparator<TreeModel> buildWrappedFullComparator() {
    return new Comparator<TreeModel>() {
      public int compare(TreeModel o1, TreeModel o2) {
        return buildFullComparator().compare(o1.getData(), o2.getData());
      }
    };
  }

  /**
   * Helper function to prepare a foreign set of nodes to be imported into this
   * TreeStore instance.
   * 
   * @param children the foreign nodes to prepare
   * @param added an existing list to which the node data will be added
   * @return the new list of prepared nodes, empty if children is null (or
   *         empty)
   */
  protected List<TreeModel> convertTreeNodes(List<? extends TreeNode<M>> children, List<M> added) {
    List<TreeModel> wrapped = new ArrayList<TreeModel>();
    if (children != null) {

      for (TreeNode<M> child : children) {
        TreeModel tree = new TreeModel(child.getData());
        added.add(child.getData());
        modelMap.put(getKeyProvider().getKey(child.getData()), tree);
        tree.addChildren(0, convertTreeNodes(child.getChildren(), added));
        wrapped.add(tree);
      }
    }
    return wrapped;
  }


  protected Map<String, TreeModel> getModelMap() {
    return modelMap;
  }

  /**
   * Gets the existing wrapper for the given data model. The wrapper is
   * retrieved based on the model data's key as returned by
   * {@link ModelKeyProvider#getKey(Object)}.
   * 
   * @param data the existing data model to look up
   * @return the corresponding wrapped tree model
   */
  protected TreeModel getWrapper(M data) {
    assert data != null : "Cannot operate on a null model";
    assert modelMap.containsKey(getKeyProvider().getKey(data)) : "The given model does not appear to already be in the TreeStore";
    return modelMap.get(getKeyProvider().getKey(data));
  }

  /**
   * Gets the data models represented by the given TreeModels
   * 
   * @param models the model
   * @return the unwrapped models
   */
  protected List<M> unwrap(List<TreeModel> models) {
    List<M> list = new ArrayList<M>();
    for (TreeModel model : models) {
      assert model != roots : "The TreeStore's root is not a valid model to be returned";
      list.add(model.getData());
    }
    return Collections.unmodifiableList(list);
  }

  /**
   * Creates a List of wrappers for the given models. As with
   * {@link #wrap(Object)}, these must not already be part of the tree.
   * 
   * @param data the data
   * @return the list of wrapped models
   */
  protected List<TreeModel> wrap(List<M> data) {
    List<TreeModel> wrappers = new ArrayList<TreeModel>();
    for (M elt : data) {
      wrappers.add(wrap(elt));
    }
    return wrappers;
  }

  /**
   * Creates a new wrapper for the given data model. The data model must not
   * already be part of the tree, use {@link #getWrapper(Object)} in that case.
   * 
   * @param data the data model to wrap
   * @return the new wrapped tree model for the given data model
   */
  protected TreeModel wrap(M data) {
    assert !modelMap.containsKey(getKeyProvider().getKey(data)) : "The given model is already in the TreeStore, and should not be assigned a new node";
    TreeModel wrapper = new TreeModel(data);
    modelMap.put(getKeyProvider().getKey(data), wrapper);
    return wrapper;
  }

  /**
   * Actual insert implementation for adding multiple nodes to a given parent.
   * Written so that no map lookups are done more than necessary, and so that
   * the event can track which changes will actually be visible.
   */
  private void insert(TreeModel parent, int index, List<M> children, boolean suppressEvent) {
    int initialCount = parent.getChildren().size();
    parent.addChildren(index, wrap(children));

    if (initialCount != parent.getChildren().size()) {
      List<M> addedChildren = new ArrayList<M>();
      List<TreeModel> currentChildren = parent.getChildren();
      if (isSorted()) {
        // TODO: The following is probably O(n^2); optimise this
        int currentChildrenSize = currentChildren.size();
        for (int i = 0; i < currentChildrenSize; i++) {
          int childrenSize = children.size();
          for (int j = 0; j < childrenSize; j++) {
            M currentData = currentChildren.get(i).getData();
            M child = children.get(j);
            if (child == currentData) {
              addedChildren.add(child);
              break;
            }
          }
        }
      } else {
        int i = index;
        int childrenSize = children.size();
        for (int j = 0; j < childrenSize; j++) {
          if (currentChildren.get(i).getData() == children.get(j)) {
            addedChildren.add(children.get(j));
            i++;
          }
        }
      }
      if (!suppressEvent && addedChildren.size() != 0) {
        fireEvent(new StoreAddEvent<M>(index, addedChildren));
      }
    }
  }

  /**
   * Actual insert implementation for adding individual nodes. Written so that
   * no map lookups are done more than necessary, no extra wrapping in Lists
   * need be done, and that way the event is only fired from one place
   * 
   * @param parent
   * @param index
   * @param child
   */
  private void insert(TreeModel parent, int index, M child) {
    int initialCount = parent.getChildren().size();
    parent.addChild(index, wrap(child));

    if (parent.getChildren().size() != initialCount) {
      int addedIndex = -1;
      if (isSorted()) {
        List<TreeModel> childrenModels = parent.getChildren();
        for (int i = 0; i < childrenModels.size(); i++) {
          if (childrenModels.get(i).getData() == child) {
            addedIndex = i;
            break;
          }
        }
      } else {
        addedIndex = index;
      }
      // child was not found; this should never happen
      assert addedIndex >= 0;
      // if the change actually occurred, fire an event
      fireEvent(new StoreAddEvent<M>(addedIndex, child));
    }
  }

  private void removeChildren(TreeModel parentWrapper) {
    if (parentWrapper.getChildren().size() != 0) {
      List<TreeModel> models = new LinkedList<TreeModel>();
      models.addAll(parentWrapper.children);
      for (int i = 0; i < models.size(); i++) {
        TreeModel wrapper = models.get(i);
        models.addAll(wrapper.children);
        
        List<M> children = getAllChildren(wrapper.data);

        modelMap.remove(getKeyProvider().getKey(wrapper.getData()));
        super.remove(wrapper.getData());
        if (wrapper.isVisible && wrapper.parent == parentWrapper) {
          // TODO consider changing the event to allow firing just once
          // index is always zero, since we are removing from first to last
          fireEvent(new TreeStoreRemoveEvent<M>(0, wrapper.data, parentWrapper.data, children));
        }
      }
    }
  }

}
