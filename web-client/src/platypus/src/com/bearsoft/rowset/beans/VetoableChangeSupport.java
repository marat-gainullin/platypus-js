/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.beans;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 */
public class VetoableChangeSupport {

    protected Object source;
    protected Set<VetoableChangeListener> listeners = new HashSet<>();

    public VetoableChangeSupport(Object aSource) {
        source = aSource;
    }

    public VetoableChangeListener[] getVetoableChangeListeners() {
        return listeners.toArray(new VetoableChangeListener[]{});
    }

    public void addVetoableChangeListener(VetoableChangeListener vl) {
        listeners.add(vl);
    }
    
    public void removeVetoableChangeListener(VetoableChangeListener vl) {
        listeners.remove(vl);
    }
}
