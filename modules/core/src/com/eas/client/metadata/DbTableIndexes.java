/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.metadata;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mg
 */
public class DbTableIndexes {

    protected Map<String, DbTableIndexSpec> indexes = new HashMap<>();

    public DbTableIndexes() {
        super();
    }

    public DbTableIndexes(DbTableIndexes aSource) {
        super();
        assert aSource != null;
        Map<String, DbTableIndexSpec> sourceIndexes = aSource.getIndexes();
        sourceIndexes.keySet().stream().forEach((idxName) -> {
            indexes.put(idxName, sourceIndexes.get(idxName).copy());
        });
    }

    public Map<String, DbTableIndexSpec> getIndexes() {
        return indexes;
    }

    public void sortIndexesColumns() {
        indexes.values().stream().forEach((index) -> {
            index.sortColumns();
        });
    }

    public DbTableIndexes copy() {
        return new DbTableIndexes(this);
    }
}
