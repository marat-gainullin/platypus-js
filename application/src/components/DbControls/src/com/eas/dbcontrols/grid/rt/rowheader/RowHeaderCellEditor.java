/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.rowheader;

import com.bearsoft.gui.grid.columns.ConstrainedColumnModel;
import com.bearsoft.gui.grid.selection.ConstrainedListSelectionModel;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.eas.dbcontrols.DbControlsUtils;
import com.eas.dbcontrols.grid.DbGrid;
import com.eas.dbcontrols.grid.DbGridRowsColumnsDesignInfo;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author mg
 */
public class RowHeaderCellEditor extends JPanel implements TableCellEditor, ActionListener {

    // May be JCheckBox or JRadioButton
    // configuration
    protected JToggleButton check;
    protected JLabel indicator = new JLabel();
    protected JLabel rowDescriptor = new JLabel();
    protected int headerType = DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_USUAL;
    // runtime
    protected JTable editingTable;
    protected int editingRow = -1;
    protected Set<CellEditorListener> listenenrs = new HashSet<>();

    public RowHeaderCellEditor(int aHeaderType) {
        super(new BorderLayout());
        headerType = aHeaderType;
        if (headerType == DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_CHECKBOX) {
            check = new JCheckBox();
        } else if (headerType == DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_RADIOBUTTON) {
            check = new JRadioButton();
        } else if (headerType == DbGridRowsColumnsDesignInfo.ROWS_HEADER_TYPE_USUAL) {
        } else {
            assert false : "RowHeaderCellEditor must be used not with \"ROWS_HEADER_TYPE_NONE\" rows header type";
        }
        indicator.setOpaque(false);
        rowDescriptor.setOpaque(false);
        add(indicator, BorderLayout.WEST);
        add(rowDescriptor, BorderLayout.CENTER);
        if (check != null) {
            add(check, BorderLayout.EAST);
            check.setOpaque(false);
            check.addActionListener(this);
        }
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int column) {
        try {
            editingTable = table;
            editingRow = rowIndex;
            if (check != null) {
                check.setSelected(table.isRowSelected(editingRow));
            }
            int modelRow = table.convertRowIndexToModel(rowIndex);
            DbGrid grid = DbControlsUtils.getFirstDbGrid(table);
            Row row = grid.index2Row(modelRow);
            if (row != null) {
                if (grid.isCurrentRow(row)) {
                    indicator.setIcon(RowHeaderCellRenderer.currentIcon);
                } else {
                    indicator.setIcon(null);
                }
                if (row.isInserted()) {
                    rowDescriptor.setIcon(RowHeaderCellRenderer.insertingIcon);
                } else if (row.isUpdated()) {
                    rowDescriptor.setIcon(RowHeaderCellRenderer.editingIcon);
                } else {
                    rowDescriptor.setIcon(null);
                }
            } else {
                indicator.setIcon(null);
                rowDescriptor.setIcon(null);
            }
            return this;
        } catch (RowsetException ex) {
            Logger.getLogger(RowHeaderCellEditor.class.getName()).log(Level.SEVERE, null, ex);
            return this;
        }
    }

    public Object getCellEditorValue() {
        return null;
    }

    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            return true;
        }
        return false;
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }

    public boolean stopCellEditing() {
        ChangeEvent event = new ChangeEvent(this);
        for (CellEditorListener l : listenenrs.toArray(new CellEditorListener[0])) {
            l.editingStopped(event);
        }
        return true;
    }

    public void cancelCellEditing() {
        ChangeEvent event = new ChangeEvent(this);
        for (CellEditorListener l : listenenrs.toArray(new CellEditorListener[0])) {
            l.editingCanceled(event);
        }
    }

    public void addCellEditorListener(CellEditorListener l) {
        listenenrs.add(l);
    }

    public void removeCellEditorListener(CellEditorListener l) {
        listenenrs.remove(l);
    }

    public void actionPerformed(ActionEvent e) {
        TableColumnModel tcm = editingTable.getColumnModel();
        if (tcm instanceof ConstrainedColumnModel
                && tcm.getSelectionModel() instanceof ConstrainedListSelectionModel) {
            int colCount = ((ConstrainedColumnModel) tcm).getDelegate().getColumnCount();
            ((ConstrainedListSelectionModel) tcm.getSelectionModel()).getDelegate().setSelectionInterval(0, colCount - 1);
        }
        if (check instanceof JRadioButton) {
            if (check.isSelected()) {
                editingTable.setRowSelectionInterval(editingRow, editingRow);
            } else {
                editingTable.removeRowSelectionInterval(editingRow, editingRow);
            }
        } else {
            if (check.isSelected()) {
                editingTable.addRowSelectionInterval(editingRow, editingRow);
            } else {
                editingTable.removeRowSelectionInterval(editingRow, editingRow);
            }
        }
    }
}
