/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.columns;

import com.bearsoft.gui.grid.columns.ConstrainedColumnModel;
import com.bearsoft.gui.grid.selection.ConstrainedListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author mg
 */
public class CheckCellEditor extends RowHeaderCellEditor {

    protected JCheckBox check;

    public CheckCellEditor() {
        super();
        check = new JCheckBox();
        add(check, BorderLayout.CENTER);
        check.setOpaque(false);
        check.addActionListener(this);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int column) {
        check.setSelected(table.isRowSelected(rowIndex));
        return super.getTableCellEditorComponent(table, value, isSelected, rowIndex, column);
    }

    @Override
    protected void invokeRowHeaderAction() {
        TableColumnModel tcm = editingTable.getColumnModel();
        if (tcm instanceof ConstrainedColumnModel
                && tcm.getSelectionModel() instanceof ConstrainedListSelectionModel) {
            int colCount = ((ConstrainedColumnModel) tcm).getDelegate().getColumnCount();
            ((ConstrainedListSelectionModel) tcm.getSelectionModel()).getDelegate().setSelectionInterval(0, colCount - 1);
        }
        if (!check.isSelected()) {
            editingTable.addRowSelectionInterval(editingRow, editingRow);
        } else {
            editingTable.removeRowSelectionInterval(editingRow, editingRow);
        }
    }
}
