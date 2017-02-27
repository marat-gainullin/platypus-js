/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util;

import java.util.Collection;

/**
 *
 * @author mg
 */
public interface CollectionListener<C, V> {

    public void added(C aCollection, V added);

    public void added(C aCollection, Collection<V> added);

    public void removed(C aCollection, V removed);

    public void removed(C aCollection, Collection<V> added);

    public void reodered(C aCollection);
    
    public void cleared(C aCollection);
}
