package com.eas.sqldrivers;

import com.eas.client.ClientConstants;
import com.eas.client.changes.JdbcChangeValue;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.JdbcField;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.sqldrivers.resolvers.MsSqlTypesResolver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mg
 */
public class MsSqlSqlDriver extends SqlDriver {

    // настройка экранирования наименования объектов БД
    private static final TwinString[] charsForWrap = {new TwinString("\"", "\""), new TwinString("[", "]")};
    private static final char[] restrictedChars = {' ', ',', '\'', '"'};

    protected static final String GET_SCHEMA_CLAUSE = "SELECT SCHEMA_NAME()";
    protected static final String CREATE_SCHEMA_CLAUSE = "CREATE SCHEMA %s";
    protected static final MsSqlTypesResolver resolver = new MsSqlTypesResolver();
    protected static final int[] mssqlErrorCodes = {
        826
    };
    protected static final String[] platypusErrorMessages = {
        EAS_TABLE_ALREADY_EXISTS
    };
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
    }

    @Override
    public boolean is(String aDialect) {
        return ClientConstants.SERVER_PROPERTY_MSSQL_DIALECT.equals(aDialect);
    }

    @Override
    public String getSql4DropTable(String aSchemaName, String aTableName) {
        String fullName = makeFullName(aSchemaName, aTableName);
        return "drop table " + fullName;
    }

    @Override
    public String getSql4EmptyTableCreation(String aSchemaName, String aTableName, String aPkFieldName) {
        String fullName = makeFullName(aSchemaName, aTableName);
        return "CREATE TABLE " + fullName + " ("
                + wrapNameIfRequired(aPkFieldName) + " NUMERIC(18, 0) NOT NULL,"
                + "CONSTRAINT " + wrapNameIfRequired(generatePkName(aTableName, PKEY_NAME_SUFFIX)) + " PRIMARY KEY (" + wrapNameIfRequired(aPkFieldName) + " ASC))";
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
        return ex != null ? ex.getLocalizedMessage() : null;
    }

    private String getFieldTypeDefinition(JdbcField aField) {
        String typeDefine = "";
        String sqlTypeName = aField.getType().toLowerCase();
        typeDefine += sqlTypeName;
        // field length
        int size = aField.getSize();
        int scale = aField.getScale();

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

    @Override
    public String getSql4DropFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        String tableName = makeFullName(aSchemaName, aFk.getTable());
        return "ALTER TABLE " + tableName + " DROP CONSTRAINT " + wrapNameIfRequired(aFk.getCName());
    }

    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        List<ForeignKeySpec> fkList = new ArrayList<>();
        fkList.add(aFk);
        return getSql4CreateFkConstraint(aSchemaName, fkList);
    }

    @Override
    public String getUsersSpaceInitResourceName() {
        return "/sqlscripts/MsSqlInitUsersSpace.sql";
    }

    @Override
    public String getVersionInitResourceName() {
        return "/sqlscripts/MsSqlInitVersion.sql";
    }

    @Override
    public void applyContextToConnection(Connection aConnection, String aSchema) throws Exception {
        //no-op
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
        String fullTableName = makeFullName(aSchemaName, aTableName);
        String alterFieldSql = String.format(ALTER_FIELD_SQL_PREFIX, fullTableName);
        return new String[]{alterFieldSql + getSql4FieldDefinition(aNewFieldMd)};
    }

    @Override
    public String[] getSqls4RenamingField(String aSchemaName, String aTableName, String aOldFieldName, JdbcField aNewFieldMd) {
        String fullTableName = makeFullName(aSchemaName, aTableName);
        String sql = String.format("EXEC sp_rename '%s.%s','%s','COLUMN'", fullTableName, aOldFieldName, aNewFieldMd.getName());
        return new String[]{sql};
    }

    @Override
    public String[] getSql4CreateColumnComment(String aOwnerName, String aTableName, String aFieldName, String aDescription) {
        if (aDescription == null) {
            aDescription = "";
        }
        return new String[]{String.format(ADD_COLUMN_COMMENT_CLAUSE, unwrapName(aOwnerName), unwrapName(aTableName), unwrapName(aFieldName), aDescription, unwrapName(aOwnerName), unwrapName(aTableName), unwrapName(aFieldName))};
    }

    @Override
    public String getSql4CreateTableComment(String aOwnerName, String aTableName, String aDescription) {
        if (aDescription == null) {
            aDescription = "";
        }
        return String.format(ADD_TABLE_COMMENT_CLAUSE, unwrapName(aOwnerName), unwrapName(aTableName), aDescription, unwrapName(aOwnerName), unwrapName(aTableName));
    }

    @Override
    public TypesResolver getTypesResolver() {
        return resolver;
    }

    @Override
    public String getSql4DropIndex(String aSchemaName, String aTableName, String aIndexName) {
        aTableName = makeFullName(aSchemaName, aTableName);
        return "drop index " + wrapNameIfRequired(aIndexName) + " on " + aTableName;
    }

    @Override
    public String getSql4CreateIndex(String aSchemaName, String aTableName, DbTableIndexSpec aIndex) {
        assert aIndex.getColumns().size() > 0 : "index definition must consist of at least 1 column";
        String indexName = wrapNameIfRequired(aIndex.getName());
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
            fieldsList += wrapNameIfRequired(column.getColumnName()) + " asc";
            if (i != aIndex.getColumns().size() - 1) {
                fieldsList += ", ";
            }
        }
        return "create " + modifier + " index " + indexName + " on " + tableName + "( " + fieldsList + " )";
    }

    @Override
    public String getSql4CreateSchema(String aSchemaName, String aPassword) {
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            return String.format(CREATE_SCHEMA_CLAUSE, aSchemaName);
        } else {
            throw new IllegalArgumentException("Schema name is null or empty.");
        }
    }

    @Override
    public String getSql4DropPkConstraint(String aSchemaName, PrimaryKeySpec aPk) {
        String constraintName = wrapNameIfRequired(aPk.getCName());
        String tableName = makeFullName(aSchemaName, aPk.getTable());
        return "alter table " + tableName + " drop constraint " + constraintName;
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
        }
        return null;
    }

    @Override
    public boolean isConstraintsDeferrable() {
        return false;
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
        return false;
    }

    private String prepareName(String aName) {
        return (isWrappedName(aName) ? unwrapName(aName) : aName);
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
