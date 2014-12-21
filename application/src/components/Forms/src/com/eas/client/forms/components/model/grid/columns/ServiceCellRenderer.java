/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.columns;

import com.bearsoft.rowset.exceptions.RowsetException;
import com.eas.client.forms.IconCache;
import com.eas.client.forms.components.model.grid.ModelGrid;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
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
        indicator.setOpaque(false);
        rowDescriptor.setOpaque(false);
        add(indicator, BorderLayout.WEST);
        add(rowDescriptor, BorderLayout.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int column) {
        try {
            int modelRow = table.convertRowIndexToModel(rowIndex);
            ModelGrid grid = ModelGrid.getFirstDbGrid(table);
            JSObject row = grid.index2Row(modelRow);
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
            return this;
        } catch (RowsetException ex) {
            Logger.getLogger(ServiceCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
            return this;
        }
    }
}
