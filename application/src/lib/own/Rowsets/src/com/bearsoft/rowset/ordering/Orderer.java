/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.ordering;

import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.RowsetContainer;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetEventsEarlyAccess;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetNextPageEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The base class for rowset's filters, locators and orderers.
 *
 * @author mg
 */
public class Orderer extends RowsetAdapter implements RowsetEventsEarlyAccess, RowsetContainer {

    public static final String CANT_ADD_CONSTRAINT_WHILE_NOT_CONSTRAINTING = "can\'t add constraint while constrainting is not begun";
    public static final String CANT_APPLY_NULL_KEY_SET = "can\'t apply null key set";
    public static final String CANT_BUILD_FILTER_ON_FILTER = "can\'t build filter on filter";
    public static final String CANT_BUILD_FILTER_WHILE_CONSTRAINTING = "can\'t build filter while constrainting";
    public static final String CANT_REMOVE_CONSTRAINT_WHILE_NOT_CONSTRAINTING = "can\'t remove constraint while constrainting is not begun";
    public static final String CONSTRAINTING_ALREADY_BEGUN = "can\'t begin constrainting while it\'s already begun";
    public static final String CONSTRAINTING_NOT_BEGUN_YET = "can\'t end constrainting while it\'s not begun";
    public static final String FILTER_ALREADY_APPLIED = "can\'t apply already applied filter";
    public static final String ORIGINAL_ROWS_IS_MISSING = "original rows is missing!";
    public static final String ROWSET_MISSING = "rowset missing";

    protected Rowset rowset;
    protected List<Integer> fieldsIndicies = new ArrayList<>();
    protected Map<List<Object>, ObservableLinkedHashSet<Row>> ordered;
    protected PropertyChangeListener keysInvalidator = (PropertyChangeEvent evt) -> {
        try {
            Row row = (Row) evt.getSource();
            int colIndex = (Integer) evt.getPropagationId();
            keysChanged(row, colIndex, evt.getOldValue(), evt.getNewValue());
        } catch (RowsetException ex) {
            Logger.getLogger(Orderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    };
    protected final boolean stable;
    protected boolean caseSensitive = true;

    /**
     * Filter constructor.
     *
     * @param aFieldsIndicies
     * @param aStable
     */
    public Orderer(List<Integer> aFieldsIndicies, boolean aStable) {
        super();
        fieldsIndicies = aFieldsIndicies;
        stable = aStable;
    }

    public Orderer(List<Integer> aFieldsIndicies) {
        this(aFieldsIndicies, false);
    }

    public boolean isStable() {
        return stable;
    }

    /**
     * Returns the underlying rowset object.
     *
     * @return Underlying rowset object.
     */
    @Override
    public Rowset getRowset() {
        return rowset;
    }

    public void setRowset(Rowset aValue) {
        if (rowset != aValue) {
            if (rowset != null) {
                ordered = null;
                rowset.removeRowsetListener(this);
            }
            rowset = aValue;
            if (rowset != null) {
                ordered = new HashMap<>();
                addRows();
                rowset.addRowsetListener(this);
            }
        }
    }

    protected void clear() {
        ordered.values().stream().forEach((ObservableLinkedHashSet<Row> aContent) -> {
            aContent.stream().forEach((Row aRow) -> {
                unsignFrom(aRow);
            });
        });
        if (stable) {
            ordered.values().stream().forEach((ObservableLinkedHashSet<Row> aContent) -> {
                aContent.clear();
            });
        } else {
            ordered.clear();
        }
    }

    protected void addRows() {
        Consumer<Row> adder = (Row aRow) -> {
            try {
                add(aRow);
            } catch (InvalidColIndexException ex) {
                Logger.getLogger(Orderer.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        if (rowset.getActiveFilter() != null) {
            rowset.getActiveFilter().getOriginalRows().stream().forEach(adder);
        } else {
            rowset.getCurrent().stream().forEach(adder);
        }
    }

    /**
     * Returns whether this filter is case sensitive.
     *
     * @return Whether this filter is case sensitive or not.
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Sets case sensivity of this rowset's filter.
     *
     * @param aValue The case sensivity flag to be setted tothe filter.
     */
    public void setCaseSensitive(boolean aValue) {
        caseSensitive = aValue;
    }

    /**
     * Returns fields (columns) ordinal numbers vector. Theese fields (columns)
     * are used to perform the filtering. The indices are 1-based.
     *
     * @return Fields (columns) ordinal numbers vector.
     */
    public List<Integer> getFields() {
        return Collections.unmodifiableList(fieldsIndicies);
    }

    public boolean isCriterion(int aFieldIndex) {
        return fieldsIndicies != null && fieldsIndicies.contains(aFieldIndex);
    }

    public boolean isEmpty() {
        return fieldsIndicies == null || fieldsIndicies.isEmpty();
    }

    /**
     * Adds a row to hash-ordered structure of rows
     *
     * @param aRow Row is to be added.
     * @return True if aRow wass added to the structure, False otherwise.
     * @throws com.bearsoft.rowset.exceptions.InvalidColIndexException
     */
    public boolean add(Row aRow) throws InvalidColIndexException {
        return add(aRow, true);
    }

    public boolean add(Row aRow, boolean addToSubset) throws InvalidColIndexException {
        if (!aRow.isDeleted()) {
            List<Object> ks = makeKeys(aRow, fieldsIndicies);
            if (ks != null) {
                ObservableLinkedHashSet<Row> subset = ordered.get(ks);
                // add to structure
                if (subset == null) {
                    subset = new ObservableLinkedHashSet<>();
                    ordered.put(ks, subset);
                }
                signOn(aRow);
                if (addToSubset) {
                    subset.add(aRow);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a row from hash-ordered structure of rows.
     *
     * @param aRow Row is to be removed.
     * @return True if deletion actually happend, False otherwise.
     * @throws RowsetException
     */
    public boolean remove(Row aRow) throws RowsetException {
        return remove(aRow, true);
    }

    public boolean remove(Row aRow, boolean removeFromSubset) throws RowsetException {
        List<Object> ks = makeKeys(aRow, fieldsIndicies);
        if (ks != null) {
            // delete from structure
            Collection<Row> subset = ordered.get(ks);
            if (subset != null) {
                if (removeFromSubset) {
                    subset.remove(aRow);
                }
                unsignFrom(aRow);
                if (!stable && subset.isEmpty()) {
                    ordered.remove(ks);
                }
                // Subset may be used by applied filter and so
                // row may be already removed from it.
                // So, we have to perform our work regardless of subset.remove() result.
                return true;
            }
        }
        return false;
    }

    /**
     * Checks wheither the orderer contains rows, corresponding to values,
     * passed in. Performs converting of passed values from thier's types to
     * rowset's corresponding fields types.
     *
     * @param values Keys to check
     * @return Whether subset of rows is found.
     * @throws RowsetException
     */
    public boolean containsKeys(Object... values) throws RowsetException {
        if (values != null && values.length > 0) {
            List<Object> ks = valuesToKeys(values);
            return ordered.containsKey(ks);
        }
        return false;
    }

    /**
     * Finds subset of rows, corresponding to values, passed to the method.
     * Performs converting of passed values from thier's types to rowset's
     * corresponding fields types.
     *
     * @param values Keys vector to find
     * @return Whether subset of rows is found.
     * @throws RowsetException
     */
    public Collection<Row> get(Object... values) throws RowsetException {
        if (values != null && values.length > 0) {
            List<Object> ks = valuesToKeys(values);
            return ordered.get(ks);
        } else {
            return null;
        }
    }

    public ObservableLinkedHashSet<Row> get(List<Object> values) throws RowsetException {
        if (values != null && !values.isEmpty()) {
            List<Object> ks = valuesToKeys(values.toArray());
            return ordered.get(ks);
        } else {
            return null;
        }
    }

    public void put(List<Object> values, ObservableLinkedHashSet<Row> aValue) throws RowsetException {
        if (values != null && !values.isEmpty()) {
            List<Object> ks = valuesToKeys(values.toArray());
            ordered.put(ks, aValue);
        }
    }

    protected List<Object> valuesToKeys(Object[] values) throws RowsetException {
        List<Object> ks = new ArrayList<>();
        assert fieldsIndicies.size() == values.length;
        for (int i = 0; i < fieldsIndicies.size(); i++) {
            Object value = rowset.getConverter().convert2RowsetCompatible(values[i], rowset.getFields().get(fieldsIndicies.get(i)).getTypeInfo());
            ks.add(value);
        }
        return ks;
    }

    /**
     * Makes key set from a row by fields indicies.
     *
     * @param aRow The source row of the key set values.
     * @param fieldsIndicies Fields indicies to build key set by.
     * @return Key set been maded.
     * @throws com.bearsoft.rowset.exceptions.InvalidColIndexException
     */
    protected List<Object> makeKeys(Row aRow, List<Integer> fieldsIndicies) throws InvalidColIndexException {
        List<Object> ks = new ArrayList<>();
        Fields _fields = aRow.getFields();
        for (int i = 0; i < fieldsIndicies.size(); i++) {
            int fIndex = fieldsIndicies.get(i);
            if (fIndex >= 1 && fIndex <= _fields.getFieldsCount()) {
                Object oKey = aRow.getColumnObject(fIndex);
                if (oKey != null && oKey instanceof String && !caseSensitive) {
                    oKey = ((String) oKey).toLowerCase();
                }
                ks.add(oKey);
            }
        }
        return ks;
    }

    protected void signOn(Row aRow) {
        Fields _fields = aRow.getFields();
        for (int i = 0; i < fieldsIndicies.size(); i++) {
            int fIndex = fieldsIndicies.get(i);
            if (fIndex >= 1 && fIndex <= _fields.getFieldsCount()) {
                Field field = _fields.get(fIndex);
                String fname = field.getName();
                aRow.addPropertyChangeListener(fname, keysInvalidator);
            }
        }
    }

    protected void unsignFrom(Row aRow) {
        Fields _fields = aRow.getFields();
        for (int i = 0; i < fieldsIndicies.size(); i++) {
            int fIndex = fieldsIndicies.get(i);
            if (fIndex >= 1 && fIndex <= _fields.getFieldsCount()) {
                Field field = _fields.get(fIndex);
                String fname = field.getName();
                aRow.removePropertyChangeListener(fname, keysInvalidator);
            }
        }
    }

    @Override
    public void rowDeleted(RowsetDeleteEvent event) {
        try {
            remove(event.getRow());
        } catch (RowsetException ex) {
            Logger.getLogger(Orderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void rowInserted(RowsetInsertEvent event) {
        try {
            add(event.getRow());
        } catch (RowsetException ex) {
            Logger.getLogger(Orderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void rowsetRequeried(RowsetRequeryEvent event) {
        clear();
        addRows();
    }

    @Override
    public void rowsetNextPageFetched(RowsetNextPageEvent event) {
        clear();
        addRows();
    }

    @Override
    public void rowsetRolledback(RowsetRollbackEvent event) {
        clear();
        addRows();
    }

    protected void keysChanged(Row aRow, int aColIndex, Object aOldValue, Object aNewValue) throws RowsetException {
        List<Object> keysToRemove = new ArrayList<>();
        Fields _fields = aRow.getFields();
        for (int i = 0; i < fieldsIndicies.size(); i++) {
            int fIndex = fieldsIndicies.get(i);
            if (fIndex >= 1 && fIndex <= _fields.getFieldsCount()) {
                Object oKey = aRow.getColumnObject(fIndex);
                if (aColIndex == fIndex) {
                    oKey = aOldValue;
                }
                if (oKey != null && oKey instanceof String && !caseSensitive) {
                    oKey = ((String) oKey).toLowerCase();
                }
                keysToRemove.add(oKey);
            }
        }
        // delete from structure
        Collection<Row> subset = ordered.get(keysToRemove);
        if (subset != null) {
            subset.remove(aRow);
            unsignFrom(aRow);
            if (!stable && subset.isEmpty()) {
                ordered.remove(keysToRemove);
            }
        }
        add(aRow);
    }
}
