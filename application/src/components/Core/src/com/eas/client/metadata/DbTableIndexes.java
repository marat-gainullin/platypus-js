/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.metadata;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.utils.CollectionEditingSupport;
import com.eas.client.ClientConstants;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mg
 */
public class DbTableIndexes {

    protected Map<String, DbTableIndexSpec> indexes = new HashMap<>();
    protected CollectionEditingSupport<DbTableIndexes, DbTableIndexSpec> collectionSupport = new CollectionEditingSupport<>(this);

    public DbTableIndexes()
    {
        super();
    }
    
    public DbTableIndexes(DbTableIndexes aSource)
    {
        super();
        assert aSource != null;
        Map<String, DbTableIndexSpec> sourceIndexes = aSource.getIndexes();
        for(String idxName:sourceIndexes.keySet())
        {
            indexes.put(new String(idxName.toCharArray()), sourceIndexes.get(idxName).copy());
        }
    }

    public Map<String, DbTableIndexSpec> getIndexes() {
        return indexes;
    }

    public void addIndexByDsRow(Row aRow) throws InvalidColIndexException, InvalidCursorPositionException {
        Object oIdxName = aRow.getColumnObject(aRow.getFields().find(ClientConstants.JDBCIDX_INDEX_NAME));
        if (oIdxName != null && oIdxName instanceof String) {
            String idxName = (String) oIdxName;
            DbTableIndexSpec idxSpec = indexes.get(idxName);
            if (idxSpec == null) {
                idxSpec = new DbTableIndexSpec();
                idxSpec.setName(idxName);
                indexes.put(idxName, idxSpec);
                collectionSupport.fireElementAdded(idxSpec);
            }
            Object oNonUnique = aRow.getColumnObject(aRow.getFields().find(ClientConstants.JDBCIDX_NON_UNIQUE));
            if (oNonUnique != null) {
                boolean isUnique = false;
                if (oNonUnique instanceof Number) {
                    isUnique = !(((Number) oNonUnique).intValue() != 0);
                }
                idxSpec.setUnique(isUnique);
            }
            Object oType = aRow.getColumnObject(aRow.getFields().find(ClientConstants.JDBCIDX_TYPE));
            if (oType != null) {
                if (oType instanceof Number) {
                    short type = ((Number) oType).shortValue();
                    idxSpec.setClustered(false);
                    idxSpec.setHashed(false);
                    switch (type) {
                        case DatabaseMetaData.tableIndexClustered:
                            idxSpec.setClustered(true);
                            break;
                        case DatabaseMetaData.tableIndexHashed:
                            idxSpec.setHashed(true);
                            break;
                        case DatabaseMetaData.tableIndexStatistic:
                            break;
                        case DatabaseMetaData.tableIndexOther:
                            break;
                    }
                }
            }
            Object oColumnName = aRow.getColumnObject(aRow.getFields().find(ClientConstants.JDBCIDX_COLUMN_NAME));
            if (oColumnName != null && oColumnName instanceof String) {
                String sColumnName = (String) oColumnName;
                DbTableIndexColumnSpec column = idxSpec.getColumn(sColumnName);
                if (column == null) {
                    column = new DbTableIndexColumnSpec(sColumnName, true);
                    idxSpec.addColumn(column);
                }
                Object oAsc = aRow.getColumnObject(aRow.getFields().find(ClientConstants.JDBCIDX_ASC_OR_DESC));
                if (oAsc != null && oAsc instanceof String) {
                    String sAsc = (String) oAsc;
                    column.setAscending(sAsc.toLowerCase().equals("a"));
                }
                Object oPosition = aRow.getColumnObject(aRow.getFields().find(ClientConstants.JDBCIDX_ORDINAL_POSITION));
                if (oPosition != null && oPosition instanceof Number) {
                    column.setOrdinalPosition((int)((Number)oPosition).shortValue());
                }
            }
            //???
            Object oPKey = aRow.getColumnObject(aRow.getFields().find(ClientConstants.JDBCIDX_PRIMARY_KEY));
            if (oPKey != null) {
                boolean isPKey = false;
                if (oPKey instanceof Number) {
                    isPKey = !(((Number) oPKey).intValue() != 0);
                }
                idxSpec.setPKey(isPKey);
            }
            //???
            Object oFKeyName = aRow.getColumnObject(aRow.getFields().find(ClientConstants.JDBCIDX_FOREIGN_KEY));
            if (oFKeyName != null && oFKeyName instanceof String) {
                String fKeyName = (String) oFKeyName;
                idxSpec.setFKeyName(fKeyName);
            }
        }
    }

    public void sortIndexesColumns()
    {
        for(DbTableIndexSpec index:indexes.values())
            index.sortColumns();
    }

    public DbTableIndexes copy()
    {
        return new DbTableIndexes(this);
    }
}
