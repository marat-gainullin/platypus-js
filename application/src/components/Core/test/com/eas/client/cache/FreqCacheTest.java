/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class FreqCacheTest {

    protected class IntFreqCache extends FreqCache<Integer, String> {

        @Override
        protected String getNewEntry(Integer aId) throws Exception {
            return "cachedElement_" + String.valueOf(aId);
        }
    }

    @Test
    public void equalsCountersShrinkTest() throws Exception {
        IntFreqCache cache = new IntFreqCache();
        cache.setMaxCacheSize(3);
        String element = cache.get(78);
        element = cache.get(79);
        element = cache.get(80);
        assertEquals(3, cache.entries.size());
        element = cache.get(81);
        assertEquals(1, cache.entries.size());
        element = cache.get(82);
        assertEquals(2, cache.entries.size());
        // all elements have same get counter values
    }

    @Test
    public void differentCountersShrinkTest() throws Exception {
        IntFreqCache cache = new IntFreqCache();
        cache.setMaxCacheSize(3);
        String element = cache.get(78);
        for (int i = 0; i < 10; i++) {
            element = cache.get(79);
        }
        element = cache.get(80);
        assertEquals(3, cache.entries.size());
        element = cache.get(81);
        assertEquals(1, cache.entries.size());
        assertTrue(cache.entries.containsKey(79));
        assertFalse(cache.entries.containsKey(81));
        element = cache.get(82);
        assertEquals(2, cache.entries.size());
        element = cache.get(83);
        assertEquals(3, cache.entries.size());
        element = cache.get(84);
        assertEquals(1, cache.entries.size());
        assertFalse(cache.entries.containsKey(79));

        for (int i = 0; i < 10; i++) {
            element = cache.get(81);
        }
        assertEquals(2, cache.entries.size());
        assertFalse(cache.entries.containsKey(79));
        element = cache.get(105);
        element = cache.get(106);// it will shrink the cache
        assertTrue(cache.entries.containsKey(81));// but 81 element will stay in the cache, because of high get counter
        element = cache.get(107);
        element = cache.get(108);
        element = cache.get(109);// it will shrink the cache again
        assertFalse(cache.entries.containsKey(81));// and 81 element will not stay in the cache, because previous shrink have
        // equalize all get counters
        // all elements have same get counter values
    }
}
