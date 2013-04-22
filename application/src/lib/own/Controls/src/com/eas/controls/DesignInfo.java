/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls;

import com.eas.design.Undesignable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for design info of all data aware controls across platypus platform.
 * DesignInfo and it's descandants fire property change events while setting new values to them.
 * In the case of assigning new content to design info or to it's descendant, "all" property
 * change event will be fired. The special "all" property name is used for outliene that all
 * properties have been changed. The designers and other listeners of property change events
 * should use the "all" property name to perform entire update of their's forms, views etc.
 * @author mg
 */
public abstract class DesignInfo {

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    protected DesignInfo() {
        super();
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (propertyChangeSupport != null) {
            propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (propertyChangeSupport != null) {
            propertyChangeSupport.removePropertyChangeListener(listener);
        }
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (propertyChangeSupport != null) {
            propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (propertyChangeSupport != null) {
            propertyChangeSupport.addPropertyChangeListener(listener);
        }
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (propertyChangeSupport != null) {
            try {
                propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
            } catch (Exception ex) {
                Logger.getLogger(DesignInfo.class.getName()).severe(ex.getMessage());
            }
        }
    }

    @Undesignable
    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    public void setPropertyChangeSupport(PropertyChangeSupport aChangeSupport) {
        propertyChangeSupport = aChangeSupport;
    }

    public void clearPropertyListeners() {
        PropertyChangeListener[] listeners = propertyChangeSupport.getPropertyChangeListeners();
        for (PropertyChangeListener l : listeners) {
            propertyChangeSupport.removePropertyChangeListener(l);
        }
    }

    public abstract boolean isEqual(Object obj);

    public DesignInfo copy() {
        DesignInfo lInfo = null;
        try {
            lInfo = getClass().newInstance();
            lInfo.assign(this);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(DesignInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lInfo;
    }

    public abstract void assign(DesignInfo aSource);
}
