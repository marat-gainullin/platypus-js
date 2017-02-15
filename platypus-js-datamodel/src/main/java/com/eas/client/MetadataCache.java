package com.eas.client;

import com.eas.client.metadata.DbTableIndexes;
import com.eas.client.metadata.DbTableKeys;
import com.eas.client.dataflow.ColumnsIndicies;
import com.eas.client.dataflow.StatementsGenerator;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.JdbcField;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.concurrent.CallableConsumer;
import com.eas.util.IdGenerator;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author mg
 */
public class MetadataCache implements StatementsGenerator.TablesContainer {

    protected class CaseInsesitiveMap<V> extends HashMap<String, V> {

        protected String keyToLowerCase(String aKey) {
            return aKey != null ? aKey.toLowerCase() : null;
        }

        @Override
        public V get(Object key) {
            return super.get(keyToLowerCase((String) key));
        }

        @Override
        public V put(String key, V value) {
            return super.put(keyToLowerCase(key), value);
        }

        @Override
        public V remove(Object key) {
            return super.remove(keyToLowerCase((String) key));
        }

        @Override
        public boolean containsKey(Object key) {
            return super.containsKey(keyToLowerCase((String) key));
        }

    }

    protected class ConcurrentCaseInsesitiveMap<V> extends ConcurrentHashMap<String, V> {

        protected final String nullKey = "null-" + IdGenerator.genId();

        protected String keyToLowerCase(String aKey) {
            return aKey != null ? aKey.toLowerCase() : nullKey;
        }

        @Override
        public V get(Object key) {
            return super.get(keyToLowerCase((String) key));
        }

        @Override
        public V put(String key, V value) {
            return super.put(keyToLowerCase(key), value);
        }

        @Override
        public V remove(Object key) {
            return super.remove(keyToLowerCase((String) key));
        }

        @Override
        public boolean containsKey(Object key) {
            return super.containsKey(keyToLowerCase((String) key));
        }

    }

    protected String datasourceName;
    protected DatabasesClient client;
    // Named tables fields cache
    protected Map<String, TablesFieldsCache> schemasTablesFields = new ConcurrentCaseInsesitiveMap<>();
    // Named tables indexes cache
    protected Map<String, TablesIndexesCache> schemasTablesIndexes = new ConcurrentCaseInsesitiveMap<>();
    protected String datasourceSchema;
    protected SqlDriver datasourceDriver;

    public MetadataCache(DatabasesClient aClient, String aDatasourceName) throws Exception {
        super();
        client = aClient;
        datasourceName = aDatasourceName;
    }

    public String getDatasourceSchema() throws Exception {
        if (datasourceSchema == null) {
            datasourceSchema = client.getConnectionSchema(datasourceName);
        }
        return datasourceSchema;
    }

    public SqlDriver getDatasourceSqlDriver() throws Exception {
        if (datasourceDriver == null) {
            datasourceDriver = client.getConnectionDriver(datasourceName);
        }
        return datasourceDriver;
    }

    private String schemaFromTableName(String aTableName) {
        int indexOfDot = aTableName.indexOf(".");
        String schema = null;
        if (indexOfDot != -1) {
            schema = aTableName.substring(0, indexOfDot);
        }
        return schema;
    }

    private String pureTableName(String aTableName) {
        int indexOfDot = aTableName.indexOf(".");
        if (indexOfDot != -1) {
            return aTableName.substring(indexOfDot + 1);
        } else {
            return aTableName;
        }
    }

    public TablesFieldsCache lookupFieldsCache(String aSchema) {
        return schemasTablesFields.get(aSchema);
    }

    private TablesIndexesCache lookupIndexesCache(String aTableName) {
        String schema = schemaFromTableName(aTableName);
        return schemasTablesIndexes.get(schema);
    }

    @Override
    public Fields getTableMetadata(String aTableName) throws Exception {
        String schema = schemaFromTableName(aTableName);
        if (!schemasTablesFields.containsKey(schema)) {
            fillTablesCacheBySchema(schema);
        }
        TablesFieldsCache cache = lookupFieldsCache(schema);
        return cache != null ? cache.get(aTableName) : null;
    }

    public void refreshTableMetadata(String aTable) throws Exception {
        String schemaName = schemaFromTableName(aTable);
        TablesFieldsCache fieldsCache = schemasTablesFields.get(schemaName);
        if (fieldsCache == null) {
            fieldsCache = new TablesFieldsCache();
            schemasTablesFields.put(schemaName, fieldsCache);
        }
        String tableName = schemaName != null && !schemaName.isEmpty() ? aTable.substring(schemaName.length() + 1) : aTable;
        Map<String, Fields> queried = fieldsCache.query(schemaName, tableName);
        fieldsCache.fill(schemaName, queried, Collections.singletonMap(tableName, null));
    }

    public void removeTableMetadata(String aTableName) throws Exception {
        String schema = schemaFromTableName(aTableName);
        TablesFieldsCache cache = lookupFieldsCache(schema);
        if (cache != null) {
            cache.remove(aTableName);
        }
    }

    public void putTableMetadata(String aTableName, Fields aFields) throws Exception {
        String schema = schemaFromTableName(aTableName);
        TablesFieldsCache cache = lookupFieldsCache(schema);
        if (cache != null) {
            cache.put(aTableName, aFields);
        }
    }

    public boolean containsTableMetadata(String aTableName) throws Exception {
        return getTableMetadata(aTableName) != null;
    }

    public void removeTableIndexes(String aTableName) throws Exception {
        TablesIndexesCache cache = lookupIndexesCache(aTableName);
        if (cache != null) {
            cache.remove(aTableName);
        }
    }

    /**
     * Fills tables cache with fields, comments, keys (pk and fk) by connection
     * default schema.
     *
     * @throws Exception
     */
    public final void fillTablesCacheByConnectionSchema() throws Exception {
        fillTablesCacheBySchema(null);
    }

    /**
     * Fills tables cache with fields, comments, keys (pk and fk).
     *
     * @param aSchema A schema for witch we should achieve metadata information.
     * If it is null, connection default schema is used
     * @throws Exception
     */
    public void fillTablesCacheBySchema(String aSchema) throws Exception {
        String schema4Sql = aSchema != null && !aSchema.isEmpty() ? aSchema : getDatasourceSchema();
        CallableConsumer<Map<String, String>, String> tablesReader = (aSchema4Sql) -> {
            DataSource ds = client.obtainDataSource(datasourceName);
            try (Connection conn = ds.getConnection()) {
                try (ResultSet r = conn.getMetaData().getTables(null, aSchema4Sql, null, new String[]{ClientConstants.JDBCPKS_TABLE_TYPE_TABLE, ClientConstants.JDBCPKS_TABLE_TYPE_VIEW})) {
                    ColumnsIndicies idxs = new ColumnsIndicies(r.getMetaData());
                    int colIndex = idxs.find(ClientConstants.JDBCCOLS_TABLE_NAME);
                    int colRemarks = idxs.find(ClientConstants.JDBCCOLS_REMARKS);
                    assert colIndex > 0;
                    assert colRemarks > 0;
                    Map<String, String> tNames = new HashMap<>();
                    while (r.next()) {
                        String lTableName = r.getString(colIndex);
                        String lRemarks = r.getString(colRemarks);
                        tNames.put(lTableName, lRemarks);
                    }
                    return tNames;
                }
            }
        };
        Map<String, String> tablesNames = tablesReader.call(schema4Sql);
        if (schema4Sql != null && !schema4Sql.isEmpty() && tablesNames.isEmpty()) {
            tablesNames = tablesReader.call(schema4Sql.toLowerCase());
        }
        if (schema4Sql != null && !schema4Sql.isEmpty() && tablesNames.isEmpty()) {
            tablesNames = tablesReader.call(schema4Sql.toUpperCase());
        }
        TablesFieldsCache tablesFields = new TablesFieldsCache();
        Map<String, Fields> queried = tablesFields.query(aSchema, tablesNames.keySet());
        tablesFields.fill(aSchema, queried, tablesNames);
        schemasTablesFields.put(aSchema, tablesFields);
    }

    /**
     * Fills indexes cache.
     *
     * @param aSchema A schema for witch we should achieve metadata information.
     * If it is null, connection default schema is used
     * @param aTable
     * @throws Exception
     */
    public void fillIndexesCacheByTable(String aSchema, String aTable) throws Exception {
        TablesIndexesCache tablesIndexes = schemasTablesIndexes.get(aSchema);
        if (tablesIndexes == null) {
            tablesIndexes = new TablesIndexesCache();
            schemasTablesIndexes.put(aSchema, tablesIndexes);
        }
        DbTableIndexes tableIndexes = tablesIndexes.get(aTable);
        if (tableIndexes == null) {
            tableIndexes = tablesIndexes.query(aSchema, aTable);
            tablesIndexes.put(aTable, tableIndexes);
        }
    }

    public void clear() throws Exception {
        schemasTablesFields.clear();
        schemasTablesIndexes.clear();
        datasourceSchema = null;
        datasourceDriver = null;
    }

    public void removeSchema(String aSchema) {
        schemasTablesFields.remove(aSchema);
        schemasTablesIndexes.remove(aSchema);
    }

    public class TablesFieldsCache extends CaseInsesitiveMap<Fields> {

        public TablesFieldsCache() {
            super();
        }

        protected void resolveKeys(String aSchema, Map<String, Fields> aTablesFields, Map<String, DbTableKeys> aTablesKeys) throws Exception {
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
                aTablesFields.keySet().stream().forEach((String lTableName) -> {
                    Fields fields = aTablesFields.get(lTableName);
                    fields.setTableDescription(aTablesDescriptions.get(lTableName));
                    String fullTableName = lTableName;
                    if (aSchema != null && !aSchema.isEmpty()) {
                        fullTableName = aSchema + "." + fullTableName;
                    }
                    put(fullTableName, fields);
                });
            }
        }

        protected Map<String, Fields> query(String aSchema, Set<String> aTables) throws Exception {
            SqlDriver sqlDriver = getDatasourceSqlDriver();
            String schema4Sql = aSchema != null && !aSchema.isEmpty() ? aSchema : getDatasourceSchema();
            Map<String, Fields> columns;
            DataSource ds = client.obtainDataSource(datasourceName);
            try (Connection conn = ds.getConnection()) {
                DatabaseMetaData meta = conn.getMetaData();
                try (ResultSet r = meta.getColumns(null, schema4Sql, null, null)) {
                    columns = readTablesColumns(r, aSchema, sqlDriver);
                }
                if (schema4Sql != null && !schema4Sql.isEmpty() && columns.isEmpty()) {
                    schema4Sql = schema4Sql.toLowerCase();
                    try (ResultSet r = meta.getColumns(null, schema4Sql, null, null)) {
                        columns = readTablesColumns(r, aSchema, sqlDriver);
                    }
                }
                if (schema4Sql != null && !schema4Sql.isEmpty() && columns.isEmpty()) {
                    schema4Sql = schema4Sql.toUpperCase();
                    try (ResultSet r = meta.getColumns(null, schema4Sql, null, null)) {
                        columns = readTablesColumns(r, aSchema, sqlDriver);
                    }
                }
                Map<String, DbTableKeys> keys = new CaseInsesitiveMap<>();
                for (String aTable : aTables) {
                    try (ResultSet r = meta.getPrimaryKeys(null, schema4Sql, aTable)) {
                        DbTableKeys tableKeys = readTablesPrimaryKeys(r);
                        keys.put(aTable, tableKeys);
                    }
                    try (ResultSet r = meta.getImportedKeys(null, schema4Sql, aTable)) {
                        readTablesForeignKeys(r, aSchema, sqlDriver, keys);
                    }
                }
                resolveKeys(aSchema, columns, keys);
                return columns;
            }
        }

        protected Map<String, Fields> query(String aSchema, String aTable) throws Exception {
            SqlDriver sqlDriver = getDatasourceSqlDriver();
            String schema4Sql = aSchema != null && !aSchema.isEmpty() ? aSchema : getDatasourceSchema();
            Map<String, Fields> columns;
            DataSource ds = client.obtainDataSource(datasourceName);
            try (Connection conn = ds.getConnection()) {
                DatabaseMetaData meta = conn.getMetaData();
                try (ResultSet r = meta.getColumns(null, schema4Sql, aTable, null)) {
                    columns = readTablesColumns(r, aSchema, sqlDriver);
                }
                if (schema4Sql != null && !schema4Sql.isEmpty() && columns.isEmpty()) {
                    schema4Sql = schema4Sql.toLowerCase();
                    aTable = aTable.toLowerCase();
                    try (ResultSet r = meta.getColumns(null, schema4Sql, aTable, null)) {
                        columns = readTablesColumns(r, aSchema, sqlDriver);
                    }
                }
                if (schema4Sql != null && !schema4Sql.isEmpty() && columns.isEmpty()) {
                    schema4Sql = schema4Sql.toUpperCase();
                    aTable = aTable.toUpperCase();
                    try (ResultSet r = meta.getColumns(null, schema4Sql, aTable, null)) {
                        columns = readTablesColumns(r, aSchema, sqlDriver);
                    }
                }
                Map<String, DbTableKeys> keys = new CaseInsesitiveMap<>();
                try (ResultSet r = meta.getPrimaryKeys(null, schema4Sql, aTable)) {
                    DbTableKeys tableKeys = readTablesPrimaryKeys(r);
                    keys.put(aTable, tableKeys);
                }
                try (ResultSet r = meta.getImportedKeys(null, schema4Sql, aTable)) {
                    readTablesForeignKeys(r, aSchema, sqlDriver, keys);
                }
                resolveKeys(aSchema, columns, keys);
                return columns;
            }
        }

        protected Map<String, Fields> readTablesColumns(ResultSet r, String aSchema, SqlDriver sqlDriver) throws Exception {
            Map<String, Fields> tabledFields = new CaseInsesitiveMap<>();
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
                    JdbcField field = new JdbcField();
                    field.setName(fName.toLowerCase());
                    field.setDescription(fDescription);
                    field.setOriginalName(fName);
                    String rdbmsTypeName = r.getString(JDBCCOLS_TYPE_NAME_INDEX);
                    field.setType(rdbmsTypeName);
                    int jdbcType = r.getInt(JDBCCOLS_DATA_TYPE_INDEX);
                    field.setJdbcType(jdbcType);
                    int size = r.getInt(JDBCCOLS_COLUMN_SIZE_INDEX);
                    field.setSize(size);
                    int scale = r.getInt(JDBCCOLS_DECIMAL_DIGITS_INDEX);
                    field.setScale(scale);
                    int precision = r.getInt(JDBCCOLS_NUM_PREC_RADIX_INDEX);
                    field.setPrecision(precision);
                    int nullable = r.getInt(JDBCCOLS_NULLABLE_INDEX);
                    field.setNullable(nullable == ResultSetMetaData.columnNullable);
                    field.setSchemaName(aSchema);
                    field.setTableName(fTableName);
                    //
                    sqlDriver.getTypesResolver().resolveSize(field);
                    fields.add(field);
                }
            }
            return tabledFields;
        }

        protected DbTableKeys readTablesPrimaryKeys(ResultSet r) throws Exception {
            DbTableKeys dbPksFks = new DbTableKeys();
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
                    //
                    dbPksFks.addPrimaryKey(lpkSchema, lpkTableName, lpkField, lpkName);
                }
            }
            return dbPksFks;
        }

        protected void readTablesForeignKeys(ResultSet r, String aSchema, SqlDriver aSqlDriver, Map<String, DbTableKeys> tabledKeys) throws Exception {
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
                    short lfkUpdateRule = r.getShort(JDBCFKS_FKUPDATE_RULE_INDEX);
                    short lfkDeleteRule = r.getShort(JDBCFKS_FKDELETE_RULE_INDEX);
                    short lfkDeferability = r.getShort(JDBCFKS_FKDEFERRABILITY_INDEX);
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
                    dbPksFks.addForeignKey(lfkSchema, lfkTableName, lfkField, lfkName, ForeignKeySpec.ForeignKeyRule.valueOf(lfkUpdateRule), ForeignKeySpec.ForeignKeyRule.valueOf(lfkDeleteRule), lfkDeferability == 5, lpkSchema, lpkTableName, lpkField, lpkName);
                }
            }
        }
    }

    public boolean containsTableIndexes(String aTableName) throws Exception {
        return getTableIndexes(aTableName) != null;
    }

    public DbTableIndexes getTableIndexes(String aTableName) throws Exception {
        String schema = schemaFromTableName(aTableName);
        String pureTableName = pureTableName(aTableName);
        fillIndexesCacheByTable(schema, pureTableName);
        TablesIndexesCache cache = lookupIndexesCache(aTableName);
        return cache != null ? cache.get(aTableName) : null;
    }

    protected class TablesIndexesCache extends CaseInsesitiveMap<DbTableIndexes> {

        public TablesIndexesCache() {
            super();
        }

        protected DbTableIndexes query(String aSchema, String aTable) throws Exception {
            DbTableIndexes tableIndexes = new DbTableIndexes();
            String schema4Sql = aSchema != null && !aSchema.isEmpty() ? aSchema : getDatasourceSchema();
            DataSource ds = client.obtainDataSource(datasourceName);
            try (Connection conn = ds.getConnection()) {
                try {
                    try (ResultSet r = conn.getMetaData().getIndexInfo(null, schema4Sql, aTable, false, false)) {
                        ColumnsIndicies idxs = new ColumnsIndicies(r.getMetaData());
                        int JDBCIDX_INDEX_NAME = idxs.find(ClientConstants.JDBCIDX_INDEX_NAME);
                        int JDBCIDX_NON_UNIQUE = idxs.find(ClientConstants.JDBCIDX_NON_UNIQUE);
                        int JDBCIDX_TYPE = idxs.find(ClientConstants.JDBCIDX_TYPE);
                        //int JDBCIDX_TABLE_NAME = idxs.find(ClientConstants.JDBCIDX_TABLE_NAME);
                        int JDBCIDX_COLUMN_NAME = idxs.find(ClientConstants.JDBCIDX_COLUMN_NAME);
                        int JDBCIDX_ASC_OR_DESC = idxs.find(ClientConstants.JDBCIDX_ASC_OR_DESC);
                        int JDBCIDX_ORDINAL_POSITION = idxs.find(ClientConstants.JDBCIDX_ORDINAL_POSITION);
                        while (r.next()) {
                            //String tableName = r.getString(JDBCIDX_TABLE_NAME);
                            String idxName = r.getString(JDBCIDX_INDEX_NAME);
                            if (!r.wasNull()) {
                                DbTableIndexSpec idxSpec = tableIndexes.getIndexes().get(idxName);
                                if (idxSpec == null) {
                                    idxSpec = new DbTableIndexSpec();
                                    idxSpec.setName(idxName);
                                    tableIndexes.getIndexes().put(idxName, idxSpec);
                                }
                                boolean isUnique = r.getBoolean(JDBCIDX_NON_UNIQUE);
                                idxSpec.setUnique(isUnique);
                                short type = r.getShort(JDBCIDX_TYPE);
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
                                String sColumnName = r.getString(JDBCIDX_COLUMN_NAME);
                                if (!r.wasNull()) {
                                    DbTableIndexColumnSpec column = idxSpec.getColumn(sColumnName);
                                    if (column == null) {
                                        column = new DbTableIndexColumnSpec(sColumnName, true);
                                        idxSpec.addColumn(column);
                                    }
                                    String sAsc = r.getString(JDBCIDX_ASC_OR_DESC);
                                    if (!r.wasNull()) {
                                        column.setAscending(sAsc.toLowerCase().equals("a"));
                                    }
                                    short sPosition = r.getShort(JDBCIDX_ORDINAL_POSITION);
                                    column.setOrdinalPosition((int) sPosition);
                                }
                            }
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(MetadataCache.class.getName()).log(Level.WARNING, ex.toString());
                }
            }
            tableIndexes.sortIndexesColumns();
            return tableIndexes;
        }
    }
}
