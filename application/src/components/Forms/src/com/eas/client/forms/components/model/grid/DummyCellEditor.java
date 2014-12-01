/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid;

import java.awt.Component;
import java.util.EventObject;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author mg
 */
public class DummyCellEditor implements TableCellEditor{

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getCellEditorValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isCellEditable(EventObject anEvent) {
        return false;
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    public boolean stopCellEditing() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void cancelCellEditing() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addCellEditorListener(CellEditorListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeCellEditorListener(CellEditorListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
