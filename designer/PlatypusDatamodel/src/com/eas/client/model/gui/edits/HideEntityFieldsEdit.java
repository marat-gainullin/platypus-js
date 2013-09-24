/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.model.gui.edits;

import com.eas.client.model.Entity;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author mg
 */
public class HideEntityFieldsEdit<E extends Entity<?, ?, E>> extends DatamodelEdit {

    protected E entity;

    public HideEntityFieldsEdit() {
        super();
    }

    public HideEntityFieldsEdit(E aEntity) {
        this();
        entity = aEntity;
    }

    @Override
    public boolean isNeedConnectors2Reroute() {
        return true;
    }

    @Override
    protected void redoWork() {
        boolean oldValue = entity.isIconified();
        PropertyChangeListener[] listeners = entity.getChangeSupport().getPropertyChangeListeners();
        for (PropertyChangeListener l : listeners) {
            entity.getChangeSupport().removePropertyChangeListener(l);
        }
        try {
            entity.setIconified(true);
        } finally {
            for (PropertyChangeListener l : listeners) {
                entity.getChangeSupport().addPropertyChangeListener(l);
            }
            PropertyChangeEvent event = new PropertyChangeEvent(this, "iconified", oldValue, entity.isIconified());
            for (PropertyChangeListener l : listeners) {
                l.propertyChange(event);
            }
        }
    }

    @Override
    protected void undoWork() {
        boolean oldValue = entity.isIconified();
        PropertyChangeListener[] listeners = entity.getChangeSupport().getPropertyChangeListeners();
        for (PropertyChangeListener l : listeners) {
            entity.getChangeSupport().removePropertyChangeListener(l);
        }
        try {
            entity.setIconified(false);
        } finally {
            for (PropertyChangeListener l : listeners) {
                entity.getChangeSupport().addPropertyChangeListener(l);
            }
            PropertyChangeEvent event = new PropertyChangeEvent(this, "iconified", oldValue, entity.isIconified());
            for (PropertyChangeListener l : listeners) {
                l.propertyChange(event);
            }
        }
    }
}
