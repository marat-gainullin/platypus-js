/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.selection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreAddEvent.StoreAddHandler;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent.StoreClearHandler;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent.StoreRecordChangeHandler;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent.StoreRemoveHandler;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent.StoreUpdateHandler;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.HasSelectionChangedHandlers;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

/**
 * Abstract base class for store based selection models.
 * 
 * @param <M> the model type contained within the store
 */
public abstract class AbstractStoreSelectionModel<M> implements StoreSelectionModel<M>, HasBeforeSelectionHandlers<M>,
    HasSelectionHandlers<M>, HasSelectionChangedHandlers<M> {

  private class Handler implements StoreAddHandler<M>, StoreRemoveHandler<M>, StoreClearHandler<M>,
      StoreRecordChangeHandler<M>, StoreUpdateHandler<M> {

    @Override
    public void onAdd(StoreAddEvent<M> event) {
      AbstractStoreSelectionModel.this.onAdd(event.getItems());
    }

    @Override
    public void onClear(StoreClearEvent<M> event) {
      AbstractStoreSelectionModel.this.onClear(event);
    }

    @Override
    public void onRecordChange(final StoreRecordChangeEvent<M> event) {
      // run defer to ensure the code runs after grid view refreshes row
      Scheduler.get().scheduleFinally(new ScheduledCommand() {
        @Override
        public void execute() {
          AbstractStoreSelectionModel.this.onRecordChange(event);
        }
      });
    }

    @Override
    public void onRemove(StoreRemoveEvent<M> event) {
      AbstractStoreSelectionModel.this.onRemove(event.getItem());
    }

    @Override
    public void onUpdate(StoreUpdateEvent<M> event) {
      final List<M> update = event.getItems();
      // run defer to ensure the code runs after grid view refreshes row
      Scheduler.get().scheduleFinally(new ScheduledCommand() {
        @Override
        public void execute() {
          for (int i = 0; i < update.size(); i++) {
            AbstractStoreSelectionModel.this.onUpdate(update.get(i));
          }
        }
      });

    }

  }

  protected M lastSelected;
  protected boolean locked;
  protected List<M> selected = new ArrayList<M>();
  protected SelectionMode selectionMode = SelectionMode.MULTI;

  protected Store<M> store;
  private Handler handler = new Handler();

  // events
  private HandlerManager handlerManager;
  private GroupingHandlerRegistration handlerRegistration = new GroupingHandlerRegistration();

  private M lastFocused;

  /**
   * If true the selection change event should be fired on click. When a
   * selection change event is fired on mouse down we don't actually fire the
   * event until click so that any active fields will be blurred BEFORE the
   * handler is run
   */
  protected boolean fireSelectionChangeOnClick;
  
  /**
   * Tracks if a mouse down event is currently being processed. When a selection
   * change event is fired on mouse down we don't actually fire the event until
   * click so that any active fields will be blurred BEFORE the handler is run.
   */
  protected boolean mouseDown;

  @Override
  public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<M> handler) {
    return ensureHandlers().addHandler(BeforeSelectionEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addSelectionChangedHandler(SelectionChangedHandler<M> handler) {
    return ensureHandlers().addHandler(SelectionChangedEvent.getType(), handler);
  }

  /**
   * Adds a {@link SelectionChangedHandler} handler for
   * {@link SelectionChangedEvent} events.
   * 
   * @since 3.0.1 This event is fired asynchronously.
   * 
   * @param handler the handler
   * @return the registration for the event
   */
  @Override
  public HandlerRegistration addSelectionHandler(SelectionHandler<M> handler) {
    return ensureHandlers().addHandler(SelectionEvent.getType(), handler);
  }

  @Override
  public void bind(Store<M> store) {
    deselectAll();
    if (this.store != null) {
      handlerRegistration.removeHandler();
    }
    this.store = store;
    if (store != null) {
      if (handlerRegistration == null) {
        handlerRegistration = new GroupingHandlerRegistration();
      }
      handlerRegistration.add(store.addStoreAddHandler(handler));
      handlerRegistration.add(store.addStoreRemoveHandler(handler));
      handlerRegistration.add(store.addStoreClearHandler(handler));
      handlerRegistration.add(store.addStoreUpdateHandler(handler));
      handlerRegistration.add(store.addStoreRecordChangeHandler(handler));
    }
  }

  @Override
  public void deselect(int index) {
    if (store instanceof ListStore) {
      ListStore<M> ls = (ListStore<M>) store;
      M m = ls.get(index);
      if (m != null) {
        doDeselect(Collections.singletonList(m), false);
      }
    }
  }

  @Override
  public void deselect(int start, int end) {
    if (store instanceof ListStore) {
      ListStore<M> ls = (ListStore<M>) store;
      List<M> list = new ArrayList<M>();
      for (int i = start; i < end; i++) {
        M m = ls.get(i);
        if (m != null) {
          list.add(m);
        }
      }
      doDeselect(list, false);
    }
  }

  @Override
  public void deselect(List<M> items) {
    doDeselect(items, false);
  }

  @Override
  public void deselect(M... items) {
    deselect(Arrays.asList(items));
  }

  @Override
  public void deselect(M item) {
    deselect(Collections.singletonList(item));
  }

  @Override
  public void deselectAll() {
    doDeselect(new ArrayList<M>(selected), false);
  }

  @Override
  public void fireEvent(GwtEvent<?> event) {
    if (handlerManager != null) {
      handlerManager.fireEvent(event);
    }
  }

  @Override
  public M getSelectedItem() {
    return lastSelected;
  }

  @Override
  public List<M> getSelectedItems() {
    return new ArrayList<M>(selected);
  }

  public List<M> getSelection() {
    return getSelectedItems();
  }

  @Override
  public SelectionMode getSelectionMode() {
    return selectionMode;
  }

  /**
   * Returns true if the selection model is locked.
   * 
   * @return the locked state
   */
  public boolean isLocked() {
    return locked;
  }

  @Override
  public boolean isSelected(M item) {
    for (M m : selected) {
      if (store.hasMatchingKey(item, m)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void refresh() {
    List<M> sel = new ArrayList<M>();
    boolean change = false;
    for (M m : selected) {
      M storeModel = store.findModel(m);
      if (storeModel != null) {
        sel.add(storeModel);
      }
    }
    if (sel.size() != selected.size()) {
      change = true;
    }
    selected.clear();
    lastSelected = null;
    setLastFocused(null);
    doSelect(sel, false, true);
    if (change) {
      fireSelectionChange();
    }
  }

  @Override
  public void select(boolean keepExisting, M... items) {
    select(Arrays.asList(items), keepExisting);
  }

  @Override
  public void select(int index, boolean keepExisting) {
    select(index, index, keepExisting);
  }

  @Override
  public void select(int start, int end, boolean keepExisting) {
    if (store instanceof ListStore) {
      ListStore<M> ls = (ListStore<M>) store;
      List<M> sel = new ArrayList<M>();
      if (start <= end) {
        for (int i = start; i <= end; i++) {
          sel.add(ls.get(i));
        }
      } else {
        for (int i = start; i >= end; i--) {
          sel.add(ls.get(i));
        }
      }
      doSelect(sel, keepExisting, false);
    }
  }

  @Override
  public void select(List<M> items, boolean keepExisting) {
    doSelect(items, keepExisting, false);
  }

  @Override
  public void select(M item, boolean keepExisting) {
    select(Collections.singletonList(item), keepExisting);
  }

  @Override
  public void selectAll() {
    select(store.getAll(), false);
  }

  /**
   * True to lock the selection model. When locked, all selection changes are
   * disabled.
   * 
   * @param locked true to lock
   */
  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  public void setSelection(List<M> selection) {
    select(selection, false);
  }

  @Override
  public void setSelectionMode(SelectionMode selectionMode) {
    this.selectionMode = selectionMode;
  }

  protected void doDeselect(List<M> models, boolean suppressEvent) {
    if (locked) return;
    boolean change = false;
    for (M m : models) {
      if (selected.remove(m)) {
        if (lastSelected == m) {
          lastSelected = selected.size() > 0 ? selected.get(selected.size() - 1) : null;
        }
        onSelectChange(m, false);
        change = true;
      }
    }
    if (!suppressEvent && change) {
      fireSelectionChange();
    }
  }

  protected void doMultiSelect(List<M> models, boolean keepExisting, boolean suppressEvent) {
    if (locked) return;
    boolean change = false;
    if (!keepExisting && selected.size() > 0) {
      change = true;
      doDeselect(new ArrayList<M>(selected), true);
    }
    for (M m : models) {
      boolean isListStore = false;
      int index = -1;
      if (store instanceof ListStore) {
        isListStore = true;
        ListStore<M> ls = (ListStore<M>) store;
        index = ls.indexOf(m);
      }

      if ((keepExisting && isSelected(m)) || (isListStore && index == -1)) {
        if (!suppressEvent) {

          BeforeSelectionEvent<M> evt = BeforeSelectionEvent.fire(this, m);
          if (evt != null && !evt.isCanceled()) {
            continue;
          }
        }
        continue;
      }

      change = true;
      lastSelected = m;

      selected.add(m);
      setLastFocused(lastSelected);
      onSelectChange(m, true);
      SelectionEvent.fire(this, m);
    }

    if (change && !suppressEvent) {
      fireSelectionChange();
    }
  }

  protected void doSelect(List<M> models, boolean keepExisting, boolean suppressEvent) {
    if (locked) return;
    if (selectionMode == SelectionMode.SINGLE) {
      M m = models.size() > 0 ? models.get(0) : null;
      if (m != null) {
        doSingleSelect(m, suppressEvent);
      }
    } else {
      doMultiSelect(models, keepExisting, suppressEvent);
    }
  }

  protected void doSingleSelect(M model, boolean suppressEvent) {
    if (locked) return;

    int index = -1;
    if (store instanceof ListStore) {
      ListStore<M> ls = (ListStore<M>) store;
      index = ls.indexOf(model);
    }
    if (store instanceof TreeStore) {
      TreeStore<M> ls = (TreeStore<M>) store;
      index = ls.indexOf(model);
    }
    if (index == -1 || isSelected(model)) {
      return;
    } else {
      if (!suppressEvent) {
        BeforeSelectionEvent<M> evt = BeforeSelectionEvent.fire(this, model);
        if (evt != null && evt.isCanceled()) {
          return;
        }
      }
    }

    boolean change = false;
    if (selected.size() > 0 && !isSelected(model)) {
      doDeselect(Collections.singletonList(lastSelected), true);
      change = true;
    }
    if (selected.size() == 0) {
      change = true;
    }
    selected.add(model);
    lastSelected = model;
    onSelectChange(model, true);
    setLastFocused(lastSelected);

    if (!suppressEvent) {
      SelectionEvent.fire(this, model);
    }

    if (change && !suppressEvent) {
      fireSelectionChange();
    }
  }

  protected HandlerManager ensureHandlers() {
    if (handlerManager == null) {
      handlerManager = new HandlerManager(this);
    }
    return handlerManager;
  }

  protected void fireSelectionChange() {
    if (mouseDown) {
      fireSelectionChangeOnClick = true;
    } else {
      fireEvent(new SelectionChangedEvent<M>(selected));
    }
  }

  protected M getLastFocused() {
    return lastFocused;
  }

  protected void onAdd(List<? extends M> models) {

  }

  protected void onClear(StoreClearEvent<M> event) {
    int oldSize = selected.size();
    selected.clear();
    lastSelected = null;
    setLastFocused(null);
    if (oldSize > 0) fireSelectionChange();
  }

  protected void onLastFocusChanged(M oldFocused, M newFocused) {

  }

  protected void onRecordChange(StoreRecordChangeEvent<M> event) {
    onUpdate(event.getRecord().getModel());
  }

  protected void onRemove(M model) {
    if (locked) return;
    if (selected.remove(model)) {
      if (lastSelected == model) {
        lastSelected = null;
      }
      if (getLastFocused() == model) {
        setLastFocused(null);
      }
      fireSelectionChange();
    }
  }

  protected abstract void onSelectChange(M model, boolean select);

  protected void onUpdate(M model) {
    if (locked) return;
    for (int i = 0; i < selected.size(); i++) {
      M m = selected.get(i);
      if (store.hasMatchingKey(model, m)) {
        if (m != model) {
          selected.remove(m);
          selected.add(i, model);
        }
        if (lastSelected == m) {
          lastSelected = model;
        }
        break;
      }
    }
    if (getLastFocused() != null && model != getLastFocused() && store.hasMatchingKey(model, getLastFocused())) {
      lastFocused = model;
    }
  }

  protected void setLastFocused(M lastFocused) {
    M lF = this.lastFocused;
    this.lastFocused = lastFocused;

    onLastFocusChanged(lF, lastFocused);
  }

}
