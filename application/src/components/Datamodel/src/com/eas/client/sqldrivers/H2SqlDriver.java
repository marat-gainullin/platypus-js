package com.eas.client.sqldrivers;

import com.eas.client.ClientConstants;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.sqldrivers.resolvers.H2TypesResolver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author vv
 */
public class H2SqlDriver extends SqlDriver {

    // настройка экранирования наименования объектов БД
    private final TwinString[] charsForWrap = {new TwinString("\"", "\""), new TwinString("`", "`")};
    private final char[] restrictedChars = {' ', ',', '\'', '"'};

    protected TypesResolver resolver = new H2TypesResolver();
    protected static final int[] h2ErrorCodes = {};
    protected static final String[] platypusErrorMessages = {};
    protected static final String SET_SCHEMA_CLAUSE = "SET SCHEMA %s";
    protected static final String GET_SCHEMA_CLAUSE = "SELECT SCHEMA()";
    protected static final String CREATE_SCHEMA_CLAUSE = "CREATE SCHEMA IF NOT EXISTS %s";
    protected static final String SQL_SCHEMAS = ""
            + "SELECT " + ClientConstants.JDBCCOLS_TABLE_SCHEM + " FROM "
            + "(SELECT SCHEMA_NAME AS " + ClientConstants.JDBCCOLS_TABLE_SCHEM + " FROM INFORMATION_SCHEMA.SCHEMATA) schemas_alias "
            + "ORDER BY " + ClientConstants.JDBCCOLS_TABLE_SCHEM;
    protected static final String SQL_TABLES_VIEWS = ""
            + "SELECT"
            + "  TABLE_NAME AS " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + "  TABLE_SCHEMA AS " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + "  TABLE_TYPE AS " + ClientConstants.JDBCPKS_TABLE_TYPE_FIELD_NAME + ","
            + "  REMARKS AS " + ClientConstants.JDBCCOLS_REMARKS + " "
            + "FROM  INFORMATION_SCHEMA.TABLES tables ";
    protected static final String SQL_ALL_TABLES_VIEWS = SQL_TABLES_VIEWS
            + "ORDER BY TABLE_SCHEMA,TABLE_NAME";
    protected static final String SQL_SCHEMA_TABLES_VIEWS = SQL_TABLES_VIEWS
            + "WHERE TABLE_SCHEMA = '%s' "
            + "ORDER BY TABLE_SCHEMA,TABLE_NAME";
    protected static final String SQL_COLUMNS = ""
            + "SELECT"
            + "  TABLE_CAT,"
            + "  " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + "  " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + "  " + ClientConstants.JDBCCOLS_COLUMN_NAME + ","
            + "  " + ClientConstants.JDBCCOLS_TYPE_NAME + ","
            + "  " + ClientConstants.JDBCCOLS_DATA_TYPE + ","
            + "  " + ClientConstants.JDBCCOLS_COLUMN_SIZE + ","
            + "  " + ClientConstants.JDBCCOLS_NULLABLE + ","
            + "  " + ClientConstants.JDBCCOLS_DECIMAL_DIGITS + ","
            + "  " + ClientConstants.JDBCCOLS_NUM_PREC_RADIX + ","
            + "  " + ClientConstants.JDBCIDX_ORDINAL_POSITION + ","
            + "  " + ClientConstants.JDBCCOLS_REMARKS + ","
            + "  COLUMN_DEFAULT_VALUE "
            + "FROM"
            + "("
            + "SELECT"
            + "  table_catalog AS TABLE_CAT,"
            + "  table_schema AS " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + "  table_name AS " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + "  column_name AS " + ClientConstants.JDBCCOLS_COLUMN_NAME + ","
            + "  data_type AS " + ClientConstants.JDBCCOLS_DATA_TYPE + ","
            + "  type_name AS " + ClientConstants.JDBCCOLS_TYPE_NAME + ","
            + "  CHARACTER_MAXIMUM_LENGTH AS " + ClientConstants.JDBCCOLS_COLUMN_SIZE + ","
            + "  (CASE is_nullable WHEN 'YES'  then 1 else 0 end) AS " + ClientConstants.JDBCCOLS_NULLABLE + ","
            + "  numeric_scale AS " + ClientConstants.JDBCCOLS_DECIMAL_DIGITS + ","
            + "  10 AS " + ClientConstants.JDBCCOLS_NUM_PREC_RADIX + ","
            + "  ordinal_position AS " + ClientConstants.JDBCIDX_ORDINAL_POSITION + ","
            + "  REMARKS AS " + ClientConstants.JDBCCOLS_REMARKS + ","
            + "  column_default AS COLUMN_DEFAULT_VALUE "
            + "FROM information_schema.columns "
            + "WHERE table_schema = '%s' AND UPPER(table_name) in (%s) "
            + "ORDER BY table_schema, table_name, ordinal_position"
            + ") columns_alias";
    protected static final String SQL_INDEX_KEYS = ""
            + "SELECT"
            + "  TABLE_CAT,"
            + "  " + ClientConstants.JDBCIDX_TABLE_SCHEM + ","
            + "  " + ClientConstants.JDBCIDX_TABLE_NAME + ","
            + "  " + ClientConstants.JDBCIDX_NON_UNIQUE + ","
            + "  " + ClientConstants.JDBCIDX_INDEX_QUALIFIER + ","
            + "  " + ClientConstants.JDBCIDX_INDEX_NAME + ","
            + "  " + ClientConstants.JDBCIDX_TYPE + ","
            + "  " + ClientConstants.JDBCIDX_ORDINAL_POSITION + ","
            + "  " + ClientConstants.JDBCIDX_COLUMN_NAME + ","
            + "  " + ClientConstants.JDBCIDX_ASC_OR_DESC + ","
            + "  CARDINALITY,"
            + "  PAGES,"
            + "  FILTER_CONDITION,"
            + "  " + ClientConstants.JDBCIDX_PRIMARY_KEY + ","
            + "  " + ClientConstants.JDBCIDX_FOREIGN_KEY + " "
            + "FROM"
            + "("
            + "SELECT"
            + "  table_catalog AS TABLE_CAT,"
            + "  table_schema AS " + ClientConstants.JDBCIDX_TABLE_SCHEM + ","
            + "  table_name AS " + ClientConstants.JDBCIDX_TABLE_NAME + ","
            + "  CASE WHEN " + ClientConstants.JDBCIDX_NON_UNIQUE + " = TRUE"
            + "     THEN 1 ELSE 0 END AS " + ClientConstants.JDBCIDX_NON_UNIQUE + ","
            + "  NULL AS " + ClientConstants.JDBCIDX_INDEX_QUALIFIER + ","
            + "  index_name as " + ClientConstants.JDBCIDX_INDEX_NAME + ","
            + "  CASE WHEN index_type_name = 'HASH INDEX' THEN 2 else index_type end AS " + ClientConstants.JDBCIDX_TYPE + ","
            + "  ORDINAL_POSITION AS " + ClientConstants.JDBCIDX_ORDINAL_POSITION + ","
            + "  column_name AS " + ClientConstants.JDBCIDX_COLUMN_NAME + ","
            + "  ASC_OR_DESC AS " + ClientConstants.JDBCIDX_ASC_OR_DESC + ","
            + "  cardinality AS CARDINALITY,"
            + "  PAGES,"
            + "  FILTER_CONDITION,"
            + "  (case when primary_key = 'TRUE' then 0 else 1 end) AS " + ClientConstants.JDBCIDX_PRIMARY_KEY + ","
            + "  (select distinct r.FK_NAME from  INFORMATION_SCHEMA.CROSS_REFERENCES r WHERE r.FKTABLE_SCHEMA = i.table_schema"
            + "        and r.fktable_name = i.table_name and i.index_name like r.FK_NAME||'%%') AS " + ClientConstants.JDBCIDX_FOREIGN_KEY + " "
            + "FROM INFORMATION_SCHEMA.INDEXES i "
            + "WHERE table_schema = '%s' AND UPPER(table_name) in (%s) "
            + "ORDER BY non_unique, index_name, ORDINAL_POSITION "
            + ") indexes_alias";
    protected static final String SQL_PRIMARY_KEYS = ""
            + "SELECT"
            + "  TABLE_CAT,"
            + "  " + ClientConstants.JDBCPKS_TABLE_SCHEM + ","
            + "  " + ClientConstants.JDBCPKS_TABLE_NAME + ","
            + "  " + ClientConstants.JDBCPKS_COLUMN_NAME + ","
            + "  KEY_SEQ,"
            + "  " + ClientConstants.JDBCPKS_CONSTRAINT_NAME + " "
            + "FROM"
            + "("
            + "SELECT"
            + "  t.table_catalog AS TABLE_CAT,"
            + "  t.table_schema AS " + ClientConstants.JDBCPKS_TABLE_SCHEM + ","
            + "  t.table_name AS " + ClientConstants.JDBCPKS_TABLE_NAME + ","
            + "  t.column_name AS " + ClientConstants.JDBCPKS_COLUMN_NAME + ","
            + "  t.ordinal_position AS KEY_SEQ,"
            + "  t.INDEX_NAME AS " + ClientConstants.JDBCPKS_CONSTRAINT_NAME + " "
            + "FROM  INFORMATION_SCHEMA.INDEXES t "
            + "WHERE"
            + "  t.PRIMARY_KEY = 'TRUE' AND"
            + "  t.table_schema = '%s' AND UPPER(t.table_name) in (%s) "
            + "ORDER BY t.table_catalog, t.table_schema, t.table_name, t.ordinal_position"
            + ") pkeys_alias";
    protected static final String SQL_FOREIGN_KEYS = ""
            + "SELECT"
            + "  PKTABLE_CAT,"
            + "  " + ClientConstants.JDBCFKS_FKPKTABLE_SCHEM + ","
            + "  " + ClientConstants.JDBCFKS_FKPKTABLE_NAME + ","
            + "  " + ClientConstants.JDBCFKS_FKPK_NAME + ","
            + "  " + ClientConstants.JDBCFKS_FKPKCOLUMN_NAME + ","
            + "  FKTABLE_CAT,"
            + "  " + ClientConstants.JDBCFKS_FKTABLE_SCHEM + ","
            + "  " + ClientConstants.JDBCFKS_FKTABLE_NAME + ","
            + "  " + ClientConstants.JDBCFKS_FK_NAME + ","
            + "  " + ClientConstants.JDBCFKS_FKCOLUMN_NAME + ","
            + "  KEY_SEQ,"
            + "  " + ClientConstants.JDBCFKS_FKUPDATE_RULE + ","
            + "  " + ClientConstants.JDBCFKS_FKDELETE_RULE + ","
            + "  " + ClientConstants.JDBCFKS_FKDEFERRABILITY + " "
            + "FROM"
            + "("
            + "SELECT"
            + "  r.pktable_catalog AS PKTABLE_CAT,"
            + "  r.PKTABLE_SCHEMA AS " + ClientConstants.JDBCFKS_FKPKTABLE_SCHEM + ","
            + "  " + ClientConstants.JDBCFKS_FKPKTABLE_NAME + ","
            + "  " + ClientConstants.JDBCFKS_FKPK_NAME + ","
            + "  " + ClientConstants.JDBCFKS_FKPKCOLUMN_NAME + ","
            + "  r.fktable_catalog AS FKTABLE_CAT,"
            + "  r.FKTABLE_SCHEMA AS " + ClientConstants.JDBCFKS_FKTABLE_SCHEM + ","
            + "  " + ClientConstants.JDBCFKS_FKTABLE_NAME + ","
            + "  " + ClientConstants.JDBCFKS_FK_NAME + ","
            + "  " + ClientConstants.JDBCFKS_FKCOLUMN_NAME + ","
            + "  ordinal_position AS KEY_SEQ,"
            + "  r.update_rule AS " + ClientConstants.JDBCFKS_FKUPDATE_RULE + ","// 0=CASCADE; 1=RESTRICT | NO ACTION; 2=SET NULL 
            + "  r.delete_rule AS " + ClientConstants.JDBCFKS_FKDELETE_RULE + ","// 0=CASCADE; 1=RESTRICT | NO ACTION; 2=SET NULL
            + "  7 AS " + ClientConstants.JDBCFKS_FKDEFERRABILITY + " "// 5- , 6- , 7- not aplicable
            + "FROM"
            + "  INFORMATION_SCHEMA.CROSS_REFERENCES  r "
            + "WHERE"
            + "  r.pktable_schema  = '%s' AND UPPER(r.fktable_name) in (%s) "
            + "ORDER BY r.pktable_catalog, r.pktable_schema, r.pktable_name, r.ordinal_position"
            + ") fkeys_alias";
    protected static final String SQL_CREATE_EMPTY_TABLE = "CREATE TABLE %s (%s DECIMAL(18,0) NOT NULL PRIMARY KEY)";
    protected static final String SQL_CREATE_TABLE_COMMENT = "COMMENT ON TABLE %s IS '%s'";
    protected static final String SQL_CREATE_COLUMN_COMMENT = "COMMENT ON COLUMN %s IS '%s'";
    protected static final String SQL_DROP_TABLE = "DROP TABLE %s";
    protected static final String SQL_CREATE_INDEX = "CREATE %s INDEX %s ON %s (%s)";
    protected static final String SQL_DROP_INDEX = "DROP INDEX %s";
    protected static final String SQL_ADD_PK = "ALTER TABLE %s ADD %s PRIMARY KEY (%s)";
    protected static final String SQL_DROP_PK = "ALTER TABLE %s DROP PRIMARY KEY";
    protected static final String SQL_ADD_FK = "ALTER TABLE %s ADD CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s (%s) %s";
    protected static final String SQL_DROP_FK = "ALTER TABLE %s DROP CONSTRAINT %s";
    protected static final String SQL_PARENTS_LIST = ""
            + "WITH RECURSIVE parents(mdent_id, mdent_parent_id) AS "
            + "( "
            + "SELECT m1.mdent_id, m1.mdent_parent_id FROM mtd_entities m1 WHERE m1.mdent_id = %s "
            + "    UNION ALL "
            + "SELECT m2.mdent_id, m2.mdent_parent_id FROM parents p, mtd_entities m2 WHERE m2.mdent_id = p.mdent_parent_id "
            + ") "
            + "SELECT mdent_id, mdent_parent_id FROM parents";
    protected static final String SQL_CHILDREN_LIST = ""
            + "WITH recursive children(mdent_id, mdent_name, mdent_parent_id, mdent_type, mdent_content_txt, mdent_content_txt_size, mdent_content_txt_crc32) AS"
            + "( "
            + "SELECT m1.mdent_id, m1.mdent_name, m1.mdent_parent_id, m1.mdent_type, m1.mdent_content_txt, m1.mdent_content_txt_size, m1.mdent_content_txt_crc32 FROM mtd_entities m1 WHERE m1.mdent_id = :%s "
            + "    union all "
            + "SELECT m2.mdent_id, m2.mdent_name, m2.mdent_parent_id, m2.mdent_type, m2.mdent_content_txt, m2.mdent_content_txt_size, m2.mdent_content_txt_crc32 FROM children c, mtd_entities m2 WHERE c.mdent_id = m2.mdent_parent_id "
            + ") "
            + "SELECT mdent_id, mdent_name, mdent_parent_id, mdent_type, mdent_content_txt, mdent_content_txt_size, mdent_content_txt_crc32 FROM children";
    protected static final String SQL_RENAME_COLUMN = "ALTER TABLE %s ALTER COLUMN %s RENAME TO %s";
    protected static final String SQL_CHANGE_COLUMN_TYPE = "ALTER TABLE %s ALTER COLUMN %s %s";
    protected static final String SQL_CHANGE_COLUMN_NULLABLE = "ALTER TABLE %s ALTER COLUMN %s SET %s NULL";

    public H2SqlDriver() {
        super();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isConstraintsDeferrable() {
        return false;
    }

    /**
     * @inheritDoc
     */
    @Override
    public TypesResolver getTypesResolver() {
        return resolver;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getUsersSpaceInitResourceName() {
        return "/" + H2SqlDriver.class.getPackage().getName().replace(".", "/") + "/sqlscripts/H2InitUsersSpace.sql";
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getVersionInitResourceName() {
        return "/" + H2SqlDriver.class.getPackage().getName().replace(".", "/") + "/sqlscripts/H2InitVersion.sql";
    }

    /**
     * @inheritDoc
     */
    @Override
    public Set<Integer> getSupportedJdbcDataTypes() {
        return resolver.getSupportedJdbcDataTypes();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void applyContextToConnection(Connection aConnection, String aSchema) throws Exception {
        if (aSchema != null && !aSchema.isEmpty()) {
            try (Statement stmt = aConnection.createStatement()) {
                stmt.execute(String.format(SET_SCHEMA_CLAUSE, wrapNameIfRequired(aSchema)));
            }
        }
    }

    @Override
    public String getSql4GetConnectionContext() {
        return GET_SCHEMA_CLAUSE;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4TablesEnumeration(String schema4Sql) {
        if (schema4Sql == null || schema4Sql.isEmpty()) {
            return SQL_ALL_TABLES_VIEWS;
        } else {
            return String.format(SQL_SCHEMA_TABLES_VIEWS, prepareName(schema4Sql));
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4SchemasEnumeration() {
        return SQL_SCHEMAS;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4CreateSchema(String aSchemaName, String aPassword) {
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            return String.format(CREATE_SCHEMA_CLAUSE, aSchemaName);
        }
        throw new IllegalArgumentException("Schema name is null or empty.");
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4TableColumns(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            String tablesIn = constructIn(aTableNames);
            return String.format(SQL_COLUMNS, prepareName(aOwnerName), tablesIn.toUpperCase());
        } else {
            return null;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4TablePrimaryKeys(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            String tablesIn = constructIn(aTableNames);
            return String.format(SQL_PRIMARY_KEYS, prepareName(aOwnerName), tablesIn.toUpperCase());
        } else {
            return null;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4TableForeignKeys(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            return String.format(SQL_FOREIGN_KEYS, prepareName(aOwnerName), constructIn(aTableNames).toUpperCase());
        } else {
            return null;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4Indexes(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            String tablesIn = constructIn(aTableNames);
            return String.format(SQL_INDEX_KEYS, prepareName(aOwnerName), tablesIn.toUpperCase());
        } else {
            return null;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public String[] getSql4CreateColumnComment(String aOwnerName, String aTableName, String aFieldName, String aDescription) {
        String fullName = wrapNameIfRequired(aTableName) + "." + wrapNameIfRequired(aFieldName);
        if (aOwnerName != null && !aOwnerName.isEmpty()) {
            fullName = wrapNameIfRequired(aOwnerName) + "." + fullName;
        }
        if (aDescription == null) {
            aDescription = "";
        }
        return new String[]{String.format(SQL_CREATE_COLUMN_COMMENT, fullName, escapeSingleQuote(aDescription))};
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4CreateTableComment(String aOwnerName, String aTableName, String aDescription) {
        String fullName = makeFullName(aOwnerName, aTableName);
        if (aDescription == null) {
            aDescription = "";
        }
        return String.format(SQL_CREATE_TABLE_COMMENT, fullName, escapeSingleQuote(aDescription));
    }

    private String escapeSingleQuote(String str) {
        return str.replaceAll("'", "''"); //NOI18N
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4DropTable(String aSchemaName, String aTableName) {
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            return String.format(SQL_DROP_TABLE, wrapNameIfRequired(aSchemaName) + "." + wrapNameIfRequired(aTableName));
        } else {
            return String.format(SQL_DROP_TABLE, wrapNameIfRequired(aTableName));
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4DropIndex(String aSchemaName, String aTableName, String aIndexName) {
        String indexName = makeFullName(aSchemaName, aIndexName);
        return String.format(SQL_DROP_INDEX, indexName);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4DropFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        String constraintName = wrapNameIfRequired(aFk.getCName());
        String tableName = makeFullName(aSchemaName, aFk.getTable());
        return String.format(SQL_DROP_FK, tableName, constraintName);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String[] getSql4CreatePkConstraint(String aSchemaName, List<PrimaryKeySpec> listPk) {

        if (listPk != null && listPk.size() > 0) {
            PrimaryKeySpec pk = listPk.get(0);
            String tableName = pk.getTable();
            String pkTableName = makeFullName(aSchemaName, tableName);
            String pkName = wrapNameIfRequired(generatePkName(tableName, PKEY_NAME_SUFFIX));
            String pkColumnName = wrapNameIfRequired(pk.getField());
            for (int i = 1; i < listPk.size(); i++) {
                pk = listPk.get(i);
                pkColumnName += ", " + wrapNameIfRequired(pk.getField());
            }
            return new String[]{
                String.format(SQL_ADD_PK, pkTableName, "CONSTRAINT " + pkName, pkColumnName)
            };
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4DropPkConstraint(String aSchemaName, PrimaryKeySpec aPk) {
        String pkTableName = makeFullName(aSchemaName, aPk.getTable());
        return String.format(SQL_DROP_PK, pkTableName);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        List<ForeignKeySpec> fkList = new ArrayList<>();
        fkList.add(aFk);
        return getSql4CreateFkConstraint(aSchemaName, fkList);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, List<ForeignKeySpec> listFk) {
        if (listFk != null && listFk.size() > 0) {
            ForeignKeySpec fk = listFk.get(0);
            String fkTableName = makeFullName(aSchemaName, fk.getTable());
            String fkName = fk.getCName();
            String fkColumnName = wrapNameIfRequired(fk.getField());

            PrimaryKeySpec pk = fk.getReferee();
            String pkSchemaName = pk.getSchema();
            String pkTableName = makeFullName(aSchemaName, pk.getTable());
            String pkColumnName = wrapNameIfRequired(pk.getField());

            for (int i = 1; i < listFk.size(); i++) {
                fk = listFk.get(i);
                pk = fk.getReferee();
                fkColumnName += ", " + wrapNameIfRequired(fk.getField());
                pkColumnName += ", " + wrapNameIfRequired(pk.getField());
            }

            String fkRule = "";
            switch (fk.getFkUpdateRule()) {
                case CASCADE:
                    fkRule += " ON UPDATE CASCADE";
                    break;
                case NOACTION:
//                case SETDEFAULT:
                    fkRule += " ON UPDATE NO ACTION";
                    break;
                case SETDEFAULT:
                    fkRule += " ON UPDATE SET DEFAULT";
                    break;
                case SETNULL:
                    fkRule += " ON UPDATE SET NULL";
                    break;
            }
            switch (fk.getFkDeleteRule()) {
                case CASCADE:
                    fkRule += " ON DELETE CASCADE";
                    break;
                case NOACTION:
//                case SETDEFAULT:
                    fkRule += " ON DELETE NO ACTION";
                    break;
                case SETDEFAULT:
                    fkRule += " ON DELETE SET DEFAULT";
                    break;
                case SETNULL:
                    fkRule += " ON DELETE SET NULL";
                    break;
            }
            return String.format(SQL_ADD_FK, fkTableName, fkName.isEmpty() ? "" : wrapNameIfRequired(fkName), fkColumnName, pkTableName, pkColumnName, fkRule);
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4CreateIndex(String aSchemaName, String aTableName, DbTableIndexSpec aIndex) {
        assert aIndex.getColumns().size() > 0 : "index definition must consist of at least 1 column";

        String tableName = makeFullName(aSchemaName, aTableName);
        String fieldsList = "";
        for (int i = 0; i < aIndex.getColumns().size(); i++) {
            DbTableIndexColumnSpec column = aIndex.getColumns().get(i);
            fieldsList += wrapNameIfRequired(column.getColumnName());
            if (!column.isAscending()) {
                fieldsList += " DESC";
            }
            if (i != aIndex.getColumns().size() - 1) {
                fieldsList += ", ";
            }
        }
        return String.format(SQL_CREATE_INDEX,
                (aIndex.isUnique() ? "UNIQUE " : "") + (aIndex.isHashed() ? "HASH " : ""),
                wrapNameIfRequired(aIndex.getName()),
                tableName,
                fieldsList);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4EmptyTableCreation(String aSchemaName, String aTableName, String aPkFieldName) {
        String fullName = makeFullName(aSchemaName, aTableName);
        return String.format(SQL_CREATE_EMPTY_TABLE, fullName, wrapNameIfRequired(aPkFieldName));
    }

    /**
     * @inheritDoc
     */
    @Override
    public String parseException(Exception ex) {
        if (ex != null && ex instanceof SQLException) {
            SQLException sqlEx = (SQLException) ex;
            int errorCode = sqlEx.getErrorCode();
            for (int i = 0; i < h2ErrorCodes.length; i++) {
                if (errorCode == h2ErrorCodes[i]) {
                    return platypusErrorMessages[i];
                }
            }
        }
        return ex.getLocalizedMessage();
    }

    private String getFieldTypeDefinition(Field aField) {
        resolver.resolve2RDBMS(aField);
        String typeDefine = "";
        String sqlTypeName = aField.getTypeInfo().getSqlTypeName().toLowerCase();
        typeDefine += sqlTypeName;
        // field length
        int size = aField.getSize();
        int scale = aField.getScale();

        if (resolver.isScaled(sqlTypeName) && size > 0) {
            typeDefine += "(" + String.valueOf(size) + "," + String.valueOf(scale) + ")";
        } else {
            if (resolver.isSized(sqlTypeName) && size > 0) {
                typeDefine += "(" + String.valueOf(size) + ")";
            }
        }
        return typeDefine;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4FieldDefinition(Field aField) {
        String fieldDefinition = wrapNameIfRequired(aField.getName()) + " " + getFieldTypeDefinition(aField);

        if (!aField.isNullable()) {
            fieldDefinition += " NOT NULL";
        } else {
            fieldDefinition += " NULL";
        }
        if (aField.isPk()) {
            fieldDefinition += " PRIMARY KEY";
        }
        return fieldDefinition;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String[] getSqls4ModifyingField(String aSchemaName, String aTableName, Field aOldFieldMd, Field aNewFieldMd) {
        assert aOldFieldMd.getName().toLowerCase().equals(aNewFieldMd.getName().toLowerCase());
        List<String> sql = new ArrayList<>();

        //Change data type
        String lOldTypeName = aOldFieldMd.getTypeInfo().getSqlTypeName();
        if (lOldTypeName == null) {
            lOldTypeName = "";
        }
        String lNewTypeName = aNewFieldMd.getTypeInfo().getSqlTypeName();
        if (lNewTypeName == null) {
            lNewTypeName = "";
        }

        String fullTableName = makeFullName(aSchemaName, aTableName);
        if (aOldFieldMd.getTypeInfo().getSqlType() != aNewFieldMd.getTypeInfo().getSqlType()
                || !lOldTypeName.equalsIgnoreCase(lNewTypeName)
                || aOldFieldMd.getSize() != aNewFieldMd.getSize()
                || aOldFieldMd.getScale() != aNewFieldMd.getScale()) {
            sql.add(String.format(
                    SQL_CHANGE_COLUMN_TYPE,
                    fullTableName,
                    wrapNameIfRequired(aOldFieldMd.getName()),
                    getFieldTypeDefinition(aNewFieldMd)));
        }

        //Change nullable
        String not = "";
        if (aOldFieldMd.isNullable() != aNewFieldMd.isNullable()) {
            if (!aNewFieldMd.isNullable()) {
                not = "NOT";
            }
            sql.add(String.format(
                    SQL_CHANGE_COLUMN_NULLABLE,
                    fullTableName,
                    wrapNameIfRequired(aOldFieldMd.getName()),
                    not));
        }

        return sql.toArray(new String[0]);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String[] getSqls4RenamingField(String aSchemaName, String aTableName, String aOldFieldName, Field aNewFieldMd) {
        String fullTableName = makeFullName(aSchemaName, aTableName);
        String renameSQL = String.format(SQL_RENAME_COLUMN, fullTableName, wrapNameIfRequired(aOldFieldName), wrapNameIfRequired(aNewFieldMd.getName()));
        return new String[]{renameSQL};
    }

    /**
     * @inheritDoc
     */
    @Override
    public Integer getJdbcTypeByRDBMSTypename(String aLowLevelTypeName) {
        return resolver.getJdbcTypeByRDBMSTypename(aLowLevelTypeName);
    }

    @Override
    public String[] getSqls4AddingField(String aSchemaName, String aTableName, Field aField) {
        String fullTableName = makeFullName(aSchemaName, aTableName);
        return new String[]{
            String.format(SqlDriver.ADD_FIELD_SQL_PREFIX, fullTableName) + getSql4FieldDefinition(aField)
        };
    }

    @Override
    public TwinString[] getCharsForWrap() {
        return charsForWrap;
    }

    @Override
    public char[] getRestrictedChars() {
        return restrictedChars;
    }

    @Override
    public boolean isHadWrapped(String aName) {
        return isHaveLowerCase(aName);
    }

    private String prepareName(String aName) {
        return (isWrappedName(aName) ? unwrapName(aName) : aName.toUpperCase());
    }
}
