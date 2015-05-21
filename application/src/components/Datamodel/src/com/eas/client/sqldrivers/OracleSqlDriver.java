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

import com.eas.client.ClientConstants;
import com.eas.client.SQLUtils;
import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.sqldrivers.resolvers.OracleTypesResolver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import com.eas.util.StringUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Marat
 */
public class OracleSqlDriver extends SqlDriver {

    // настройка экранирования наименования объектов БД
    private final TwinString[] charsForWrap = {new TwinString("\"", "\"")};
    private final char[] restrictedChars = {' ', ',', '\'', '"'};

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
    public static final String SQL_ALL_TABLES_VIEWS = ""
            + "select "
            + " OWNER as " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + " TABLE_NAME as " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + " TABLE_TYPE as " + ClientConstants.JDBCPKS_TABLE_TYPE_FIELD_NAME + ","
            + " COMMENTS as " + ClientConstants.JDBCCOLS_REMARKS + " "
            + "from all_tab_comments "
            + "order by " + ClientConstants.JDBCCOLS_TABLE_SCHEM + "," + ClientConstants.JDBCCOLS_TABLE_NAME;
    public static final String SQL_SCHEMA_TABLES_VIEWS = ""
            + "select "
            + " OWNER as " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + " TABLE_NAME as " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + " TABLE_TYPE as " + ClientConstants.JDBCPKS_TABLE_TYPE_FIELD_NAME + ","
            + " COMMENTS as " + ClientConstants.JDBCCOLS_REMARKS + " "
            + "from all_tab_comments "
            + "where OWNER = '%s' "
            + "order by " + ClientConstants.JDBCCOLS_TABLE_SCHEM + "," + ClientConstants.JDBCCOLS_TABLE_NAME;
    public static final String SQL_SCHEMAS = ""
            + "select u.USERNAME as " + ClientConstants.JDBCCOLS_TABLE_SCHEM + " from sys.ALL_USERS u "
            + "order by " + ClientConstants.JDBCCOLS_TABLE_SCHEM;
    protected static final String SQL_COLUMNS = ""
            + "SELECT"
            + " NULL table_cat,"
            + " t.owner " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + " t.table_name " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + " t.column_name column_name,"
            + " decode(t.data_type, 'CHAR', 1, 'VARCHAR2', 12, 'NUMBER', 3, 'LONG', -1, 'DATE', 91, 'RAW', -3, 'LONG RAW', -4, 'BLOB', "
            + "        2004, 'CLOB', 2005, 'BFILE', -13, 'FLOAT', 6, 'TIMESTAMP(6)', 93, 'TIMESTAMP(6) WITH TIME ZONE',"
            + "       -101, 'TIMESTAMP(6) WITH LOCAL TIME ZONE', -102, 'INTERVAL YEAR(2) TO MONTH', -103, 'INTERVAL DAY(2) TO SECOND(6)', "
            + "       -104, 'BINARY_FLOAT', 100, 'BINARY_DOUBLE', 101, 1111) AS data_type,"
            + " (case when t.data_type_owner is null then t.data_type else t.data_type_owner || '.' || t.data_type end) type_name,"
            + " (case when t.char_length > 0 then t.char_length else nvl(t.data_precision,t.data_length) end) AS column_size,"
            + " 0 AS buffer_length,"
            + " t.data_scale decimal_digits,"
            + " 10 AS num_prec_radix,"
            + " decode(t.nullable, 'N', 0, 1) AS nullable,"
            + " c.comments " + ClientConstants.JDBCCOLS_REMARKS + ", "
            + " t.data_default column_def,"
            + " 0 AS sql_data_type,"
            + " 0 AS sql_datetime_sub,"
            + " t.data_length char_octet_length,"
            + " t.column_id ordinal_position,"
            + " decode(t.nullable, 'N', 'NO', 'YES') AS is_nullable "
            + "FROM all_tab_columns t, sys.all_col_comments c "
            + "WHERE t.owner = '%s' and t.owner = c.owner and t.table_name = c.table_name and t.column_name = c.column_name "
            + "AND Upper(t.table_name) in (%s) "
            + "ORDER BY table_schem, table_name, ordinal_position";
    protected static final String SQL_PRIMARY_KEYS = ""
            + "SELECT"
            + " NULL table_cat,"
            + " c.owner " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + " c.table_name " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + " c.column_name " + ClientConstants.JDBCCOLS_COLUMN_NAME + ","
            + " c.position key_seq,"
            + " c.constraint_name pk_name "
            + "FROM all_cons_columns c, all_constraints k "
            + "WHERE k.constraint_type = 'P'"
            + " AND k.constraint_name = c.constraint_name"
            + " AND k.table_name = c.table_name"
            + " AND k.owner = c.owner"
            + " AND k.owner = '%s'"
            + " AND Upper(k.table_name) in (%s) "
            + "ORDER BY c.owner,c.table_name,c.position";
    protected static final String SQL_FOREIGN_KEYS = ""
            + "with"
            + " fkey as (select"
            + "             r_owner,"
            + "             r_constraint_name,"
            + "             owner fktable_schem,"
            + "             constraint_name fk_name,"
            + "             table_name fktable_name,"
            + "             delete_rule,deferrable,deferred"
            + "          from all_constraints t"
            + "          where constraint_type = 'R' and owner = '%s' and Upper(table_name) in (%s)),"
            + " fpkey as (select"
            + "              fktable_schem,"
            + "              fk_name,"
            + "              fktable_name,"
            + "              t2.delete_rule,"
            + "              t2.deferrable,"
            + "              t2.deferred,"
            + "              owner pktable_schem,"
            + "              constraint_name pk_name,"
            + "              table_name pktable_name"
            + "          from all_constraints t1 inner join fkey t2 on (t1.owner = t2.r_owner and t1.constraint_name = t2.r_constraint_name)"
            + "          where t1.constraint_type = 'P')"
            + " select"
            + "   fpkey.fktable_schem,"
            + "   fpkey.fk_name,"
            + "   fpkey.fktable_name,"
            + "   null update_rule,"
            + "   decode(fpkey.delete_rule, 'CASCADE', 0, 'SET NULL', 2, 1) as delete_rule,"
            + "   decode(fpkey.DEFERRABLE, 'DEFERRABLE', 5, 'NOT DEFERRABLE', 7, 'DEFERRED', 6) deferrability,"
            + "   fpkey.deferred,"
            + "   fpkey.pktable_schem,"
            + "   fpkey.pk_name,"
            + "   fpkey.pktable_name,"
            + "   fcol.column_name fkcolumn_name,"
            + "   pcol.column_name pkcolumn_name,"
            + "   fcol.position as key_seq"
            + " from fpkey"
            + "   inner join all_cons_columns fcol on fpkey.fktable_schem = fcol.owner and  fpkey.fk_name = fcol.constraint_name"
            + "   inner join all_cons_columns pcol on fpkey.pktable_schem = pcol.owner and  fpkey.pk_name = pcol.constraint_name"
            + " where fcol.position = pcol.position "
            + " order by pktable_schem, pktable_name, key_seq";
    protected static final String SQL_INDEX_KEYS = ""
            + "SELECT"
            + " null table_cat,"
            + " i.owner " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + " i.table_name " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + " decode(i.uniqueness, 'UNIQUE', 0, 1) AS non_unique,"
            + " null index_qualifier,"
            + " i.index_name,"
            + " (case when index_type = 'BITMAP' then 2 else 1 end) AS " + ClientConstants.JDBCIDX_TYPE + ","
            + " c.column_position ordinal_position,"
            + " c.column_name,"
            + " null asc_or_desc,"
            + " i.distinct_keys cardinality,"
            + " i.leaf_blocks pages,"
            + " null filter_condition,"
            + " (case when (select count(*) from all_constraints k where k.owner = i.owner and k.table_name = i.table_name"
            + "  and k.constraint_type = 'P' AND k.constraint_name = i.index_name) > 0 then 0 else 1 end) " + ClientConstants.JDBCIDX_PRIMARY_KEY + ","
            + " null " + ClientConstants.JDBCIDX_FOREIGN_KEY + " "
            + "FROM all_indexes i, all_ind_columns c "
            + "WHERE i.owner = '%s'"
            + " AND Upper(i.table_name) in (%s)"
            + " AND i.index_name = c.index_name"
            + " AND i.table_owner = c.table_owner"
            + " AND i.table_name = c.table_name"
            + " AND i.owner = c.index_owner "
            + "ORDER BY non_unique, type, index_name, ordinal_position ";

    @Override
    public String getSql4TableColumns(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            String tablesIn = constructIn(aTableNames);
            return String.format(SQL_COLUMNS, prepareName(aOwnerName), tablesIn.toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4Indexes(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            String tablesIn = constructIn(aTableNames);
            return String.format(SQL_INDEX_KEYS, prepareName(aOwnerName), tablesIn.toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4TablePrimaryKeys(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            String tablesIn = constructIn(aTableNames);
            return String.format(SQL_PRIMARY_KEYS, prepareName(aOwnerName), tablesIn.toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public String getSql4TableForeignKeys(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            String tablesList = constructIn(aTableNames).toUpperCase();
            return String.format(SQL_FOREIGN_KEYS, prepareName(aOwnerName), tablesList);
        } else {
            return null;
        }
    }

    @Override
    public String getSql4DropTable(String aSchemaName, String aTableName) {
        return "drop table " + makeFullName(aSchemaName, aTableName);
    }

    @Override
    public String getSql4EmptyTableCreation(String aSchemaName, String aTableName, String aPkFieldName) {
        String fullName = makeFullName(aSchemaName, aTableName);
        aPkFieldName = wrapNameIfRequired(aPkFieldName);
        return "CREATE TABLE " + fullName + " ("
                + aPkFieldName + " NUMBER NOT NULL,"
                + "CONSTRAINT " + wrapNameIfRequired(generatePkName(aTableName, PKEY_NAME_SUFFIX)) + " PRIMARY KEY (" + aPkFieldName + "))";
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
        String typeDefine = "";
        String sqlTypeName = aField.getTypeInfo().getSqlTypeName().toUpperCase();
        typeDefine += sqlTypeName;
        int sqlType = aField.getTypeInfo().getSqlType();
        // field length
        int size = aField.getSize();
        if (size > 0) {
            int scale = aField.getScale();
            if (SQLUtils.getTypeGroup(sqlType) == SQLUtils.TypesGroup.NUMBERS && size > 38) {
                typeDefine = " FLOAT (" + String.valueOf(size) + ")";
            } else if (resolver.isScaled(sqlTypeName) && resolver.isSized(sqlTypeName)) {
                typeDefine += "(" + String.valueOf(size) + "," + String.valueOf(scale) + ")";
            } else if (resolver.isSized(sqlTypeName)) {
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
        return getSql4FieldDefinition(aField, true);
    }

    private String getSql4FieldDefinition(Field aField, boolean aCurrentNullable) {
        String fieldDefinition = wrapNameIfRequired(aField.getName()) + " " + getFieldTypeDefinition(aField);

        if (aField.isNullable()) {
            if (!aCurrentNullable) {
                fieldDefinition += " null";
            }
        } else {
            if (aCurrentNullable) {
                fieldDefinition += " not null";
            }
        }
        return fieldDefinition;
    }

    @Override
    public String getSql4DropFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        String constraintName = wrapNameIfRequired(aFk.getCName());
        String tableName = makeFullName(aSchemaName, aFk.getTable());
        return "alter table " + tableName + " drop constraint " + constraintName;
    }

    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        List<ForeignKeySpec> fkList = new ArrayList<>();
        fkList.add(aFk);
        return getSql4CreateFkConstraint(aSchemaName, fkList);
    }

    @Override
    public String getUsersSpaceInitResourceName() {
        return "/" + OracleSqlDriver.class.getPackage().getName().replace(".", "/") + "/sqlscripts/OracleInitUsersSpace.sql";
    }

    @Override
    public String getVersionInitResourceName() {
        return "/" + OracleSqlDriver.class.getPackage().getName().replace(".", "/") + "/sqlscripts/OracleInitVersion.sql";
    }

    @Override
    public Set<Integer> getSupportedJdbcDataTypes() {
        return resolver.getSupportedJdbcDataTypes();
    }

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
    public String[] getSqls4ModifyingField(String aSchemaName, String aTableName, Field aOldFieldMd, Field aNewFieldMd) {
        List<String> sqls = new ArrayList<>();
        Field newFieldMd = aNewFieldMd.copy();
        String fullTableName = makeFullName(aSchemaName, aTableName);
        String updateDefinition = String.format(MODIFY_FIELD_SQL_PREFIX, fullTableName) + wrapNameIfRequired(aOldFieldMd.getName()) + " ";
        String fieldDefination = getFieldTypeDefinition(newFieldMd);

        DataTypeInfo newTypeInfo = newFieldMd.getTypeInfo();
        String newSqlTypeName = newTypeInfo.getSqlTypeName();
        if (newSqlTypeName == null) {
            newSqlTypeName = "";
        }
        int newScale = newFieldMd.getScale();
        int newSize = newFieldMd.getSize();
        boolean newNullable = newFieldMd.isNullable();

        DataTypeInfo oldTypeInfo = aOldFieldMd.getTypeInfo();
        String oldSqlTypeName = oldTypeInfo.getSqlTypeName();
        if (oldSqlTypeName == null) {
            oldSqlTypeName = "";
        }
        int oldScale = aOldFieldMd.getScale();
        int oldSize = aOldFieldMd.getSize();
        boolean oldNullable = aOldFieldMd.isNullable();

        if (!oldSqlTypeName.equalsIgnoreCase(newSqlTypeName)
                || (resolver.isSized(newSqlTypeName) && newSize != oldSize)
                || (resolver.isScaled(newSqlTypeName) && newScale != oldScale)) {
            sqls.add(updateDefinition + fieldDefination);
        }
        if (oldNullable != newNullable) {
            sqls.add(updateDefinition + (newNullable ? " null" : " not null"));
        }
        return (String[]) sqls.toArray(new String[sqls.size()]);
    }

    @Override
    public String[] getSqls4RenamingField(String aSchemaName, String aTableName, String aOldFieldName, Field aNewFieldMd) {
        String fullTableName = makeFullName(aSchemaName, aTableName);
        String sqlText = String.format(RENAME_FIELD_SQL_PREFIX, fullTableName, wrapNameIfRequired(aOldFieldName), wrapNameIfRequired(aNewFieldMd.getName()));
        return new String[]{
            sqlText
        };
    }

    @Override
    public String[] getSql4CreateColumnComment(String aOwnerName, String aTableName, String aFieldName, String aDescription) {
        String ownerName = wrapNameIfRequired(aOwnerName);
        String tableName = wrapNameIfRequired(aTableName);
        String fieldName = wrapNameIfRequired(aFieldName);
        String sqlText = ownerName == null ? StringUtils.join(".", tableName, fieldName) : StringUtils.join(".", ownerName, tableName, fieldName);
        if (aDescription == null) {
            aDescription = "";
        }
        return new String[]{"comment on column " + sqlText + " is '" + aDescription.replaceAll("'", "''") + "'"};
    }

    @Override
    public String getSql4CreateTableComment(String aOwnerName, String aTableName, String aDescription) {
        String sqlText = StringUtils.join(".", wrapNameIfRequired(aOwnerName), wrapNameIfRequired(aTableName));
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
    public TypesResolver getTypesResolver() {
        return resolver;
    }

    @Override
    public String getSql4DropIndex(String aSchemaName, String aTableName, String aIndexName) {
        return "drop index " + makeFullName(aSchemaName, aIndexName);
    }

    @Override
    public String getSql4CreateIndex(String aSchemaName, String aTableName, DbTableIndexSpec aIndex) {
        assert aIndex.getColumns().size() > 0 : "index definition must consist of at least 1 column";
        String indexName = makeFullName(aSchemaName, aIndex.getName());
        String tableName = makeFullName(aSchemaName, aTableName);
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
            fieldsList += wrapNameIfRequired(column.getColumnName());
            if (i != aIndex.getColumns().size() - 1) {
                fieldsList += ", ";
            }
        }
        return "create " + modifier + " index " + indexName + " on " + tableName + "( " + fieldsList + " )";
    }

    @Override
    public String getSql4TablesEnumeration(String schema4Sql) {
        if (schema4Sql == null || schema4Sql.isEmpty()) {
            return SQL_ALL_TABLES_VIEWS;
        } else {
            return String.format(SQL_SCHEMA_TABLES_VIEWS, prepareName(schema4Sql));
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
        String tableName = makeFullName(aSchemaName, aPk.getTable());
        return "alter table " + tableName + " drop primary key";
    }

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
            switch (fk.getFkDeleteRule()) {
                case CASCADE:
                    fkRule += " ON DELETE CASCADE ";
                    break;
                case NOACTION:
                case SETDEFAULT:
//                    fkRule += " ON DELETE NO ACTION ";
                    break;
//                case SETDEFAULT:
//                    break;
                case SETNULL:
                    fkRule += " ON DELETE SET NULL ";
                    break;
            }
            if (fk.getFkDeferrable()) {
                fkRule += " DEFERRABLE INITIALLY DEFERRED";
            }
            return String.format("ALTER TABLE %s ADD (CONSTRAINT %s"
                    + " FOREIGN KEY (%s) REFERENCES %s (%s) %s)", fkTableName, fkName.isEmpty() ? "" : wrapNameIfRequired(fkName), fkColumnName, pkTableName, pkColumnName, fkRule);
        }
        return null;
    }

    @Override
    public String[] getSql4CreatePkConstraint(String aSchemaName, List<PrimaryKeySpec> listPk) {
        if (listPk != null && listPk.size() > 0) {
            PrimaryKeySpec pk = listPk.get(0);
            String tableName = pk.getTable();
            String pkName = wrapNameIfRequired(generatePkName(tableName, PKEY_NAME_SUFFIX));
            String pkColumnName = wrapNameIfRequired(pk.getField());
            for (int i = 1; i < listPk.size(); i++) {
                pk = listPk.get(i);
                pkColumnName += ", " + wrapNameIfRequired(pk.getField());
            }
            return new String[]{
                String.format("ALTER TABLE %s ADD CONSTRAINT %s PRIMARY KEY (%s)", makeFullName(aSchemaName, tableName), pkName, pkColumnName)
            };
        };
        return null;
    }

    @Override
    public boolean isConstraintsDeferrable() {
        return true;
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
