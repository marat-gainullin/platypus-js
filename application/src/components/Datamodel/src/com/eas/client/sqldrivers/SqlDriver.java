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
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.settings.SettingsConstants;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public abstract class SqlDriver {

    protected class TwinString {

        private final String left;
        private final String right;

        TwinString(String aLeft, String aRight) {
            left = aLeft;
            right = aRight;
        }

        public String getLeft() {
            return left;
        }

        public String getRight() {
            return right;
        }
    }

    // error codes
    protected static final String EAS_TABLE_ALREADY_EXISTS = "EAS_TABLE_ALREADY_EXISTS";
    protected static final String EAS_TABLE_DOESNT_EXISTS = "EAS_TABLE_DOESNT_EXISTS";
    // misc
    protected static final String EAS_SQL_SCRIPT_DELIMITER = "#GO";
    public static final String DROP_FIELD_SQL_PREFIX = "alter table %s drop column ";
    public static final String ADD_FIELD_SQL_PREFIX = "alter table %s add ";

    public static final String PKEY_NAME_SUFFIX = "_pk";

    public SqlDriver() {
        super();
    }

    /**
     * Adds tables, foreign keys etc. to a database for in database users space
     *
     * @param aConnection
     * @throws Exception
     */
    public void initializeUsersSpace(Connection aConnection) throws Exception {
        if (!checkUsersSpaceInitialized(aConnection)) {
            String scriptText = readUsersSpaceInitScriptResource();
            Logger.getLogger(SqlDriver.class.getName()).log(Level.INFO, "About to initialize in-database users space.");
            applyScript(scriptText, aConnection);
        }
    }

    /**
     * Adds tables, foreign keys etc. to a database for database versioning
     *
     * @param aConnection
     * @throws Exception
     */
    public void initializeVersion(Connection aConnection) throws Exception {
        if (!checkVersionInitialized(aConnection)) {
            String scriptText = readVersionInitScriptResource();
            Logger.getLogger(SqlDriver.class.getName()).log(Level.INFO, "About to initialize database versioning.");
            applyScript(scriptText, aConnection);
        }
    }

    /**
     * *
     * The database supports deferrable constraints to enable constrains check
     * on transaction commit.
     *
     * @return true if constraints is deferrable
     */
    public abstract boolean isConstraintsDeferrable();

    /**
     * *
     * Gets type resolver to convert SQL types to JDBC types and vice-versa.
     *
     * @return TypesResolver instance
     */
    public abstract TypesResolver getTypesResolver();

    /**
     *
     * Gets in database users space initial script location and file name.
     *
     * @return
     */
    public abstract String getUsersSpaceInitResourceName();

    /**
     *
     * Gets database versioning initial script location and file name.
     *
     * @return
     */
    public abstract String getVersionInitResourceName();

    /**
     * Returns subset of jdbc types, supported by particular database. The trick
     * is that database uses own identifiers for it's types and we need an extra
     * abstraction level.
     *
     * @return Subset of jdbc types, supported by particular database.
     */
    public abstract Set<Integer> getSupportedJdbcDataTypes();

    /**
     * *
     * Sets current schema for current session.
     *
     * @param aConnection JDBC connection
     * @param aSchema Schema name
     * @throws Exception in the case of operation failure
     */
    public abstract void applyContextToConnection(Connection aConnection, String aSchema) throws Exception;

    /**
     * Gets current schema for connection
     *
     * @param aConnection JDBC connection
     * @return Schema name
     * @throws Exception in the case of operation failure
     */
    public String getConnectionContext(Connection aConnection) throws Exception {
        try (PreparedStatement stmt = aConnection.prepareStatement(getSql4GetConnectionContext())) {
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getString(1);
        }
    }

    public abstract String getSql4GetConnectionContext();

    /**
     * Returns sql query text, usable for enumerating tables in particular
     * schema
     *
     * @param schema4Sql Schema name. If this parameter is null sql for all
     * tables in all schemas will be returned.
     * @return Sql query text
     */
    public abstract String getSql4TablesEnumeration(String schema4Sql);

    /**
     * Returns sql text for retriving schemas list.
     *
     * @return Sql text.
     */
    public abstract String getSql4SchemasEnumeration();

    /**
     * Returns sql text for create new schema.
     *
     * @param aSchemaName schema name
     * @param aPassword owner password, required for some databases (Oracle)
     * @return Sql text.
     */
    public abstract String getSql4CreateSchema(String aSchemaName, String aPassword);

    /**
     * Returns sql query text for getting columns metadata for tables. TODO:
     * Implement result set fields description.
     *
     * @param aOwnerName Schema name
     * @param aTableNames Tables names set
     * @return
     */
    public abstract String getSql4TableColumns(String aOwnerName, Set<String> aTableNames);

    /**
     * *
     * Returns sql query text for getting primary keys metadata for tables.
     *
     * @param aOwnerName Schema name
     * @param aTableNames Tables names set
     * @return
     */
    public abstract String getSql4TablePrimaryKeys(String aOwnerName, Set<String> aTableNames);

    /**
     * *
     * Returns sql query text for getting foreign keys metadata for tables.
     *
     * @param aOwnerName Schema name
     * @param aTableNames Tables names set
     * @return
     */
    public abstract String getSql4TableForeignKeys(String aOwnerName, Set<String> aTableNames);

    /**
     * *
     * Returns sql query text for getting indexes metadata for tables.
     *
     * @param aOwnerName Schema name
     * @param aTableNames Tables names set
     * @return
     */
    public abstract String getSql4Indexes(String aOwnerName, Set<String> aTableNames);

    /**
     * *
     * Returns sql clause array to set column's comment. Eeach sql clause from
     * array executed consequentially
     *
     * @param aOwnerName Schema name
     * @param aTableName Table name
     * @param aFieldName Column name
     * @param aDescription Comment
     * @return Sql texts array
     */
    public abstract String[] getSql4CreateColumnComment(String aOwnerName, String aTableName, String aFieldName, String aDescription);

    /**
     * *
     * Returns sql clause to set table's comment.
     *
     * @param aOwnerName Schema name
     * @param aTableName Table name
     * @param aDescription Comment
     * @return Sql text
     */
    public abstract String getSql4CreateTableComment(String aOwnerName, String aTableName, String aDescription);

    /**
     * *
     * Gets sql clause for dropping the table.
     *
     * @param aSchemaName Schema name
     * @param aTableName Table name
     * @return sql text
     */
    public abstract String getSql4DropTable(String aSchemaName, String aTableName);

    /**
     * *
     * Gets sql clause for dropping the index on the table.
     *
     * @param aSchemaName Schema name
     * @param aTableName Table name
     * @param aIndexName Index name
     * @return sql text
     */
    public abstract String getSql4DropIndex(String aSchemaName, String aTableName, String aIndexName);

    /**
     * *
     * Gets sql clause for dropping the foreign key constraint.
     *
     * @param aSchemaName Schema name
     * @param aFk Foreign key specification object
     * @return Sql text
     */
    public abstract String getSql4DropFkConstraint(String aSchemaName, ForeignKeySpec aFk);

    /**
     * *
     * Gets sql clause for creating the primary key.
     *
     * @param aSchemaName Schema name
     * @param listPk Primary key columns specifications list
     * @return Sql text
     */
    public abstract String[] getSql4CreatePkConstraint(String aSchemaName, List<PrimaryKeySpec> listPk);

    /**
     * *
     * Gets sql clause for dropping the primary key.
     *
     * @param aSchemaName Schema name
     * @param aPk Primary key specification
     * @return Sql text
     */
    public abstract String getSql4DropPkConstraint(String aSchemaName, PrimaryKeySpec aPk);

    /**
     * *
     * Gets sql clause for creating the foreign key constraint.
     *
     * @param aSchemaName Schema name
     * @param aFk Foreign key specification
     * @return Sql text
     */
    public abstract String getSql4CreateFkConstraint(String aSchemaName, ForeignKeySpec aFk);

    /**
     * *
     * Gets sql clause for creating the foreign key constraint.
     *
     * @param aSchemaName Schema name
     * @param listFk Foreign key columns specifications list
     * @return Sql text
     */
    public abstract String getSql4CreateFkConstraint(String aSchemaName, List<ForeignKeySpec> listFk);

    /**
     * *
     * Gets sql clause for creating the index
     *
     * @param aSchemaName Schema name
     * @param aTableName Table name
     * @param aIndex Index specification
     * @return Sql text
     */
    public abstract String getSql4CreateIndex(String aSchemaName, String aTableName, DbTableIndexSpec aIndex);

    /**
     * *
     * Gets sql clause for creating an empty table.
     *
     * @param aSchemaName Schema name
     * @param aTableName Table name
     * @param aPkFieldName Column name for primary key
     * @return Sql text
     */
    public abstract String getSql4EmptyTableCreation(String aSchemaName, String aTableName, String aPkFieldName);

    /**
     * *
     * Gets specific exception message.
     *
     * @param ex Exception
     * @return Exception message
     */
    public abstract String parseException(Exception ex);

    /**
     * Generates Sql string fragment for field definition, according to specific
     * features of particular database. If it meets any strange type, such
     * java.sql.Types.OTHER or java.sql.Types.STRUCT, it uses the field's type
     * name.
     *
     * @param aField A field information to deal with.
     * @return Sql string for field definition
     */
    public abstract String getSql4FieldDefinition(Field aField);

    /**
     * Generates Sql string to modify a field, according to specific features of
     * particular database. If it meats any strange type, such
     * java.sql.Types.OTHER or java.sql.Types.STRUCT, it uses the field's type
     * name.
     *
     * @param aSchemaName Schema name
     * @param aTableName Name of the table with that field
     * @param aField A field information
     * @return Sql array string for field modification.
     */
    public abstract String[] getSqls4AddingField(String aSchemaName, String aTableName, Field aField);

    /**
     * Generates sql texts array for dropping a field. Sql clauses from array
     * will execute consequentially
     *
     * @param aSchemaName Schema name
     * @param aTableName Name of a table the field to dropped from.
     * @param aFieldName Field name to drop
     * @return Sql string generted.
     */
    public String[] getSql4DroppingField(String aSchemaName, String aTableName, String aFieldName) {
        String fullTableName = wrapNameIfRequired(aTableName);
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            fullTableName = wrapNameIfRequired(aSchemaName) + "." + fullTableName;
        }
        return new String[]{
            String.format(DROP_FIELD_SQL_PREFIX, fullTableName) + wrapNameIfRequired(aFieldName)
        };
    }

    /**
     * Generates Sql string to modify a field, according to specific features of
     * particular database. If it meats any strange type, such
     * java.sql.Types.OTHER or java.sql.Types.STRUCT, it uses the field's type
     * name.
     *
     * @param aSchemaName Schema name
     * @param aTableName Name of the table with that field
     * @param aOldFieldMd A field information to migrate from.
     * @param aNewFieldMd A field information to migrate to.
     * @return Sql array string for field modification.
     */
    public abstract String[] getSqls4ModifyingField(String aSchemaName, String aTableName, Field aOldFieldMd, Field aNewFieldMd);

    /**
     * *
     * Generates Sql string to rename a field, according to specific features of
     * particular database.
     *
     * @param aSchemaName Schema name
     * @param aTableName Table name
     * @param aOldFieldName Old column name
     * @param aNewFieldMd New field
     * @return Sql array string for field modification.
     */
    public abstract String[] getSqls4RenamingField(String aSchemaName, String aTableName, String aOldFieldName, Field aNewFieldMd);

    /**
     * Converts JDBC type to specific database type
     *
     * @param aLowLevelTypeName Specific database name
     * @return JDBC type
     */
    public abstract Integer getJdbcTypeByRDBMSTypename(String aLowLevelTypeName);

    public static void applyScript(String scriptText, Connection aConnection) throws Exception {
        String[] commandsTexts = scriptText.split(EAS_SQL_SCRIPT_DELIMITER);
        aConnection.setAutoCommit(false);
        if (commandsTexts != null) {
            try (Statement stmt = aConnection.createStatement()) {
                for (int i = 0; i < commandsTexts.length; i++) {
                    try {
                        String queryText = commandsTexts[i];
                        queryText = queryText.replace('\r', ' ');
                        queryText = queryText.replace('\n', ' ');
                        if (!queryText.isEmpty()) {
                            stmt.execute(queryText);
                            aConnection.commit();
                        }
                    } catch (Exception ex) {
                        aConnection.rollback();
                        Logger.getLogger(SqlDriver.class.getName()).log(Level.WARNING, "Error applying SQL script. {0}", ex.getMessage());
                    }
                }
            }
        }
    }

    private boolean checkUsersSpaceInitialized(Connection aConnection) {
        try {
            try (PreparedStatement stmt = aConnection.prepareStatement(String.format(SQLUtils.SQL_MAX_COMMON_BY_FIELD, ClientConstants.F_USR_NAME, ClientConstants.F_USR_NAME, ClientConstants.T_MTD_USERS))) {
                ResultSet res = stmt.executeQuery();
                res.close();
            }
            return true;
        } catch (SQLException ex) {
            try {
                aConnection.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(SqlDriver.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(SqlDriver.class.getName()).log(Level.WARNING, "In database users space seems to be uninitialized. {0}", ex.getMessage());
        }
        return false;
    }

    private boolean checkVersionInitialized(Connection aConnection) {
        try {
            try (PreparedStatement stmt = aConnection.prepareStatement(String.format(SQLUtils.SQL_MAX_COMMON_BY_FIELD, ClientConstants.F_VERSION_VALUE, ClientConstants.F_VERSION_VALUE, ClientConstants.T_MTD_VERSION))) {
                ResultSet res = stmt.executeQuery();
                res.close();
            }
            return true;
        } catch (SQLException ex) {
            try {
                aConnection.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(SqlDriver.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(SqlDriver.class.getName()).log(Level.WARNING, "Database vertioning seems to be uninitialized. {0}", ex.getMessage());
        }
        return false;
    }

    private String readUsersSpaceInitScriptResource() throws IOException {
        String resName = getUsersSpaceInitResourceName();
        return readScriptResource(resName);
    }

    private String readVersionInitScriptResource() throws IOException {
        String resName = getVersionInitResourceName();
        return readScriptResource(resName);
    }

    protected String readScriptResource(String resName) throws IOException {
        try (InputStream is = SqlDriver.class.getResourceAsStream(resName)) {
            byte[] data = new byte[is.available()];
            is.read(data);
            return new String(data, SettingsConstants.COMMON_ENCODING);
        }
    }

    public String makeFullName(String aSchemaName, String aName) {
        String name = wrapNameIfRequired(aName);
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            name = wrapNameIfRequired(aSchemaName) + "." + name;
        }
        return name;
    }

    protected String constructIn(Set<String> strings) {
        StringBuilder sb = new StringBuilder();
        String delimiter = "";
        for (String l : strings) {
            sb.append(delimiter).append("'").append(l.replaceAll("'", "''")).append("'");
            delimiter = ", ";
        }
        return sb.toString();
    }

    abstract public TwinString[] getCharsForWrap();

    abstract public char[] getRestrictedChars();

    abstract public boolean isHadWrapped(String aName);

    protected boolean isHaveLowerCase(String aValue) {
        if (aValue != null) {
            for (char c : aValue.toCharArray()) {
                if (Character.isLowerCase(c)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isHaveUpperCase(String aValue) {
        if (aValue != null) {
            for (char c : aValue.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 
     * Wrapping names containing restricted symbols.
     *
     * @param aName Name to wrap
     * @return Wrapped text
     */
    public String wrapNameIfRequired(String aName) {
        return wrapName(aName, isRequiredWrap(aName));
    }

    public String wrapName(String aName, boolean requiredOnly) {
        if (aName != null && !aName.isEmpty() && !isWrappedName(aName) && requiredOnly) {
            TwinString[] twinsWrap = getCharsForWrap();
            if (twinsWrap != null && twinsWrap.length > 0) {
                String wrapL = twinsWrap[0].getLeft();
                String wrapR = twinsWrap[0].getRight();
                StringBuilder sb = new StringBuilder();
                sb.append(wrapL);
                if (wrapL.length() == 1) {
                    if (wrapL.equals(wrapR)) {
                        sb.append(aName.replaceAll(wrapL, wrapL + wrapL));
                    } else {
                        sb.append(aName.replaceAll(wrapL, wrapL + wrapL).replaceAll(wrapR, wrapR + wrapR));
                    }
                } else {
                    sb.append(aName);
                }
                sb.append(wrapR);
                return sb.toString();
            }
        }
        return aName;
    }

    public String unwrapName(String aName) {
        int wrapLength = getWrapLength(aName);
        if (wrapLength > 0) {
            int length = aName.length();
            String left = aName.substring(0, wrapLength);
            String right = aName.substring(length - wrapLength);
            if (left.equals(right)) {
                return aName.substring(wrapLength, length - wrapLength).replaceAll(left + right, left);
            }
            return aName.substring(wrapLength, length - wrapLength);
        }
        return aName;
    }

    public boolean isWrappedName(String aName) {
        return getWrapLength(aName) > 0;
    }

    public int getWrapLength(String aName) {
        if (aName != null && !aName.isEmpty()) {
            TwinString[] twins = getCharsForWrap();

            if (twins != null) {
                for (TwinString twin : twins) {
                    String left = twin.getLeft();
                    String right = twin.getRight();
                    if (aName.startsWith(left) && aName.endsWith(right)) {
                        return left.length();
                    }
                }
            }
        }
        return 0;
    }

    public boolean isRequiredWrap(String aName) {
        if (aName != null && !aName.isEmpty()) {
            char[] restricted = getRestrictedChars();
            assert restricted != null;
            for (char c : aName.toCharArray()) {
                for (char rC : restricted) {
                    if (c == rC) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String generatePkName(String aTableName, String aSuffix) {
        int wrapLength = getWrapLength(aTableName);
        StringBuilder sb = new StringBuilder();
        sb.append(aTableName.substring(0, aTableName.length() - wrapLength));
        sb.append(aSuffix);
        sb.append(aTableName.substring(aTableName.length() - wrapLength));
        return sb.toString();
    }
}
