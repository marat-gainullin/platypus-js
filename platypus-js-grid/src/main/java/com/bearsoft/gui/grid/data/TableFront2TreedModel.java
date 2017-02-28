/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.data;

import com.bearsoft.gui.grid.events.data.ElementsAddedEvent;
import com.bearsoft.gui.grid.events.data.ElementsDataChangedEvent;
import com.bearsoft.gui.grid.events.data.ElementsRemovedEvent;
import com.bearsoft.gui.grid.events.data.TreedModelListener;
import com.eas.util.ListenerRegistration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * This class in the table front to a treed data.
 *
 * @author mg
 * @param <T>
 */
public class TableFront2TreedModel<T> implements TableModel {

    protected Set<TableModelListener> listeners = new HashSet<>();
    protected Set<CollapseExpandListener<T>> collapseExpandListeners = new HashSet<>();
    protected TreedModel<T> treedModel;
    protected Set<T> expanded = new HashSet<>();
    protected List<T> front;
    protected Map<T, Integer> frontIndexes = new HashMap<>();

    protected class TreedDataListener implements TreedModelListener<T> {

        @Override
        public void elementsDataChanged(ElementsDataChangedEvent<T> anEvent) {
            if (anEvent.getElements() == null && anEvent.getColIndex() == -1) {
                fireAllDataChanged();
            } else if (!anEvent.getElements().isEmpty() && !anEvent.isAjusting()) {
                Map<Integer, Integer> indicies = convertElements2Indicies(anEvent.getElements());
                for (Integer rIndex : indicies.keySet()) {
                    fireRowChanged(anEvent.getColIndex(), rIndex);
                }
            }
        }

        @Override
        public void elementsAdded(ElementsAddedEvent<T> anEvent) {
            if (!anEvent.isAjusting()) {
                List<T> elementsInExpandedSpace = removeCollapsed(anEvent.getElements());
                if (!elementsInExpandedSpace.isEmpty()) {
                    invalidateFront();
                    Map<Integer, Integer> indicies = convertElements2Indicies(elementsInExpandedSpace);
                    generateIntervaleInserts(indicies);
                }
            }
        }

        @Override
        public void elementsRemoved(ElementsRemovedEvent<T> anEvent) {
            if (!anEvent.isAjusting()) {
                List<T> elementsInExpandedSpace = removeCollapsed(anEvent.getElements());
                if (!elementsInExpandedSpace.isEmpty()) {
                    Map<Integer, Integer> indicies = convertElements2Indicies(elementsInExpandedSpace);
                    invalidateFront();
                    generateIntervaledDeletes(indicies);
                }
            }
        }

        private Map<Integer, Integer> convertElements2Indicies(List<T> aElements) {
            TreeMap<Integer, Integer> indicies = new TreeMap<>();
            for (int i = 0; i < aElements.size(); i++) {
                Integer rIndex = getIndexOf(aElements.get(i));
                indicies.put(rIndex, rIndex);
            }
            return indicies;
        }

        private List<T> removeCollapsed(List<T> aOffer) {
            List<T> filtered = new ArrayList<>();
            for (T el : aOffer) {
                if (el != null) {
                    T elParent = treedModel.getParentOf(el);
                    if (elParent == null || expanded.contains(elParent)) {
                        filtered.add(el);
                    }
                    filtered.addAll(treedModel.getChildrenOf(el));
                }
            }
            return filtered;
        }

        private void generateIntervaleInserts(Map<Integer, Integer> aIndiciesChanged) {
            generateIntervaledEvents(aIndiciesChanged, true);
        }

        private void generateIntervaledDeletes(Map<Integer, Integer> aIndiciesChanged) {
            generateIntervaledEvents(aIndiciesChanged, false);
        }

        private void generateIntervaledEvents(Map<Integer, Integer> aIndiciesChanged, boolean isInserts) {
            // generate intervaled events
            Integer rOldIndex = null;
            Integer rIndex1 = null;
            Integer rIndex2 = null;
            for (Integer rIndex : aIndiciesChanged.keySet()) {
                if (rOldIndex == null || (rIndex - rOldIndex) > 1) {
                    if (rIndex1 != null) {
                        if (isInserts) {
                            fireRowsInserted(rIndex1, rIndex2);
                        } else {
                            fireRowsDeleted(rIndex1, rIndex2);
                        }
                    }
                    rIndex1 = rIndex;
                }
                rIndex2 = rIndex;
                rOldIndex = rIndex;
            }
            if (isInserts) {
                fireRowsInserted(rIndex1, rIndex2);
            } else {
                fireRowsDeleted(rIndex1, rIndex2);
            }
        }

        @Override
        public void elementsStructureChanged() {
            invalidateFront();
            fireAllDataChanged();
        }
    }

    /**
     * Table front constructor. Constructs a lazy tree front.
     *
     * @param aTreedModel - Deep treed model, containing data.
     */
    public TableFront2TreedModel(TreedModel<T> aTreedModel) {
        super();
        treedModel = aTreedModel;
        treedModel.addTreedModelListener(new TreedDataListener());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnCount() {
        return treedModel.getColumnCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName(int columnIndex) {
        return treedModel.getColumnName(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return treedModel.getColumnClass(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRowCount() {
        validateFront();
        return front.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        validateFront();
        T el = front.get(rowIndex);
        return treedModel.getValue(el, columnIndex);
    }

    public T getElementAt(int rowIndex) {
        validateFront();
        return (rowIndex >= 0 && rowIndex < front.size()) ? front.get(rowIndex) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        validateFront();
        T el = front.get(rowIndex);
        treedModel.setValue(el, columnIndex, aValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    protected void validateFront() {
        assert treedModel != null;
        if (front == null) {
            List<T> children = treedModel.getChildrenOf(null);
            front = new ArrayList<>();
            front.addAll(children);
            int i = 0;
            while (i < front.size()) {
                if (expanded.contains(front.get(i))) {
                    List<T> children1 = treedModel.getChildrenOf(front.get(i));
                    front.addAll(i + 1, children1);
                }
                ++i;
            }
            frontIndexes.clear();
            for (i = 0; i < front.size(); i++) {
                frontIndexes.put(front.get(i), i);
            }
        }
    }

    protected void invalidateFront() {
        front = null;
        frontIndexes.clear();
    }

    /**
     * Builds path to specified element if the element belongs to the model.
     *
     * @param anElement Element to build path to.
     * @return ArrayList&lt;T&gt; of elements comprising the path, excluding
     * root null element.
     */
    public List<T> buildPathTo(T anElement) {
        List<T> path = new ArrayList<>();
        if (anElement != null) {
            T currentParent = anElement;
            path.add(currentParent);
            while (currentParent != null) {
                currentParent = getParentOf(currentParent);
                if (currentParent != null) {
                    path.add(0, currentParent);
                }
            }
        }
        return path;
    }

    protected T getParentOf(T aElement) {
        assert treedModel != null;
        return treedModel.getParentOf(aElement);
    }

    /**
     * Converts element of the model to it's position in front.
     *
     * @param anElement Element of the model to return index of.
     * @return Index of model's element. It returns -1 if element is not in
     * expanded space, including case of an element doesn't belong to the model
     * at all.
     */
    public int getIndexOf(T anElement) {
        validateFront();
        Integer index = frontIndexes.get(anElement);
        return index != null ? index : -1;
    }

    protected void fireAllDataChanged() {
        TableModelEvent event = new TableModelEvent(this);
        for (TableModelListener l : listeners) {
            l.tableChanged(event);
        }
    }

    protected void fireRowsInserted(int aFirstRow, int aLastRow) {
        TableModelEvent event = new TableModelEvent(this, aFirstRow, aLastRow, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
        for (TableModelListener l : listeners) {
            l.tableChanged(event);
        }
    }

    protected void fireRowsDeleted(int aFirstRow, int aLastRow) {
        TableModelEvent event = new TableModelEvent(this, aFirstRow, aLastRow, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
        for (TableModelListener l : listeners) {
            l.tableChanged(event);
        }
    }

    protected void fireRowChanged(int aModelColumnIndex, int aRowIndex) {
        TableModelEvent event = new TableModelEvent(this, aRowIndex, aRowIndex, aModelColumnIndex, TableModelEvent.UPDATE);
        for (TableModelListener l : listeners) {
            l.tableChanged(event);
        }
    }

    protected void fireRowsChanged(int aModelColumnIndex, int aFirstRow, int aLastRow) {
        TableModelEvent event = new TableModelEvent(this, aFirstRow, aLastRow, aModelColumnIndex, TableModelEvent.UPDATE);
        for (TableModelListener l : listeners) {
            l.tableChanged(event);
        }
    }

    public ListenerRegistration addCollapseExpandListener(CollapseExpandListener l) {
        collapseExpandListeners.add(l);
        return () -> {
            collapseExpandListeners.remove(l);
        };
    }

    public void removeCollapseExpandListener(CollapseExpandListener l) {
        collapseExpandListeners.remove(l);
    }

    protected void fireCollapsed(T aItem) {
        for (CollapseExpandListener l : collapseExpandListeners.toArray(new CollapseExpandListener[]{})) {
            l.collapsed(aItem);
        }
    }

    protected void fireExpanded(T aItem) {
        for (CollapseExpandListener l : collapseExpandListeners.toArray(new CollapseExpandListener[]{})) {
            l.expanded(aItem);
        }
    }

    public boolean isExpanded(T anElement) {
        return expanded.contains(anElement);
    }

    public void expand(final T anElement, boolean aAsynchronous) {
        List<T> children = treedModel.getChildrenOf(anElement);
        if (!expanded.contains(anElement)) {
            if (!children.isEmpty()) {
                invalidateFront();
                expanded.add(anElement);
                int firstRow = getIndexOf(children.get(0));
                int lastRow = getIndexOf(children.get(children.size() - 1));
                fireRowsInserted(firstRow, lastRow);
            }
            fireExpanded(anElement);
        }
    }

    public void collapse(T anElement) {
        List<T> children = treedModel.getChildrenOf(anElement);
        if (expanded.contains(anElement)) {
            if (!children.isEmpty()) {
                int firstRow = getIndexOf(children.get(0));
                int lastRow = getIndexOf(children.get(children.size() - 1));
                invalidateFront();
                expanded.remove(anElement);
                fireRowsDeleted(firstRow, lastRow);
            }
            fireCollapsed(anElement);
        }
    }

    public TreedModel<T> unwrap() {
        return treedModel;
    }
}
