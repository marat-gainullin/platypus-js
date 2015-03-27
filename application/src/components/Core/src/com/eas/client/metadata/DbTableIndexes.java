/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.metadata;

import com.eas.client.ClientConstants;
import com.eas.client.dataflow.ColumnsIndicies;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        for (String idxName : sourceIndexes.keySet()) {
            indexes.put(new String(idxName.toCharArray()), sourceIndexes.get(idxName).copy());
        }
    }

    public Map<String, DbTableIndexSpec> getIndexes() {
        return indexes;
    }

    public void readIndices(ResultSet r) throws SQLException {
        ColumnsIndicies idxs = new ColumnsIndicies(r.getMetaData());
        int JDBCIDX_INDEX_NAME = idxs.find(ClientConstants.JDBCIDX_INDEX_NAME);
        int JDBCIDX_NON_UNIQUE = idxs.find(ClientConstants.JDBCIDX_NON_UNIQUE);
        int JDBCIDX_TYPE = idxs.find(ClientConstants.JDBCIDX_TYPE);
        int JDBCIDX_COLUMN_NAME = idxs.find(ClientConstants.JDBCIDX_COLUMN_NAME);
        int JDBCIDX_ASC_OR_DESC = idxs.find(ClientConstants.JDBCIDX_ASC_OR_DESC);
        int JDBCIDX_ORDINAL_POSITION = idxs.find(ClientConstants.JDBCIDX_ORDINAL_POSITION);
        int JDBCIDX_PRIMARY_KEY = idxs.find(ClientConstants.JDBCIDX_PRIMARY_KEY);
        int JDBCIDX_FOREIGN_KEY = idxs.find(ClientConstants.JDBCIDX_FOREIGN_KEY);
        while (r.next()) {
            Object oIdxName = r.getObject(JDBCIDX_INDEX_NAME);
            if (oIdxName != null && oIdxName instanceof String) {
                String idxName = (String) oIdxName;
                DbTableIndexSpec idxSpec = indexes.get(idxName);
                if (idxSpec == null) {
                    idxSpec = new DbTableIndexSpec();
                    idxSpec.setName(idxName);
                    indexes.put(idxName, idxSpec);
                }
                Object oNonUnique = r.getObject(JDBCIDX_NON_UNIQUE);
                if (oNonUnique != null) {
                    boolean isUnique = false;
                    if (oNonUnique instanceof Number) {
                        isUnique = !(((Number) oNonUnique).intValue() != 0);
                    }
                    idxSpec.setUnique(isUnique);
                }
                Object oType = r.getObject(JDBCIDX_TYPE);
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
                Object oColumnName = r.getObject(JDBCIDX_COLUMN_NAME);
                if (oColumnName != null && oColumnName instanceof String) {
                    String sColumnName = (String) oColumnName;
                    DbTableIndexColumnSpec column = idxSpec.getColumn(sColumnName);
                    if (column == null) {
                        column = new DbTableIndexColumnSpec(sColumnName, true);
                        idxSpec.addColumn(column);
                    }
                    Object oAsc = r.getObject(JDBCIDX_ASC_OR_DESC);
                    if (oAsc != null && oAsc instanceof String) {
                        String sAsc = (String) oAsc;
                        column.setAscending(sAsc.toLowerCase().equals("a"));
                    }
                    Object oPosition = r.getObject(JDBCIDX_ORDINAL_POSITION);
                    if (oPosition != null && oPosition instanceof Number) {
                        column.setOrdinalPosition((int) ((Number) oPosition).shortValue());
                    }
                }
                //???
                Object oPKey = r.getObject(JDBCIDX_PRIMARY_KEY);
                if (oPKey != null) {
                    boolean isPKey = false;
                    if (oPKey instanceof Number) {
                        isPKey = !(((Number) oPKey).intValue() != 0);
                    }
                    idxSpec.setPKey(isPKey);
                }
                //???
                Object oFKeyName = r.getObject(JDBCIDX_FOREIGN_KEY);
                if (oFKeyName != null && oFKeyName instanceof String) {
                    String fKeyName = (String) oFKeyName;
                    idxSpec.setFKeyName(fKeyName);
                }
            }
        }
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
