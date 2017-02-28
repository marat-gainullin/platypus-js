/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.columns;

import com.bearsoft.gui.grid.events.insets.InsetAfterLastChangedEvent;
import com.bearsoft.gui.grid.events.insets.InsetChangeListener;
import com.bearsoft.gui.grid.events.insets.InsetPreFirstChangedEvent;
import com.bearsoft.gui.grid.insets.LinearInset;
import com.bearsoft.gui.grid.insets.InsetPart;
import com.bearsoft.gui.grid.insets.InsetPart.PartKind;
import com.bearsoft.gui.grid.exceptions.GridException;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

/**
 * This column model implementation intended to create insetted column space around
 * delegate column model. It raises events from itself and from delegate model as if they
 * appeared straight in this model.
 * The feature is there are extra columns on the left and on the right.
 * As a consequence this model has no any information about how to fill inset columns.
 * This problem is solved with pluggable <code>InsettedColumnsFactory</code> interface.
 * @see InsettedColumnsFactory
 * @author Gala
 */
public class InsettedColumnModel implements TableColumnModel, ListSelectionListener {

    public static final String LEFT_INSET_WRONG_COLUMNS_MSG = "Columns count in the left inset is wrong. Inset value is %d and actual columns count is %d";
    public static final String RIGHT_INSET_WRONG_COLUMNS_MSG = "Columns count in the right inset is wrong. Inset value is %d and actual columns count is %d";
    // insetted attributes
    protected LinearInset inset;
    protected TableColumnModel delegate;
    protected List<TableColumn> leftInsetColumns = new ArrayList<>();
    protected List<TableColumn> rightInsetColumns = new ArrayList<>();
    protected InsettedColumnsFactory columnsFactory = new DefaultInsettedColumnsFactory();
    // colums standard attributes
    protected int columnMargin = 0;
    protected boolean columnSelectionAllowed = true;
    protected ListSelectionModel selectionModel;
    protected Set<TableColumnModelListener> listeners = new HashSet<>();

    protected class InsetListener implements InsetChangeListener {

        @Override
        public void insetPreFirstChanged(InsetPreFirstChangedEvent anEvent) {
            if (anEvent.getNewValue() > anEvent.getOldValue()) {
                assert leftInsetColumns.size() == anEvent.getOldValue();
                generateLeftInsetColumns(anEvent.getOldValue(), anEvent.getNewValue());
                /*
                for(int i=anEvent.getOldValue();i<anEvent.getNewValue();i++)
                {
                TableColumn col = columnsFactory.createLeft(inset.getPreFirst(), 0);
                leftInsetColumns.add(0, col);
                fireColumnAdded(0);
                }
                 */
                assert leftInsetColumns.size() == anEvent.getNewValue();
            } else if (anEvent.getNewValue() < anEvent.getOldValue()) {
                assert leftInsetColumns.size() == anEvent.getOldValue();
                while (leftInsetColumns.size() > anEvent.getNewValue()) {
                    leftInsetColumns.remove(0);
                    fireColumnRemoved(0);
                }
                assert leftInsetColumns.size() == anEvent.getNewValue();
            }
        }

        @Override
        public void insetAfterLastChanged(InsetAfterLastChangedEvent anEvent) {
            if (anEvent.getNewValue() > anEvent.getOldValue()) {
                assert rightInsetColumns.size() == anEvent.getOldValue();
                generateRightInsetColumns(anEvent.getOldValue(), anEvent.getNewValue());
                /*
                for (int i = anEvent.getOldValue(); i < anEvent.getNewValue(); i++) {
                    TableColumn col = columnsFactory.createRight(inset.getAfterLast(), rightInsetColumns.size());
                    rightInsetColumns.add(col);
                    fireColumnAdded(getColumnCount() - 1);
                }
                 * 
                 */
                assert rightInsetColumns.size() == anEvent.getNewValue();
            } else if (anEvent.getNewValue() < anEvent.getOldValue()) {
                assert rightInsetColumns.size() == anEvent.getOldValue();
                while (rightInsetColumns.size() > anEvent.getNewValue()) {
                    int col2Remove = getColumnCount() - 1;
                    rightInsetColumns.remove(rightInsetColumns.size() - 1);
                    fireColumnRemoved(col2Remove);
                }
                assert rightInsetColumns.size() == anEvent.getNewValue();
            }
        }
    }

    protected class DelegatedColumnModelListener implements TableColumnModelListener {

        @Override
        public void columnAdded(TableColumnModelEvent e) {
            fireColumnAdded(inset.toOuterSpace(new InsetPart(PartKind.CONTENT, e.getToIndex()), LinearInset.EMPTY_CONTENT));
        }

        @Override
        public void columnRemoved(TableColumnModelEvent e) {
            fireColumnRemoved(inset.toOuterSpace(new InsetPart(PartKind.CONTENT, e.getFromIndex()), LinearInset.EMPTY_CONTENT));
        }

        @Override
        public void columnMoved(TableColumnModelEvent e) {
            fireColumnMoved(inset.toOuterSpace(new InsetPart(PartKind.CONTENT, e.getFromIndex()), LinearInset.EMPTY_CONTENT),
                    inset.toOuterSpace(new InsetPart(PartKind.CONTENT, e.getToIndex()), LinearInset.EMPTY_CONTENT));
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

        @Override
        public boolean hasMoreElements() {
            return columnIndex < getColumnCount();
        }

        @Override
        public TableColumn nextElement() {
            if (columnIndex >= 0 && columnIndex < leftInsetColumns.size()) {
                return leftInsetColumns.get(columnIndex++);
            } else if (columnIndex >= leftInsetColumns.size() && columnIndex < leftInsetColumns.size() + delegate.getColumnCount()) {
                return delegate.getColumn(columnIndex++ - leftInsetColumns.size());
            } else if (columnIndex >= leftInsetColumns.size() + delegate.getColumnCount() && columnIndex < getColumnCount()) {
                return rightInsetColumns.get(columnIndex++ - leftInsetColumns.size() - delegate.getColumnCount());
            } else {
                throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * Insetted column model constructor. It takes a <code>InsettedColumnsFactory</code> instance as a parameter.
     * @param aDelegate A column model all significant work is delegated to.
     * @param aInset Insetted column space definition.
     * @param aColumnsFactory A <code>InsettedColumnsFactory</code> instance to be used as insetted columns space filler.
     * @see TableColumnModel
     * @see LinearInset
     * @see InsettedColumnsFactory
     */
    public InsettedColumnModel(TableColumnModel aDelegate, LinearInset aInset, InsettedColumnsFactory aColumnsFactory) {
        super();
        delegate = aDelegate;
        inset = aInset;
        columnsFactory = aColumnsFactory;
        columnSelectionAllowed = delegate.getColumnSelectionAllowed();
        columnMargin = delegate.getColumnMargin();
        inset.addInsetChangeListener(new InsetListener());
        delegate.addColumnModelListener(new DelegatedColumnModelListener());
        generateLeftInsetColumns(0, inset.getPreFirst());
        generateRightInsetColumns(0, inset.getAfterLast());
    }

    /**
     * Insetted column model constructor.
     * @param aDelegate A column model all significant work is delegated to.
     * @param aInset Insetted column space definition.
     * @see TableColumnModel
     * @see LinearInset
     * @see InsettedColumnsFactory
     */
    public InsettedColumnModel(TableColumnModel aDelegate, LinearInset aInset) {
        this(aDelegate, aInset, new DefaultInsettedColumnsFactory());
    }

    protected void checkColumns() throws GridException {
        if (leftInsetColumns.size() != inset.getPreFirst()) {
            throw new GridException(String.format(LEFT_INSET_WRONG_COLUMNS_MSG, inset.getPreFirst(), leftInsetColumns.size()));
        }
        if (rightInsetColumns.size() != inset.getAfterLast()) {
            throw new GridException(String.format(RIGHT_INSET_WRONG_COLUMNS_MSG, inset.getAfterLast(), rightInsetColumns.size()));
        }
    }

    /**
     *
     * @param aMin Inclusive
     * @param aMax Exclusive
     */
    protected void generateLeftInsetColumns(int aMin, int aMax) {
        for (int i = aMin; i < aMax; i++) {
            TableColumn col = columnsFactory.createLeft(inset.getPreFirst(), 0);
            leftInsetColumns.add(0, col);
            fireColumnAdded(0);
        }
    }

    protected void generateRightInsetColumns(int aMin, int aMax) {
        for (int i = aMin; i < aMax; i++) {
            TableColumn col = columnsFactory.createRight(inset.getAfterLast(), rightInsetColumns.size());
            rightInsetColumns.add(col);
            fireColumnAdded(getColumnCount() - 1);
        }
    }

    protected int getLeftInsetSelectedColumnCount() {
        int selected = 0;
        for (int i = 0; i < leftInsetColumns.size(); i++) {
            if (selectionModel.isSelectedIndex(i)) {
                selected++;
            }
        }
        return selected;
    }

    protected int getContentSelectedColumnCount() {
        int selected = 0;
        for (int i = 0; i < delegate.getColumnCount(); i++) {
            if (selectionModel.isSelectedIndex(leftInsetColumns.size() + i)) {
                selected++;
            }
        }
        return selected;
    }

    protected int getRightInsetSelectedColumnCount() {
        int selected = 0;
        for (int i = 0; i < rightInsetColumns.size(); i++) {
            if (selectionModel.isSelectedIndex(leftInsetColumns.size() + delegate.getColumnCount() + i)) {
                selected++;
            }
        }
        return selected;
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
    public void removeColumn(TableColumn column) {
        delegate.removeColumn(column);
        // the event will raise by itself
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void moveColumn(int sourceIndex, int destIndex) {
        InsetPart sourcePart = inset.toInnerSpace(sourceIndex, delegate.getColumnCount());
        InsetPart destPart = inset.toInnerSpace(destIndex, delegate.getColumnCount());
        if (sourcePart.getKind() == destPart.getKind()) {
            if (sourcePart.getKind() == PartKind.BEFORE) {
                TableColumn destCol = leftInsetColumns.get(destIndex);
                TableColumn sourceCol = leftInsetColumns.get(sourceIndex);
                leftInsetColumns.set(destIndex, sourceCol);
                leftInsetColumns.set(sourceIndex, destCol);
                fireColumnMoved(sourceIndex, destIndex);
            } else if (sourcePart.getKind() == PartKind.AFTER) {
                TableColumn destCol = rightInsetColumns.get(destIndex);
                TableColumn sourceCol = rightInsetColumns.get(sourceIndex);
                rightInsetColumns.set(destIndex, sourceCol);
                rightInsetColumns.set(sourceIndex, destCol);
                fireColumnMoved(sourceIndex, destIndex);
            } else {
                delegate.moveColumn(sourcePart.getValue(), destPart.getValue());
                // the event will raise by itself
            }
        }
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
        return delegate.getColumnCount() + leftInsetColumns.size() + rightInsetColumns.size();
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
    public int getColumnIndex(Object aColumnId) {
        for (int i = 0; i < leftInsetColumns.size(); i++) {
            Object id = leftInsetColumns.get(i).getIdentifier();
            if (id.equals(aColumnId)) {
                return i;
            }
        }
        int delegateIndex = delegate.getColumnIndex(aColumnId);
        if (delegateIndex >= 0 && delegateIndex < delegate.getColumnCount()) {
            return delegateIndex + leftInsetColumns.size();
        }
        for (int i = 0; i < rightInsetColumns.size(); i++) {
            Object id = rightInsetColumns.get(i).getIdentifier();
            if (id.equals(aColumnId)) {
                return leftInsetColumns.size() + delegate.getColumnCount() + i;
            }
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableColumn getColumn(int aColumnIndex) {
        if (aColumnIndex >= 0 && aColumnIndex < leftInsetColumns.size()) {
            return leftInsetColumns.get(aColumnIndex);
        } else if (aColumnIndex >= leftInsetColumns.size() && aColumnIndex < leftInsetColumns.size() + delegate.getColumnCount()) {
            return delegate.getColumn(aColumnIndex - leftInsetColumns.size());
        } else // columnIndex >= leftInsetColumns.size()+delegate.getColumnCount() && columnIndex < getColumnCount()
        {
            return rightInsetColumns.get(aColumnIndex - leftInsetColumns.size() - delegate.getColumnCount());
        }
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
        for (int i = 0; i < leftInsetColumns.size(); i++) {
            width += leftInsetColumns.get(i).getWidth();
        }
        width += delegate.getTotalColumnWidth();
        for (int i = 0; i < rightInsetColumns.size(); i++) {
            width += rightInsetColumns.get(i).getWidth();
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
        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < getColumnCount(); i++) {
            if (selectionModel.isSelectedIndex(i)) {
                selected.add(i);
            }
        }
        int[] sArray = new int[selected.size()];
        for (int j = 0; j < sArray.length; j++) {
            sArray[j] = selected.get(j);
        }
        return sArray;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSelectedColumnCount() {
        if (delegate.getSelectionModel() != null) {
            return getLeftInsetSelectedColumnCount() + delegate.getSelectedColumnCount() + getRightInsetSelectedColumnCount();
        } else {
            return getLeftInsetSelectedColumnCount() + getContentSelectedColumnCount() + getRightInsetSelectedColumnCount();
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

    protected void fireColumnSelectionChanged(ListSelectionEvent e)
    {
        listeners.forEach((l) -> {
            l.columnSelectionChanged(e);
        });
    }

    protected void fireColumnMarginChanged(int aMargin) {
        ChangeEvent event = new ChangeEvent(this);
        listeners.forEach((l) -> {
            l.columnMarginChanged(event);
        });
    }

    protected void fireColumnRemoved(int aPosition) {
        TableColumnModelEvent event = new TableColumnModelEvent(this, aPosition, 0);
        listeners.forEach((l) -> {
            l.columnRemoved(event);
        });
    }

    protected void fireColumnAdded(int aPosition) {
        TableColumnModelEvent event = new TableColumnModelEvent(this, 0, aPosition);
        listeners.forEach((l) -> {
            l.columnAdded(event);
        });
    }

    protected void fireColumnMoved(int sourceIndex, int destIndex) {
        TableColumnModelEvent event = new TableColumnModelEvent(this, sourceIndex, destIndex);
        listeners.forEach((l) -> {
            l.columnMoved(event);
        });
    }
}
