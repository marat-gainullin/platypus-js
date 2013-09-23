/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.models;

import com.bearsoft.gui.grid.data.TreedModel;
import com.bearsoft.gui.grid.events.data.ElementsAddedEvent;
import com.bearsoft.gui.grid.events.data.ElementsDataChangedEvent;
import com.bearsoft.gui.grid.events.data.ElementsRemovedEvent;
import com.bearsoft.gui.grid.events.data.TreedModelListener;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.dbcontrols.grid.rt.columns.model.ModelColumn;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author Gala
 */
public class RowsetsTreedModel extends RowsetsModel implements TreedModel<Row> {

    protected Set<TreedModelListener<Row>> listeners = new HashSet<>();
    // locator by primary key field is in the super model
    // locator by parent field
    protected Locator parentLocator;
    protected int parentFieldIndex;
    protected TreedRowsRowsetListener rowsRowsetListener;

    public RowsetsTreedModel(ApplicationEntity<?, ?, ?> aRowsEntity, Rowset aRowsRowset, int aParentFieldIndex, Scriptable aScriptScope, Function aCellsHandler) {
        super(aRowsEntity, aRowsRowset, aScriptScope, aCellsHandler);
        parentFieldIndex = aParentFieldIndex;
        parentLocator = rowsRowset.createParentLocator(parentFieldIndex, pkLocator);
        parentLocator.beginConstrainting();
        try {
            parentLocator.addConstraint(parentFieldIndex);
        } finally {
            parentLocator.endConstrainting();
        }
        rowsRowsetListener = new TreedRowsRowsetListener(this);
        rowsRowset.addRowsetListener(rowsRowsetListener);
    }

    public int getParentFieldIndex() {
        return parentFieldIndex;
    }

    @Override
    public Row getParentOf(Row anElement) {
        try {
            Row rSubject = (Row) anElement;
            if (pkLocator.find(rSubject.getColumnObject(parentFieldIndex))) {
                Row rParent = pkLocator.getRow(0);
                if (rParent != rSubject) {
                    return rParent;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (RowsetException ex) {
            Logger.getLogger(RowsetsTreedModel.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public List<Row> getChildrenOf(Row anElement) {
        try {
            Row rSubject = (Row) anElement;
            Object rowKey = null;
            if (rSubject != null) {
                // According to documentation, we have to identify rows by first
                // primary key occured
                for (int i = 1; i <= rSubject.getFields().getFieldsCount(); i++) {
                    if (rSubject.getFields().get(i).isPk()) {
                        rowKey = rSubject.getColumnObject(i);
                        break;
                    }
                }
            }
            if (parentLocator.find(new Object[]{rowKey})) {
                List<Row> children = new ArrayList<>();
                for (int i = 0; i < parentLocator.getSize(); i++) {
                    if (parentLocator.getRow(i) != anElement) {// one layer depth cycle reference avoiding
                        children.add(parentLocator.getRow(i));
                    }
                }
                return children;
            } else {
                return Collections.emptyList();
            }
        } catch (RowsetException | IllegalStateException ex) {
            Logger.getLogger(RowsetsTreedModel.class.getName()).log(Level.SEVERE, null, ex);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isLeaf(Row anElement) {
        try {
            Row rSubject = (Row) anElement;
            Object rowKey = null;
            if (rSubject != null) {
                // According to documentation, we have identify rows with first
                // primary key occured
                for (int i = 1; i <= rSubject.getFields().getFieldsCount(); i++) {
                    if (rSubject.getFields().get(i).isPk()) {
                        rowKey = rSubject.getColumnObject(i);
                        break;
                    }
                }
            }
            return !parentLocator.find(new Object[]{rowKey});
        } catch (Exception ex) {
            Logger.getLogger(RowsetsTreedModel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public void addTreedModelListener(TreedModelListener<Row> aListener) {
        listeners.add(aListener);
    }

    @Override
    public void removeTreedModelListener(TreedModelListener<Row> aListener) {
        listeners.remove(aListener);
    }

    public void fireElementsAdded(List<Row> aRemoved) {
        ElementsAddedEvent<Row> ev = new ElementsAddedEvent<>(aRemoved);
        for (TreedModelListener<Row> l : listeners) {
            l.elementsAdded(ev);
        }
    }

    public void fireElementsRemoved(List<Row> aRemoved) {
        ElementsRemovedEvent<Row> ev = new ElementsRemovedEvent<>(aRemoved);
        for (TreedModelListener<Row> l : listeners) {
            l.elementsRemoved(ev);
        }
    }

    @Override
    public void fireColumnRowsetChanged(ModelColumn aColumn) {
        ElementsDataChangedEvent<Row> ev = new ElementsDataChangedEvent<>(null, getModelColumnIndex(aColumn));
        for (TreedModelListener<Row> l : listeners) {
            l.elementsDataChanged(ev);
        }
    }

    @Override
    public void fireRowsDataChanged() {
        ElementsDataChangedEvent<Row> ev = new ElementsDataChangedEvent<>();
        for (TreedModelListener<Row> l : listeners) {
            l.elementsDataChanged(ev);
        }
    }

    public void fireRowsRowsetRowsChanged(Row aRow, int aRowsRowsetFieldIndex, boolean aAjusting) {
        Fields rFields = aRow.getFields();
        boolean inPrimaryKeys = rFields.getPrimaryKeys().contains(rFields.get(aRowsRowsetFieldIndex));
        boolean inParentKeys = aRowsRowsetFieldIndex == parentFieldIndex;
        if (inPrimaryKeys || inParentKeys) {
            parentLocator.invalidate();
            for (TreedModelListener<Row> l : listeners) {
                l.elementsStructureChanged();
            }
        } else {
            ElementsDataChangedEvent<Row> ev = new ElementsDataChangedEvent<>(Collections.singletonList(aRow), getModelColumnIndex(aRowsRowsetFieldIndex), aAjusting);
            for (TreedModelListener<Row> l : listeners) {
                l.elementsDataChanged(ev);
            }
        }
    }
}