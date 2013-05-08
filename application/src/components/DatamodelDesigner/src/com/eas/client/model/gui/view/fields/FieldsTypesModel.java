/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view.fields;

import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.model.Model;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author mg
 */
public class FieldsTypesModel implements ComboBoxModel<Integer> {
    private Set<ListDataListener> listeners = new HashSet<>();
    private Integer[] sqlTypes;
    private Integer selectedItem;
    private Model<?, ?, ?, ?> model;

    public FieldsTypesModel(Model<?, ?, ?, ?> aModel) {
        super();
        model = aModel;
    }

    @Override
    public int getSize() {
        try {
            if (model != null) {
                if (sqlTypes == null) {
                    Set<Integer> col = RowsetUtils.typesNames.keySet();
                    if (col != null) {
                        List<Integer> tV = new ArrayList<>();
                        for (Integer t : col) {
                            if (model.isTypeSupported(t)) {
                                tV.add(t);
                            }
                        }
                        sqlTypes = tV.toArray(new Integer[]{});
                    }
                }
                return sqlTypes.length;
            } else {
                return 0;
            }
        } catch (Exception ex) {
            Logger.getLogger(FieldsView.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public Integer getElementAt(int index) {
        if (index >= 0 && index < sqlTypes.length) {
            return sqlTypes[index];
        }
        return null;
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selectedItem = (Integer) anItem;
    }

    @Override
    public Integer getSelectedItem() {
        return selectedItem;
    }

    public void fireDataChanged() {
        for (ListDataListener l : listeners) {
            if (l != null) {
                l.contentsChanged(new ListDataEvent(FieldsTypesModel.this, ListDataEvent.CONTENTS_CHANGED, 0, getSize() - 1));
            }
        }
    }
    
}
