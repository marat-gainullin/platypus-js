package com.eas.sqldrivers;

import com.eas.client.ClientConstants;
import com.eas.client.changes.JdbcChangeValue;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.JdbcField;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.sqldrivers.resolvers.Db2TypesResolver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import com.eas.util.StringUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kl
 */
public class Db2SqlDriver extends SqlDriver {

    // Настройка экранирования наименования объектов БД
    private static final TwinString[] charsForWrap = {new TwinString("\"", "\"")};
    private static final char[] restrictedChars = {' ', ',', '\'', '"'};

    protected static final String SET_SCHEMA_CLAUSE = "SET SCHEMA %s";
    protected static final String GET_SCHEMA_CLAUSE = "VALUES CURRENT SCHEMA";
    protected static final String CREATE_SCHEMA_CLAUSE = "CREATE SCHEMA %s";
    protected static final Db2TypesResolver resolver = new Db2TypesResolver();
    /**
     * Listing of SQLSTATE values
     */
    protected static final int[] db2ErrorCodes = {};
    protected static final String[] platypusErrorMessages = {};
    protected static final String SQL_RENAME_FIELD = "alter table %s rename column %s to %s";
    protected static final String SQL_MODIFY_FIELD = "alter table %s modify ";
    protected static final String ALTER_FIELD_SQL_PREFIX = "alter table %s alter column ";
    protected static final String REORG_TABLE = "CALL SYSPROC.ADMIN_CMD('REORG TABLE %s')";
    protected static final String VOLATILE_TABLE = "ALTER TABLE %s VOLATILE CARDINALITY";

    @Override
    public boolean is(String aDialect) {
        return ClientConstants.SERVER_PROPERTY_DB2_DIALECT.equals(aDialect);
    }

    @Override
    public String getSql4CreateSchema(String aSchemaName, String aPassword) {
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            return String.format(CREATE_SCHEMA_CLAUSE, aSchemaName);
        }
        throw new IllegalArgumentException("Schema name is null or empty.");
    }

    @Override
    public String[] getSql4CreateColumnComment(String aOwnerName, String aTableName, String aFieldName, String aDescription) {
        aOwnerName = wrapNameIfRequired(aOwnerName);
        aTableName = wrapNameIfRequired(aTableName);
        aFieldName = wrapNameIfRequired(aFieldName);
        String sqlText = aOwnerName == null ? StringUtils.join(".", aTableName, aFieldName) : StringUtils.join(".", aOwnerName, aTableName, aFieldName);
        if (aDescription == null) {
            aDescription = "";
        }
        return new String[]{"comment on column " + sqlText + " is '" + aDescription + "'"};
    }

    @Override
    public String getSql4CreateTableComment(String aOwnerName, String aTableName, String aDescription) {
        String sqlText = StringUtils.join(".", wrapNameIfRequired(aOwnerName), wrapNameIfRequired(aTableName));
        if (aDescription == null) {
            aDescription = "";
        }
        return "comment on table " + sqlText + " is '" + aDescription + "'";
    }

    @Override
    public String getSql4DropTable(String aSchemaName, String aTableName) {
        return "drop table " + makeFullName(aSchemaName, aTableName);
    }

    @Override
    public String getSql4DropIndex(String aSchemaName, String aTableName, String aIndexName) {
        return "drop index " + makeFullName(aSchemaName, aIndexName);
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
    public String getSql4CreateIndex(String aSchemaName, String aTableName, DbTableIndexSpec aIndex) {
        assert aIndex.getColumns().size() > 0 : "index definition must consist of at least 1 column";
        String indexName = makeFullName(aSchemaName, aIndex.getName());
        String tableName = makeFullName(aSchemaName, aTableName);
        String modifier = "";
        if (aIndex.isUnique()) {
            modifier = "unique";
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
    public String getSql4EmptyTableCreation(String aSchemaName, String aTableName, String aPkFieldName) {
        String tableName = makeFullName(aSchemaName, aTableName);
        aPkFieldName = wrapNameIfRequired(aPkFieldName);
        return "CREATE TABLE " + tableName + " ("
                + aPkFieldName + " DECIMAL(18,0) NOT NULL,"
                + "CONSTRAINT " + wrapNameIfRequired(generatePkName(aTableName, PKEY_NAME_SUFFIX)) + " PRIMARY KEY (" + aPkFieldName + "))";
    }

    @Override
    public String parseException(Exception ex) {
        if (ex != null && ex instanceof SQLException) {
            SQLException sqlEx = (SQLException) ex;
            int errorCode = sqlEx.getErrorCode();
            for (int i = 0; i < db2ErrorCodes.length; i++) {
                if (errorCode == db2ErrorCodes[i]) {
                    return platypusErrorMessages[i];
                }
            }
        }
        return ex.getLocalizedMessage();
    }

    private String getFieldTypeDefinition(JdbcField aField) {
        String typeName = aField.getType();
        int size = aField.getSize();
        int scale = aField.getScale();
        if (resolver.isScaled(typeName) && resolver.isSized(typeName) && size > 0) {
            typeName += "(" + String.valueOf(size) + "," + String.valueOf(scale) + ")";
        } else if (resolver.isSized(typeName) && size > 0) {
            typeName += "(" + String.valueOf(size) + ")";
        } else if (resolver.isScaled(typeName) && scale > 0) {
            typeName += "(" + String.valueOf(scale) + ")";
        }
        return typeName;
    }

    @Override
    public String getSql4FieldDefinition(JdbcField aField) {
        String fieldDefinition = wrapNameIfRequired(aField.getName()) + " " + getFieldTypeDefinition(aField);
        return fieldDefinition;
    }

    @Override
    public String[] getSqls4ModifyingField(String aSchemaName, String aTableName, JdbcField aOldFieldMd, JdbcField aNewFieldMd) {
        List<String> sqls = new ArrayList<>();
        JdbcField newFieldMd = aNewFieldMd.copy();
        String fullTableName = makeFullName(aSchemaName, aTableName);
        String updateDefinition = String.format(ALTER_FIELD_SQL_PREFIX, fullTableName) + wrapNameIfRequired(aOldFieldMd.getName()) + " ";
        String fieldDefination = getFieldTypeDefinition(newFieldMd);

        String newSqlTypeName = newFieldMd.getType();
        if (newSqlTypeName == null) {
            newSqlTypeName = "";
        }
        int newScale = newFieldMd.getScale();
        int newSize = newFieldMd.getSize();
        boolean newNullable = newFieldMd.isNullable();

        String oldSqlTypeName = aOldFieldMd.getType();
        if (oldSqlTypeName == null) {
            oldSqlTypeName = "";
        }
        int oldScale = aOldFieldMd.getScale();
        int oldSize = aOldFieldMd.getSize();
        boolean oldNullable = aOldFieldMd.isNullable();

        sqls.add(getSql4VolatileTable(fullTableName));
        if (!oldSqlTypeName.equalsIgnoreCase(newSqlTypeName)
                || (resolver.isSized(newSqlTypeName) && newSize != oldSize)
                || (resolver.isScaled(newSqlTypeName) && newScale != oldScale)) {
            sqls.add(updateDefinition + " set data type " + fieldDefination);
        }
        if (oldNullable != newNullable) {
            sqls.add(updateDefinition + (newNullable ? " drop not null" : " set not null"));
        }
        if (sqls.size() == 1) {
            sqls.clear();
        } else {
            sqls.add(getSql4ReorgTable(fullTableName));
        }

        return (String[]) sqls.toArray(new String[sqls.size()]);
    }

    @Override
    public String[] getSql4DroppingField(String aSchemaName, String aTableName, String aFieldName) {
        String fullTableName = makeFullName(aSchemaName, aTableName);
        return new String[]{
            getSql4VolatileTable(fullTableName),
            String.format(DROP_FIELD_SQL_PREFIX, fullTableName) + wrapNameIfRequired(aFieldName),
            getSql4ReorgTable(fullTableName)
        };
    }

    /**
     * DB2 9.7 or later
     */
    @Override
    public String[] getSqls4RenamingField(String aSchemaName, String aTableName, String aOldFieldName, JdbcField aNewFieldMd) {
        String fullTableName = makeFullName(aSchemaName, aTableName);
        String sqlText = String.format(SQL_RENAME_FIELD, fullTableName, wrapNameIfRequired(aOldFieldName), wrapNameIfRequired(aNewFieldMd.getName()));
        return new String[]{
            getSql4VolatileTable(fullTableName),
            sqlText,
            getSql4ReorgTable(fullTableName)
        };
    }

    private String getSql4VolatileTable(String aTableName) {
        return String.format(VOLATILE_TABLE, aTableName);
    }

    private String getSql4ReorgTable(String aTableName) {
        return String.format(REORG_TABLE, aTableName);
    }

    @Override
    public TypesResolver getTypesResolver() {
        return resolver;
    }

    @Override
    public String getUsersSpaceInitResourceName() {
        return "/sqlscripts/Db2InitUsersSpace.sql";
    }

    @Override
    public String getVersionInitResourceName() {
        return "/sqlscripts/Db2InitVersion.sql";
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

    @Override
    public String getSql4DropPkConstraint(String aSchemaName, PrimaryKeySpec aPk) {
        return "alter table " + makeFullName(aSchemaName, aPk.getTable()) + " drop primary key";
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

            /**
             * The DB2 system does not allow the "on update cascade" option for
             * foreign key constraints.
             */
            String fkRule = " ON UPDATE NO ACTION";
            switch (fk.getFkDeleteRule()) {
                case CASCADE:
                    fkRule += " ON DELETE CASCADE ";
                    break;
                case NOACTION:
                case SETDEFAULT:
                    fkRule += " ON DELETE no action ";
                    break;
                case SETNULL:
                    fkRule += " ON DELETE set null ";
                    break;
            }
            //fkRule += " NOT ENFORCED";
            return String.format("ALTER TABLE %s ADD CONSTRAINT %s"
                    + " FOREIGN KEY (%s) REFERENCES %s (%s) %s", fkTableName, fkName.isEmpty() ? "" : wrapNameIfRequired(fkName), fkColumnName, pkTableName, pkColumnName, fkRule);
        }
        return null;
    }

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
                getSql4VolatileTable(pkTableName),
                String.format("ALTER TABLE %s ADD CONSTRAINT %s PRIMARY KEY (%s)", pkTableName, pkName, pkColumnName),
                getSql4ReorgTable(pkTableName)
            };
        }
        return null;
    }

    @Override
    public boolean isConstraintsDeferrable() {
        return false;
    }

    @Override
    public String[] getSqls4AddingField(String aSchemaName, String aTableName, JdbcField aField) {
        List<String> sqls = new ArrayList<>();
        String fullTableName = makeFullName(aSchemaName, aTableName);
        sqls.add(getSql4VolatileTable(fullTableName));
        sqls.add(String.format(SqlDriver.ADD_FIELD_SQL_PREFIX, fullTableName) + getSql4FieldDefinition(aField));
        if (!aField.isNullable()) {
            sqls.add(String.format(ALTER_FIELD_SQL_PREFIX, fullTableName) + wrapNameIfRequired(aField.getName()) + " set not null");
        }
        sqls.add(getSql4ReorgTable(fullTableName));
        return (String[]) sqls.toArray(new String[sqls.size()]);
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
    
    @Override
    public JdbcChangeValue convertGeometry(String aValue, Connection aConnection) throws SQLException {
        return null;
    }

    @Override
    public String readGeometry(Wrapper aRs, int aColumnIndex, Connection aConnection) throws SQLException {
        return null;
    }
}
