/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid;

import com.bearsoft.gui.grid.columns.ConstrainedColumnModel;
import com.bearsoft.gui.grid.data.CellData;
import com.bearsoft.gui.grid.editing.InsettedTreeEditor;
import com.bearsoft.gui.grid.rendering.InsettedTreeRenderer;
import com.eas.client.forms.ModelCellEditingListener;
import com.eas.client.forms.events.CellRenderEvent;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import com.eas.client.forms.components.model.grid.columns.RadioServiceColumn;
import com.eas.gui.ScriptColor;
import com.eas.script.HasPublished;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.EventObject;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class GridTable extends JTable implements ModelCellEditingListener {

    protected Color oddRowsColor;
    protected boolean editable = true;
    protected boolean showOddRowsInOtherColor = true;
    protected JTable aboveNeightbour;
    protected TablesGridContainer gridContainer;

    public GridTable(JTable aAboveNeightbour, TablesGridContainer aGridContainer) {
        super();
        aboveNeightbour = aAboveNeightbour;
        gridContainer = aGridContainer;
        setDoubleBuffered(true);
    }

    public Color getOddRowsColor() {
        return oddRowsColor;
    }

    public void setOddRowsColor(Color aValue) {
        oddRowsColor = aValue;
    }

    public boolean isShowOddRowsInOtherColor() {
        return showOddRowsInOtherColor;
    }

    public void setShowOddRowsInOtherColor(boolean aValue) {
        showOddRowsInOtherColor = aValue;
    }

    @Override
    public int convertColumnIndexToModel(int viewColumnIndex) {
        if (columnModel instanceof ConstrainedColumnModel) {
            return ((ConstrainedColumnModel) columnModel).getConstraint().unconstraint(viewColumnIndex);
        } else {
            return viewColumnIndex;
        }
    }

    @Override
    public int convertColumnIndexToView(int modelColumnIndex) {
        if (columnModel instanceof ConstrainedColumnModel) {
            return ((ConstrainedColumnModel) columnModel).getConstraint().constraint(modelColumnIndex);
        } else {
            return modelColumnIndex;
        }
    }

    protected boolean allowCellEdit(int row, int column) {
        TableColumn tCol = getColumnModel().getColumn(column);
        return (editable && !isColumnReadOnly(tCol))
                || tCol instanceof RadioServiceColumn
                || tCol.getCellEditor() instanceof InsettedTreeEditor;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return allowCellEdit(row, column) && super.isCellEditable(row, column);
    }

    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        return super.editCellAt(row, column, e);
    }

    protected boolean cellEditingCompletion;

    @Override
    public void cellEditingCompleted() {
        cellEditingCompletion = true;
        try {
            TableModel tm = getModel();
            TableCellEditor ce = getCellEditor();
            if (ce != null && tm != null && isEditing()) {
                int modelEditingRow = convertRowIndexToModel(getEditingRow());
                int modelEditingColumn = convertColumnIndexToModel(getEditingColumn());
                int oldRowCount = tm.getRowCount();
                Object newEditorValue = ce.getCellEditorValue();
                tm.setValueAt(newEditorValue, modelEditingRow, modelEditingColumn);
                int newRowCount = tm.getRowCount();
                if (oldRowCount != newRowCount) {
                    ce.cancelCellEditing();
                }
            }
        } finally {
            cellEditingCompletion = false;
        }
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component res = super.prepareRenderer(renderer, row, column);
        int lrow = row;
        if (aboveNeightbour != null) {
            lrow += aboveNeightbour.getRowCount();
        }
        if (showOddRowsInOtherColor) {
            if ((lrow + 1) % 2 != 0) {
                if (getBackground().equals(res.getBackground())) {
                    if (oddRowsColor != null) {
                        res.setBackground(oddRowsColor);
                    } else {
                        Color aColor = getBackground().equals(Color.WHITE)
                                ? ScriptColor.darker(getBackground(), 0.95)
                                : ScriptColor.brighter(getBackground(), 0.95);
                        res.setBackground(aColor);
                    }
                }
            }
        }
        Object value = getValueAt(row, column);
        TableColumn tColumn = getColumnModel().getColumn(column);
        if (tColumn instanceof ModelColumn) {
            ModelColumn col = (ModelColumn) tColumn;
            JSObject lOnRender = gridContainer != null ? gridContainer.getOnRender() : null;
            if (col.getOnRender() != null) {
                lOnRender = col.getOnRender();
            }
            if (lOnRender != null) {
                JSObject element = gridContainer != null ? gridContainer.elementByViewIndex(lrow) : null;
                String display = res instanceof JLabel ? ((JLabel) res).getText() : (value != null ? value.toString() : "");
                CellRenderEvent event = new CellRenderEvent(new HasPublished() {

                    @Override
                    public JSObject getPublished() {
                        return col.getEventsSource();
                    }

                    @Override
                    public void setPublished(JSObject aPublished) {
                    }

                }, col, new CellData(value, display), element);
                lOnRender.call(col.getEventsSource(), new Object[]{event.getPublished()});
            }
        }
        return res;
    }

    //
    protected boolean processingKeyBinding;
    protected boolean editingEndedWhileKeyBinding;

    @Override
    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
        processingKeyBinding = true;
        try {
            return super.processKeyBinding(ks, e, condition, pressed);
        } finally {
            processingKeyBinding = false;
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (gridContainer != null) {
            // TableModelEvent may occur while super constructor is called.
            // So, we wait our initialization in constructor will be completed.
            for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
                TableColumn tc = getColumnModel().getColumn(i);
                if (tc.getCellRenderer() instanceof InsettedTreeRenderer) {
                    ((InsettedTreeRenderer) tc.getCellRenderer()).refresh();
                }
            }
            if (!cellEditingCompletion && getCellEditor() != null) {
                getCellEditor().cancelCellEditing();
            }
        }
        super.tableChanged(e);
    }

    @Override
    public void sorterChanged(RowSorterEvent e) {
        if (gridContainer != null) {
            gridContainer.try2CancelAnyEditing();
        }
        super.sorterChanged(e);
    }

    @Override
    public void columnAdded(TableColumnModelEvent e) {
        if (gridContainer != null) {
            gridContainer.reindexColumns();
        }
        super.columnAdded(e);
    }

    @Override
    public void columnRemoved(TableColumnModelEvent e) {
        if (gridContainer != null) {
            gridContainer.reindexColumns();
        }
        super.columnRemoved(e);
    }

    @Override
    public void columnMoved(TableColumnModelEvent e) {
        if (gridContainer != null) {
            gridContainer.reindexColumns();
        }
        super.columnMoved(e);
    }

    @Override
    public void editingStopped(ChangeEvent e) {
        editingEndedWhileKeyBinding = false;
        if (processingKeyBinding) {
            editingEndedWhileKeyBinding = true;
        }
        super.editingStopped(e);
        if (gridContainer != null && gridContainer.isAutoRedraw()) {
            gridContainer.redraw();
        }
    }

    @Override
    public void editingCanceled(ChangeEvent e) {
        editingEndedWhileKeyBinding = false;
        if (processingKeyBinding) {
            editingEndedWhileKeyBinding = true;
        }
        super.editingCanceled(e);
    }

    @Override
    public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
        boolean bad = processingKeyBinding && editingEndedWhileKeyBinding;
        if (!bad) {
            TableColumn tc = getColumnModel().getColumn(columnIndex);
            if (skipableColumn(tc)) {
                int leadColumnIndex = getColumnModel().getSelectionModel().getLeadSelectionIndex();
                if (columnIndex - 1 == leadColumnIndex) {
                    while (skipableColumn(tc)) {
                        columnIndex++;
                        if (columnIndex >= 0 && columnIndex < getColumnModel().getColumnCount()) {
                            tc = getColumnModel().getColumn(columnIndex);
                        } else {
                            break;
                        }
                    }
                } else if (columnIndex + 1 == leadColumnIndex) {
                    while (skipableColumn(tc)) {
                        columnIndex--;
                        if (columnIndex >= 0 && columnIndex < getColumnModel().getColumnCount()) {
                            tc = getColumnModel().getColumn(columnIndex);
                        } else {
                            break;
                        }
                    }
                }
            }
            if (columnIndex >= 0 && columnIndex < getColumnModel().getColumnCount()) {
                super.changeSelection(rowIndex, columnIndex, toggle, extend);
            }
        }
        editingEndedWhileKeyBinding = false;
    }

    public static boolean skipableColumn(TableColumn tc) {
        return (tc.getWidth() == 0 && tc.getMinWidth() == 0 && tc.getMaxWidth() == 0);
    }

    public void setEditable(boolean aValue) {
        editable = aValue;
        for (int column = 0; column < getColumnModel().getColumnCount(); column++) {
            TableColumn tCol = getColumnModel().getColumn(column);
            if (tCol.getCellEditor() instanceof InsettedTreeEditor<?>) {
                InsettedTreeEditor<?> ie = (InsettedTreeEditor<?>) tCol.getCellEditor();
                ie.setEditable(editable);
            }
        }
    }

    private boolean isColumnReadOnly(TableColumn aViewCol) {
        if (aViewCol.getIdentifier() instanceof ModelColumn) {
            return ((ModelColumn) aViewCol.getIdentifier()).isReadOnly();
        }
        return false;
    }
}
