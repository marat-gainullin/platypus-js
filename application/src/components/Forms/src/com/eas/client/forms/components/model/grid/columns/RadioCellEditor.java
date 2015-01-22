/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.columns;

import com.bearsoft.gui.grid.columns.ConstrainedColumnModel;
import com.bearsoft.gui.grid.selection.ConstrainedListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author mg
 */
public class RadioCellEditor extends RowHeaderCellEditor {

    protected JRadioButton radio;

    public RadioCellEditor() {
        super();
        radio = new JRadioButton();
        add(radio, BorderLayout.CENTER);
        radio.setOpaque(false);
        radio.addActionListener(this);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int column) {
        radio.setSelected(table.isRowSelected(rowIndex));
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
        if (!radio.isSelected()) {
            editingTable.setRowSelectionInterval(editingRow, editingRow);
        } else {
            editingTable.removeRowSelectionInterval(editingRow, editingRow);
        }
    }
}
