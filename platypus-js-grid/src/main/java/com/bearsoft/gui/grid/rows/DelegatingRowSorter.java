/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.rows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.TableModel;

/**
 *
 * @author mg
 */
public abstract class DelegatingRowSorter<M extends TableModel> extends RowSorter<M> {

    protected static final String UNSUPPORTED_METHOD_MSG = "Method is unsupported while delegate row sorter is absent. A method should be called on delegate, so it can't work without it.";
    protected M model;
    protected RowSorter<M> delegate;
    protected List<SortKey> emptySortKeysList = new ArrayList<>();

    protected DelegatingRowSorter(RowSorter<M> aDelegate, M aModel) {
        super();
        delegate = aDelegate;
        model = aModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public M getModel() {
        if (delegate != null) {
            return delegate.getModel();
        } else {
            return model;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getModelRowCount() {
        if (delegate != null) {
            return delegate.getModelRowCount();
        } else {
            return model.getRowCount();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modelStructureChanged() {
        if (delegate != null) {
            delegate.modelStructureChanged();
        } else {
            fireRowSorterChanged(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void allRowsChanged() {
        if (delegate != null) {
            delegate.allRowsChanged();
        } else {
            fireRowSorterChanged(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rowsInserted(int firstRow, int endRow) {
        if (delegate != null) {
            delegate.rowsInserted(firstRow, endRow);
        } else {
            fireRowSorterChanged(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rowsDeleted(int firstRow, int endRow) {
        if (delegate != null) {
            delegate.rowsDeleted(firstRow, endRow);
        } else {
            fireRowSorterChanged(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rowsUpdated(int firstRow, int endRow) {
        if (delegate != null) {
            delegate.rowsUpdated(firstRow, endRow);
        } else {
            fireRowSorterChanged(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rowsUpdated(int firstRow, int endRow, int column) {
        if (delegate != null) {
            delegate.rowsUpdated(firstRow, endRow, column);
        } else {
            fireRowSorterChanged(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSortKeys(List<? extends SortKey> keys) {
        if (delegate != null) {
            delegate.setSortKeys(keys);
        } else {
            throw new RuntimeException(UNSUPPORTED_METHOD_MSG);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends SortKey> getSortKeys() {
        if (delegate != null) {
            return delegate.getSortKeys();
        } else {
            return Collections.unmodifiableList(emptySortKeysList);//throw new RuntimeException(UNSUPPORTED_METHOD_MSG);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toggleSortOrder(int aColIndex) {
        if (delegate != null) {
            delegate.toggleSortOrder(aColIndex);
        } else {
            //throw new RuntimeException(UNSUPPORTED_METHOD_MSG); // Unfortunately Swing calls this method allways
        }
    }
}
