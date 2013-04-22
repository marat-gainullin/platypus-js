/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.client.CacheListener;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 */
public class CachingSupport<K> {

    protected Set<CacheListener> easListeners = new HashSet<>();

    public void addListener(CacheListener aListener) throws Exception {
        easListeners.add(aListener);
    }

    public void removeListener(CacheListener aListener) throws Exception {
        easListeners.remove(aListener);
    }

    public void fireAdded(K aId) throws Exception  {
        for (CacheListener listener : easListeners) {
            listener.added(aId);
        }
    }

    public void fireRemoved(K aId) throws Exception  {
        for (CacheListener listener : easListeners) {
            listener.removed(aId);
        }
    }

    public void fireCleared() throws Exception  {
        for (CacheListener listener : easListeners) {
            listener.cleared();
        }
    }
}
