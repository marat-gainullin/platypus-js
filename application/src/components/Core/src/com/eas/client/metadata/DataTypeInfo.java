/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.metadata;

import com.eas.util.IDGenerator;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Data type info is class intended to hold information about data type in the following manner.
 * It holds sql type identifier from java.sql.Types, it's text description and java class name of representing class in java proframming language.
 * It's used in field's metadata description, custom serializers identification and in converting process.
 * @author mg
 * @see Types
 * @see Field
 * @see com.bearsoft.rowset.Converter
 * @see com.bearsoft.rowset.serial.CustomSerializer
 */
public class DataTypeInfo {

    private static final Map<Integer, String> typesNames = new HashMap<>();

    static {
        typesNames.put(java.sql.Types.ARRAY, "ARRAY");
        typesNames.put(java.sql.Types.BIGINT, "BIGINT");
        typesNames.put(java.sql.Types.BINARY, "BINARY");
        typesNames.put(java.sql.Types.BIT, "BIT");
        typesNames.put(java.sql.Types.BLOB, "BLOB");
        typesNames.put(java.sql.Types.BOOLEAN, "BOOLEAN");
        typesNames.put(java.sql.Types.CHAR, "CHAR");
        typesNames.put(java.sql.Types.CLOB, "CLOB");
        typesNames.put(java.sql.Types.DATALINK, "DATALINK");
        typesNames.put(java.sql.Types.DATE, "DATE");
        typesNames.put(java.sql.Types.DECIMAL, "DECIMAL");
        typesNames.put(java.sql.Types.DISTINCT, "DISTINCT");
        typesNames.put(java.sql.Types.DOUBLE, "DOUBLE");
        typesNames.put(java.sql.Types.FLOAT, "FLOAT");
        typesNames.put(java.sql.Types.INTEGER, "INTEGER");
        typesNames.put(java.sql.Types.JAVA_OBJECT, "JAVA_OBJECT");
        typesNames.put(java.sql.Types.LONGNVARCHAR, "LONGNVARCHAR");
        typesNames.put(java.sql.Types.LONGVARBINARY, "LONGVARBINARY");
        typesNames.put(java.sql.Types.LONGVARCHAR, "LONGVARCHAR");
        typesNames.put(java.sql.Types.NCHAR, "NCHAR");
        typesNames.put(java.sql.Types.NCLOB, "NCLOB");
        typesNames.put(java.sql.Types.NULL, "NULL");
        typesNames.put(java.sql.Types.NUMERIC, "NUMERIC");
        typesNames.put(java.sql.Types.NVARCHAR, "NVARCHAR");
        typesNames.put(java.sql.Types.OTHER, "OTHER");
        typesNames.put(java.sql.Types.REAL, "REAL");
        typesNames.put(java.sql.Types.REF, "REF");
        typesNames.put(java.sql.Types.ROWID, "ROWID");
        typesNames.put(java.sql.Types.SMALLINT, "SMALLINT");
        typesNames.put(java.sql.Types.SQLXML, "SQLXML");
        typesNames.put(java.sql.Types.STRUCT, "STRUCT");
        typesNames.put(java.sql.Types.TIME, "TIME");
        typesNames.put(java.sql.Types.TIMESTAMP, "TIMESTAMP");
        typesNames.put(java.sql.Types.TINYINT, "TINYINT");
        typesNames.put(java.sql.Types.VARBINARY, "VARBINARY");
        typesNames.put(java.sql.Types.VARCHAR, "VARCHAR");
    }
    public static String getTypeName(int type) {
        return typesNames.get(type);
    }
    
    public static DataTypeInfo JAVA_OBJECT = new DataTypeInfo(Types.JAVA_OBJECT, getTypeName(Types.JAVA_OBJECT), Object.class.getName());
    public static DataTypeInfo DATALINK = new DataTypeInfo(Types.DATALINK, getTypeName(Types.DATALINK), Object.class.getName());
    public static DataTypeInfo DISTINCT = new DataTypeInfo(Types.DISTINCT, getTypeName(Types.DISTINCT), Object.class.getName());
    public static DataTypeInfo NULL = new DataTypeInfo(Types.NULL, getTypeName(Types.NULL), Object.class.getName());
    public static DataTypeInfo ROWID = new DataTypeInfo(Types.ROWID, getTypeName(Types.ROWID), String.class.getName());
    public static DataTypeInfo REF = new DataTypeInfo(Types.REF, getTypeName(Types.REF), String.class.getName());
    public static DataTypeInfo SQLXML = new DataTypeInfo(Types.SQLXML, getTypeName(Types.SQLXML), String.class.getName());
    public static DataTypeInfo ARRAY = new DataTypeInfo(Types.ARRAY, getTypeName(Types.ARRAY), String.class.getName());
    public static DataTypeInfo STRUCT = new DataTypeInfo(Types.STRUCT, getTypeName(Types.STRUCT), String.class.getName());
    public static DataTypeInfo OTHER = new DataTypeInfo(Types.OTHER, getTypeName(Types.OTHER), Object.class.getName());
    public static DataTypeInfo BLOB = new DataTypeInfo(Types.BLOB, getTypeName(Types.BLOB), Object.class.getName());
    public static DataTypeInfo BINARY = new DataTypeInfo(Types.BINARY, getTypeName(Types.BINARY), Object.class.getName());
    public static DataTypeInfo VARBINARY = new DataTypeInfo(Types.VARBINARY, getTypeName(Types.VARBINARY), Object.class.getName());
    public static DataTypeInfo LONGVARBINARY = new DataTypeInfo(Types.LONGVARBINARY, getTypeName(Types.LONGVARBINARY), Object.class.getName());
    public static DataTypeInfo CLOB = new DataTypeInfo(Types.CLOB, getTypeName(Types.CLOB), String.class.getName());
    public static DataTypeInfo NCLOB = new DataTypeInfo(Types.NCLOB, getTypeName(Types.NCLOB), String.class.getName());
    public static DataTypeInfo DECIMAL = new DataTypeInfo(Types.DECIMAL, getTypeName(Types.DECIMAL), java.math.BigDecimal.class.getName());
    public static DataTypeInfo NUMERIC = new DataTypeInfo(Types.NUMERIC, getTypeName(Types.NUMERIC), java.math.BigDecimal.class.getName());
    public static DataTypeInfo BIGINT = new DataTypeInfo(Types.BIGINT, getTypeName(Types.BIGINT), java.math.BigInteger.class.getName());
    public static DataTypeInfo SMALLINT = new DataTypeInfo(Types.SMALLINT, getTypeName(Types.SMALLINT), Short.class.getName());
    public static DataTypeInfo TINYINT = new DataTypeInfo(Types.TINYINT, getTypeName(Types.TINYINT), Integer.class.getName());
    public static DataTypeInfo INTEGER = new DataTypeInfo(Types.INTEGER, getTypeName(Types.INTEGER), Integer.class.getName());
    public static DataTypeInfo REAL = new DataTypeInfo(Types.REAL, getTypeName(Types.REAL), Float.class.getName());
    public static DataTypeInfo FLOAT = new DataTypeInfo(Types.FLOAT, getTypeName(Types.FLOAT), Float.class.getName());
    public static DataTypeInfo DOUBLE = new DataTypeInfo(Types.DOUBLE, getTypeName(Types.DOUBLE), Double.class.getName());
    public static DataTypeInfo CHAR = new DataTypeInfo(Types.CHAR, getTypeName(Types.CHAR), String.class.getName());
    public static DataTypeInfo VARCHAR = new DataTypeInfo(Types.VARCHAR, getTypeName(Types.VARCHAR), String.class.getName());
    public static DataTypeInfo LONGVARCHAR = new DataTypeInfo(Types.LONGVARCHAR, getTypeName(Types.LONGVARCHAR), String.class.getName());
    public static DataTypeInfo NCHAR = new DataTypeInfo(Types.NCHAR, getTypeName(Types.NCHAR), String.class.getName());
    public static DataTypeInfo NVARCHAR = new DataTypeInfo(Types.NVARCHAR, getTypeName(Types.NVARCHAR), String.class.getName());
    public static DataTypeInfo LONGNVARCHAR = new DataTypeInfo(Types.LONGNVARCHAR, getTypeName(Types.LONGNVARCHAR), String.class.getName());
    public static DataTypeInfo BOOLEAN = new DataTypeInfo(Types.BOOLEAN, getTypeName(Types.BOOLEAN), Boolean.class.getName());
    public static DataTypeInfo BIT = new DataTypeInfo(Types.BIT, getTypeName(Types.BIT), Boolean.class.getName());
    public static DataTypeInfo DATE = new DataTypeInfo(Types.DATE, getTypeName(Types.DATE), java.util.Date.class.getName());
    public static DataTypeInfo TIMESTAMP = new DataTypeInfo(Types.TIMESTAMP, getTypeName(Types.TIMESTAMP), java.util.Date.class.getName());
    public static DataTypeInfo TIME = new DataTypeInfo(Types.TIME, getTypeName(Types.TIME), java.util.Date.class.getName());
    public static DataTypeInfo GEOMETRY = new DataTypeInfo(Types.OTHER, "JTS_Geometry", java.lang.Object.class.getName());

    public static DataTypeInfo valueOf(int aSqlType) {
        switch (aSqlType) {
            case Types.JAVA_OBJECT:
                return JAVA_OBJECT;
            case Types.DATALINK:
                return DATALINK;
            case Types.DISTINCT:
                return DISTINCT;
            case Types.NULL:
                return NULL;
            case Types.ROWID:
                return ROWID;
            case Types.REF:
                return REF;
            case Types.SQLXML:
                return SQLXML;
            case Types.ARRAY:
                return ARRAY;
            case Types.STRUCT:
                return STRUCT;
            case Types.OTHER:
                return OTHER;
            case Types.BLOB:
                return BLOB;
            case Types.BINARY:
                return BINARY;
            case Types.VARBINARY:
                return VARBINARY;
            case Types.LONGVARBINARY:
                return LONGVARBINARY;
            case Types.CLOB:
                return CLOB;
            case Types.NCLOB:
                return NCLOB;
            case Types.DECIMAL:
                return DECIMAL;
            case Types.NUMERIC:
                return NUMERIC;
            case Types.BIGINT:
                return BIGINT;
            case Types.SMALLINT:
                return SMALLINT;
            case Types.TINYINT:
                return TINYINT;
            case Types.INTEGER:
                return INTEGER;
            case Types.REAL:
                return REAL;
            case Types.FLOAT:
                return FLOAT;
            case Types.DOUBLE:
                return DOUBLE;
            case Types.CHAR:
                return CHAR;
            case Types.VARCHAR:
                return VARCHAR;
            case Types.LONGVARCHAR:
                return LONGVARCHAR;
            case Types.NCHAR:
                return NCHAR;
            case Types.NVARCHAR:
                return NVARCHAR;
            case Types.LONGNVARCHAR:
                return LONGNVARCHAR;
            case Types.BOOLEAN:
                return BOOLEAN;
            case Types.BIT:
                return BIT;
            case Types.DATE:
                return DATE;
            case Types.TIMESTAMP:
                return TIMESTAMP;
            case Types.TIME:
                return TIME;
            default:
                return OTHER;
        }
    }
    protected int sqlType;
    protected String sqlTypeName;
    protected String javaClassName;

    public DataTypeInfo() {
        super();
    }

    public DataTypeInfo(int aSqlType, String aSqlTypeName, String aJavaClassName) {
        super();
        sqlType = aSqlType;
        sqlTypeName = aSqlTypeName;
        javaClassName = aJavaClassName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof DataTypeInfo) {
            DataTypeInfo rf = (DataTypeInfo) obj;
            String rfJavaClassName = rf.getJavaClassName();
            return sqlType == rf.getSqlType()
                    && ((getSqlTypeName() == null && rf.getSqlTypeName() == null) || (getSqlTypeName() != null && getSqlTypeName().equals(rf.getSqlTypeName())))
                    && ((javaClassName == null && rfJavaClassName == null) || (javaClassName != null && javaClassName.equals(rfJavaClassName)));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.sqlType;
        hash = 53 * hash + (getSqlTypeName() != null ? getSqlTypeName().hashCode() : 0);
        hash = 53 * hash + (this.javaClassName != null ? this.javaClassName.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getSqlTypeName()).append(" (").append(javaClassName).append(")");
        return sb.toString();
    }

    public DataTypeInfo copy() {
        return new DataTypeInfo(sqlType, sqlTypeName, javaClassName);
    }

    public int getSqlType() {
        return sqlType;
    }

    /**
     * Sets sql type of thid type info. Resets sqlTypeName to null.
     * Thus, the schema is: setSqlType -> setSqlTypeName and again in this order if
     * you whant to change sql type later.
     * @param aSqlType
     */
    public void setSqlType(int aSqlType) {
        sqlType = aSqlType;
        sqlTypeName = null;
    }

    public String getSqlTypeName() {
            return sqlTypeName != null ? sqlTypeName : getTypeName(sqlType);
    }

    public void setSqlTypeName(String aValue) {
        sqlTypeName = aValue;
    }

    public String getJavaClassName() {
        return javaClassName;
    }

    public void setJavaClassName(String aValue) {
        javaClassName = aValue;
    }
    
    public Object generateValue() {
        switch (sqlType) {
            case java.sql.Types.TINYINT:
            case java.sql.Types.SMALLINT:
            case java.sql.Types.INTEGER:
            case java.sql.Types.BIGINT:
            case java.sql.Types.FLOAT:
            case java.sql.Types.REAL:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.NUMERIC:
            case java.sql.Types.DECIMAL:
                return IDGenerator.genID();
            case java.sql.Types.CHAR:
            case java.sql.Types.VARCHAR:
            case java.sql.Types.LONGVARCHAR:
            case java.sql.Types.NCHAR:
            case java.sql.Types.NVARCHAR:
            case java.sql.Types.LONGNVARCHAR:
                return String.valueOf(IDGenerator.genID());
            case java.sql.Types.DATE:
            case java.sql.Types.TIME:
            case java.sql.Types.TIMESTAMP:
                return new Date(IDGenerator.genID());
            case java.sql.Types.BIT:
            case java.sql.Types.BOOLEAN:
                return false;
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
            case java.sql.Types.NCLOB:
                break;
        }
        assert false;
        return null;
    }

}
