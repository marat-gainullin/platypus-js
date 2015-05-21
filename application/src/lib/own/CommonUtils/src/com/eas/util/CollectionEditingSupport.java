/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class CollectionEditingSupport<C, V> {

    protected C collection;
    protected Set<CollectionListener<C, V>> listeners = new HashSet<>();

    public CollectionEditingSupport(C aCollection) {
        super();
        collection = aCollection;
    }

    public void addListener(CollectionListener aListener) {
        listeners.add(aListener);
    }

    public boolean removeListener(CollectionListener aListener) {
        return listeners.remove(aListener);
    }

    public void fireElementAdded(V element) {
        try {
            for (CollectionListener l : listeners) {
                l.added(collection, element);
            }
        } catch (Exception ex) {
            Logger.getLogger(CollectionEditingSupport.class.getName()).log(Level.SEVERE, "While firering an event \"fireElementAdded\"", ex);
        }
    }

    public void fireElementsAdded(Collection<V> elements) {
        try {
            for (CollectionListener l : listeners) {
                l.added(collection, elements);
            }
        } catch (Exception ex) {
            Logger.getLogger(CollectionEditingSupport.class.getName()).log(Level.SEVERE, "While firering an event \"fireElementsAdded\"", ex);
        }
    }

    public void fireElementRemoved(V element) {
        try {
            for (CollectionListener l : listeners) {
                l.removed(collection, element);
            }
        } catch (Exception ex) {
            Logger.getLogger(CollectionEditingSupport.class.getName()).log(Level.SEVERE, "While firering an event \"fireElementRemoved\"", ex);
        }
    }

    public void fireElementsRemoved(Collection<V> elements) {
        try {
            for (CollectionListener l : listeners) {
                l.removed(collection, elements);
            }
        } catch (Exception ex) {
            Logger.getLogger(CollectionEditingSupport.class.getName()).log(Level.SEVERE, "While firering an event \"fireElementsRemoved\"", ex);
        }
    }

    public void fireCleared() {
        try {
            for (CollectionListener l : listeners) {
                l.cleared(collection);
            }
        } catch (Exception ex) {
            Logger.getLogger(CollectionEditingSupport.class.getName()).log(Level.SEVERE, "While firering an event \"fireCleared\"", ex);
        }
    }
    
    public void fireReodered() {
        try {
            for (CollectionListener l : listeners) {
                l.reodered(collection);
            }
        } catch (Exception ex) {
            Logger.getLogger(CollectionEditingSupport.class.getName()).log(Level.SEVERE, "While firering an event \"fireReodered\"", ex);
        }
    }
}
