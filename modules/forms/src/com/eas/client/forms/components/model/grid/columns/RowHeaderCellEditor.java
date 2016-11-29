/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.columns;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author mg
 */
public abstract class RowHeaderCellEditor extends JPanel implements TableCellEditor, ActionListener {

    protected JTable editingTable;
    protected int editingRow = -1;
    protected Set<CellEditorListener> listenenrs = new HashSet<>();

    public RowHeaderCellEditor() {
        super(new BorderLayout());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
        editingTable = table;
        editingRow = row;
        return this;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return anEvent instanceof MouseEvent;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        invokeRowHeaderAction();
        return false;
    }

    @Override
    public boolean stopCellEditing() {
        ChangeEvent event = new ChangeEvent(this);
        for (CellEditorListener l : listenenrs.toArray(new CellEditorListener[0])) {
            l.editingStopped(event);
        }
        return true;
    }

    @Override
    public void cancelCellEditing() {
        ChangeEvent event = new ChangeEvent(this);
        for (CellEditorListener l : listenenrs.toArray(new CellEditorListener[0])) {
            l.editingCanceled(event);
        }
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        listenenrs.add(l);
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        listenenrs.remove(l);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        invokeRowHeaderAction();
    }

    protected abstract void invokeRowHeaderAction();
}
