/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.customizer;

import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.datamodel.ApplicationModel;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author mg
 */
public class DsParametersComboModel implements ComboBoxModel {

    protected ApplicationModel dm = null;
    protected Long entityID = null;
    protected Parameters parameters = null;
    protected Parameter selected = null;
    protected Set<ListDataListener> listeners = new HashSet<>();

    public DsParametersComboModel() {
        super();
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(ApplicationModel aDm, Long aEntityID, Parameters aParameters) {
        dm = aDm;
        entityID = aEntityID;
        parameters = aParameters;
        selected = null;
        fireDataChanged();
    }

    public ApplicationModel getDatamodel() {
        return dm;
    }

    public Long getEntityID() {
        return entityID;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if (anItem == null || anItem instanceof Parameter) {
            selected = (Parameter) anItem;
        }
    }

    @Override
    public Object getSelectedItem() {
        if (parameters != null) {
            return selected;
        }
        return null;
    }

    @Override
    public int getSize() {
        if (parameters != null) {
            return parameters.getParametersCount();
        }
        return 0;
    }

    @Override
    public Object getElementAt(int index) {
        if (parameters != null && index >= 0 && index < parameters.getParametersCount()) {
            return parameters.get(index + 1);
        }
        return null;
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    protected void fireDataChanged() {
        ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize() - 1);
        for (ListDataListener l : listeners) {
            l.contentsChanged(event);
        }
    }
}
