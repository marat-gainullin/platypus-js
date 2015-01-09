/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.ordering;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Predicate;

/**
 *
 * @author mg
 * @param <T>
 */
public class ObservableLinkedHashSet<T> extends LinkedHashSet<T> {

    public Object tag;

    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener aListener) {
        propertyChangeSupport.addPropertyChangeListener(aListener);
    }

    public void addPropertyChangeListener(String aPropertyName, PropertyChangeListener aListener) {
        propertyChangeSupport.addPropertyChangeListener(aPropertyName, aListener);
    }

    public void removePropertyChangeListener(PropertyChangeListener aListener) {
        propertyChangeSupport.removePropertyChangeListener(aListener);
    }

    public void removePropertyChangeListener(String aPropertyName, PropertyChangeListener aListener) {
        propertyChangeSupport.removePropertyChangeListener(aPropertyName, aListener);
    }

    @Override
    public boolean remove(Object o) {
        int oldLength = super.size();
        if (super.remove(o)) {
            propertyChangeSupport.firePropertyChange("length", oldLength, super.size());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        int oldLength = super.size();
        boolean res = super.removeAll(c);
        propertyChangeSupport.firePropertyChange("length", oldLength, super.size());
        return res;
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        int oldLength = super.size();
        if (super.removeIf(filter)) {
            propertyChangeSupport.firePropertyChange("length", oldLength, super.size());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean add(T e) {
        int oldLength = super.size();
        if (super.add(e)) {
            propertyChangeSupport.firePropertyChange("length", oldLength, super.size());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        int oldLength = super.size();
        if (super.addAll(c)) {
            propertyChangeSupport.firePropertyChange("length", oldLength, super.size());
            return true;
        } else {
            return false;
        }
    }

}
