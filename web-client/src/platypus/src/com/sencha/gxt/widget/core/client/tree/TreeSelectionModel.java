/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.KeyNav;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.event.XEvent;
import com.sencha.gxt.widget.core.client.selection.AbstractStoreSelectionModel;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;

/**
 * <code>Tree</code> selection model.
 * 
 * @param <M> the model type
 */
public class TreeSelectionModel<M> extends AbstractStoreSelectionModel<M> {
  private class Handler implements MouseDownHandler, ClickHandler {

    @Override
    public void onClick(ClickEvent event) {
      onMouseClick(event.getNativeEvent());
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
      TreeSelectionModel.this.onMouseDown(event.getNativeEvent());
    }

  }

  protected KeyNav keyNav = new KeyNav() {
    public void onDown(NativeEvent evt) {
      onKeyDown(evt);
    };

    @Override
    public void onLeft(NativeEvent evt) {
      onKeyLeft(evt);
    }

    @Override
    public void onRight(NativeEvent evt) {
      onKeyRight(evt);
    }

    @Override
    public void onUp(NativeEvent e) {
      onKeyUp(e);
    }
  };

  protected Tree<M,?> tree;

  protected TreeStore<M> treeStore;

  private Handler handler = new Handler();
  private GroupingHandlerRegistration handlerRegistration;

  public TreeSelectionModel() {
  }

  public void bindTree(Tree<M, ?> tree) {
    if (this.tree != null) {
      handlerRegistration.removeHandler();
      keyNav.bind(null);
      bind(null);
      this.treeStore = null;
    }
    this.tree = tree;
    if (tree != null) {
      if (handlerRegistration == null) {
        handlerRegistration = new GroupingHandlerRegistration();
      }
      handlerRegistration.add(tree.addDomHandler(handler, MouseDownEvent.getType()));
      handlerRegistration.add(tree.addDomHandler(handler, ClickEvent.getType()));
      keyNav.bind(tree);
      bind(tree.getStore());
      this.treeStore = (TreeStore<M>) tree.getStore();
    }
  }

  @Override
  public void deselect(int index) {
    assert false : "This method not implemented for trees";
  }

  @Override
  public void deselect(int start, int end) {
    assert false : "This method not implemented for trees";
  }

  @Override
  public boolean isSelected(M item) {
    return selected.contains(item);
  }

  @Override
  public void select(int start, int end, boolean keepExisting) {
    assert false : "This method not implemented for trees";
  }

  /**
   * Selects the item below the selected item in the tree, intelligently walking
   * the nodes.
   */
  public void selectNext() {
    M next = next();
    if (next != null) {
      doSingleSelect(next, false);
    }
  }

  /**
   * Selects the item above the selected item in the tree, intelligently walking
   * the nodes.
   */
  public void selectPrevious() {
    M prev = prev();
    if (prev != null) {
      doSingleSelect(prev, false);
    }
  }

  protected M next() {
    M sel = lastSelected;
    if (sel == null) {
      return null;
    }
    M first = treeStore.getFirstChild(sel);
    if (first != null && tree.isExpanded(sel)) {
      return first;
    } else {
      M nextSibling = treeStore.getNextSibling(sel);
      if (nextSibling != null) {
        return nextSibling;
      } else {
        M p = treeStore.getParent(sel);
        while (p != null) {
          nextSibling = treeStore.getNextSibling(p);
          if (nextSibling != null) {
            return nextSibling;
          }
          p = treeStore.getParent(p);
        }
      }
    }
    return null;
  }

  protected void onKeyDown(NativeEvent e) {
    e.preventDefault();
    M next = next();
    if (next != null) {
      doSingleSelect(next, false);
      tree.scrollIntoView(next);
    }
  }

  protected void onKeyLeft(NativeEvent ce) {
    ce.preventDefault();
    if (!tree.isLeaf(lastSelected) && tree.isExpanded(lastSelected)) {
      tree.setExpanded(lastSelected, false);
    } else if (treeStore.getParent(lastSelected) != null) {
      doSingleSelect(treeStore.getParent(lastSelected), false);
    }
  }

  protected void onKeyRight(NativeEvent ce) {
    ce.preventDefault();
    if (!tree.isLeaf(lastSelected) && !tree.isExpanded(lastSelected)) {
      tree.setExpanded(lastSelected, true);
    }
  }

  protected void onKeyUp(NativeEvent e) {
    e.preventDefault();
    M prev = prev();
    if (prev != null) {
      doSingleSelect(prev, false);
      tree.scrollIntoView(prev);
    }
  }

  protected void onMouseClick(NativeEvent e) {
    if (isLocked()) {
      return;
    }
    
    if (fireSelectionChangeOnClick) {
      fireSelectionChange();
      fireSelectionChangeOnClick = false;
    }

    XEvent xe = e.<XEvent> cast();

    if (selectionMode == SelectionMode.MULTI) {
      TreeNode<M> node = tree.findNode((Element) e.getEventTarget().cast());
      if (node != null && isSelected(node.getModel()) && getSelectedItems().size() > 1) {
        if (!xe.getCtrlOrMetaKey() && !e.getShiftKey()) {
          select(Collections.singletonList(node.getModel()), false);
        }
      }
    }
  }

  protected void onMouseDown(NativeEvent e) {
    XEvent xe = e.<XEvent> cast();
    Element target = e.getEventTarget().cast();
    TreeNode<M> node = tree.findNode(target);
    if (node == null) {
      return;
    }
    M item = (M) node.getModel();
    if (item == null) return;
    if (!tree.getView().isSelectableTarget(item, target)) {
      return;
    }
    if (e.<XEvent> cast().isRightClick() && isSelected((M) item)) {
      return;
    }
    
    mouseDown = true;
    
    M sel = item;
    switch (selectionMode) {
      case SIMPLE:
        if (isSelected(sel)) {
          deselect(sel);
        } else {
          doSelect(Collections.singletonList(sel), true, false);
        }
        break;
      case SINGLE:
        tree.focus();
        doSingleSelect(sel, false);
        break;
      case MULTI:
        if (isSelected(sel) && !xe.getCtrlOrMetaKey() && !e.getShiftKey()) {
          return;
        }
        if (e.getShiftKey() && lastSelected != null) {
          List<M> items = new ArrayList<M>();
          if (lastSelected == sel) {
            return;
          }
          TreeNode<M> selNode = tree.findNode(lastSelected);
          TreeNode<M> itemNode = tree.findNode(sel);
          if (selNode.getElement() != null && itemNode.getElement() != null) {
            if (selNode.getElement().getAbsoluteTop() < itemNode.getElement().getAbsoluteTop()) {
              M next = next();
              while (next != null) {
                items.add(next);
                lastSelected = next;
                if (next == sel) break;
                next = next();
              }
            } else {
              M prev = prev();
              while (prev != null) {
                items.add(prev);
                lastSelected = prev;
                if (prev == sel) break;
                prev = prev();
              }
            }
            tree.focus();
            doSelect(items, true, false);
          }
        } else if (xe.getCtrlOrMetaKey() && isSelected(sel)) {
          tree.focus();
          doDeselect(Collections.singletonList(sel), false);
        } else {
          tree.focus();
          doSelect(Collections.singletonList(sel), xe.getCtrlOrMetaKey(), false);
        }
        break;
    }
    
    mouseDown = false;
  }

  @Override
  protected void onSelectChange(M model, boolean select) {
    tree.getView().onSelectChange(model, select);
  }

  protected M prev() {
    M sel = lastSelected;
    if (sel == null) {
      return sel;
    }
    M prev = treeStore.getPreviousSibling(sel);
    if (prev != null) {
      if ((!tree.isExpanded(prev) || treeStore.getChildCount(prev) < 1)) {
        return prev;
      } else {
        M lastChild = treeStore.getLastChild(prev);
        while (lastChild != null && treeStore.getChildCount(lastChild) > 0 && tree.isExpanded(lastChild)) {
          lastChild = treeStore.getLastChild(lastChild);
        }
        return lastChild;
      }
    } else {
      M parent = treeStore.getParent(sel);
      if (parent != null) {
        return parent;
      }
    }
    return null;
  }
}
