/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.output;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.model.gui.view.fields.FieldsView;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author mg
 */
public class TypeEditor implements TableCellEditor, ActionListener {

    protected Set<CellEditorListener> listeners = new HashSet<>();
    protected FieldsView<QueryEntity, QueryModel>.FieldsTypesModel typesModel;
    protected TypeRenderer typesRenderer;
    protected DataTypeInfo typeValue;
    protected JComboBox<Integer> comboTypes;

    public TypeEditor(FieldsView<QueryEntity, QueryModel>.FieldsTypesModel aTypesModel) {
        super();
        typesModel = aTypesModel;
        typesRenderer = new TypeRenderer();
        comboTypes = new JComboBox<>();
        comboTypes.setModel(typesModel);
        comboTypes.setRenderer(typesRenderer);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof DataTypeInfo) {
            typeValue = (DataTypeInfo) value;
            comboTypes.setSelectedItem(typeValue.getSqlType());
            comboTypes.removeActionListener(this);
            comboTypes.addActionListener(this);
            return comboTypes;
        } else {
            return null;
        }
    }

    @Override
    public Object getCellEditorValue() {
        return typeValue;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            MouseEvent mEvent = (MouseEvent) anEvent;
            return mEvent.getClickCount() > 1;
        } else {
            return true;
        }
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean stopCellEditing() {
        comboTypes.removeActionListener(this);
        ChangeEvent event = new ChangeEvent(this);
        for (CellEditorListener l : listeners) {
            l.editingStopped(event);
        }
        typeValue = null;
        return true;
    }

    @Override
    public void cancelCellEditing() {
        comboTypes.removeActionListener(this);
        ChangeEvent event = new ChangeEvent(this);
        for (CellEditorListener l : listeners) {
            l.editingCanceled(event);
        }
        typeValue = null;
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        listeners.add(l);
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        listeners.remove(l);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object oSelected = comboTypes.getSelectedItem();
        if (oSelected != null && oSelected instanceof Integer) {
            Integer iSelected = (Integer) oSelected;
            typeValue = DataTypeInfo.valueOf(iSelected);
            stopCellEditing();
        }
    }
}
