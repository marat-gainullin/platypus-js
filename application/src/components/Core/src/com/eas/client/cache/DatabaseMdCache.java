/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.cache;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.bearsoft.rowset.metadata.PrimaryKeySpec;
import com.eas.client.ClientConstants;
import com.eas.client.DbClient;
import com.eas.client.DbMetadataCache;
import com.eas.client.SQLUtils;
import com.eas.client.metadata.DbTableComments;
import com.eas.client.metadata.DbTableIndexes;
import com.eas.client.metadata.DbTableKeys;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.sqldrivers.SqlDriver;
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
public class DatabaseMdCache implements DbMetadataCache {

    protected String dbId;
    protected DbClient client;
    // Named tables cache
    protected TablesFieldsCache tablesFields = null;
    // Named tables indexes cache
    protected IndexesCache tablesIndexes = null;
    protected String connectionSchema = null;
    protected SqlDriver connectionDriver = null;
    protected Rowset dbmsSupportedTypes = null;

    public DatabaseMdCache(DbClient aClient, String aDbId) throws Exception {
        super();
        client = aClient;
        dbId = aDbId;
        tablesFields = new TablesFieldsCache();
        tablesIndexes = new IndexesCache();
    }

    @Override
    public String getConnectionSchema() throws Exception {
        if (connectionSchema == null) {
            connectionSchema = client.getConnectionSchema(dbId);
        }
        return connectionSchema;
    }

    @Override
    public SqlDriver getConnectionDriver() throws Exception {
        if (connectionDriver == null) {
            connectionDriver = SQLUtils.getSqlDriver(client.getConnectionDialect(dbId));
        }
        return connectionDriver;
    }

    @Override
    public Fields getTableMetadata(String aTableName) throws Exception {
        return tablesFields.get(aTableName);
    }

    @Override
    public void removeTableMetadata(String aTableName) throws Exception {
        tablesFields.remove(aTableName);
    }

    @Override
    public boolean containsTableMetadata(String aTableName) throws Exception {
        return tablesFields.containsKey(aTableName);
    }

    @Override
    public void removeTableIndexes(String aTableName) throws Exception {
        tablesIndexes.remove(aTableName);
    }

    @Override
    public Rowset getDbTypesInfo() throws Exception {
        if (dbmsSupportedTypes == null) {
            dbmsSupportedTypes = client.getDbTypesInfo(dbId);
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
    @Override
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
    @Override
    public void fillTablesCacheBySchema(String aSchema, boolean aFullMetadata) throws Exception {
        String schema4Sql = aSchema;
        if (schema4Sql == null || schema4Sql.isEmpty()) {
            schema4Sql = getConnectionSchema();
        }
        if (schema4Sql != null && !schema4Sql.isEmpty()) {
            SqlDriver driver = getConnectionDriver();
            String queryText = driver.getSql4TablesEnumeration(schema4Sql);
            SqlCompiledQuery query = new SqlCompiledQuery(client, dbId, queryText);
            Rowset rs = query.executeQuery();
            int colIndex = rs.getFields().find(ClientConstants.JDBCCOLS_TABLE_NAME);
            assert colIndex > 0;
            Set<String> tablesNames = new HashSet<>();
            rs.beforeFirst();
            while (rs.next()) {
                String lTableName = rs.getString(colIndex);
                tablesNames.add(lTableName);
                if (tablesNames.size() >= 100) {
                    Map<String, Fields> md = tablesFields.query(aSchema, tablesNames, aFullMetadata);
                    tablesFields.fill(aSchema, md);
                    tablesNames.clear();
                }
            }
            if (!tablesNames.isEmpty()) {
                Map<String, Fields> md = tablesFields.query(aSchema, tablesNames, aFullMetadata);
                tablesFields.fill(aSchema, md);
            }
        }
    }

    @Override
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
        System.gc();
    }

    protected class TablesFieldsCache extends FreqCache<String, Fields> {

        public TablesFieldsCache() {
            super();
        }

        protected void merge(String aSchema, Map<String, Fields> aTablesFields, Map<String, DbTableKeys> aTablesKeys, Map<String, DbTableComments> aTablesComments) throws Exception {
            for (String lTableName : aTablesFields.keySet()) {
                Fields fields = aTablesFields.get(lTableName);
                DbTableKeys keys = aTablesKeys.get(lTableName);
                DbTableComments comments = aTablesComments.get(lTableName);
                if (keys != null) {
                    for (Entry<String, PrimaryKeySpec> pkEntry : keys.getPks().entrySet()) {
                        Field f = fields.get(pkEntry.getKey());
                        f.setPk(f != null);
                    }
                    for (Entry<String, ForeignKeySpec> fkEntry : keys.getFks().entrySet()) {
                        Field f = fields.get(fkEntry.getKey());
                        if (f != null) {
                            f.setFk(fkEntry.getValue());
                        }
                    }
                }
                if (comments != null) {
                    fields.setTableDescription(comments.getTableComment());
                    for (Entry<String, String> cEntry : comments.getFieldsComments().entrySet()) {
                        Field f = fields.get(cEntry.getKey());
                        if (f != null) {
                            f.setDescription(cEntry.getValue());
                        }
                    }
                }
            }
        }

        protected void fill(String aSchema, Map<String, Fields> aTablesFields) throws Exception {
            if (aTablesFields != null && !aTablesFields.isEmpty()) {
                synchronized (lock) {
                    for (String lTableName : aTablesFields.keySet()) {
                        Fields fields = aTablesFields.get(lTableName);
                        String fullTableName = lTableName;
                        if (aSchema != null && !aSchema.isEmpty()) {
                            fullTableName = aSchema + "." + fullTableName;
                        }
                        entries.put(transformKey(fullTableName), new CacheEntry(fullTableName, fields));
                    }
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
                            SqlCompiledQuery compiledQuery = new SqlCompiledQuery(client, dbId, SQLUtils.makeTableNameMetadataQuery(aId));
                            Rowset rs = compiledQuery.executeQuery();
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
                    Rowset tablesCommentsRs = null;
                    Rowset columnsCommentsRs = null;
                    Rowset primaryKeysRs = null;
                    Rowset foreignKeysRs = null;
                    String colsSql = driver.getSql4TableColumns(schema4Sql, tables2Retrive);
                    if (colsSql != null && !colsSql.isEmpty()) {
                        tablesColumnsRs = new SqlCompiledQuery(client, dbId, colsSql).executeQuery();
                    }
                    String sqlPks = driver.getSql4TablePrimaryKeys(schema4Sql, tables2Retrive);
                    if (sqlPks != null && !sqlPks.isEmpty()) {
                        primaryKeysRs = new SqlCompiledQuery(client, dbId, sqlPks).executeQuery();
                    }
                    if (aFullMetadata) {
                        String sqlTC = driver.getSql4TableComments(schema4Sql, tables2Retrive);
                        String sqlCC = driver.getSql4ColumnsComments(schema4Sql, tables2Retrive);
                        if (sqlTC != null && !sqlTC.isEmpty()
                                && sqlCC != null && !sqlCC.isEmpty()) {
                            tablesCommentsRs = new SqlCompiledQuery(client, dbId, sqlTC).executeQuery();
                            columnsCommentsRs = new SqlCompiledQuery(client, dbId, sqlCC).executeQuery();
                        }
                        String sqlFks = driver.getSql4TableForeignKeys(schema4Sql, tables2Retrive);
                        if (sqlFks != null && !sqlFks.isEmpty()) {
                            foreignKeysRs = new SqlCompiledQuery(client, dbId, sqlFks).executeQuery();
                        }
                    }
                    return read(tablesColumnsRs, tablesCommentsRs, columnsCommentsRs, primaryKeysRs, foreignKeysRs, aSchema, driver);
                }
            }
            return null;
        }

        protected Map<String, Fields> read(Rowset aTablesColumnsRs, Rowset aTablesCommentsRs, Rowset aColumnsCommentsRs, Rowset aPrimaryKeysRs, Rowset aForeignKeysRs, String aSchema, SqlDriver sqlDriver) throws Exception {
            Map<String, Fields> columns = readTablesColumns(aTablesColumnsRs, aSchema, sqlDriver);
            Map<String, DbTableKeys> keys = readTablesKeys(aPrimaryKeysRs, aForeignKeysRs, aSchema, sqlDriver);
            Map<String, DbTableComments> comments = readTablesComments(aTablesCommentsRs, aColumnsCommentsRs, aSchema, sqlDriver);
            merge(aSchema, columns, keys, comments);
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
                //int JDBCCOLS_REMARKS_INDEX = colIndicies.get(ClientConstants.JDBCCOLS_REMARKS);
                int JDBCCOLS_DATA_TYPE_INDEX = colIndicies.get(ClientConstants.JDBCCOLS_DATA_TYPE);
                int JDBCCOLS_TYPE_NAME_INDEX = colIndicies.get(ClientConstants.JDBCCOLS_TYPE_NAME);
                int JDBCCOLS_COLUMN_SIZE_INDEX = colIndicies.get(ClientConstants.JDBCCOLS_COLUMN_SIZE);
                int JDBCCOLS_DECIMAL_DIGITS_INDEX = colIndicies.get(ClientConstants.JDBCCOLS_DECIMAL_DIGITS);
                int JDBCCOLS_NUM_PREC_RADIX_INDEX = colIndicies.get(ClientConstants.JDBCCOLS_NUM_PREC_RADIX);
                int JDBCCOLS_NULLABLE_INDEX = colIndicies.get(ClientConstants.JDBCCOLS_NULLABLE);
                colsRs.beforeFirst();
                while (colsRs.next()) {
                    String fTableName = colsRs.getString(JDBCCOLS_TABLE_INDEX);
                    Fields fields = tabledFields.get(fTableName);
                    if (fields == null) {
                        fields = new Fields();
                        tabledFields.put(fTableName, fields);
                    }
                    String fName = colsRs.getString(JDBCCOLS_COLUMN_INDEX);
                    //String fDescription = colsRs.getString(JDBCCOLS_REMARKS_INDEX);
                    //Field field = new Field(fName, fDescription);
                    Field field = new Field(fName);
                    field.setOriginalName(fName);
                    String rdbmsTypeName = colsRs.getString(JDBCCOLS_TYPE_NAME_INDEX);
                    Integer correctType = sqlDriver.getJdbcTypeByRDBMSTypename(rdbmsTypeName);
                    if (correctType != null) {
                        field.getTypeInfo().setSqlType(correctType);
                    } else {
                        field.getTypeInfo().setSqlType(colsRs.getInt(JDBCCOLS_DATA_TYPE_INDEX));
                    }
                    field.getTypeInfo().setSqlTypeName(rdbmsTypeName);
                    Integer iSize = colsRs.getInt(JDBCCOLS_COLUMN_SIZE_INDEX);
                    if (iSize != null) {
                        field.setSize(iSize);
                    }
                    Integer iScale = colsRs.getInt(JDBCCOLS_DECIMAL_DIGITS_INDEX);
                    if (iScale != null) {
                        field.setScale(iScale);
                    }
                    Integer iPrecision = colsRs.getInt(JDBCCOLS_NUM_PREC_RADIX_INDEX);
                    if (iPrecision != null) {
                        field.setPrecision(iPrecision);
                    }
                    Integer iNullable = colsRs.getInt(JDBCCOLS_NULLABLE_INDEX);
                    if (iNullable != null) {
                        field.setNullable(iNullable == ResultSetMetaData.columnNullable);
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

                pksRs.beforeFirst();
                while (pksRs.next()) {
                    String lpkSchema = pksRs.getString(JDBCPKS_TABLE_SCHEM_INDEX);
                    String lpkTableName = pksRs.getString(JDBCPKS_TABLE_NAME_INDEX);
                    String lpkField = pksRs.getString(JDBCPKS_COLUMN_NAME_INDEX);
                    String lpkName = pksRs.getString(JDBCPKS_CONSTRAINT_NAME_INDEX);
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
                fksRs.beforeFirst();
                while (fksRs.next()) {
                    String lfkSchema = fksRs.getString(JDBCFKS_FKTABLE_SCHEM_INDEX);
                    String lfkTableName = fksRs.getString(JDBCFKS_FKTABLE_NAME_INDEX);
                    String lfkField = fksRs.getString(JDBCFKS_FKCOLUMN_NAME_INDEX);
                    String lfkName = fksRs.getString(JDBCFKS_FK_NAME_INDEX);
                    Short lfkUpdateRule = fksRs.getShort(JDBCFKS_FKUPDATE_RULE_INDEX);
                    Short lfkDeleteRule = fksRs.getShort(JDBCFKS_FKDELETE_RULE_INDEX);
                    Short lfkDeferability = fksRs.getShort(JDBCFKS_FKDEFERRABILITY_INDEX);
                    //
                    String lpkSchema = fksRs.getString(JDBCFKS_FKPKTABLE_SCHEM_INDEX);
                    String lpkTableName = fksRs.getString(JDBCFKS_FKPKTABLE_NAME_INDEX);
                    String lpkField = fksRs.getString(JDBCFKS_FKPKCOLUMN_NAME_INDEX);
                    String lpkName = fksRs.getString(JDBCFKS_FKPK_NAME_INDEX);
                    //
                    DbTableKeys dbPksFks = tabledKeys.get(lfkTableName);
                    if (dbPksFks == null) {
                        dbPksFks = new DbTableKeys();
                        tabledKeys.put(lfkTableName, dbPksFks);
                    }
                    dbPksFks.addForeignKey(lfkSchema, lfkTableName, lfkField, lfkName, ForeignKeySpec.ForeignKeyRule.valueOf(lfkUpdateRule != null ? lfkUpdateRule : 0/*DatabaseMetaData.importedKeyCascade*/), ForeignKeySpec.ForeignKeyRule.valueOf(lfkDeleteRule != null ? lfkDeleteRule : 0/*DatabaseMetaData.importedKeyCascade*/), lfkDeferability != null ? lfkDeferability == 5/*DatabaseMetaData.importedKeyInitiallyDeferred*/ ? true : false : false, lpkSchema, lpkTableName, lpkField, lpkName);
                }
            }
            return tabledKeys;
        }

        protected Map<String, DbTableComments> readTablesComments(Rowset tcRowset, Rowset ccRowset, String aOwner, SqlDriver aDrv) throws Exception {
            Map<String, DbTableComments> tabledComments = new HashMap<>();
            tcRowset.beforeFirst();
            while (tcRowset.next()) {
                String lTableName = aDrv.getTableNameFromCommentsDs(tcRowset);
                DbTableComments lTableComments = tabledComments.get(lTableName);
                if (lTableComments == null) {
                    lTableComments = new DbTableComments();
                    tabledComments.put(lTableName, lTableComments);
                }
                lTableComments.setTableComment(aDrv.getTableCommentFromCommentsDs(tcRowset));
            }

            ccRowset.beforeFirst();
            while (ccRowset.next()) {
                String lTableName = aDrv.getTableNameFromCommentsDs(ccRowset);
                DbTableComments lTableComments = tabledComments.get(lTableName);
                if (lTableComments == null) {
                    lTableComments = new DbTableComments();
                    tabledComments.put(lTableName, lTableComments);
                }
                lTableComments.setFieldComment(aDrv.getColumnNameFromCommentsDs(ccRowset), aDrv.getColumnCommentFromCommentsDs(ccRowset));
            }
            return tabledComments;
        }

        /*
         @Override
         protected Fields cloneEntry(Fields source) {
         if (source != null) {
         return source.copy();
         }
         return null;
         }
         */
    }

    @Override
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
                            SqlCompiledQuery indexesQuery = new SqlCompiledQuery(client, dbId, sql4IndexesText);
                            Rowset indexesRs = indexesQuery.executeQuery();
                            if (indexesRs != null) {
                                indexesRs.beforeFirst();
                                while (indexesRs.next()) {
                                    dbTableIndexes.addIndexByDsRow(indexesRs);
                                }
                                dbTableIndexes.sortIndexesColumns();
                            }
                            return dbTableIndexes;
                        }
                    }
                }
            }
            return null;
        }

        /*
         @Override
         protected DbTableIndexes cloneEntry(DbTableIndexes source) {
         if (source != null) {
         return source.copy();
         }
         return null;
         }
         */
    }
}
