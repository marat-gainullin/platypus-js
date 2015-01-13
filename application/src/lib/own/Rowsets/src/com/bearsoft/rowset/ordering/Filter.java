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
public class Filter extends Orderer implements HasPublished {

    protected List<Row> originalRows;
    protected int originalPos;
    //
    protected boolean filterApplied;
    protected List<Object> appliedKeys;
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
     * @param Row
     * @param aColIndex
     * @param aOldValue
     * @param aNewValue
     * @throws com.bearsoft.rowset.exceptions.RowsetException
     */
    @Override
    protected void keysChanged(final Row Row, final int aColIndex, final Object aOldValue, final Object aNewValue) throws RowsetException {
        super.keysChanged(Row, aColIndex, aOldValue, aNewValue);
        if (rowset.isImmediateFilter()) {
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
                                    keyValue = rowset.getConverter().convert2RowsetCompatible(keyValue, keyField.getTypeInfo());
                                    if (!caseSensitive && keyValue != null && keyValue instanceof String) {
                                        keyValue = ((String) keyValue).toLowerCase();
                                    }
                                    values.set(i, keyValue);
                                } else {
                                    throw new RowsetException("Filtering keys array is greater then rowset's fields array");
                                }
                            }
                            boolean wasBeforeFirst = rowset.isBeforeFirst();
                            boolean wasAfterLast = rowset.isAfterLast();
                            ObservableLinkedHashSet<Row> subSet = ordered.get(values);
                            if (subSet == null) {
                                subSet = new ObservableLinkedHashSet<>();
                                ordered.put(values, subSet);
                            }
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
                            rowset.setCurrent(new ArrayList<>(Arrays.asList(subSet.toArray(new Row[]{}))));
                            rowset.setActiveFilter(this);
                            Set<RowsetListener> listeners = rowset.getRowsetChangeSupport().getRowsetListeners();
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

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Applies the filter with values passed in. Values correspond to key fields in createFilter() call.\n"
            + " * @param values Values for keys in createFilter() call.\n"
            + " */", params = "values")
    public void apply(Object... values) throws RowsetException {
        if (values != null && values.length > 0) {
            List<Object> ks = new ArrayList<>();
            ks.addAll(Arrays.asList(values));
            apply(ks);
        } else {
            throw new RowsetException("bad filtering conditions. Absent or empty");
        }
    }

    public void deactivate() {
        filterApplied = false;
        originalRows = null;
        originalPos = 0;
    }

    @ScriptFunction(jsDoc = "/**\n"
            + " * Cancels applied filter.\n"
            + " */")
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
        super.rowInserted(event);
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
