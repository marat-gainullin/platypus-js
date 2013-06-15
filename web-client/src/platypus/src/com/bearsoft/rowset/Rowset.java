/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.changes.Delete;
import com.bearsoft.rowset.changes.Insert;
import com.bearsoft.rowset.changes.Update;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.dataflow.TransactionListener;
import com.bearsoft.rowset.events.RowsetChangeSupport;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.exceptions.FlowProviderFailedException;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.InvalidFieldsExceptionException;
import com.bearsoft.rowset.exceptions.MissingFlowProviderException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.filters.Filter;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.locators.ParentLocator;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.ordering.DefaultOrderersFactory;
import com.bearsoft.rowset.ordering.HashOrderer;
import com.bearsoft.rowset.ordering.OrderersFactory;
import com.bearsoft.rowset.utils.KeySet;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.Callback;
import com.eas.client.Cancellable;
import com.eas.client.CancellableCallback;
import com.eas.client.Utils;
import com.eas.client.beans.PropertyChangeEvent;
import com.eas.client.beans.PropertyChangeListener;
import com.eas.client.beans.PropertyChangeSupport;
import com.eas.client.beans.VetoableChangeListener;

/**
 * Rowset serves as original and updated rows vectors holder. There are three
 * developing themes: - rowset's life, with it's data processing (sorting,
 * locating and filtering). - rowset's saving and restoring to and from variety
 * of sources (database, files and others). - applying and rolling back changes,
 * maded to it's data.
 * 
 * @author mg
 */
public class Rowset implements PropertyChangeListener, VetoableChangeListener, TransactionListener {

	public static final String BAD_FLOW_PROVIDER_RESULT_MSG = "Flow Provider must return at least an empty rowset";
	// multi-tier transactions support
	protected String sessionId = null;
	// support for data flows.
	protected FlowProvider flow = null;
	protected TransactionListener.Registration transactionRegisration;
	// rowset's metadata
	protected Fields fields = null;
	// rowset's data
	protected List<Row> original = new ArrayList();
	protected List<Row> current = new ArrayList();
	// data view capabilities
	protected int currentRowPos = 0; // before first position
	protected boolean showOriginal = false;
	// data processing
	protected Set<Filter> filters = new HashSet(); // filters
	protected Set<Locator> locators = new HashSet(); // locators
	protected Filter activeFilter = null;
	protected Row insertingRow = null;
	// client code interaction
	protected PropertyChangeSupport propertyChangeSupport;
	protected RowsetChangeSupport rowsetChangeSupport;
	protected OrderersFactory orderersFactory;
	protected boolean pending;
	protected boolean transacted = false;
	protected boolean immediateFilter = true;
	protected boolean modified = false;

	/**
	 * Simple constructor.
	 */
	public Rowset() {
		super();
		propertyChangeSupport = new PropertyChangeSupport(this);
		rowsetChangeSupport = new RowsetChangeSupport(this);
		orderersFactory = new DefaultOrderersFactory(this);
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

	// multi-tier transactions support
	@Override
	public void commited() throws Exception {
		if (transactionRegisration != null) {
			transactionRegisration.remove();
			transactionRegisration = null;
		}
		final Set<RowsetListener> lrowsetListeners = rowsetChangeSupport.getRowsetListeners();
		rowsetChangeSupport.setRowsetListeners(null);
		try {
			currentToOriginal();
		} finally {
			rowsetChangeSupport.setRowsetListeners(lrowsetListeners);
		}
		rowsetChangeSupport.fireSavedEvent();
	}

	@Override
	public void rolledback() throws Exception {
		if (transactionRegisration != null) {
			transactionRegisration.remove();
			transactionRegisration = null;
		}
		final Set<RowsetListener> lrowsetListeners = rowsetChangeSupport.getRowsetListeners();
		rowsetChangeSupport.setRowsetListeners(null);
		try {
			originalToCurrent();
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

	/**
	 * Removes <code>PropertyChangeListener</code> from this rowset.
	 * 
	 * @param aListener
	 *            <code>PropertyChangeListener</code> to be removed.
	 */
	public void removePropertyChangeListener(PropertyChangeListener aListener) {
		propertyChangeSupport.removePropertyChangeListener(aListener);
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
	 * Returns current <code>OrderersFactory</code> object, installed on this
	 * rowset.
	 * 
	 * @return Currently installed <code>OrderersFactory</code> object
	 */
	public OrderersFactory getOrderersFactory() {
		return orderersFactory;
	}

	/**
	 * Installed <code>OrderersFactory</code> object on this rowset.
	 * 
	 * @param orderersFactory
	 *            Factory object to install.
	 */
	public void setOrderersFactory(OrderersFactory orderersFactory) {
		this.orderersFactory = orderersFactory;
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
		Fields oldValue = fields;
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

	public boolean isPending() {
		return pending;
	}

	/**
	 * Returns whether this rowset is transacted. If it's transacted, the rowset
	 * will not fire the saved event and will not call currentToOriginal method
	 * after appling changes. It will be done by somebody at the commit. If this
	 * rowset is not transacted, than it will act as standalone rowset and will
	 * call currentToOriginal method and fire the saved event.
	 * 
	 * @return Whether this rowset is transacted.
	 * @see #setTransacted(boolean)
	 */
	public boolean isTransacted() {
		return transacted;
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
	 * Sets the transacted flag. The flag is described in isTransacted() method
	 * doc.
	 * 
	 * @param transacted
	 * @see #isTransacted()
	 */
	public void setTransacted(boolean aTransacted) {
		boolean oldValue = transacted;
		if (transacted != aTransacted) {
			transacted = aTransacted;
			propertyChangeSupport.firePropertyChange("transacted", oldValue, transacted);
		}
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

	public Cancellable refresh(CancellableCallback onSuccess, final Callback<String> onFailure) throws Exception {
		return refresh(new Parameters(), onSuccess, onFailure);
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
	public Cancellable refresh(Parameters aParams, final CancellableCallback onSuccess, final Callback<String> onFailure) throws Exception {
		if (flow != null) {
			if (rowsetChangeSupport.fireWillRequeryEvent()) {
				pending = true;
				return flow.refresh(aParams, new RowsetCallbackAdapter() {

					@Override
					protected void doWork(Rowset aRowset) throws Exception {
						if (aRowset != null) {
							if (activeFilter != null && activeFilter.isApplied()) {
								activeFilter.deactivate(); // No implicit calls
								                           // to setCurrent and
								                           // etc.
								activeFilter = null;
							}
							if (fields == null) {
								setFields(aRowset.getFields());
							}
							List<Row> rows = aRowset.getCurrent();
							aRowset.setCurrent(new ArrayList());
							setCurrent(rows);
							currentToOriginal();
							invalidateFilters();
							Set<RowsetListener> l = rowsetChangeSupport.getRowsetListeners();
							rowsetChangeSupport.setRowsetListeners(null);
							try {
								first();
							} finally {
								rowsetChangeSupport.setRowsetListeners(l);
							}
							pending = false;
							rowsetChangeSupport.fireRequeriedEvent();
							onSuccess.run();
						} else {
							throw new FlowProviderFailedException(BAD_FLOW_PROVIDER_RESULT_MSG);
						}
					}
				}, new Callback<String>() {
					public void run(String aResult) throws Exception {
						pending = false;
						rowsetChangeSupport.fireNetErrorEvent(aResult);
						if(onFailure != null)
							onFailure.run(aResult);
					}

					public void cancel() {
					}
				});
			}
			return null;
		} else {
			throw new MissingFlowProviderException();
		}
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
		unsubscribeFromRows(current);
		current = aCurrent;
		subscribeOnRows(current);
		currentRowPos = 0;
		invalidateLocators();
		// WARNING: Invalidating of filters MUST NOT go here, because filtering
		// calls this setCurrent(List<Row> aCurrent) method.
		// Current set of rowset's rows is changed and so, we need to invalidate
		// locators, but NOT FILTERS!
	}

	/**
	 * Method similar to setCurrent, except it doesn't unsubscribe from old rows
	 * events and doesn't subscribe on new rows events. Used with filtering
	 * classes. The idea is that new rows list is a subset of this rowset's
	 * native rows and so, we don't need to riddle theese rows's events
	 * subscribers.
	 * 
	 * @param aCurrent
	 *            Rows list to be setted as current rowset's content.
	 * @see HashOrderer
	 * @see Filter
	 * @see Locator
	 */
	public void setSubsetAsCurrent(List<Row> aCurrent) {
		assert fields != null;
		current = aCurrent;
		currentRowPos = 0;
		invalidateLocators();
		// WARNING: Invalidating of filters MUST NOT go here, because filtering
		// calls this setCurrent(List<Row> aCurrent) method.
		// Current set of rowset's rows is changed and so, we need to invalidate
		// locators, but NOT FILTERS!
	}

	private void unsubscribeFromRows(List<Row> aRows) {
		for (Row row : aRows) {
			row.removePropertyChangeListener(this);
			row.removeVetoableChangeListener(this);
		}
	}

	/**
	 * Subscribes this rowset on rows events and sets this rowset's fields to
	 * the rows.
	 * 
	 * @param aRows
	 */
	private void subscribeOnRows(List<Row> aRows) {
		for (Row row : aRows) {
			row.addPropertyChangeListener(this);
			row.addVetoableChangeListener(this);
			// hack. We extremely need a way to set row's fields without related
			// processing
			row.fields = fields;
		}
	}

	/**
	 * Moves cursor on pre first position. Cusor position becomes 0.
	 * 
	 * @see #absolute(int aCursorPos)
	 * @see #getCursorPos()
	 * @see #size()
	 * @see #first()
	 * @see #isBeforeFirst()
	 * @see #previous()
	 * @see #next()
	 * @see #last()
	 * @see #afterLast()
	 * @see #isAfterLast()
	 */
	public boolean beforeFirst() throws InvalidCursorPositionException {
		if (!isBeforeFirst()) {
			if (rowsetChangeSupport.fireWillScrollEvent(0)) {
				int oldCurrentRowPos = currentRowPos;
				currentRowPos = 0;
				rowsetChangeSupport.fireScrolledEvent(oldCurrentRowPos);
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Returns if cursor is before the first row. Takes into account
	 * <code>showOriginal</code> flag
	 * 
	 * @return True if the cursor is on before first position.
	 * @see #absolute(int aCursorPos)
	 * @see #getCursorPos()
	 * @see #size()
	 * @see #beforeFirst()
	 * @see #first()
	 * @see #previous()
	 * @see #next()
	 * @see #last()
	 * @see #afterLast()
	 * @see #isAfterLast()
	 */
	public boolean isBeforeFirst() {
		assert currentRowPos >= 0;
		return isEmpty() || currentRowPos == 0;
	}

	/**
	 * Moves cursor to the first position in the rowset. It won't to position
	 * the rowset if it is empty. After that, position becomes 1 if this method
	 * returns true. If this method returns false, than position remains
	 * unchnaged. Takes into account <code>showOriginal</code> flag
	 * 
	 * @return True if rowset is on the first position, and false if it is not.
	 * @see #absolute(int aCursorPos)
	 * @see #getCursorPos()
	 * @see #size()
	 * @see #beforeFirst()
	 * @see #isBeforeFirst()
	 * @see #previous()
	 * @see #next()
	 * @see #last()
	 * @see #afterLast()
	 * @see #isAfterLast()
	 */
	public boolean first() throws InvalidCursorPositionException {
		if (!isEmpty()) {
			if (currentRowPos != 1) {
				if (rowsetChangeSupport.fireWillScrollEvent(1)) {
					int oldCurrentRowPos = currentRowPos;
					currentRowPos = 1;
					rowsetChangeSupport.fireScrolledEvent(oldCurrentRowPos);
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * Moves cursor to the last position in the rowset. It won't to position the
	 * rowset if it is empty. After that, position equals to rows count if this
	 * method returns true. If this method returns false, than position remains
	 * unchnaged. Takes into account <code>showOriginal</code> flag
	 * 
	 * @return True if rowset is on the last position, and false if it is not.
	 * @see #absolute(int aCursorPos)
	 * @see #getCursorPos()
	 * @see #size()
	 * @see #beforeFirst()
	 * @see #first()
	 * @see #isBeforeFirst()
	 * @see #previous()
	 * @see #next()
	 * @see #afterLast()
	 * @see #isAfterLast()
	 */
	public boolean last() throws InvalidCursorPositionException {
		if (!isEmpty()) {
			if (currentRowPos != size()) {
				if (rowsetChangeSupport.fireWillScrollEvent(size())) {
					int oldCurrentRowPos = currentRowPos;
					currentRowPos = size();
					rowsetChangeSupport.fireScrolledEvent(oldCurrentRowPos);
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * Moves cursor to the after last position in the rowset. It won't to
	 * position the rowset if it is empty. After positioning, position equals to
	 * rows count+1 if this method returns true. If this method returns false,
	 * than position remains 0. Takes into account <code>showOriginal</code>
	 * flag
	 * 
	 * @return True if has been positioned, and false if it hasn't.
	 * @see #absolute(int aCursorPos)
	 * @see #getCursorPos()
	 * @see #size()
	 * @see #beforeFirst()
	 * @see #first()
	 * @see #isBeforeFirst()
	 * @see #previous()
	 * @see #next()
	 * @see #last()
	 * @see #isAfterLast()
	 */
	public boolean afterLast() throws InvalidCursorPositionException {
		if (!isAfterLast()) {
			if (rowsetChangeSupport.fireWillScrollEvent(size() + 1)) {
				int oldCurrentRowPos = currentRowPos;
				currentRowPos = size() + 1;
				rowsetChangeSupport.fireScrolledEvent(oldCurrentRowPos);
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Returns if cursor is after last row. Takes into account
	 * <code>showOriginal</code> flag
	 * 
	 * @return True if the cursor is on after last position.
	 * @see #absolute(int aCursorPos)
	 * @see #getCursorPos()
	 * @see #size()
	 * @see #beforeFirst()
	 * @see #first()
	 * @see #isBeforeFirst()
	 * @see #previous()
	 * @see #next()
	 * @see #last()
	 * @see #afterLast()
	 */
	public boolean isAfterLast() {
		return isEmpty() || currentRowPos > size();
	}

	/**
	 * Moves the cursor one position forward. Takes into account
	 * <code>showOriginal</code> flag
	 * 
	 * @return True if new position is on the next row. False if the rowset is
	 *         empty or cursor becomes after last position. In this case cusor
	 *         is moved, but method returns false.
	 * @see #absolute(int aCursorPos)
	 * @see #getCursorPos()
	 * @see #size()
	 * @see #beforeFirst()
	 * @see #first()
	 * @see #isBeforeFirst()
	 * @see #previous()
	 * @see #last()
	 * @see #afterLast()
	 * @see #isAfterLast()
	 */
	public boolean next() throws InvalidCursorPositionException {
		if (!isEmpty()) {
			if (currentRowPos < size() + 1) {
				if (currentRowPos < size()) {
					if (rowsetChangeSupport.fireWillScrollEvent(currentRowPos + 1)) {
						int oldCurrentRowPos = currentRowPos;
						currentRowPos++;
						rowsetChangeSupport.fireScrolledEvent(oldCurrentRowPos);
						return true;
					} else {
						return false;
					}
				} else {
					if (rowsetChangeSupport.fireWillScrollEvent(currentRowPos + 1)) {
						int oldCurrentRowPos = currentRowPos;
						currentRowPos++;
						rowsetChangeSupport.fireScrolledEvent(oldCurrentRowPos);
					}
					return false;
				}
			}
		} else {
			assert currentRowPos == 0;
		}
		return false;
	}

	/**
	 * Moves the cursor one position backward. Takes into account
	 * <code>showOriginal</code> flag
	 * 
	 * @return True if new position is on the previous row. False if the rowset
	 *         is empty or cursor becomes before first position. In this case
	 *         cusor is moved, but method returns false.
	 * @see #absolute(int aCursorPos)
	 * @see #getCursorPos()
	 * @see #size()
	 * @see #beforeFirst()
	 * @see #first()
	 * @see #isBeforeFirst()
	 * @see #next()
	 * @see #last()
	 * @see #afterLast()
	 * @see #isAfterLast()
	 */
	public boolean previous() throws InvalidCursorPositionException {
		if (!isEmpty()) {
			if (currentRowPos > 0) {
				if (currentRowPos > 1) {
					if (rowsetChangeSupport.fireWillScrollEvent(currentRowPos - 1)) {
						int oldCurrentRowPos = currentRowPos;
						currentRowPos--;
						rowsetChangeSupport.fireScrolledEvent(oldCurrentRowPos);
						return true;
					} else {
						return false;
					}
				} else {
					if (rowsetChangeSupport.fireWillScrollEvent(currentRowPos - 1)) {
						int oldCurrentRowPos = currentRowPos;
						currentRowPos--;
						rowsetChangeSupport.fireScrolledEvent(oldCurrentRowPos);
					}
					return false;
				}
			} else {
				return false;
			}
		} else {
			assert currentRowPos == 0;
		}
		return false;
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
		if (showOriginal) {
			return original.size();
		} else {
			return current.size();
		}
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
			throw new InvalidCursorPositionException("currentRowPos < 0. Before before first posotion is illegal");
		}
		if (currentRowPos > size() + 1) {
			throw new InvalidCursorPositionException("currentRowPos > current.size()+1. After after last position is illegal");
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
	public boolean absolute(int aCursorPos) throws InvalidCursorPositionException {
		if (!isEmpty()) {
			if (aCursorPos >= 1 && aCursorPos <= size()) {
				if (aCursorPos != currentRowPos) {
					if (rowsetChangeSupport.fireWillScrollEvent(aCursorPos)) {
						int oldCurrentRowPos = currentRowPos;
						currentRowPos = aCursorPos;
						rowsetChangeSupport.fireScrolledEvent(oldCurrentRowPos);
						return true;
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
	 * Returns a row by ordinal number. Row number is 1-based.
	 * 
	 * @param aRowNumber
	 *            Row number you whant to be used to locate the row.
	 * @return Row if speciified row number is valid, null otherwise.
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
	public Row getRow(int aRowNumber) {
		if (!isEmpty()) {
			if (aRowNumber >= 1 && aRowNumber <= size()) {
				if (showOriginal) {
					return original.get(aRowNumber - 1);
				} else {
					return current.get(aRowNumber - 1);
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Returns whether this rowset is empty.
	 * 
	 * @return Whether this rowset is empty.
	 */
	public boolean isEmpty() {
		if (showOriginal) {
			return original.isEmpty();
		} else {
			return current.isEmpty();
		}
	}

	/**
	 * Simple insert method. Inserts a new <code>Row</code> in this rowset in
	 * both original and current rows vectors. Initialization with current
	 * filter values is performed.
	 */
	public void insert() throws RowsetException {
		insert(new Object[] {});
	}

	/**
	 * Simple insert method. Inserts a new <code>Row</code> in this rowset in
	 * both original and current rows arrays. First, filter's values are used
	 * for initialization, than <code>initingValues</code> specified is used.
	 * Takes into account <code>showOriginal</code> flag. If
	 * <code>showOriginal</code> flag is setted, than no action is performed.
	 * 
	 * @param initingValues
	 *            Values inserting row to be initialized with.
	 * @throws RowsetException
	 */
	public void insert(Object... initingValues) throws RowsetException {
		if (!showOriginal) {
			assert fields != null;
			Row row = new Row();
			row.setFields(fields);
			insert(row, false, initingValues);
		}
	}

	/**
	 * Simple insert method. Inserts a new <code>Row</code> in this rowset in
	 * both original and current rows arrays. First, filter's values are used
	 * for initialization, than <code>initingValues</code> specified is used.
	 * Takes into account <code>showOriginal</code> flag. If
	 * <code>showOriginal</code> flag is setted, than no action is performed.
	 * 
	 * @param insertAt
	 *            Index new row wull be placed at. 1-Based.
	 * @param initingValues
	 *            Values inserting row to be initialized with.
	 * @throws RowsetException
	 */
	public void insertAt(int insertAt, Object... initingValues) throws RowsetException {
		if (!showOriginal) {
			assert fields != null;
			Row row = new Row();
			row.setFields(fields);
			insertAt(row, false, insertAt, initingValues);
		}
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
	public void insert(Row toInsert, boolean aAjusting, Object... initingValues) throws RowsetException {
		int insertAtPosition;
		if (isBeforeFirst()) {
			insertAtPosition = 1;
		} else if (isAfterLast()) {
			insertAtPosition = currentRowPos;
		} else {
			insertAtPosition = currentRowPos + 1;
		}
		insertAt(toInsert, aAjusting, insertAtPosition, initingValues);
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
	public void insertAt(Row toInsert, boolean aAjusting, int insertAt, Object... initingValues) throws RowsetException {
		if (!showOriginal) {
			assert fields != null;
			if (toInsert == null) {
				throw new RowsetException("Bad inserting row. It must be non null value.");
			}
			if (toInsert.getColumnCount() != fields.getFieldsCount()) {
				throw new RowsetException("Bad column count. While inserting, columns count in a row must same with fields count in rowset fields.");
			}

			insertingRow = toInsert;
			try {
				if (rowsetChangeSupport.fireWillInsertEvent(insertingRow, aAjusting)) {
					initColumns(insertingRow, initingValues);
					insertingRow.setInserted();
					// work on current rows list, probably filtered
					List<Row> lcurrent = current;
					lcurrent.add(insertAt - 1, insertingRow);
					currentRowPos = insertAt;
					if (activeFilter != null) {
						// work on rowset's original rows vector, hided by
						// active
						// filter
						lcurrent = activeFilter.getOriginalRows();
						int lcurrentRowPos = activeFilter.getOriginalPos();
						if (lcurrentRowPos == 0) { // before first
							lcurrent.add(0, insertingRow);
							lcurrentRowPos = 1;
						} else if (lcurrentRowPos > lcurrent.size()) {
							lcurrent.add(insertingRow);
							// lcurrent.size() has been incremented by add()
							// method.
							lcurrentRowPos = lcurrent.size();
						} else {
							lcurrent.add(lcurrentRowPos, insertingRow);
							lcurrentRowPos++;
						}
						activeFilter.setOriginalPos(lcurrentRowPos);
					}
					original.add(insertingRow);

					addRow2Filters(insertingRow, -1);
					invalidateLocators();
					Row insertedRow = insertingRow;
					modified = true;
					insertedRow.addVetoableChangeListener(this);
					insertedRow.addPropertyChangeListener(this);
					generateInsert(insertedRow);
					rowsetChangeSupport.fireRowInsertedEvent(insertedRow, aAjusting);
				}
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
	protected void initColumns(Row aRow, Object... values) throws RowsetException {
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
					if (values[i] != null && (values[i] instanceof Integer || values[i] instanceof Double || values[i] instanceof Field)) {
						Field field = null;
						int colIndex = 0;
						if (values[i] instanceof Field) {
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
			// disapear.
			if (activeFilter != null) {
				List<Integer> filterCriteriaFields = activeFilter.getFields();
				KeySet ks = activeFilter.getKeysetApplied();
				assert filterCriteriaFields != null;
				assert ks != null;
				assert filterCriteriaFields.size() == ks.size();
				for (int i = 0; i < filterCriteriaFields.size(); i++) {
					int colIndex = filterCriteriaFields.get(i);
					Field field = fields.get(colIndex);
					// do not touch key fields!
					if (!field.isPk()) {
						Object fieldValue = ks.get(i);
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
	 * Removes a row from all filters as aFieldIndex == -1, and from some
	 * filters if aFieldIndex != -1.
	 * 
	 * @param aRow
	 *            - A row to operate with
	 * @param aFieldIndex
	 *            - A field index to detemine if it is filtering criteria. It
	 *            may be -1 to force removing a row from filters
	 * @return True if aRow was removed from one of the filters, False
	 *         otherwise.
	 * @throws RowsetException
	 */
	public boolean removeRowFromFilters(Row aRow, int aFieldIndex) throws RowsetException {
		boolean removed = false;
		if (filters != null) {
			for (Filter hf : filters) {
				assert hf != null;
				if (aFieldIndex == -1 || hf.isFilteringCriteria(aFieldIndex)) {
					boolean lRemoved = hf.remove(aRow);
					if (lRemoved) {
						removed = true;
					}
				}
			}
		}
		return removed;
	}

	/**
	 * Adds a row to all filters as aFieldIndex == -1, and to some filters if
	 * aFieldIndex != -1.
	 * 
	 * @param aRow
	 *            - A row to operate with
	 * @param aFieldIndex
	 *            - A field index to detemine if it is filtering criteria. It
	 *            may be -1 to force adding a row to filters
	 * @return True if aRow was added to one of the filters, False otherwise.
	 * @throws RowsetException
	 */
	public boolean addRow2Filters(Row aRow, int aFieldIndex) throws RowsetException {
		boolean added = false;
		if (filters != null) {
			for (Filter hf : filters) {
				assert hf != null;
				if (aFieldIndex == -1 || hf.isFilteringCriteria(aFieldIndex)) {
					boolean lAdded = hf.add(aRow);
					if (lAdded) {
						added = true;
					}
				}
			}
		}
		return added;
	}

	/**
	 * Returns if <code>showOriginal</code> flag is set.
	 * 
	 * @return True if showOriginal flag is set.
	 */
	public boolean isShowOriginal() {
		return showOriginal;
	}

	/**
	 * Sets <code>showOriginal</code> flag to this rowset.
	 * 
	 * @param aShowOriginal
	 *            Flag, indicating this rowset show original rows vector.
	 */
	public void setShowOriginal(boolean aShowOriginal) {
		showOriginal = aShowOriginal;
		if (currentRowPos < 0) {
			currentRowPos = 0;
		}
		if (currentRowPos > size() + 1) {
			currentRowPos = size() + 1;
		}
	}

	/**
	 * Deletes current row. It means current row is marked as deleted and
	 * removed from cuurent rows vector. If cursor is not on the valid position
	 * no action is performed. Takes into account <code>showOriginal</code>
	 * flag. If <code>showOriginal</code> flag is setted, than no action is
	 * performed. If <code>showOriginal</code> flag setted, than no action is
	 * performed.
	 * 
	 * @see #delete(java.util.Set)
	 * @see #deleteAll()
	 * @throws RowsetException
	 */
	public void delete() throws RowsetException {
		if (!showOriginal) {
			checkCursor();
			Row row = getCurrentRow();
			assert row != null;
			if (rowsetChangeSupport.fireWillDeleteEvent(row)) {
				row.setDeleted();
				row.removePropertyChangeListener(this);
				row.removeVetoableChangeListener(this);
				generateDelete(row);
				current.remove(currentRowPos - 1);
				if (!isEmpty()) {
					if (isBeforeFirst()) {
						currentRowPos = 1;
					}
					if (isAfterLast()) {
						currentRowPos = current.size();
					}
				} else {
					currentRowPos = 0;
				}
				invalidateLocators();
				removeRowFromFilters(row, -1);
				modified = true;
				rowsetChangeSupport.fireRowDeletedEvent(row);
			}
		}
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
		if (!showOriginal) {
			/**
			 * The following approach is very harmful! If any events listener
			 * whould like to veto the deletion, than the cycle never ends.
			 * while(!isEmpty()) delete();
			 */
			boolean wasBeforeFirst = isBeforeFirst();
			boolean wasAfterLast = isAfterLast();
			for (int i = current.size() - 1; i >= 0; i--) {
				Row row = current.get(i);
				assert row != null;
				if (rowsetChangeSupport.fireWillDeleteEvent(row, i != 0)) { // last
					                                                        // iteration
					                                                        // will
					                                                        // fire
					                                                        // non-ajusting
					                                                        // event
					invalidateLocators();
					row.setDeleted();
					row.removePropertyChangeListener(this);
					row.removeVetoableChangeListener(this);
					generateDelete(row);
					current.remove(i);
					removeRowFromFilters(row, -1);
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
				if (wasBeforeFirst) {
					currentRowPos = 0;
				}
				if (wasAfterLast) {
					currentRowPos = size() + 1;
				}
				if (!wasBeforeFirst && !wasAfterLast && currentRowPos > size()) {
					currentRowPos = size();
				}
			}
			wideCheckCursor();
		}
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
		if (!showOriginal) {
			Set<Row> rows2Delete = new HashSet();
			rows2Delete.addAll(aRows2Delete);
			boolean wasBeforeFirst = isBeforeFirst();
			boolean wasAfterLast = isAfterLast();
			for (int i = current.size() - 1; i >= 0; i--) {
				Row row = current.get(i);
				assert row != null;
				if (rows2Delete.contains(row)) {
					rows2Delete.remove(row);
					if (rowsetChangeSupport.fireWillDeleteEvent(row, !rows2Delete.isEmpty())) { // last
						                                                                        // iteration
						                                                                        // will
						                                                                        // fire
						                                                                        // non-ajusting
						                                                                        // event
						invalidateLocators();
						row.setDeleted();
						row.removePropertyChangeListener(this);
						row.removeVetoableChangeListener(this);
						generateDelete(row);
						current.remove(i);
						removeRowFromFilters(row, -1);
						modified = true;
						currentRowPos = i + 1;
						rowsetChangeSupport.fireRowDeletedEvent(row, !rows2Delete.isEmpty()); // last
						                                                                      // iteration
						                                                                      // will
						                                                                      // fire
						                                                                      // non-ajusting
						                                                                      // event
						currentRowPos = Math.min(currentRowPos, current.size());
					}
				}
			}
			if (current.isEmpty()) {
				currentRowPos = 0;
			} else {
				if (wasBeforeFirst) {
					currentRowPos = 0;
				}
				if (wasAfterLast) {
					currentRowPos = size() + 1;
				}
				if (!wasBeforeFirst && !wasAfterLast && currentRowPos > size()) {
					currentRowPos = size();
				}
			}
			wideCheckCursor();
		}
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
	public void deleteAt(int aRowIndex) throws RowsetException {
		if (!showOriginal) {
			boolean wasBeforeFirst = isBeforeFirst();
			boolean wasAfterLast = isAfterLast();
			if (aRowIndex >= 1 && aRowIndex <= size()) {
				Row row = current.get(aRowIndex - 1);
				assert row != null;
				if (rowsetChangeSupport.fireWillDeleteEvent(row, false)) {
					// last iteration will fire non-ajusting event
					invalidateLocators();
					row.setDeleted();
					row.removePropertyChangeListener(this);
					row.removeVetoableChangeListener(this);
					generateDelete(row);
					current.remove(aRowIndex - 1);
					removeRowFromFilters(row, -1);
					modified = true;
					currentRowPos = aRowIndex;
					rowsetChangeSupport.fireRowDeletedEvent(row, false);
					// last iteration will fire non-ajusting event
					currentRowPos = Math.min(currentRowPos, current.size());
				}
				if (current.isEmpty()) {
					currentRowPos = 0;
				} else {
					if (wasBeforeFirst) {
						currentRowPos = 0;
					}
					if (wasAfterLast) {
						currentRowPos = size() + 1;
					}
					if (!wasBeforeFirst && !wasAfterLast && currentRowPos > size()) {
						currentRowPos = size();
					}
				}
				wideCheckCursor();
			} else
				throw new InvalidCursorPositionException("Cursor position pointing to deleted row must be in range [1, size()]");
		}
	}

	public Object getJsObject(int colIndex) throws Exception {
		return Utils.toJs(getObject(colIndex));
	}

	/**
	 * Returns value of particular field of current row by index of column.
	 * 
	 * @param colIndex
	 *            Index of particular field.
	 * @return Value of perticular field of current row by index of column.
	 * @throws InvalidColIndexException
	 * @throws InvalidCursorPositionException
	 * @see #updateObject(int colIndex, Object aValue)
	 */
	public Object getObject(int colIndex) throws InvalidColIndexException, InvalidCursorPositionException {
		checkCursor();
		checkColIndex(colIndex);
		Row row = getCurrentRow();
		assert row != null;
		if (showOriginal) {
			return row.getOriginalColumnObject(colIndex);
		} else {
			return row.getColumnObject(colIndex);
		}
	}

	/**
	 * Returns value of particular field of current row by index of column as
	 * string.
	 * 
	 * @param colIndex
	 *            Index of particular field.
	 * @return Value of perticular field of current row by index of column as
	 *         string.
	 * @throws InvalidColIndexException
	 * @throws InvalidCursorPositionException
	 * @see #updateObject(int colIndex, Object aValue)
	 * @see #getObject(int colIndex)
	 */
	public String getString(int colIndex) throws InvalidColIndexException, InvalidCursorPositionException {
		return (String) getObject(colIndex);
	}

	/**
	 * Returns value of particular field of current row by index of column as
	 * integer number.
	 * 
	 * @param colIndex
	 *            Index of particular field.
	 * @return Value of perticular field of current row by index of column as
	 *         integer number.
	 * @throws InvalidColIndexException
	 * @throws InvalidCursorPositionException
	 * @see #updateObject(int colIndex, Object aValue)
	 * @see #getObject(int colIndex)
	 */
	public Integer getInt(int colIndex) throws InvalidColIndexException, InvalidCursorPositionException {
		Object value = getObject(colIndex);
		if (value instanceof Integer) {
			return (Integer) getObject(colIndex);
		} else if (value instanceof Number) {
			return ((Number) value).intValue();
		} else {
			return null;
		}
	}

	/**
	 * Returns value of particular field of current row by index of column as
	 * date.
	 * 
	 * @param colIndex
	 *            Index of particular field.
	 * @return Value of perticular field of current row by index of column as
	 *         date.
	 * @throws InvalidColIndexException
	 * @throws InvalidCursorPositionException
	 * @see #updateObject(int colIndex, Object aValue)
	 * @see #getObject(int colIndex)
	 */
	public Date getDate(int colIndex) throws InvalidColIndexException, InvalidCursorPositionException {
		return (Date) getObject(colIndex);
	}

	/**
	 * Returns value of particular field of current row by index of column as
	 * double number.
	 * 
	 * @param colIndex
	 *            Index of particular field.
	 * @return Value of perticular field of current row by index of column as
	 *         double number.
	 * @throws InvalidColIndexException
	 * @throws InvalidCursorPositionException
	 * @see #updateObject(int colIndex, Object aValue)
	 * @see #getObject(int colIndex)
	 */
	public Double getDouble(int colIndex) throws InvalidColIndexException, InvalidCursorPositionException {
		Object value = getObject(colIndex);
		if (value instanceof Double) {
			return (Double) getObject(colIndex);
		} else if (value instanceof Number) {
			return ((Number) value).doubleValue();
		} else {
			return null;
		}
	}

	/**
	 * Returns value of particular field of current row by index of column as
	 * float number.
	 * 
	 * @param colIndex
	 *            Index of particular field.
	 * @return Value of perticular field of current row by index of column as
	 *         float number.
	 * @throws InvalidColIndexException
	 * @throws InvalidCursorPositionException
	 * @see #updateObject(int colIndex, Object aValue)
	 * @see #getObject(int colIndex)
	 */
	public Float getFloat(int colIndex) throws InvalidColIndexException, InvalidCursorPositionException {
		Object value = getObject(colIndex);
		if (value instanceof Float) {
			return (Float) getObject(colIndex);
		} else if (value instanceof Number) {
			return ((Number) value).floatValue();
		} else {
			return null;
		}
	}

	/**
	 * Returns value of particular field of current row by index of column as
	 * boolean value.
	 * 
	 * @param colIndex
	 *            Index of particular field.
	 * @return Value of perticular field of current row by index of column as
	 *         boolean value.
	 * @throws InvalidColIndexException
	 * @throws InvalidCursorPositionException
	 * @see #updateObject(int colIndex, Object aValue)
	 * @see #getObject(int colIndex)
	 */
	public Boolean getBoolean(int colIndex) throws InvalidColIndexException, InvalidCursorPositionException {
		return (Boolean) getObject(colIndex);
	}

	/**
	 * Returns value of particular field of current row by index of column as
	 * short number.
	 * 
	 * @param colIndex
	 *            Index of particular field.
	 * @return Value of perticular field of current row by index of column as
	 *         short number.
	 * @throws InvalidColIndexException
	 * @throws InvalidCursorPositionException
	 * @see #updateObject(int colIndex, Object aValue)
	 * @see #getObject(int colIndex)
	 */
	public Short getShort(int colIndex) throws InvalidColIndexException, InvalidCursorPositionException {
		Object value = getObject(colIndex);
		if (value instanceof Short) {
			return (Short) getObject(colIndex);
		} else if (value instanceof Number) {
			return ((Number) value).shortValue();
		} else {
			return null;
		}
	}

	/**
	 * Returns value of particular field of current row by index of column as
	 * long number.
	 * 
	 * @param colIndex
	 *            Index of particular field.
	 * @return Value of perticular field of current row by index of column as
	 *         long number.
	 * @throws InvalidColIndexException
	 * @throws InvalidCursorPositionException
	 * @see #updateObject(int colIndex, Object aValue)
	 * @see #getObject(int colIndex)
	 */
	public Long getLong(int colIndex) throws InvalidColIndexException, InvalidCursorPositionException {
		Object value = getObject(colIndex);
		if (value instanceof Long) {
			return (Long) getObject(colIndex);
		} else if (value instanceof Number) {
			return ((Number) value).longValue();
		} else {
			return null;
		}
	}

	public void updateJsObject(int colIndex, Object aValue) throws Exception {
		updateObject(colIndex, Utils.toJava(aValue));
	}

	/**
	 * Updates particular field of the current record.
	 * 
	 * @param colIndex
	 *            Index of particular field. 1-Based.
	 * @param aValue
	 *            Value you whant to be setted to field.
	 * @throws RowsetException
	 */
	public void updateObject(int colIndex, Object aValue) throws RowsetException {
		checkCursor();
		checkColIndex(colIndex);
		Row row = getCurrentRow();
		assert row != null;
		row.setColumnObject(colIndex, aValue);
	}

	private int extractColIndex(PropertyChangeEvent evt) {
		int colIndex = 0;
		if (evt.getPropagationId() != null && evt.getPropagationId() instanceof Integer) {
			colIndex = (Integer) evt.getPropagationId();
		} else {
			colIndex = fields.find(evt.getPropertyName());
		}
		return colIndex;
	}

	@Override
	public void vetoableChange(PropertyChangeEvent evt) throws Exception {
		int colIndex = extractColIndex(evt);
		assert colIndex != 0;
		assert evt.getSource() instanceof Row;
		if (!rowsetChangeSupport.fireWillChangeEvent((Row) evt.getSource(), colIndex, evt.getOldValue(), evt.getNewValue())) {
			throw new Exception("One of rowset's change listeners have prohibited a column change");
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		try {
			int colIndex = extractColIndex(evt);
			assert colIndex != 0;
			assert evt.getSource() instanceof Row;
			Row row = (Row) evt.getSource();
			invalidateLocatorsByColIndex(colIndex);
			row.getInternalCurrentValues().set(colIndex - 1, evt.getOldValue());
			removeRowFromFilters(row, colIndex);
			row.getInternalCurrentValues().set(colIndex - 1, evt.getNewValue());
			addRow2Filters(row, colIndex);
			modified = true;
			generateUpdate(colIndex, row, evt.getOldValue(), evt.getNewValue());
			rowsetChangeSupport.fireRowChangedEvent(row, colIndex, evt.getOldValue());
			if (activeFilter != null && immediateFilter && activeFilter.isFilteringCriteria(colIndex)) {
				activeFilter.refilterRowset();
			}
		} catch (RowsetException ex) {
			Logger.getLogger(Rowset.class.getName()).log(Level.SEVERE, null, ex);
		}
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
		} else if (!isEmpty() && !isBeforeFirst() && !isAfterLast()) {
			if (showOriginal) {
				return original.get(currentRowPos - 1);
			} else {
				return current.get(currentRowPos - 1);
			}
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
	 * Cancels modifications maded to this rowset. After that no difference
	 * between original and current rows vectors and row's data have place.
	 */
	public void originalToCurrent() throws RowsetException {
		Filter wasFilter = activeFilter;
		boolean wasApplied = false;
		if (wasFilter != null) {
			wasApplied = wasFilter.isApplied();
			wasFilter.cancelFilter();
		}
		try {
			current.clear();
			current.addAll(original);
			for (int i = current.size() - 1; i >= 0; i--) {
				Row row = current.get(i);
				assert row != null;
				row.originalToCurrent();
				if (row.isInserted()) {
					row.removePropertyChangeListener(this);
					row.removeVetoableChangeListener(this);
					current.remove(i);
					original.remove(i);
				}
				row.clearDeleted();
			}
			modified = false;
			invalidateLocators();
			invalidateFilters();
		} finally {
			if (wasFilter != null && wasApplied) {
				wasFilter.refilterRowset();// implicit setCurrent() call.
			}
		}
	}

	protected void generateInsert(Row aRow) {
		if (flow != null && flow.getChangeLog() != null) {
			if (transactionRegisration == null) {
				transactionRegisration = flow.addTransactionListener(this);
			}
			List<Change> changesLog = flow.getChangeLog();
			Insert insert = new Insert(flow.getEntityId());
			List<Change.Value> data = new ArrayList();
			for (int i = 0; i < aRow.getCurrentValues().length; i++) {
				Field field = aRow.getFields().get(i + 1);
				Object value = aRow.getCurrentValues()[i];
				if (value != null || field.isStrong4Insert()) {
					data.add(new Change.Value(field.getName(), value, field.getTypeInfo()));
				}
			}
			insert.data = data.toArray(new Change.Value[] {});
			changesLog.add(insert);
		}
	}

	private Change.Value[] generateChangeLogKeys(int colIndex, Row aRow, Object oldValue) {
		if (fields != null) {
			List<Change.Value> keys = new ArrayList();
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

	protected void generateUpdate(int colIndex, Row aRow, Object oldValue, Object newValue) {
		if (fields != null && flow != null && flow.getChangeLog() != null) {
			if (transactionRegisration == null) {
				transactionRegisration = flow.addTransactionListener(this);
			}
			List<Change> changesLog = flow.getChangeLog();
			Field field = fields.get(colIndex);
			Update update = new Update(flow.getEntityId());
			update.data = new Change.Value[] { new Change.Value(field.getName(), newValue, field.getTypeInfo()) };
			update.keys = generateChangeLogKeys(colIndex, aRow, oldValue);
			changesLog.add(update);
		}
	}

	protected void generateDelete(Row aRow) {
		if (flow != null && flow.getChangeLog() != null) {
			if (transactionRegisration == null) {
				transactionRegisration = flow.addTransactionListener(this);
			}
			List<Change> changesLog = flow.getChangeLog();
			Delete delete = new Delete(flow.getEntityId());
			delete.keys = generateChangeLogKeys(-1, aRow, null);
			changesLog.add(delete);
		}
	}

	/**
	 * Creates and returns new filter based on this rowset.
	 * 
	 * @return New filter based on this rowset.
	 */
	public Filter createFilter() {
		Filter hf = orderersFactory.createFilter();
		filters.add(hf);
		return hf;
	}

	/**
	 * Creates and returns new locator based on this rowset.
	 * 
	 * @return New locator based on this rowset.
	 */
	public Locator createLocator() {
		Locator hl = orderersFactory.createLocator();
		locators.add(hl);
		return hl;
	}

	/**
	 * Creates locator, that doesn't distinguish the null key and key that is
	 * not found in the rowset by <code>aParentColIndex</code> with
	 * <code>aByPkLocator</code>
	 * 
	 * @param aParentColIndex
	 *            Index of column that is used to achive key values to locate
	 *            "parent" rows.
	 * @param aByPkLocator
	 *            A <code>Locator</code> that is used to locate "parent" rows.
	 * @return New locator.
	 */
	public Locator createParentLocator(int aParentColIndex, Locator aByPkLocator) {
		Locator hl = new ParentLocator(this, aParentColIndex, aByPkLocator);
		locators.add(hl);
		return hl;
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
			activeFilter.cancelFilter();
		}
		filters.remove(aFilter);
		aFilter.die();
	}

	/**
	 * Removes specified locator from this rowset.
	 * 
	 * @param aLocator
	 *            Locator object to be removed.
	 */
	public void removeLocator(Locator aLocator) {
		locators.remove(aLocator);
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
				invalidateLocators();
				rowsetChangeSupport.fireSortedEvent();
			}
		}
	}

	public void reverse() throws InvalidCursorPositionException {
		if (rowsetChangeSupport.fireWillSortEvent()) {
			Collections.reverse(current);
			invalidateLocators();
			rowsetChangeSupport.fireSortedEvent();
		}
	}

	/*
	 * Invlidates all installed locators
	 */
	public void invalidateLocators() {
		if (locators != null) {
			for (Locator loc : locators) {
				assert loc != null;
				loc.invalidate();
			}
		}
	}

	/*
	 * Invlidates all installed filters and deactivates them. Also it cancels
	 * any active filter.
	 */
	protected void invalidateFilters() {
		activeFilter = null;
		if (filters != null) {
			for (Filter filter : filters) {
				assert filter != null;
				filter.deactivate();
				filter.invalidate();
			}
		}
	}

	/**
	 * Invlidates all installed locators with constrainting set including
	 * <code>aColIndex</code>.
	 * 
	 * @param aColIndex
	 *            Column index to examine constraints with.
	 */
	public void invalidateLocatorsByColIndex(int aColIndex) {
		if (locators != null) {
			for (Locator loc : locators) {
				assert loc != null;
				if (loc.getFields().contains(aColIndex)) {
					loc.invalidate();
				}
			}
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
}
