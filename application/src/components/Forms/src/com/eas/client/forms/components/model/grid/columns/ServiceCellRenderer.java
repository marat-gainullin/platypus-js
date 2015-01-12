/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.columns;

import com.eas.client.forms.IconCache;
import com.eas.client.forms.components.model.grid.ModelGrid;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ServiceCellRenderer extends RowHeaderCellRenderer implements TableCellRenderer {

    public static Icon currentIcon = IconCache.getIcon("16x16/currentRow.png");
    public static Icon editingIcon = IconCache.getIcon("16x16/editingRow.png");
    public static Icon insertingIcon = IconCache.getIcon("16x16/newRow.png");
    public static Icon processingIcon = IconCache.getIcon("16x16/hourglass-select-remain.png");
    protected JLabel indicator = new JLabel();
    protected JLabel rowDescriptor = new JLabel();

    public ServiceCellRenderer() {
        super();
        indicator.setHorizontalTextPosition(JLabel.LEFT);
        indicator.setIconTextGap(0);
        indicator.setText(" ");
        indicator.setOpaque(false);
        rowDescriptor.setOpaque(false);
        add(indicator, BorderLayout.WEST);
        add(rowDescriptor, BorderLayout.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int column) {
        ModelGrid grid = ModelGrid.getFirstDbGrid(table);
        JSObject row = grid.elementByViewIndex(rowIndex);
        if (row != null) {
            if (grid.isElementProcessed(row)) {
                indicator.setIcon(processingIcon);
            } else if (grid.isCurrentRow(row)) {
                indicator.setIcon(currentIcon);
            } else {
                indicator.setIcon(null);
            }
            /*
             if (row.isInserted()) {
             rowDescriptor.setIcon(insertingIcon);
             } else if (row.isUpdated()) {
             rowDescriptor.setIcon(editingIcon);
             } else {
             rowDescriptor.setIcon(null);
             }
             */
        } else {
            indicator.setIcon(null);
            rowDescriptor.setIcon(null);
        }
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        if (hasFocus) {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        } else {
            setBorder(null);
        }
        return this;
    }
}
