/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.ordering;

import com.eas.script.HasPublished;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Predicate;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 * @param <T>
 */
public class ObservableLinkedHashSet<T extends HasPublished> extends LinkedHashSet<T> {

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
            lengthChanged(oldLength);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        int oldLength = super.size();
        boolean res = super.removeAll(c);
        lengthChanged(oldLength);
        return res;
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        int oldLength = super.size();
        if (super.removeIf(filter)) {
            lengthChanged(oldLength);
            return true;
        } else {
            return false;
        }
    }

    protected void lengthChanged(int oldLength) {
        if (tag != null) {
            JSObject jsArray = (JSObject) ScriptUtils.wrap(tag);
            JSObject splice = (JSObject) jsArray.getMember("splice");
            List<Object> args = new ArrayList<>();
            args.add(0);
            args.add(JSType.toNumber(jsArray.getMember("length")));
            stream().forEach((T r) -> {
                JSObject jsRow = r.getPublished();
                args.add(ScriptUtils.unwrap(jsRow));
            });
            splice.call(tag, args.toArray());
        }
        propertyChangeSupport.firePropertyChange("length", oldLength, super.size());
    }

    @Override
    public boolean add(T e) {
        int oldLength = super.size();
        if (super.add(e)) {
            lengthChanged(oldLength);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        int oldLength = super.size();
        if (super.addAll(c)) {
            lengthChanged(oldLength);
            return true;
        } else {
            return false;
        }
    }

}
