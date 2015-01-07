/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.ordering;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.RowsetContainer;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetEventsEarlyAccess;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetNextPageEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetSortEvent;
import java.util.HashMap;
import java.util.Map;

public class Locator extends RowsetAdapter implements RowsetEventsEarlyAccess, RowsetContainer {

    @Override
    public void rowDeleted(RowsetDeleteEvent event) {
        invalidate();
    }

    @Override
    public void rowInserted(RowsetInsertEvent event) {
        invalidate();
    }

    @Override
    public void rowsetSorted(RowsetSortEvent event) {
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

    /*
    public static final String LOCATOR_IS_INVALID = "locator is invalid! Rowset was edited but locator havn\'t been rebuild.";
    public static final String INDEX_IS_INVALID = "index is out of bounds";
    public static final String WRONG_POSITION_MARKER = "invalid position in locator's subset";
    protected int subSetPos = -1;
    */
    protected Rowset rowset;
    protected Map<Row, Integer> indices;

    public Locator() {
        super();
    }

    @Override
    public Rowset getRowset() {
        return rowset;
    }

    public void setRowset(Rowset aValue) {
        if (rowset != aValue) {
            if (rowset != null) {
                indices.clear();
                rowset.removeRowsetListener(this);
            }
            rowset = aValue;
            invalidate();
            if (rowset != null) {
                rowset.addRowsetListener(this);
            }
        }
    }

    protected void invalidate(){
        indices = null;
    }
    
    protected void validate(){
        if(indices == null){
            indices = new HashMap<>();
            for(int i=0; i < rowset.getCurrent().size(); i++){
                indices.put(rowset.getCurrent().get(i), i);
            }
        }
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

    public boolean isValid() {
        return indices != null;
    }

    /**
     * This function changes row position within same locator's subset of rows
     *
     * @param aRow A Row, that position is to be changed.
     * @param aIndex Index, the row would be at.
     * @return A <code>Row</code> object.
     * @throws ArrayIndexOutOfBoundsException public Row changeRowPosition(Row
     * aRow, int aIndex) throws ArrayIndexOutOfBoundsException { if (subSet !=
     * null && !subSet.isEmpty()) { if (rowset != null) { if (valid) { int
     * lIndex = -1; RowWrap rw = null; for (int i = 0; i < subSet.size(); i++) {
     * if (subSet.get(i).getRow() == aRow) { lIndex = i; rw = subSet.get(i);
     * break; } } if (rw != null) { if (aIndex != lIndex) {
     * subSet.remove(lIndex); subSet.add(aIndex, rw); } return rw.getRow(); } }
     * else { throw new IllegalStateException(LOCATOR_IS_INVALID); } } else {
     * throw new IllegalStateException(ROWSET_MISSING); } } return null; }
     */

    /**
     * Returns row position in rowset. Row is the subset's element at
     * <code>aSubsetPos</code>.
     *
     * @param aSubsetPos Subset's element position. It's 1-based.
     * @return Row position in rowset.
     * @throws IllegalStateException
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
     */

    /**
     * Changes row index in the ordered structure.
     *
     * @param aRow Row to change index of.
     * @param newIndex Value row index to be changed to.
     * @throws RowsetException
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
     */

    /**
     * Adds row to the ordered structure.
     *
     * @param aRow Row to add.
     * @param aIndex Index to be assigned to <code>aRow</code> in ordered
     * structure.
     * @throws RowsetException
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
     */

    /**
     * Sorts rows into partitioned subsets.
     *
     * @param aRowsComparator Comparator used while sorting.
    public void sort(Comparator<Row> aRowsComparator) {
        if (ordered != null) {
            for (List<RowWrap> subSet2Sort : ordered.values()) {
                if (subSet2Sort != null) {
                    Collections.sort(subSet2Sort, aRowWrapsComparator);
                }
            }
        }
    }
     */
}
