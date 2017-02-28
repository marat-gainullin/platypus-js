/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.columns;

import com.bearsoft.gui.grid.constraints.LinearConstraint;
import com.bearsoft.gui.grid.events.constraints.ConstraintChangeListener;
import com.bearsoft.gui.grid.events.constraints.ConstraintMaximumChangedEvent;
import com.bearsoft.gui.grid.events.constraints.ConstraintMinimumChangedEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * This column model implementation intended to constraint delegate columns
 * space. It raises events from itself and from delegate model as if they
 * appeared straight in this model.
 *
 * @author Gala
 */
public class ConstrainedColumnModel implements TableColumnModel, ListSelectionListener {

    protected TableColumnModel delegate;
    protected int columnMargin;
    protected boolean columnSelectionAllowed;
    protected LinearConstraint constraint;
    protected ListSelectionModel selectionModel;
    protected Set<TableColumnModelListener> listeners = new HashSet<>();

    protected class ConstraintListener implements ConstraintChangeListener {

        @Override
        public void constraintMinimumChanged(ConstraintMinimumChangedEvent anEvent) {
            if (anEvent.getOldValue() != anEvent.getNewValue()) {
                for (int colIndex = 0; colIndex < Math.abs(anEvent.getNewValue() - anEvent.getOldValue()); colIndex++) {
                    if (anEvent.getOldValue() < anEvent.getNewValue()) {
                        fireColumnRemoved(0);
                    } else {
                        assert anEvent.getNewValue() < anEvent.getOldValue();
                        fireColumnAdded(0);
                    }
                }
            }
        }

        @Override
        public void constraintMaximumChanged(ConstraintMaximumChangedEvent anEvent) {
            if (anEvent.getOldValue() != anEvent.getNewValue()) {
                if (anEvent.getOldValue() < anEvent.getNewValue()) {
                    for (int colIndex = anEvent.getOldValue() + 1; colIndex <= anEvent.getNewValue(); colIndex++) {
                        fireColumnAdded(colIndex);
                    }
                } else {
                    assert anEvent.getNewValue() < anEvent.getOldValue();
                    for (int colIndex = anEvent.getOldValue(); colIndex > anEvent.getNewValue(); colIndex--) {
                        fireColumnRemoved(colIndex);
                    }
                }
            }
        }
    }

    protected class DelegatedColumnModelListener implements TableColumnModelListener {

        @Override
        public void columnAdded(TableColumnModelEvent e) {
            if (constraint.inConstraint(e.getToIndex())) {
                fireColumnAdded(constraint.constraint(e.getToIndex()));
            }
        }

        @Override
        public void columnRemoved(TableColumnModelEvent e) {
            if (constraint.inConstraint(e.getFromIndex())) {
                fireColumnRemoved(constraint.constraint(e.getFromIndex()));
            }
        }

        @Override
        public void columnMoved(TableColumnModelEvent e) {
            if (constraint.inConstraint(e.getFromIndex()) && constraint.inConstraint(e.getToIndex())) {
                fireColumnMoved(constraint.constraint(e.getFromIndex()), constraint.constraint(e.getToIndex()));
            } else {
                if (constraint.inConstraint(e.getFromIndex())) {
                    fireColumnRemoved(constraint.constraint(e.getFromIndex()));
                }
                if (constraint.inConstraint(e.getToIndex())) {
                    fireColumnAdded(constraint.constraint(e.getToIndex()));
                }
            }
        }

        @Override
        public void columnMarginChanged(ChangeEvent e) {
            Object oSource = e.getSource();
            assert oSource == delegate;
            int oldMargin = columnMargin;
            columnMargin = delegate.getColumnMargin();
            // Swing fires columnMarginChanged event when any column's width changes
            // this fact has no any reason. It seems that it's simply bad work of swing's programmers.
            // So, we have no any outgo, but unconditionally fire this event too.
            //if (oldMargin != columnMargin) {
            fireColumnMarginChanged(columnMargin);
            //}
        }

        @Override
        public void columnSelectionChanged(ListSelectionEvent e) {
            fireSelectionModelChanged();
        }
    }

    protected class ColumnsEnumerator implements Enumeration<TableColumn> {

        protected int columnIndex = 0;

        public ColumnsEnumerator() {
            super();
        }

        @Override
        public boolean hasMoreElements() {
            return columnIndex < getColumnCount();
        }

        @Override
        public TableColumn nextElement() {
            return getColumn(columnIndex++);
        }
    }

    /**
     * ConstrainedColumnModel constructor.
     *
     * @param aDelegate A <code>TableColumnModel</code> instance all significant
     * work is delegated to.
     * @param aConstraint A <code>LinearConstraint</code> instacne defining the
     * constraint.
     * @see TableColumnModel
     */
    public ConstrainedColumnModel(TableColumnModel aDelegate, LinearConstraint aConstraint) {
        super();
        delegate = aDelegate;
        constraint = aConstraint;
        columnSelectionAllowed = delegate.getColumnSelectionAllowed();
        columnMargin = delegate.getColumnMargin();
        delegate.addColumnModelListener(new DelegatedColumnModelListener());
        constraint.addConstraintChangeListener(new ConstraintListener());
    }

    public TableColumnModel getDelegate() {
        return delegate;
    }

    public LinearConstraint getConstraint() {
        return constraint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addColumn(TableColumn aColumn) {
        delegate.addColumn(aColumn);
        // the event will raise by itself
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeColumn(TableColumn aColumn) {
        delegate.removeColumn(aColumn);
        // the event will raise by itself
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void moveColumn(int oldIndex, int newIndex) {
        int colCount = getColumnCount();
        if (oldIndex >= 0 && oldIndex < colCount
                && newIndex >= 0 && newIndex < colCount) {
            int uncOldIndex = constraint.unconstraint(oldIndex);
            int uncNewIndex = constraint.unconstraint(newIndex);
            delegate.moveColumn(uncOldIndex, uncNewIndex);
            // the event will raise by itself
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<TableColumn> getColumns() {
        return new ColumnsEnumerator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColumnMargin(int aMargin) {
        columnMargin = aMargin;
        delegate.setColumnMargin(aMargin);
        // the event will raise by itself
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnCount() {
        int delegatedMaxColumn = delegate.getColumnCount() - 1;
        int ajMax = Math.min(delegatedMaxColumn, constraint.getMax());
        int ajMin = Math.max(0, constraint.getMin());
        return ajMax - ajMin + 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnIndex(Object columnId) {
        int delegatedColIndex = delegate.getColumnIndex(columnId);
        if (constraint.inConstraint(delegatedColIndex)) {
            return constraint.constraint(delegatedColIndex);
        } else {
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableColumn getColumn(int columnIndex) {
        return delegate.getColumn(constraint.unconstraint(columnIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnMargin() {
        return columnMargin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnIndexAtX(int xPosition) {
        int width = 0;
        for (int i = 0; i < getColumnCount(); i++) {
            TableColumn col = getColumn(i);
            if (xPosition >= width && xPosition < width + col.getWidth()) {
                return i;
            }
            width += col.getWidth();
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalColumnWidth() {
        int width = 0;
        for (int i = 0; i < getColumnCount(); i++) {
            TableColumn col = getColumn(i);
            width += col.getWidth();
        }
        return width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColumnSelectionAllowed(boolean aAllowed) {
        columnSelectionAllowed = aAllowed;
        delegate.setColumnSelectionAllowed(aAllowed);
        // the event will raise by itself
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getColumnSelectionAllowed() {
        return columnSelectionAllowed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getSelectedColumns() {
        int[] selectedInDelegate = delegate.getSelectedColumns();
        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < selectedInDelegate.length; i++) {
            if (selectedInDelegate[i] >= constraint.getMin()
                    && selectedInDelegate[i] <= constraint.getMax()) {
                selected.add(selectedInDelegate[i] - constraint.getMin());
            }
        }
        int[] arrSelected = new int[selected.size()];
        for (int i = 0; i < arrSelected.length; i++) {
            arrSelected[i] = selected.get(i);
        }
        return arrSelected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSelectedColumnCount() {
        int[] selectedColumnsIndexes = getSelectedColumns();
        if (selectedColumnsIndexes != null) {
            return selectedColumnsIndexes.length;
        } else {
            return 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelectionModel(ListSelectionModel newModel) {
        if (selectionModel != null) {
            selectionModel.removeListSelectionListener(this);
        }
        selectionModel = newModel;
        selectionModel.addListSelectionListener(this);
        fireSelectionModelChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListSelectionModel getSelectionModel() {
        return selectionModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addColumnModelListener(TableColumnModelListener l) {
        listeners.add(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeColumnModelListener(TableColumnModelListener l) {
        listeners.remove(l);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        fireColumnSelectionChanged(e);
    }

    protected void fireSelectionModelChanged() {
        ListSelectionEvent selEvent = new ListSelectionEvent(this, 0, getColumnCount() - 1, false);
        fireColumnSelectionChanged(selEvent);
    }

    protected void fireColumnSelectionChanged(ListSelectionEvent e) {
        for(TableColumnModelListener l : listeners.toArray(new TableColumnModelListener[]{})){
            l.columnSelectionChanged(e);
        }
    }

    public void fireColumnMarginChanged(int aMargin) {
        ChangeEvent event = new ChangeEvent(this);
        for(TableColumnModelListener l : listeners.toArray(new TableColumnModelListener[]{})){
            l.columnMarginChanged(event);
        }
    }

    protected void fireColumnRemoved(int aPosition) {
        TableColumnModelEvent event = new TableColumnModelEvent(this, aPosition, 0);
        for(TableColumnModelListener l : listeners.toArray(new TableColumnModelListener[]{})){
            l.columnRemoved(event);
        }
    }

    protected void fireColumnAdded(int aPosition) {
        TableColumnModelEvent event = new TableColumnModelEvent(this, 0, aPosition);
        for(TableColumnModelListener l : listeners.toArray(new TableColumnModelListener[]{})){
            l.columnAdded(event);
        }
    }

    protected void fireColumnMoved(int sourceIndex, int destIndex) {
        TableColumnModelEvent event = new TableColumnModelEvent(this, sourceIndex, destIndex);
        for(TableColumnModelListener l : listeners.toArray(new TableColumnModelListener[]{})){
            l.columnMoved(event);
        }
    }
}
