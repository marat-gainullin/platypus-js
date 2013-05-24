/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import java.util.Collections;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Element;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.KeyNav;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.event.RefreshEvent;
import com.sencha.gxt.widget.core.client.event.RefreshEvent.RefreshHandler;
import com.sencha.gxt.widget.core.client.event.XEvent;
import com.sencha.gxt.widget.core.client.selection.AbstractStoreSelectionModel;

/**
 * ListView selection model.
 * 
 * @param <M> the model type
 */
public class ListViewSelectionModel<M> extends AbstractStoreSelectionModel<M> {

  protected boolean enableNavKeys = true;
  protected KeyNav keyNav = new KeyNav() {

    @Override
    public void onDown(NativeEvent e) {
      if (isVertical) {
        onKeyDown(e);
      }
    }

    @Override
    public void onKeyPress(NativeEvent ce) {
      ListViewSelectionModel.this.onKeyPress(ce);
    }

    @Override
    public void onLeft(NativeEvent e) {
      if (!isVertical) {
        onKeyUp(e);
      }
    }

    @Override
    public void onRight(NativeEvent e) {
      if (!isVertical) {
        onKeyDown(e);
      }
    }

    @Override
    public void onUp(NativeEvent e) {
      if (isVertical) {
        onKeyUp(e);
      }
    }

  };
  protected ListStore<M> listStore;
  protected ListView<M, ?> listView;

  private boolean isVertical = true;
  private Handler handler = new Handler();

  private GroupingHandlerRegistration handlerRegistration = new GroupingHandlerRegistration();

  private class Handler implements MouseDownHandler, ClickHandler, RefreshHandler {

    @Override
    public void onClick(ClickEvent event) {
      handleMouseClick(event);
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
      handleMouseDown(event);
    }

    @Override
    public void onRefresh(RefreshEvent event) {
      refresh();
      if (getLastFocused() != null) {
        listView.onHighlightRow(listStore.indexOf(getLastFocused()), true);
      }
    }
  }

  /**
   * Binds the list view to the selection model.
   * 
   * @param listView the list view
   */
  public void bindList(ListView<M, ?> listView) {
    if (this.listView != null) {
      handlerRegistration.removeHandler();
      keyNav.bind(null);
      this.listStore = null;
      bind(null);
    }
    this.listView = listView;
    if (listView != null) {
      if (handlerRegistration == null) {
        handlerRegistration = new GroupingHandlerRegistration();
      }
      handlerRegistration.add(listView.addDomHandler(handler, MouseDownEvent.getType()));
      handlerRegistration.add(listView.addDomHandler(handler, ClickEvent.getType()));
      handlerRegistration.add(listView.addRefreshHandler(handler));
      keyNav.bind(listView);
      bind(listView.getStore());
      this.listStore = listView.getStore();
    }
  }

  /**
   * Returns true if up and down arrow keys are used for navigation. Else left
   * and right arrow keys are used.
   * 
   * @return the isVertical
   */
  public boolean isVertical() {
    return isVertical;
  }

  /**
   * Sets if up and down arrow keys or left and right arrow keys should be used
   * (defaults to true).
   * 
   * @param isVertical the isVertical to set
   */
  public void setVertical(boolean isVertical) {
    this.isVertical = isVertical;
  }

  protected void handleMouseClick(ClickEvent ce) {
    XEvent e = ce.getNativeEvent().<XEvent> cast();
    XElement target = e.getEventTargetEl();
   
    if (isLocked() || isInput(target)) {
      return;
    }
    
    if (fireSelectionChangeOnClick) {
      fireSelectionChange();
      fireSelectionChangeOnClick = false;
    }
    
    int index = listView.findElementIndex(target);
    
    if (index == -1) {
      deselectAll();
      return;
    }
    if (selectionMode == SelectionMode.MULTI) {
      M sel = listStore.get(index);
      if (e.getCtrlOrMetaKey() && isSelected(sel)) {
        doDeselect(Collections.singletonList(sel), false);
      } else if (e.getCtrlOrMetaKey()) {
        doSelect(Collections.singletonList(sel), true, false);
        listView.focusItem(index);
      } else if (isSelected(sel) && !e.getShiftKey() && !e.getCtrlOrMetaKey() && selected.size() > 1) {
        doSelect(Collections.singletonList(sel), false, false);
        listView.focusItem(index);
      }
    }

  }

  protected void handleMouseDown(MouseDownEvent mde) {
    XEvent e = mde.getNativeEvent().<XEvent> cast();
    XElement target = e.getEventTargetEl();
    int index = listView.findElementIndex(target);

    if (index == -1 || isLocked() || isInput(target)) {
      return;
    }
    
    mouseDown = true;
    
    if (e.isRightClick()) {
      if (selectionMode != SelectionMode.SINGLE && isSelected(listStore.get(index))) {
        return;
      }
      select(index, false);
      listView.focusItem(index);
    } else {
      M sel = listStore.get(index);
      if (selectionMode == SelectionMode.SIMPLE) {
        if (!isSelected(sel)) {
          listView.focusItem(index);
          select(sel, true);
        }

      } else if (selectionMode == SelectionMode.SINGLE) {
        if (e.getCtrlOrMetaKey() && isSelected(sel)) {
          deselect(sel);
        } else if (!isSelected(sel)) {
          listView.focusItem(index);
          select(sel, false);
        }
      } else if (!e.getCtrlOrMetaKey()) {
        if (e.getShiftKey() && lastSelected != null) {
          int last = listStore.indexOf(lastSelected);
          listView.focusItem(last);
          select(last, index, e.getCtrlOrMetaKey());

        } else if (!isSelected(sel)) {
          listView.focusItem(index);
          doSelect(Collections.singletonList(sel), false, false);
        }
      }
    }
    
    mouseDown = false;
  }

  protected boolean isInput(Element target) {
    String tag = target.getTagName();
    return "INPUT".equals(tag) || "TEXTAREA".equals(tag);
  }

  protected void onKeyDown(NativeEvent event) {
    XEvent e = event.<XEvent> cast();

    if (!e.getCtrlOrMetaKey() && selected.size() == 0 && getLastFocused() == null) {
      select(0, false);
    } else {
      int idx = listStore.indexOf(getLastFocused());
      if (idx >= 0 && (idx + 1) < listStore.size()) {
        if (e.getCtrlOrMetaKey() || (e.getShiftKey() && isSelected(listStore.get(idx + 1)))) {
          if (!e.getCtrlOrMetaKey()) {
            deselect(idx);
          }

          M lF = listStore.get(idx + 1);
          if (lF != null) {
            setLastFocused(lF);
            listView.focusItem(idx + 1);
          }

        } else {
          if (e.getShiftKey() && lastSelected != getLastFocused()) {
            select(listStore.indexOf(lastSelected), idx + 1, true);
            listView.focusItem(idx + 1);
          } else {
            if (idx + 1 < listStore.size()) {
              select(idx + 1, e.getShiftKey());
              listView.focusItem(idx + 1);
            }
          }
        }
      }
    }

    e.preventDefault();
  }

  protected void onKeyPress(NativeEvent event) {
    XEvent e = event.<XEvent> cast();

    if (lastSelected != null && enableNavKeys) {
      int kc = e.getKeyCode();
      if (kc == KeyCodes.KEY_PAGEUP || kc == KeyCodes.KEY_HOME) {
        e.stopEvent();
        select(0, false);
        listView.focusItem(0);
      } else if (kc == KeyCodes.KEY_PAGEDOWN || kc == KeyCodes.KEY_END) {
        e.stopEvent();
        int idx = listStore.indexOf(listStore.get(listStore.size() - 1));
        select(idx, false);
        listView.focusItem(idx);
      }
    }
    // if space bar is pressed
    if (e.getKeyCode() == 32) {
      if (getLastFocused() != null) {
        if (e.getShiftKey() && lastSelected != null) {
          int last = listStore.indexOf(lastSelected);
          int i = listStore.indexOf(getLastFocused());
          select(last, i, e.getCtrlOrMetaKey());
          listView.focusItem(i);
        } else {
          if (isSelected(getLastFocused())) {
            deselect(getLastFocused());
          } else {
            select(getLastFocused(), true);
            listView.focusItem(listStore.indexOf(getLastFocused()));
          }
        }
      }
    }
  }

  protected void onKeyUp(NativeEvent event) {
    XEvent e = event.<XEvent> cast();
    int idx = listStore.indexOf(getLastFocused());
    if (idx >= 1) {
      if (e.getCtrlOrMetaKey() || (e.getShiftKey() && isSelected(listStore.get(idx - 1)))) {
        if (!e.getCtrlOrMetaKey()) {
          deselect(idx);
        }

        M lF = listStore.get(idx - 1);
        if (lF != null) {
          setLastFocused(lF);
          listView.focusItem(idx - 1);
        }

      } else {

        if (e.getShiftKey() && lastSelected != getLastFocused()) {
          select(listStore.indexOf(lastSelected), idx - 1, true);
          listView.focusItem(idx - 1);
        } else {
          if (idx > 0) {
            select(idx - 1, e.getShiftKey());
            listView.focusItem(idx - 1);
          }
        }
      }
    }

    e.preventDefault();
  }

  @Override
  protected void onLastFocusChanged(M oldFocused, M newFocused) {
    int i;
    if (oldFocused != null) {
      i = listStore.indexOf(oldFocused);
      if (i >= 0) {
        listView.onHighlightRow(i, false);
      }
    }
    if (newFocused != null) {
      i = listStore.indexOf(newFocused);
      if (i >= 0) {
        listView.onHighlightRow(i, true);
      }
    }
  }

  void onRowUpdated(M model) {
    if (isSelected(model)) {
      onSelectChange(model, true);
    }
    if (getLastFocused() == model) {
      setLastFocused(getLastFocused());
    }
  }

  @Override
  protected void onSelectChange(M model, boolean select) {
    listView.onSelectChange(model, select);
  }

}
