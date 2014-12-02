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
import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.TableCellRenderer;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class RowHeaderCellRenderer extends JPanel implements TableCellRenderer {

    public static Icon currentIcon = IconCache.getIcon("16x16/currentRow.png");
    public static Icon editingIcon = IconCache.getIcon("16x16/editingRow.png");
    public static Icon insertingIcon = IconCache.getIcon("16x16/newRow.png");
    public static Icon processingIcon = IconCache.getIcon("16x16/hourglass-select-remain.png");
    // May be JCheckBox or JRadioButton
    protected JToggleButton check;
    protected JLabel indicator = new JLabel();
    protected JLabel rowDescriptor = new JLabel();
    protected int headerType = RowHeaderTableColumn.ROWS_HEADER_TYPE_USUAL;

    public RowHeaderCellRenderer(int aHeaderType) {
        super(new BorderLayout());
        headerType = aHeaderType;
        if (headerType == RowHeaderTableColumn.ROWS_HEADER_TYPE_CHECKBOX) {
            check = new JCheckBox();
        } else if (headerType == RowHeaderTableColumn.ROWS_HEADER_TYPE_RADIOBUTTON) {
            check = new JRadioButton();
        } else if (headerType == RowHeaderTableColumn.ROWS_HEADER_TYPE_USUAL) {
        } else {
            assert false : "RowHeaderCellRenderer must be used not with \"ROWS_HEADER_TYPE_NONE\" rows header type";
        }
        indicator.setOpaque(false);
        rowDescriptor.setOpaque(false);
        add(indicator, BorderLayout.WEST);
        add(rowDescriptor, BorderLayout.CENTER);
        if (check != null) {
            add(check, BorderLayout.EAST);
            check.setOpaque(false);
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int column) {
        try {
            if (check != null) {
                check.setSelected(table.isRowSelected(rowIndex));
            }
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
            Logger.getLogger(RowHeaderCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
            return this;
        }
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void repaint(Rectangle r) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     *
     * @since 1.5
     */
    @Override
    public void repaint() {
    }
}
