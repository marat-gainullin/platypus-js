package com.eas.sqldrivers;

import com.eas.client.ClientConstants;
import com.eas.client.changes.JdbcChangeValue;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.JdbcField;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.sqldrivers.resolvers.PostgreTypesResolver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import com.eas.util.StringUtils;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;
import org.postgis.PGgeometry;

/**
 *
 * @author mg
 */
public class PostgreSqlDriver extends SqlDriver {

    // настройка экранирования наименования объектов БД
    private static final TwinString[] charsForWrap = {new TwinString("\"", "\"")};
    private static final char[] restrictedChars = {' ', ',', '\'', '"'};

    protected static final PostgreTypesResolver resolver = new PostgreTypesResolver();
    protected static final String SET_SCHEMA_CLAUSE = "set search_path = %s,public";
    protected static final String GET_SCHEMA_CLAUSE = "select current_schema()";
    protected static final String CREATE_SCHEMA_CLAUSE = "CREATE SCHEMA %s";
    protected static final String DEF_OTHER_TYPE_NAME = "point";
    protected static final String RENAME_FIELD_SQL_PREFIX = "alter table %s rename column %s to %s";
    protected static final String MODIFY_FIELD_SQL_PREFIX = "alter table %s alter ";
    
    @Override
    public boolean is(String aDialect) {
        return ClientConstants.SERVER_PROPERTY_POSTGRE_DIALECT.equals(aDialect);
    }

    @Override
    public TypesResolver getTypesResolver() {
        return resolver;
    }

    @Override
    public String getUsersSpaceInitResourceName() {
        return "/sqlscripts/PostgreInitUsersSpace.sql";
    }

    @Override
    public String getVersionInitResourceName() {
        return "/sqlscripts/PostgreInitVersion.sql";
    }

    @Override
    public String getSql4GetConnectionContext() {
        return GET_SCHEMA_CLAUSE;
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
    public String[] getSql4CreateColumnComment(String aOwnerName, String aTableName, String aFieldName, String aDescription) {
        String ownerName = wrapNameIfRequired(aOwnerName);
        String tableName = wrapNameIfRequired(aTableName);
        String fieldName = wrapNameIfRequired(aFieldName);
        String sqlText = aOwnerName == null ? StringUtils.join(".", tableName, fieldName) : StringUtils.join(".", ownerName, tableName, fieldName);
        if (aDescription == null) {
            aDescription = "";
        }
        return new String[]{String.format("comment on column %s is '%s'", sqlText, aDescription.replaceAll("'", "''"))};
    }

    @Override
    public String getSql4CreateTableComment(String aOwnerName, String aTableName, String aDescription) {
        String sqlText = StringUtils.join(".", wrapNameIfRequired(aOwnerName), wrapNameIfRequired(aTableName));
        if (aDescription == null) {
            aDescription = "";
        }
        return String.format("comment on table %s is '%s'", sqlText, aDescription.replaceAll("'", "''"));
    }

    @Override
    public String getSql4DropTable(String aSchemaName, String aTableName) {
        return "drop table " + makeFullName(aSchemaName, aTableName) + " cascade";
    }

    @Override
    public String getSql4DropIndex(String aSchemaName, String aTableName, String aIndexName) {
        return "drop index " + makeFullName(aSchemaName, aIndexName);
    }

    @Override
    public String getSql4CreateIndex(String aSchemaName, String aTableName, DbTableIndexSpec aIndex) {
        assert aIndex.getColumns().size() > 0 : "index definition must consist of at least 1 column";
        String indexName = wrapNameIfRequired(aIndex.getName());
        String tableName = makeFullName(aSchemaName, aTableName);
        String modifier = "";
        if (aIndex.isUnique()) {
            modifier = "unique";
        }
        String methodClause = "";
        if (aIndex.isHashed()) {
            methodClause = " using hash ";
        }
        String fieldsList = "";
        for (int i = 0; i < aIndex.getColumns().size(); i++) {
            DbTableIndexColumnSpec column = aIndex.getColumns().get(i);
            fieldsList += wrapNameIfRequired(column.getColumnName());
            if (i != aIndex.getColumns().size() - 1) {
                fieldsList += ", ";
            }
        }
        return "create " + modifier + " index " + indexName + " on " + tableName + " " + methodClause + " ( " + fieldsList + " )";
    }

    @Override
    public String getSql4DropFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        String constraintName = wrapNameIfRequired(aFk.getCName());
        String tableName = makeFullName(aSchemaName, aFk.getTable());
        return "alter table " + tableName + " drop constraint " + constraintName + " cascade";
    }

    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        List<ForeignKeySpec> fkList = new ArrayList<>();
        fkList.add(aFk);
        return getSql4CreateFkConstraint(aSchemaName, fkList);
    }

    @Override
    public String getSql4EmptyTableCreation(String aSchemaName, String aTableName, String aPkFieldName) {
        String fullName = makeFullName(aSchemaName, aTableName);
        String pkFieldName = wrapNameIfRequired(aPkFieldName);
        return "CREATE TABLE " + fullName + " ("
                + pkFieldName + " NUMERIC NOT NULL,"
                + "CONSTRAINT " + wrapNameIfRequired(generatePkName(aTableName, PKEY_NAME_SUFFIX)) + " PRIMARY KEY (" + pkFieldName + "))";
    }

    private String getFieldTypeDefinition(JdbcField aField) {
        String typeDefine = "";
        String sqlTypeName = aField.getType().toLowerCase();
        // field length
        int size = aField.getSize();
        int scale = aField.getScale();

        typeDefine += sqlTypeName;
        if (resolver.isScaled(sqlTypeName) && resolver.isSized(sqlTypeName) && size > 0) {
            typeDefine += "(" + String.valueOf(size) + "," + String.valueOf(scale) + ")";
        } else {
            if (resolver.isSized(sqlTypeName) && size > 0) {
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
        String fieldName = wrapNameIfRequired(aField.getName());
        String fieldDefinition = fieldName + " " + getFieldTypeDefinition(aField);

        if (!aField.isNullable()) {
            fieldDefinition += " not null";
        } else {
            fieldDefinition += " null";
        }
        return fieldDefinition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getSqls4ModifyingField(String aSchemaName, String aTableName, JdbcField aOldFieldMd, JdbcField aNewFieldMd) {
        List<String> sqls = new ArrayList<>();
        JdbcField newFieldMd = aNewFieldMd.copy();
        String fullTableName = makeFullName(aSchemaName, aTableName);
        String fieldName = wrapNameIfRequired(aOldFieldMd.getName());
        String updateDefinition = String.format(MODIFY_FIELD_SQL_PREFIX, fullTableName) + fieldName + " ";
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

        if (!newSqlTypeName.equalsIgnoreCase(oldSqlTypeName)
                || (resolver.isSized(newSqlTypeName) && newSize != oldSize)
                || (resolver.isScaled(newSqlTypeName) && newScale != oldScale)) {
            sqls.add(updateDefinition + " type " + fieldDefination + " using " + fieldName + "::" + newSqlTypeName);
        }
        if (oldNullable != newNullable) {
            sqls.add(updateDefinition + (newNullable ? " drop not null" : " set not null"));
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
    public String parseException(Exception ex) {
        return ex.getMessage();
    }

    @Override
    public String getSql4CreateSchema(String aSchemaName, String aPassword) {
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            return String.format(CREATE_SCHEMA_CLAUSE, aSchemaName);
        }
        throw new IllegalArgumentException("Schema name is null or empty.");
    }

    @Override
    public String getSql4DropPkConstraint(String aSchemaName, PrimaryKeySpec aPk) {
        String constraintName = wrapNameIfRequired(aPk.getCName());
        String tableFullName = makeFullName(aSchemaName, aPk.getTable());
        return "alter table " + tableFullName + " drop constraint " + constraintName;
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
            switch (fk.getFkUpdateRule()) {
                case CASCADE:
                    fkRule += " ON UPDATE CASCADE ";
                    break;
                case NOACTION:
                    fkRule += " ON UPDATE no action";
                    break;
                case SETDEFAULT:
                    fkRule += " ON UPDATE set default";
                    break;
                case SETNULL:
                    fkRule += " ON UPDATE set null";
                    break;
            }
            switch (fk.getFkDeleteRule()) {
                case CASCADE:
                    fkRule += " ON DELETE CASCADE ";
                    break;
                case NOACTION:
                    fkRule += " ON DELETE no action ";
                    break;
                case SETDEFAULT:
                    fkRule += " ON DELETE set default ";
                    break;
                case SETNULL:
                    fkRule += " ON DELETE set null ";
                    break;
            }
            if (fk.getFkDeferrable()) {
                fkRule += " DEFERRABLE INITIALLY DEFERRED";
            }
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
                String.format("ALTER TABLE %s ADD CONSTRAINT %s PRIMARY KEY (%s)", pkTableName, pkName, pkColumnName)
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
    public String[] getSql4DroppingField(String aSchemaName, String aTableName, String aFieldName) {
        String fullTableName = wrapNameIfRequired(aTableName);
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            fullTableName = wrapNameIfRequired(aSchemaName) + "." + fullTableName;
        }
        return new String[]{
            String.format(DROP_FIELD_SQL_PREFIX, fullTableName) + wrapNameIfRequired(aFieldName) + " cascade"
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
        return isHaveUpperCase(aName);
    }

    @Override
    public JdbcChangeValue convertGeometry(String aValue, Connection aConnection) throws SQLException {
        JdbcChangeValue jdbcValue = new JdbcChangeValue(null, null, 0, null);
        jdbcValue.value = aValue != null ? new PGgeometry(aValue) : null;
        jdbcValue.jdbcType = Types.OTHER;
        jdbcValue.sqlTypeName = "geometry";
        return jdbcValue;
    }

    @Override
    public String readGeometry(Wrapper aRs, int aColumnIndex, Connection aConnection) throws SQLException {
        Object read = aRs instanceof ResultSet ? ((ResultSet) aRs).getObject(aColumnIndex) : ((CallableStatement) aRs).getObject(aColumnIndex);
        boolean wasNull = aRs instanceof ResultSet ? ((ResultSet) aRs).wasNull() : ((CallableStatement) aRs).wasNull();
        if (wasNull) {
            return null;
        } else {
            if (read instanceof PGgeometry) {
                PGgeometry pgg = (PGgeometry) read;
                read = pgg.getGeometry();
            }else if(read.getClass().getName().equals(PGgeometry.class.getName())){// Crazy netbeans designer!
                return read.toString();
            }
            if (read instanceof org.postgis.Geometry) {
                org.postgis.Geometry g = (org.postgis.Geometry) read;
                StringBuffer sb = new StringBuffer();
                g.outerWKT(sb);
                return sb.toString();
            } else {
                return null;
            }
        }
    }
}
