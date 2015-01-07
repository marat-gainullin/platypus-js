/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.ordering;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetEventsEarlyAccess;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.events.RowsetNextPageEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetSortEvent;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.ordering.HashOrderer;
import com.bearsoft.rowset.utils.KeySet;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Locator is filter subclass, intended to locate particular rows. It doesn't
 * filter rowset, but rows partition is also used.
 *
 * @author mg
 */
public class Locator {

    protected class Invalidator extends RowsetAdapter implements RowsetEventsEarlyAccess {

        @Override
        public void rowsetSorted(RowsetSortEvent event) {
            invalidate();
        }

        @Override
        public void rowDeleted(RowsetDeleteEvent event) {
            invalidate();
        }

        @Override
        public void rowInserted(RowsetInsertEvent event) {
            invalidate();
        }

        @Override
        public void rowsetFiltered(RowsetFilterEvent event) {
            invalidate();
        }

        @Override
        public void rowsetRequeried(RowsetRequeryEvent event) {
            invalidate();
        }

        @Override
        public void rowsetNextPageFetched(RowsetNextPageEvent event) {
            invalidate();
        }

        @Override
        public void rowsetRolledback(RowsetRollbackEvent event) {
            invalidate();
        }
    }

    public static final String LOCATOR_IS_INVALID = "locator is invalid! Rowset was edited but locator havn\'t been rebuild.";
    public static final String INDEX_IS_INVALID = "index is out of bounds";
    public static final String WRONG_POSITION_MARKER = "invalid position in locator's subset";
    protected int subSetPos = -1;
    protected Rowset rowset;
    protected RowsetListener invalidator = new Invalidator();
    protected Map<Row, Integer> indices = new HashMap<>();

    /**
     * Locator's constructor.
     *
     * @param aRowset Rowset this locator is build for.
     */
    public Locator(Rowset aRowset) {
        super();
        rowset = aRowset;
        rowset.addRowsetListener(invalidator);
    }

    /**
     * Returns index of row in the row's subset, defined by
     * <code>HashOrderer</code>.
     *
     * @param aRow Row which index is returned.
     * @return Index of row in the row's subset, defined by
     * <code>HashOrderer</code>.
     * @throws IllegalStateException
     */
    public int indexOf(Row aRow) throws IllegalStateException {
        validate();
        Integer idx = indices.get(aRow);
        return idx != null ? idx : -1;
    }

    /**
     * This function changes row position within same locator's subset of rows
     *
     * @param aRow A Row, that position is to be changed.
     * @param aIndex Index, the row would be at.
     * @return A <code>Row</code> object.
     * @throws ArrayIndexOutOfBoundsException
    public Row changeRowPosition(Row aRow, int aIndex) throws ArrayIndexOutOfBoundsException {
        if (subSet != null && !subSet.isEmpty()) {
            if (rowset != null) {
                if (valid) {
                    int lIndex = -1;
                    RowWrap rw = null;
                    for (int i = 0; i < subSet.size(); i++) {
                        if (subSet.get(i).getRow() == aRow) {
                            lIndex = i;
                            rw = subSet.get(i);
                            break;
                        }
                    }
                    if (rw != null) {
                        if (aIndex != lIndex) {
                            subSet.remove(lIndex);
                            subSet.add(aIndex, rw);
                        }
                        return rw.getRow();
                    }
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        }
        return null;
    }
    */

    /**
     * Returns size of current subset. Current subset is defined when
     * <code>find()</code> method is called.
     *
     * @return Size of current subset, corresponding to <code>KeySet</code>, has
     * been passed to <code>find()</code> method
     * @throws IllegalStateException
     */
    public int getSize() throws IllegalStateException {
        if (subSet != null && !subSet.isEmpty()) {
            if (rowset != null) {
                if (valid) {
                    return subSet.size();
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        } else {
            return 0;
        }
    }

    /**
     * Returns row position in rowset. Row is the subset's element at
     * <code>aSubsetPos</code>.
     *
     * @param aSubsetPos Subset's element position. It's 1-based.
     * @return Row position in rowset.
     * @throws IllegalStateException
     */
    public int getRowsetPos(int aSubsetPos) throws IllegalStateException {
        if (subSet != null && !subSet.isEmpty()) {
            if (rowset != null) {
                if (valid) {
                    if (aSubsetPos >= 1 && aSubsetPos <= subSet.size()) {
                        RowWrap rw = subSet.get(aSubsetPos - 1);
                        if (rw != null) {
                            return rw.getIndex();
                        }
                    } else {
                        throw new IllegalStateException(WRONG_POSITION_MARKER);
                    }
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        }
        return -1;
    }

    /**
     * Prepares the locator for iterating through underlying rowset. It acts
     * like rowset's method <code>beforeFirst()</code>
     */
    public void beforeFirst() {
        subSetPos = -1;
    }

    /**
     * Prepares the locator for iterating through underlying rowset. It acts
     * like rowset's method <code>afterLast()</code>
     */
    public void afterLast() {
        if (subSet != null) {
            subSetPos = subSet.size();
        }
    }

    /**
     * Positions the rowset on the row, corresponding to the first subset's
     * position.
     *
     * @return Whether rowset have been repositioned.
     * @throws IllegalStateException
     * @throws com.bearsoft.rowset.exceptions.InvalidCursorPositionException
     */
    public boolean first() throws IllegalStateException, InvalidCursorPositionException {
        if (subSet != null && !subSet.isEmpty()) {
            if (rowset != null) {
                if (valid) {
                    subSetPos = 0;
                    RowWrap rw = subSet.get(subSetPos);
                    if (rw != null) {
                        boolean res = rowset.absolute(rw.getIndex());
                        assert !res || rowset.getCurrentRow() == rw.getRow();
                        return res;
                    }
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        }
        return false;
    }

    /**
     * Positions the rowset on the row, corresponding to the next subset's
     * position.
     *
     * @return Whether rowset have been repositioned.
     * @throws IllegalStateException
     * @throws com.bearsoft.rowset.exceptions.InvalidCursorPositionException
     */
    public boolean next() throws IllegalStateException, InvalidCursorPositionException {
        if (subSet != null && !subSet.isEmpty() && !isAfterLast()) {
            if (rowset != null) {
                if (valid) {
                    subSetPos++;
                    if (subSetPos >= 0 && subSetPos < subSet.size()) {
                        RowWrap rw = subSet.get(subSetPos);
                        if (rw != null) {
                            boolean res = rowset.absolute(rw.getIndex());
                            assert !res || rowset.getCurrentRow() == rw.getRow();
                            return res;
                        }
                    }
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        }
        return false;
    }

    /**
     * Positions the rowset on the row, corresponding to the first subset's
     * position.
     *
     * @param index Subset's position. index is 0-based.
     * @return Whether rowset have been repositioned.
     * @throws IllegalStateException
     * @throws com.bearsoft.rowset.exceptions.InvalidCursorPositionException
     */
    public boolean absolute(int index) throws IllegalStateException, InvalidCursorPositionException {
        if (subSet != null && !subSet.isEmpty() && !isAfterLast()) {
            if (rowset != null) {
                if (valid) {
                    if (index >= 0 && index < subSet.size()) {
                        subSetPos = index;
                        RowWrap rw = subSet.get(subSetPos);
                        if (rw != null) {
                            boolean res = rowset.absolute(rw.getIndex());
                            assert !res || rowset.getCurrentRow() == rw.getRow();
                            return res;
                        }
                    }
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        }
        return false;
    }

    /**
     * Positions the rowset on the row, corresponding to the previous subset's
     * position.
     *
     * @return Whether rowset have been repositioned.
     * @throws IllegalStateException
     * @throws com.bearsoft.rowset.exceptions.InvalidCursorPositionException
     */
    public boolean previous() throws IllegalStateException, InvalidCursorPositionException {
        if (subSet != null && !subSet.isEmpty() && !isBeforeFirst()) {
            if (rowset != null) {
                if (valid) {
                    subSetPos--;
                    if (subSetPos >= 0 && subSetPos < subSet.size()) {
                        RowWrap rw = subSet.get(subSetPos);
                        if (rw != null) {
                            boolean res = rowset.absolute(rw.getIndex());
                            assert !res || rowset.getCurrentRow() == rw.getRow();
                            return res;
                        }
                    }
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        }
        return false;
    }

    /**
     * Positions the rowset on the row, corresponding to the last subset's
     * position.
     *
     * @return Whether rowset have been repositioned.
     * @throws IllegalStateException
     * @throws com.bearsoft.rowset.exceptions.InvalidCursorPositionException
     */
    public boolean last() throws IllegalStateException, InvalidCursorPositionException {
        if (subSet != null && !subSet.isEmpty()) {
            if (rowset != null) {
                if (valid) {
                    subSetPos = subSet.size() - 1;
                    RowWrap rw = subSet.get(subSetPos);
                    if (rw != null) {
                        boolean res = rowset.absolute(rw.getIndex());
                        assert !res || rowset.getCurrentRow() == rw.getRow();
                        return res;
                    }
                } else {
                    throw new IllegalStateException(LOCATOR_IS_INVALID);
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        }
        return false;
    }

    /**
     * Returns whether subset's position is before first.
     *
     * @return Whether subset's position is before first.
     */
    public boolean isBeforeFirst() {
        return rowset == null || subSet == null || subSet.isEmpty() || subSetPos < 0;
    }

    /**
     * Returns whether subset's position is after last.
     *
     * @return Whether subset's position is after last.
     */
    public boolean isAfterLast() {
        return rowset == null || subSet == null || subSet.isEmpty() || subSetPos >= subSet.size();
    }

    /**
     * Changes row index in the ordered structure.
     *
     * @param aRow Row to change index of.
     * @param newIndex Value row index to be changed to.
     * @throws RowsetException
     */
    public void setRowOriginalIndex(Row aRow, int newIndex) throws RowsetException {
        KeySet ks = makeKeySet(aRow, fieldsIndicies);
        if (ks != null) {
            // delete from structure
            List<RowWrap> subset = ordered.get(ks);
            if (subset != null) {
                int rIndex = subset.indexOf(aRow);
                if (rIndex >= 0 && rIndex < subset.size()) {
                    RowWrap rw = subset.get(rIndex);
                    if (rw != null) {
                        rw.setIndex(newIndex);
                    }
                }
            }
        }
    }

    /**
     * Adds row to the ordered structure.
     *
     * @param aRow Row to add.
     * @param aIndex Index to be assigned to <code>aRow</code> in ordered
     * structure.
     * @throws RowsetException
     */
    public void add(Row aRow, int aIndex) throws RowsetException {
        KeySet ks = makeKeySet(aRow, fieldsIndicies);
        if (ks != null) {
            TaggedList<RowWrap> subset = ordered.get(ks);
            // add to structure
            if (subset == null) {
                subset = new TaggedList<>();
                ordered.put(ks, subset);
            }
            subset.add(new RowWrap(aRow, aIndex));
            keyInvalidators.addAll(signOn(aRow, fieldsIndicies, (PropertyChangeEvent evt) -> {
                invalidate();
            }));
        }
    }

    /**
     * Builds ordered structure of this locator.
     *
     * @throws RowsetException
     */
    @Override
    public void build() throws RowsetException {
        if (!constrainting) {
            if (rowset != null) {
                List<Row> rows = getRowsetRows();
                if (rows != null) {
                    ordered.clear();
                    for (int i = 0; i < rows.size(); i++) {
                        add(rows.get(i), i + 1);
                    }
                }
            } else {
                throw new IllegalStateException(ROWSET_MISSING);
            }
        } else {
            throw new IllegalStateException("can't build locator while constrainting");
        }
    }

    /**
     * Sorts rows into partitioned subsets.
     *
     * @param aRowWrapsComparator Comparator used while sorting.
     */
    public void sort(Comparator<RowWrap> aRowWrapsComparator) {
        if (ordered != null) {
            for (List<RowWrap> subSet2Sort : ordered.values()) {
                if (subSet2Sort != null) {
                    Collections.sort(subSet2Sort, aRowWrapsComparator);
                }
            }
        }
    }

    /**
     * Returns vector of rows gathered from all subsets in ordered structure.
     *
     * @return Vector of rows gathered from all subsets in ordered structure.
     */
    public List<Row> getAllRowsVector() {
        List<Row> rows = new ArrayList<>();
        for (List<RowWrap> lsubSet : ordered.values()) {
            assert lsubSet != null;
            for (RowWrap rw : lsubSet) {
                assert rw != null;
                rows.add(rw.getRow());
            }
        }
        return rows;
    }

    public TaggedList<RowWrap> getSubSet() {
        return subSet;
    }

    /**
     * Marks this locator as invalid.
     */
    @Override
    public void invalidate() {
        keyInvalidators.stream().forEach((Runnable unsign) -> {
            unsign.run();
        });
        keyInvalidators.clear();
        super.invalidate();
        ordered.clear();
        subSet = null;
    }

    /**
     * Uninitializes the locator, helping GC do it's work.
     */
    @Override
    public void die() {
        keyInvalidators.stream().forEach((Runnable unsign) -> {
            unsign.run();
        });
        keyInvalidators.clear();
        rowset.removeRowsetListener(invalidator);
        super.die();
        subSet = null;
        valid = false;
    }
}
