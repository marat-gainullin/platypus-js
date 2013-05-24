/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.tree;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;
import com.sencha.gxt.widget.core.client.tree.Tree.Joint;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;

public class TreeView<M> {

  public enum TreeViewRenderMode {
    /**
     * Render the entire node.
     */
    ALL,
    /**
     * Render the node body, used with buffered rendering.
     */
    BUFFER_BODY,
    /**
     * Render the node wrapper, used with buffered rendering.
     */
    BUFFER_WRAP
  };

  protected TreeNode<M> over;
  protected Tree<M, ?> tree;
  protected TreeStore<M> treeStore;

  private int cacheSize = 20;
  private int cleanDelay = 500;
  private int scrollDelay = 1;

  @SuppressWarnings("unchecked")
  public void bind(Component component, Store<M> store) {
    this.tree = (Tree<M, ?>) component;
    this.treeStore = (TreeStore<M>) store;
  }

  public void collapse(TreeNode<M> node) {
    getContainer(node).getStyle().setDisplay(Display.NONE);
    tree.refresh(node.getModel());
  }

  public void expand(TreeNode<M> node) {
    getContainer(node).getStyle().setDisplay(Display.BLOCK);
    tree.refresh(node.getModel());
  }

  /**
   * Returns the cache size.
   * 
   * @return the cache size
   */
  public int getCacheSize() {
    return cacheSize;
  }

  public Element getCheckElement(TreeNode<M> node) {
    if (node.getCheckElement() == null) {
      node.setCheckElement(getElementContainer(node) != null
          ? tree.appearance.getCheckElement(getElementContainer(node)) : null);
    }
    return node.getCheckElement();
  }

  public int getCleanDelay() {
    return cleanDelay;
  }

  public Element getContainer(TreeNode<M> node) {
    if (node.getContainerElement() == null) {
      SafeHtmlBuilder sb = new SafeHtmlBuilder();
      tree.appearance.renderContainer(sb);
      node.setContainerElement(node.getElement().appendChild(XDOM.create(sb.toSafeHtml())));
    }
    return node.getContainerElement();
  }

  public XElement getElementContainer(TreeNode<M> node) {
    if (node.getElementContainer() == null) {
      node.setElContainer(node.getElement() != null
          ? tree.appearance.getContainerElement(node.getElement().<XElement> cast()) : null);
    }
    return node.getElementContainer().cast();
  }

  public Element getIconElement(TreeNode<M> node) {
    if (node.getIconElement() == null) {
      node.setIconElement(getElementContainer(node) != null ? tree.appearance.getIconElement(getElementContainer(node))
          : null);
    }
    return node.getIconElement();
  }

  public Element getJointElement(TreeNode<M> node) {
    if (node.getJointElement() == null) {
      node.setJointElement(tree.appearance.getJointElement(getElementContainer(node)));
    }
    return node.getJointElement();
  }

  public int getScrollDelay() {
    return scrollDelay;
  }

  public SafeHtml getTemplate(M m, String id, SafeHtml text, ImageResource icon, boolean checkable, CheckState checked,
      Joint joint, int level, TreeViewRenderMode renderMode) {
    SafeHtmlBuilder sb = new SafeHtmlBuilder();
    tree.appearance.renderNode(sb, id, text, tree.getStyle(), icon, checkable, checked, joint, level, renderMode);
    return sb.toSafeHtml();
  }

  public Element getTextElement(TreeNode<M> node) {
    if (node.getTextElement() == null) {
      node.setTextElement(getElementContainer(node) != null ? tree.appearance.getTextElement(getElementContainer(node))
          : null);
    }
    return node.getTextElement();
  }

  public boolean isSelectableTarget(M m, Element target) {
    TreeNode<M> n = findNode(m);
    if (n == null) {
      return false;
    }
    XElement xtarget = target.cast();
    boolean joint = tree.appearance.isJointElement(xtarget);
    if (joint) {
      return false;
    }
    if (!joint && tree.isCheckable()) {
      return !tree.appearance.isCheckElement(xtarget);
    }
    return true;
  }

  public void onCheckChange(TreeNode<M> node, boolean checkable, CheckState state) {
    Element checkEl = (Element) getCheckElement(node);
    if (checkEl != null) {
      node.setCheckElement(tree.appearance.onCheckChange(node.getElement().<XElement> cast(),
          checkEl.<XElement> cast(), checkable, state));
    }
  }

  public void onDropChange(TreeNode<M> node, boolean drop) {
    XElement e = tree.getView().getElementContainer(node);
    tree.appearance.onDropOver(e, drop);
  }

  public void onEvent(Event ce) {
    int type = ce.getTypeInt();
    switch (type) {
      case Event.ONMOUSEOVER:
        if (tree.isTrackMouseOver()) {
          onMouseOver(ce);
        }
        break;
      case Event.ONMOUSEOUT:
        if (tree.isTrackMouseOver()) {
          onMouseOut(ce);
        }
        break;
    }
  }

  public void onIconStyleChange(TreeNode<M> node, ImageResource icon) {
    Element iconEl = getIconElement(node);
    if (iconEl != null) {
      Element e;
      if (icon != null) {
        e = getImage(icon);
      } else {
        e = DOM.createSpan();
      }
      node.setIconElement((Element) node.getElement().getFirstChild().insertBefore(e, iconEl));
      iconEl.removeFromParent();
    }
  }

  public void onJointChange(TreeNode<M> node, Joint joint) {
    Element jointEl = getJointElement(node);
    if (jointEl != null) {
      node.setJointElement(tree.appearance.onJointChange(node.getElement().<XElement> cast(),
          jointEl.<XElement> cast(), joint, tree.getStyle()));
    }
  }

  public void onLoading(TreeNode<M> node) {
    // onIconStyleChange(node, IconHelper.createStyle("x-tree3-loading"));
  }

  public void onOverChange(TreeNode<M> node, boolean over) {
    tree.appearance.onHover(getElementContainer(node).<XElement> cast(), over);
  }

  public void onSelectChange(M model, boolean select) {
    if (select) {
      M p = treeStore.getParent(model);
      if (p != null) {
        tree.setExpanded(treeStore.getParent(model), true);
      }
    }
    TreeNode<M> node = findNode(model);
    if (node != null) {
      Element e = getElementContainer(node);
      if (e != null) {
        tree.appearance.onSelect(e.<XElement> cast(), select);
      }
    }
  }

  public void onTextChange(TreeNode<M> node, SafeHtml text) {
    Element textEl = getTextElement(node);
    if (textEl != null) {
      textEl.setInnerHTML(Util.isEmptyString(text.asString()) ? "&#160;" : text.asString());
    }
  }

  public void setCacheSize(int cacheSize) {
    this.cacheSize = cacheSize;
  }

  public void setCleanDelay(int cleanDelay) {
    this.cleanDelay = cleanDelay;
  }

  public void setScrollDelay(int scrollDelay) {
    this.scrollDelay = scrollDelay;
  }

  protected TreeNode<M> findNode(M m) {
    return tree.findNode(m);
  }

  protected int getCalculatedRowHeight() {
    return 21;
  }

  protected int getIndenting(TreeNode<M> node) {
    return 18;
  }

  protected void onMouseOut(NativeEvent ce) {
    if (over != null) {
      onOverChange(over, false);
      over = null;
    }
  }

  protected void onMouseOver(NativeEvent ne) {
    TreeNode<M> node = getNode((Element) ne.getEventTarget().cast());
    if (node != null) {
      if (over != node) {
        onMouseOut(ne);
        over = node;
        onOverChange(over, true);
      }
    }
  }

  private Element getImage(ImageResource ir) {
    return AbstractImagePrototype.create(ir).createElement();
  }

  protected TreeNode<M> getNode(Element target) {
    return tree.findNode(target);
  }

}
