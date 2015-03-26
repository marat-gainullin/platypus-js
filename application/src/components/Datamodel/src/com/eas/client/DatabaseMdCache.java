/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client;

import com.eas.client.metadata.DbTableIndexes;
import com.eas.client.metadata.DbTableKeys;
import com.eas.client.cache.FreqCache;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.sqldrivers.SqlDriver;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class DatabaseMdCache {

    protected String datasourceName;
    protected DatabasesClient client;
    // Named tables cache
    protected TablesFieldsCache tablesFields;
    // Named tables indexes cache
    protected IndexesCache tablesIndexes;
    protected String connectionSchema;
    protected SqlDriver connectionDriver;
    protected Rowset dbmsSupportedTypes;

    public DatabaseMdCache(DatabasesClient aClient, String aDbId) throws Exception {
        super();
        client = aClient;
        datasourceName = aDbId;
        tablesFields = new TablesFieldsCache();
        tablesIndexes = new IndexesCache();
    }

    public String getConnectionSchema() throws Exception {
        if (connectionSchema == null) {
            connectionSchema = client.getConnectionSchema(datasourceName);
        }
        return connectionSchema;
    }

    public SqlDriver getConnectionDriver() throws Exception {
        if (connectionDriver == null) {
            connectionDriver = client.getConnectionDriver(datasourceName);
        }
        return connectionDriver;
    }

    public Fields getTableMetadata(String aTableName) throws Exception {
        return tablesFields.get(aTableName);
    }

    public void removeTableMetadata(String aTableName) throws Exception {
        tablesFields.remove(aTableName);
    }

    public boolean containsTableMetadata(String aTableName) throws Exception {
        return tablesFields.containsKey(aTableName);
    }

    public void removeTableIndexes(String aTableName) throws Exception {
        tablesIndexes.remove(aTableName);
    }

    public Rowset getDbTypesInfo() throws Exception {
        if (dbmsSupportedTypes == null) {
            dbmsSupportedTypes = client.getDbTypesInfo(datasourceName);
        }
        return dbmsSupportedTypes;
    }

    /**
     * Fills tables cache with fields, comments, keys (pk and fk) by connection
     * default schema.
     *
     * @param aFullMetadata
     * @throws Exception
     */
    public final void fillTablesCacheByConnectionSchema(boolean aFullMetadata) throws Exception {
        fillTablesCacheBySchema(null, aFullMetadata);
    }

    /**
     * Fills tables cache with fields, comments, keys (pk and fk).
     *
     * @param aSchema A schema for witch we should achieve metadata information.
     * If it is null, connection default schema is used
     * @param aFullMetadata Indicated that full metadata is to be archieved.
     * @throws Exception
     */
    public void fillTablesCacheBySchema(String aSchema, boolean aFullMetadata) throws Exception {
        String schema4Sql = aSchema;
        if (schema4Sql == null || schema4Sql.isEmpty()) {
            schema4Sql = getConnectionSchema();
        }
        if (schema4Sql != null && !schema4Sql.isEmpty()) {
            SqlDriver driver = getConnectionDriver();
            String queryText = driver.getSql4TablesEnumeration(schema4Sql);
            SqlCompiledQuery query = new SqlCompiledQuery(client, datasourceName, queryText);
            Rowset rs = query.executeQuery(null, null);
            int colIndex = rs.getFields().find(ClientConstants.JDBCCOLS_TABLE_NAME);
            int colRemarks = rs.getFields().find(ClientConstants.JDBCCOLS_REMARKS);
            assert colIndex > 0;
            assert colRemarks > 0;
            tablesFields.clear();
            Map<String, String> tablesNames = new HashMap<>();
            for (Row r : rs.getCurrent()) {
                String lTableName = (String) r.getColumnObject(colIndex);
                String lRemarks = (String) r.getColumnObject(colRemarks);
                tablesNames.put(lTableName, lRemarks);
                if (tablesNames.size() >= 100) {
                    Map<String, Fields> md = tablesFields.query(aSchema, tablesNames.keySet(), aFullMetadata);
                    tablesFields.fill(aSchema, md, tablesNames);
                    tablesNames.clear();
                }
            }
            if (!tablesNames.isEmpty()) {
                Map<String, Fields> md = tablesFields.query(aSchema, tablesNames.keySet(), aFullMetadata);
                tablesFields.fill(aSchema, md, tablesNames);
            }
        }
    }

    public void clear() throws Exception {
        if (tablesFields != null) {
            tablesFields.clear();
        }
        if (tablesIndexes != null) {
            tablesIndexes.clear();
        }
        connectionSchema = null;
        connectionDriver = null;
        dbmsSupportedTypes = null;
    }

    protected class TablesFieldsCache extends FreqCache<String, Fields> {

        public TablesFieldsCache() {
            super();
        }

        protected void merge(String aSchema, Map<String, Fields> aTablesFields, Map<String, DbTableKeys> aTablesKeys) throws Exception {
            aTablesFields.keySet().stream().forEach((String lTableName) -> {
                Fields fields = aTablesFields.get(lTableName);
                DbTableKeys keys = aTablesKeys.get(lTableName);
                if (keys != null) {
                    keys.getPks().entrySet().stream().forEach((Entry<String, PrimaryKeySpec> pkEntry) -> {
                        Field f = fields.get(pkEntry.getKey());
                        if (f != null) {
                            f.setPk(true);
                        }
                    });
                    keys.getFks().entrySet().stream().forEach((Entry<String, ForeignKeySpec> fkEntry) -> {
                        Field f = fields.get(fkEntry.getKey());
                        if (f != null) {
                            f.setFk(fkEntry.getValue());
                        }
                    });
                }
            });
        }

        protected void fill(String aSchema, Map<String, Fields> aTablesFields, Map<String, String> aTablesDescriptions) throws Exception {
            if (aTablesFields != null && !aTablesFields.isEmpty()) {
                synchronized (lock) {
                    aTablesFields.keySet().stream().forEach((String lTableName) -> {
                        Fields fields = aTablesFields.get(lTableName);
                        fields.setTableDescription(aTablesDescriptions.get(lTableName));
                        String fullTableName = lTableName;
                        if (aSchema != null && !aSchema.isEmpty()) {
                            fullTableName = aSchema + "." + fullTableName;
                        }
                        entries.put(transformKey(fullTableName), new CacheEntry(fullTableName, fields));
                    });
                }
            }
        }

        @Override
        protected String transformKey(String aKey) {
            return aKey != null ? aKey.toLowerCase() : null;
        }

        @Override
        protected Fields getNewEntry(String aId) throws Exception {
            if (client != null && aId != null && !aId.isEmpty()) {
                SqlDriver sqlDriver = getConnectionDriver();
                assert sqlDriver != null;
                String lOwner = null;
                String lTable;
                int indexOfDot = aId.indexOf('.');
                if (indexOfDot != -1) {
                    lOwner = aId.substring(0, indexOfDot);
                    lTable = aId.substring(indexOfDot + 1);
                } else {
                    lTable = aId;
                }
                if (lTable != null) {
                    if (lOwner != null) {
                        lOwner = lOwner.trim();
                    }
                    lTable = lTable.trim();
                    if ((lOwner == null || !lOwner.isEmpty()) && !lTable.isEmpty()) {
                        Set<String> lTableNames = new HashSet<>();
                        lTableNames.add(lTable);
                        Map<String, Fields> fieldses = query(lOwner, lTableNames, true);
                        if (fieldses != null && !fieldses.isEmpty()) {
                            assert fieldses.size() == 1;
                            return fieldses.values().iterator().next();
                        } else {
                            SqlCompiledQuery compiledQuery = new SqlCompiledQuery(client, datasourceName, SQLUtils.makeTableNameMetadataQuery(aId));
                            Rowset rs = compiledQuery.executeQuery(null, null);
                            return rs.getFields();
                        }
                    }
                }
            }
            return null;
        }

        protected Map<String, Fields> query(String aSchema, Set<String> aTables, boolean aFullMetadata) throws Exception {
            if (aTables != null) {
                SqlDriver driver = getConnectionDriver();
                String schema4Sql = aSchema;
                if (schema4Sql == null || schema4Sql.isEmpty()) {
                    schema4Sql = getConnectionSchema();
                }
                Set<String> tables2Retrive = new HashSet<>();
                for (String lTable : aTables) {
                    String qtn = lTable;
                    if (aSchema != null && !aSchema.isEmpty()) {
                        qtn = aSchema + "." + qtn;
                    }
                    if (!containsTableMetadata(qtn)) {
                        tables2Retrive.add(lTable);
                    }
                }
                if (!tables2Retrive.isEmpty()) {
                    Rowset tablesColumnsRs = null;
                    Rowset primaryKeysRs = null;
                    Rowset foreignKeysRs = null;
                    String colsSql = driver.getSql4TableColumns(schema4Sql, tables2Retrive);
                    if (colsSql != null && !colsSql.isEmpty()) {
                        tablesColumnsRs = new SqlCompiledQuery(client, datasourceName, colsSql).executeQuery(null, null);
                    }
                    String sqlPks = driver.getSql4TablePrimaryKeys(schema4Sql, tables2Retrive);
                    if (sqlPks != null && !sqlPks.isEmpty()) {
                        primaryKeysRs = new SqlCompiledQuery(client, datasourceName, sqlPks).executeQuery(null, null);
                    }
                    if (aFullMetadata) {
                        String sqlFks = driver.getSql4TableForeignKeys(schema4Sql, tables2Retrive);
                        if (sqlFks != null && !sqlFks.isEmpty()) {
                            foreignKeysRs = new SqlCompiledQuery(client, datasourceName, sqlFks).executeQuery(null, null);
                        }
                    }
                    return read(tablesColumnsRs, primaryKeysRs, foreignKeysRs, aSchema, driver);
                }
            }
            return null;
        }

        protected Map<String, Fields> read(Rowset aTablesColumnsRs, Rowset aPrimaryKeysRs, Rowset aForeignKeysRs, String aSchema, SqlDriver sqlDriver) throws Exception {
            Map<String, Fields> columns = readTablesColumns(aTablesColumnsRs, aSchema, sqlDriver);
            Map<String, DbTableKeys> keys = readTablesKeys(aPrimaryKeysRs, aForeignKeysRs, aSchema, sqlDriver);
            merge(aSchema, columns, keys);
            return columns;
        }

        protected Map<String, Fields> readTablesColumns(Rowset colsRs, String aSchema, SqlDriver sqlDriver) throws Exception {
            Map<String, Fields> tabledFields = new HashMap<>();
            if (colsRs != null) {
                Fields colsMd = colsRs.getFields();
                Map<String, Integer> colIndicies = new HashMap<>();
                for (int i = 1; i <= colsMd.getFieldsCount(); i++) {
                    colIndicies.put(colsMd.get(i).getName().toUpperCase(), i);
                }
                int JDBCCOLS_TABLE_INDEX = colIndicies.get(ClientConstants.JDBCCOLS_TABLE_NAME);
                int JDBCCOLS_COLUMN_INDEX = colIndicies.get(ClientConstants.JDBCCOLS_COLUMN_NAME);
                int JDBCCOLS_REMARKS_INDEX = colIndicies.get(ClientConstants.JDBCCOLS_REMARKS);
                int JDBCCOLS_DATA_TYPE_INDEX = colIndicies.get(ClientConstants.JDBCCOLS_DATA_TYPE);
                int JDBCCOLS_TYPE_NAME_INDEX = colIndicies.get(ClientConstants.JDBCCOLS_TYPE_NAME);
                int JDBCCOLS_COLUMN_SIZE_INDEX = colIndicies.get(ClientConstants.JDBCCOLS_COLUMN_SIZE);
                int JDBCCOLS_DECIMAL_DIGITS_INDEX = colIndicies.get(ClientConstants.JDBCCOLS_DECIMAL_DIGITS);
                int JDBCCOLS_NUM_PREC_RADIX_INDEX = colIndicies.get(ClientConstants.JDBCCOLS_NUM_PREC_RADIX);
                int JDBCCOLS_NULLABLE_INDEX = colIndicies.get(ClientConstants.JDBCCOLS_NULLABLE);
                for (Row r : colsRs.getCurrent()) {
                    String fTableName = (String) r.getColumnObject(JDBCCOLS_TABLE_INDEX);
                    Fields fields = tabledFields.get(fTableName);
                    if (fields == null) {
                        fields = new Fields();
                        tabledFields.put(fTableName, fields);
                    }
                    String fName = (String) r.getColumnObject(JDBCCOLS_COLUMN_INDEX);
                    String fDescription = (String) r.getColumnObject(JDBCCOLS_REMARKS_INDEX);
                    Field field = new Field(fName.toLowerCase(), fDescription);
                    field.setOriginalName(fName);
                    String rdbmsTypeName = (String) r.getColumnObject(JDBCCOLS_TYPE_NAME_INDEX);
                    Integer correctType = sqlDriver.getJdbcTypeByRDBMSTypename(rdbmsTypeName);
                    if (correctType != null) {
                        field.getTypeInfo().setSqlType(correctType);
                    } else {
                        Object oSqlType = r.getColumnObject(JDBCCOLS_DATA_TYPE_INDEX);
                        if (oSqlType instanceof Number) {
                            field.getTypeInfo().setSqlType(((Number) oSqlType).intValue());
                        }
                    }
                    field.getTypeInfo().setSqlTypeName(rdbmsTypeName);
                    Object oSize = r.getColumnObject(JDBCCOLS_COLUMN_SIZE_INDEX);
                    if (oSize instanceof Number) {
                        field.setSize(((Number) oSize).intValue());
                    }
                    Object oScale = r.getColumnObject(JDBCCOLS_DECIMAL_DIGITS_INDEX);
                    if (oScale instanceof Number) {
                        field.setScale(((Number) oScale).intValue());
                    }
                    Object oPrecision = r.getColumnObject(JDBCCOLS_NUM_PREC_RADIX_INDEX);
                    if (oPrecision instanceof Number) {
                        field.setPrecision(((Number) oPrecision).intValue());
                    }
                    Object oNullable = r.getColumnObject(JDBCCOLS_NULLABLE_INDEX);
                    if (oNullable instanceof Number) {
                        field.setNullable(((Number) oNullable).intValue() == ResultSetMetaData.columnNullable);
                    }
                    field.setSchemaName(aSchema);
                    field.setTableName(fTableName);
                    // Fields types abstraction is partly used here,
                    // because of metadata processing tasks
                    sqlDriver.getTypesResolver().resolve2Application(field);
                    field.getTypeInfo().setSqlTypeName(rdbmsTypeName);
                    //
                    fields.add(field);
                }
            }
            return tabledFields;
        }

        protected Map<String, DbTableKeys> readTablesKeys(Rowset pksRs, Rowset fksRs, String aSchema, SqlDriver aDrv) throws Exception {
            Map<String, DbTableKeys> tabledKeys = new HashMap<>();
            // pks
            if (pksRs != null) {
                Fields colsMd = pksRs.getFields();
                Map<String, Integer> colsIndicies = new HashMap<>();
                for (int i = 1; i <= colsMd.getFieldsCount(); i++) {
                    colsIndicies.put(colsMd.get(i).getName().toUpperCase(), i);
                }
                int JDBCPKS_TABLE_SCHEM_INDEX = colsIndicies.get(ClientConstants.JDBCPKS_TABLE_SCHEM);
                int JDBCPKS_TABLE_NAME_INDEX = colsIndicies.get(ClientConstants.JDBCPKS_TABLE_NAME);
                int JDBCPKS_COLUMN_NAME_INDEX = colsIndicies.get(ClientConstants.JDBCPKS_COLUMN_NAME);
                int JDBCPKS_CONSTRAINT_NAME_INDEX = colsIndicies.get(ClientConstants.JDBCPKS_CONSTRAINT_NAME);

                for (Row r : pksRs.getCurrent()) {
                    String lpkSchema = (String) r.getColumnObject(JDBCPKS_TABLE_SCHEM_INDEX);
                    String lpkTableName = (String) r.getColumnObject(JDBCPKS_TABLE_NAME_INDEX);
                    String lpkField = (String) r.getColumnObject(JDBCPKS_COLUMN_NAME_INDEX);
                    String lpkName = (String) r.getColumnObject(JDBCPKS_CONSTRAINT_NAME_INDEX);
                    DbTableKeys dbPksFks = tabledKeys.get(lpkTableName);
                    if (dbPksFks == null) {
                        dbPksFks = new DbTableKeys(lpkTableName);
                        tabledKeys.put(lpkTableName, dbPksFks);
                    }
                    //
                    dbPksFks.addPrimaryKey(lpkSchema, lpkTableName, lpkField, lpkName);
                }
            }
            // fks
            if (fksRs != null) {
                Fields colsMd = fksRs.getFields();
                Map<String, Integer> colsIndicies = new HashMap<>();
                for (int i = 1; i <= colsMd.getFieldsCount(); i++) {
                    colsIndicies.put(colsMd.get(i).getName().toUpperCase(), i);
                }

                int JDBCFKS_FKTABLE_SCHEM_INDEX = colsIndicies.get(ClientConstants.JDBCFKS_FKTABLE_SCHEM);
                int JDBCFKS_FKTABLE_NAME_INDEX = colsIndicies.get(ClientConstants.JDBCFKS_FKTABLE_NAME);
                int JDBCFKS_FKCOLUMN_NAME_INDEX = colsIndicies.get(ClientConstants.JDBCFKS_FKCOLUMN_NAME);
                int JDBCFKS_FK_NAME_INDEX = colsIndicies.get(ClientConstants.JDBCFKS_FK_NAME);
                int JDBCFKS_FKUPDATE_RULE_INDEX = colsIndicies.get(ClientConstants.JDBCFKS_FKUPDATE_RULE);
                int JDBCFKS_FKDELETE_RULE_INDEX = colsIndicies.get(ClientConstants.JDBCFKS_FKDELETE_RULE);
                int JDBCFKS_FKDEFERRABILITY_INDEX = colsIndicies.get(ClientConstants.JDBCFKS_FKDEFERRABILITY);
                //
                int JDBCFKS_FKPKTABLE_SCHEM_INDEX = colsIndicies.get(ClientConstants.JDBCFKS_FKPKTABLE_SCHEM);
                int JDBCFKS_FKPKTABLE_NAME_INDEX = colsIndicies.get(ClientConstants.JDBCFKS_FKPKTABLE_NAME);
                int JDBCFKS_FKPKCOLUMN_NAME_INDEX = colsIndicies.get(ClientConstants.JDBCFKS_FKPKCOLUMN_NAME);
                int JDBCFKS_FKPK_NAME_INDEX = colsIndicies.get(ClientConstants.JDBCFKS_FKPK_NAME);
                for (Row r : fksRs.getCurrent()) {
                    String lfkSchema = (String) r.getColumnObject(JDBCFKS_FKTABLE_SCHEM_INDEX);
                    String lfkTableName = (String) r.getColumnObject(JDBCFKS_FKTABLE_NAME_INDEX);
                    String lfkField = (String) r.getColumnObject(JDBCFKS_FKCOLUMN_NAME_INDEX);
                    String lfkName = (String) r.getColumnObject(JDBCFKS_FK_NAME_INDEX);
                    Short lfkUpdateRule = null;
                    Object ofkUpdateRule = r.getColumnObject(JDBCFKS_FKUPDATE_RULE_INDEX);
                    if (ofkUpdateRule instanceof Number) {
                        lfkUpdateRule = ((Number) ofkUpdateRule).shortValue();
                    }
                    Short lfkDeleteRule = null;
                    Object ofkDeleteRule = r.getColumnObject(JDBCFKS_FKDELETE_RULE_INDEX);
                    if (ofkDeleteRule instanceof Number) {
                        lfkDeleteRule = ((Number) ofkDeleteRule).shortValue();
                    }
                    Short lfkDeferability = null;
                    Object ofkDeferability = r.getColumnObject(JDBCFKS_FKDEFERRABILITY_INDEX);
                    if (ofkDeferability instanceof Number) {
                        lfkDeferability = ((Number) ofkDeferability).shortValue();
                    }
                    //
                    String lpkSchema = (String) r.getColumnObject(JDBCFKS_FKPKTABLE_SCHEM_INDEX);
                    String lpkTableName = (String) r.getColumnObject(JDBCFKS_FKPKTABLE_NAME_INDEX);
                    String lpkField = (String) r.getColumnObject(JDBCFKS_FKPKCOLUMN_NAME_INDEX);
                    String lpkName = (String) r.getColumnObject(JDBCFKS_FKPK_NAME_INDEX);
                    //
                    DbTableKeys dbPksFks = tabledKeys.get(lfkTableName);
                    if (dbPksFks == null) {
                        dbPksFks = new DbTableKeys();
                        tabledKeys.put(lfkTableName, dbPksFks);
                    }
                    dbPksFks.addForeignKey(lfkSchema, lfkTableName, lfkField, lfkName, ForeignKeySpec.ForeignKeyRule.valueOf(lfkUpdateRule != null ? lfkUpdateRule : 0/*DatabaseMetaData.importedKeyCascade*/), ForeignKeySpec.ForeignKeyRule.valueOf(lfkDeleteRule != null ? lfkDeleteRule : 0/*DatabaseMetaData.importedKeyCascade*/), lfkDeferability != null && lfkDeferability == 5, lpkSchema, lpkTableName, lpkField, lpkName);
                }
            }
            return tabledKeys;
        }
    }

    public DbTableIndexes getTableIndexes(String aTableName) throws Exception {
        if (aTableName != null && !aTableName.isEmpty() && tablesIndexes != null) {
            return tablesIndexes.get(aTableName);
        }
        return null;
    }

    protected class IndexesCache extends FreqCache<String, DbTableIndexes> {

        public IndexesCache() {
            super();
        }

        @Override
        protected String transformKey(String aKey) {
            return aKey != null ? aKey.toLowerCase() : null;
        }

        @Override
        protected DbTableIndexes getNewEntry(String aId) throws Exception {
            if (client != null) {
                SqlDriver sqlDriver = getConnectionDriver();
                assert sqlDriver != null;
                String lOwner = null;
                String lTable;
                int indexOfDot = aId.indexOf('.');
                if (indexOfDot != -1) {
                    lOwner = aId.substring(0, indexOfDot);
                    lTable = aId.substring(indexOfDot + 1);
                } else {
                    lTable = aId;
                }
                if (lTable != null) {
                    if (lOwner != null) {
                        lOwner = lOwner.trim();
                    }
                    lTable = lTable.trim();
                    if ((lOwner == null || !lOwner.isEmpty()) && !lTable.isEmpty()) {
                        DbTableIndexes dbTableIndexes = new DbTableIndexes();
                        Set<String> lTableNames = new HashSet<>();
                        lTableNames.add(lTable);
                        String schema4Sql = lOwner;
                        if (schema4Sql == null) {
                            schema4Sql = getConnectionSchema();
                        }
                        String sql4IndexesText = sqlDriver.getSql4Indexes(schema4Sql, lTableNames);
                        if (sql4IndexesText != null && !sql4IndexesText.isEmpty()) {
                            SqlCompiledQuery indexesQuery = new SqlCompiledQuery(client, datasourceName, sql4IndexesText);
                            Rowset indexesRs = indexesQuery.executeQuery(null, null);
                            if (indexesRs != null) {
                                indexesRs.getCurrent().stream().forEach((r) -> {
                                    try {
                                        dbTableIndexes.addIndexByDsRow(r);
                                    } catch (InvalidColIndexException | InvalidCursorPositionException ex) {
                                        Logger.getLogger(DatabaseMdCache.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                });
                                dbTableIndexes.sortIndexesColumns();
                            }
                            return dbTableIndexes;
                        }
                    }
                }
            }
            return null;
        }
    }
}
