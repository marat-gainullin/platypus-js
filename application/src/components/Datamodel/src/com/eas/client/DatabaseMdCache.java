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
import com.eas.client.dataflow.ColumnsIndicies;
import com.eas.client.dataflow.JdbcReader;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.concurrent.CallableConsumer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
    //protected Rowset dbmsSupportedTypes;

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
    /*
     public Rowset getDbTypesInfo() throws Exception {
     if (dbmsSupportedTypes == null) {
     dbmsSupportedTypes = client.getDbTypesInfo(datasourceName);
     }
     return dbmsSupportedTypes;
     }
     */

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
            query.executeQuery((ResultSet r) -> {
                ColumnsIndicies idxs = new ColumnsIndicies(r.getMetaData());
                int colIndex = idxs.find(ClientConstants.JDBCCOLS_TABLE_NAME);
                int colRemarks = idxs.find(ClientConstants.JDBCCOLS_REMARKS);
                assert colIndex > 0;
                assert colRemarks > 0;
                tablesFields.clear();
                Map<String, String> tablesNames = new HashMap<>();
                while (r.next()) {
                    String lTableName = r.getString(colIndex);
                    String lRemarks = r.getString(colRemarks);
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
                return null;
            }, null, null);
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
//        dbmsSupportedTypes = null;
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
                            CallableConsumer<Fields, ResultSet> doWork = (ResultSet rs) -> {
                                return JdbcReader.readFields(rs.getMetaData());
                            };
                            return compiledQuery.executeQuery(doWork, null, null);
                        }
                    }
                }
            }
            return null;
        }

        protected Map<String, Fields> query(String aSchema, Set<String> aTables, boolean aFullMetadata) throws Exception {
            if (aTables != null) {
                SqlDriver sqlDriver = getConnectionDriver();
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
                    String colsSql = sqlDriver.getSql4TableColumns(schema4Sql, tables2Retrive);
                    Map<String, Fields> columns = new SqlCompiledQuery(client, datasourceName, colsSql).<Map<String, Fields>>executeQuery((ResultSet r) -> {
                        return readTablesColumns(r, aSchema, sqlDriver);
                    }, null, null);
                    String sqlPks = sqlDriver.getSql4TablePrimaryKeys(schema4Sql, tables2Retrive);
                    Map<String, DbTableKeys> keys = new SqlCompiledQuery(client, datasourceName, sqlPks).<Map<String, DbTableKeys>>executeQuery((ResultSet r) -> {
                        return readTablesPrimaryKeys(r, aSchema, sqlDriver);
                    }, null, null);
                    if (aFullMetadata) {
                        String sqlFks = sqlDriver.getSql4TableForeignKeys(schema4Sql, tables2Retrive);
                        if (sqlFks != null && !sqlFks.isEmpty()) {
                            SqlCompiledQuery foreignKeysQuery = new SqlCompiledQuery(client, datasourceName, sqlFks);
                            foreignKeysQuery.executeQuery((ResultSet r) -> {
                                readTablesForeignKeys(r, aSchema, sqlDriver, keys);
                                return null;
                            }, null, null);
                        }
                    }
                    merge(aSchema, columns, keys);
                    return columns;
                }
            }
            return null;
        }

        protected Map<String, Fields> readTablesColumns(ResultSet r, String aSchema, SqlDriver sqlDriver) throws Exception {
            Map<String, Fields> tabledFields = new HashMap<>();
            if (r != null) {
                ColumnsIndicies colIndicies = new ColumnsIndicies(r.getMetaData());
                int JDBCCOLS_TABLE_INDEX = colIndicies.find(ClientConstants.JDBCCOLS_TABLE_NAME);
                int JDBCCOLS_COLUMN_INDEX = colIndicies.find(ClientConstants.JDBCCOLS_COLUMN_NAME);
                int JDBCCOLS_REMARKS_INDEX = colIndicies.find(ClientConstants.JDBCCOLS_REMARKS);
                int JDBCCOLS_DATA_TYPE_INDEX = colIndicies.find(ClientConstants.JDBCCOLS_DATA_TYPE);
                int JDBCCOLS_TYPE_NAME_INDEX = colIndicies.find(ClientConstants.JDBCCOLS_TYPE_NAME);
                int JDBCCOLS_COLUMN_SIZE_INDEX = colIndicies.find(ClientConstants.JDBCCOLS_COLUMN_SIZE);
                int JDBCCOLS_DECIMAL_DIGITS_INDEX = colIndicies.find(ClientConstants.JDBCCOLS_DECIMAL_DIGITS);
                int JDBCCOLS_NUM_PREC_RADIX_INDEX = colIndicies.find(ClientConstants.JDBCCOLS_NUM_PREC_RADIX);
                int JDBCCOLS_NULLABLE_INDEX = colIndicies.find(ClientConstants.JDBCCOLS_NULLABLE);
                while (r.next()) {
                    String fTableName = r.getString(JDBCCOLS_TABLE_INDEX);
                    Fields fields = tabledFields.get(fTableName);
                    if (fields == null) {
                        fields = new Fields();
                        tabledFields.put(fTableName, fields);
                    }
                    String fName = r.getString(JDBCCOLS_COLUMN_INDEX);
                    String fDescription = r.getString(JDBCCOLS_REMARKS_INDEX);
                    Field field = new Field(fName.toLowerCase(), fDescription);
                    field.setOriginalName(fName);
                    String rdbmsTypeName = r.getString(JDBCCOLS_TYPE_NAME_INDEX);
                    Integer correctType = sqlDriver.getJdbcTypeByRDBMSTypename(rdbmsTypeName);
                    if (correctType != null) {
                        field.getTypeInfo().setSqlType(correctType);
                    } else {
                        Object oSqlType = r.getObject(JDBCCOLS_DATA_TYPE_INDEX);
                        if (oSqlType instanceof Number) {
                            field.getTypeInfo().setSqlType(((Number) oSqlType).intValue());
                        }
                    }
                    field.getTypeInfo().setSqlTypeName(rdbmsTypeName);
                    Object oSize = r.getObject(JDBCCOLS_COLUMN_SIZE_INDEX);
                    if (oSize instanceof Number) {
                        field.setSize(((Number) oSize).intValue());
                    }
                    Object oScale = r.getObject(JDBCCOLS_DECIMAL_DIGITS_INDEX);
                    if (oScale instanceof Number) {
                        field.setScale(((Number) oScale).intValue());
                    }
                    Object oPrecision = r.getObject(JDBCCOLS_NUM_PREC_RADIX_INDEX);
                    if (oPrecision instanceof Number) {
                        field.setPrecision(((Number) oPrecision).intValue());
                    }
                    Object oNullable = r.getObject(JDBCCOLS_NULLABLE_INDEX);
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

        protected Map<String, DbTableKeys> readTablesPrimaryKeys(ResultSet r, String aSchema, SqlDriver aSqlDriver) throws Exception {
            Map<String, DbTableKeys> tabledKeys = new HashMap<>();
            // pks
            if (r != null) {
                ColumnsIndicies colsIndicies = new ColumnsIndicies(r.getMetaData());
                int JDBCPKS_TABLE_SCHEM_INDEX = colsIndicies.find(ClientConstants.JDBCPKS_TABLE_SCHEM);
                int JDBCPKS_TABLE_NAME_INDEX = colsIndicies.find(ClientConstants.JDBCPKS_TABLE_NAME);
                int JDBCPKS_COLUMN_NAME_INDEX = colsIndicies.find(ClientConstants.JDBCPKS_COLUMN_NAME);
                int JDBCPKS_CONSTRAINT_NAME_INDEX = colsIndicies.find(ClientConstants.JDBCPKS_CONSTRAINT_NAME);

                while (r.next()) {
                    String lpkSchema = r.getString(JDBCPKS_TABLE_SCHEM_INDEX);
                    String lpkTableName = r.getString(JDBCPKS_TABLE_NAME_INDEX);
                    String lpkField = r.getString(JDBCPKS_COLUMN_NAME_INDEX);
                    String lpkName = r.getString(JDBCPKS_CONSTRAINT_NAME_INDEX);
                    DbTableKeys dbPksFks = tabledKeys.get(lpkTableName);
                    if (dbPksFks == null) {
                        dbPksFks = new DbTableKeys(lpkTableName);
                        tabledKeys.put(lpkTableName, dbPksFks);
                    }
                    //
                    dbPksFks.addPrimaryKey(lpkSchema, lpkTableName, lpkField, lpkName);
                }
            }
            return tabledKeys;
        }

        protected void readTablesForeignKeys(ResultSet r, String aSchema, SqlDriver aSqlDriver, Map<String, DbTableKeys> tabledKeys) throws Exception {
            // fks
            if (r != null) {
                ColumnsIndicies colsIndicies = new ColumnsIndicies(r.getMetaData());
                int JDBCFKS_FKTABLE_SCHEM_INDEX = colsIndicies.find(ClientConstants.JDBCFKS_FKTABLE_SCHEM);
                int JDBCFKS_FKTABLE_NAME_INDEX = colsIndicies.find(ClientConstants.JDBCFKS_FKTABLE_NAME);
                int JDBCFKS_FKCOLUMN_NAME_INDEX = colsIndicies.find(ClientConstants.JDBCFKS_FKCOLUMN_NAME);
                int JDBCFKS_FK_NAME_INDEX = colsIndicies.find(ClientConstants.JDBCFKS_FK_NAME);
                int JDBCFKS_FKUPDATE_RULE_INDEX = colsIndicies.find(ClientConstants.JDBCFKS_FKUPDATE_RULE);
                int JDBCFKS_FKDELETE_RULE_INDEX = colsIndicies.find(ClientConstants.JDBCFKS_FKDELETE_RULE);
                int JDBCFKS_FKDEFERRABILITY_INDEX = colsIndicies.find(ClientConstants.JDBCFKS_FKDEFERRABILITY);
                //
                int JDBCFKS_FKPKTABLE_SCHEM_INDEX = colsIndicies.find(ClientConstants.JDBCFKS_FKPKTABLE_SCHEM);
                int JDBCFKS_FKPKTABLE_NAME_INDEX = colsIndicies.find(ClientConstants.JDBCFKS_FKPKTABLE_NAME);
                int JDBCFKS_FKPKCOLUMN_NAME_INDEX = colsIndicies.find(ClientConstants.JDBCFKS_FKPKCOLUMN_NAME);
                int JDBCFKS_FKPK_NAME_INDEX = colsIndicies.find(ClientConstants.JDBCFKS_FKPK_NAME);
                while (r.next()) {
                    String lfkSchema = r.getString(JDBCFKS_FKTABLE_SCHEM_INDEX);
                    String lfkTableName = r.getString(JDBCFKS_FKTABLE_NAME_INDEX);
                    String lfkField = r.getString(JDBCFKS_FKCOLUMN_NAME_INDEX);
                    String lfkName = r.getString(JDBCFKS_FK_NAME_INDEX);
                    Short lfkUpdateRule = null;
                    Object ofkUpdateRule = r.getObject(JDBCFKS_FKUPDATE_RULE_INDEX);
                    if (ofkUpdateRule instanceof Number) {
                        lfkUpdateRule = ((Number) ofkUpdateRule).shortValue();
                    }
                    Short lfkDeleteRule = null;
                    Object ofkDeleteRule = r.getObject(JDBCFKS_FKDELETE_RULE_INDEX);
                    if (ofkDeleteRule instanceof Number) {
                        lfkDeleteRule = ((Number) ofkDeleteRule).shortValue();
                    }
                    Short lfkDeferability = null;
                    Object ofkDeferability = r.getObject(JDBCFKS_FKDEFERRABILITY_INDEX);
                    if (ofkDeferability instanceof Number) {
                        lfkDeferability = ((Number) ofkDeferability).shortValue();
                    }
                    //
                    String lpkSchema = r.getString(JDBCFKS_FKPKTABLE_SCHEM_INDEX);
                    String lpkTableName = r.getString(JDBCFKS_FKPKTABLE_NAME_INDEX);
                    String lpkField = r.getString(JDBCFKS_FKPKCOLUMN_NAME_INDEX);
                    String lpkName = r.getString(JDBCFKS_FKPK_NAME_INDEX);
                    //
                    DbTableKeys dbPksFks = tabledKeys.get(lfkTableName);
                    if (dbPksFks == null) {
                        dbPksFks = new DbTableKeys();
                        tabledKeys.put(lfkTableName, dbPksFks);
                    }
                    dbPksFks.addForeignKey(lfkSchema, lfkTableName, lfkField, lfkName, ForeignKeySpec.ForeignKeyRule.valueOf(lfkUpdateRule != null ? lfkUpdateRule : 0/*DatabaseMetaData.importedKeyCascade*/), ForeignKeySpec.ForeignKeyRule.valueOf(lfkDeleteRule != null ? lfkDeleteRule : 0/*DatabaseMetaData.importedKeyCascade*/), lfkDeferability != null && lfkDeferability == 5, lpkSchema, lpkTableName, lpkField, lpkName);
                }
            }
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
        protected DbTableIndexes getNewEntry(String aTableName) throws Exception {
            if (client != null) {
                SqlDriver sqlDriver = getConnectionDriver();
                assert sqlDriver != null;
                String lOwner = null;
                String lTable;
                int indexOfDot = aTableName.indexOf('.');
                if (indexOfDot != -1) {
                    lOwner = aTableName.substring(0, indexOfDot);
                    lTable = aTableName.substring(indexOfDot + 1);
                } else {
                    lTable = aTableName;
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
                            indexesQuery.executeQuery((ResultSet r) -> {
                                dbTableIndexes.readIndices(r);
                                dbTableIndexes.sortIndexesColumns();
                                return null;
                            }, null, null);
                            return dbTableIndexes;
                        }
                    }
                }
            }
            return null;
        }
    }
}
