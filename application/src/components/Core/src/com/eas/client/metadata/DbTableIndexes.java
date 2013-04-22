/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.metadata;

import com.bearsoft.rowset.Rowset;
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

    public void addIndexByDsRow(Rowset indexesRs) throws InvalidColIndexException, InvalidCursorPositionException {
        Object oIdxName = indexesRs.getObject(indexesRs.getFields().find(ClientConstants.JDBCIDX_INDEX_NAME));
        if (oIdxName != null && oIdxName instanceof String) {
            String idxName = (String) oIdxName;
            DbTableIndexSpec idxSpec = indexes.get(idxName);
            if (idxSpec == null) {
                idxSpec = new DbTableIndexSpec();
                idxSpec.setName(idxName);
                indexes.put(idxName, idxSpec);
                collectionSupport.fireElementAdded(idxSpec);
            }
            Object oNonUnique = indexesRs.getObject(indexesRs.getFields().find(ClientConstants.JDBCIDX_NON_UNIQUE));
            if (oNonUnique != null) {
                boolean isUnique = false;
                if (oNonUnique instanceof Number) {
                    isUnique = !(((Number) oNonUnique).intValue() != 0);
                }
                idxSpec.setUnique(isUnique);
            }
            Object oType = indexesRs.getObject(indexesRs.getFields().find(ClientConstants.JDBCIDX_TYPE));
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
            Object oColumnName = indexesRs.getObject(indexesRs.getFields().find(ClientConstants.JDBCIDX_COLUMN_NAME));
            if (oColumnName != null && oColumnName instanceof String) {
                String sColumnName = (String) oColumnName;
                DbTableIndexColumnSpec column = idxSpec.getColumn(sColumnName);
                if (column == null) {
                    column = new DbTableIndexColumnSpec(sColumnName, true);
                    idxSpec.addColumn(column);
                }
                Object oAsc = indexesRs.getObject(indexesRs.getFields().find(ClientConstants.JDBCIDX_ASC_OR_DESC));
                if (oAsc != null && oAsc instanceof String) {
                    String sAsc = (String) oAsc;
                    column.setAscending(sAsc.toLowerCase().equals("a"));
                }
                Object oPosition = indexesRs.getObject(indexesRs.getFields().find(ClientConstants.JDBCIDX_ORDINAL_POSITION));
                if (oPosition != null && oPosition instanceof Number) {
                    column.setOrdinalPosition((int)((Number)oPosition).shortValue());
                }
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
