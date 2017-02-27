/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script.copies;

import java.util.TreeMap;

/**
 *
 * @author mg
 */
public class ObjectCopy extends TreeMap<String, Object>{

    protected Object key = new Object();
    
    public ObjectCopy() {
        super();
    }
    
    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ObjectCopy) {
            ObjectCopy other = (ObjectCopy) o;
            return other.key == key;
        } else {
            return false;
        }
    }

}
