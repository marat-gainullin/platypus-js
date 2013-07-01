/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.sqldrivers;

import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.bearsoft.rowset.metadata.PrimaryKeySpec;
import com.eas.client.ClientConstants;
import com.eas.client.SQLUtils;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.sqldrivers.converters.OracleConverter;
import com.eas.client.sqldrivers.resolvers.OracleTypesResolver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import com.eas.util.StringUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Marat
 */
public class OracleSqlDriver extends SqlDriver {

    protected static final Converter converter = new OracleConverter();
    protected static final OracleTypesResolver resolver = new OracleTypesResolver();
    protected static final String SET_SCHEMA_CLAUSE = "alter session set current_schema = %s";
    protected static final String GET_SCHEMA_CLAUSE = "SELECT sys_context('USERENV', 'CURRENT_SCHEMA') FROM DUAL";
    protected static final String CREATE_SCHEMA_CLAUSE = "CREATE USER %s IDENTIFIED BY %s";
    protected static final String RENAME_FIELD_SQL_PREFIX = "alter table %s rename column %s to %s";
    protected static final String MODIFY_FIELD_SQL_PREFIX = "alter table %s modify ";
    protected static final String DEFAULT_OBJECT_TYPE_NAME = "SYS.ANYDATA";
    protected static final int[] oraErrorCodes = {
        955, 942
    };
    protected static final String[] platypusErrorMessages = {
        EAS_TABLE_ALREADY_EXISTS,
        EAS_TABLE_DOESNT_EXISTS
    };
    public static final String SQL_ALL_TABLES = ""
            + "select t.TABLE_NAME as " + ClientConstants.JDBCCOLS_TABLE_NAME + ", t.OWNER as " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ", '" + ClientConstants.JDBCPKS_TABLE_TYPE_TABLE + "' as " + ClientConstants.JDBCPKS_TABLE_TYPE_FIELD_NAME + " from sys.ALL_TABLES t ";
    public static final String SQL_ALL_VIEWS = ""
            + "select v.VIEW_NAME as " + ClientConstants.JDBCCOLS_TABLE_NAME + ", v.OWNER as " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ", '" + ClientConstants.JDBCPKS_TABLE_TYPE_VIEW + "' as " + ClientConstants.JDBCPKS_TABLE_TYPE_FIELD_NAME + " from sys.ALL_VIEWS v ";
    public static final String SQL_ALL_TABLES_VIEWS = ""
            + SQL_ALL_TABLES
            + " union "
            + SQL_ALL_VIEWS
            + " order by " + ClientConstants.JDBCCOLS_TABLE_SCHEM + "," + ClientConstants.JDBCCOLS_TABLE_NAME;
    public static final String SQL_SCHEMA_TABLES = ""
            + SQL_ALL_TABLES
            + "where Upper(t.OWNER) = Upper('%s') ";
    public static final String SQL_SCHEMA_VIEWS = ""
            + SQL_ALL_VIEWS
            + "where Upper(v.OWNER) = Upper('%s')";
    public static final String SQL_SCHEMA_TABLES_VIEWS = ""
            + SQL_SCHEMA_TABLES
            + " union "
            + SQL_SCHEMA_VIEWS
            + " order by " + ClientConstants.JDBCCOLS_TABLE_SCHEM + "," + ClientConstants.JDBCCOLS_TABLE_NAME;
    public static final String SQL_SCHEMAS = ""
            + "select u.USERNAME as " + ClientConstants.JDBCCOLS_TABLE_SCHEM + " from sys.ALL_USERS u "
            + "order by " + ClientConstants.JDBCCOLS_TABLE_SCHEM;
    protected static final String SQL_COLUMNS = ""
            + "SELECT NULL table_cat, t.owner " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ", t.table_name " + ClientConstants.JDBCCOLS_TABLE_NAME + ", "
            + "t.column_name column_name, decode(t.data_type, 'CHAR', 1, 'VARCHAR2', 12, "
            + "'NUMBER', 3, 'LONG', -1, 'DATE', 91, 'RAW', -3, 'LONG RAW', -4, 'BLOB', "
            + "2004, 'CLOB', 2005, 'BFILE', -13, 'FLOAT', 6, 'TIMESTAMP(6)', 93, "
            + "'TIMESTAMP(6) WITH TIME ZONE', -101, 'TIMESTAMP(6) WITH LOCAL TIME ZONE', "
            + "-102, 'INTERVAL YEAR(2) TO MONTH', -103, 'INTERVAL DAY(2) TO SECOND(6)', "
            + "-104, 'BINARY_FLOAT', 100, 'BINARY_DOUBLE', 101, 1111) AS data_type, "
            + "t.data_type type_name, decode(t.data_precision, null, t.data_length, "
            + "t.data_precision) AS column_size, 0 AS buffer_length, "
            + "t.data_scale decimal_digits, 10 AS num_prec_radix, decode(t.nullable, "
            + "'N', 0, 1) AS nullable, NULL remarks, t.data_default column_def, 0 AS "
            + "sql_data_type, 0 AS sql_datetime_sub, t.data_length char_octet_length, "
            + "t.column_id ordinal_position, decode(t.nullable, 'N', 'NO', 'YES') AS "
            + "is_nullable "
            + "FROM all_tab_columns t "
            + "WHERE Upper(t.owner) = Upper('%s') "
            + "AND Upper(t.table_name) in (%s) "
            + "ORDER BY table_schem, table_name, ordinal_position";
    protected static final String SQL_PRIMARY_KEYS = ""
            + "SELECT NULL table_cat, c.owner " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ", c.table_name " + ClientConstants.JDBCCOLS_TABLE_NAME + ", c.column_name " + ClientConstants.JDBCCOLS_COLUMN_NAME + ", "
            + "c.position key_seq, c.constraint_name pk_name "
            + "FROM all_cons_columns c, all_constraints k "
            + "WHERE k.constraint_type = 'P' "
            + "AND k.constraint_name = c.constraint_name "
            + "AND k.table_name = c.table_name "
            + "AND k.owner = c.owner "
            + "AND Upper(k.owner) = Upper('%s') "
            + "AND Upper(k.table_name) in (%s) "
            + "ORDER BY c.owner,c.table_name,c.position";
    protected static final String SQL_FOREIGN_KEYS = ""
            + "SELECT NULL pktable_cat, p.owner pktable_schem, p.table_name pktable_name, "
            + "pc.column_name pkcolumn_name, NULL fktable_cat, f.owner fktable_schem,  "
            + "f.table_name fktable_name, fc.column_name fkcolumn_name,  "
            + "fc.position key_seq, null as update_rule, decode(f.delete_rule, 'CASCADE',  "
            + "0, 'SET NULL', 2, 1) as delete_rule, f.constraint_name fk_name,  "
            + "p.constraint_name pk_name, decode(f.deferrable, 'DEFERRABLE', 5,  "
            + "'NOT DEFERRABLE', 7, 'DEFERRED', 6) deferrability "
            + "FROM all_cons_columns pc, all_constraints p, all_cons_columns fc,  "
            + "all_constraints f "
            + "WHERE 1 = 1 "
            + "AND f.constraint_type = 'R' "
            + "AND p.owner = f.r_owner "
            + "AND p.constraint_name = f.r_constraint_name "
            + "AND p.constraint_type = 'P' "
            + "AND pc.owner = p.owner "
            + "AND pc.constraint_name = p.constraint_name "
            + "AND pc.table_name = p.table_name "
            + "AND fc.owner = f.owner "
            + "AND fc.constraint_name = f.constraint_name "
            + "AND fc.table_name = f.table_name "
            + "AND fc.position = pc.position "
            + "AND Upper(p.owner) = Upper('%s') "
            + "AND Upper(f.table_name) in (%s) "
            + "ORDER BY pktable_schem, pktable_name, key_seq ";
    protected static final String SQL_INDEX_KEYS = ""
            + "SELECT null table_cat, i.owner " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ", i.table_name " + ClientConstants.JDBCCOLS_TABLE_NAME + ", decode(i.uniqueness, "
            + "'UNIQUE', 0, 1) AS non_unique, null index_qualifier, i.index_name, 1 AS type, "
            + "c.column_position ordinal_position, c.column_name, null asc_or_desc, "
            + "i.distinct_keys cardinality, i.leaf_blocks pages, null filter_condition "
            + "FROM all_indexes i, all_ind_columns c "
            + "WHERE Upper(i.owner) = Upper('%s') "
            + "and Upper(i.table_name) in (%s) "
            + "AND i.index_name = c.index_name "
            + "AND i.table_owner = c.table_owner "
            + "AND i.table_name = c.table_name "
            + "AND i.owner = c.index_owner "
            + "ORDER BY non_unique, type, index_name, ordinal_position ";
    protected static final String SQL_COLUMNS_COMMENTS = ""
            + "select * from sys.all_col_comments all_col_comments where "
            + "Upper(owner) = Upper('%s') "
            + "and Upper(table_name) in (%s)";
    protected static final String SQL_TABLE_COMMENTS = ""
            + "select * from sys.all_tab_comments all_tab_comments where "
            + "Upper(owner) = Upper('%s') "
            + "and Upper(table_name) in (%s)";

    @Override
    public String getSql4TableColumns(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            String tablesIn = constructIn(aTableNames);
            return String.format(SQL_COLUMNS, aOwnerName.toUpperCase(), tablesIn.toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4Indexes(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            String tablesIn = constructIn(aTableNames);
            return String.format(SQL_INDEX_KEYS, aOwnerName.toUpperCase(), tablesIn.toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4TablePrimaryKeys(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            String tablesIn = constructIn(aTableNames);
            return String.format(SQL_PRIMARY_KEYS, aOwnerName.toUpperCase(), tablesIn.toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4TableForeignKeys(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            return String.format(SQL_FOREIGN_KEYS, aOwnerName.toUpperCase(), constructIn(aTableNames).toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4ColumnsComments(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            return String.format(SQL_COLUMNS_COMMENTS, aOwnerName.toUpperCase(), constructIn(aTableNames).toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4TableComments(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            return String.format(SQL_TABLE_COMMENTS, aOwnerName.toUpperCase(), constructIn(aTableNames).toUpperCase());
        } else if (aOwnerName != null && !aOwnerName.isEmpty()) {
            return String.format("select * from sys.all_tab_comments all_tab_comments where Upper(owner) = Upper('%s')", aOwnerName.toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4MtdEntitiesParentsList(String aChildParamName) {
        if (aChildParamName != null) {
            return "select mtd." + ClientConstants.F_MDENT_ID + ", mtd." + ClientConstants.F_MDENT_PARENT_ID + " from " + ClientConstants.T_MTD_ENTITIES + " mtd start with mtd." + ClientConstants.F_MDENT_ID + " = :" + aChildParamName + " connect by prior mtd." + ClientConstants.F_MDENT_PARENT_ID + "=mtd." + ClientConstants.F_MDENT_ID;
        } else {
            return null;
        }
    }

    @Override
    public String getSql4MtdEntitiesChildrenList(String aParentParamName) {
        if (aParentParamName == null || aParentParamName.isEmpty()) {
            return null;
        } else {
            return "SELECT T_MTD_ENTITIES_1.MDENT_ID, T_MTD_ENTITIES_1.MDENT_NAME, T_MTD_ENTITIES_1.MDENT_PARENT_ID, T_MTD_ENTITIES_1.MDENT_TYPE, T_MTD_ENTITIES_1.MDENT_CONTENT_TXT, T_MTD_ENTITIES_1.MDENT_CONTENT_TXT_SIZE, T_MTD_ENTITIES_1.MDENT_CONTENT_TXT_CRC32 FROM MTD_ENTITIES T_MTD_ENTITIES_1 START WITH (T_MTD_ENTITIES_1.MDENT_ID=:" + aParentParamName + ") CONNECT BY (PRIOR T_MTD_ENTITIES_1.MDENT_ID=T_MTD_ENTITIES_1.MDENT_PARENT_ID)";
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
    public String getSql4DropTable(String aSchemaName, String aTableName) {
        String dropClause = "drop table ";
        aTableName = wrapName(aTableName);
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            return dropClause + wrapName(aSchemaName) + "." + aTableName;
        } else {
            return dropClause + aTableName;
        }
    }

    @Override
    public String getSql4EmptyTableCreation(String aSchemaName, String aTableName, String aPkFieldName) {
        String fullName = wrapName(aTableName);
        aPkFieldName = wrapName(aPkFieldName);
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            fullName = wrapName(aSchemaName) + "." + fullName;
        }
        return "CREATE TABLE " + fullName + " ("
                + aPkFieldName + " NUMBER NOT NULL,"
                + "CONSTRAINT " + wrapName(aTableName + "_PK") + " PRIMARY KEY (" + aPkFieldName + "))";
    }

    @Override
    public String parseException(Exception ex) {
        if (ex != null && ex instanceof SQLException) {
            SQLException sqlEx = (SQLException) ex;
            int errorCode = sqlEx.getErrorCode();
            for (int i = 0; i < oraErrorCodes.length; i++) {
                if (errorCode == oraErrorCodes[i]) {
                    return platypusErrorMessages[i];
                }
            }
        }
        return ex.getLocalizedMessage();
    }

    private String getFieldTypeDefinition(Field aField) {
        resolver.resolve2RDBMS(aField);
        String typeName = aField.getTypeInfo().getSqlTypeName().toUpperCase();
        if (aField.getTypeInfo().getSqlType() == java.sql.Types.STRUCT) {
            if (typeName == null || typeName.isEmpty() || typeName.equalsIgnoreCase("STRUCT")) {
                typeName = DEFAULT_OBJECT_TYPE_NAME;
            }
        }
        // field length
        int size = aField.getSize();
        if (SQLUtils.isSameTypeGroup(aField.getTypeInfo().getSqlType(), java.sql.Types.VARCHAR)
                || aField.getTypeInfo().getSqlType() == java.sql.Types.VARBINARY) {
            if (size > 0 && aField.getTypeInfo().getSqlType() != java.sql.Types.LONGVARCHAR) {
                if (aField.getTypeInfo().getSqlType() == Types.NCHAR || aField.getTypeInfo().getSqlType() == Types.NVARCHAR) {
                    size = size / 2;
                }
                typeName += "(" + String.valueOf(size) + ")";
            }
        } else if (SQLUtils.isSameTypeGroup(aField.getTypeInfo().getSqlType(), java.sql.Types.NUMERIC)) { // numeric types
            typeName = " NUMBER ";
            if (size > 0) {
                if (size > 38) {
                    typeName = " FLOAT (" + String.valueOf(size) + ")";
                } else {
                    typeName += "(" + String.valueOf(size) + ", " + String.valueOf(aField.getScale()) + ")";
                }
            }
        } else if (SQLUtils.isSameTypeGroup(aField.getTypeInfo().getSqlType(), java.sql.Types.BOOLEAN)) {
            typeName = " NUMBER ";
        } else if (SQLUtils.isSameTypeGroup(aField.getTypeInfo().getSqlType(), java.sql.Types.TIME)) {
            if (aField.getTypeInfo().getSqlType() != java.sql.Types.TIMESTAMP) {
                typeName = " DATE ";
            }
        } else if (SQLUtils.isSameTypeGroup(aField.getTypeInfo().getSqlType(), java.sql.Types.BLOB)) {
            if (aField.getTypeInfo().getSqlType() != java.sql.Types.CLOB
                    && aField.getTypeInfo().getSqlType() != java.sql.Types.NCLOB
                    && aField.getTypeInfo().getSqlType() != java.sql.Types.LONGVARBINARY
                    && aField.getTypeInfo().getSqlType() != java.sql.Types.VARBINARY) {
                typeName = " BLOB ";
            }
        }
        return typeName;

    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4FieldDefinition(Field aField) {

        String fieldDefinition = wrapName(aField.getName()) + " " + getFieldTypeDefinition(aField);

        if (!aField.isNullable()) {
            fieldDefinition += " not null";
        } else {
            fieldDefinition += " null";
        }
        if (aField.isPk()) {
            fieldDefinition += " CONSTRAINT " + wrapName(aField.getTableName() + "_PK") + " PRIMARY KEY ";
        }
        return fieldDefinition;
    }

    @Override
    public String getSql4DropFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        String constraintName = wrapName(aFk.getCName());
        String leftTableFullName = wrapName(aFk.getTable());
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            leftTableFullName = wrapName(aSchemaName) + "." + leftTableFullName;
        }
        return "alter table " + leftTableFullName + " drop constraint " + constraintName;
    }

    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        List<ForeignKeySpec> fkList = new ArrayList();
        fkList.add(aFk);
        return getSql4CreateFkConstraint(aSchemaName, fkList);
    }

    @Override
    public String getApplicationSchemaInitResourceName() {
        return "/" + OracleSqlDriver.class.getPackage().getName().replace(".", "/") + "/sqlscripts/OracleInitSchema.sql";
    }

    @Override
    public Set<Integer> getSupportedJdbcDataTypes() {
        return resolver.getSupportedJdbcDataTypes();
    }

    @Override
    public void applyContextToConnection(Connection aConnection, String aSchema) throws Exception {
        if (aSchema != null && !aSchema.isEmpty()) {
            try (Statement stmt = aConnection.createStatement()) {
                stmt.execute(String.format(SET_SCHEMA_CLAUSE, wrapName(aSchema)));
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
    public String[] getSqls4ModifyingField(String aTableName, Field aOldFieldMd, Field aNewFieldMd) {
        List<String> sql = new ArrayList();
        assert aOldFieldMd.getName().toLowerCase().equals(aNewFieldMd.getName().toLowerCase());
        String sqlModifyingClause = wrapName(aOldFieldMd.getName()) + " ";

        aTableName = wrapName(aTableName);
        String lOldTypeName = aOldFieldMd.getTypeInfo().getSqlTypeName();
        if (lOldTypeName == null) {
            lOldTypeName = "";
        }
        String lNewTypeName = aNewFieldMd.getTypeInfo().getSqlTypeName();
        if (lNewTypeName == null) {
            lNewTypeName = "";
        }
        if (aOldFieldMd.getTypeInfo().getSqlType() != aNewFieldMd.getTypeInfo().getSqlType()
                || !lOldTypeName.equalsIgnoreCase(lNewTypeName)
                || aOldFieldMd.getSize() != aNewFieldMd.getSize()
                || aOldFieldMd.getPrecision() != aNewFieldMd.getPrecision()
                || aOldFieldMd.getScale() != aNewFieldMd.getScale()) {
            if ((SQLUtils.isSameTypeGroup(aNewFieldMd.getTypeInfo().getSqlType(), java.sql.Types.BLOB)
                    || SQLUtils.isSameTypeGroup(aOldFieldMd.getTypeInfo().getSqlType(), java.sql.Types.BLOB)
                    || SQLUtils.isSameTypeGroup(aNewFieldMd.getTypeInfo().getSqlType(), java.sql.Types.STRUCT)
                    || SQLUtils.isSameTypeGroup(aOldFieldMd.getTypeInfo().getSqlType(), java.sql.Types.STRUCT))
                    && (aNewFieldMd.getTypeInfo().getSqlType() != aOldFieldMd.getTypeInfo().getSqlType() || !lOldTypeName.equalsIgnoreCase(lNewTypeName))) {
                String dropFieldClause[] = getSql4DroppingField(aTableName, wrapName(aOldFieldMd.getName()));
                sql.addAll(Arrays.asList(dropFieldClause));
                String addFieldClause = String.format(ADD_FIELD_SQL_PREFIX, aTableName) + getSql4FieldDefinition(aNewFieldMd);
                sql.add(addFieldClause);
                return (String[]) sql.toArray(new String[sql.size()]);
            } else {
                // type name
                sqlModifyingClause += getFieldTypeDefinition(aNewFieldMd);

            }
        }
        if (aOldFieldMd.isNullable() != aNewFieldMd.isNullable()) {
            if (!aNewFieldMd.isNullable()) {
                sqlModifyingClause += " not null";
            } else {
                sqlModifyingClause += " null";
            }
        }
        if (aNewFieldMd.isPk() && !aOldFieldMd.isPk()) {
            sqlModifyingClause += " CONSTRAINT " + wrapName(aNewFieldMd.getTableName() + "_PK") + " PRIMARY KEY ";
        }

        String sqlText = String.format(MODIFY_FIELD_SQL_PREFIX, aTableName) + sqlModifyingClause;
        return new String[]{
                    sqlText
                };
    }

    @Override
    public String[] getSqls4RenamingField(String aTableName, String aOldFieldName, Field aNewFieldMd) {
        String sqlText = String.format(RENAME_FIELD_SQL_PREFIX, wrapName(aTableName), wrapName(aOldFieldName), wrapName(aNewFieldMd.getName()));
        return new String[]{
                    sqlText
                };
    }

    @Override
    public String[] getSql4CreateColumnComment(String aOwnerName, String aTableName, String aFieldName, String aDescription) {
        aOwnerName = wrapName(aOwnerName);
        aTableName = wrapName(aTableName);
        aFieldName = wrapName(aFieldName);
        String sqlText = aOwnerName == null ? StringUtils.join(".", aTableName, aFieldName) : StringUtils.join(".", aOwnerName, aTableName, aFieldName);
        if (aDescription == null) {
            aDescription = "";
        }
        return new String[]{"comment on column " + sqlText + " is '" + aDescription.replaceAll("'", "''") + "'"};
    }

    @Override
    public String getSql4CreateTableComment(String aOwnerName, String aTableName, String aDescription) {
        String sqlText = StringUtils.join(".", wrapName(aOwnerName), wrapName(aTableName));
        if (aDescription == null) {
            aDescription = "";
        }
        return "comment on table " + sqlText + " is '" + aDescription.replaceAll("'", "''") + "'";
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
        aIndexName = wrapName(aIndexName);
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            aIndexName = wrapName(aSchemaName) + "." + aIndexName;
        }
        return "drop index " + aIndexName;
    }

    @Override
    public String getSql4CreateIndex(String aSchemaName, String aTableName, DbTableIndexSpec aIndex) {
        assert aIndex.getColumns().size() > 0 : "index definition must consist of at least 1 column";
        String indexName = wrapName(aIndex.getName());
        aSchemaName = wrapName(aSchemaName);
        aTableName = wrapName(aTableName);
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            indexName = aSchemaName + "." + indexName;
        }
        String tableName = aTableName;
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            tableName = aSchemaName + "." + tableName;
        }
        String modifier = "";
        /*
         * if(aIndex.isClustered()) modifier = "clustered"; else
         */
        if (aIndex.isUnique()) {
            modifier = "unique";
        } else if (aIndex.isHashed()) {
            modifier = "bitmap";
        }
        String fieldsList = "";
        for (int i = 0; i < aIndex.getColumns().size(); i++) {
            DbTableIndexColumnSpec column = aIndex.getColumns().get(i);
            fieldsList += wrapName(column.getColumnName());
            if (i != aIndex.getColumns().size() - 1) {
                fieldsList += ", ";
            }
        }
        return "create " + modifier + " index " + indexName + " on " + tableName + "( " + fieldsList + " )";
    }

    @Override
    public String getSql4TablesEnumeration(String schema4Sql) {
        if (schema4Sql == null) {
            return SQL_ALL_TABLES_VIEWS;
        } else {
            schema4Sql = schema4Sql.toUpperCase();
            return String.format(SQL_SCHEMA_TABLES_VIEWS, schema4Sql, schema4Sql);
        }
    }

    @Override
    public String getSql4SchemasEnumeration() {
        return SQL_SCHEMAS;
    }

    @Override
    public String getSql4CreateSchema(String aSchemaName, String aPassword) {
        if (aSchemaName == null || aSchemaName.isEmpty()) {
            throw new IllegalArgumentException("Schema name is null or empty.");       
        }
        if (aPassword == null || aPassword.isEmpty()) {
            throw new IllegalArgumentException("Schema owner password is null or empty.");       
        }
        return String.format(CREATE_SCHEMA_CLAUSE, aSchemaName);
    }

    @Override
    public String getSql4DropPkConstraint(String aSchemaName, PrimaryKeySpec aPk) {
//        String constraintName = wrapName(aPk.getCName());
        String leftTableFullName = wrapName(aPk.getTable());
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            leftTableFullName = wrapName(aSchemaName) + "." + leftTableFullName;
        }
        return "alter table " + leftTableFullName + " drop primary key";
        //return "alter table " + leftTableFullName + " drop constraint " + constraintName;
    }

    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, List<ForeignKeySpec> listFk) {
        if (listFk != null && listFk.size() > 0) {
            ForeignKeySpec fk = listFk.get(0);
            String fkTableName = wrapName(fk.getTable());
            String fkName = fk.getCName();
            String fkColumnName = wrapName(fk.getField());

            PrimaryKeySpec pk = fk.getReferee();
            String pkSchemaName = pk.getSchema();
            String pkTableName = wrapName(pk.getTable());
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
                    break;
                case SETDEFAULT:
                    break;
                case SETNULL:
                    fkRule += " ON DELETE set null ";
                    break;
            }
            if (fk.getFkDeferrable()) {
                fkRule += " DEFERRABLE INITIALLY DEFERRED";
            }
            if (aSchemaName != null && !aSchemaName.isEmpty()) {
                fkTableName = wrapName(aSchemaName) + "." + fkTableName;
            }
            if (pkSchemaName != null && !pkSchemaName.isEmpty()) {
                pkTableName = wrapName(pkSchemaName) + "." + pkTableName;
            }

            return String.format("ALTER TABLE %s ADD (CONSTRAINT %s"
                    + " FOREIGN KEY (%s) REFERENCES %s (%s) %s)", fkTableName, fkName.isEmpty() ? "" : wrapName(fkName), fkColumnName, pkTableName, pkColumnName, fkRule);


        }
        return null;
    }

    @Override
    public String getSql4CreatePkConstraint(String aSchemaName, List<PrimaryKeySpec> listPk) {
        if (listPk != null && listPk.size() > 0) {
            PrimaryKeySpec pk = listPk.get(0);
            String pkTableName = wrapName(pk.getTable());
            String pkName = pk.getCName();
            String pkColumnName = wrapName(pk.getField());
            for (int i = 1; i < listPk.size(); i++) {
                pk = listPk.get(i);
                pkColumnName += ", " + wrapName(pk.getField());
            }
            if (aSchemaName != null && !aSchemaName.isEmpty()) {
                pkTableName = wrapName(aSchemaName) + "." + pkTableName;
            }
            return String.format("ALTER TABLE %s ADD %s PRIMARY KEY (%s)", pkTableName, (pkName.isEmpty() ? "" : "CONSTRAINT "+wrapName(pkName)), pkColumnName);
        }
        return null;
    }

    @Override
    public boolean isConstraintsDeferrable() {
        return true;
    }
}
