/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.columns;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JRadioButton;
import javax.swing.JTable;

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
        if (radio.isSelected()) {
            editingTable.setRowSelectionInterval(editingRow, editingRow);
        } else {
            editingTable.removeRowSelectionInterval(editingRow, editingRow);
        }
    }
}
