/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.beans;

import java.util.HashSet;
import java.util.Set;

import com.bearsoft.rowset.Rowset;

/**
 * 
 * @author mg
 */
public class PropertyChangeSupport {

	protected Object source;
	protected Set<PropertyChangeListener> listeners = new HashSet<>();

	public PropertyChangeSupport(Object aSource) {
		source = aSource;
	}

	public PropertyChangeListener[] getVetoableChangeListeners() {
		return listeners.toArray(new PropertyChangeListener[] {});
	}

	public void addPropertyChangeListener(PropertyChangeListener vl) {
		listeners.add(vl);
	}

	public void removePropertyChangeListener(PropertyChangeListener vl) {
		listeners.remove(vl);
	}

	public void firePropertyChange(PropertyChangeEvent event) {
		for (PropertyChangeListener l : listeners.toArray(new PropertyChangeListener[]{}))
			l.propertyChange(event);
	}

	public void firePropertyChange(String aPropertyName, boolean oldValue, boolean newValue) {
		firePropertyChange(new PropertyChangeEvent(source, aPropertyName, oldValue, newValue));
	}

	public void firePropertyChange(String aPropertyName, Rowset oldValue, Rowset newValue) {
		firePropertyChange(new PropertyChangeEvent(source, aPropertyName, oldValue, newValue));
	}

	public void firePropertyChange(String aPropertyName, String oldValue, String newValue) {
		firePropertyChange(new PropertyChangeEvent(source, aPropertyName, oldValue, newValue));
	}
}
