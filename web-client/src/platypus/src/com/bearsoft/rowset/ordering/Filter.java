/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.ordering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.Utils.JsObject;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Filter class. Performs multi column filtering of rowset.
 * 
 * @author mg
 */
public class Filter extends Orderer {

	protected List<Row> originalRows;
	protected int originalPos;
	//
	protected boolean filterApplied;
	protected List<Object> appliedKeys;

	/**
	 * The filter's class constructor
	 * 
	 * @param aFieldsIndicies
	 */
	public Filter(List<Integer> aFieldsIndicies) {
		super(aFieldsIndicies);
	}

	/**
	 * Returns applied keys List according to feilds indicies vector, setted
	 * previously.
	 * 
	 * @return Applied values KeySet.
	 * @see #getFields()
	 * @see #setFields(java.util.Vector)
	 */
	public List<Object> getAppliedKeys() {
		return appliedKeys;
	}

	/**
	 * Returns whether this filter is applied to it's rowset.
	 * 
	 * @return Whether this filter is applied to it's rowset.
	 */
	public boolean isApplied() {
		return filterApplied;
	}

	/**
	 * Returns rows vector that was in the rowset before this filter has been
	 * applied.
	 * 
	 * @return Rows vector that was in the rowset before this filter has been
	 *         applied.
	 */
	public List<Row> getOriginalRows() {
		return originalRows;
	}

	/**
	 * Returns rowset's cursor position was in the rowset before this filter has
	 * been applied.
	 * 
	 * @return Rowset's cursor position was in the rowset before this filter has
	 *         been applied.
	 */
	public int getOriginalPos() {
		return originalPos;
	}

	/**
	 * Sets rowset's cursor position was in the rowset before this filter has
	 * been applied.
	 * 
	 * @param Row
	 * @param aColIndex
	 * @param aOldValue
	 * @param aNewValue
	 * @throws com.bearsoft.rowset.exceptions.RowsetException
	 */
	@Override
	protected void keysChanged(final Row Row, final int aColIndex, final Object aOldValue, final Object aNewValue) throws RowsetException {
		super.keysChanged(Row, aColIndex, aOldValue, aNewValue);
		if (filterApplied && rowset.isImmediateFilter()) {
			refilterRowset();
		}
	}

	/**
	 * Reapplies this filtrer to rowset.
	 * 
	 * @throws RowsetException
	 */
	public void refilterRowset() throws RowsetException {
		apply(appliedKeys);
		/*
		 * if (!filterApplied || !rowset.isImmediateFilter()) { clear();
		 * addRows(); apply(appliedKeys); }
		 */
	}

	private boolean filtering = false;

	/**
	 * Applies this filter ti the rowset with keys values according to fields
	 * (columns) conditions vector already prepared with
	 * <code>setFields()</code> or <code>...Constrainting()</code> methods.
	 * 
	 * @param values
	 *            Values <code>KeySet</code>.
	 * @throws RowsetException
	 * @see #beginConstrainting()
	 * @see #isConstrainting()
	 * @see #addConstraint(int aFieldIndex)
	 * @see #removeConstraint(int aFieldIndex)
	 * @see #endConstrainting()
	 */
	public void apply(List<Object> values) throws RowsetException {
		if (rowset != null) {
			if (!filtering) {
				filtering = true;
				try {
					if (values != null) {
						if (rowset.getRowsetChangeSupport().fireWillFilterEvent()) {
							for (int i = 0; i < values.size(); i++) {
								Object keyValue = values.get(i);
								if (i < fieldsIndicies.size()) {
									int keyFieldColIndex = fieldsIndicies.get(i);
									Field keyField = rowset.getFields().get(keyFieldColIndex);
									keyValue = Converter.convert2RowsetCompatible(keyValue, keyField.getTypeInfo());
									if (!caseSensitive && keyValue != null && keyValue instanceof String) {
										keyValue = ((String) keyValue).toLowerCase();
									}
									values.set(i, keyValue);
								} else {
									throw new RowsetException("Filtering keys array is greater then rowset's fields array");
								}
							}
							LinkedHashSet<Row> subSet = ordered.get(values);
							if (!filterApplied) {
								if (rowset.getActiveFilter() != null) {
									originalRows = rowset.getActiveFilter().getOriginalRows();
									originalPos = rowset.getActiveFilter().getOriginalPos();
									rowset.getActiveFilter().deactivate();
								} else {
									originalRows = rowset.getCurrent();
									originalPos = rowset.getCursorPos();
								}
							} else {
								assert rowset.getActiveFilter() == this : "filter applied flag has unactual value";
							}
							rowset.setCurrent(subSet != null ? new ArrayList<Row>(Arrays.asList(subSet.toArray(new Row[] {}))) : new ArrayList<Row>());
							rowset.setActiveFilter(this);
							Set<RowsetListener> listeners = rowset.getRowsetChangeSupport().getRowsetListeners();
							rowset.getRowsetChangeSupport().setRowsetListeners(null);
							try {
								if (rowset.isEmpty())
									rowset.setCursorPos(0);
								else
									rowset.setCursorPos(1);
							} finally {
								rowset.getRowsetChangeSupport().setRowsetListeners(listeners);
							}
							filterApplied = true;
							appliedKeys = values;
							rowset.getRowsetChangeSupport().fireFilteredEvent();
						}
					} else {
						throw new RowsetException(CANT_APPLY_NULL_KEY_SET);
					}
				} finally {
					filtering = false;
				}
			}
		} else {
			throw new RowsetException(ROWSET_MISSING);
		}
	}

	public void apply(Object... values) throws RowsetException {
		if (values != null && values.length > 0) {
			List<Object> ks = new ArrayList<>();
			ks.addAll(Arrays.asList(values));
			apply(ks);
		} else {
			throw new RowsetException("bad filtering conditions. Absent or empty");
		}
	}

	public void jsApply(JavaScriptObject aArguments) throws Exception {
		List<Object> oArgs = new ArrayList<>();
		int length = aArguments.<JsObject> cast().getInteger("length");
		for (int i = 0; i < length; i++) {
			Object javaValue = Utils.toJava(aArguments.<JsObject> cast().getSlot(i));
			oArgs.add(javaValue);
		}
		apply(oArgs);
	}

	public void deactivate() {
		filterApplied = false;
		originalRows = null;
		originalPos = 0;
	}

	public void cancel() throws RowsetException {
		if (rowset != null) {
			if (!filtering) {
				filtering = true;
				try {
					if (filterApplied) {
						if (originalRows != null) {
							if (rowset.getRowsetChangeSupport().fireWillFilterEvent()) {
								rowset.setActiveFilter(null);
								for (int i = originalRows.size() - 1; i >= 0; i--) {
									if (originalRows.get(i).isDeleted()) {
										originalRows.remove(i);
									}
								}
								rowset.setCurrent(originalRows);
								originalRows = null;
								boolean positioned = rowset.setCursorPos(originalPos);
								if (!rowset.isEmpty() && !positioned) {
									rowset.setCursorPos(1);
								}
								originalPos = 0;
								rowset.getRowsetChangeSupport().fireFilteredEvent();
							}
						} else {
							throw new RowsetException(ORIGINAL_ROWS_IS_MISSING);
						}
						filterApplied = false;
						assert rowset.getActiveFilter() == null;
					}
				} finally {
					filtering = false;
				}
			}
		} else {
			throw new RowsetException(ROWSET_MISSING);
		}
	}

	@Override
	public void rowInserted(RowsetInsertEvent event) {
		Row insertingRow = event.getRow();
		// work on rowset's native rows, hided by the filter
		if(filterApplied){
			if (originalPos == 0) { // before first
				originalRows.add(0, insertingRow);
				originalPos = 1;
			} else if (originalPos > originalRows.size()) {
				originalRows.add(insertingRow);
				originalPos = originalRows.size();
			} else {
				originalRows.add(originalPos, insertingRow);
				originalPos++;
			}
		}
		super.rowInserted(event);
	}

	protected JavaScriptObject jsPublished;

	public void setPublished(JavaScriptObject aPublished) {
		jsPublished = aPublished;
	}

	public JavaScriptObject getPublished() {
		return jsPublished;
	}

	public static native JavaScriptObject publishFacade(Filter aFilter) throws Exception/*-{
		if (aFilter != null) {
			var published = aFilter.@com.bearsoft.rowset.ordering.Filter::getPublished()();
			if (published == null) {
				published = {
					apply : function() {
						aFilter.@com.bearsoft.rowset.ordering.Filter::jsApply(Lcom/google/gwt/core/client/JavaScriptObject;)(arguments);
					},
					cancel : function() {
						aFilter.@com.bearsoft.rowset.ordering.Filter::cancel()();
					},
					unwrap : function() {
						return aFilter;
					}
				}
				Object.defineProperty(published, "applied", {
					get : function(){
						return aFilter.@com.bearsoft.rowset.ordering.Filter::isApplied()();
					}
				});
				aFilter.@com.bearsoft.rowset.ordering.Filter::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			return published;
		} else
			return null;
	}-*/;

}
