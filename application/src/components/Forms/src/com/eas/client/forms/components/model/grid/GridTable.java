/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid;

import com.bearsoft.gui.grid.editing.InsettedTreeEditor;
import com.bearsoft.gui.grid.rendering.InsettedTreeRenderer;
import com.eas.client.forms.ModelCellEditingListener;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import com.eas.client.forms.components.model.grid.columns.RadioServiceColumn;
import com.eas.gui.ScriptColor;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

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
        if (isShowOddRowsInOtherColor()) {
            int lrow = row;
            if (aboveNeightbour != null) {
                lrow += aboveNeightbour.getRowCount();
            }
            lrow++;// 0-base to 1-based.
            int half = lrow / 2;
            if (half * 2 != lrow) {
                if (showOddRowsInOtherColor && getBackground().equals(res.getBackground())) {
                    if (oddRowsColor != null) {
                        res.setBackground(oddRowsColor);
                    } else {
                        Color aColor = getBackground().equals(Color.WHITE)
                                ? ScriptColor.darker(getBackground(), 0.95)
                                : ScriptColor.brighter(getBackground(), 0.95);
                        res.setBackground(aColor);
                    }
                }
                return res;
            }
        }
        return res;
    }
    //
    protected boolean processingKeyBinding = false;
    protected boolean editingEndedWhileKeyBinding = false;

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
    public void editingStopped(ChangeEvent e) {
        editingEndedWhileKeyBinding = false;
        if (processingKeyBinding) {
            editingEndedWhileKeyBinding = true;
        }
        super.editingStopped(e);
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
        boolean bad = processingKeyBinding && editingEndedWhileKeyBinding && getSelectionModel().getLeadSelectionIndex() == getRowCount() - 1 && rowIndex == 0;
        if (!bad) {
            TableColumn tc = getColumnModel().getColumn(columnIndex);
            if (!(tc instanceof RadioServiceColumn)) {
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
        } else {
            editingEndedWhileKeyBinding = false;
        }
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
