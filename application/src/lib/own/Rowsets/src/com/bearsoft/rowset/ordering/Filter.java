/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.ordering;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.utils.KeySet;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Filter class. Performs multi column filtering of rowset.
 *
 * @author mg
 */
public class Filter extends HashOrderer implements HasPublished {

    protected List<Row> originalRows;
    protected int originalPos;
    //
    protected boolean filterApplied;
    protected KeySet keysetApplied;
    protected JSObject published;

    /**
     * The filter's class constructor
     *
     * @param aFieldsIndicies
     */
    public Filter(List<Integer> aFieldsIndicies) {
        super(aFieldsIndicies);
    }

    /**
     * Returns applied values vector (KeySet) according to feilds indicies
     * vector, setted previously.
     *
     * @return Applied values KeySet.
     * @see #getFields()
     * @see #setFields(java.util.Vector)
     */
    public KeySet getKeysetApplied() {
        return keysetApplied;
    }

    /**
     * Returns whether this filter is applied to it's rowset.
     *
     * @return Whether this filter is applied to it's rowset.
     */
    @ScriptFunction(jsDoc = "/**\n"
            + " * Returns whether this filter is applied.\n"
            + "*/")
    public boolean isApplied() {
        return filterApplied;
    }

    /**
     * Returns rows vector that was in the rowset before this filter has been
     * applied.
     *
     * @return Rows vector that was in the rowset before this filter has been
     * applied.
     */
    public List<Row> getOriginalRows() {
        return originalRows;
    }

    /**
     * Returns rowset's cursor position was in the rowset before this filter has
     * been applied.
     *
     * @return Rowset's cursor position was in the rowset before this filter has
     * been applied.
     */
    public int getOriginalPos() {
        return originalPos;
    }

    /**
     * Sets rowset's cursor position was in the rowset before this filter has
     * been applied.
     *
     * @param aPos Position is to be setted.
     */
    public void setOriginalPos(int aPos) {
        originalPos = aPos;
    }

    @Override
    protected void keysChanged(Row Row) throws RowsetException {
        super.keysChanged(Row);
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
        filterRowset(keysetApplied);
    }

    /**
     * Applies this filter to the rowset with keys values according to fields
     * (columns) conditions collection already prepared with
     * <code>setFields()</code> or <code>...Constrainting()</code> methods.
     *
     * @param values Values vector.
     * @throws RowsetException
     * @see #beginConstrainting()
     * @see #isConstrainting()
     * @see #addConstraint(int aFieldIndex)
     * @see #removeConstraint(int aFieldIndex)
     * @see #endConstrainting()
     */
    public void filterRowset(Object... values) throws RowsetException {
        if (values != null && values.length > 0) {
            KeySet ks = new KeySet();
            ks.addAll(Arrays.asList(values));
            filterRowset(ks);
        } else {
            throw new RowsetException("bad filtering conditions. Absent or empty");
        }
    }
    private boolean filtering = false;

    /**
     * Applies this filter ti the rowset with keys values according to fields
     * (columns) conditions vector already prepared with
     * <code>setFields()</code> or <code>...Constrainting()</code> methods.
     *
     * @param values Values <code>KeySet</code>.
     * @throws RowsetException
     * @see #beginConstrainting()
     * @see #isConstrainting()
     * @see #addConstraint(int aFieldIndex)
     * @see #removeConstraint(int aFieldIndex)
     * @see #endConstrainting()
     */
    public void filterRowset(KeySet values) throws RowsetException {
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
                                    keyValue = rowset.getConverter().convert2RowsetCompatible(keyValue, keyField.getTypeInfo());
                                    if (!caseSensitive && keyValue != null && keyValue instanceof String) {
                                        keyValue = ((String) keyValue).toLowerCase();
                                    }
                                    values.set(i, keyValue);
                                } else {
                                    throw new RowsetException("Filtering keys array is greater then rowset's fields array");
                                }
                            }
                            if (filterApplied) {
                                filterApplied = false;
                            }
                            Filter activeFilter = rowset.getActiveFilter();
                            boolean foreignFilter = activeFilter == null || activeFilter != this;
                            if (foreignFilter && activeFilter != null) {
                                activeFilter.cancelFilter();
                            }
                            if (!filterApplied) {
                                boolean wasBeforeFirst = rowset.isBeforeFirst();
                                boolean wasAfterLast = rowset.isAfterLast();
                                List<Row> subSet = ordered.get(values);
                                if (subSet == null) {
                                    subSet = new ArrayList<>();
                                }
                                if (foreignFilter) {
                                    originalRows = rowset.getCurrent();
                                    originalPos = rowset.getCursorPos();
                                }
                                rowset.setCurrent(subSet);
                                rowset.setActiveFilter(this);
                                Set<RowsetListener> l = rowset.getRowsetChangeSupport().getRowsetListeners();
                                rowset.getRowsetChangeSupport().setRowsetListeners(null);
                                try {
                                    if (wasBeforeFirst) {
                                        rowset.beforeFirst();
                                    } else if (wasAfterLast) {
                                        rowset.afterLast();
                                    } else if (!rowset.isEmpty()) {
                                        rowset.first();
                                    }
                                } finally {
                                    rowset.getRowsetChangeSupport().setRowsetListeners(l);
                                }
                                filterApplied = true;
                                keysetApplied = values;
                            }
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

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Applies the filter with values passed in. Values correspond to key fields in createFilter() call.\n"
            + " * @param values Values for keys in createFilter() call.\n"
            + " */", params = "values")
    public void apply(Object... values) throws RowsetException {
        filterRowset(values);
    }

    @ScriptFunction(jsDoc = "/**\n"
            + " * Cancels applied filter.\n"
            + " */")
    public void cancel() throws RowsetException {
        cancelFilter();
    }

    /**
     * Returns whether any filter is applied on the underlying rowset.
     *
     * @return Whether any filter is applied on the underlying rowset.
     */
    protected boolean isAnyFilterInstalled() {
        return rowset != null && rowset.getActiveFilter() != null;
    }

    /**
     * Cancels this filter from rowset.
     *
     * @throws IllegalStateException
     * @throws RowsetException
     */
    public void cancelFilter() throws RowsetException {
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
                                boolean positioned = rowset.absolute(originalPos);
                                if (!rowset.isEmpty() && !positioned) {
                                    rowset.first();
                                }
                                originalPos = 0;
                                rowset.getRowsetChangeSupport().fireFilteredEvent();
                            }
                        } else {
                            throw new RowsetException(ORIGINAL_ROWS_IS_MISSING);
                        }
                        filterApplied = false;
                        assert !isAnyFilterInstalled();
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
        super.rowInserted(event);
        if (filterApplied) {
            Row insertingRow = event.getRow();
            // work on rowset's native rows, hided by the filter
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
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @Override
    public void setPublished(JSObject aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}
