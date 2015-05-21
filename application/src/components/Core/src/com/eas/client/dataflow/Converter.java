/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dataflow;

import com.eas.client.metadata.DataTypeInfo;
import com.eas.util.BinaryUtils;
import com.eas.util.StringUtils;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Converter has to convert some value of any compatible class to value of
 * predefined class, according to sql type.
 *
 * @author mg
 */
public class Converter {

    public static BigDecimal number2BigDecimal(Number aNumber) {
        if (aNumber instanceof Float || aNumber instanceof Double) {
            return new BigDecimal(aNumber.doubleValue());
        } else if (aNumber instanceof BigInteger) {
            return new BigDecimal((BigInteger) aNumber);
        } else if (aNumber instanceof BigDecimal) {
            return (BigDecimal) aNumber;
        } else {
            return new BigDecimal(aNumber.longValue());
        }
    }

    public static Object get(Wrapper aRs, int aColIndex, DataTypeInfo aTypeInfo) throws SQLException {
        try {
            Object value = null;
            switch (aTypeInfo.getSqlType()) {
                case Types.JAVA_OBJECT:
                case Types.DATALINK:
                case Types.DISTINCT:
                case Types.NULL:
                case Types.ROWID:
                case Types.REF:
                case Types.SQLXML:
                case Types.ARRAY:
                case Types.STRUCT:
                case Types.OTHER:
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getString(aColIndex) : ((CallableStatement) aRs).getString(aColIndex);
                    break;
                case Types.BINARY:
                case Types.VARBINARY:
                case Types.LONGVARBINARY:
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getBytes(aColIndex) : ((CallableStatement) aRs).getBytes(aColIndex);
                    break;
                case Types.BLOB:
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getBlob(aColIndex) : ((CallableStatement) aRs).getBlob(aColIndex);
                    if (value != null) {
                        try (InputStream is = ((Blob) value).getBinaryStream()) {
                            value = BinaryUtils.readStream(is, -1);
                        }
                    }
                    break;
                // clobs
                case Types.CLOB:
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getClob(aColIndex) : ((CallableStatement) aRs).getClob(aColIndex);
                    if (value != null) {
                        try (Reader reader = ((Clob) value).getCharacterStream()) {
                            value = StringUtils.readReader(reader, -1);
                        }
                    }
                    break;
                case Types.NCLOB:
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getNClob(aColIndex) : ((CallableStatement) aRs).getNClob(aColIndex);
                    if (value != null) {
                        try (Reader reader = ((NClob) value).getCharacterStream()) {
                            value = StringUtils.readReader(reader, -1);
                        }
                    }
                    break;
                // numbers
                case Types.DECIMAL:
                case Types.NUMERIC:
                    // target type - BigDecimal
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getBigDecimal(aColIndex) : ((CallableStatement) aRs).getBigDecimal(aColIndex);
                    break;
                case Types.BIGINT:
                    // target type - BigInteger
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getBigDecimal(aColIndex) : ((CallableStatement) aRs).getBigDecimal(aColIndex);
                    if (value != null) {
                        value = ((BigDecimal) value).toBigInteger();
                    }
                    break;
                case Types.SMALLINT:
                    // target type - Short
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getShort(aColIndex) : ((CallableStatement) aRs).getShort(aColIndex);
                    break;
                case Types.TINYINT:
                case Types.INTEGER:
                    // target type - Int
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getInt(aColIndex) : ((CallableStatement) aRs).getInt(aColIndex);
                    break;
                case Types.REAL:
                case Types.FLOAT:
                    // target type - Float
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getFloat(aColIndex) : ((CallableStatement) aRs).getFloat(aColIndex);
                    break;
                case Types.DOUBLE:
                    // target type - Double
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getDouble(aColIndex) : ((CallableStatement) aRs).getDouble(aColIndex);
                    break;
                // strings
                case Types.CHAR:
                case Types.NCHAR:
                case Types.VARCHAR:
                case Types.NVARCHAR:
                case Types.LONGVARCHAR:
                case Types.LONGNVARCHAR:
                    // target type - string
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getString(aColIndex) : ((CallableStatement) aRs).getString(aColIndex);
                    break;
                // booleans
                case Types.BOOLEAN:
                case Types.BIT:
                    // target type - Boolean
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getBoolean(aColIndex) : ((CallableStatement) aRs).getBoolean(aColIndex);
                    break;
                // dates, times
                case Types.DATE:
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getDate(aColIndex) : ((CallableStatement) aRs).getDate(aColIndex);
                    break;
                case Types.TIMESTAMP:
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getTimestamp(aColIndex) : ((CallableStatement) aRs).getTimestamp(aColIndex);
                    break;
                case Types.TIME:
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getTime(aColIndex) : ((CallableStatement) aRs).getTime(aColIndex);
                    break;
            }
            if (aRs instanceof ResultSet ? ((ResultSet) aRs).wasNull() : ((CallableStatement) aRs).wasNull()) {
                value = null;
            }
            return value;
        } catch (Exception ex) {
            if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new SQLException(ex);
            }
        }
    }

    public static void convertAndAssign(Object aValue, DataTypeInfo aTypeInfo, Connection aConn, int aParameterIndex, PreparedStatement aStmt) throws SQLException {
        if (aValue != null) {
            /*
            if (aValue instanceof JSObject) {
                aValue = aSpace.toJava(aValue);
            }
            */
            switch (aTypeInfo.getSqlType()) {
                // Some strange types. No one knows how to work with them.
                case Types.JAVA_OBJECT:
                case Types.DATALINK:
                case Types.DISTINCT:
                case Types.NULL:
                case Types.ROWID:
                case Types.REF:
                case Types.SQLXML:
                case Types.ARRAY:
                case Types.STRUCT:
                case Types.OTHER:
                    try {
                        aStmt.setString(aParameterIndex, aValue.toString());
                    } catch (Exception ex) {
                        aStmt.setNull(aParameterIndex, aTypeInfo.getSqlType(), aTypeInfo.getSqlTypeName());
                        Logger.getLogger(Converter.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.BINARY:
                case Types.VARBINARY:
                case Types.LONGVARBINARY:
                    // target type - byte[]
                    if (aValue instanceof byte[]) {
                        aStmt.setBytes(aParameterIndex, (byte[]) aValue);
                    } else {
                        Logger.getLogger(Converter.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.BLOB:
                    // target type - java.sql.Blob
                    if (aValue instanceof Blob) {
                        aStmt.setBlob(aParameterIndex, (Blob) aValue);
                    } else {
                        Logger.getLogger(Converter.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.CLOB:
                    // target type - java.sql.Clob
                    if (aValue instanceof Clob) {
                        aStmt.setClob(aParameterIndex, (Clob) aValue);
                    } else {
                        Logger.getLogger(Converter.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.NCLOB:
                    // target type - java.sql.NClob
                    if (aValue instanceof NClob) {
                        aStmt.setNClob(aParameterIndex, (NClob) aValue);
                    } else {
                        Logger.getLogger(Converter.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.DECIMAL:
                case Types.NUMERIC:
                    // target type - BigDecimal
                    // target type - BigDecimal
                    BigDecimal castedDecimal = null;
                    if (aValue instanceof Number) {
                        castedDecimal = number2BigDecimal((Number) aValue);
                    } else if (aValue instanceof String) {
                        castedDecimal = new BigDecimal((String) aValue);
                    } else if (aValue instanceof Boolean) {
                        castedDecimal = new BigDecimal(((Boolean) aValue) ? 1 : 0);
                    } else if (aValue instanceof Date) {
                        castedDecimal = new BigDecimal(((Date) aValue).getTime());
                    }
                    if (castedDecimal != null) {
                        aStmt.setBigDecimal(aParameterIndex, castedDecimal);
                    } else {
                        Logger.getLogger(Converter.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.BIGINT:
                    // target type - BigInteger
                    BigInteger castedInteger = null;
                    if (aValue instanceof Number) {
                        castedInteger = BigInteger.valueOf(((Number) aValue).longValue());
                    } else if (aValue instanceof String) {
                        castedInteger = new BigInteger((String) aValue);
                    } else if (aValue instanceof Boolean) {
                        castedInteger = BigInteger.valueOf(((Boolean) aValue) ? 1 : 0);
                    } else if (aValue instanceof Date) {
                        castedInteger = BigInteger.valueOf(((Date) aValue).getTime());
                    }
                    if (castedInteger != null) {
                        aStmt.setBigDecimal(aParameterIndex, new BigDecimal(castedInteger));
                    } else {
                        Logger.getLogger(Converter.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.SMALLINT:
                    // target type - Short
                    // target type - Short
                    Short castedShort = null;
                    if (aValue instanceof Number) {
                        castedShort = ((Number) aValue).shortValue();
                    } else if (aValue instanceof String) {
                        castedShort = Double.valueOf((String) aValue).shortValue();
                    } else if (aValue instanceof Boolean) {
                        castedShort = Integer.valueOf(((Boolean) aValue) ? 1 : 0).shortValue();
                    } else if (aValue instanceof Date) {
                        castedShort = Integer.valueOf((int) ((Date) aValue).getTime()).shortValue();
                    }
                    if (castedShort != null) {
                        aStmt.setShort(aParameterIndex, castedShort);
                    } else {
                        Logger.getLogger(Converter.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.TINYINT:
                case Types.INTEGER:
                    // target type - Integer
                    Integer castedInt = null;
                    if (aValue instanceof Number) {
                        castedInt = ((Number) aValue).intValue();
                    } else if (aValue instanceof String) {
                        castedInt = Double.valueOf((String) aValue).intValue();
                    } else if (aValue instanceof Boolean) {
                        castedInt = (Boolean) aValue ? 1 : 0;
                    } else if (aValue instanceof Date) {
                        castedInt = (int) ((Date) aValue).getTime();
                    }
                    if (castedInt != null) {
                        aStmt.setInt(aParameterIndex, castedInt);
                    } else {
                        Logger.getLogger(Converter.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.REAL:
                case Types.FLOAT:
                    // target type - Float
                    Float castedFloat = null;
                    if (aValue instanceof Number) {
                        castedFloat = ((Number) aValue).floatValue();
                    } else if (aValue instanceof String) {
                        castedFloat = Float.valueOf((String) aValue);
                    } else if (aValue instanceof Boolean) {
                        castedFloat = Float.valueOf(((Boolean) aValue) ? 1 : 0);
                    } else if (aValue instanceof Date) {
                        castedFloat = (float) ((Date) aValue).getTime();
                    }
                    if (castedFloat != null) {
                        aStmt.setFloat(aParameterIndex, castedFloat);
                    } else {
                        Logger.getLogger(Converter.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.DOUBLE:
                    // target type - Double
                    Double castedDouble = null;
                    if (aValue instanceof Number) {
                        castedDouble = ((Number) aValue).doubleValue();
                    } else if (aValue instanceof String) {
                        castedDouble = Double.valueOf((String) aValue);
                    } else if (aValue instanceof Boolean) {
                        castedDouble = Double.valueOf(((Boolean) aValue) ? 1 : 0);
                    } else if (aValue instanceof Date) {
                        castedDouble = (double) ((Date) aValue).getTime();
                    }
                    if (castedDouble != null) {
                        aStmt.setDouble(aParameterIndex, castedDouble);
                    } else {
                        Logger.getLogger(Converter.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                case Types.NCHAR:
                case Types.NVARCHAR:
                case Types.LONGNVARCHAR:
                    // target type - string
                    // target type - string
                    String castedString = null;
                    if (aValue instanceof Number) {
                        castedString = ((Number) aValue).toString();
                    } else if (aValue instanceof String) {
                        castedString = (String) aValue;
                    } else if (aValue instanceof Boolean) {
                        castedString = ((Boolean) aValue) ? ((Boolean) aValue).toString() : "";
                    } else if (aValue instanceof Date) {
                        castedString = String.valueOf(((Date) aValue).getTime());
                    } else if (aValue instanceof Clob) {
                        castedString = ((Clob) aValue).getSubString(1, (int) ((Clob) aValue).length());
                    }
                    if (castedString != null) {
                        if (aTypeInfo.getSqlType() == Types.NCHAR || aTypeInfo.getSqlType() == Types.NVARCHAR || aTypeInfo.getSqlType() == Types.LONGNVARCHAR) {
                            aStmt.setNString(aParameterIndex, castedString);
                        } else {
                            aStmt.setString(aParameterIndex, castedString);
                        }
                    } else {
                        Logger.getLogger(Converter.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.BOOLEAN:
                case Types.BIT:
                    // target type - Boolean
                    Boolean castedBoolean = null;
                    if (aValue instanceof Number) {
                        castedBoolean = !(((Number) aValue).intValue() == 0);
                    } else if (aValue instanceof String || aValue instanceof Clob) {
                        String s = null;
                        if (aValue instanceof String) {
                            s = (String) aValue;
                        } else {
                            s = ((Clob) aValue).getSubString(1, (int) ((Clob) aValue).length());
                        }
                        castedBoolean = !s.isEmpty();
                    } else if (aValue instanceof Boolean) {
                        castedBoolean = (Boolean) aValue;
                    } else if (aValue instanceof Date) {
                        castedBoolean = !((Date) aValue).equals(new Date(0));
                    }
                    if (castedBoolean != null) {
                        aStmt.setBoolean(aParameterIndex, castedBoolean);
                    } else {
                        Logger.getLogger(Converter.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.DATE:
                case Types.TIMESTAMP:
                case Types.TIME:
                    // target type - date
                    Date castedDate = null;
                    if (aValue instanceof Number) {
                        castedDate = new Date(((Number) aValue).longValue());
                    } else if (aValue instanceof String) {
                        castedDate = new Date(Long.valueOf((String) aValue));
                    } else if (aValue instanceof Boolean) {
                        castedDate = new Date(((Boolean) aValue) ? 1 : 0);
                    } else if (aValue instanceof Date) {
                        castedDate = ((Date) aValue);
                    }
                    if (castedDate != null) {
                        if (aTypeInfo.getSqlType() == Types.DATE) {
                            aStmt.setDate(aParameterIndex, new java.sql.Date(castedDate.getTime()));
                        } else if (aTypeInfo.getSqlType() == Types.TIMESTAMP) {
                            aStmt.setTimestamp(aParameterIndex, new java.sql.Timestamp(castedDate.getTime()));
                        } else if (aTypeInfo.getSqlType() == Types.TIME) {
                            aStmt.setTime(aParameterIndex, new java.sql.Time(castedDate.getTime()));
                        } else {
                            assert false;
                        }
                    } else {
                        Logger.getLogger(Converter.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
            }
        } else {
            try {
                aStmt.setNull(aParameterIndex, aTypeInfo.getSqlType());
            } catch (SQLException ex) {
                aStmt.setNull(aParameterIndex, aTypeInfo.getSqlType(), aTypeInfo.getSqlTypeName());
            }
        }
    }
    protected static final String FALLED_TO_NULL_MSG = "Some value falled to null while tranferring to a database. May be it''s class is unsupported: {0}";
}
