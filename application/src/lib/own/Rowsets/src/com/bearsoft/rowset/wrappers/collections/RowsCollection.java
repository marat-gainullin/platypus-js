/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.wrappers.collections;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.ordering.Locator;
import com.bearsoft.rowset.metadata.Field;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for rowset collections api implementation (List and Map).
 * @author Gala
 */
public class RowsCollection {

    protected Rowset rowset;
    protected Locator locator;

    public RowsCollection(Rowset aRowset) {
        super();
        rowset = aRowset;
        initializeLocator();
    }

    public int size() {
        return rowset.size();
    }

    public boolean isEmpty() {
        return rowset.isEmpty();
    }

    public void clear() {
        try {
            rowset.deleteAll();
        } catch (RowsetException ex) {
            Logger.getLogger(RowsList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initializeLocator() throws IllegalStateException {
        locator = rowset.createLocator();
        List<Field> pks = rowset.getFields().getPrimaryKeys();
        locator.beginConstrainting();
        try {
            for (Field f : pks) {
                locator.addConstraint(rowset.getFields().find(f.getName()));
            }
        } finally {
            locator.endConstrainting();
        }
    }

    protected Object[] checkKey(Object oKey) {
        Object[] key = null;
        if (key instanceof Object[]) {
            key = new Object[]{key};
        } else {
            key = (Object[]) oKey;
        }
        assert locator.getFields().size() == key.length : "Key must be array of same length as primary keys cortege. If cortege size is single value, than key may be non-array.";
        return key;
    }

    protected Object[] getKey(Row value) throws Exception {
        Object[] key = new Object[locator.getFields().size()];
        for (int i = 0; i < key.length; i++) {
            key[i] = value.getColumnObject(locator.getFields().get(i));
        }
        return key;
    }

    public Rowset unwrap()
    {
        return rowset;
    }
}
