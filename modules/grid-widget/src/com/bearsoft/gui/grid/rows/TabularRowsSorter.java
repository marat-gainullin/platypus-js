/* 
 * To change this template, choose Tools | Templates 
 * and open the template in the editor. 
 */
package com.bearsoft.gui.grid.rows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;

/**
 *
 * @author mg
 * @param <M>
 */
public class TabularRowsSorter<M extends TableModel> extends RowSorter<M> {

    public boolean applySelection(ListSelectionModel aSelection) {
        boolean res = true;
        if (viewSelection != null) {
            viewSelection.clearSelection();
            for (int irow = aSelection.getMinSelectionIndex(); irow <= aSelection.getMaxSelectionIndex(); irow++) {
                if (aSelection.isSelectedIndex(irow)) {
                    if (irow >= 0 && irow < getModelRowCount()) {
                        int viewRow = convertRowIndexToView(irow);
                        if (viewRow >= 0 && viewRow < getViewRowCount()) {
                            viewSelection.addSelectionInterval(viewRow, viewRow);
                            if (!viewSelection.isSelectedIndex(viewRow)) {
                                res = false;
                            }
                        } else {
                            res = false;
                        }
                    } else {
                        res = false;
                    }
                }
            }
        }
        return res;
    }

    public ListSelectionModel saveSelection() {
        ListSelectionModel cacheSelection = new DefaultListSelectionModel();
        if (viewSelection != null) {
            for (int irow = viewSelection.getMinSelectionIndex(); irow <= viewSelection.getMaxSelectionIndex(); irow++) {
                if (viewSelection.isSelectedIndex(irow)) {
                    int modelRow = convertRowIndexToModel(irow);
                    cacheSelection.addSelectionInterval(modelRow, modelRow);
                }
            }
        }
        return cacheSelection;
    }

    protected class SorterRowComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            return compareRows(o1, o2);
        }
    }
    protected M model;
    protected List<? extends SortKey> criteria = new ArrayList<>();
    protected int modelRowCount = -1;
    protected int viewToModel[] = null;
    protected int modelToView[] = null;
    protected ListSelectionModel viewSelection;

    public TabularRowsSorter(M aModel, ListSelectionModel aViewSelection) {
        super();
        model = aModel;
        viewSelection = aViewSelection;
    }

    @Override
    public M getModel() {
        return model;
    }

    private boolean isColumnValid(int column) {
        return column >= 0 && column < model.getColumnCount();
    }

    private void checkColumn(int column) {
        if (!isColumnValid(column)) {
            throw new IndexOutOfBoundsException(
                    "column beyond range of TableModel");
        }
    }

    public boolean isSortingCriteria(int column) {
        checkColumn(column);
        for (int i = 0; i < criteria.size(); i++) {
            SortKey key = criteria.get(i);
            if (key.getColumn() == column) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void toggleSortOrder(int column) {
        checkColumn(column);
        List<SortKey> toMutate = new ArrayList<>(criteria);
        boolean found = false;
        for (int i = 0; i < toMutate.size(); i++) {
            SortKey key = toMutate.get(i);
            if (key.getColumn() == column) {
                SortOrder newSortOrder = null;
                if (key.getSortOrder() == SortOrder.ASCENDING) {
                    newSortOrder = SortOrder.DESCENDING;
                } else if (key.getSortOrder() == SortOrder.DESCENDING) {
                    newSortOrder = SortOrder.UNSORTED;
                } else if (key.getSortOrder() == SortOrder.UNSORTED) {
                    newSortOrder = SortOrder.ASCENDING;
                }
                assert newSortOrder != null;
                toMutate.set(i, new SortKey(key.getColumn(),
                        newSortOrder));
                found = true;
                break;
            }
        }
        if (!found) {
            toMutate.add(new SortKey(column, SortOrder.ASCENDING));
        }
        setSortKeys(toMutate);
    }

    @Override
    public void setSortKeys(List<? extends SortKey> keys) {
        criteria = keys;
        sort();
    }

    @Override
    public List<? extends SortKey> getSortKeys() {
        return Collections.unmodifiableList(criteria);
    }

    @Override
    public int convertRowIndexToModel(int index) {
        if (viewToModel != null && index >= 0 && index < viewToModel.length) {
            if (isSorted()) {
                return viewToModel[index];
            } else {
                return index;
            }
        } else {
            return index;
        }
    }

    @Override
    public int convertRowIndexToView(int index) {
        if (modelToView != null && index >= 0 && index < modelToView.length) {
            if (isSorted()) {
                return modelToView[index];
            } else {
                return index;
            }
        } else {
            return index;
        }
    }

    @Override
    public int getViewRowCount() {
        return model.getRowCount();
    }

    @Override
    public int getModelRowCount() {
        return model.getRowCount();
    }

    @Override
    public void modelStructureChanged() {
        if (isSorted()) {
            unsort();
        }
    }

    @Override
    public void allRowsChanged() {
        if (isSorted()) {
            sort();
        }
    }

    @Override
    public void rowsInserted(int firstRow, int endRow) {
        if (isSorted()) {
            sort();
        }
    }

    @Override
    public void rowsDeleted(int firstRow, int endRow) {
        if (isSorted()) {
            sort();
        }
    }

    @Override
    public void rowsUpdated(int firstRow, int endRow) {
        if (isSorted()) {
            sort();
        }
    }

    @Override
    public void rowsUpdated(int firstRow, int endRow, int column) {
        if (isSorted() && isSortingCriteria(column)) {
            sort();
        }
    }

    private boolean isSorted() {
        return modelToView != null;
    }

    public int compareRows(int row1, int row2) {
        int order = 0;
        for (int i = 0; i < criteria.size(); i++) {
            SortKey key = criteria.get(i);
            if (key.getSortOrder() != SortOrder.UNSORTED) {
                Object o1 = model.getValueAt(row1, key.getColumn());
                Object o2 = model.getValueAt(row2, key.getColumn());
                if (o1 instanceof Comparable<?> && o2 instanceof Comparable<?>) {
                    Comparable<Object> c1 = (Comparable<Object>) o1;
                    Comparable<Object> c2 = (Comparable<Object>) o2;
                    order = c1.compareTo(c2);
                } else if (o1 == null && o2 != null) {
                    order = 1;
                } else if (o1 != null && o2 == null) {
                    order = -1;
                }
                order = key.getSortOrder() == SortOrder.DESCENDING ? -order : order;
                if (order != 0) {
                    break;
                }
            }
        }
        return order;
    }

    private void sort() {
        ListSelectionModel savedSelection = saveSelection();
        try {
            modelRowCount = model.getRowCount();
            Integer[] modelRows = new Integer[modelRowCount];
            for (int i = 0; i < modelRows.length; i++) {
                modelRows[i] = i;
            }
            Arrays.sort(modelRows, new SorterRowComparator());
            viewToModel = new int[modelRows.length];
            modelToView = new int[modelRows.length];
            for (int i = 0; i < modelRows.length; i++) {
                viewToModel[i] = modelRows[i];
                modelToView[modelRows[i]] = i;
            }
            fireSortOrderChanged();
        } finally {
            applySelection(savedSelection);
        }
    }

    private void unsort() {
        ListSelectionModel savedSelection = saveSelection();
        try {
            modelRowCount = -1;
            criteria.clear();
            viewToModel = null;
            modelToView = null;
            fireSortOrderChanged();
        } finally {
            applySelection(savedSelection);
        }
    }
}
