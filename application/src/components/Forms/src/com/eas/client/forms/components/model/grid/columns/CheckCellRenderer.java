/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.columns;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTable;

/**
 *
 * @author mg
 */
public class CheckCellRenderer extends RowHeaderCellRenderer {

    protected JCheckBox check;

    public CheckCellRenderer() {
        super();
        check = new JCheckBox();
        add(check, BorderLayout.CENTER);
        check.setOpaque(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int column) {
        if (check != null) {
            check.setSelected(table.isRowSelected(rowIndex));
        }
        return this;
    }
}
