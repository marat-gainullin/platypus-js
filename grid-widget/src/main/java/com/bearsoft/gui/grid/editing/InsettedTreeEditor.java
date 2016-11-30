/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.editing;

import com.bearsoft.gui.grid.data.TableFront2TreedModel;
import com.bearsoft.gui.grid.rendering.TreeColumnLeadingComponent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Gala
 * @param <T>
 */
public class InsettedTreeEditor<T> extends InsettedEditor {

    public static final String INSETTEDTREEEDITOR_NEEDS_TREECOLUMNLEADING = "InsettedTreeEditor needs TreeColumnLeadingComponent instance as leading component";
    protected TreeColumnLeadingComponent<T> treeLeadingComponent;
    protected boolean editable = true;

    /**
     * Creates a new instance of InsettedRenderer.
     *
     * @param aDelegate TableCellRenderer instance we have delegate all
     * significant work to.
     * @see InsettedEditor
     * @see TableCellEditor
     */
    public InsettedTreeEditor(TableCellEditor aDelegate) {
        super(aDelegate);
    }

    /**
     * Creates a new instance of InsettedRenderer.
     *
     * @param aDelegate TableCellRenderer instance we have delegate all
     * significant work to.
     * @param aLeadingComponent Component that will be rendered on the left side
     * of table cell.
     * @see InsettedEditor
     * @see TableCellRenderer
     */
    public InsettedTreeEditor(TableCellEditor aDelegate, Component aLeadingComponent) {
        super(aDelegate, aLeadingComponent);
        assert leadingComponent instanceof TreeColumnLeadingComponent : INSETTEDTREEEDITOR_NEEDS_TREECOLUMNLEADING;
        treeLeadingComponent = (TreeColumnLeadingComponent) leadingComponent;
    }

    /**
     * Creates a new instance of InsettedRenderer.
     *
     * @param aDelegate TableCellRenderer instance we have delegate all
     * significant work to.
     * @param aLeadingComponent Component that will be rendered on the left side
     * of table cell.
     * @param aTrailingComponent Component that will be rendered on the right
     * side of table cell.
     * @see InsettedEditor
     * @see TableCellEditor
     */
    public InsettedTreeEditor(TableCellEditor aDelegate, Component aLeadingComponent, Component aTrailingComponent) {
        super(aDelegate, aLeadingComponent, aTrailingComponent);
        assert leadingComponent instanceof TreeColumnLeadingComponent : INSETTEDTREEEDITOR_NEEDS_TREECOLUMNLEADING;
        treeLeadingComponent = (TreeColumnLeadingComponent<T>) leadingComponent;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean aValue) {
        editable = aValue;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (editable) {
            treeLeadingComponent.unprepare();
            treeLeadingComponent.prepareRow(table.convertRowIndexToModel(row));
            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        } else {
            return null;
        }
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            MouseEvent mEvent = (MouseEvent) anEvent;
            if (isMouseInLegs(mEvent)) {
                JTable table = (JTable) mEvent.getSource();
                mEvent.consume();
                int row2Edit = table.rowAtPoint(mEvent.getPoint());
                table.getSelectionModel().setValueIsAdjusting(false);
                toggle(table.convertRowIndexToModel(row2Edit));
                return false;
            } // let the delegate decide whether cell is editable
        }
        return super.isCellEditable(anEvent);
    }

    private boolean isMouseInLegs(MouseEvent mEvent) {
        if (mEvent.getSource() instanceof JTable) {
            JTable table = (JTable) mEvent.getSource();
            Point tablePoint = mEvent.getPoint();
            int row2Edit = table.rowAtPoint(mEvent.getPoint());
            int col2Edit = table.columnAtPoint(mEvent.getPoint());
            Rectangle cellRectangle = table.getCellRect(row2Edit, col2Edit, false);
            treeLeadingComponent.unprepare();
            treeLeadingComponent.prepareRow(table.convertRowIndexToModel(row2Edit));
            if (!treeLeadingComponent.isLeaf()) {
                Dimension prefSize = treeLeadingComponent.getPreferredSize();
                int prefWidth = prefSize.width;
                if (treeLeadingComponent.getNodeIcon() != null) {
                    prefWidth -= treeLeadingComponent.getNodeIcon().getIconWidth();
                }
                int legsIconWidth = treeLeadingComponent.getLegsIcon() != null ? treeLeadingComponent.getLegsIcon().getIconWidth() : 0;
                if (SwingUtilities.isLeftMouseButton(mEvent)
                        && tablePoint.x - cellRectangle.x <= prefWidth
                        && tablePoint.x - cellRectangle.x >= prefWidth - legsIconWidth - 3) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Toggles model's elemnt at the specified row.
     *
     * @param row2Toggle Row number in model space.
     */
    private void toggle(int row2Toggle) {
        TableFront2TreedModel<T> front = treeLeadingComponent.getFront();
        T element2Toggle = front.getElementAt(row2Toggle);
        if (front.isExpanded(element2Toggle)) {
            front.collapse(element2Toggle);
        } else {
            front.expand(element2Toggle, true);
        }
    }
}
