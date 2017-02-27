/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @param <K>
 * @param <V>
 * @author pk
 */
public class ResultTable<K, V> extends HashMap<K, V> {

    private Map<K, Integer> errorCodes = new HashMap<>();

    public ResultTable(Map<? extends K, ? extends V> m) {
        super(m);
    }

    public ResultTable() {
    }

    public ResultTable(int initialCapacity) {
        throw new UnsupportedOperationException();
    }

    public ResultTable(int initialCapacity, float loadFactor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V put(K key, V value) {
        final Collection<V> values = this.values();
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    public void put(K key, V value, int errorCode) {
        errorCodes.put(key, errorCode);
        super.put(key, value);
    }

    public Integer errorCode(K key) {
        return errorCodes.get(key);
    }
}
