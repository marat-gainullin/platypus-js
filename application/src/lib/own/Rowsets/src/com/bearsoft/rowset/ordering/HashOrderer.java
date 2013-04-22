/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.ordering;

import com.bearsoft.rowset.utils.KeySet;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.locators.RowWrap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The base class for rowset's filters, locators and sorters.
 * @author mg
 */
public abstract class HashOrderer extends Object {

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
    protected Rowset rowset = null;
    protected Fields fields = null;
    protected List<Integer> fieldsIndicies = new ArrayList<>();
    protected boolean constrainting = false;
    protected Map<KeySet, List<RowWrap>> ordered = new HashMap<>();
    protected boolean caseSensitive = true;
    protected boolean valid = false;

    /**
     * Filter constructor.
     * @param aRowset The rowset this filter to be applied on.
     */
    public HashOrderer(Rowset aRowset) {
        super();
        rowset = aRowset;
        fields = aRowset.getFields();
    }

    /**
     * Returns the underlying rowset object.
     * @return Underlying rowset object.
     */
    public Rowset getRowset() {
        return rowset;
    }

    /**
     * Returns whether this filter is case sensitive.
     * @return Whether this filter is case sensitive or not.
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Sets case sensivity of this rowset's filter.
     * @param caseSensitive The case sensivity flag to be setted tothe filter.
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * Returns fields (columns) ordinal numbers vector. Theese fields (columns) are used to perform the filtering.
     * The indices are 1-based.
     * @return Fields (columns) ordinal numbers vector.
     */
    public List<Integer> getFields() {
        return fieldsIndicies;
    }

    /**
     * Sets fields (columns) ordinal numbers vector. Theese fields (columns) are used to perform the filtering.
     * The indices are 1-based.
     * @param fields Fields (columns) ordinal numbers vector.
     */
    public void setFields(List<Integer> fields) {
        this.fieldsIndicies = fields;
    }

    /**
     * Begins filtering fields (columns) vector building.
     * @throws IllegalStateException
     * @see #isConstrainting()
     * @see #addConstraint(int aFieldIndex)
     * @see #removeConstraint(int aFieldIndex)
     * @see #endConstrainting()
     * @see #isEmpty()
     */
    public void beginConstrainting() throws IllegalStateException {
        if (!constrainting) {
            constrainting = true;
        } else {
            throw new IllegalStateException(CONSTRAINTING_ALREADY_BEGUN);
        }
    }

    /**
     * Adds field (column) index to filtering fields (columns) vector
     * @param aFieldIndex A field (column) index to be added.
     * @throws IllegalStateException
     * @see #beginConstrainting()
     * @see #isConstrainting()
     * @see #removeConstraint(int aFieldIndex)
     * @see #endConstrainting()
     * @see #isEmpty()
     */
    public void addConstraint(int aFieldIndex) throws IllegalStateException {
        if (constrainting) {
            fieldsIndicies.add(aFieldIndex);
        } else {
            throw new IllegalStateException(CANT_ADD_CONSTRAINT_WHILE_NOT_CONSTRAINTING);
        }
    }

    /**
     * Removes field (column) index from filtering fields (columns) vector
     * @param aFieldIndex A field (column) index to be deleted.
     * @throws IllegalStateException
     * @see #beginConstrainting()
     * @see #isConstrainting()
     * @see #addConstraint(int aFieldIndex)
     * @see #endConstrainting()
     * @see #isEmpty()
     */
    public void removeConstraint(int aFieldIndex) throws IllegalStateException {
        if (constrainting) {
            fieldsIndicies.remove(aFieldIndex);
        } else {
            throw new IllegalStateException(CANT_REMOVE_CONSTRAINT_WHILE_NOT_CONSTRAINTING);
        }
    }

    /**
     * Ends the process of filtering fields vector constructing.
     * @throws IllegalStateException
     * @see #beginConstrainting()
     * @see #isConstrainting()
     * @see #addConstraint(int aFieldIndex)
     * @see #removeConstraint(int aFieldIndex)
     * @see #isEmpty()
     */
    public void endConstrainting() throws IllegalStateException {
        if (constrainting) {
            constrainting = false;
        } else {
            throw new IllegalStateException(CONSTRAINTING_NOT_BEGUN_YET);
        }
    }

    /**
     * Returns whether filtering fields (columns) vector building is in process.
     * @return Whether filtering fields (columns) vector building is in process.
     * @see #beginConstrainting()
     * @see #addConstraint(int aFieldIndex)
     * @see #removeConstraint(int aFieldIndex)
     * @see #endConstrainting()
     * @see #isEmpty()
     */
    public boolean isConstrainting() {
        return constrainting;
    }

    /**
     * Returns whether a <code>aFieldIndex</code> is in filtering conditions vector.
     * @param aFieldIndex Fields index to check.
     * @return Whether a <code>aFieldIndex</code> is in filtering conditions vector.
     * @see beginConstrainting()
     * @see isConstrainting()
     * @see addConstraint(int aFieldIndex)
     * @see removeConstraint(int aFieldIndex)
     * @see endConstrainting()
     * @see isEmpty()
     */
    public boolean isFilteringCriteria(int aFieldIndex) {
        return fieldsIndicies != null && fieldsIndicies.indexOf(aFieldIndex) != -1;
    }

    /**
     * Returns whether filtering fields vector is empty.
     * @return Whether filtering fields vector is empty.
     * @see #beginConstrainting()
     * @see #isConstrainting()
     * @see #addConstraint(int aFieldIndex)
     * @see #removeConstraint(int aFieldIndex)
     * @see #endConstrainting()
     */
    public boolean isEmpty() {
        return fieldsIndicies == null || fieldsIndicies.isEmpty();
    }

    /**
     * Helps the GC to do it's work by setting all properties to null.
     */
    public void die() {
        rowset = null;
        fields = null;
        fieldsIndicies = null;
        ordered = null;
    }

    /**
     * Builds a hash-ordered structure of the rowset's rows by multi fielded key set.
     * @throws RowsetException
     */
    public abstract void build() throws RowsetException;

    /**
     * Returns index of rows-related wrap in the hash-ordered structure.
     * @param aSubSet Subset of rows wraps in the hash-ordered structure.
     * @param aRow Row index of wich is to be returned.
     * @return
     */
    int indexOfRowWrap(List<RowWrap> aSubSet, Row aRow) {
        if (aSubSet != null && aRow != null) {
            for (int i = 0; i < aSubSet.size(); i++) {
                if (aSubSet.get(i) != null && aSubSet.get(i).getRow() == aRow) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Removes a row from hash-ordered structure of row wraps.
     * @param aRow Row is to be removed.
     * @return True if deletion actually happend, False otherwise.
     * @throws RowsetException
     */
    public boolean remove(Row aRow) throws RowsetException {
        KeySet ks = makeKeySet(aRow, fieldsIndicies);
        if (ks != null) {
            // delete from structure
            List<RowWrap> subset = ordered.get(ks);
            if (subset != null) {
                int rIndex = indexOfRowWrap(subset, aRow);
                if (rIndex >= 0 && rIndex < subset.size()) {
                    subset.remove(rIndex);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Makes key set from a row by fields indicies.
     * @param aRow The source row of the key set values.
     * @param fieldsIndicies Fields indicies to build key set by.
     * @return Key set been maded.
     * @throws RowsetException
     */
    protected KeySet makeKeySet(Row aRow, List<Integer> fieldsIndicies) throws RowsetException {
        KeySet ks = new KeySet();
        if (fields != null) {
            for (int i = 0; i < fieldsIndicies.size(); i++) {
                int fIndex = fieldsIndicies.get(i);
                if (fIndex >= 1 && fIndex <= fields.getFieldsCount()) {
                    Object oKey = aRow.getColumnObject(fIndex);
                    if (oKey != null && oKey instanceof String && !caseSensitive) {
                        oKey = ((String) oKey).toLowerCase();
                    }
                    ks.add(oKey);
                }
            }
        }
        return ks;
    }

    /**
     * Returns the rowset's current, probably filtered rows vector.
     * @return The rowset's current, probably filtered rows vector.
     */
    protected List<Row> getRowsetRows() {
        return rowset.getCurrent();
    }

    /**
     * Returns whether any filter is applied on the underlying rowset.
     * @return Whether any filter is applied on the underlying rowset.
     */
    protected boolean isAnyFilterInstalled() {
        return rowset != null && rowset.getActiveFilter() != null;
    }

    /**
     * Marks this orderer as invalid.
     */
    public void invalidate() {
        valid = false;
    }

    /**
     * Returns whether orderer is valid.
     * @return Whether orderer is valid.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Validates the orderer, building ordered structure of rows.
     * @throws RowsetException
     */
    public void validate() throws RowsetException {
        assert !valid;
        build();
        valid = true;
    }
}
