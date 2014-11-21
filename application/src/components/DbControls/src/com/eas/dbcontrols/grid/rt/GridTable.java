/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt;

import com.bearsoft.gui.grid.editing.InsettedTreeEditor;
import com.bearsoft.gui.grid.rendering.InsettedTreeRenderer;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.eas.dbcontrols.DbControlEditingListener;
import com.eas.dbcontrols.ScalarDbControl;
import com.eas.dbcontrols.grid.rt.columns.model.ModelColumn;
import com.eas.dbcontrols.grid.rt.columns.view.RowHeaderTableColumn;
import com.eas.dbcontrols.grid.rt.models.RowsetsModel;
import com.eas.gui.CascadedStyle;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class GridTable extends JTable implements DbControlEditingListener {

    protected Color oddRowsColor;
    protected boolean editable = true;
    protected boolean showOddRowsInOtherColor = true;
    protected JTable aboveNeightbour = null;
    protected Rowset rowsRowset;
    protected TablesGridContainer gridContainer;

    public GridTable(JTable aAboveNeightbour, Rowset aRowsRowset, TablesGridContainer aGridContainer) {
        super();
        aboveNeightbour = aAboveNeightbour;
        rowsRowset = aRowsRowset;
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
                || tCol instanceof RowHeaderTableColumn
                || tCol.getCellEditor() instanceof InsettedTreeEditor;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return allowCellEdit(row, column) && super.isCellEditable(row, column);
    }

    @Override
    protected void paintComponent(Graphics g) {
        try {
            int oldCursorPos = rowsRowset.getCursorPos();
            try {
                super.paintComponent(g);
            } finally {
                RowsetsModel.restoreRowsRowsetCursorPos(rowsRowset, oldCursorPos);
            }
        } catch (Exception ex) {
            Logger.getLogger(GridTable.class.getName()).log(Level.SEVERE, null, ex);
        }
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

                Object newModelValue = tm.getValueAt(modelEditingRow, modelEditingColumn);
                if (ce instanceof ScalarDbControl) {
                    ScalarDbControl control = (ScalarDbControl) ce;
                    control.beginUpdate();
                    try {
                        control.setEditingValue(newModelValue);
                    } finally {
                        control.endUpdate();
                    }
                }

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
                                ? CascadedStyle.darkerColor(getBackground(), 0.95)
                                : CascadedStyle.brighterColor(getBackground(), 0.95);
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

    private TableColumn getResizingColumn() {
        return (tableHeader == null) ? null
                : tableHeader.getResizingColumn();
    }

    @Override
    public void doLayout() {
        TableColumn resizingColumn = getResizingColumn();
        boolean resizingColumnMet = false;
        int leftWidth = 0;
        List<TableColumn> rightColumns = new ArrayList<>();
        for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
            if (resizingColumn == null || resizingColumnMet) {
                rightColumns.add(getColumnModel().getColumn(i));
            } else {
                leftWidth += getColumnModel().getColumn(i).getWidth();
            }
            if (!resizingColumnMet && resizingColumn == getColumnModel().getColumn(i)) {
                resizingColumnMet = true;
            }
        }
        int rightWidthRemoved = 0;
        int rightWidth = 0;
        for (int i = rightColumns.size() - 1; i >= 0; i--) {
            TableColumn tCol = rightColumns.get(i);
            rightWidth += tCol.getWidth();
            if (tCol instanceof RowHeaderTableColumn 
                    || !tCol.getResizable()) {
                rightColumns.remove(i);
                rightWidthRemoved += tCol.getWidth();
            }
        }
        // There are cases, when this is very harmful.
        // Furthermore, it seems, that accounting such border delta is useless
        // Note! If you change this, than you must test all grid columns resising capabilities
        // in synthetica LAFs, jtattoo LAFs and of course in metal LAF.
        int borderDelta = 0;//getBorder() == null ? 0 : getBorder().getBorderInsets(this).left + getBorder().getBorderInsets(this).right;
        int delta = (getWidth() - borderDelta) - (leftWidth + rightWidth);
        if (delta != 0) {
            for (int i = 0; i < rightColumns.size(); i++) {
                TableColumn tCol = rightColumns.get(i);
                float coef = (float) tCol.getWidth() / (float) (rightWidth - rightWidthRemoved);
                int newWidth = tCol.getWidth() + Math.round(delta * coef);
                silentSetWidth2Column(tCol, newWidth);
            }
            if ((getWidth() - borderDelta) != getColumnModel().getTotalColumnWidth() && getColumnModel().getColumnCount() > 0) {
                TableColumn goatCol = resizingColumn;
                if (goatCol == null) {
                    goatCol = getColumnModel().getColumn(getColumnModel().getColumnCount() - 1);
                }
                if (goatCol.getResizable()) {
                    int goatColNewWidth = goatCol.getWidth() + ((getWidth() - borderDelta) - getColumnModel().getTotalColumnWidth());
                    silentSetWidth2Column(goatCol, goatColNewWidth);
                }
            }
        }
    }

    private void silentSetWidth2Column(TableColumn tCol, int newWidth) {
        PropertyChangeListener[] listeners = tCol.getPropertyChangeListeners();
        for (PropertyChangeListener pcl : listeners) {
            tCol.removePropertyChangeListener(pcl);
        }
        try {
            tCol.setWidth(Math.max(tCol.getMinWidth(), newWidth));
        } finally {
            for (PropertyChangeListener pcl : listeners) {
                tCol.addPropertyChangeListener(pcl);
            }
        }
    }

    @Override
    public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
        boolean bad = processingKeyBinding && editingEndedWhileKeyBinding && getSelectionModel().getLeadSelectionIndex() == getRowCount() - 1 && rowIndex == 0;
        if (!bad) {
            TableColumn tc = getColumnModel().getColumn(columnIndex);
            if (!(tc instanceof RowHeaderTableColumn)) {
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
                InsettedTreeEditor<Row> ie = (InsettedTreeEditor<Row>) tCol.getCellEditor();
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
