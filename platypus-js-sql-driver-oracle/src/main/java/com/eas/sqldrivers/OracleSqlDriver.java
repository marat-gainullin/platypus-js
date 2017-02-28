package com.eas.sqldrivers;

import com.eas.client.ClientConstants;
import com.eas.client.changes.JdbcChangeValue;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.JdbcField;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.sqldrivers.resolvers.OracleTypesResolver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import com.eas.util.StringUtils;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleConnection;
import oracle.sql.STRUCT;
import org.geotools.data.oracle.sdo.GeometryConverter;

/**
 *
 * @author mg
 */
public class OracleSqlDriver extends SqlDriver {

    // настройка экранирования наименования объектов БД
    private static final TwinString[] charsForWrap = {new TwinString("\"", "\"")};
    private static final char[] restrictedChars = {' ', ',', '\'', '"'};

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

    @Override
    public boolean is(String aDialect) {
        return ClientConstants.SERVER_PROPERTY_ORACLE_DIALECT.equals(aDialect);
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

    private String getFieldTypeDefinition(JdbcField aField) {
        String typeDefine = "";
        String sqlTypeName = aField.getType().toUpperCase();
        typeDefine += sqlTypeName;
        // field length
        int size = aField.getSize();
        if (size > 0) {
            int scale = aField.getScale();
            if (resolver.isScaled(sqlTypeName) && resolver.isSized(sqlTypeName)) {
                typeDefine += "(" + String.valueOf(size) + "," + String.valueOf(scale) + ")";
            } else if (resolver.isSized(sqlTypeName)) {
                typeDefine += "(" + String.valueOf(size) + ")";
            }
        }
        return typeDefine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSql4FieldDefinition(JdbcField aField) {
        String fieldDefinition = wrapNameIfRequired(aField.getName()) + " " + getFieldTypeDefinition(aField);

        if (aField.isNullable()) {
            fieldDefinition += " null";
        } else {
            fieldDefinition += " not null";
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
        return "/sqlscripts/OracleInitUsersSpace.sql";
    }

    @Override
    public String getVersionInitResourceName() {
        return "/sqlscripts/OracleInitVersion.sql";
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
     * {@inheritDoc}
     */
    @Override
    public String[] getSqls4ModifyingField(String aSchemaName, String aTableName, JdbcField aOldFieldMd, JdbcField aNewFieldMd) {
        List<String> sqls = new ArrayList<>();
        JdbcField newFieldMd = aNewFieldMd.copy();
        String fullTableName = makeFullName(aSchemaName, aTableName);
        String updateDefinition = String.format(MODIFY_FIELD_SQL_PREFIX, fullTableName) + wrapNameIfRequired(aOldFieldMd.getName()) + " ";
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
    public String[] getSqls4RenamingField(String aSchemaName, String aTableName, String aOldFieldName, JdbcField aNewFieldMd) {
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
    public String getSql4CreateSchema(String aSchemaName, String aPassword) {
        if (aSchemaName == null || aSchemaName.isEmpty()) {
            throw new IllegalArgumentException("Schema name is null or empty.");
        }
        if (aPassword == null || aPassword.isEmpty()) {
            throw new IllegalArgumentException("Schema owner password is null or empty.");
        }
        return String.format(CREATE_SCHEMA_CLAUSE, aSchemaName, "");
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
    public String[] getSqls4AddingField(String aSchemaName, String aTableName, JdbcField aField) {
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

    @Override
    public JdbcChangeValue convertGeometry(String aValue, Connection aConnection) throws SQLException {
        if (!(aConnection instanceof OracleConnection)) {
            aConnection = aConnection.unwrap(OracleConnection.class);
        }
        try {
            GeometryConverter gc = new GeometryConverter((OracleConnection) aConnection);
            JdbcChangeValue jdbcValue = new JdbcChangeValue(null, null, 0, null);
            WKTReader reader = new WKTReader();
            jdbcValue.value = aValue != null ? gc.toSDO(reader.read(aValue)) : null;
            jdbcValue.jdbcType = Types.STRUCT;
            jdbcValue.sqlTypeName = "MDSYS.SDO_GEOMETRY";
            return jdbcValue;
        } catch (ParseException ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public String readGeometry(Wrapper aRs, int aColumnIndex, Connection aConnection) throws SQLException {
        Object read = aRs instanceof ResultSet ? ((ResultSet) aRs).getObject(aColumnIndex) : ((CallableStatement) aRs).getObject(aColumnIndex);
        boolean wasNull = aRs instanceof ResultSet ? ((ResultSet) aRs).wasNull() : ((CallableStatement) aRs).wasNull();
        if (wasNull) {
            return null;
        } else {
            if (read instanceof STRUCT) {
                STRUCT struct = (STRUCT) read;
                GeometryConverter reader = new GeometryConverter(struct.getInternalConnection());
                Geometry geometry = reader.asGeometry(struct);
                WKTWriter writer = new WKTWriter();
                return writer.write(geometry);
            } else {
                return null;
            }
        }
    }
}
