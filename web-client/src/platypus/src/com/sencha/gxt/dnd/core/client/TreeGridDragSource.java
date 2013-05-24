/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.dnd.core.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Element;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.shared.FastSet;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DND.TreeSource;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

/**
 * Enables a {@link TreeGrid} to act as the source of a drag and drop operation.
 * <p/>
 * Use {@link #setTreeSource(TreeSource)} to specify whether leaf nodes,
 * non-leaf nodes or both types of nodes can be dragged (defaults to
 * {@link TreeSource#BOTH}). The drag operation is cancelled if the user
 * attempts to drag a node type that is not permitted.
 * <p/>
 * The drag data consists of a list of items of type {@code <M>}. It is
 * optimized to remove children of parents that are also in the list (i.e. if a
 * parent is the subject of a drag operation then all of its children are
 * implicitly part of the drag operation).
 * 
 * @param <M> the model type
 */
public class TreeGridDragSource<M> extends DragSource {

  private TreeSource treeGridSource = TreeSource.BOTH;

  /**
   * Creates a drag source for the specified tree grid.
   * 
   * @param widget the tree grid to enable as a drag source
   */
  public TreeGridDragSource(TreeGrid<M> widget) {
    super(widget);
    setStatusText("{0} items selected");
  }

  /**
   * Returns the tree grid associated with this drag source.
   * 
   * @return the tree grid associated with this drag source
   */
  public TreeSource getTreeGridSource() {
    return treeGridSource;
  }

  @SuppressWarnings("unchecked")
  @Override
  public TreeGrid<M> getWidget() {
    return (TreeGrid<M>) super.getWidget();
  }

  /**
   * Sets the tree source (defaults to {@link TreeSource#BOTH}).
   * 
   * @param treeGridSource the tree source
   */
  public void setTreeGridSource(TreeSource treeGridSource) {
    this.treeGridSource = treeGridSource;
  }

  @Override
  protected void onDragDrop(DndDropEvent event) {
    if (event.getOperation() == Operation.MOVE) {
      @SuppressWarnings("unchecked")
      List<TreeNode<M>> sel = (List<TreeNode<M>>) event.getData();
      for (TreeNode<M> s : sel) {
        getWidget().getTreeStore().remove(s.getData());
      }
    }
  }

  @Override
  protected void onDragStart(DndDragStartEvent event) {
    Element startTarget = event.getDragStartEvent().getStartElement().<Element> cast();
    Tree.TreeNode<M> start = getWidget().findNode(startTarget);
    if (start == null || !getWidget().getTreeView().isSelectableTarget(startTarget)) {
      event.setCancelled(true);
      return;
    }

    List<M> selected = getWidget().getSelectionModel().getSelectedItems();

    if (selected.size() == 0) {
      event.setCancelled(true);
      return;
    }

    List<TreeNode<M>> selectedSubTrees = new ArrayList<TreeNode<M>>();
    if (getTreeGridSource() == TreeSource.LEAF) {
      for (M item : selected) {
        if (getWidget().isLeaf(item)) {
          selectedSubTrees.add(getWidget().getTreeStore().getSubTree(item));
        } else {
          // forget it, we've got a non-leaf
          event.setCancelled(true);
          return;
        }
      }
    } else {
      ModelKeyProvider<? super M> kp = getWidget().getTreeStore().getKeyProvider();
      FastSet nonLeafKeys = new FastSet();
      for (M item : selected) {
        if (getWidget().isLeaf(item)) {
          if (treeGridSource == TreeSource.NODE) {
            event.setCancelled(true);
            return;
          }
        } else {
          nonLeafKeys.add(kp.getKey(item));
        }
      }
      for (int i = selected.size() - 1; i >= 0; i--) {
        // TODO consider tracking these parents, and if they are part of another
        // parent, adding them to the keyset
        M parent = selected.get(i);
        while ((parent = getWidget().getTreeStore().getParent(parent)) != null) {
          if (nonLeafKeys.contains(kp.getKey(parent))) {
            selected.remove(i);
          }
        }
      }
      for (M item : selected) {
        selectedSubTrees.add(getWidget().getTreeStore().getSubTree(item));
      }
    }

    if (selectedSubTrees.size() > 0) {
      event.setData(selectedSubTrees);
    } else {
      event.setCancelled(true);
    }

    if (selected.size() > 0) {
      event.setCancelled(false);

      if (getStatusText() == null) {
        event.getStatusProxy().update(DefaultMessages.getMessages().listField_itemsSelected(selected.size()));
      } else {
        event.getStatusProxy().update(Format.substitute(getStatusText(), selected.size()));
      }
    }
  }
}
