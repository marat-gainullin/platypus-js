/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.columns;

import com.eas.client.forms.components.model.grid.ModelGrid;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ServiceCellEditor extends RowHeaderCellEditor implements TableCellEditor, ActionListener {

    protected JLabel indicator = new JLabel();
    protected JLabel rowDescriptor = new JLabel();

    public ServiceCellEditor() {
        super();
        indicator.setOpaque(false);
        rowDescriptor.setOpaque(false);
        add(indicator, BorderLayout.WEST);
        add(rowDescriptor, BorderLayout.CENTER);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int column) {
        ModelGrid grid = ModelGrid.getFirstDbGrid(table);
        JSObject row = grid.elementByViewIndex(rowIndex);
        if (row != null) {
            if (grid.isCurrentRow(row)) {
                indicator.setIcon(ServiceCellRenderer.currentIcon);
            } else {
                indicator.setIcon(null);
            }
            /*
             if (row.isInserted()) {
             rowDescriptor.setIcon(RowHeaderCellRenderer.insertingIcon);
             } else if (row.isUpdated()) {
             rowDescriptor.setIcon(RowHeaderCellRenderer.editingIcon);
             } else {
             rowDescriptor.setIcon(null);
             }
             */
        } else {
            indicator.setIcon(null);
            rowDescriptor.setIcon(null);
        }
        super.getTableCellEditorComponent(table, value, isSelected, rowIndex, column);
        return this;
    }

    @Override
    protected void invokeRowHeaderAction() {
        editingTable.setRowSelectionInterval(editingRow, editingRow);
    }
}
