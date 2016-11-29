/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.columns;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.UIManager;

/**
 *
 * @author mg
 */
public class RadioCellRenderer extends RowHeaderCellRenderer {

    protected JRadioButton radio;

    public RadioCellRenderer() {
        super();
        radio = new JRadioButton();
        add(radio, BorderLayout.CENTER);
        radio.setOpaque(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int column) {
        radio.setSelected(table.isRowSelected(rowIndex));
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        if (hasFocus) {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        } else {
            setBorder(null);
        }
        return this;
    }

}
