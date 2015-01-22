/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bearsoft.rowset.Utils.JsObject;
import com.bearsoft.rowset.beans.HasPropertyListeners;
import com.bearsoft.rowset.beans.PropertyChangeListener;
import com.bearsoft.rowset.beans.PropertyChangeSupport;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.changes.Delete;
import com.bearsoft.rowset.changes.Insert;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetChangeSupport;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetSortEvent;
import com.bearsoft.rowset.exceptions.FlowProviderFailedException;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.InvalidFieldsExceptionException;
import com.bearsoft.rowset.exceptions.MissingFlowProviderException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.ordering.Filter;
import com.bearsoft.rowset.ordering.Locator;
import com.bearsoft.rowset.ordering.Orderer;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Rowset serves as original and updated rows vectors holder. There are three
 * developing themes: - rowset's life, with it's data processing (sorting,
 * locating and filtering). - rowset's saving and restoring to and from variety
 * of sources (database, files and others). - applying and rolling back changes,
 * maded to it's data.
 * 
 * @author mg
 */
public class Rowset implements HasPropertyListeners{

	public static final String BAD_FLOW_PROVIDER_RESULT_MSG = "Flow Provider must return at least an empty rowset";
	// rowset's data changes log.
	protected List<Change> log;
	// support for data flows.
	protected FlowProvider flow;
	// rowset's metadata
	protected Fields fields;
	// rowset's data
	protected List<Row> original = new ArrayList<>();
	protected List<Row> current = new ArrayList<>();
	// data view capabilities
	protected int currentRowPos; // before first position
	// data processing
	protected Set<Orderer> orderers = new HashSet<>(); // orderers
	protected Set<Filter> filters = new HashSet<>(); // filters
	protected Set<Locator> locators = new HashSet<>(); // locators
	protected Filter activeFilter;
	protected Row insertingRow;
	// client code interaction
	protected PropertyChangeSupport propertyChangeSupport;
	protected RowsetChangeSupport rowsetChangeSupport;
	protected boolean immediateFilter = true;
	protected boolean modified;

	/**
	 * Simple constructor.
	 */
	public Rowset() {
		super();
		propertyChangeSupport = new PropertyChangeSupport(this);
		rowsetChangeSupport = new RowsetChangeSupport(this);
	}

	/**
	 * Rowset's metadata constructor.
	 * 
	 * @param aFields
	 *            Columns definition new rowset have to work with.
	 */
	public Rowset(Fields aFields) {
		this();
		fields = aFields;
	}

	/**
	 * Rowset's data flow constructor. Gets a converter from flow provider
	 * passed in if flow provider is JdbcFlowProvider.
	 * 
	 * @param aProvider
	 *            Data flow provider new rowset have to work with.
	 * @see DatabaseFlowProvider
	 */
	public Rowset(FlowProvider aProvider) {
		this();
		flow = aProvider;
	}

	public List<Change> getLog() {
		return log;
	}

	public void setLog(List<Change> aValue) {
		log = aValue;
	}

	/**
	 * Returns the flow provider instance, used by this rowset to support data
	 * flow process.
	 * 
	 * @return Current flow provider.
	 */
	public FlowProvider getFlowProvider() {
		return flow;
	}

	/**
	 * Sets the provider to be used in data flow process.
	 * 
	 * @param aFlowProvider
	 *            Flow provider to set.
	 */
	public void setFlowProvider(FlowProvider aFlowProvider) {
		flow = aFlowProvider;
	}

	// changes management support
	public void commited() throws Exception {
		final Set<RowsetListener> lrowsetListeners = rowsetChangeSupport.getRowsetListeners();
		rowsetChangeSupport.setRowsetListeners(null);
		try {
			currentToOriginal();
		} finally {
			rowsetChangeSupport.setRowsetListeners(lrowsetListeners);
		}
		rowsetChangeSupport.fireSavedEvent();
	}

	public void rolledback() throws Exception {
		rowsetChangeSupport.fireBeforeRollback();
		final Set<RowsetListener> lrowsetListeners = rowsetChangeSupport.getRowsetListeners();
		rowsetChangeSupport.setRowsetListeners(null);
		try {
			originalToCurrent();
			if (current.isEmpty()) {
				currentRowPos = 0;
			} else
				currentRowPos = 1;
		} finally {
			rowsetChangeSupport.setRowsetListeners(lrowsetListeners);
		}
		rowsetChangeSupport.fireRolledbackEvent();
	}

	/**
	 * Registers <code>PropertyChangeListener</code> on this rowset.
	 * 
	 * @param aListener
	 *            <code>PropertyChangeListener</code> to be registered.
	 */
	public void addPropertyChangeListener(PropertyChangeListener aListener) {
		propertyChangeSupport.addPropertyChangeListener(aListener);
	}

	public void addPropertyChangeListener(String aPropertyName, PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(aPropertyName, l);
	}

	/**
	 * Removes <code>PropertyChangeListener</code> from this rowset.
	 * 
	 * @param aListener
	 *            <code>PropertyChangeListener</code> to be removed.
	 */
	public void removePropertyChangeListener(PropertyChangeListener aListener) {
		propertyChangeSupport.removePropertyChangeListener(aListener);
	}

	public void removePropertyChangeListener(String aPropertyName, PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(aPropertyName, l);
	}

    @Override
    public PropertyChangeListener[] getPropertyChangeListeners() {
    	return propertyChangeSupport.getPropertyChangeListeners();
    }
    
	public void firePropertyChange(String aPropertyName, Object aOldValue, Object aNewValue) {
		propertyChangeSupport.firePropertyChange(aPropertyName, aOldValue, aNewValue);
	}

	/**
	 * Registers <code>RowsetListener</code> on this rowset.
	 * 
	 * @param aListener
	 *            <code>RowsetListener</code> to be registered.
	 */
	public void addRowsetListener(RowsetListener aListener) {
		rowsetChangeSupport.addRowsetListener(aListener);
	}

	/**
	 * Removes <code>RowsetListener</code> from this rowset.
	 * 
	 * @param aListener
	 *            <code>RowsetListener</code> to be removed.
	 */
	public void removeRowsetListener(RowsetListener aListener) {
		rowsetChangeSupport.removeRowsetListener(aListener);
	}

	/**
	 * Inner utility method for filters and some others.
	 * 
	 * @return <code>RowsetChangeSupport</code> instance from this rowset.
	 */
	public RowsetChangeSupport getRowsetChangeSupport() {
		return rowsetChangeSupport;
	}

	/**
	 * Columns definition getter.
	 * 
	 * @return Columns definition of this rowset.
	 */
	public Fields getFields() {
		return fields;
	}

	/**
	 * Columns definition setter.
	 * 
	 * @param aFields
	 *            Columns definition.
	 */
	public void setFields(Fields aFields) throws InvalidFieldsExceptionException {
		if (!current.isEmpty()) {
			Row row = current.get(0);
			assert row != null;
			if (row.getColumnCount() != aFields.getFieldsCount()) {
				throw new InvalidFieldsExceptionException("column count is wrong, expected: " + row.getColumnCount() + ", but " + aFields.getFieldsCount() + " is got");
			}
		} else if (!original.isEmpty()) {
			Row row = original.get(0);
			assert row != null;
			if (row.getColumnCount() != aFields.getFieldsCount()) {
				throw new InvalidFieldsExceptionException("column count is wrong, expected: " + row.getColumnCount() + ", but " + aFields.getFieldsCount() + " is got");
			}
		}
		fields = aFields;
	}

	/**
	 * Returns active (current) filter of this rowset. May be null.
	 * 
	 * @return Active (current) filter of this rowset. May be null.
	 */
	public Filter getActiveFilter() {
		return activeFilter;
	}

	/**
	 * Sets active (current) filter of this rowset. For internal use only.
	 * 
	 * @param aValue
	 *            Filter to be setted as active (current) filter of this rowset.
	 *            May be null.
	 */
	public void setActiveFilter(Filter aValue) {
		activeFilter = aValue;
	}

	/**
	 * Retruns this rowset's modified status.
	 * 
	 * @return True if this rowses have changes since last currentToOriginal()
	 *         and originalToCurrent() method calls
	 * @see #currentToOriginal()
	 * @see #originalToCurrent()
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * Sets the modified flag for this rowset. It's not recomended to use this
	 * method, but in some cases it may be useful.
	 * 
	 * @param aValue
	 */
	public void setModified(boolean aValue) {
		modified = aValue;
	}

	/**
	 * Returns whether installed filter have to immediately re-filter this
	 * rowset after updating a field which is filtering criteria.
	 * 
	 * @return True is filter have to re-filter this rowset immediately after
	 *         updating a field which is filtering criteria.
	 */
	public boolean isImmediateFilter() {
		return immediateFilter;
	}

	/**
	 * Sets immediateFilter flag for this rowset.
	 * 
	 * @param aValue
	 * @see #isImmediateFilter()
	 */
	public void setImmediateFilter(boolean aValue) {
		if (immediateFilter != aValue) {
			boolean oldValue = immediateFilter;
			immediateFilter = aValue;
			propertyChangeSupport.firePropertyChange("immediateFilter", oldValue, immediateFilter);
		}
	}

	public Cancellable refresh(final Callback<Rowset, String> aCallback) throws Exception {
		return refresh(new Parameters(), aCallback);
	}

	/**
	 * Queries some source for data, according to supplied parameters values. It
	 * queries data using flow provider installed on this rowset instance. It
	 * fires RowsetRequeriedEvent event. Call to refresh() will uninstall any
	 * installed filter and invalidate other filters, have been created on this
	 * rowset.
	 * 
	 * @param aParams
	 *            Parameters values, ordered with some unknown criteria.
	 * @see Parameters
	 */
	public Cancellable refresh(Parameters aParams, final Callback<Rowset, String> aCallback) throws Exception {
		if (flow != null) {
			if (rowsetChangeSupport.fireWillRequeryEvent()) {
				rowsetChangeSupport.fireBeforeRequery();
				return flow.refresh(aParams, new CallbackAdapter<Rowset, String>() {

					@Override
					protected void doWork(Rowset aRowset) throws Exception {
						if (aRowset != null) {
							if (activeFilter != null) {
								// No implicit calls to setCurrent and etc.
								activeFilter.deactivate();
								activeFilter = null;
							}
							if (fields == null) {
								setFields(aRowset.getFields());
							}
							List<Row> rows = aRowset.getCurrent();
							aRowset.setCurrent(new ArrayList<Row>());
							aRowset.currentToOriginal();
							for (int r = 0; r < current.size(); r++) {
								Row checked = current.get(r);
								//if (checked.isInserted() || checked.isUpdated() - if uncomment, then same rows will be fetched form a database) {
								if (checked.isInserted()) {
									rows.add(checked);
								}
							}
							setCurrent(rows);
							for (int r = 0; r < rows.size(); r++) {
								Row row = rows.get(r);
								row.setLog(log);
								row.setEntityName(flow.getEntityId());
							}
							setCurrent(rows);
							currentToOriginal();
							// silent first
							if (!current.isEmpty()) {
								currentRowPos = 1;
							}
							rowsetChangeSupport.fireRequeriedEvent();
							if (aCallback != null) {
								aCallback.onSuccess(Rowset.this);
							}
						} else {
							throw new FlowProviderFailedException(BAD_FLOW_PROVIDER_RESULT_MSG);
						}
					}

					@Override
					public void onFailure(String reason) {
						if (reason == null)
							reason = "Unknown network error. May be cancelled.";
						rowsetChangeSupport.fireNetErrorEvent(reason);
						if (aCallback != null) {
							aCallback.onFailure(reason);
						}
					}

				});
			}
			return null;
		} else {
			throw new MissingFlowProviderException();
		}
	}

	public void silentFirst() throws InvalidCursorPositionException {
		currentRowPos = isEmpty() ? 0 : 1;
	}

	/**
	 * Returns current rows vector. Used with filtering classes.
	 * 
	 * @return Current rows vector.
	 * @see HashOrderer
	 */
	public List<Row> getCurrent() {
		return current;
	}

	/**
	 * Returns original rows vector.
	 * 
	 * @return Original rows vector.
	 */
	public List<Row> getOriginal() {
		return original;
	}

	/**
	 * Sets current rows vector. Unsubscribes from old rows events and sub
	 * subscribes on new rows events.
	 * 
	 * @param aCurrent
	 *            Current rows list.
	 * @see HashOrderer
	 * @see Filter
	 * @see Locator
	 */
	public void setCurrent(List<Row> aCurrent) {
		assert fields != null;
		assert fields != null;
		current = aCurrent;
		currentRowPos = 0;
	}

	/**
	 * Returns rows count in this rowset. Takes into account
	 * <code>showOriginal</code> flag
	 * 
	 * @return Rows count in this rowset.
	 * @see #absolute(int aCursorPos)
	 * @see #getCursorPos()
	 * @see #beforeFirst()
	 * @see #first()
	 * @see #isBeforeFirst()
	 * @see #previous()
	 * @see #next()
	 * @see #last()
	 * @see #afterLast()
	 * @see #isAfterLast()
	 */
	public int size() {
		return current.size();
	}

	public Row getRow(int aCursorPos) {
		return current.get(aCursorPos - 1);
	}

	/**
	 * Checks whether cusor is in the valid position. If not than the
	 * <code>InvalidCursorPositionException</code> is thrown.
	 * 
	 * @throws InvalidCursorPositionException
	 */
	protected void checkCursor() throws InvalidCursorPositionException {
		if (!isInserting()) {
			if (currentRowPos < 1) {
				throw new InvalidCursorPositionException("currentRowPos < 1");
			}
			if (currentRowPos > size()) {
				throw new InvalidCursorPositionException("currentRowPos > current.size()");
			}
		}
	}

	/**
	 * Checks whether cusor is in the valid position, including before first and
	 * after last position. If not than the
	 * <code>InvalidCursorPositionException</code> is thrown.
	 * 
	 * @throws InvalidCursorPositionException
	 */
	public void wideCheckCursor() throws InvalidCursorPositionException {
		if (currentRowPos < 0) {
			throw new InvalidCursorPositionException("currentRowPos < 0. Before before first position is illegal");
		}
		if (currentRowPos > size() + 1) {
			throw new InvalidCursorPositionException("currentRowPos > current.size() + 1. After after last position is illegal");
		}
	}

	/**
	 * Checks whether index of column is valid for this rowset's
	 * <code>Fields</code>
	 * 
	 * @param aColIndex
	 *            Index of particular column.
	 * @throws InvalidColIndexException
	 */
	protected void checkColIndex(int aColIndex) throws InvalidColIndexException {
		assert fields != null;
		Field field = fields.get(aColIndex);
		if (field == null) {
			throw new InvalidColIndexException(aColIndex + " have been passed as aColIndex parameter. But it had to be >= 1 and <= " + fields.getFieldsCount());
		}
	}

	/**
	 * Returns current cursor position in this rowset.
	 * 
	 * @return Current cursor position in this rowset.
	 * @see #absolute(int aCursorPos)
	 * @see #size()
	 * @see #beforeFirst()
	 * @see #first()
	 * @see #isBeforeFirst()
	 * @see #previous()
	 * @see #next()
	 * @see #last()
	 * @see #afterLast()
	 * @see #isAfterLast()
	 */
	public int getCursorPos() {
		return currentRowPos;
	}

	/**
	 * Positions rowset on specified row number. Row number is 1-based.
	 * 
	 * @param aCursorPos
	 *            Cursor position you whant to be setted in this rowset.
	 * @return True if cursor position in rowset equals to aCursorPos.
	 * @throws InvalidCursorPositionException
	 * @see #getCursorPos()
	 * @see #size()
	 * @see #beforeFirst()
	 * @see #first()
	 * @see #isBeforeFirst()
	 * @see #previous()
	 * @see #next()
	 * @see #last()
	 * @see #afterLast()
	 * @see #isAfterLast()
	 */
	public boolean setCursorPos(int aCursorPos) throws InvalidCursorPositionException {
		if (!isEmpty()) {
			if (aCursorPos >= 0 && aCursorPos <= size() + 1) {
				if (aCursorPos != currentRowPos) {
					if (rowsetChangeSupport.fireWillScrollEvent(aCursorPos)) {
						int oldCurrentRowPos = currentRowPos;
						currentRowPos = aCursorPos;
						rowsetChangeSupport.fireScrolledEvent(oldCurrentRowPos);
						// return if cursor points to any record
						return currentRowPos > 0 && currentRowPos < size() + 1;
					} else {
						return false;
					}
				} else {
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Returns whether this rowset is empty.
	 * 
	 * @return Whether this rowset is empty.
	 */
	public boolean isEmpty() {
		return current.isEmpty();
	}

	/**
	 * Collections - like insert method. Inserts a passed <code>Row</code> in
	 * this rowset in both original and current rows vectors. Initialization
	 * with current filter values is performed.
	 * 
	 * @param toInsert
	 *            A row to insertt in the rowset.
	 * @throws RowsetException
	 */
	public void insert(Row toInsert, boolean aAjusting) throws RowsetException {
		insert(toInsert, aAjusting, new Object[] {});
	}

	/**
	 * Row insert method. Inserts a passed <code>Row</code> in this rowset in
	 * both original and current rows arrays. First, filter's values are used
	 * for initialization, than <code>initingValues</code> specified is used.
	 * Takes into account <code>showOriginal</code> flag. If
	 * <code>showOriginal</code> flag is setted, than no action is performed.
	 * 
	 * @param toInsert
	 *            A row to insert in the rowset.
	 * @param aAjusting
	 *            Flag, indicating that inserting is within a batch operation
	 * @param initingValues
	 *            Values inserting row to be initialized with.
	 * @throws RowsetException
	 */
	public void insert(Row toInsert, boolean aAjusting, Object[] initingValues) throws RowsetException {
		int insertAtPosition = currentRowPos >= 0 && currentRowPos <= size() ? currentRowPos + 1 : size() + 1;
		insertAt(toInsert, aAjusting, insertAtPosition, initingValues);
	}

	public void insertAt(boolean aAjusting, int insertAt, Object[] initingValues) throws RowsetException {
		insertAt(new Row(flow.getEntityId(), fields), aAjusting, insertAt, initingValues);
	}

	/**
	 * Row insert method. Inserts a passed <code>Row</code> in this rowset in
	 * both original and current rows arrays. First, filter's values are used
	 * for initialization, than <code>initingValues</code> specified is used.
	 * Takes into account <code>showOriginal</code> flag. If
	 * <code>showOriginal</code> flag is setted, than no action is performed.
	 * 
	 * @param toInsert
	 *            A row to insert in the rowset.
	 * @param aAjusting
	 *            Flag, indicating that inserting is within a batch operation
	 * @param insertAt
	 *            Index the new row to be added at. 1-Based.
	 * @param initingValues
	 *            Values inserting row to be initialized with.
	 * @throws RowsetException
	 */
	public void insertAt(Row toInsert, boolean aAjusting, int insertAt, Object[] initingValues) throws RowsetException {
		assert fields != null;
		if (toInsert == null) {
			throw new RowsetException("Bad inserting row. It must be non null value.");
		}
		if (toInsert.getColumnCount() != fields.getFieldsCount()) {
			throw new RowsetException("Bad column count. While inserting, columns count in a row must same with fields count in rowset fields.");
		}
		toInsert.setLog(log);
		toInsert.setEntityName(flow != null ? flow.getEntityId() : "");
		if (rowsetChangeSupport.fireWillInsertEvent(toInsert, aAjusting)) {
			insertingRow = toInsert;
			try {
				initColumns(insertingRow, initingValues);
				insertingRow.setInserted();
				// work on current rows list, probably filtered
				current.add(insertAt - 1, insertingRow);
				currentRowPos = insertAt;
				original.add(insertingRow);
				Row insertedRow = insertingRow;
				modified = true;
				generateInsert(insertedRow);
				rowsetChangeSupport.fireRowInsertedEvent(insertedRow, aAjusting);
			} finally {
				insertingRow = null;
			}
		}
	}

	/**
	 * Returns whether rowset is in inserting a new row state.
	 * 
	 * @return Whether rowset is inserting a row.
	 */
	public boolean isInserting() {
		return insertingRow != null;
	}

	/**
	 * Initializes new row with supplied initialization values. Than initializes
	 * it with active filter values.
	 * 
	 * @param aRow
	 *            A <code>Row</code> to initialize.
	 * @param values
	 *            Values the specified <code>Row</code> to initialize with.
	 */
	protected void initColumns(Row aRow, Object[] values) throws RowsetException {
		if (aRow != null) {
			// key fields generation
			for (int i = 1; i <= fields.getFieldsCount(); i++) {
				Field field = fields.get(i);
				if (field.isPk() && aRow.getColumnObject(i) == null) {
					Object pkValue = RowsetUtils.generatePkValueByType(field.getTypeInfo().getType());
					pkValue = Converter.convert2RowsetCompatible(pkValue, field.getTypeInfo());
					aRow.setColumnObject(i, pkValue);
				}
			}
			// user supplied fields, including values for primary keys
			if (values != null && values.length > 0 && values.length % 2 == 0) {
				for (int i = 0; i < values.length - 1; i += 2) {
					if (values[i] != null && (values[i] instanceof Integer || values[i] instanceof Double || values[i] instanceof String || values[i] instanceof Field)) {
						Field field = null;
						int colIndex = 0;
						if (values[i] instanceof String) {
							colIndex = fields.find((String) values[i]);
							field = fields.get(colIndex);
						} else if (values[i] instanceof Field) {
							field = (Field) values[i];
							colIndex = fields.find(field.getName());
						} else {
							colIndex = values[i] instanceof Integer ? (Integer) values[i] : (int) Math.round((Double) values[i]);
							field = fields.get(colIndex);
						}
						if (field != null && colIndex != 0) {
							Object fieldValue = values[i + 1];
							fieldValue = Converter.convert2RowsetCompatible(fieldValue, field.getTypeInfo());
							aRow.setColumnObject(colIndex, fieldValue);
						}
					}
				}
			}
			// filtered values to corresponding fields, excluding key fields
			// This initing is also needed in aAjusting case, because otherwise
			// row will
			// disappear.
			if (activeFilter != null) {
				List<Integer> filterCriteriaFields = activeFilter.getFields();
				List<Object> filteringKeys = activeFilter.getAppliedKeys();
				assert filterCriteriaFields != null;
				assert filteringKeys != null;
				assert filterCriteriaFields.size() == filteringKeys.size();
				for (int i = 0; i < filterCriteriaFields.size(); i++) {
					int colIndex = filterCriteriaFields.get(i);
					Field field = fields.get(colIndex);
					// do not touch key fields!
					if (!field.isPk()) {
						Object fieldValue = filteringKeys.get(i);
						if (fieldValue == RowsetUtils.UNDEFINED_SQL_VALUE) {
							fieldValue = null;
						}
						fieldValue = Converter.convert2RowsetCompatible(fieldValue, field.getTypeInfo());
						aRow.setColumnObject(colIndex, fieldValue);
					}
				}
			}
		}
	}

	protected boolean isFilteringCriteria(int aFieldIndex) {
		if (activeFilter != null) {
			List<Integer> fIdxes = activeFilter.getFields();
			if (fIdxes != null && fIdxes.contains(aFieldIndex)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Deletes all rows in the rowset. Rows are marked as deleted and removed
	 * from cuurent rows vector. After deleting, cursor position becomes invalid
	 * and both <code>isBeforeFirst()</code> and <code>isAfterLast()</code> must
	 * return true. Subsequent calls to this method perform no action. Takes
	 * into account <code>showOriginal</code> flag. If <code>showOriginal</code>
	 * flag setted, than no action is performed.
	 * 
	 * @see #isBeforeFirst()
	 * @see #isAfterLast()
	 * @see #delete()
	 * @see #delete(java.util.Set)
	 * @throws RowsetException
	 */
	public void deleteAll() throws RowsetException {
		for (int i = current.size() - 1; i >= 0; i--) {
			Row row = current.get(i);
			assert row != null;
			if (rowsetChangeSupport.fireWillDeleteEvent(row, i != 0)) { // last
				                                                        // iteration
				                                                        // will
				                                                        // fire
				                                                        // non-ajusting
				                                                        // event
				row.setDeleted();
				generateDelete(row);
				current.remove(i);
				modified = true;
				currentRowPos = i + 1;
				rowsetChangeSupport.fireRowDeletedEvent(row, i != 0); // last
				                                                      // iteration
				                                                      // will
				                                                      // fire
				                                                      // non-ajusting
				                                                      // event
				currentRowPos = Math.min(currentRowPos, current.size());
			}
		}
		if (current.isEmpty()) {
			currentRowPos = 0;
		} else {
			if (currentRowPos < 1) {
				currentRowPos = 1;
			}
			if (currentRowPos > size()) {
				currentRowPos = size();
			}
		}
		wideCheckCursor();
	}

	public void delete(Row aRow) throws RowsetException {
		delete(Collections.singleton(aRow));
	}

	/**
	 * Deletes specified rows from the rowset. Rows are marked as deleted and
	 * removed from cuurent rows vector. After deleting, cursor position becomes
	 * invalid and rowset may be repositioned.
	 * 
	 * @param rows2Delete
	 *            Set of rows to be deleted from the rowset
	 * @see #isBeforeFirst()
	 * @see #isAfterLast()
	 * @see #delete()
	 * @see #deleteAll()
	 * @throws RowsetException
	 */
	public void delete(Collection<Row> aRows2Delete) throws RowsetException {
		Set<Row> rows2Delete = new HashSet<>();
		rows2Delete.addAll(aRows2Delete);
		for (int i = current.size() - 1; i >= 0; i--) {
			Row row = current.get(i);
			assert row != null;
			if (rows2Delete.contains(row)) {
				rows2Delete.remove(row);
				// last iteration will fire non-ajusting event
				if (rowsetChangeSupport.fireWillDeleteEvent(row, !rows2Delete.isEmpty())) {
					row.setDeleted();
					generateDelete(row);
					current.remove(i);
					modified = true;
					currentRowPos = Math.min(i + 1, current.size());
					// last iteration will fire non-ajusting event
					rowsetChangeSupport.fireRowDeletedEvent(row, !rows2Delete.isEmpty());
				}
			}
		}
		wideCheckCursor();
	}

	/**
	 * Deletes specified row from the rowset by index. Row is marked as deleted
	 * and removed from cuurent rows vector. After deleting, cursor position
	 * becomes invalid and rowset may be repositioned.
	 * 
	 * @param aRowIndex
	 *            Index of row to be deleted from the rowset. aRowIndex is
	 *            1-based.
	 * @see #isBeforeFirst()
	 * @see #isAfterLast()
	 * @see #delete()
	 * @see #deleteAll()
	 * @throws RowsetException
	 */
	public Row deleteAt(int aRowIndex) throws RowsetException {
		return deleteAt(aRowIndex, false);
	}

	public Row deleteAt(int aRowIndex, boolean aAjusting) throws RowsetException {
		if (aRowIndex >= 1 && aRowIndex <= size()) {
			Row row = current.get(aRowIndex - 1);
			assert row != null;
			if (rowsetChangeSupport.fireWillDeleteEvent(row, aAjusting)) {
				// last iteration will fire non-ajusting event
				row.setDeleted();
				generateDelete(row);
				current.remove(aRowIndex - 1);
				modified = true;
				currentRowPos = Math.min(aRowIndex, current.size());
				rowsetChangeSupport.fireRowDeletedEvent(row, aAjusting);
				// last iteration will fire non-ajusting event
			}
			wideCheckCursor();
			return row;
		} else
			throw new InvalidCursorPositionException("Cursor position pointing to deleted row must be in range [1, size()]");
	}

	/**
	 * Returns <code>Row</code> at current cursor position. Doesn't perform
	 * current position check, so it has to be called internally.
	 * 
	 * @return <code>Row</code> at current cursor position.
	 */
	public Row getCurrentRow() {
		if (insertingRow != null) {
			return insertingRow;
		} else if (!isEmpty()) {
			return currentRowPos >= 1 && currentRowPos <= current.size() ? current.get(currentRowPos - 1) : null;
		} else {
			return null;
		}
	}

	/**
	 * Applies modifications maded to this rowset. After that no difference
	 * between original and current rows vectors and row's data have place.
	 */
	public void currentToOriginal() {
		original.clear();
		List<Row> lcurrent = null;
		if (activeFilter != null && activeFilter.isApplied()) {
			lcurrent = activeFilter.getOriginalRows();
		} else {
			lcurrent = current;
		}
		original.addAll(lcurrent);
		for (int i = original.size() - 1; i >= 0; i--) {
			Row row = original.get(i);
			assert row != null;
			row.currentToOriginal();
			row.clearInserted();
			if (row.isDeleted()) {// Should never happen. Added for code
				                  // strength in case of mark and sweep row
				                  // deletion.
				original.remove(i);
				lcurrent.remove(i);
			}
		}
		modified = false;
	}

	/**
	 * Cancels modifications made to this rowset. After that no difference
	 * between original and current rows vectors and row's data have place.
	 */
	public void originalToCurrent() throws RowsetException {
		Filter wasFilter = activeFilter;
		if (wasFilter != null) {
			wasFilter.cancel();// implicit setCurrent() and setActiveFilter()
			                   // calls.
		}
		try {
			current.clear();
			current.addAll(original);
			for (int i = current.size() - 1; i >= 0; i--) {
				Row row = current.get(i);
				assert row != null;
				row.originalToCurrent();
				if (row.isInserted()) {
					current.remove(i);
					original.remove(i);
				}
				row.clearDeleted();
			}
			modified = false;
		} finally {
			if (wasFilter != null) {
				wasFilter.refilterRowset();// implicit setCurrent() and
				                           // setActiveFilter() calls.
			}
		}
	}

	protected void generateInsert(Row aRow) {
		if (flow != null && flow.getChangeLog() != null) {
			List<Change> changesLog = flow.getChangeLog();
			Insert insert = new Insert(flow.getEntityId());
			List<Change.Value> data = new ArrayList<>();
			for (int i = 0; i < aRow.getCurrentValues().length; i++) {
				Field field = aRow.getFields().get(i + 1);
				Object value = aRow.getCurrentValues()[i];
				if (value != null || field.isStrong4Insert()) {
					data.add(new Change.Value(field.getName(), value, field.getTypeInfo()));
				}
			}
			insert.data = data.toArray(new Change.Value[] {});
			changesLog.add(insert);
			aRow.setInserted(insert);
		}
	}

	private Change.Value[] generateChangeLogKeys(int colIndex, Row aRow, Object oldValue) {
		if (fields != null) {
			List<Change.Value> keys = new ArrayList<>();
			for (int i = 1; i <= fields.getFieldsCount(); i++) {
				Field field = fields.get(i);
				// Some tricky processing of primary key modification case ...
				if (field.isPk()) {
					Object value = aRow.getInternalCurrentValues().get(i - 1);
					if (i == colIndex) {
						value = oldValue;
					}
					keys.add(new Change.Value(field.getName(), value, field.getTypeInfo()));
				}
			}
			return keys.toArray(new Change.Value[] {});
		} else {
			return null;
		}
	}

	protected void generateDelete(Row aRow) {
		if (flow != null && flow.getChangeLog() != null) {
			List<Change> changesLog = flow.getChangeLog();
			Delete delete = new Delete(flow.getEntityId());
			delete.keys = generateChangeLogKeys(-1, aRow, null);
			changesLog.add(delete);
		}
	}

	/**
	 * Creates and returns new orderer based on this rowset.
	 * 
	 * @param aFieldIndicies
	 * @return New filter based on this rowset.
	 */
	public Orderer createOrderer(List<Integer> aFieldIndicies) {
		Orderer hf = new Orderer(aFieldIndicies);
		orderers.add(hf);
		hf.setRowset(this);
		return hf;
	}

	/**
	 * Creates and returns new filter based on this rowset.
	 * 
	 * @param aFieldIndicies
	 * @return New filter based on this rowset.
	 */
	public Filter createFilter(List<Integer> aFieldIndicies) {
		Filter hf = new Filter(aFieldIndicies);
		filters.add(hf);
		hf.setRowset(this);
		return hf;
	}

	/**
	 * Creates and returns new locator based on this rowset.
	 * 
	 * @return New locator based on this rowset.
	 */
	public Locator createLocator() {
		Locator hl = new Locator();
		locators.add(hl);
		hl.setRowset(this);
		return hl;
	}

	/**
	 * Removes specified filter from this rowset.
	 * 
	 * @param aOrderer
	 *            Locator object to be removed.
	 * @throws RowsetException
	 */
	public void removeOrderer(Orderer aOrderer) throws RowsetException {
		if (orderers.remove(aOrderer)) {
			aOrderer.setRowset(null);
		}
	}

	/**
	 * Removes specified filter from this rowset.
	 * 
	 * @param aFilter
	 *            Locator object to be removed.
	 * @throws RowsetException
	 */
	public void removeFilter(Filter aFilter) throws RowsetException {
		if (aFilter == activeFilter) {
			activeFilter.cancel();
		}
		if (filters.remove(aFilter)) {
			aFilter.setRowset(null);
		}
	}

	/**
	 * Removes specified locator from this rowset.
	 * 
	 * @param aLocator
	 *            Locator object to be removed.
	 */
	public void removeLocator(Locator aLocator) {
		if (locators.remove(aLocator)) {
			aLocator.setRowset(null);
		}
	}

	/**
	 * Service method to sort current vector of rows.
	 * 
	 * @param aComparator
	 *            Comparator to use while sorting rows.
	 * @throws InvalidCursorPositionException
	 */
	public void sort(Comparator<Row> aComparator) throws InvalidCursorPositionException {
		if (aComparator != null) {
			if (rowsetChangeSupport.fireWillSortEvent()) {
				Collections.sort(current, aComparator);
				rowsetChangeSupport.fireSortedEvent();
			}
		}
	}

	public void reverse() throws InvalidCursorPositionException {
		if (rowsetChangeSupport.fireWillSortEvent()) {
			Collections.reverse(current);
			rowsetChangeSupport.fireSortedEvent();
		}
	}

	/**
	 * Returns array of locators installed on this rowset.
	 * 
	 * @return Array of locators installed on this rowset.
	 */
	public Locator[] getLocators() {
		Locator[] res = new Locator[locators.size()];
		return locators.toArray(res);
	}

	/**
	 * Returns array of filters installed on this rowset.
	 * 
	 * @return Array of filters installed on this rowset.
	 */
	public Filter[] getFilters() {
		Filter[] res = new Filter[filters.size()];
		return filters.toArray(res);
	}

	/**
	 * Returns array of orderers installed on this rowset.
	 * 
	 * @return Array of orderers installed on this rowset.
	 */
	public Orderer[] getOrderers() {
		return orderers.toArray(new Orderer[] {});
	}

	public static void addRowsetContentJsListener(Rowset aTarget, final JavaScriptObject onContentChanged) {
		aTarget.addRowsetListener(new RowsetAdapter() {

			protected void contentChanged() {
				onContentChanged.<JsObject> cast().apply(null, JavaScriptObject.createArray());
			}

			@Override
			public void rowsetFiltered(RowsetFilterEvent event) {
				contentChanged();
			}

			@Override
			public void rowsetRequeried(RowsetRequeryEvent event) {
				contentChanged();
			}

			@Override
			public void rowsetRolledback(RowsetRollbackEvent event) {
				contentChanged();
			}

			@Override
			public void rowsetSorted(RowsetSortEvent event) {
				contentChanged();
			}
		});
	}

}
