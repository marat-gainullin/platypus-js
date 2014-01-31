/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset;

import com.bearsoft.rowset.compacts.CompactBlob;
import com.bearsoft.rowset.compacts.CompactClob;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.bearsoft.rowset.wrappers.jdbc.BlobImpl;
import com.bearsoft.rowset.wrappers.jdbc.ClobImpl;
import com.bearsoft.rowset.wrappers.jdbc.NClobImpl;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RowsetConverter has to convert some value of any compatible class to value of
 * predefined class, according to sql type.
 *
 * @author mg
 */
public class RowsetConverter implements Converter {

    @Override
    public Object getFromJdbcAndConvert2RowsetCompatible(ResultSet aRs, int aColIndex, DataTypeInfo aTypeInfo) throws RowsetException {
        try {
            Object value = null;
            switch (aTypeInfo.getSqlType()) {
                // Some strange types. No one knows how to work with them.
                case Types.JAVA_OBJECT:
                case Types.DATALINK:
                case Types.DISTINCT:
                case Types.NULL:
                case Types.ROWID:
                    value = aRs.getRowId(aColIndex);
                    break;
                case Types.REF:
                    value = aRs.getRef(aColIndex);
                    break;
                case Types.SQLXML:
                    value = aRs.getSQLXML(aColIndex);
                    break;
                case Types.ARRAY:
                    value = aRs.getArray(aColIndex);
                    break;
                case Types.STRUCT:
                    value = null;
                    break;
                case Types.OTHER:
                    value = null;
                    break;
                case Types.BINARY:
                case Types.VARBINARY:
                case Types.LONGVARBINARY:
                    value = aRs.getBytes(aColIndex);
                    break;
                case Types.BLOB:
                    value = aRs.getBlob(aColIndex);
                    break;
                // clobs
                case Types.CLOB:
                    value = aRs.getClob(aColIndex);
                    break;
                case Types.NCLOB:
                    value = aRs.getNClob(aColIndex);
                    break;
                // numbers
                case Types.DECIMAL:
                case Types.NUMERIC:
                    // target type - BigDecimal
                    value = aRs.getBigDecimal(aColIndex);
                    break;
                case Types.BIGINT:
                    // target type - BigInteger
                    value = aRs.getBigDecimal(aColIndex);
                    if (value != null) {
                        value = ((BigDecimal) value).toBigInteger();
                    }
                    break;
                case Types.SMALLINT:
                    // target type - Short
                    value = aRs.getShort(aColIndex);
                    break;
                case Types.TINYINT:
                case Types.INTEGER:
                    // target type - Int
                    value = aRs.getInt(aColIndex);
                    break;
                case Types.REAL:
                case Types.FLOAT:
                    // target type - Float
                    value = aRs.getFloat(aColIndex);
                    break;
                case Types.DOUBLE:
                    // target type - Double
                    value = aRs.getDouble(aColIndex);
                    break;
                // strings
                case Types.CHAR:
                case Types.NCHAR:
                case Types.VARCHAR:
                case Types.NVARCHAR:
                case Types.LONGVARCHAR:
                case Types.LONGNVARCHAR:
                    // target type - string
                    value = aRs.getString(aColIndex);
                    break;
                // booleans
                case Types.BOOLEAN:
                case Types.BIT:
                    // target type - Boolean
                    value = aRs.getBoolean(aColIndex);
                    break;
                // dates, times
                case Types.DATE:
                    value = aRs.getDate(aColIndex);
                    break;
                case Types.TIMESTAMP:
                    value = aRs.getTimestamp(aColIndex);
                    break;
                case Types.TIME:
                    value = aRs.getTime(aColIndex);
                    break;
            }
            if (aRs.wasNull()) {
                value = null;
            }
            return convert2RowsetCompatible(value, aTypeInfo);
        } catch (SQLException ex) {
            throw new RowsetException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public Object convert2RowsetCompatible(Object aValue, DataTypeInfo aTypeInfo) throws RowsetException {
        if (aValue != null) {
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
                    if (aValue instanceof String) {
                        try {
                            return (new WKTReader()).read((String) aValue);
                        } catch (ParseException ex) {
                            // no op
                        }
                    }
                    return aValue;
                case Types.BINARY:
                case Types.VARBINARY:
                case Types.LONGVARBINARY:
                case Types.BLOB:
                    // target type - CompactBlob
                    if (aValue instanceof CompactBlob) {
                        return aValue;
                    } else if (aValue instanceof Blob) {
                        try {
                            return new CompactBlob(((Blob) aValue).getBytes(1, (int) ((Blob) aValue).length()));
                        } catch (SQLException ex) {
                            Logger.getLogger(RowsetConverter.class.getName()).log(Level.SEVERE, null, ex);
                            throw new RowsetException(ex);
                        }
                    } else if (aValue instanceof byte[]) {
                        return new CompactBlob((byte[]) aValue);
                    }
                    break;
                // clobs
                case Types.CLOB:
                case Types.NCLOB:
                    // target type - CompactClob
                    if (aValue instanceof CompactClob) {
                        return aValue;
                    } else if (aValue instanceof Clob) {
                        try {
                            return new CompactClob(((Clob) aValue).getSubString(1, (int) ((Clob) aValue).length()));
                        } catch (SQLException ex) {
                            Logger.getLogger(RowsetConverter.class.getName()).log(Level.SEVERE, null, ex);
                            throw new RowsetException(ex);
                        }
                    } else if (aValue instanceof char[]) {
                        return new CompactClob(new String((char[]) aValue));
                    } else if (aValue instanceof String) {
                        return new CompactClob((String) aValue);
                    }
                    break;
                // numbers
                case Types.DECIMAL:
                case Types.NUMERIC:
                    // target type - BigDecimal
                    if (aValue instanceof Number) {
                        return RowsetUtils.number2BigDecimal((Number) aValue);
                    } else if (aValue instanceof String) {
                        return new BigDecimal((String) aValue);
                    } else if (aValue instanceof Boolean) {
                        return new BigDecimal(((Boolean) aValue) ? 1 : 0);
                    } else if (aValue instanceof Date) {
                        return new BigDecimal(((Date) aValue).getTime());
                    }
                    break;
                case Types.BIGINT:
                    // target type - BigInteger
                    if (aValue instanceof Number) {
                        return BigInteger.valueOf(((Number) aValue).longValue());
                    } else if (aValue instanceof String) {
                        return new BigInteger((String) aValue);
                    } else if (aValue instanceof Boolean) {
                        return BigInteger.valueOf(((Boolean) aValue) ? 1 : 0);
                    } else if (aValue instanceof Date) {
                        return BigInteger.valueOf(((Date) aValue).getTime());
                    }
                    break;
                case Types.SMALLINT:
                    // target type - Short
                    if (aValue instanceof Number) {
                        return ((Number) aValue).shortValue();
                    } else if (aValue instanceof String) {
                        return Integer.valueOf((String) aValue).shortValue();
                    } else if (aValue instanceof Boolean) {
                        return Integer.valueOf(((Boolean) aValue) ? 1 : 0).shortValue();
                    } else if (aValue instanceof Date) {
                        return Integer.valueOf((int) ((Date) aValue).getTime()).shortValue();
                    }
                    break;
                case Types.TINYINT:
                case Types.INTEGER:
                    // target type - Integer
                    if (aValue instanceof Number) {
                        return Integer.valueOf(((Number) aValue).intValue());
                    } else if (aValue instanceof String) {
                        return Integer.valueOf((String) aValue);
                    } else if (aValue instanceof Boolean) {
                        return Integer.valueOf(((Boolean) aValue) ? 1 : 0);
                    } else if (aValue instanceof Date) {
                        return Integer.valueOf((int) ((Date) aValue).getTime());
                    }
                    break;
                case Types.REAL:
                case Types.FLOAT:
                    // target type - Float
                    if (aValue instanceof Number) {
                        return Float.valueOf(((Number) aValue).floatValue());
                    } else if (aValue instanceof String) {
                        return Float.valueOf((String) aValue);
                    } else if (aValue instanceof Boolean) {
                        return Float.valueOf(((Boolean) aValue) ? 1 : 0);
                    } else if (aValue instanceof Date) {
                        return Float.valueOf((float) ((Date) aValue).getTime());
                    }
                    break;
                case Types.DOUBLE:
                    // target type - Double
                    if (aValue instanceof Number) {
                        return Double.valueOf(((Number) aValue).doubleValue());
                    } else if (aValue instanceof String) {
                        return Double.valueOf((String) aValue);
                    } else if (aValue instanceof Boolean) {
                        return Double.valueOf(((Boolean) aValue) ? 1 : 0);
                    } else if (aValue instanceof Date) {
                        return Double.valueOf((double) ((Date) aValue).getTime());
                    }
                    break;
                // strings
                case Types.CHAR:
                case Types.NCHAR:
                case Types.VARCHAR:
                case Types.NVARCHAR:
                case Types.LONGVARCHAR:
                case Types.LONGNVARCHAR:
                    // target type - string
                    if (aValue instanceof Number) {
                        return ((Number) aValue).toString();
                    } else if (aValue instanceof String) {
                        return (String) aValue;
                    } else if (aValue instanceof Boolean) {
                        return ((Boolean) aValue) ? ((Boolean) aValue).toString() : "";
                    } else if (aValue instanceof Date) {
                        return String.valueOf(((Date) aValue).getTime());
                    } else if (aValue instanceof CompactClob) {
                        return ((CompactClob) aValue).getData();
                    } else if (aValue instanceof Clob) {
                        try {
                            return ((Clob) aValue).getSubString(1, (int) ((Clob) aValue).length());
                        } catch (SQLException ex) {
                            throw new RowsetException(ex);
                        }
                    }
                    break;
                // booleans
                case Types.BOOLEAN:
                case Types.BIT:
                    // target type - Boolean
                    if (aValue instanceof Number) {
                        return !(((Number) aValue).intValue() == 0);
                    } else if (aValue instanceof String || aValue instanceof CompactClob || aValue instanceof Clob) {
                        try {
                            String s = null;
                            if (aValue instanceof String) {
                                s = (String) aValue;
                            } else if (aValue instanceof CompactClob) {
                                s = ((CompactClob) aValue).getData();
                            } else {
                                s = ((Clob) aValue).getSubString(1, (int) ((Clob) aValue).length());
                            }
                            return !s.isEmpty();
                        } catch (SQLException ex) {
                            throw new RowsetException(ex);
                        }
                    } else if (aValue instanceof Boolean) {
                        return ((Boolean) aValue);
                    } else if (aValue instanceof Date) {
                        return !((Date) aValue).equals(new Date(0));
                    }
                    break;
                // dates, times
                case Types.DATE:
                case Types.TIMESTAMP:
                case Types.TIME:
                    // target type - date
                    Date lValue = null;
                    if (aValue instanceof Number) {
                        lValue = new Date(((Number) aValue).longValue());
                    } else if (aValue instanceof String) {
                        lValue = new Date(Long.valueOf((String) aValue));
                    } else if (aValue instanceof Boolean) {
                        lValue = new Date(Long.valueOf(((Boolean) aValue) ? 1 : 0));
                    } else if (aValue instanceof Date) {
                        lValue = ((Date) aValue);
                    } else {
                        return aValue;
                    }
                    return lValue;
            }
        }
        return aValue;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Object convert2JdbcCompatible(Object aValue, DataTypeInfo aTypeInfo) throws RowsetException {
        try {
            if (aValue != null) {
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
                        return null;
                    case Types.BINARY:
                    case Types.VARBINARY:
                    case Types.LONGVARBINARY:
                        // target type - byte[]
                        if (aValue instanceof Blob) {
                            return ((Blob) aValue).getBytes(1, (int) ((Blob) aValue).length());
                        } else if (aValue instanceof CompactBlob) {
                            return ((CompactBlob) aValue).getData();
                        } else if (aValue instanceof byte[]) {
                            return (byte[]) aValue;
                        }
                        checkDataLoss(aValue);
                        return null;
                    case Types.BLOB:
                        // target type - java.sql.Blob
                        if (aValue instanceof Blob) {
                            return aValue;
                        } else if (aValue instanceof CompactBlob) {
                            return new BlobImpl((CompactBlob) aValue);
                        } else if (aValue instanceof byte[]) {
                            return new BlobImpl(new CompactBlob((byte[]) aValue));
                        }
                        checkDataLoss(aValue);
                        return null;
                    case Types.CLOB:
                        // target type - java.sql.Clob
                        if (aValue instanceof Clob) {
                            return aValue;
                        } else if (aValue instanceof CompactClob) {
                            return new ClobImpl((CompactClob) aValue);
                        } else if (aValue instanceof char[]) {
                            return new ClobImpl(new CompactClob(new String((char[]) aValue)));
                        } else if (aValue instanceof String) {
                            return new ClobImpl(new CompactClob((String) aValue));
                        }
                        checkDataLoss(aValue);
                        return null;
                    case Types.NCLOB:
                        // target type - java.sql.NClob
                        if (aValue instanceof NClob) {
                            return aValue;
                        } else if (aValue instanceof CompactClob) {
                            return new NClobImpl((CompactClob) aValue);
                        } else if (aValue instanceof char[]) {
                            return new NClobImpl(new CompactClob(new String((char[]) aValue)));
                        } else if (aValue instanceof String) {
                            return new NClobImpl(new CompactClob((String) aValue));
                        }
                        checkDataLoss(aValue);
                        return null;
                    case Types.DECIMAL:
                    case Types.NUMERIC:
                        // target type - BigDecimal
                        BigDecimal castedDecimal = null;
                        if (aValue instanceof Number) {
                            castedDecimal = RowsetUtils.number2BigDecimal((Number) aValue);
                        } else if (aValue instanceof String) {
                            castedDecimal = new BigDecimal((String) aValue);
                        } else if (aValue instanceof Boolean) {
                            castedDecimal = new BigDecimal(((Boolean) aValue) ? 1 : 0);
                        } else if (aValue instanceof Date) {
                            castedDecimal = new BigDecimal(((Date) aValue).getTime());
                        }
                        if (castedDecimal == null) {
                            checkDataLoss(aValue);
                        }
                        return castedDecimal;
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
                        if (castedInteger == null) {
                            checkDataLoss(aValue);
                        }
                        return castedInteger;
                    case Types.SMALLINT:
                        // target type - Short
                        Short castedShort = null;
                        if (aValue instanceof Number) {
                            castedShort = ((Number) aValue).shortValue();
                        } else if (aValue instanceof String) {
                            castedShort = Integer.valueOf((String) aValue).shortValue();
                        } else if (aValue instanceof Boolean) {
                            castedShort = Integer.valueOf(((Boolean) aValue) ? 1 : 0).shortValue();
                        } else if (aValue instanceof Date) {
                            castedShort = Integer.valueOf((int) ((Date) aValue).getTime()).shortValue();
                        }
                        if (castedShort == null) {
                            checkDataLoss(aValue);
                        }
                        return castedShort;
                    case Types.TINYINT:
                    case Types.INTEGER:
                        // target type - Integer
                        Integer castedInt = null;
                        if (aValue instanceof Number) {
                            castedInt = Integer.valueOf(((Number) aValue).intValue());
                        } else if (aValue instanceof String) {
                            castedInt = Integer.valueOf((String) aValue);
                        } else if (aValue instanceof Boolean) {
                            castedInt = Integer.valueOf(((Boolean) aValue) ? 1 : 0);
                        } else if (aValue instanceof Date) {
                            castedInt = Integer.valueOf((int) ((Date) aValue).getTime());
                        }
                        if (castedInt == null) {
                            checkDataLoss(aValue);
                        }
                        return castedInt;
                    case Types.REAL:
                    case Types.FLOAT:
                        // target type - Float
                        Float castedFloat = null;
                        if (aValue instanceof Number) {
                            castedFloat = Float.valueOf(((Number) aValue).floatValue());
                        } else if (aValue instanceof String) {
                            castedFloat = Float.valueOf((String) aValue);
                        } else if (aValue instanceof Boolean) {
                            castedFloat = Float.valueOf(((Boolean) aValue) ? 1 : 0);
                        } else if (aValue instanceof Date) {
                            castedFloat = Float.valueOf((float) ((Date) aValue).getTime());
                        }
                        if (castedFloat == null) {
                            checkDataLoss(aValue);
                        }
                        return castedFloat;
                    case Types.DOUBLE:
                        // target type - Double
                        Double castedDouble = null;
                        if (aValue instanceof Number) {
                            castedDouble = Double.valueOf(((Number) aValue).doubleValue());
                        } else if (aValue instanceof String) {
                            castedDouble = Double.valueOf((String) aValue);
                        } else if (aValue instanceof Boolean) {
                            castedDouble = Double.valueOf(((Boolean) aValue) ? 1 : 0);
                        } else if (aValue instanceof Date) {
                            castedDouble = Double.valueOf((double) ((Date) aValue).getTime());
                        }
                        if (castedDouble == null) {
                            checkDataLoss(aValue);
                        }
                        return castedDouble;
                    case Types.CHAR:
                    case Types.VARCHAR:
                    case Types.LONGVARCHAR:
                    case Types.NCHAR:
                    case Types.NVARCHAR:
                    case Types.LONGNVARCHAR:
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
                        } else if (aValue instanceof CompactClob) {
                            castedString = ((CompactClob) aValue).getData();
                        } else if (aValue instanceof Clob) {
                            try {
                                castedString = ((Clob) aValue).getSubString(1, (int) ((Clob) aValue).length());
                            } catch (SQLException ex) {
                                throw new RowsetException(ex);
                            }
                        }
                        if (castedString == null) {
                            checkDataLoss(aValue);
                        }
                        return castedString;
                    case Types.BOOLEAN:
                    case Types.BIT:
                        // target type - Boolean
                        Boolean castedBoolean = null;
                        if (aValue instanceof Number) {
                            castedBoolean = !(((Number) aValue).intValue() == 0);
                        } else if (aValue instanceof String || aValue instanceof CompactClob || aValue instanceof Clob) {
                            try {
                                String s = null;
                                if (aValue instanceof String) {
                                    s = (String) aValue;
                                } else if (aValue instanceof CompactClob) {
                                    s = ((CompactClob) aValue).getData();
                                } else {
                                    s = ((Clob) aValue).getSubString(1, (int) ((Clob) aValue).length());
                                }
                                castedBoolean = !s.isEmpty();
                            } catch (SQLException ex) {
                                throw new RowsetException(ex);
                            }
                        } else if (aValue instanceof Boolean) {
                            castedBoolean = (Boolean) aValue;
                        } else if (aValue instanceof Date) {
                            castedBoolean = !((Date) aValue).equals(new Date(0));
                        }
                        if (castedBoolean == null) {
                            checkDataLoss(aValue);
                        }
                        return castedBoolean;
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
                            castedDate = new Date(Long.valueOf(((Boolean) aValue) ? 1 : 0));
                        } else if (aValue instanceof Date) {
                            castedDate = ((Date) aValue);
                        }
                        if (castedDate != null) {
                            if (aTypeInfo.getSqlType() == Types.DATE) {
                                return new java.sql.Date(castedDate.getTime());
                            } else if (aTypeInfo.getSqlType() == Types.TIMESTAMP) {
                                return new java.sql.Timestamp(castedDate.getTime());
                            } else if (aTypeInfo.getSqlType() == Types.TIME) {
                                return new java.sql.Time(castedDate.getTime());
                            } else {
                                assert false;
                            }
                        } else {
                            checkDataLoss(aValue);
                            return null;
                        }
                    default: {
                        checkDataLoss(aValue);
                        return null;
                    }
                }
            } else {
                checkDataLoss(aValue);
                return null;
            }
        } catch (SQLException ex) {
            throw new RowsetException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void convert2JdbcAndAssign(Object aValue, DataTypeInfo aTypeInfo, Connection aConn, int aParameterIndex, PreparedStatement aStmt) throws RowsetException {
        try {
            aValue = convert2JdbcCompatible(aValue, aTypeInfo);
            if (aValue != null) {
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
                            aStmt.setNull(aParameterIndex, aTypeInfo.getSqlType());
                        } catch (Exception ex) {
                            aStmt.setNull(aParameterIndex, aTypeInfo.getSqlType(), aTypeInfo.getSqlTypeName());
                        }
                        checkDataLoss(aValue);
                        break;
                    case Types.BINARY:
                    case Types.VARBINARY:
                    case Types.LONGVARBINARY:
                        // target type - byte[]
                        if (aValue instanceof byte[]) {
                            aStmt.setBytes(aParameterIndex, (byte[]) aValue);
                        } else if (aValue instanceof CompactBlob) {
                            aStmt.setBytes(aParameterIndex, ((CompactBlob) aValue).getData());
                        }
                        break;
                    case Types.BLOB:
                        // target type - java.sql.Blob
                        if (aValue instanceof Blob) {
                            aStmt.setBlob(aParameterIndex, (Blob) aValue);
                        }
                        break;
                    case Types.CLOB:
                        // target type - java.sql.Clob
                        if (aValue instanceof Clob) {
                            aStmt.setClob(aParameterIndex, (Clob) aValue);
                        }
                        break;
                    case Types.NCLOB:
                        // target type - java.sql.NClob
                        if (aValue instanceof NClob) {
                            aStmt.setNClob(aParameterIndex, (NClob) aValue);
                        }
                        break;
                    case Types.DECIMAL:
                    case Types.NUMERIC:
                        // target type - BigDecimal
                        BigDecimal castedDecimal = (BigDecimal) aValue;
                        if (castedDecimal != null) {
                            aStmt.setBigDecimal(aParameterIndex, castedDecimal);
                        } else {
                            aStmt.setNull(aParameterIndex, aTypeInfo.getSqlType());
                            checkDataLoss(aValue);
                        }
                        break;
                    case Types.BIGINT:
                        // target type - BigInteger
                        BigInteger castedInteger = (BigInteger) aValue;
                        if (castedInteger != null) {
                            aStmt.setBigDecimal(aParameterIndex, new BigDecimal(castedInteger));
                        } else {
                            aStmt.setNull(aParameterIndex, aTypeInfo.getSqlType());
                            checkDataLoss(aValue);
                        }
                        break;
                    case Types.SMALLINT:
                        // target type - Short
                        Short castedShort = (Short) aValue;
                        if (castedShort != null) {
                            aStmt.setShort(aParameterIndex, castedShort);
                        } else {
                            aStmt.setNull(aParameterIndex, aTypeInfo.getSqlType());
                            checkDataLoss(aValue);
                        }
                        break;
                    case Types.TINYINT:
                    case Types.INTEGER:
                        // target type - Integer
                        Integer castedInt = (Integer) aValue;
                        if (castedInt != null) {
                            aStmt.setInt(aParameterIndex, castedInt);
                        } else {
                            aStmt.setNull(aParameterIndex, aTypeInfo.getSqlType());
                            checkDataLoss(aValue);
                        }
                        break;
                    case Types.REAL:
                    case Types.FLOAT:
                        // target type - Float
                        Float castedFloat = (Float) aValue;
                        if (castedFloat != null) {
                            aStmt.setFloat(aParameterIndex, castedFloat);
                        } else {
                            aStmt.setNull(aParameterIndex, aTypeInfo.getSqlType());
                            checkDataLoss(aValue);
                        }
                        break;
                    case Types.DOUBLE:
                        // target type - Double
                        Double castedDouble = (Double) aValue;
                        if (castedDouble != null) {
                            aStmt.setDouble(aParameterIndex, castedDouble);
                        } else {
                            aStmt.setNull(aParameterIndex, aTypeInfo.getSqlType());
                            checkDataLoss(aValue);
                        }
                        break;
                    case Types.CHAR:
                    case Types.VARCHAR:
                    case Types.LONGVARCHAR:
                    case Types.NCHAR:
                    case Types.NVARCHAR:
                    case Types.LONGNVARCHAR:
                        // target type - string
                        String castedString = (String) aValue;
                        if (castedString != null) {
                            if (aTypeInfo.getSqlType() == Types.NCHAR || aTypeInfo.getSqlType() == Types.NVARCHAR || aTypeInfo.getSqlType() == Types.LONGNVARCHAR) {
                                aStmt.setNString(aParameterIndex, castedString);
                            } else {
                                aStmt.setString(aParameterIndex, castedString);
                            }
                        } else {
                            aStmt.setNull(aParameterIndex, aTypeInfo.getSqlType());
                            checkDataLoss(aValue);
                        }
                        break;
                    case Types.BOOLEAN:
                    case Types.BIT:
                        // target type - Boolean
                        Boolean castedBoolean = (Boolean) aValue;
                        if (castedBoolean != null) {
                            aStmt.setBoolean(aParameterIndex, castedBoolean);
                        } else {
                            aStmt.setNull(aParameterIndex, aTypeInfo.getSqlType());
                            checkDataLoss(aValue);
                        }
                        break;
                    case Types.DATE:
                    case Types.TIMESTAMP:
                    case Types.TIME:
                        // target type - date
                        if (aTypeInfo.getSqlType() == Types.DATE) {
                            aStmt.setDate(aParameterIndex, (java.sql.Date) aValue);
                        } else if (aTypeInfo.getSqlType() == Types.TIMESTAMP) {
                            aStmt.setTimestamp(aParameterIndex, (java.sql.Timestamp) aValue);
                        } else if (aTypeInfo.getSqlType() == Types.TIME) {
                            aStmt.setTime(aParameterIndex, (java.sql.Time) aValue);
                        } else {
                            assert false;
                        }
                        break;
                }
            } else {
                try {
                    aStmt.setNull(aParameterIndex, aTypeInfo.getSqlType());
                    checkDataLoss(aValue);
                } catch (SQLException ex) {
                    aStmt.setNull(aParameterIndex, aTypeInfo.getSqlType(), aTypeInfo.getSqlTypeName());
                }
            }
        } catch (SQLException ex) {
            throw new RowsetException(ex);
        }
    }

    protected void checkDataLoss(Object aValue) {
        if (aValue != null) {
            Logger.getLogger(RowsetConverter.class.getName()).log(Level.WARNING, "Some value falled to null while tranferring to a database. May be it''s class in unsupported: {0}", aValue.getClass().getName());
        }
    }
}
