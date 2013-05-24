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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreAddEvent.StoreAddHandler;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent.StoreClearHandler;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent.StoreDataChangeHandler;
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.data.shared.event.StoreFilterEvent.StoreFilterHandler;
import com.sencha.gxt.data.shared.event.StoreHandlers;
import com.sencha.gxt.data.shared.event.StoreHandlers.HasStoreHandlers;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent.StoreRecordChangeHandler;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent.StoreRemoveHandler;
import com.sencha.gxt.data.shared.event.StoreSortEvent;
import com.sencha.gxt.data.shared.event.StoreSortEvent.StoreSortHandler;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent.StoreUpdateHandler;

/**
 * Store is a client-side cache for collections of data. Modifications made to
 * the Store via Records are not passed right away to the data, allowing for the
 * changes to be committed or rolled back.
 * 
 * @param <M> the model type
 */
public abstract class Store<M> implements HasStoreHandlers<M> {
  /**
   * Represents a change that can occur to a given model. This interface may not
   * be required, it will depend on if legacy cases need it or not to allow
   * PropertyChange to be implemented another way
   * 
   * @param <M> the model type
   * @param <V> the value type (for the changed property in the model)
   */
  public interface Change<M, V> {
    /**
     * Gets a tag for this change, so that two changes, both making
     * modifications to the same field, can replace each other, as they must be
     * mutually exclusive
     * 
     * @return the tag
     */
    Object getChangeTag();

    /**
     * Gets the value that will be set on the model in modify(M).
     * 
     * @return the value
     */
    V getValue();

    /**
     * Checks to see if the given model already has the change
     * 
     * @param model the model
     * @return true if model already has the change
     */
    boolean isCurrentValue(M model);

    /**
     * Make the change recorded here to the given model
     * 
     * @param model the model
     */
    void modify(M model);
  }

  /**
   * ValueProvider-based change impl - takes a ValueProvider and the new value
   * to be changed. The ValueProvider instance should be reused, as it will be
   * the objecttag.
   * 
   * @param <M> the model type
   * @param <V> the value type (for the changed property in the model)
   */
  public static class PropertyChange<M, V> implements Change<M, V> {
    private final ValueProvider<? super M, V> access;
    private final V value;

    /**
     * Creates a new property change.
     * 
     * @param propertyAccess the changed property
     * @param value the changed value
     */
    public PropertyChange(ValueProvider<? super M, V> propertyAccess, V value) {
      access = propertyAccess;
      this.value = value;
    }

    public final Object getChangeTag() {
      return access;
    }

    public V getValue() {
      return value;
    }

    public boolean isCurrentValue(M model) {
      return value == null ? access.getValue(model) == null : value.equals(access.getValue(model));
    }

    public final void modify(M model) {
      access.setValue(model, value);
    }
  }

  /**
   * Records wrap model instances and provide specialized editing features,
   * including modification tracking and editing capabilities.
   */
  public class Record {

    private final M model;
    private final Map<Object, Change<M, ?>> changes = new HashMap<Object, Store.Change<M, ?>>();

    /**
     * Creates a new record that wraps the given model.
     * 
     * @param model the model to be wrapped by this record
     */
    public Record(M model) {
      this.model = model;
    }

    /**
     * Adds a change to the data in this Record. If auto commit is enabled, the
     * change will be made directly to the model, else the change will be queued
     * up until commit() is called.
     * 
     * @param <V> the value type (for the changed property in the model)
     * @param property the property to change
     * @param value the changed value
     */
    public <V> void addChange(ValueProvider<? super M, V> property, V value) {
      if (!isAutoCommit) {
        PropertyChange<M, V> c = new PropertyChange<M, V>(property, value);
        if (c.isCurrentValue(model)) {
          changes.remove(c.getChangeTag());
          if (changes.size() == 0) {
            modifiedRecords.remove(this);
          }
        } else {
          changes.put(c.getChangeTag(), c);
          modifiedRecords.add(this);
        }
        fireEvent(new StoreRecordChangeEvent<M>(this, property));
      } else {
        property.setValue(model, value);
        fireEvent(new StoreUpdateEvent<M>(Collections.singletonList(this.model)));
      }
    }

    /**
     * Commits the changes to the model tracked by this record.
     */
    public void commit(boolean fireEvent) {
      if (isDirty()) {
        for (Change<M, ?> c : changes.values()) {
          assert c.isCurrentValue(model) == false : "Current value was somehow stored in a record's change set!";
          c.modify(model);
        }
        changes.clear();
        if (fireEvent) {
          fireEvent(new StoreUpdateEvent<M>(Collections.singletonList(this.model)));
        }
      }
    }

    /**
     * Gets the current Change object applied to that property, if any.
     * 
     * @param <V> the value type (for the changed property in the model)
     * @param property the changed property
     * @return a Change object, or null if the value is the default
     */
    @SuppressWarnings("unchecked")
    public <V> Change<M, V> getChange(ValueProvider<? super M, V> property) {
      // This will be typesafe ONLY if only addChange(ValueProvider<M,V>, V) is
      // called
      // if we keep this, kill the other addChange, or the Change interface
      // itself
      return (Change<M, V>) changes.get(property);
    }

    /**
     * Returns all changes.
     * 
     * @return collection of the changes
     */
    public Collection<Change<M, ?>> getChanges() {
      return changes.values();
    }

    /**
     * Returns the wrapped model instance.
     * 
     * @return the model
     */
    public M getModel() {
      return model;
    }

    /**
     * Gets the current value of this property in the record, whether it is
     * saved or not.
     * 
     * The value on the model in this record can be obtained by calling
     * property.getValue(record.getModel())
     * 
     * @param <V> the value type (for the property in the model)
     * @param property the property containing the value to get
     * @return current value of this property
     */
    public <V> V getValue(ValueProvider<? super M, V> property) {
      Change<M, V> change = getChange(property);
      if (change == null) {
        return property.getValue(model);
      } else {
        return change.getValue();
      }
    }

    /**
     * Returns true if the record has uncommitted changes.
     * 
     * @return the dirty state
     */
    public boolean isDirty() {
      return !changes.isEmpty();
    }

    /**
     * Rejects a single change made to the Record since its creation, or since
     * the last commit operation.
     * 
     * Fires a {@link StoreUpdateEvent} if a change is made.
     * 
     * @param property the property of the model to revert
     */
    public void revert(ValueProvider<? super M,?> property) {
      if (changes.remove(property) != null) {
        fireEvent(new StoreUpdateEvent<M>(Collections.singletonList(this.model)));
      }
    }

    /**
     * Rejects all changes made to the Record since either creation, or the last
     * commit operation. Modified fields are reverted to their original values.
     */
    public void revert() {
      changes.clear();

      fireEvent(new StoreUpdateEvent<M>(Collections.singletonList(this.model)));
    }
  }

  /**
   * Defines the interface for store filters.
   * 
   * Filters receive only the last stored version of data, in contrast to 2.x,
   * where the current changed value was always available. To get the change
   * value, ask the Store for a Record instance.
   * 
   * @param <M> the model type
   */
  public interface StoreFilter<M> {
    /**
     * Indicates if the given item should be kept visible in the store. If false
     * is returned, the item will not be visible, and if true is returned, the
     * item may be visible, pending any other filter's decision.
     * 
     * @param store the store containing the item to be kept visible
     * @param parent the parent of the item (for hierarchical stores)
     * @param item the item to keep visible
     * @return true if the item will be visible, false if not
     */
    public boolean select(Store<M> store, M parent, M item);
  }

  /**
   * Sort information for a Store to use. Constructors make it possible to
   * easily sort based on either a property of the items in the store, or sort
   * the items themselves.
   * 
   * Sort direction may be changed after creation, but the comparator is fixed.
   * A new StoreSortInfo object must be created to change the comparator.
   * 
   * @param <M> the model type
   */
  public static class StoreSortInfo<M> implements Comparator<M> {
    private SortDir direction;
    private final Comparator<M> comparator;
    private final ValueProvider<? super M, ?> valueProvider;

    /**
     * Creates a sort info object based on the given comparator to act on the
     * item itself. Complex comparators can easily be built in this way, instead
     * of adding multiple StoreSortInfo objects, or using one of the other
     * constructors.
     * 
     * @param itemComparator the comparator to use to sort the items
     * @param direction the sort direction
     */
    public StoreSortInfo(Comparator<M> itemComparator, SortDir direction) {
      this.comparator = itemComparator;
      this.direction = direction;
      this.valueProvider = null;
    }

    /**
     * Creates a sort info object to act on a property of the items and a custom
     * comparator for that property's type.
     * 
     * @param <V> the property type
     * @param property the sort property
     * @param itemComparator the comparator to use in the sort
     * @param direction the sort direction
     */
    public <V> StoreSortInfo(final ValueProvider<? super M, V> property, final Comparator<V> itemComparator, SortDir direction) {
      this.valueProvider = property;
      this.direction = direction;
      this.comparator = new Comparator<M>() {
        public int compare(M o1, M o2) {
          return itemComparator.compare(property.getValue(o1), property.getValue(o2));
        }
      };
    }

    /**
     * Convenience constructor for sorting based on a {@link Comparable}
     * property of items in the store.
     * 
     * @param <V> the property type
     * @param property the sort property
     * @param direction the sort direction
     */
    public <V extends Comparable<V>> StoreSortInfo(final ValueProvider<? super M, V> property, SortDir direction) {
      this.valueProvider = property;
      this.direction = direction;
      this.comparator = new Comparator<M>() {
        public int compare(M o1, M o2) {
          V v1 = property.getValue(o1);
          V v2 = property.getValue(o2);
          if ((v1 == null & v2 != null) || (v1 != null && v2 == null)) {
            return v1 == null ? -1 : 1;
          }
          if (v1 == null & v2 == null) {
            return 0;
          }
          return v1.compareTo(v2);
        }
      };
    }

    @Override
    public int compare(M o1, M o2) {
      int val = comparator.compare(o1, o2);
      return direction == SortDir.ASC ? val : -val;
    }

    /**
     * Returns the current sort direction for this sort info.
     * 
     * @return the current sort direction
     */
    public SortDir getDirection() {
      return direction;
    }

    /**
     * If the sort info object is configured to act on a property of the items,
     * returns the path that the property's ValueProvider makes available,
     * otherwise returns empty string.
     * 
     * @return the path for the property value provider or empty string if no
     *         value provider is configured
     */
    public String getPath() {
      return valueProvider != null ? valueProvider.getPath() : "";
    }

    /**
     * Returns the sort property's ValueProvider.
     * 
     * @return the sort property's ValueProvider or null if one has not been
     *         configured
     */
    public ValueProvider<? super M, ?> getValueProvider() {
      return valueProvider;
    }

    /**
     * Sets a new sort direction. Will not take effect until
     * {@link Store#applySort(boolean)} is called on the store containing the
     * sort info.
     * 
     * @param direction the sort direction
     */
    public void setDirection(SortDir direction) {
      this.direction = direction;
    }
  }

  // TODO lazily init these?
  private Map<M, Record> records = new HashMap<M, Record>();
  private Set<Record> modifiedRecords = new HashSet<Record>();
  private boolean isAutoCommit = false;
  private ModelKeyProvider<? super M> keyProvider;
  private List<StoreSortInfo<M>> comparators = new ArrayList<StoreSortInfo<M>>();
  private HandlerManager handlerManager;
  private boolean filtersEnabled;

  /**
   * Using a LinkedHashSet so each filter can only be added once, and order
   * matters
   */
  private LinkedHashSet<StoreFilter<M>> filters;

  /**
   * Creates a store with the given key provider. The key provider is
   * responsible for returning a unique key for a given model
   * 
   * @param keyProvider the key provider, responsible for returning a unique key
   *          for a given model
   */
  public Store(ModelKeyProvider<? super M> keyProvider) {
    this.keyProvider = keyProvider;
  }

  /**
   * Adds the filter to the end of the store's set of filters. Runs the filters
   * again if they are enabled.
   * 
   * @param filter the filter to add
   */
  public void addFilter(StoreFilter<M> filter) {
    if (filters == null) {
      filters = new LinkedHashSet<Store.StoreFilter<M>>();
    }
    filters.add(filter);

    if (filtersEnabled) {
      // TODO consider not running the full set of filters, just limiting what
      // is already visible
      applyFilters();
    }
  }

  /**
   * Adds the sort info at the specified index. The store will be sorted after
   * this change.
   * 
   * @param index the sort index
   * @param info the sort info
   */
  public void addSortInfo(int index, StoreSortInfo<M> info) {
    comparators.add(index, info);
    applySort(false);
  }

  /**
   * Adds the specified sort info to the store. The store will then be sorted
   * based on this new sort info.
   * 
   * @param info the sort info
   */
  public void addSortInfo(StoreSortInfo<M> info) {
    comparators.add(info);
    applySort(false);
  }

  @Override
  public HandlerRegistration addStoreAddHandler(StoreAddHandler<M> handler) {
    return ensureHandlers().addHandler(StoreAddEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addStoreClearHandler(StoreClearHandler<M> handler) {
    return ensureHandlers().addHandler(StoreClearEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addStoreDataChangeHandler(StoreDataChangeHandler<M> handler) {
    return ensureHandlers().addHandler(StoreDataChangeEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addStoreFilterHandler(StoreFilterHandler<M> handler) {
    return ensureHandlers().addHandler(StoreFilterEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addStoreHandlers(StoreHandlers<M> handlers) {
    GroupingHandlerRegistration reg = new GroupingHandlerRegistration();
    reg.add(addStoreAddHandler(handlers));
    reg.add(addStoreRemoveHandler(handlers));
    reg.add(addStoreClearHandler(handlers));
    reg.add(addStoreDataChangeHandler(handlers));
    reg.add(addStoreFilterHandler(handlers));
    reg.add(addStoreUpdateHandler(handlers));
    reg.add(addStoreRecordChangeHandler(handlers));
    reg.add(addStoreSortHandler(handlers));
    return reg;
  }

  @Override
  public HandlerRegistration addStoreRecordChangeHandler(StoreRecordChangeHandler<M> handler) {
    return ensureHandlers().addHandler(StoreRecordChangeEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addStoreRemoveHandler(StoreRemoveHandler<M> handler) {
    return ensureHandlers().addHandler(StoreRemoveEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addStoreSortHandler(StoreSortHandler<M> handler) {
    return ensureHandlers().addHandler(StoreSortEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addStoreUpdateHandler(StoreUpdateHandler<M> handler) {
    return ensureHandlers().addHandler(StoreUpdateEvent.getType(), handler);
  }

  /**
   * Tells the store to re-apply sort settings and to fire an event when
   * complete. Must be called when manipulating the sort settings directly
   * instead of through the store, or the sort order will not change.
   * 
   * Automatically called after {@link #addSortInfo(StoreSortInfo)},
   * {@link #addSortInfo(int, StoreSortInfo)}, and {@link #clearSortInfo()}.
   * 
   * @param suppressEvent true to suppress event from firing
   */
  public abstract void applySort(boolean suppressEvent);

  /**
   * Removes all of the sort info from the store, so subsequent calls to
   * applySort will not change the order.
   */
  public void clearSortInfo() {
    comparators.clear();
  }

  /**
   * Commits the outstanding changes.
   */
  public void commitChanges() {
    List<M> committedData = new ArrayList<M>();
    for (Record r : modifiedRecords) {
      r.commit(false);
      committedData.add(r.getModel());
    }
    modifiedRecords.clear();
    fireEvent(new StoreUpdateEvent<M>(committedData));
  }

  /**
   * Finds the matching model using the store's key provider. This can be used
   * to check if an item is present in the store, as it will return null if not.
   * 
   * @param model target model
   * @return the matching model or null if the model is not present
   */
  public M findModel(M model) {
    return findModelWithKey(getKeyProvider().getKey(model));
  }

  /**
   * Finds the model with the given key, using {@link ModelKeyProvider} as
   * necessary.
   * 
   * @param key the key of the model to find
   * @return the model with the given key, or null if the model cannot be found
   *         in the store
   */
  public abstract M findModelWithKey(String key);

  public void fireEvent(GwtEvent<?> event) {
    if (handlerManager != null) {
      handlerManager.fireEvent(event);
    }
  }

  /**
   * Returns a list of all items contained in the store. Modifying this list
   * will not change the store, as this is a copy of the contents of the store.
   * Note also that because this is a copy, this can be a expensive call to
   * make.
   * 
   * @return the list of items
   */
  public abstract List<M> getAll();

  /**
   * Returns the stores filters.
   * 
   * @return the filters
   */
  public LinkedHashSet<StoreFilter<M>> getFilters() {
    return filters;
  }

  /**
   * Returns the stores model key provider.
   * 
   * @return the model key provider
   */
  public ModelKeyProvider<? super M> getKeyProvider() {
    return keyProvider;
  }

  /**
   * Returns a list of records that have been changed and not committed.
   * 
   * @return the list of modified records
   */
  public Collection<Store<M>.Record> getModifiedRecords() {
    return Collections.unmodifiableCollection(modifiedRecords);
  }

  /**
   * Gets the current Record instance for the given item.
   * 
   * @param data the data key
   * @return the record
   */
  public Record getRecord(M data) {
    Record rec = records.get(data);
    if (rec == null) {
      rec = new Record(data);
      records.put(data, rec);
    }
    return rec;
  }

  /**
   * Gets the list of sort info objects. This list may be modified directly, but
   * before it takes effect, {@link #applySort(boolean)} must be called. Note
   * that {@link #addSortInfo(StoreSortInfo)} and
   * {@link #addSortInfo(int, StoreSortInfo)} will add the new sort info the
   * this list, and then call applySort directly.
   * 
   * @return the current mutable list of StoreSortInfo instances
   */
  public List<StoreSortInfo<M>> getSortInfo() {
    return comparators;
  }

  /**
   * Returns true if the two models have the same key.
   * 
   * @param model1 the first model
   * @param model2 the second model
   * @return true if equals
   */
  public boolean hasMatchingKey(M model1, M model2) {
    return keyProvider.getKey(model1).equals(keyProvider.getKey(model2));
  }

  /**
   * Returns true if a record exists for the given model.
   * 
   * @param data the model
   * @return true if a record exists
   */
  public boolean hasRecord(M data) {
    return records.get(data) != null;
  }

  /**
   * Returns true if auto commit is enabled.
   * 
   * @return true if auto commit is enabled
   */
  public boolean isAutoCommit() {
    return isAutoCommit;
  }

  /**
   * Returns true if filtering is enabled, whether or not filters are present.
   * 
   * @return true if filtering is enabled
   */
  public boolean isEnableFilters() {
    return filtersEnabled;
  }

  /**
   * Returns true if filtering is enabled AND the store has filters.
   * 
   * @return true if the store is filtered
   */
  public boolean isFiltered() {
    return filtersEnabled && filters != null && filters.size() != 0;
  }

  /**
   * Cancel outstanding changes on all changed records.
   */
  public void rejectChanges() {
    for (Record r : modifiedRecords) {
      r.revert();
    }
    modifiedRecords.clear();
  }

  /**
   * Removes the filter from the store's set of filters. Runs the filters again
   * if they are enabled, and the filter was actually in the list.
   * 
   * @param filter the filter to be removed
   */
  public void removeFilter(StoreFilter<M> filter) {
    if (filters != null) {
      if (filters.remove(filter) && filtersEnabled) {
        // the list of active filters has changed, unless we cache what showed
        // what item, we need to re-run the whole set
        applyFilters();
      }
    }
  }

  /**
   * Removes all filters.
   */
  public void removeFilters() {
    if (filters != null) {
      filters.clear();
      applyFilters();
    }
  }

  /**
   * Enables or disables auto commit. If auto commit is enabled, the change will
   * be made directly to the model, else the change will be queued up until
   * commit() is called.
   * 
   * @param isAutoCommit true to enable auto commit
   * @see Record#addChange(ValueProvider, Object)
   */
  public void setAutoCommit(boolean isAutoCommit) {
    this.isAutoCommit = isAutoCommit;
  }

  /**
   * Enables or disables the filters.
   * 
   * @param enableFilters true to enable filters
   */
  public void setEnableFilters(boolean enableFilters) {
    if (this.filtersEnabled == enableFilters) {
      return;
    }
    this.filtersEnabled = enableFilters;
    applyFilters();
  }

  /**
   * Replaces the item that matches the key of the given item, and fires a
   * {@link StoreUpdateEvent} to indicate that this change has occurred.
   * 
   * This will not cause the sort or filter to be re-applied to the object.
   * 
   * @param item the new item to take its place in the Store.
   */
  public abstract void update(M item);

  /**
   * Sets the filters to run again, whether they need it or not. Will fire a
   * Filter event if anything has changed.
   */
  protected abstract void applyFilters();

  /**
   * Creates a new master <code>Comparator</code> that runs all the
   * {@link StoreSortInfo} comparators in sequence, returning the value of the
   * first comparator that returns a non-zero value, or zero if no comparator
   * returns a non-zero value.
   * 
   * @return the new master comparator
   */
  protected Comparator<M> buildFullComparator() {
    return new Comparator<M>() {
      public int compare(M o1, M o2) {
        for (int i = 0; i < comparators.size(); i++) {
          int val = comparators.get(i).compare(o1, o2);
          if (val != 0) {
            return val;
          }
        }
        return 0;
      }
    };
  }

  /**
   * Removes all items from the store.
   */
  protected void clear() {
    modifiedRecords.clear();
    records.clear();
  }

  /**
   * Ensures the store's handler manager exists, creating it if necessary (lazy
   * construction).
   * 
   * @return the store's handler manager
   */
  protected HandlerManager ensureHandlers() {
    if (handlerManager == null) {
      handlerManager = new HandlerManager(this);
    }
    return handlerManager;
  }

  /**
   * Returns true if the store is sorted ({@link StoreSortInfo} has been added
   * and not cleared).
   * 
   * @return true if the store is sorted.
   */
  protected boolean isSorted() {
    return comparators.size() != 0;
  }

  /**
   * Cleans up any reference the Store might've had to the model. Must be called
   * by subclasses. Returns a boolean, as {@link Collection#remove(Object)}
   * 
   * @param model the data model to remove
   * @return boolean, indicating if it was removed from the Store. Subclasses
   *         should modify this to return false if necessary
   */
  protected boolean remove(M model) {
    if (records.containsKey(model)) {
      modifiedRecords.remove(records.remove(model));
    }
    return true;
  }

}
