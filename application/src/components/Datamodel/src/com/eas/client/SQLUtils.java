/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client;

import com.eas.client.dataflow.Converter;
import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.sqldrivers.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.RowSetMetaDataImpl;

/**
 *
 * @author mg
 */
public class SQLUtils {

    public static final String TABLE_NAME_2_SQL = "select * from %s";
    public static final String PARAMETER_NAME_REGEXP = ":{1}([A-za-z]\\w*\\b)";
    public static final String PROPERTIES_VALUE_REGEXP = "={1}([A-za-z]\\w+\\b)";
    public static final String SQL_SELECT_COMMON_WHERE_BY_FIELD = "select * from %s where %s.%s = :%s";
    public static final String SQL_SELECT_COMMON_WHERE_ISNULL_FIELD = "select * from %s where %s.%s is null";
    public static final String SQL_PARAMETER_FIELD_VALUE = "fieldValue";
    public static final String SQL_UPDATE_COMMON_WHERE_BY_FIELD = "update %s set %s = %s where %s.%s = :" + SQL_PARAMETER_FIELD_VALUE;
    public static final String SQL_UPDATE2_COMMON_WHERE_BY_FIELD = "update %s set %s = %s, %s = %s where %s.%s = :" + SQL_PARAMETER_FIELD_VALUE;
    public static final String SQL_UPDATE3_COMMON_WHERE_BY_FIELD = "update %s set %s = %s, %s = %s, %s = %s where %s.%s = :" + SQL_PARAMETER_FIELD_VALUE;
    public static final String SQL_UPDATE4_COMMON_WHERE_BY_FIELD = "update %s set %s = %s, %s = %s, %s = %s, %s = %s where %s.%s = :" + SQL_PARAMETER_FIELD_VALUE;
    public static final String SQL_DELETE_COMMON_WHERE_BY_FIELD = "delete from %s where %s.%s = :" + SQL_PARAMETER_FIELD_VALUE;
    public static final String SQL_INSERT_COMMON_ID_FIELD = "insert into %s columns = (%s) values = ( :" + SQL_PARAMETER_FIELD_VALUE + ")";
    public static final String SQL_MAX_COMMON_BY_FIELD = "select max(%s) %s from %s";
    protected static final OracleSqlDriver easOraDriver = new OracleSqlDriver();
    protected static final MsSqlSqlDriver easMsSqlDriver = new MsSqlSqlDriver();
    protected static final PostgreSqlDriver easPostgreSqlDriver = new PostgreSqlDriver();
    protected static final MySqlSqlDriver easMySqlSqlDriver = new MySqlSqlDriver();
    protected static final Db2SqlDriver easDb2Driver = new Db2SqlDriver();
    protected static final H2SqlDriver easH2Driver = new H2SqlDriver();

    public static ResultSetMetaData cloneResultSetMetadata(ResultSetMetaData md) {
        if (md != null) {
            try {
                RowSetMetaDataImpl lmd = new RowSetMetaDataImpl();
                lmd.setColumnCount(md.getColumnCount());
                for (int columnIndex = 1; columnIndex <= md.getColumnCount(); columnIndex++) {
                    lmd.setAutoIncrement(columnIndex, md.isAutoIncrement(columnIndex));
                    lmd.setCaseSensitive(columnIndex, md.isCaseSensitive(columnIndex));
                    lmd.setSearchable(columnIndex, md.isSearchable(columnIndex));
                    lmd.setCurrency(columnIndex, md.isCurrency(columnIndex));
                    lmd.setNullable(columnIndex, md.isNullable(columnIndex));
                    lmd.setSigned(columnIndex, md.isSigned(columnIndex));
                    lmd.setColumnDisplaySize(columnIndex, md.getColumnDisplaySize(columnIndex));

                    String lSourceString = md.getColumnLabel(columnIndex);
                    if (lSourceString == null) {
                        lSourceString = "";
                    }
                    lmd.setColumnLabel(columnIndex, new String(lSourceString.toCharArray()));

                    lSourceString = md.getColumnName(columnIndex);
                    if (lSourceString == null) {
                        lSourceString = "";
                    }
                    lmd.setColumnName(columnIndex, new String(lSourceString.toCharArray()));

                    lSourceString = md.getSchemaName(columnIndex);
                    if (lSourceString == null) {
                        lSourceString = "";
                    }
                    lmd.setSchemaName(columnIndex, new String(lSourceString.toCharArray()));

                    lmd.setPrecision(columnIndex, md.getPrecision(columnIndex));
                    lmd.setScale(columnIndex, md.getScale(columnIndex));

                    lSourceString = md.getTableName(columnIndex);
                    if (lSourceString == null) {
                        lSourceString = "";
                    }
                    lmd.setTableName(columnIndex, new String(lSourceString.toCharArray()));

                    lSourceString = md.getCatalogName(columnIndex);
                    if (lSourceString == null) {
                        lSourceString = "";
                    }
                    lmd.setCatalogName(columnIndex, new String(lSourceString.toCharArray()));

                    lmd.setColumnType(columnIndex, md.getColumnType(columnIndex));

                    lSourceString = md.getColumnTypeName(columnIndex);
                    if (lSourceString == null) {
                        lSourceString = "";
                    }
                    lmd.setColumnTypeName(columnIndex, new String(lSourceString.toCharArray()));
                }
                return lmd;
            } catch (SQLException ex) {
                Logger.getLogger(SQLUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public static Object generateBooleanValue4Type(int colType, Boolean aValue) {
        switch (colType) {
            case java.sql.Types.TINYINT:
            case java.sql.Types.SMALLINT:
            case java.sql.Types.INTEGER:
            case java.sql.Types.BIGINT:
            case java.sql.Types.FLOAT:
            case java.sql.Types.REAL:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.NUMERIC:
            case java.sql.Types.DECIMAL:
                return aValue ? 1 : 0;
            case java.sql.Types.CHAR:
            case java.sql.Types.VARCHAR:
            case java.sql.Types.LONGVARCHAR:
                return aValue ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
            case java.sql.Types.DATE:
            case java.sql.Types.TIME:
            case java.sql.Types.TIMESTAMP:
                return aValue ? new Date(1) : new Date(0);
            case java.sql.Types.BIT:
            case java.sql.Types.BOOLEAN:
                return aValue;
            case java.sql.Types.BINARY:
            case java.sql.Types.VARBINARY:
            case java.sql.Types.LONGVARBINARY:
            case java.sql.Types.NULL:
            case java.sql.Types.OTHER:
            case java.sql.Types.JAVA_OBJECT:
            case java.sql.Types.DISTINCT:
            case java.sql.Types.STRUCT:
            case java.sql.Types.ARRAY:
            case java.sql.Types.BLOB:
            case java.sql.Types.CLOB:
            case java.sql.Types.REF:
            case java.sql.Types.DATALINK:
            case java.sql.Types.ROWID:
            case java.sql.Types.SQLXML:
            case java.sql.Types.NCHAR:
            case java.sql.Types.NVARCHAR:
            case java.sql.Types.LONGNVARCHAR:
            case java.sql.Types.NCLOB:
                break;
        }
        assert false;
        return null;
    }

    public static String convertJDBCObject2String(Object aObj) {
        if (aObj != null) {
            if (aObj instanceof String) {
                return (String) aObj;
            } else if (aObj instanceof Number) {
                if (aObj instanceof Integer) {
                    return String.valueOf((Integer) aObj);
                } else if (aObj instanceof Long) {
                    return String.valueOf((Long) aObj);
                } else if (aObj instanceof Byte) {
                    return String.valueOf((Byte) aObj);
                } else if (aObj instanceof Short) {
                    return String.valueOf((Short) aObj);
                } else if (aObj instanceof Float) {
                    return String.valueOf((Float) aObj);
                } else if (aObj instanceof Double) {
                    return String.valueOf((Double) aObj);
                } else if (aObj instanceof BigDecimal) {
                    BigDecimal bd = (BigDecimal) aObj;
                    if (bd.precision() - bd.scale() > 0) {
                        return String.valueOf(bd.longValue());
                    } else {
                        return String.valueOf(bd.doubleValue());
                    }
                } else if (aObj instanceof BigInteger) {
                    BigInteger bi = (BigInteger) aObj;
                    return String.valueOf(bi.longValue());
                }
            } else if (aObj instanceof Clob) {
                return clob2String((Clob) aObj);
            } else {
                return aObj.toString();
            }
        }
        return null;
    }

    public static String dialectByUrl(String aJdbcUrl) {
        String dialect = null;
        if (aJdbcUrl != null) {
            aJdbcUrl = aJdbcUrl.toLowerCase();
            if (aJdbcUrl.contains("jdbc:oracle")) { //NOI18N
                dialect = ClientConstants.SERVER_PROPERTY_ORACLE_DIALECT;
            } else if (aJdbcUrl.contains("jdbc:jtds:sqlserver")) { //NOI18N
                dialect = ClientConstants.SERVER_PROPERTY_MSSQL_DIALECT;
            } else if (aJdbcUrl.contains("jdbc:postgre")) { //NOI18N
                dialect = ClientConstants.SERVER_PROPERTY_POSTGRE_DIALECT;
            } else if (aJdbcUrl.contains("jdbc:db2")) { //NOI18N
                dialect = ClientConstants.SERVER_PROPERTY_DB2_DIALECT;
            } else if (aJdbcUrl.contains("jdbc:mysql")) { //NOI18N
                dialect = ClientConstants.SERVER_PROPERTY_MYSQL_DIALECT;
            } else if (aJdbcUrl.contains("jdbc:h2")) { //NOI18N
                dialect = ClientConstants.SERVER_PROPERTY_H2_DIALECT;
            }
        }
        return dialect;
    }

    public static String dialectByProductName(String aName) {
        String dialect = null;
        if (aName != null) {
            aName = aName.toLowerCase();
            if (aName.contains("oracle")) { //NOI18N
                dialect = ClientConstants.SERVER_PROPERTY_ORACLE_DIALECT;
            } else if (aName.contains("microsoft")) { //NOI18N
                dialect = ClientConstants.SERVER_PROPERTY_MSSQL_DIALECT;
            } else if (aName.contains("postgre")) { //NOI18N
                dialect = ClientConstants.SERVER_PROPERTY_POSTGRE_DIALECT;
            } else if (aName.contains("db2")) { //NOI18N
                dialect = ClientConstants.SERVER_PROPERTY_DB2_DIALECT;
            } else if (aName.contains("mysql")) { //NOI18N
                dialect = ClientConstants.SERVER_PROPERTY_MYSQL_DIALECT;
            } else if (aName.contains("h2")) { //NOI18N
                dialect = ClientConstants.SERVER_PROPERTY_H2_DIALECT;
            }
        }
        return dialect;
    }

    public static SqlDriver getSqlDriver(String aDialect) {
        if (aDialect != null) {
            if (ClientConstants.SERVER_PROPERTY_ORACLE_DIALECT.equalsIgnoreCase(aDialect)) {
                return easOraDriver;
            } else if (ClientConstants.SERVER_PROPERTY_MSSQL_DIALECT.equalsIgnoreCase(aDialect)) {
                return easMsSqlDriver;
            } else if (ClientConstants.SERVER_PROPERTY_POSTGRE_DIALECT.equalsIgnoreCase(aDialect)) {
                return easPostgreSqlDriver;
            } else if (ClientConstants.SERVER_PROPERTY_MYSQL_DIALECT.equalsIgnoreCase(aDialect)) {
                return easMySqlSqlDriver;
            } else if (ClientConstants.SERVER_PROPERTY_DB2_DIALECT.equalsIgnoreCase(aDialect)) {
                return easDb2Driver;
            } else if (ClientConstants.SERVER_PROPERTY_H2_DIALECT.equalsIgnoreCase(aDialect)) {
                return easH2Driver;
            }
            //else if (connectionString.indexOf("Derby") != -1)
            //...
        }
        return null;
    }

    public static SqlQuery validateTableSqlQuery(String aTableDbId, String tableName, String tableSchemaName, DatabasesClient aClient) throws Exception {
        return validateTableSqlQuery(aTableDbId, tableName, tableSchemaName, aClient, true);
    }

    public static SqlQuery validateTableSqlQuery(String aTableDatasourceName, String aTableName, String aTableSchemaName, DatabasesClient aClient, boolean forceMdQuery) throws Exception {
        String fullTableName = aTableName;
        if (aTableSchemaName != null && !aTableSchemaName.isEmpty()) {
            fullTableName = aTableSchemaName + "." + fullTableName;
        }
        SqlQuery query = new SqlQuery(aClient, aTableDatasourceName, SQLUtils.makeQueryByTableName(fullTableName));
        DatabaseMdCache mdCache = aClient.getDbMetadataCache(aTableDatasourceName);
        if (mdCache != null) {
            Fields tableFields = forceMdQuery || mdCache.containsTableMetadata(fullTableName) ? mdCache.getTableMetadata(fullTableName) : null;
            if (tableFields != null) {
                query.setFields(tableFields);
            } else {
                throw new Exception("Table " + fullTableName + " doesn't exist. Datasource: " + aTableDatasourceName);
            }
        }
        return query;
    }

    public static String extractSchemaName(String aName) {
        int indexOfDot = aName.indexOf('.');
        if (indexOfDot != -1) {
            return aName.substring(0, indexOfDot);
        }
        return "";
    }

    public static String extractTableName(String aName) {
        int indexOfDot = aName.indexOf('.');
        if (indexOfDot != -1) {
            return aName.substring(indexOfDot + 1);
        }
        return aName;
    }

    public static String getLocalizedPkName() {
        return DbLocalizations.getString("PRIMARY_KEY");
    }

    public static String getLocalizedFkName() {
        return DbLocalizations.getString("FOREIGN_KEY");
    }

    public static boolean isKeysCompatible(Field aField1, Field aField2) {
        if (aField1 != null && aField2 != null) {
            PrimaryKeySpec[] lKeys = new PrimaryKeySpec[2];
            int lKeysCount = 0;
            if (aField1.isPk()) {
                String lSchema = null;//aField1.getSchemaName();
                if (lSchema != null) {
                    lSchema = lSchema.toLowerCase();
                }
                String lTable = aField1.getTableName();
                if (lTable != null) {
                    lTable = lTable.toLowerCase();
                }
                String lField = aField1.getName();
                if (lField != null) {
                    lField = lField.toLowerCase();
                }
                lKeys[lKeysCount] = new PrimaryKeySpec(lSchema, lTable, lField, null);
                lKeysCount++;
            }
            if (aField1.isFk()) {
                PrimaryKeySpec lfoundFk = aField1.getFk().getReferee();
                String lSchema = null;//lfoundFk.getSchema();
                if (lSchema != null) {
                    lSchema = lSchema.toLowerCase();
                }
                String lTable = lfoundFk.getTable();
                if (lTable != null) {
                    lTable = lTable.toLowerCase();
                }
                String lField = lfoundFk.getField();
                if (lField != null) {
                    lField = lField.toLowerCase();
                }
                lKeys[lKeysCount] = new PrimaryKeySpec(lSchema, lTable, lField, null);
                lKeysCount++;
            }

            PrimaryKeySpec[] rKeys = new PrimaryKeySpec[2];
            int rKeysCount = 0;
            if (aField2.isPk()) {
                String lSchema = null;//aField2.getSchemaName();
                if (lSchema != null) {
                    lSchema = lSchema.toLowerCase();
                }
                String lTable = aField2.getTableName();
                if (lTable != null) {
                    lTable = lTable.toLowerCase();
                }
                String lField = aField2.getName();
                if (lField != null) {
                    lField = lField.toLowerCase();
                }
                rKeys[rKeysCount] = new PrimaryKeySpec(lSchema, lTable, lField, null);
                rKeysCount++;
            }
            if (aField2.isFk()) {
                PrimaryKeySpec lfoundFk = aField2.getFk().getReferee();
                String lSchema = null;//lfoundFk.getSchema();
                if (lSchema != null) {
                    lSchema = lSchema.toLowerCase();
                }
                String lTable = lfoundFk.getTable();
                if (lTable != null) {
                    lTable = lTable.toLowerCase();
                }
                String lField = lfoundFk.getField();
                if (lField != null) {
                    lField = lField.toLowerCase();
                }
                rKeys[rKeysCount] = new PrimaryKeySpec(lSchema, lTable, lField, null);
                rKeysCount++;
            }

            for (int i = 0; i < lKeysCount; i++) {
                for (int j = 0; j < rKeysCount; j++) {
                    if (lKeys[i].equals(rKeys[j])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String makeQueryByTableName(String aTableName) {
        return String.format(TABLE_NAME_2_SQL, aTableName);
    }

    public static String makeTableNameMetadataQuery(String aTableName) {
        return makeQueryMetadataQuery(makeQueryByTableName(aTableName));
    }
    
    public static final String SQL_FALSE_CONDITION = " where 1=0";
    public static final String SQL_2_METADTA_TAIL = "t01010101" + SQL_FALSE_CONDITION;
    public static final String SQL_2_METADTA = "select * from ( %s ) " + SQL_2_METADTA_TAIL;
    
    public static String makeQueryMetadataQuery(String sql) {
        if (sql != null && !sql.isEmpty()) {
            if (!sql.endsWith(SQL_2_METADTA_TAIL)) {
                String lsql = sql.toLowerCase().replaceAll("[\n\r]", "");
                if (lsql.matches(".+\\bwhere\\b.+")
                        // complex queries
                        || lsql.matches(".+\\border\\b.+")
                        || lsql.matches(".+\\bgroup\\b.+")
                        || lsql.matches(".+\\bconnect\\b.+")) {
                    return String.format(SQL_2_METADTA, sql);
                } else // simple queries
                {
                    return sql + SQL_FALSE_CONDITION;
                }
            } else {// bypass
                return sql;
            }
        }
        return "";
    }

    /*
    public static Object cloneFieldValue(Object aValue) {
        return RowsetUtils.cloneFieldValue(aValue);
    }
    */

    public static Long extractLongFromJDBCObject(Object aValue) {
        Long lRetValue = null;
        if (aValue instanceof Long) {
            lRetValue = (Long) aValue;
        } else if (aValue instanceof BigDecimal) {
            lRetValue = ((BigDecimal) aValue).longValue();
        } else if (aValue instanceof BigInteger) {
            lRetValue = ((BigInteger) aValue).longValue();
        } else if (aValue instanceof Integer) {
            lRetValue = ((Integer) aValue).longValue();
        } else if (aValue instanceof Double) {
            lRetValue = ((Double) aValue).longValue();
        } else if (aValue instanceof Float) {
            lRetValue = ((Float) aValue).longValue();
        } else if (aValue instanceof Byte) {
            lRetValue = ((Byte) aValue).longValue();
        } else if (aValue instanceof Short) {
            lRetValue = ((Short) aValue).longValue();
        }
        return lRetValue;
    }

    public static Integer extractIntegerFromJDBCObject(Object aValue) {
        Integer lRetValue = null;
        if (aValue instanceof Long) {
            lRetValue = ((Long) aValue).intValue();
        } else if (aValue instanceof BigDecimal) {
            lRetValue = ((BigDecimal) aValue).intValue();
        } else if (aValue instanceof BigInteger) {
            lRetValue = ((BigInteger) aValue).intValue();
        } else if (aValue instanceof Integer) {
            lRetValue = (Integer) aValue;
        } else if (aValue instanceof Double) {
            lRetValue = ((Double) aValue).intValue();
        } else if (aValue instanceof Float) {
            lRetValue = ((Float) aValue).intValue();
        } else if (aValue instanceof Byte) {
            lRetValue = ((Byte) aValue).intValue();
        } else if (aValue instanceof Short) {
            lRetValue = ((Short) aValue).intValue();
        }
        return lRetValue;
    }

    public static Double extractDoubleFromJDBCObject(Object aValue) {
        Double lRetValue = null;
        if (aValue instanceof Long) {
            lRetValue = ((Long) aValue).doubleValue();
        } else if (aValue instanceof BigDecimal) {
            lRetValue = ((BigDecimal) aValue).doubleValue();
        } else if (aValue instanceof BigInteger) {
            lRetValue = ((BigInteger) aValue).doubleValue();
        } else if (aValue instanceof Integer) {
            lRetValue = ((Integer) aValue).doubleValue();
        } else if (aValue instanceof Double) {
            lRetValue = (Double) aValue;
        } else if (aValue instanceof Float) {
            lRetValue = ((Float) aValue).doubleValue();
        } else if (aValue instanceof Byte) {
            lRetValue = ((Byte) aValue).doubleValue();
        } else if (aValue instanceof Short) {
            lRetValue = ((Short) aValue).doubleValue();
        }
        return lRetValue;
    }

    public static boolean smartEquals(Object aNewValue, Object aOldValue) {
        if (aNewValue != null) {
            if (aNewValue instanceof Number && aOldValue instanceof Number) {
                BigDecimal bd1 = Converter.number2BigDecimal((Number) aNewValue);
                BigDecimal bd2 = Converter.number2BigDecimal((Number) aOldValue);
                return bd1.equals(bd2);
            } else {
                return aNewValue.equals(aOldValue);
            }
        } else {
            return aOldValue == null;
        }
    }

    public static boolean isJdbcEqual(Object aValue, Object other) {
        if (aValue != null && other == null) {
            return false;
        }
        if (aValue == null && other != null) {
            return false;
        }
        if (aValue == null && other == null) {
            return true;
        }
        if (aValue != null && other != null) {
            if (aValue.getClass() != other.getClass()) {
                return false;
            }
            if (aValue instanceof BigDecimal) {
                BigDecimal casted = (BigDecimal) aValue;
                BigDecimal otherValue = (BigDecimal) other;
                return casted.equals(otherValue);
            } else if (aValue instanceof BigInteger) {
                BigInteger casted = (BigInteger) aValue;
                BigInteger otherValue = (BigInteger) other;
                return casted.equals(otherValue);
            } else if (aValue instanceof Boolean) {
                Boolean casted = (Boolean) aValue;
                Boolean otherValue = (Boolean) other;
                return casted.equals(otherValue);
            } else if (aValue instanceof Byte) {
                Byte casted = (Byte) aValue;
                Byte otherValue = (Byte) other;
                return casted.equals(otherValue);
            } else if (aValue instanceof Double) {
                Double casted = (Double) aValue;
                Double otherValue = (Double) other;
                return casted.equals(otherValue);
            } else if (aValue instanceof Float) {
                Float casted = (Float) aValue;
                Float otherValue = (Float) other;
                return casted.equals(otherValue);
            } else if (aValue instanceof Integer) {
                Integer casted = (Integer) aValue;
                Integer otherValue = (Integer) other;
                return casted.equals(otherValue);
            } else if (aValue instanceof Long) {
                Long casted = (Long) aValue;
                Long otherValue = (Long) other;
                return casted.equals(otherValue);
            } else if (aValue instanceof Short) {
                Short casted = (Short) aValue;
                Short otherValue = (Short) other;
                return casted.equals(otherValue);
            } else if (aValue instanceof Time) {
                Time casted = (Time) aValue;
                Time otherValue = (Time) other;
                return casted.equals(otherValue);
            } else if (aValue instanceof Timestamp) {
                Timestamp casted = (Timestamp) aValue;
                Timestamp otherValue = (Timestamp) other;
                return casted.equals(otherValue);
            } else if (aValue instanceof java.util.Date) {
                java.util.Date casted = (java.util.Date) aValue;
                java.util.Date otherValue = (java.util.Date) other;
                return casted.equals(otherValue);
            } else if (aValue instanceof String) {
                String casted = (String) aValue;
                String otherValue = (String) other;
                return casted.equals(otherValue);
            } else if (aValue instanceof URL) {
                URL casted = (URL) aValue;
                String urlValue = casted.toString();

                URL newValue = (URL) other;
                String urlOtherValue = newValue.toString();
                if (urlValue != null && urlOtherValue == null) {
                    return false;
                }
                if (urlValue == null && urlOtherValue != null) {
                    return false;
                }
                return urlValue.equals(urlOtherValue);
            } else if (aValue instanceof Array) {
                return false;
            } else if (aValue instanceof Blob) {
                return false;
            } else if (aValue instanceof Clob) {
                return false;
            } else //            if(aValue instanceof NClob) NClob - is the Clob!!!
            //            {
            //                NClob casted = (NClob)aValue;
            //                NClob newValue = new SerialNClob();
            //            }else
            if (aValue instanceof SQLXML) {
                try {
                    SQLXML casted = (SQLXML) aValue;
                    String xmlValue = casted.getString();

                    SQLXML newValue = (SQLXML) other;
                    String xmlOtherValue = newValue.getString();
                    if (xmlValue != null && xmlOtherValue == null) {
                        return false;
                    }
                    if (xmlValue == null && xmlOtherValue != null) {
                        return false;
                    }
                    return xmlValue.equals(xmlOtherValue);
                } catch (SQLException ex) {
                    Logger.getLogger(SQLUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        return aValue.equals(other);
    }

    public static void processFieldsPreClient(Fields aFields) {
        for (Field field : aFields.toCollection()) {
            field.setTableName(null);
            field.setSchemaName(null);
            if (field.isFk()) {
                field.getFk().setCName(null);
                field.getFk().setField(null);
                field.getFk().setTable(null);
                field.getFk().setSchema(null);
            }
        }
    }
    public static final Map<Integer, TypesGroup> typesCompatible = new HashMap<>();

    public enum TypesGroup {

        NUMBERS,
        LOGICAL,
        BLOB,
        LOBS,
        BINARIES,
        STRINGS,
        DATES,
        USER_DEFINED,
        UNSUPPORTED;

        /**
         * This method should be deleted when all application runtime will
         * migrate to TypesGroup types
         *
         * @return
         */
        public int toJdbcAnalog() {
            if (this == NUMBERS) {
                return Types.NUMERIC;
            } else if (this == LOGICAL) {
                return Types.BOOLEAN;
            } else if (this == BLOB) {
                return Types.BLOB;
            } else if (this == LOBS) {
                return Types.CLOB;
            } else if (this == BINARIES) {
                return Types.VARBINARY;
            } else if (this == STRINGS) {
                return Types.VARCHAR;
            } else if (this == DATES) {
                return Types.TIMESTAMP;
            } else {
                return Types.OTHER;
            }
        }
    }
    public static final ResourceBundle DbLocalizations = ResourceBundle.getBundle("com/eas/client/DbLocalizations");

    static {
        // Unknown. Not working with
        typesCompatible.put(java.sql.Types.ARRAY, TypesGroup.UNSUPPORTED);
        typesCompatible.put(java.sql.Types.DATALINK, TypesGroup.UNSUPPORTED);
        typesCompatible.put(java.sql.Types.JAVA_OBJECT, TypesGroup.UNSUPPORTED);
        typesCompatible.put(java.sql.Types.REF, TypesGroup.UNSUPPORTED);
        typesCompatible.put(java.sql.Types.OTHER, TypesGroup.UNSUPPORTED);
        typesCompatible.put(java.sql.Types.DISTINCT, TypesGroup.UNSUPPORTED);
        typesCompatible.put(java.sql.Types.NULL, TypesGroup.UNSUPPORTED);
        typesCompatible.put(java.sql.Types.ROWID, TypesGroup.UNSUPPORTED);
        // Aggregated types - those which consist of values of other types.
        typesCompatible.put(java.sql.Types.STRUCT, TypesGroup.USER_DEFINED);
        // Numbers
        typesCompatible.put(java.sql.Types.BIGINT, TypesGroup.NUMBERS);
        typesCompatible.put(java.sql.Types.DECIMAL, TypesGroup.NUMBERS);
        typesCompatible.put(java.sql.Types.DOUBLE, TypesGroup.NUMBERS);
        typesCompatible.put(java.sql.Types.FLOAT, TypesGroup.NUMBERS);
        typesCompatible.put(java.sql.Types.INTEGER, TypesGroup.NUMBERS);
        typesCompatible.put(java.sql.Types.NUMERIC, TypesGroup.NUMBERS);
        typesCompatible.put(java.sql.Types.REAL, TypesGroup.NUMBERS);
        typesCompatible.put(java.sql.Types.SMALLINT, TypesGroup.NUMBERS);
        typesCompatible.put(java.sql.Types.TINYINT, TypesGroup.NUMBERS);
        // Logical
        typesCompatible.put(java.sql.Types.BOOLEAN, TypesGroup.LOGICAL);
        typesCompatible.put(java.sql.Types.BIT, TypesGroup.LOGICAL);
        // Binaries
        typesCompatible.put(java.sql.Types.VARBINARY, TypesGroup.BINARIES);
        typesCompatible.put(java.sql.Types.BINARY, TypesGroup.BINARIES);
        typesCompatible.put(java.sql.Types.LONGVARBINARY, TypesGroup.BINARIES);
        // Lobs
        typesCompatible.put(java.sql.Types.BLOB, TypesGroup.BLOB);
        typesCompatible.put(java.sql.Types.CLOB, TypesGroup.LOBS);
        typesCompatible.put(java.sql.Types.NCLOB, TypesGroup.LOBS);
        // Strings
        typesCompatible.put(java.sql.Types.CHAR, TypesGroup.STRINGS);
        typesCompatible.put(java.sql.Types.LONGNVARCHAR, TypesGroup.STRINGS);
        typesCompatible.put(java.sql.Types.LONGVARCHAR, TypesGroup.STRINGS);
        typesCompatible.put(java.sql.Types.NCHAR, TypesGroup.STRINGS);
        typesCompatible.put(java.sql.Types.NVARCHAR, TypesGroup.STRINGS);
        typesCompatible.put(java.sql.Types.VARCHAR, TypesGroup.STRINGS);
        typesCompatible.put(java.sql.Types.SQLXML, TypesGroup.STRINGS);
        // Dates, times
        typesCompatible.put(java.sql.Types.DATE, TypesGroup.DATES);
        typesCompatible.put(java.sql.Types.TIME, TypesGroup.DATES);
        typesCompatible.put(java.sql.Types.TIMESTAMP, TypesGroup.DATES);
    }

    public static boolean isTypeSupported(int aType) {
        return typesCompatible.get(aType) != TypesGroup.UNSUPPORTED;
    }

    public static TypesGroup getTypeGroup(int aType) {
        return typesCompatible.get(aType);
    }

    public static boolean isSimpleTypesCompatible(int leftType, int rightType) {
        TypesGroup leftTypeGroup = typesCompatible.get(leftType);
        TypesGroup rightTypeGroup = typesCompatible.get(rightType);
        if (leftTypeGroup != null && rightTypeGroup != null) {
            return (leftTypeGroup == rightTypeGroup
                    || // Dates to Strings are allowed
                    (leftTypeGroup == TypesGroup.DATES && rightTypeGroup == TypesGroup.STRINGS)
                    || // Numbers to Strings are allowed
                    (leftTypeGroup == TypesGroup.NUMBERS && rightTypeGroup == TypesGroup.STRINGS)
                    || // Numbers to Booleans are allowed
                    (leftTypeGroup == TypesGroup.NUMBERS && rightTypeGroup == TypesGroup.LOGICAL));
        }
        return false;
    }

    /*
    public static boolean isTypeCompatible2JavaClass(int jdbcType, Class<?> aClass) {
        return RowsetUtils.isTypeCompatible2JavaClass(jdbcType, aClass);
    }
    */

    public static String getLocalizedTypeName(int type) {
        return getLocalizedTypeName(DataTypeInfo.getTypeName(type));
    }

    public static String getLocalizedTypeName(String aTypeName) {
        try {
            if (aTypeName != null) {
                return DbLocalizations.getString(aTypeName);
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    public static String clob2String(Clob source) {
        if (source != null) {
            try {
                return source.getSubString(1, (int) source.length());
            } catch (SQLException sqlEx) {
                Logger.getLogger(SQLUtils.class.getName()).log(Level.SEVERE, null, sqlEx);
            }
        }
        return null;
    }

    public static SqlQuery constructQueryByTableName(DatabasesClient aClient, String aTableName) {
        if (aTableName != null) {
            SqlQuery query = new SqlQuery(aClient, SQLUtils.makeQueryByTableName(aTableName));
            return query;
        }
        return null;
    }

    public static String sqlObject2stringRepresentation(int sqlType, Object value) throws SQLException {
        String result;
        switch (sqlType) {
            case java.sql.Types.DATE:
                if (value instanceof java.util.Date) {
                    java.sql.Date sqlDate = new java.sql.Date(((java.util.Date) value).getTime());
                    result = sqlDate.toString();
                } else {
                    result = value.toString();
                }
                break;
            case java.sql.Types.TIME:
                if (value instanceof java.util.Date) {
                    java.sql.Time sqlTime = new java.sql.Time(((java.util.Date) value).getTime());
                    result = sqlTime.toString();
                } else {
                    result = value.toString();
                }
                break;
            case java.sql.Types.TIMESTAMP:
                if (value instanceof java.util.Date) {
                    java.sql.Timestamp timestamp = new java.sql.Timestamp(((java.util.Date) value).getTime());
                    result = timestamp.toString();
                } else {
                    result = value.toString();
                }
                break;
            case java.sql.Types.CLOB:
                if (value instanceof Clob) {
                    result = ((Clob) value).getSubString(1, (int) ((Clob) value).length());
                } else {
                    result = value.toString();
                }
                break;
            default:
                result = value.toString();
                break;
        }
        return result;
    }

    public static int sqlType4Object(Object obj) throws SQLException {
        if (obj instanceof String) {
            return Types.VARCHAR;
        } else if (obj instanceof java.math.BigDecimal) {
            return Types.NUMERIC;
        } else if (obj instanceof Boolean) {
            return Types.BOOLEAN;
        } else if (obj instanceof Integer) {
            return Types.INTEGER;
        } else if (obj instanceof Long) {
            return Types.BIGINT;
        } else if (obj instanceof Float) {
            return Types.REAL;
        } else if (obj instanceof Double) {
            return Types.DOUBLE;
        } else if (obj instanceof byte[]) {
            return Types.BINARY;
        } else if (obj instanceof java.sql.Date) {
            return Types.DATE;
        } else if (obj instanceof java.sql.Time) {
            return Types.TIME;
        } else if (obj instanceof java.sql.Timestamp) {
            return Types.TIMESTAMP;
        } else if (obj instanceof java.sql.Clob) {
            return Types.CLOB;
        } else if (obj instanceof Blob) {
            return Types.BLOB;
        } else if (obj instanceof java.sql.Array) {
            return Types.ARRAY;
        } else if (obj instanceof java.sql.Struct) {
            return Types.STRUCT;
        } else if (obj instanceof java.sql.Ref) {
            return Types.REF;
        } else {
            return Types.JAVA_OBJECT;
        }
    }

    public static Object stringRepresentation2SqlObject(int sqlType, String paramValText) throws SQLException {
        Object result;
        try {
            switch (sqlType) {
                case java.sql.Types.BIGINT:
                    result = Long.parseLong(paramValText);
                    break;
                case java.sql.Types.BIT:
                    result = Boolean.parseBoolean(paramValText);
                    break;
                case java.sql.Types.CLOB:
                    result = paramValText;
                    break;
                case java.sql.Types.BOOLEAN:
                    result = Boolean.parseBoolean(paramValText);
                    break;
                case java.sql.Types.CHAR:
                    result = paramValText;
                    break;
                case java.sql.Types.DATE:
                    try {
                        result = java.sql.Date.valueOf(paramValText);
                    } catch (IllegalArgumentException ex) {
                        throw new SQLException("Bad date representation: " + paramValText, ex);
                    }
                    break;
                case java.sql.Types.DECIMAL:
                    result = Double.parseDouble(paramValText);
                    break;
                case java.sql.Types.DOUBLE:
                case java.sql.Types.FLOAT:
                    result = Double.parseDouble(paramValText);
                    break;
                case java.sql.Types.INTEGER:
                    result = Integer.parseInt(paramValText);
                    break;
                case java.sql.Types.LONGNVARCHAR:
                    result = paramValText;
                    break;
                case java.sql.Types.LONGVARCHAR:
                    result = paramValText;
                    break;
                case java.sql.Types.NULL:
                    result = null;
                    break;
                case java.sql.Types.NUMERIC:
                    result = Double.parseDouble(paramValText);
                    break;
                case java.sql.Types.NVARCHAR:
                    result = paramValText;
                    break;
                case java.sql.Types.REAL:
                    result = Double.parseDouble(paramValText);
                    break;
                case java.sql.Types.SMALLINT:
                    result = Short.parseShort(paramValText);
                    break;
                case java.sql.Types.SQLXML:
                    result = paramValText;
                    break;
                case java.sql.Types.TIME:
                    try {
                        result = java.sql.Time.valueOf(paramValText);
                    } catch (IllegalArgumentException ex) {
                        throw new SQLException("Bad time representation: " + paramValText, ex);
                    }
                    break;
                case java.sql.Types.TIMESTAMP:
                    try {
                        result = java.sql.Timestamp.valueOf(paramValText);
                    } catch (IllegalArgumentException ex) {
                        throw new SQLException("Bad timestamp representation: " + paramValText, ex);
                    }
                    break;
                case java.sql.Types.TINYINT:
                    result = Byte.parseByte(paramValText);
                    break;
                case java.sql.Types.VARCHAR:
                    result = paramValText;
                    break;
                default:
                    throw new SQLException("Unsupported java.sql.Types: " + DataTypeInfo.getTypeName(sqlType));
            }
        } catch (NumberFormatException ex) {
            throw new SQLException("Bad number representation: " + paramValText, ex);
        }
        return result;
    }
}
