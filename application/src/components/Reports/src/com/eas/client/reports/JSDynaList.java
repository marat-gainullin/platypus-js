/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import java.util.Collection;
import java.util.Iterator;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author Andrew
 */
public class JSDynaList implements Collection {

    private final JSObject delegate;
    private final int timezoneOffset;
    private final int length;

    public JSDynaList(JSObject aDelegate, int aTimezoneOffset) {
        super();
        if (aDelegate != null) {
            delegate = aDelegate;
            length = JSType.toInteger(delegate.getMember("length"));
            timezoneOffset = aTimezoneOffset;
        } else {
            throw new IllegalArgumentException("A Delegate could not be null.");
        }
    }

    public Object get(int aIndex){
        return JSDynaBean.wrap(delegate.getSlot(aIndex), timezoneOffset);
    }
    
    @Override
    public int size() {
        return length;
    }

    @Override
    public boolean isEmpty() {
        return length == 0;
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Iterator iterator() {
        return new JSDynaListIterator(delegate, timezoneOffset, length);
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object[] toArray(Object[] a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean add(Object e) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean containsAll(Collection c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported.");
    }

}
