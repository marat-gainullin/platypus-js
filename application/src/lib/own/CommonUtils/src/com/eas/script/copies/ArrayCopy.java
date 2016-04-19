/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script.copies;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author mg
 */
public class ArrayCopy extends LinkedList<Object> {

    protected Object key = new Object();
    protected Map<String, Object> props = new TreeMap<>();

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ArrayCopy) {
            ArrayCopy other = (ArrayCopy) o;
            return other.key == key;
        } else {
            return false;
        }
    }

    public Object get(String k) {
        return props.get(k);
    }

    public Object put(String k, Object v) {
        return props.put(k, v);
    }

    public Set<String> keySet() {
        return props.keySet();
    }
}
