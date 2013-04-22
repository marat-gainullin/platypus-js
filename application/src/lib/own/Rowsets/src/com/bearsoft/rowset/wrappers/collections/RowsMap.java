/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.wrappers.collections;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.RowsetException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Rows descendants map implementation. The primary keys values cortege is used
 * as a key in this map and row/row descendants as values.
 * @author Gala
 */
public class RowsMap<T extends Row> extends RowsCollection implements Map<Object[], T> {

    public RowsMap(Rowset aRowset) {
        super(aRowset);
    }

    public boolean containsKey(Object oKey) {
        try {
            Object[] key = checkKey(oKey);
            return locator.find(key);
        } catch (RowsetException ex) {
            Logger.getLogger(RowsMap.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean containsValue(Object oValue) {
        if (oValue instanceof Row) {
            try {
                Row value = (Row) oValue;
                Object[] key = getKey(value);
                return locator.find(key);
            } catch (Exception ex) {
                Logger.getLogger(RowsMap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public T get(Object oKey) {
        try {
            Object[] key = checkKey(oKey);
            if (locator.find(key)) {
                return (T) locator.getRow(0);
            } else {
                return null;
            }
        } catch (RowsetException ex) {
            Logger.getLogger(RowsMap.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public T put(Object[] key, Row value) {
        throw new UnsupportedOperationException("Not supported yet.");
        // TODO: implement put in similar way as set in rowsList
    }

    public T remove(Object oKey) {
        try {
            Object[] key = checkKey(oKey);
            if (locator.find(key) && locator.first()) {
                Row row = rowset.getCurrentRow();
                rowset.delete();
                return (T) row;
            } else {
                return null;
            }
        } catch (Exception ex) {
            Logger.getLogger(RowsMap.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void putAll(Map<? extends Object[], ? extends T> toPut) {
        for (Entry<? extends Object[], ? extends Row> entry : toPut.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public Set<Object[]> keySet() {
        try {
            Set<Object[]> keys = new HashSet<>();
            for (Row row : locator.getAllRowsVector()) {
                keys.add(getKey(row));
            }
            return keys;
        } catch (Exception ex) {
            Logger.getLogger(RowsMap.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Collection<T> values() {
        return (Collection<T>) rowset.getCurrent();
    }

    public Set<Entry<Object[], T>> entrySet() {
        try {
            Set<Entry<Object[], T>> entries = new HashSet<>();
            for (Row row : locator.getAllRowsVector()) {
                entries.add(new SimpleEntry<>(getKey(row), (T) row));
            }
            return entries;
        } catch (Exception ex) {
            Logger.getLogger(RowsMap.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
