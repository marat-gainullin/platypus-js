package com.eas.sqldrivers;

import com.eas.client.ClientConstants;
import com.eas.client.changes.JdbcChangeValue;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.JdbcField;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.sqldrivers.resolvers.H2TypesResolver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vv
 */
public class H2SqlDriver extends SqlDriver {

    // настройка экранирования наименования объектов БД
    private static final TwinString[] charsForWrap = {new TwinString("\"", "\""), new TwinString("`", "`")};
    private static final char[] restrictedChars = {' ', ',', '\'', '"'};

    protected TypesResolver resolver = new H2TypesResolver();
    protected static final int[] h2ErrorCodes = {};
    protected static final String[] platypusErrorMessages = {};
    protected static final String SET_SCHEMA_CLAUSE = "SET SCHEMA %s";
    protected static final String GET_SCHEMA_CLAUSE = "SELECT SCHEMA()";
    protected static final String CREATE_SCHEMA_CLAUSE = "CREATE SCHEMA IF NOT EXISTS %s";
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

    @Override
    public boolean is(String aDialect) {
        return ClientConstants.SERVER_PROPERTY_H2_DIALECT.equals(aDialect);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConstraintsDeferrable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypesResolver getTypesResolver() {
        return resolver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsersSpaceInitResourceName() {
        return "/sqlscripts/H2InitUsersSpace.sql";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVersionInitResourceName() {
        return "/sqlscripts/H2InitVersion.sql";
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public String getSql4CreateSchema(String aSchemaName, String aPassword) {
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            return String.format(CREATE_SCHEMA_CLAUSE, aSchemaName);
        }
        throw new IllegalArgumentException("Schema name is null or empty.");
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public String getSql4DropIndex(String aSchemaName, String aTableName, String aIndexName) {
        String indexName = makeFullName(aSchemaName, aIndexName);
        return String.format(SQL_DROP_INDEX, indexName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSql4DropFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        String constraintName = wrapNameIfRequired(aFk.getCName());
        String tableName = makeFullName(aSchemaName, aFk.getTable());
        return String.format(SQL_DROP_FK, tableName, constraintName);
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public String getSql4DropPkConstraint(String aSchemaName, PrimaryKeySpec aPk) {
        String pkTableName = makeFullName(aSchemaName, aPk.getTable());
        return String.format(SQL_DROP_PK, pkTableName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        List<ForeignKeySpec> fkList = new ArrayList<>();
        fkList.add(aFk);
        return getSql4CreateFkConstraint(aSchemaName, fkList);
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public String getSql4EmptyTableCreation(String aSchemaName, String aTableName, String aPkFieldName) {
        String fullName = makeFullName(aSchemaName, aTableName);
        return String.format(SQL_CREATE_EMPTY_TABLE, fullName, wrapNameIfRequired(aPkFieldName));
    }

    /**
     * {@inheritDoc}
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

    private String getFieldTypeDefinition(JdbcField aField) {
        String typeDefine = "";
        String sqlTypeName = aField.getType().toLowerCase();
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
     * {@inheritDoc}
     */
    @Override
    public String getSql4FieldDefinition(JdbcField aField) {
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
     * {@inheritDoc}
     */
    @Override
    public String[] getSqls4ModifyingField(String aSchemaName, String aTableName, JdbcField aOldFieldMd, JdbcField aNewFieldMd) {
        assert aOldFieldMd.getName().toLowerCase().equals(aNewFieldMd.getName().toLowerCase());
        List<String> sql = new ArrayList<>();

        //Change data type
        String lOldTypeName = aOldFieldMd.getType();
        if (lOldTypeName == null) {
            lOldTypeName = "";
        }
        String lNewTypeName = aNewFieldMd.getType();
        if (lNewTypeName == null) {
            lNewTypeName = "";
        }

        String fullTableName = makeFullName(aSchemaName, aTableName);
        if (!lOldTypeName.equalsIgnoreCase(lNewTypeName)
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
     * {@inheritDoc}
     */
    @Override
    public String[] getSqls4RenamingField(String aSchemaName, String aTableName, String aOldFieldName, JdbcField aNewFieldMd) {
        String fullTableName = makeFullName(aSchemaName, aTableName);
        String renameSQL = String.format(SQL_RENAME_COLUMN, fullTableName, wrapNameIfRequired(aOldFieldName), wrapNameIfRequired(aNewFieldMd.getName()));
        return new String[]{renameSQL};
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
