/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.veers;

import com.bearsoft.gui.grid.constraints.LinearConstraint;
import com.bearsoft.gui.grid.header.GridColumnsGroup;
import com.bearsoft.gui.grid.header.MultiLevelHeader;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.events.RowChangeEvent;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetNextPageEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetSaveEvent;
import com.bearsoft.rowset.events.RowsetScrollEvent;
import com.bearsoft.rowset.events.RowsetSortEvent;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.dbcontrols.grid.rt.HasStyle;
import com.eas.dbcontrols.grid.rt.columns.ScriptableColumn;
import com.eas.dbcontrols.grid.rt.columns.model.ModelColumn;
import com.eas.dbcontrols.grid.rt.columns.model.RowModelColumn;
import com.eas.dbcontrols.grid.rt.columns.view.RowHeaderTableColumn;
import com.eas.dbcontrols.grid.rt.models.RowsetsModel;
import com.eas.gui.CascadedStyle;
import com.eas.script.NativeJavaHostObject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * Helper class, intended to manage table's columns in the view and model
 * perspectives. It consist of methods to remove and add columns. It takes into
 * account, that model colunms should be also added or removed. Model indexes of
 * view's columns are also corrected.
 *
 * @author Gala
 */
public class ColumnsRiddler extends RowsetAdapter implements HasStyle {

    public void die() {
        Set<TableColumn> viewcols = new HashSet<>();
        for (int i = view.getColumnCount() - 1; i >= 0; i--) {
            TableColumn viewCol = view.getColumn(i);
            viewcols.add(viewCol);
        }
        for (Rowset rowset : columnsSources.keySet()) {
            for (Row row : rowset.getCurrent()) {
                for (PropertyChangeListener l : row.getChangeSupport().getPropertyChangeListeners()) {
                    if (l instanceof Row2ColumnChangesReflector && viewcols.contains(((Row2ColumnChangesReflector) l).getTableColumn())) {
                        row.removePropertyChangeListener(l);
                    }
                }
            }
        }
        columnsSources = null;
        model = null;
        rows2Cols = null;
        view = null;
    }

    /**
     * @return the scriptableColumns
     */
    public List<ScriptableColumn> getScriptableColumns() {
        return scriptableColumns;
    }

    /**
     * @param aValue the scriptableColumns to set
     */
    public void setScriptableColumns(List<ScriptableColumn> aValue) {
        scriptableColumns = aValue;
    }

    protected class Row2ColumnChangesReflector implements PropertyChangeListener {

        protected TableColumn vCol;

        public Row2ColumnChangesReflector(TableColumn aVCol) {
            super();
            vCol = aVCol;
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if (vCol != null
                    && vCol.getIdentifier() instanceof RowModelColumn
                    && ((RowModelColumn) vCol.getIdentifier()).getRow() == event.getSource()
                    && event.getPropagationId() instanceof Integer
                    && ((RowModelColumn) vCol.getIdentifier()).getColTitleFieldIndex() == ((Integer) event.getPropagationId()).intValue()) {
                vCol.setHeaderValue(event.getNewValue());
            }
        }

        public TableColumn getTableColumn() {
            return vCol;
        }
    }
    private static String COLUMN_NAME = "Column";
    protected Map<TableColumn, GridColumnsGroup> leavesToGroups;
    protected TableColumnModel view;
    protected MultiLevelHeader leveledHeader;
    protected RowsetsModel model;
    protected Map<Rowset, List<ColumnsSource>> columnsSources;
    protected Map<Row, TableColumn> rows2Cols = new HashMap<>();
    protected LinearConstraint leftConstraint;
    protected LinearConstraint rightConstraint;
    protected HasStyle styleHost;
    private List<ScriptableColumn> scriptableColumns;
    private Map<TableColumn, ScriptableColumn> columnsStore = new HashMap<>();
    private int fixedColumns;
    private NativeJavaHostObject hostObject;

    public ColumnsRiddler(Map<TableColumn, GridColumnsGroup> aLeavesToGroups, TableColumnModel aView, MultiLevelHeader aHeader, RowsetsModel aModel, Map<Rowset, List<ColumnsSource>> aColumnsSources, HasStyle aStyleHost, List<ScriptableColumn> aScriptableColumns, Object aHostObject) {
        super();
        leavesToGroups = aLeavesToGroups;
        view = aView;
        leveledHeader = aHeader;
        fixedColumns = getFixedColumnsCount();
        model = aModel;
        columnsSources = aColumnsSources;
        styleHost = aStyleHost;
        scriptableColumns = aScriptableColumns;
        if (aHostObject != null && aHostObject instanceof NativeJavaHostObject) {
            hostObject = (NativeJavaHostObject) aHostObject;
        }
        installColumnsSourcesListeners();
    }

    @Override
    public CascadedStyle getStyle() {
        return styleHost != null ? styleHost.getStyle() : null;
    }

    private int getFixedColumnsCount() {
        int fixedCount = 0;
        for (int i = 0; i < view.getColumnCount(); i++) {
            if (view.getColumn(i) instanceof RowHeaderTableColumn) {
                fixedCount++;
            }
        }
        return fixedCount;
    }

    private void removeScriptableColumn(ScriptableColumn aColumn) {
        if (hostObject != null) {
            if (aColumn != null && aColumn.getName() != null && !aColumn.getName().isEmpty()) {
                hostObject.delete(aColumn.getName());
            }
        }
    }

    private void defineScriptableColumn(ScriptableColumn aColumn) {
        if (hostObject != null) {
            if (aColumn != null && aColumn.getName() != null && !aColumn.getName().isEmpty()) {
                Object jsColumn = Context.javaToJS(aColumn, hostObject);
                assert jsColumn instanceof Scriptable;
                aColumn.setPublished((Scriptable) jsColumn);
                hostObject.defineProperty(aColumn.getName(), jsColumn);
            }
        }
    }

    public LinearConstraint getLeftConstraint() {
        return leftConstraint;
    }

    public void setLeftConstraint(LinearConstraint aValue) {
        leftConstraint = aValue;
    }

    public LinearConstraint getRightConstraint() {
        return rightConstraint;
    }

    public void setRightConstraint(LinearConstraint aValue) {
        rightConstraint = aValue;
    }

    private void installColumnsSourcesListeners() {
        for (Rowset cSource : columnsSources.keySet()) {
            cSource.addRowsetListener(this);
        }
    }

    public void riddle(Rowset aRowset) {
        try {
            if (leveledHeader != null) {
                leveledHeader.getColumnModelListener().setEventsEnabled(false);
            }
            try {
                removeUnactual(aRowset);
                addAbsent(aRowset);
                reindexView();
            } finally {
                if (leveledHeader != null) {
                    leveledHeader.getColumnModelListener().setEventsEnabled(true);
                    leveledHeader.regenerate();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ColumnsRiddler.class.getName()).severe(ex.getLocalizedMessage());
        }
    }

    /**
     * Performs initial columns filling of the model and view with columns from
     * rowsets, according to columns sources.
     *
     * @throws Exception
     */
    public void fill() throws Exception {
        for (Rowset rowset : columnsSources.keySet()) {
            removeUnactual(rowset);
            addAbsent(rowset);
        }
        reindexView();
    }

    /**
     * Removes any columns, that present in model and view but absent in
     * underlying rowset.
     *
     * @param aRowset Rowset to deal with.
     * @throws Exception
     * @see Rowset
     */
    private void removeUnactual(Rowset aRowset) throws Exception {
        Set<RowModelColumn> toRemove = new HashSet<>();
        for (int i = view.getColumnCount() - 1; i >= 0; i--) {
            TableColumn viewCol = view.getColumn(i);
            Object oId = viewCol.getIdentifier();
            if (oId instanceof RowModelColumn) {
                RowModelColumn rmCol = (RowModelColumn) oId;
                if (rmCol.getRowset() == aRowset && !rmCol.isActual()) {
                    PropertyChangeListener[] listeners = rmCol.getRow().getChangeSupport().getPropertyChangeListeners();
                    for (PropertyChangeListener l : listeners) {
                        if (l instanceof Row2ColumnChangesReflector
                                && ((Row2ColumnChangesReflector) l).getTableColumn() == viewCol) {
                            rmCol.getRow().removePropertyChangeListener(l);
                        }
                    }
                    if (leavesToGroups != null) {
                        GridColumnsGroup leafGroup = leavesToGroups.get(viewCol);
                        if (leafGroup != null) {
                            assert leafGroup.getParent() != null;
                            leafGroup.getParent().removeChild(leafGroup);
                            leavesToGroups.remove(viewCol);
                        }
                    }
                    // remove from view
                    view.removeColumn(viewCol);
                    ScriptableColumn scriptableColumn = columnsStore.get(viewCol);
                    removeScriptableColumn(scriptableColumn);
                    scriptableColumns.remove(scriptableColumn);
                    columnsStore.remove(viewCol);
                    // mark for removing from model
                    toRemove.add(rmCol);
                }
            }
        }
        //  remove from model
        for (int i = model.getColumnCount() - 1; i >= 0; i--) {
            ModelColumn mCol = model.getColumn(i);
            if (mCol instanceof RowModelColumn && toRemove.contains((RowModelColumn) mCol)) {
                model.removeColumn(i);
            }
        }
        if (leftConstraint != null) {
            assert rightConstraint != null : "Left and right constraints must be setted commonly";
            leftConstraint.setMax(leftConstraint.getMax() - toRemove.size());
            rightConstraint.setMin(rightConstraint.getMin() - toRemove.size());
        }
    }

    /**
     * Adds columns both to view and model if there present in underlying
     * rowset, but absent in model and view.
     *
     * @param aRowset Rowset to deal with. This rowset must present in key set
     * of column sources map.
     * @see Rowset
     */
    private void addAbsent(Rowset aRowset) throws Exception {
        Set<Row> colsRows = new HashSet<>();
        for (int i = 0; i < model.getColumnCount(); i++) {
            ModelColumn mCol = model.getColumn(i);
            if (mCol instanceof RowModelColumn) {
                colsRows.add(((RowModelColumn) mCol).getRow());
            }
        }
        int added = 0;
        List<ColumnsSource> colSources = columnsSources.get(aRowset);
        for (ColumnsSource colSource : colSources) {
            int idxAfterGlue = 0;
            for (int i = 0; i < view.getColumnCount(); i++) {
                if (view.getColumn(i) == colSource.getPrefirstViewColumn()) {
                    idxAfterGlue = i + 1;
                    break;
                }
            }
            List<Row> rows = aRowset.getCurrent();
            for (int i = 0; i < rows.size(); i++) {
                if (!colsRows.contains(rows.get(i))) {
                    int localIndexToAddTo = i;
                    Row colRow = rows.get(i);
                    RowModelColumn rCol = new RowModelColumn(colSource.getColumnsLocator(), colRow, colSource.getColumnsTitleColIndex(), colSource.getCellsLocator(), colSource.getCellsValuesRowset(), colSource.getCellsValuesFieldIndex(), colSource.getCellsHandler(), colSource.getSelectHandler(), colSource.isReadOnly(), this, null, null);
                    model.addColumn(rCol);
                    added++;
                    TableColumn vCol = new TableColumn();
                    vCol.setIdentifier(rCol);
                    vCol.setHeaderValue(rCol.getRow().getColumnObject(rCol.getColTitleFieldIndex()));
                    vCol.setWidth(50);
                    vCol.setPreferredWidth(50);
                    vCol.setMaxWidth(Integer.MAX_VALUE);
                    vCol.setMinWidth(5);
                    colSource.getRowsColumnsHandler().handle(vCol);
                    //view.addColumn(vCol);
                    //view.moveColumn(view.getColumnCount() - 1, localIndexToAddTo + idxAfterGlue);
                    colRow.addPropertyChangeListener(new Row2ColumnChangesReflector(vCol));
                    GridColumnsGroup rowGroup = new GridColumnsGroup(vCol);
                    if (leavesToGroups != null) {
                        leavesToGroups.put(vCol, rowGroup);
                    }
                    if (colSource.getGroup() != null) {
                        colSource.getGroup().addChild(rowGroup);
                    }
                    view.addColumn(vCol);
                    view.moveColumn(view.getColumnCount() - 1, localIndexToAddTo + idxAfterGlue);
                    DbGridColumn dbCol = new DbGridColumn();
                    dbCol.setName(COLUMN_NAME + IDGenerator.genID().toString());
                    ScriptableColumn scriptColumn = new ScriptableColumn(dbCol, rCol, vCol, view.getColumnCount() - 1, view, model, leavesToGroups);
                    // -1 excludes anchor column.
                    int curentPos = localIndexToAddTo + idxAfterGlue - fixedColumns - 1;
                    if (curentPos > scriptableColumns.size()) {
                        scriptableColumns.add(scriptColumn);
                    } else {
                        scriptableColumns.add(curentPos, scriptColumn);
                    }
                    defineScriptableColumn(scriptColumn);
                    columnsStore.put(vCol, scriptColumn);
                }
            }
        }
        if (leftConstraint != null) {
            assert rightConstraint != null : "Left ans right constraints must be setted commonly";
            leftConstraint.setMax(leftConstraint.getMax() + added);
            rightConstraint.setMin(rightConstraint.getMin() + added);
        }
    }

    private void reindexView() throws Exception {
        rows2Cols.clear();
        Map<ModelColumn, Integer> colIndexes = new HashMap<>();
        for (int i = 0; i < model.getColumnCount(); i++) {
            ModelColumn mCol = model.getColumn(i);
            colIndexes.put(mCol, i);
        }
        for (int i = 0; i < view.getColumnCount(); i++) {
            TableColumn vCol = view.getColumn(i);
            Object oCol = vCol.getIdentifier();
            if (oCol != null && oCol instanceof ModelColumn) {
                ModelColumn mCol = (ModelColumn) oCol;
                vCol.setModelIndex(colIndexes.get(mCol));
                if (mCol instanceof RowModelColumn) {
                    RowModelColumn mrCol = (RowModelColumn) mCol;
                    rows2Cols.put(mrCol.getRow(), vCol);
                    vCol.setHeaderValue(mrCol.getRow().getColumnObject(mrCol.getColTitleFieldIndex()));
                }
            }
        }
    }

    @Override
    public void rowsetFiltered(RowsetFilterEvent event) {
        riddle(event.getRowset());
        model.fireRowsDataChanged();
    }

    @Override
    public void rowsetRequeried(RowsetRequeryEvent event) {
        riddle(event.getRowset());
        model.fireRowsDataChanged();
    }

    @Override
    public void rowsetRolledback(RowsetRollbackEvent event) {
        riddle(event.getRowset());
    }

    @Override
    public void rowsetNextPageFetched(RowsetNextPageEvent event) {
        riddle(event.getRowset());
        model.fireRowsDataChanged();
    }

    @Override
    public void rowsetSaved(RowsetSaveEvent event) {
    }

    @Override
    public void rowsetScrolled(RowsetScrollEvent event) {
    }

    @Override
    public void rowsetSorted(RowsetSortEvent event) {
        //riddle(event.getRowset()); such processing is questionable
    }

    @Override
    public void rowInserted(RowsetInsertEvent event) {
        if (!event.isAjusting()) {
            try {
                addAbsent(event.getRowset());
                reindexView();
            } catch (Exception ex) {
                Logger.getLogger(ColumnsRiddler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void rowChanged(RowChangeEvent event) {
        // Row2ColumnChangesReflector takes care of reflecting rows changes to column's titles
    }

    @Override
    public void rowDeleted(RowsetDeleteEvent event) {
        if (!event.isAjusting()) {
            try {
                removeUnactual(event.getRowset());
                reindexView();
                model.fireRowsDataChanged();
            } catch (Exception ex) {
                Logger.getLogger(ColumnsRiddler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
