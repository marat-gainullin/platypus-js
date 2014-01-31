/* Datamodel license.
 * Exclusive rights on this code in any form are belong to it's author. This code was
 * developed for commercial purposes only. For any questions and any actions with this
 * code in any form you have to contact to it's author.
 * All rights reserved.
 */
package com.eas.client.cache;

import com.eas.client.CacheListener;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author mg
 */
public abstract class FreqCache<K, V> {

    protected class CacheEntry {

        protected K key;
        protected V value;
        protected int getCounter;

        public CacheEntry(K aKey, V aValue) {
            key = transformKey(aKey);
            value = aValue;
        }
    }
    protected final Object lock = new Object();
    protected Map<K, CacheEntry> entries = new HashMap<>();
    private CachingSupport<K> cachingSupport = new CachingSupport<>();
    protected int MAX_CACHE_SIZE = 512;
    protected int SIZE_TO_SHRINK = MAX_CACHE_SIZE / 2;

    public FreqCache() {
        super();
    }

    public int getMaxCacheSize() {
        return MAX_CACHE_SIZE;
    }

    public void setMaxCacheSize(int aNewSize) {
        if (aNewSize >= 2) {
            MAX_CACHE_SIZE = aNewSize;
            SIZE_TO_SHRINK = MAX_CACHE_SIZE / 2;
        }
    }

    protected final void shrink() throws Exception {
        if (entries.size() > MAX_CACHE_SIZE) {
            SortedMap<Integer, CacheEntry> sorter = new TreeMap<>();
            for (CacheEntry entry : entries.values()) {
                // There might be same get counters for different entries
                while (sorter.containsKey(entry.getCounter)) {
                    entry.getCounter++;
                }
                assert !sorter.containsKey(entry.getCounter);
                sorter.put(entry.getCounter, entry);
                entry.getCounter = 0; // Let's equalize getting counters
            }
            assert sorter.size() == entries.size();
            while (sorter.size() > SIZE_TO_SHRINK) {
                Integer sorterKey = sorter.firstKey();
                CacheEntry entry = sorter.get(sorterKey);
                sorter.remove(sorterKey);
                entries.remove(entry.key);
                cachingSupport.fireRemoved(entry.key);
            }
        }
    }

    public void addListener(CacheListener<K> aListener) throws Exception {
        cachingSupport.addListener(aListener);
    }

    public void removeListener(CacheListener<K> aListener) throws Exception {
        cachingSupport.removeListener(aListener);
    }

    /**
     * Test if application element with specified identifier exists in this cahe
     * in memory.
     *
     * @param aKey Identifier of application element to test.
     * @return True if application element with specified identifier is present.
     * @throws Exception
     */
    public boolean containsKey(K aKey) throws Exception {
        synchronized (lock) {
            aKey = transformKey(aKey);
            return entries.containsKey(aKey);
        }
    }

    protected void putEntry(CacheEntry aEntry) {
        aEntry.getCounter++;
        entries.put(aEntry.key, aEntry);
    }

    protected K transformKey(K aKey) {
        return aKey;
    }

    public V get(K aKey) throws Exception {
        K aId = transformKey(aKey);
        V element = null;
        boolean contains;
        synchronized (lock) {
            contains = entries.containsKey(aId);
            if (contains) {
                element = entries.get(aId).value;
            }
        }

        if (!contains) {
            element = getNewEntry(aKey); // Get it from somewhere
        }

        synchronized (lock) {
            if (entries.containsKey(aId)) {
                CacheEntry entry = entries.get(aId);
                entry.getCounter++;
                element = entry.value; // Probably it's not nessasary
            } else if(element != null){
                putEntry(new CacheEntry(aId, element));
            }
            shrink();
            return element;
        }
    }

    public void put(K aKey, V aValue) throws Exception {
        synchronized (lock) {
            if (aValue != null) {
                aKey = transformKey(aKey);
                entries.put(aKey, new CacheEntry(aKey, aValue));
                cachingSupport.fireAdded(aKey);
            }
        }
    }

    public void remove(K aKey) throws Exception {
        synchronized (lock) {
            if (aKey != null) {
                aKey = transformKey(aKey);
                entries.remove(aKey);
                cachingSupport.fireRemoved(aKey);
            }
        }
    }

    public void clear() throws Exception {
        synchronized (lock) {
            if (entries != null) {
                entries.clear();
            }
            cachingSupport.fireCleared();
        }
    }

    /**
     * Gets a new entry from somewhere. Descendants take care about it.
     * Attention! Warning! This function is not synchronized!!! Descendants shound
     * synchronize any this cache operations by 'lock' object synchronization!
     */
    protected abstract V getNewEntry(K aId) throws Exception;
}
