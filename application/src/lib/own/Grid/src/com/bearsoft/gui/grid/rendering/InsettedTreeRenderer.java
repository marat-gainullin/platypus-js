/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.rendering;

import com.bearsoft.gui.grid.data.CellData;
import com.eas.gui.CascadedStyle;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Gala
 */
public class InsettedTreeRenderer<T> extends InsettedRenderer {

    public static final String INSETTEDTREERENDERER_NEEDS_TREECOLUMNLEADING = "InsettedTreeRenderer needs TreeColumnLeadingComponent instance as leading component";
    protected TreeColumnLeadingComponent<T> treeLeadingComponent;

    /**
     * Creates a new instance of InsettedRenderer.
     * @param aDelegate TableCellRenderer instance we have delegate all significant work to.
     * @see InsettedRenderer
     * @see TableCellRenderer
     */
    public InsettedTreeRenderer(TableCellRenderer aDelegate) {
        super(aDelegate);
    }

    /**
     * Creates a new instance of InsettedRenderer.
     * @param aDelegate TableCellRenderer instance we have delegate all significant work to.
     * @param aLeadingComponent Component that will be rendered on the left side of table cell.
     * @see InsettedRenderer
     * @see TableCellRenderer
     */
    public InsettedTreeRenderer(TableCellRenderer aDelegate, Component aLeadingComponent) {
        super(aDelegate, aLeadingComponent);
        assert leadingComponent instanceof TreeColumnLeadingComponent<?> : INSETTEDTREERENDERER_NEEDS_TREECOLUMNLEADING;
        treeLeadingComponent = (TreeColumnLeadingComponent<T>) leadingComponent;
    }

    /**
     * Creates a new instance of InsettedRenderer.
     * @param aDelegate TableCellRenderer instance we have delegate all significant work to.
     * @param aLeadingComponent Component that will be rendered on the left side of table cell.
     * @param aTrailingComponent  Component that will be rendered on the right side of table cell.
     * @see InsettedRenderer
     * @see TableCellRenderer
     */
    public InsettedTreeRenderer(TableCellRenderer aDelegate, Component aLeadingComponent, Component aTrailingComponent) {
        super(aDelegate, aLeadingComponent, aTrailingComponent);
        assert leadingComponent instanceof TreeColumnLeadingComponent<?> : INSETTEDTREERENDERER_NEEDS_TREECOLUMNLEADING;
        treeLeadingComponent = (TreeColumnLeadingComponent<T>) leadingComponent;
    }

    /**
     * Refreshes all cache information about rows and rendering.
     */
    public void refresh() {
        if (treeLeadingComponent != null) {
            treeLeadingComponent.unprepare();
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof CellData && ((CellData) value).getStyle() != null) {
            CascadedStyle oldStyle = treeLeadingComponent.getStyle();
            treeLeadingComponent.setStyle(((CellData) value).getStyle());
            try {
                treeLeadingComponent.prepareRow(table.convertRowIndexToModel(row));
            } finally {
                treeLeadingComponent.setStyle(oldStyle);
            }
        } else {
            treeLeadingComponent.prepareRow(table.convertRowIndexToModel(row));
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
