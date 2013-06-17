/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers;

import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.RowsetConverter;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.bearsoft.rowset.metadata.PrimaryKeySpec;
import com.eas.client.ClientConstants;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.sqldrivers.resolvers.MsSqlTypesResolver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mg
 */
public class MsSqlSqlDriver extends SqlDriver {

    protected static final String COMMIT_DDL_CLAUSE = "begin %s; commit; end";
    protected static final String GET_SCHEMA_CLAUSE = "SELECT SCHEMA_NAME()";
    protected static final String CREATE_SCHEMA_CLAUSE = "CREATE SCHEMA %s";
    protected static final Converter converter = new RowsetConverter();
    protected static final MsSqlTypesResolver resolver = new MsSqlTypesResolver();
    protected static final int[] mssqlErrorCodes = {
        826
    };
    protected static final String[] platypusErrorMessages = {
        EAS_TABLE_ALREADY_EXISTS
    };
    public static final String SQL_ALL_TABLES = ""
            + "Select"
            + " t.name as " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + " s.name as " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + " (case type_desc when 'USER_TABLE' then '" + ClientConstants.JDBCPKS_TABLE_TYPE_TABLE + "' else type_desc end) as " + ClientConstants.JDBCPKS_TABLE_TYPE_FIELD_NAME + " "
            + "from sys.all_objects as t inner join sys.schemas as s on (s.schema_id = t.schema_id) "
            + "where t.type in  ('U','V')";
    public static final String SQL_SCHEMA_TABLES = SQL_ALL_TABLES
            + " and Upper(s.name)=Upper('%s')"
            + " order by " + ClientConstants.JDBCCOLS_TABLE_SCHEM + "," + ClientConstants.JDBCCOLS_TABLE_NAME;
    public static final String SQL_SCHEMAS = ""
            + "Select s.name as " + ClientConstants.JDBCCOLS_TABLE_SCHEM + " from sys.schemas as s "
            + "order by " + ClientConstants.JDBCCOLS_TABLE_SCHEM;
    public static final String SQL_COLUMNS = ""
            + "SELECT "
            + " schemas.name AS " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + " tables.name AS " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + " columns.name AS " + ClientConstants.JDBCCOLS_COLUMN_NAME + ","
            + " columns.column_id as ordinal_position,"
            + " types.user_type_id as data_type,"
            + " types.name as " + ClientConstants.JDBCCOLS_TYPE_NAME + ","
            + " (case when types.user_type_id in (231,239) then columns.max_length/2 else columns.max_length end) as column_size,"
            + " columns.scale as decimal_digits,"
            + " columns.precision as NUM_PREC_RADIX,"
            + " (case columns.is_nullable when 1 then 1 else 0 end) as nullable," //в выражении case происходит конвертация типов
            + " null as REMARKS "
            + "FROM sys.columns AS columns"
            + " INNER JOIN sys.tables AS tables ON columns.object_id = tables.object_id"
            + " INNER JOIN sys.schemas AS schemas ON schemas.schema_id = tables.schema_id"
            + " INNER JOIN sys.types as types on columns.user_type_id = types.user_type_id "
            + "WHERE Upper(schemas.name)=Upper('%s') and Upper(tables.name) in (%s) ";
    public static final String SQL_PRIMARY_KEYS = ""
            + "SELECT"
            + " schemas.name as " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + " tables.name as " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + " columns.name as " + ClientConstants.JDBCCOLS_COLUMN_NAME + ","
            + " constraints.name as pk_name,"
            + " idxcols.key_ordinal AS  KEY_SEQ "
            + "FROM sys.key_constraints as constraints"
            + " inner join sys.schemas schemas on constraints.schema_id = schemas.schema_id"
            + " inner join sys.tables tables on constraints.parent_object_id = tables.object_id"
            + " inner join sys.index_columns idxcols on constraints.unique_index_id = idxcols.index_id and idxcols.object_id = tables.object_id"
            + " inner join sys.columns columns on columns.object_id = tables.object_id and columns.column_id=idxcols.column_id "
            + "WHERE Upper(schemas.name) = '%s' and Upper(tables.name) in (%s) AND constraints.type_desc = 'PRIMARY_KEY_CONSTRAINT' "
            + "ORDER BY schemas.name, tables.name, idxcols.key_ordinal";
    protected static final String SQL_FOREIGN_KEYS = ""
            + "SELECT"
            + " ref_schema.name as fktable_schem,"
            + " ref_tables.name as fktable_name,"
            + " ref_columns.name as fkcolumn_name,"
            + " pk_schema.name  as pktable_schem,"
            + " pk_tables.name  as pktable_name,"
            + " pk_columns.name  as pkcolumn_name,"
            + " constraint_columns.constraint_column_id as key_seq,"
            + " case constraints.update_referential_action_desc when 'CASCADE' then 0 when 'NO_ACTION' then 1 else 2 end as update_rule,"
            + " case constraints.delete_referential_action_desc when 'CASCADE' then 0 when 'NO_ACTION' then 1 else 2 end as delete_rule,"
            + " constraints.name as fk_name, pk.name as pk_name, 7 as deferrability "
            + "FROM sys.foreign_key_columns constraint_columns"
            + " inner join sys.tables pk_tables on pk_tables.object_id = constraint_columns.referenced_object_id"
            + " inner join sys.tables ref_tables on ref_tables.object_id = constraint_columns.parent_object_id"
            + " inner join sys.columns pk_columns on pk_columns.object_id = pk_tables.object_id and pk_columns.column_id = constraint_columns.referenced_column_id"
            + " inner join sys.columns ref_columns on ref_columns.object_id = ref_tables.object_id and ref_columns.column_id = constraint_columns.parent_column_id"
            + " inner join sys.schemas pk_schema on pk_tables.schema_id = pk_schema.schema_id"
            + " inner join sys.schemas ref_schema on ref_tables.schema_id = ref_schema.schema_id"
            + " inner join sys.foreign_keys constraints on constraints.object_id = constraint_columns.constraint_object_id"
            + " inner join sys.key_constraints pk on pk.parent_object_id = pk_tables.object_id "
            + "WHERE Upper(ref_schema.name) = Upper('%s') and Upper(ref_tables.name) in (%s) "
            + "ORDER BY ref_schema.name,ref_tables.name,constraint_columns.constraint_column_id";
    protected static final String SQL_INDEX_KEYS = ""
            + "SELECT"
            + " null table_cat,"
            + " schemas.name as " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + " tables.name AS " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + " (case indexes.is_unique when 1 then 0 else 1 end) as non_unique,"
            + " null index_qualifier,"
            + " indexes.name as index_name,"
            + " 1 AS type,"
            + " clindexes.key_ordinal ordinal_position,"
            + " columns.name as column_name,"
            + " null asc_or_desc,"
            + " null cardinality,"
            + " null pages,"
            + " null filter_condition,"
            + " (CASE WHEN indexes.is_primary_key = 1 THEN 0 ELSE 1 END) AS " + ClientConstants.JDBCIDX_PRIMARY_KEY + ","
            + " null AS " + ClientConstants.JDBCIDX_FOREIGN_KEY + " "
            + "FROM sys.columns AS columns"
            + " INNER JOIN sys.tables AS tables ON columns.object_id = tables.object_id"
            + " INNER JOIN sys.schemas AS schemas ON schemas.schema_id = tables.schema_id"
            + " INNER JOIN sys.index_columns AS clindexes ON clindexes.object_id = columns.object_id and clindexes.column_id=columns.column_id"
            + " INNER JOIN sys.indexes AS indexes ON indexes.object_id = clindexes.object_id and indexes.index_id = clindexes.index_id "
            + "WHERE Upper(schemas.name) = Upper('%s') AND Upper(tables.name) in (%s) "
            + "ORDER BY non_unique, type, index_name, ordinal_position";
    public static final String SQL_COLUMNS_COMMENTS = ""
            + "SELECT"
            + " schemas.name AS " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + " tables.name AS " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + " columns.name AS " + ClientConstants.JDBCCOLS_COLUMN_NAME + ","
            + " cast(sys.extended_properties.value as varchar(200)) AS COMMENTS "
            + "FROM sys.columns AS columns"
            + " INNER JOIN sys.tables AS tables ON columns.object_id = tables.object_id"
            + " INNER JOIN sys.schemas AS schemas ON schemas.schema_id = tables.schema_id"
            + " LEFT OUTER JOIN sys.extended_properties ON sys.extended_properties.major_id = columns.object_id AND sys.extended_properties.minor_id = columns.column_id "
            + "WHERE (UPPER(sys.extended_properties.name) LIKE UPPER('%%description%%')) and Upper(schemas.name)=Upper('%s') and Upper(tables.name) in (%s)";
    public static final String SQL_TABLE_COMMENTS = ""
            + "SELECT"
            + " schemas.name AS " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + " tables.name AS " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + " cast(sys.extended_properties.value as varchar(200)) AS COMMENTS "
            + "FROM sys.tables AS tables"
            + " INNER JOIN sys.schemas AS schemas ON schemas.schema_id = tables.schema_id"
            + " LEFT OUTER JOIN sys.extended_properties ON sys.extended_properties.major_id = tables.object_id AND "
            + " (sys.extended_properties.minor_id IS NULL OR sys.extended_properties.minor_id = 0) "
            + "WHERE (UPPER(sys.extended_properties.name) LIKE UPPER('%%description%%')) and Upper(schemas.name)=Upper('%s') and Upper(tables.name) in (%s)";
    public static final String SQL_ALL_OWNER_TABLES_COMMENTS = ""
            + "SELECT"
            + " schemas.name AS " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + " tables.name AS " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + " cast(sys.extended_properties.value as varchar(200)) AS COMMENTS "
            + "FROM sys.tables AS tables"
            + " INNER JOIN sys.schemas AS schemas ON schemas.schema_id = tables.schema_id"
            + " LEFT OUTER JOIN sys.extended_properties ON sys.extended_properties.major_id = tables.object_id AND"
            + " (sys.extended_properties.minor_id IS NULL OR sys.extended_properties.minor_id = 0) "
            + "WHERE (UPPER(sys.extended_properties.name) LIKE UPPER('%%description%%')) and Upper(schemas.name)=Upper('%s')";
    protected static final String ADD_COLUMN_COMMENT_CLAUSE = ""
            + "begin "
            + "begin try "
            + "EXEC sys.sp_dropextendedproperty @name=N'MS_Description' , @level0type=N'SCHEMA',@level0name=N'%s', @level1type=N'TABLE',@level1name=N'%s', @level2type=N'COLUMN',@level2name=N'%s' "
            + "end try "
            + "begin catch "
            + "end catch "
            + "EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'%s' , @level0type=N'SCHEMA',@level0name=N'%s', @level1type=N'TABLE',@level1name=N'%s', @level2type=N'COLUMN',@level2name=N'%s' "
            + " commit "
            + "end ";
    protected static final String ADD_TABLE_COMMENT_CLAUSE = ""
            + "begin "
            + "  begin try "
            + "    EXEC sys.sp_dropextendedproperty @name=N'MS_Description' , @level0type=N'SCHEMA',@level0name=N'%s', @level1type=N'TABLE',@level1name=N'%s'"
            + "  end try  "
            + "  begin catch "
            + "  end catch  "
            + "  EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'%s' , @level0type=N'SCHEMA',@level0name=N'%s', @level1type=N'TABLE',@level1name=N'%s' "
            + " commit "
            + "end ";
    protected static final String ALTER_FIELD_SQL_PREFIX = "alter table %s alter column ";

    public MsSqlSqlDriver() {
        super();
        setWrap("[", "]", new String[]{" "});
    }

    @Override
    public String getSql4TableColumns(String aOwnerName, Set<String> aTableNames) {
        String tablesIn = "";
        if (aTableNames != null && !aTableNames.isEmpty()) {
            tablesIn = constructIn(aTableNames);
            return String.format(SQL_COLUMNS, aOwnerName, tablesIn.toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4TablePrimaryKeys(String aOwnerName, Set<String> aTableNames) {
        String tablesIn = "";
        if (aTableNames != null && !aTableNames.isEmpty()) {
            tablesIn = constructIn(aTableNames);
            return String.format(SQL_PRIMARY_KEYS, aOwnerName, tablesIn.toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4TableForeignKeys(String aOwnerName, Set<String> aTableNames) {
        String tablesIn = "";
        if (aTableNames != null && !aTableNames.isEmpty()) {
            tablesIn = constructIn(aTableNames);
            return String.format(SQL_FOREIGN_KEYS, aOwnerName, tablesIn.toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4Indexes(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            String tablesIn = constructIn(aTableNames);
            return String.format(SQL_INDEX_KEYS, aOwnerName, tablesIn.toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4ColumnsComments(String aOwnerName, Set<String> aTableNames) {
        String tablesIn = "";
        if (aTableNames != null && !aTableNames.isEmpty()) {
            tablesIn = constructIn(aTableNames);
            return String.format(SQL_COLUMNS_COMMENTS, aOwnerName, tablesIn.toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4TableComments(String aOwnerName, Set<String> aTableNames) {
        String tablesIn = "";
        if (aTableNames != null && !aTableNames.isEmpty()) {
            tablesIn = constructIn(aTableNames);
            return String.format(SQL_TABLE_COMMENTS, aOwnerName, tablesIn.toUpperCase());
        } else {
            return String.format(SQL_ALL_OWNER_TABLES_COMMENTS, aOwnerName);
        }
    }

    @Override
    public String getColumnNameFromCommentsDs(Rowset rs) throws RowsetException {
        if (!rs.isAfterLast() && !rs.isBeforeFirst()) {
            return (String) rs.getObject(rs.getFields().find(ClientConstants.F_COLUMNS_COMMENTS_FIELD_FIELD_NAME));
        }
        return null;
    }

    @Override
    public String getColumnCommentFromCommentsDs(Rowset rs) throws RowsetException {
        if (!rs.isAfterLast() && !rs.isBeforeFirst()) {
            return (String) rs.getObject(rs.getFields().find(ClientConstants.F_COLUMNS_COMMENTS_COMMENT_FIELD_NAME));
        }
        return null;
    }

    @Override
    public String getTableNameFromCommentsDs(Rowset rs) throws RowsetException {
        if (!rs.isAfterLast() && !rs.isBeforeFirst()) {
            return (String) rs.getObject(rs.getFields().find(ClientConstants.F_TABLE_COMMENTS_NAME_FIELD_NAME));
        }
        return null;
    }

    @Override
    public String getTableCommentFromCommentsDs(Rowset rs) throws RowsetException {
        if (!rs.isAfterLast() && !rs.isBeforeFirst()) {
            return (String) rs.getObject(rs.getFields().find(ClientConstants.F_TABLE_COMMENTS_COMMENT_FIELD_NAME));
        }
        return null;
    }

    @Override
    public String getSql4MtdEntitiesParentsList(String aChildParamName) {
        return "select * from buildMtdEntitiesParents(:" + aChildParamName + ")";
    }

    @Override
    public String getSql4MtdEntitiesChildrenList(String aParentParamName) {
        return "select * from buildMtdEntitiesChildrenList(:" + aParentParamName + ")";
    }

    @Override
    public String getSql4DropTable(String aSchemaName, String aTableName) {
        String fullName = makeFullName(aSchemaName, aTableName);
        return String.format(COMMIT_DDL_CLAUSE, "drop table " + fullName);
    }

    @Override
    public String getSql4EmptyTableCreation(String aSchemaName, String aTableName, String aPkFieldName) {
        String fullName = makeFullName(aSchemaName, aTableName);
        return String.format(COMMIT_DDL_CLAUSE, "CREATE TABLE " + fullName + " ("
                + wrapName(aPkFieldName) + " NUMERIC(18, 0) NOT NULL,"
                + "CONSTRAINT " + wrapName(aTableName + PKEY_NAME_SUFFIX) + " PRIMARY KEY (" + wrapName(aPkFieldName) + " ASC))");
    }

    @Override
    public String parseException(Exception ex) {
        if (ex != null && ex instanceof SQLException) {
            SQLException sqlEx = (SQLException) ex;
            int errorCode = sqlEx.getErrorCode();
            for (int i = 0; i < mssqlErrorCodes.length; i++) {
                if (errorCode == mssqlErrorCodes[i]) {
                    return platypusErrorMessages[i];
                }
            }
        }
        return ex.getLocalizedMessage();
    }

    private String getFieldTypeDefinition(Field aField) {
        resolver.resolve2RDBMS(aField);
        String typeName = aField.getTypeInfo().getSqlTypeName().toLowerCase();
        int sqlType = aField.getTypeInfo().getSqlType();
        // field length
        int size = aField.getSize();
        int scale = aField.getScale();

        if (resolver.isScaled(sqlType) && resolver.isSized(sqlType) && size > 0) {
            typeName += "(" + String.valueOf(size) + "," + String.valueOf(scale) + ")";
        } else {
            if (resolver.isSized(sqlType) && size > 0) {
                typeName += "(" + String.valueOf(size) + ")";
            }
        }
        return typeName;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4FieldDefinition(Field aField) {
        String fieldName = wrapName(aField.getName());
        String fieldDefinition = fieldName + " " + getFieldTypeDefinition(aField);

        if (!aField.isNullable()) {
            fieldDefinition += " not null";
        } else {
            fieldDefinition += " null";
        }
        return fieldDefinition;
    }

    @Override
    public String getSql4DropFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        String tableName = makeFullName(aSchemaName, aFk.getTable());
        String sqlText = "ALTER TABLE " + tableName + " DROP CONSTRAINT " + wrapName(aFk.getCName());
        return String.format(COMMIT_DDL_CLAUSE, sqlText);
    }

    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        List<ForeignKeySpec> fkList = new ArrayList();
        fkList.add(aFk);
        return getSql4CreateFkConstraint(aSchemaName, fkList);
    }

    @Override
    public String getApplicationSchemaInitResourceName() {
        return "/" + MsSqlSqlDriver.class.getPackage().getName().replace(".", "/") + "/sqlscripts/MsSqlInitSchema.sql";
    }

    @Override
    public Set<Integer> getSupportedJdbcDataTypes() {
        return resolver.getSupportedJdbcDataTypes();
    }

    @Override
    public void applyContextToConnection(Connection aConnection, String aSchema) throws Exception {
        //no-op
    }

    @Override
    protected String getSql4GetConnectionContext() {
        return GET_SCHEMA_CLAUSE;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String[] getSqls4ModifyingField(String aSchemaName, String aTableName, Field aOldFieldMd, Field aNewFieldMd) {
        String fullTableName = makeFullName(aSchemaName, aTableName);
        String alterFieldSql = String.format(ALTER_FIELD_SQL_PREFIX, fullTableName);
        return new String[]{String.format(COMMIT_DDL_CLAUSE, alterFieldSql + getSql4FieldDefinition(aNewFieldMd))};
    }

    @Override
    public String[] getSqls4RenamingField(String aSchemaName, String aTableName, String aOldFieldName, Field aNewFieldMd) {
        String fullTableName = makeFullName(aSchemaName, aTableName);
        String sql = String.format("EXEC sp_rename '%s.%s','%s','COLUMN'", fullTableName, aOldFieldName, aNewFieldMd.getName());
        return new String[]{String.format(COMMIT_DDL_CLAUSE, sql)};
    }

    @Override
    public String[] getSql4CreateColumnComment(String aOwnerName, String aTableName, String aFieldName, String aDescription) {
        if (aDescription == null) {
            aDescription = "";
        }
        return new String[]{String.format(ADD_COLUMN_COMMENT_CLAUSE, wrapName(aOwnerName), wrapName(aTableName), wrapName(aFieldName), aDescription, wrapName(aOwnerName), wrapName(aTableName), wrapName(aFieldName))};
    }

    @Override
    public String getSql4CreateTableComment(String aOwnerName, String aTableName, String aDescription) {
        if (aDescription == null) {
            aDescription = "";
        }
        return String.format(ADD_TABLE_COMMENT_CLAUSE, aOwnerName, aTableName, aDescription, aOwnerName, aTableName);
    }

    @Override
    public Integer getJdbcTypeByRDBMSTypename(String aLowLevelTypeName) {
        return resolver.getJdbcTypeByRDBMSTypename(aLowLevelTypeName);
    }

    @Override
    public Converter getConverter() {
        return converter;
    }

    @Override
    public TypesResolver getTypesResolver() {
        return resolver;
    }

    @Override
    public String getSql4DropIndex(String aSchemaName, String aTableName, String aIndexName) {
        aTableName = makeFullName(aSchemaName, aTableName);
        return String.format(COMMIT_DDL_CLAUSE, "drop index " + wrapName(aIndexName) + " on " + aTableName);
    }

    @Override
    public String getSql4CreateIndex(String aSchemaName, String aTableName, DbTableIndexSpec aIndex) {
        assert aIndex.getColumns().size() > 0 : "index definition must consist of at least 1 column";
        String indexName = wrapName(aIndex.getName());
        String tableName = makeFullName(aSchemaName, aTableName);
        String modifier = "";
        /*
         * if(aIndex.isClustered()) modifier = "clustered"; else
         */
        if (aIndex.isUnique()) {
            modifier = "unique";
        }
        modifier += " nonclustered";

        String fieldsList = "";
        for (int i = 0; i < aIndex.getColumns().size(); i++) {
            DbTableIndexColumnSpec column = aIndex.getColumns().get(i);
            fieldsList += wrapName(column.getColumnName()) + " asc";
            if (i != aIndex.getColumns().size() - 1) {
                fieldsList += ", ";
            }
        }
        return String.format(COMMIT_DDL_CLAUSE, "create " + modifier + " index " + indexName + " on " + tableName + "( " + fieldsList + " )");
    }

    @Override
    public String getSql4TablesEnumeration(String schema4Sql) {
        if (schema4Sql == null) {
            return SQL_ALL_TABLES
                    + " order by " + ClientConstants.JDBCCOLS_TABLE_SCHEM + "," + ClientConstants.JDBCCOLS_TABLE_NAME;
        } else {
            return String.format(SQL_SCHEMA_TABLES, schema4Sql);
        }
    }

    @Override
    public String getSql4SchemasEnumeration() {
        return SQL_SCHEMAS;
    }

    @Override
    public String getSql4CreateSchema(String aSchemaName, String aPassword) {
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            return String.format(COMMIT_DDL_CLAUSE, String.format(CREATE_SCHEMA_CLAUSE, aSchemaName));
        }
        throw new IllegalArgumentException("Schema name is null or empty.");
    }

    @Override
    public String getSql4DropPkConstraint(String aSchemaName, PrimaryKeySpec aPk) {
        String constraintName = wrapName(aPk.getCName());
        String tableName = makeFullName(aSchemaName, aPk.getTable());
        return String.format(COMMIT_DDL_CLAUSE, "alter table " + tableName + " drop constraint " + constraintName);
    }

    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, List<ForeignKeySpec> listFk) {
        if (listFk != null && listFk.size() > 0) {
            ForeignKeySpec fk = listFk.get(0);
            String fkTableName = makeFullName(aSchemaName, fk.getTable());
            String fkName = fk.getCName();
            String fkColumnName = wrapName(fk.getField());

            PrimaryKeySpec pk = fk.getReferee();
            String pkSchemaName = pk.getSchema();
            String pkTableName = makeFullName(aSchemaName, pk.getTable());
            String pkColumnName = wrapName(pk.getField());

            for (int i = 1; i < listFk.size(); i++) {
                fk = listFk.get(i);
                pk = fk.getReferee();
                fkColumnName += ", " + wrapName(fk.getField());
                pkColumnName += ", " + wrapName(pk.getField());
            }

            String fkRule = "";
            switch (fk.getFkDeleteRule()) {
                case CASCADE:
                    fkRule += " ON DELETE CASCADE ";
                    break;
                case NOACTION:
                    fkRule += " ON DELETE NO ACTION ";
                    break;
                case SETDEFAULT:
                    fkRule += " ON DELETE SET DEFAULT ";
                    break;
                case SETNULL:
                    fkRule += " ON DELETE set null ";
                    break;
            }
            switch (fk.getFkUpdateRule()) {
                case CASCADE:
                    fkRule += " ON UPDATE CASCADE ";
                    break;
                case NOACTION:
                    fkRule += " ON UPDATE NO ACTION ";
                    break;
                case SETDEFAULT:
                    fkRule += " ON UPDATE SET DEFAULT ";
                    break;
                case SETNULL:
                    fkRule += " ON UPDATE set null ";
                    break;
            }
            return String.format(COMMIT_DDL_CLAUSE, String.format("ALTER TABLE %s ADD CONSTRAINT %s"
                    + " FOREIGN KEY (%s) REFERENCES %s (%s) %s", fkTableName, fkName.isEmpty() ? "" : wrapName(fkName), fkColumnName, pkTableName, pkColumnName, fkRule));
        }
        return null;
    }

    @Override
    public String[] getSql4CreatePkConstraint(String aSchemaName, List<PrimaryKeySpec> listPk) {

        if (listPk != null && listPk.size() > 0) {
            PrimaryKeySpec pk = listPk.get(0);
            String tableName = pk.getTable();
            String pkTableName = makeFullName(aSchemaName, tableName);
            String pkName = wrapName(tableName + PKEY_NAME_SUFFIX);
            String pkColumnName = wrapName(pk.getField());
            for (int i = 1; i < listPk.size(); i++) {
                pk = listPk.get(i);
                pkColumnName += ", " + wrapName(pk.getField());
            }
            return new String[]{
                String.format(COMMIT_DDL_CLAUSE, String.format("ALTER TABLE %s ADD CONSTRAINT %s PRIMARY KEY (%s)", pkTableName, pkName, pkColumnName))
            };
        }
        return null;
    }

    @Override
    public boolean isConstraintsDeferrable() {
        return false;
    }

    @Override
    public String[] getSqls4AddingField(String aSchemaName, String aTableName, Field aField) {
        String fullTableName = makeFullName(aSchemaName, aTableName);
        return new String[]{
            String.format(COMMIT_DDL_CLAUSE, String.format(SqlDriver.ADD_FIELD_SQL_PREFIX, fullTableName) + getSql4FieldDefinition(aField))
        };
    }
}
