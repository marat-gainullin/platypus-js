/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreSortEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;

/**
 * {@link List}-like client side cache for elements. All operations are
 * performed on the currently visible set of elements.
 * 
 * @param <M> the model type
 */
public class ListStore<M> extends Store<M> {

  private final List<M> allItems;
  private List<M> visibleItems;

  /**
   * Creates a new store.
   * 
   * @param keyProvider the key provider
   */
  public ListStore(ModelKeyProvider<? super M> keyProvider) {
    super(keyProvider);
    visibleItems = allItems = new ArrayList<M>();
  }

  /**
   * Like add(M), except the item will be inserted at the given index in the
   * visible items. If filters are enabled, it will be added to the un-filtered
   * list after its previous sibling in the filtered list.
   * 
   * Note that the item may not be visible, depending on the filters configured.
   * In this case its position in the un-filtered list will still be after where
   * it would have been in the filtered list.
   * 
   * @param index - the index in the visible items. Must be a valid index, in
   *          the range [0,size()). If the store is sorted, the index will be
   *          ignored
   * @param item the item to be added
   */
  public void add(int index, M item) {
    // TODO before event?

    // if the store is already sorted, then ignore the index and insert where it
    // belongs
    if (isSorted()) {
      int insertionIndex = Collections.binarySearch(visibleItems, item, buildFullComparator());
      index = insertionIndex < 0 ? (-insertionIndex - 1) : insertionIndex;
    }
    if (isFiltered()) {
      // if the item is first, insert it first. Else, insert it after the
      // previous visible item
      final int allItemsIndex;
      if (isSorted()) {
        // binary search is probably overkill, can we use the index we've
        // already found?
        int insertionIndex = Collections.binarySearch(allItems, item, buildFullComparator());
        allItemsIndex = insertionIndex < 0 ? (-insertionIndex - 1) : insertionIndex;
      } else {
        allItemsIndex = index == 0 ? 0 : allItems.indexOf(visibleItems.get(index - 1)) + 1;
      }

      allItems.add(allItemsIndex, item);

      // add the item if visible, no need to re-run all filters
      if (!isFilteredOut(item)) {
        visibleItems.add(index, item);
        fireEvent(new StoreAddEvent<M>(index, item));
      }
    } else {
      allItems.add(index, item);

      fireEvent(new StoreAddEvent<M>(index, item));
    }
  }

  /**
   * Adds the given item to the end of the list.
   * 
   * @param item the item to be added
   */
  public void add(M item) {
    add(size(), item);
  }

  /**
   * Adds all items to the end of the list.
   * 
   * 
   * @param items the items to be added
   * @return true if all items added
   */
  public boolean addAll(Collection<? extends M> items) {
    return addAll(size(), items);
  }

  /**
   * Adds all items at the given position.
   * 
   * @param index the insert index
   * @param items the items to be added
   * @return true if all items added
   */
  public boolean addAll(int index, Collection<? extends M> items) {

    // re-apply filters, checking only newly appended items
    if (isFiltered()) {
      // TODO filter events?
      List<M> inserted = new ArrayList<M>();
      for (M item : items) {
        if (!isFilteredOut(item)) {
          inserted.add(item);
        }
      }
      int actualIndex = index == 0 ? 0 : allItems.indexOf(visibleItems.get(index - 1)) + 1;
      visibleItems.addAll(index, inserted);
      allItems.addAll(actualIndex, items);

      if (isSorted()) {
        // no slower than 2.x, and easier to implement
        applySort(true);
      }
      if (inserted.size() != 0) {

        if (isSorted()) {
          fireSortedAddEvents(items);
        } else {
          fireEvent(new StoreAddEvent<M>(visibleItems.size(), inserted));
        }
      }
    } else {
      allItems.addAll(index, items);
      if (isSorted()) {
        // no slower than 2.x, and easier to implement
        applySort(true);

        fireSortedAddEvents(items);

      } else {
        fireEvent(new StoreAddEvent<M>(index, new ArrayList<M>(items)));
      }
    }
    return true;
  }

  @Override
  public void applySort(boolean suppressEvent) {
    Collections.sort(visibleItems, buildFullComparator());
    // If filters are enabled, then visibleItems != allItems, so sort both
    if (visibleItems != allItems) {
      Collections.sort(allItems, buildFullComparator());
    }
    if (!suppressEvent) {
      fireEvent(new StoreSortEvent<M>());
    }
  }

  @Override
  public void clear() {
    super.clear();
    allItems.clear();
    visibleItems.clear();
    fireEvent(new StoreClearEvent<M>());
  }

  @Override
  public M findModelWithKey(String key) {
    for (int i = 0; i < size(); i++) {
      if (getKeyProvider().getKey(get(i)).equals(key)) {
        return get(i);
      }
    }
    return null;
  }

  /**
   * Gets the given item from the list. In keeping with the 2.x api, does not
   * throw exceptions if you request obviously unavailable items.
   * 
   * Index is relative to the ordered set of visible items.
   * 
   * @param index the specified index.
   * @return the visible item.
   */
  public M get(int index) {
    return index >= visibleItems.size() || index < 0 ? null : visibleItems.get(index);
  }

  /**
   * 
   * @return an ordered list of all items in the ListStore
   */
  @Override
  public List<M> getAll() {
    return Collections.unmodifiableList(visibleItems);
  }

  /**
   * Gets the position of the item in the set of visible items.
   * 
   * @param item the visible item
   * @return the index of the item
   */
  public int indexOf(M item) {
    return visibleItems.indexOf(item);
  }

  /**
   * A ListStore acts like a Java List, and stuff is arranged in a linear
   * fashion, so it may be advantageous to remove based on index. Note that this
   * index is relative to the visible items, so items not visible cannot be
   * removed in this way.
   * 
   * @param index the index of the item to remove
   */
  public M remove(int index) {
    // TODO before event?

    M model = get(index);

    // assert model != null?
    if (model != null) {
      if (isFiltered()) {
        allItems.remove(visibleItems.remove(index));
      } else {
        allItems.remove(index);
      }
      super.remove(model);

      fireEvent(new StoreRemoveEvent<M>(index, model));
    }

    return model;
  }

  /**
   * Removes the given item, visible or not, from the store.
   * 
   * @param model model to remove
   * @return true if item was removed
   */
  @Override
  public boolean remove(M model) {
    if (null == remove(indexOf(model))) {
      return allItems.remove(model);
    }
    return true;
  }

  /**
   * Replaces all the items in the store with given list of new items.
   * 
   * @param newItems the new contents of the store
   */
  public void replaceAll(List<M> newItems) {
    super.clear();
    allItems.clear();
    visibleItems.clear();

    if (isSorted()) {
      Collections.sort(newItems, buildFullComparator());
    }
    allItems.addAll(newItems);
    if (isFiltered()) {
      for (M item : newItems) {
        if (!isFilteredOut(item)) {
          visibleItems.add(item);
        }
      }
    }
    fireEvent(new StoreDataChangeEvent<M>());
  }

  /**
   * Returns the number of visible items.
   * 
   * @return the number of visible items
   */
  public int size() {
    return visibleItems.size();
  }

  /**
   * Creates a new list containing references to the items in the specified
   * range.
   * 
   * @param start the starting index
   * @param end the ending index
   * @return a list of references to the store items in given range
   */
  public List<M> subList(int start, int end) {
    List<M> sub = new ArrayList<M>();
    for (int i = start; i >= 0 && i < end && i < size(); i++) {
      sub.add(get(i));
    }
    return sub;
  }

  public void update(M item) {
    String itemKey = getKeyProvider().getKey(item);

    M oldItem = null;
    int oldIndex = -1;
    for (int i = 0; i < allItems.size(); i++) {
      if (getKeyProvider().getKey(allItems.get(i)).equals(itemKey)) {
        oldItem = allItems.get(i);
        oldIndex = i;
        allItems.set(i, item);
        break;
      }
    }
    assert oldItem != null && oldIndex != -1 : "Item was not already in the store, cannot be updated";

    // if the filter is enabled and the old item was visible, the new item will
    // be as well
    if (isFiltered() && !isFilteredOut(oldItem)) {
      visibleItems.set(indexOf(oldItem), item);
    }
    if (!isFiltered() || !isFilteredOut(oldItem)) {
      fireEvent(new StoreUpdateEvent<M>(Collections.singletonList(item)));
    }
  }

  @Override
  protected void applyFilters() {
    visibleItems = new ArrayList<M>();
    if (isFiltered()) {
      for (int i = 0; i < allItems.size(); i++) {
        M item = allItems.get(i);
        if (!isFilteredOut(item)) {
          visibleItems.add(item);
        }
      }
      fireEvent(new StoreFilterEvent<M>());
    } else if (visibleItems != allItems) {
      visibleItems = allItems;
      fireEvent(new StoreFilterEvent<M>());
    }
  }

  /**
   * Takes a collection of newly added items and fires a {@link StoreAddEvent}
   * for each one. The events are fired in store sequence order (i.e. the order
   * in which the items appear in the store).
   * 
   * @param items a collection of items that have been added to the store
   */
  protected void fireSortedAddEvents(Collection<? extends M> items) {
    TreeSet<StoreAddEvent<M>> events = new TreeSet<StoreAddEvent<M>>(new Comparator<StoreAddEvent<M>>() {
      @Override
      public int compare(StoreAddEvent<M> o1, StoreAddEvent<M> o2) {
        return o1.getIndex() - o2.getIndex();
      }
    });
    for (M m : items) {
      int idx = indexOf(m);
      events.add(new StoreAddEvent<M>(idx, m));
    }
    for (StoreAddEvent<M> event : events) {
      fireEvent(event);
    }
  }

  /**
   * Returns true if the given item is set to be removed by any filter.
   * 
   * @param item the model
   * @return true if filtered
   */
  protected boolean isFilteredOut(M item) {
    for (StoreFilter<M> filter : getFilters()) {
      if (!filter.select(this, item, item)) {
        return true;
      }
    }
    return false;
  }

}
