/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.metadata;

import com.bearsoft.rowset.compacts.*;
import com.bearsoft.rowset.utils.RowsetUtils;
import java.sql.Types;

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

    public static DataTypeInfo INOPERABLE_TYPE = new DataTypeInfo(RowsetUtils.INOPERABLE_TYPE_MARKER, null, Object.class.getName());
    public static DataTypeInfo JAVA_OBJECT = new DataTypeInfo(Types.JAVA_OBJECT, RowsetUtils.getTypeName(Types.JAVA_OBJECT), Object.class.getName());
    public static DataTypeInfo DATALINK = new DataTypeInfo(Types.DATALINK, RowsetUtils.getTypeName(Types.DATALINK), Object.class.getName());
    public static DataTypeInfo DISTINCT = new DataTypeInfo(Types.DISTINCT, RowsetUtils.getTypeName(Types.DISTINCT), Object.class.getName());
    public static DataTypeInfo NULL = new DataTypeInfo(Types.NULL, RowsetUtils.getTypeName(Types.NULL), Object.class.getName());
    public static DataTypeInfo ROWID = new DataTypeInfo(Types.ROWID, RowsetUtils.getTypeName(Types.ROWID), CompactRowId.class.getName());
    public static DataTypeInfo REF = new DataTypeInfo(Types.REF, RowsetUtils.getTypeName(Types.REF), CompactRef.class.getName());
    public static DataTypeInfo SQLXML = new DataTypeInfo(Types.SQLXML, RowsetUtils.getTypeName(Types.SQLXML), CompactSqlXml.class.getName());
    public static DataTypeInfo ARRAY = new DataTypeInfo(Types.ARRAY, RowsetUtils.getTypeName(Types.ARRAY), CompactArray.class.getName());
    public static DataTypeInfo STRUCT = new DataTypeInfo(Types.STRUCT, RowsetUtils.getTypeName(Types.STRUCT), CompactStruct.class.getName());
    public static DataTypeInfo OTHER = new DataTypeInfo(Types.OTHER, RowsetUtils.getTypeName(Types.OTHER), Object.class.getName());
    public static DataTypeInfo BLOB = new DataTypeInfo(Types.BLOB, RowsetUtils.getTypeName(Types.BLOB), CompactBlob.class.getName());
    public static DataTypeInfo BINARY = new DataTypeInfo(Types.BINARY, RowsetUtils.getTypeName(Types.BINARY), CompactBlob.class.getName());
    public static DataTypeInfo VARBINARY = new DataTypeInfo(Types.VARBINARY, RowsetUtils.getTypeName(Types.VARBINARY), CompactBlob.class.getName());
    public static DataTypeInfo LONGVARBINARY = new DataTypeInfo(Types.LONGVARBINARY, RowsetUtils.getTypeName(Types.LONGVARBINARY), CompactBlob.class.getName());
    public static DataTypeInfo CLOB = new DataTypeInfo(Types.CLOB, RowsetUtils.getTypeName(Types.CLOB), CompactClob.class.getName());
    public static DataTypeInfo NCLOB = new DataTypeInfo(Types.NCLOB, RowsetUtils.getTypeName(Types.NCLOB), CompactClob.class.getName());
    public static DataTypeInfo DECIMAL = new DataTypeInfo(Types.DECIMAL, RowsetUtils.getTypeName(Types.DECIMAL), java.math.BigDecimal.class.getName());
    public static DataTypeInfo NUMERIC = new DataTypeInfo(Types.NUMERIC, RowsetUtils.getTypeName(Types.NUMERIC), java.math.BigDecimal.class.getName());
    public static DataTypeInfo BIGINT = new DataTypeInfo(Types.BIGINT, RowsetUtils.getTypeName(Types.BIGINT), java.math.BigInteger.class.getName());
    public static DataTypeInfo SMALLINT = new DataTypeInfo(Types.SMALLINT, RowsetUtils.getTypeName(Types.SMALLINT), Short.class.getName());
    public static DataTypeInfo TINYINT = new DataTypeInfo(Types.TINYINT, RowsetUtils.getTypeName(Types.TINYINT), Integer.class.getName());
    public static DataTypeInfo INTEGER = new DataTypeInfo(Types.INTEGER, RowsetUtils.getTypeName(Types.INTEGER), Integer.class.getName());
    public static DataTypeInfo REAL = new DataTypeInfo(Types.REAL, RowsetUtils.getTypeName(Types.REAL), Float.class.getName());
    public static DataTypeInfo FLOAT = new DataTypeInfo(Types.FLOAT, RowsetUtils.getTypeName(Types.FLOAT), Float.class.getName());
    public static DataTypeInfo DOUBLE = new DataTypeInfo(Types.DOUBLE, RowsetUtils.getTypeName(Types.DOUBLE), Double.class.getName());
    public static DataTypeInfo CHAR = new DataTypeInfo(Types.CHAR, RowsetUtils.getTypeName(Types.CHAR), String.class.getName());
    public static DataTypeInfo VARCHAR = new DataTypeInfo(Types.VARCHAR, RowsetUtils.getTypeName(Types.VARCHAR), String.class.getName());
    public static DataTypeInfo LONGVARCHAR = new DataTypeInfo(Types.LONGVARCHAR, RowsetUtils.getTypeName(Types.LONGVARCHAR), String.class.getName());
    public static DataTypeInfo NCHAR = new DataTypeInfo(Types.NCHAR, RowsetUtils.getTypeName(Types.NCHAR), String.class.getName());
    public static DataTypeInfo NVARCHAR = new DataTypeInfo(Types.NVARCHAR, RowsetUtils.getTypeName(Types.NVARCHAR), String.class.getName());
    public static DataTypeInfo LONGNVARCHAR = new DataTypeInfo(Types.LONGNVARCHAR, RowsetUtils.getTypeName(Types.LONGNVARCHAR), String.class.getName());
    public static DataTypeInfo BOOLEAN = new DataTypeInfo(Types.BOOLEAN, RowsetUtils.getTypeName(Types.BOOLEAN), Boolean.class.getName());
    public static DataTypeInfo BIT = new DataTypeInfo(Types.BIT, RowsetUtils.getTypeName(Types.BIT), Boolean.class.getName());
    public static DataTypeInfo DATE = new DataTypeInfo(Types.DATE, RowsetUtils.getTypeName(Types.DATE), java.util.Date.class.getName());
    public static DataTypeInfo TIMESTAMP = new DataTypeInfo(Types.TIMESTAMP, RowsetUtils.getTypeName(Types.TIMESTAMP), java.util.Date.class.getName());
    public static DataTypeInfo TIME = new DataTypeInfo(Types.TIME, RowsetUtils.getTypeName(Types.TIME), java.util.Date.class.getName());

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
        if (sqlType == RowsetUtils.INOPERABLE_TYPE_MARKER) {
            return "INOPERABLE_TYPE";
        } else {
            return sqlTypeName != null ? sqlTypeName : RowsetUtils.getTypeName(sqlType);
        }
    }

    public void setSqlTypeName(String sqlTypeName) {
        this.sqlTypeName = sqlTypeName;
    }

    public String getJavaClassName() {
        return javaClassName;
    }

    public void setJavaClassName(String javaClassName) {
        this.javaClassName = javaClassName;
    }
}
