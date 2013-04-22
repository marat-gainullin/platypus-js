/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.output;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author mg
 */
public class SourceRenderer extends DefaultTableCellRenderer {

    public SourceRenderer() {
        super();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (!isSelected) {
            Component rComp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            JButton btn = new JButton();
            rComp.setBackground(btn.getBackground());
            return rComp;
        } else {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
