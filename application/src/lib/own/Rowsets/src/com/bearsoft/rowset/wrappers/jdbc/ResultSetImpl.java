/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.wrappers.jdbc;

import com.bearsoft.rowset.utils.RowsetUtils;
import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.compacts.CompactBlob;
import com.bearsoft.rowset.compacts.CompactClob;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * This class is Jdbc wrapper for Rowset instance.
 * @author mg
 */
public class ResultSetImpl implements ResultSet {

    protected Rowset delegate;
    protected Converter converter;
    protected boolean wasNull = false;

    /**
     * Simple constructor. Accepts a Rowset parameter being used for delegating.
     * @param aRowset Delagate rowset.
     */
    public ResultSetImpl(Rowset aRowset, Converter aConverter) {
        super();
        delegate = aRowset;
        converter = aConverter;
    }

    /**
     * @inheritDoc
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) {
            return (T) delegate;
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(Rowset.class);
    }

    /**
     * @inheritDoc
     */
    public void close() throws SQLException {
    }

    /**
     * @inheritDoc
     */
    public boolean wasNull() throws SQLException {
        return wasNull;
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @inheritDoc
     */
    public String getString(int columnIndex) throws SQLException {
        try {
            String value = (String) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.VARCHAR);
            wasNull = value == null;
            return value;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public String getNString(int columnIndex) throws SQLException {
        try {
            String value = (String) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.NVARCHAR);
            wasNull = value == null;
            return value;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public boolean getBoolean(int columnIndex) throws SQLException {
        try {
            Boolean value = (Boolean) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.BOOLEAN);
            wasNull = value == null;
            if (value != null) {
                return value;
            } else {
                return false;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public byte getByte(int columnIndex) throws SQLException {
        try {
            Short value = (Short) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.SMALLINT);
            wasNull = value == null;
            if (value != null) {
                return value.byteValue();
            } else {
                return 0;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public short getShort(int columnIndex) throws SQLException {
        try {
            Short value = (Short) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.SMALLINT);
            wasNull = value == null;
            if (value != null) {
                return value;
            } else {
                return 0;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public int getInt(int columnIndex) throws SQLException {
        try {
            Integer value = (Integer) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.INTEGER);
            wasNull = value == null;
            if (value != null) {
                return value;
            } else {
                return 0;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public long getLong(int columnIndex) throws SQLException {
        try {
            BigInteger value = (BigInteger) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.BIGINT);
            wasNull = value == null;
            if (value != null) {
                return value.longValue();
            } else {
                return 0;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public float getFloat(int columnIndex) throws SQLException {
        try {
            Float value = (Float) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.FLOAT);
            wasNull = value == null;
            if (value != null) {
                return value;
            } else {
                return 0.0f;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public double getDouble(int columnIndex) throws SQLException {
        try {
            Double value = (Double) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.DOUBLE);
            wasNull = value == null;
            if (value != null) {
                return value;
            } else {
                return 0.0f;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        try {
            BigDecimal value = (BigDecimal) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.DECIMAL);
            wasNull = value == null;
            return value;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public byte[] getBytes(int columnIndex) throws SQLException {
        try {
            Blob value = (Blob) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.BLOB);
            wasNull = value == null;
            if (value != null) {
                return value.getBytes(1, (int) value.length());
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public Date getDate(int columnIndex) throws SQLException {
        try {
            Date value = (Date) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.DATE);
            wasNull = value == null;
            return value;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public Time getTime(int columnIndex) throws SQLException {
        try {
            Time value = (Time) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.TIME);
            wasNull = value == null;
            return value;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        try {
            Timestamp value = (Timestamp) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.TIMESTAMP);
            wasNull = value == null;
            return value;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        try {
            Clob value = (Clob) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.CLOB);
            wasNull = value == null;
            if (value != null) {
                return value.getAsciiStream();
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        try {
            Clob value = (Clob) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.CLOB);
            wasNull = value == null;
            if (value != null) {
                return value.getAsciiStream();
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        try {
            Blob value = (Blob) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.BLOB);
            wasNull = value == null;
            if (value != null) {
                return value.getBinaryStream();
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        try {
            BigDecimal value = (BigDecimal) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.DECIMAL);
            wasNull = value == null;
            return value;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        try {
            Clob value = (Clob) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.CLOB);
            wasNull = value == null;
            if (value != null) {
                return value.getCharacterStream();
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public Object getObject(int columnIndex) throws SQLException {
        try {
            Object value = converter.convert2JdbcCompatible(delegate.getObject(columnIndex), delegate.getFields().get(columnIndex).getTypeInfo());
            wasNull = value == null;
            return value;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return getObject(columnIndex);
    }

    /**
     * @inheritDoc
     */
    public Blob getBlob(int columnIndex) throws SQLException {
        try {
            Blob value = (Blob) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.BLOB);
            wasNull = value == null;
            return value;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public Clob getClob(int columnIndex) throws SQLException {
        try {
            Clob value = (Clob) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.CLOB);
            wasNull = value == null;
            return value;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public NClob getNClob(int columnIndex) throws SQLException {
        try {
            NClob value = (NClob) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.NCLOB);
            wasNull = value == null;
            return value;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return getDate(columnIndex);
    }

    /**
     * @inheritDoc
     */
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return getTime(columnIndex);
    }

    /**
     * @inheritDoc
     */
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return getTimestamp(columnIndex);
    }

    /**
     * @inheritDoc
     */
    public Ref getRef(int columnIndex) throws SQLException {
        try {
            Ref value = (Ref) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.REF);
            wasNull = value == null;
            return value;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public Array getArray(int columnIndex) throws SQLException {
        try {
            Array value = (Array) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.ARRAY);
            wasNull = value == null;
            return value;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public URL getURL(int columnIndex) throws SQLException {
        try {
            String value = (String) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.VARCHAR);
            wasNull = value == null;
            return value != null ? new URL(value) : null;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public RowId getRowId(int columnIndex) throws SQLException {
        try {
            RowId value = (RowId) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.ROWID);
            wasNull = value == null;
            return value;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        try {
            SQLXML value = (SQLXML) converter.convert2JdbcCompatible(delegate.getObject(columnIndex), DataTypeInfo.SQLXML);
            wasNull = value == null;
            return value;
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return getCharacterStream(columnIndex);
    }

    /**
     * @inheritDoc
     */
    public NClob getNClob(String columnLabel) throws SQLException {
        return getNClob(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return getSQLXML(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public String getString(String columnLabel) throws SQLException {
        return getString(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public boolean getBoolean(String columnLabel) throws SQLException {
        return getBoolean(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public byte getByte(String columnLabel) throws SQLException {
        return getByte(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public short getShort(String columnLabel) throws SQLException {
        return getShort(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public int getInt(String columnLabel) throws SQLException {
        return getInt(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public long getLong(String columnLabel) throws SQLException {
        return getLong(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public float getFloat(String columnLabel) throws SQLException {
        return getFloat(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public double getDouble(String columnLabel) throws SQLException {
        return getDouble(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return getBigDecimal(findColumn(columnLabel), scale);
    }

    /**
     * @inheritDoc
     */
    public byte[] getBytes(String columnLabel) throws SQLException {
        return getBytes(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public Date getDate(String columnLabel) throws SQLException {
        return getDate(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public Time getTime(String columnLabel) throws SQLException {
        return getTime(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getTimestamp(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return getAsciiStream(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return getUnicodeStream(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return getBinaryStream(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public Object getObject(String columnLabel) throws SQLException {
        return getObject(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return getObject(findColumn(columnLabel), map);
    }

    /**
     * @inheritDoc
     */
    public Ref getRef(String columnLabel) throws SQLException {
        return getRef(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public Blob getBlob(String columnLabel) throws SQLException {
        return getBlob(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public Clob getClob(String columnLabel) throws SQLException {
        return getClob(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public Array getArray(String columnLabel) throws SQLException {
        return getArray(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return getDate(findColumn(columnLabel), cal);
    }

    /**
     * @inheritDoc
     */
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return getTime(findColumn(columnLabel), cal);
    }

    /**
     * @inheritDoc
     */
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return getTimestamp(findColumn(columnLabel), cal);
    }

    /**
     * @inheritDoc
     */
    public RowId getRowId(String columnLabel) throws SQLException {
        return getRowId(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public URL getURL(String columnLabel) throws SQLException {
        return getURL(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return getCharacterStream(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return getBigDecimal(findColumn(columnLabel));
    }

    // Updating interface
    /**
     * @inheritDoc
     */
    public void updateNull(int columnIndex) throws SQLException {
        try {
            delegate.getCurrentRow().setColumnObject(columnIndex, null);
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateByte(int columnIndex, byte x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateShort(int columnIndex, short x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateInt(int columnIndex, int x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateLong(int columnIndex, long x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateFloat(int columnIndex, float x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateDouble(int columnIndex, double x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateString(int columnIndex, String x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateDate(int columnIndex, Date x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateTime(int columnIndex, Time x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        try {
            byte[] data = RowsetUtils.readStream(x, length);
            CompactClob clob = new CompactClob(data, "us-ascii");
            delegate.getCurrentRow().setColumnObject(columnIndex, clob);
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        try {
            byte[] data = RowsetUtils.readStream(x, length);
            CompactBlob blob = new CompactBlob(data);
            delegate.getCurrentRow().setColumnObject(columnIndex, blob);
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        try {
            String data = RowsetUtils.readReader(x, length);
            CompactClob clob = new CompactClob(data);
            delegate.getCurrentRow().setColumnObject(columnIndex, clob);
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public void updateNString(int columnIndex, String nString) throws SQLException {
        updateObject(columnIndex, nString);
    }

    /**
     * @inheritDoc
     */
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        updateObject(columnIndex, nClob);
    }

    /**
     * @inheritDoc
     */
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateArray(int columnIndex, Array x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateObject(int columnIndex, Object x) throws SQLException {
        try {
            delegate.getCurrentRow().setColumnObject(columnIndex, x);
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        updateObject(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        updateObject(columnIndex, xmlObject);
    }

    /**
     * @inheritDoc
     */
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        updateBinaryStream(columnIndex, inputStream, length);
    }

    /**
     * @inheritDoc
     */
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        updateCharacterStream(columnIndex, reader, length);
    }

    /**
     * @inheritDoc
     */
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        updateClob(columnIndex, reader, length);
    }

    /**
     * @inheritDoc
     */
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        updateBlob(columnIndex, inputStream, -1);
    }

    /**
     * @inheritDoc
     */
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        updateClob(columnIndex, reader, -1);
    }

    /**
     * @inheritDoc
     */
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        updateClob(columnIndex, reader);
    }

    /**
     * @inheritDoc
     */
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        updateCharacterStream(columnIndex, x, length);
    }

    /**
     * @inheritDoc
     */
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        updateAsciiStream(columnIndex, x, (int) length);
    }

    /**
     * @inheritDoc
     */
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        updateBinaryStream(columnIndex, x, (int) length);
    }

    /**
     * @inheritDoc
     */
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        updateCharacterStream(columnIndex, x, (int) length);
    }

    /**
     * @inheritDoc
     */
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        updateCharacterStream(columnIndex, x);
    }

    /**
     * @inheritDoc
     */
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        updateAsciiStream(columnIndex, x, -1);
    }

    /**
     * @inheritDoc
     */
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        updateBinaryStream(columnIndex, x, -1);
    }

    /**
     * @inheritDoc
     */
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        updateCharacterStream(columnIndex, x, -1);
    }

    /**
     * @inheritDoc
     */
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        updateNCharacterStream(findColumn(columnLabel), reader);
    }

    /**
     * @inheritDoc
     */
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        updateSQLXML(findColumn(columnLabel), xmlObject);
    }

    /**
     * @inheritDoc
     */
    public String getNString(String columnLabel) throws SQLException {
        return getNString(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return getNCharacterStream(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        updateNCharacterStream(findColumn(columnLabel), reader, length);
    }

    /**
     * @inheritDoc
     */
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        updateAsciiStream(findColumn(columnLabel), x, length);
    }

    /**
     * @inheritDoc
     */
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        updateBinaryStream(findColumn(columnLabel), x, length);
    }

    /**
     * @inheritDoc
     */
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        updateCharacterStream(findColumn(columnLabel), reader, length);
    }

    /**
     * @inheritDoc
     */
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        updateBlob(findColumn(columnLabel), inputStream, length);
    }

    /**
     * @inheritDoc
     */
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        updateClob(findColumn(columnLabel), reader, length);
    }

    /**
     * @inheritDoc
     */
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        updateNClob(findColumn(columnLabel), reader, length);
    }

    /**
     * @inheritDoc
     */
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        updateAsciiStream(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        updateBinaryStream(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        updateCharacterStream(findColumn(columnLabel), reader);
    }

    /**
     * @inheritDoc
     */
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        updateBlob(findColumn(columnLabel), inputStream);
    }

    /**
     * @inheritDoc
     */
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        updateClob(findColumn(columnLabel), reader);
    }

    /**
     * @inheritDoc
     */
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        updateNClob(findColumn(columnLabel), reader);
    }

    /**
     * @inheritDoc
     */
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        updateRowId(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateNull(String columnLabel) throws SQLException {
        updateNull(findColumn(columnLabel));
    }

    /**
     * @inheritDoc
     */
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        updateBoolean(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateByte(String columnLabel, byte x) throws SQLException {
        updateByte(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateShort(String columnLabel, short x) throws SQLException {
        updateShort(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateInt(String columnLabel, int x) throws SQLException {
        updateInt(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateLong(String columnLabel, long x) throws SQLException {
        updateLong(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateFloat(String columnLabel, float x) throws SQLException {
        updateFloat(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateDouble(String columnLabel, double x) throws SQLException {
        updateDouble(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        updateBigDecimal(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateString(String columnLabel, String x) throws SQLException {
        updateString(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        updateBytes(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateDate(String columnLabel, Date x) throws SQLException {
        updateDate(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateTime(String columnLabel, Time x) throws SQLException {
        updateTime(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        updateTimestamp(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        updateAsciiStream(findColumn(columnLabel), x, length);
    }

    /**
     * @inheritDoc
     */
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        updateBinaryStream(findColumn(columnLabel), x, length);
    }

    /**
     * @inheritDoc
     */
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        updateCharacterStream(findColumn(columnLabel), reader, length);
    }

    /**
     * @inheritDoc
     */
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        updateRef(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        updateBlob(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        updateClob(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateArray(String columnLabel, Array x) throws SQLException {
        updateArray(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        updateObject(findColumn(columnLabel), x, scaleOrLength);
    }

    /**
     * @inheritDoc
     */
    public void updateObject(String columnLabel, Object x) throws SQLException {
        updateObject(findColumn(columnLabel), x);
    }

    /**
     * @inheritDoc
     */
    public void updateNString(String columnLabel, String nString) throws SQLException {
        updateNString(findColumn(columnLabel), nString);
    }

    /**
     * @inheritDoc
     */
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        updateNClob(findColumn(columnLabel), nClob);
    }

    // Processing interface
    /**
     * @inheritDoc
     */
    public void insertRow() throws SQLException {
        try {
            delegate.insert(new Row(delegate.getFlowProvider().getEntityId(), delegate.getFields()), false);
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public void updateRow() throws SQLException {
    }

    /**
     * @inheritDoc
     */
    public void deleteRow() throws SQLException {
        try {
            delegate.delete();
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public void refreshRow() throws SQLException {
    }

    /**
     * @inheritDoc
     */
    public void cancelRowUpdates() throws SQLException {
        try {
            delegate.originalToCurrent();
            delegate.getRowsetChangeSupport().fireRolledbackEvent();
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public void moveToInsertRow() throws SQLException {
    }

    /**
     * @inheritDoc
     */
    public void moveToCurrentRow() throws SQLException {
    }

    /**
     * @inheritDoc
     */
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    /**
     * @inheritDoc
     */
    public void clearWarnings() throws SQLException {
    }

    /**
     * @inheritDoc
     */
    public String getCursorName() throws SQLException {
        return "Rowset wrapper cursor";
    }

    /**
     * @inheritDoc
     */
    public boolean rowUpdated() throws SQLException {
        return delegate.getCurrentRow().isUpdated();
    }

    /**
     * @inheritDoc
     */
    public boolean rowInserted() throws SQLException {
        return delegate.getCurrentRow().isInserted();
    }

    /**
     * @inheritDoc
     */
    public boolean rowDeleted() throws SQLException {
        return delegate.getCurrentRow().isDeleted();
    }

    /**
     * @inheritDoc
     */
    public boolean isClosed() throws SQLException {
        return false;
    }

    // Metadata interface
    /**
     * @inheritDoc
     */
    public int findColumn(String columnLabel) throws SQLException {
        return delegate.getFields().find(columnLabel);
    }

    /**
     * @inheritDoc
     */
    public ResultSetMetaData getMetaData() throws SQLException {
        return new ResultSetMetaDataImpl(delegate.getFields());
    }

    /**
     * @inheritDoc
     */
    public Statement getStatement() throws SQLException {
        return null;
    }

    /**
     * @inheritDoc
     */
    public void setFetchDirection(int direction) throws SQLException {
    }

    /**
     * @inheritDoc
     */
    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    /**
     * @inheritDoc
     */
    public void setFetchSize(int rows) throws SQLException {
    }

    /**
     * @inheritDoc
     */
    public int getFetchSize() throws SQLException {
        return delegate.size();
    }

    /**
     * @inheritDoc
     */
    public int getType() throws SQLException {
        return ResultSet.TYPE_SCROLL_INSENSITIVE;
    }

    /**
     * @inheritDoc
     */
    public int getConcurrency() throws SQLException {
        return ResultSet.CONCUR_UPDATABLE;
    }

    /**
     * @inheritDoc
     */
    public int getHoldability() throws SQLException {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    // Scroll interface
    /**
     * @inheritDoc
     */
    public boolean isBeforeFirst() throws SQLException {
        return delegate.isBeforeFirst();
    }

    /**
     * @inheritDoc
     */
    public boolean isAfterLast() throws SQLException {
        return delegate.isAfterLast();
    }

    /**
     * @inheritDoc
     */
    public boolean isFirst() throws SQLException {
        return !delegate.isEmpty() && delegate.getCursorPos() == 1;
    }

    /**
     * @inheritDoc
     */
    public boolean isLast() throws SQLException {
        return !delegate.isEmpty() && delegate.getCursorPos() == delegate.size();
    }

    /**
     * @inheritDoc
     */
    public void beforeFirst() throws SQLException {
        try {
            delegate.beforeFirst();
        } catch (InvalidCursorPositionException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public void afterLast() throws SQLException {
        try {
            delegate.afterLast();
        } catch (InvalidCursorPositionException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public boolean first() throws SQLException {
        try {
            return delegate.first();
        } catch (InvalidCursorPositionException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public boolean last() throws SQLException {
        try {
            return delegate.last();
        } catch (InvalidCursorPositionException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public boolean next() throws SQLException {
        try {
            return delegate.next();
        } catch (InvalidCursorPositionException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public boolean absolute(int row) throws SQLException {
        try {
            return delegate.absolute(row);
        } catch (InvalidCursorPositionException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public boolean relative(int rows) throws SQLException {
        try {
            return delegate.absolute(delegate.getCursorPos() + rows);
        } catch (InvalidCursorPositionException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public boolean previous() throws SQLException {
        try {
            return delegate.previous();
        } catch (InvalidCursorPositionException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public int getRow() throws SQLException {
        return delegate.getCursorPos();
    }
}
