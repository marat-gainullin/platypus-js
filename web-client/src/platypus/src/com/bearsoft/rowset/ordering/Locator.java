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
    public void rowsetRolledback(RowsetRollbackEvent event) {
        invalidate();
    }

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

    protected void invalidate() {
        indices = null;
    }

    protected void validate() {
        if (indices == null) {
            indices = new HashMap<>();
            for (int i = 0; i < rowset.getCurrent().size(); i++) {
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
}
