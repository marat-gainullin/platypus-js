/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.resourcepool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 *
 * @author mg
 */
public class BearCallableStatement extends BearPreparedStatement implements CallableStatement {

    public BearCallableStatement(String aSqlClause, CallableStatement aDelegate, BearDatabaseConnection aOwningConnection) {
        super(aSqlClause, aDelegate, aOwningConnection);
    }

    @Override
    public void close() throws SQLException {
        checkClosed();
        owningConnection.returnCallableStatement(sqlClause, (CallableStatement) delegate);
        delegate = null;
    }

    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).registerOutParameter(parameterIndex, sqlType);
    }

    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).registerOutParameter(parameterIndex, sqlType, scale);
    }

    public boolean wasNull() throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).wasNull();
    }

    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getString(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getString(parameterIndex);
    }

    public boolean getBoolean(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBoolean(parameterIndex);
    }

    public byte getByte(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getByte(parameterIndex);
    }

    public short getShort(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getShort(parameterIndex);
    }

    public int getInt(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getInt(parameterIndex);
    }

    public long getLong(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getLong(parameterIndex);
    }

    public float getFloat(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getFloat(parameterIndex);
    }

    public double getDouble(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getDouble(parameterIndex);
    }

    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBigDecimal(parameterIndex, scale);
    }

    public byte[] getBytes(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBytes(parameterIndex);
    }

    public Date getDate(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getDate(parameterIndex);
    }

    public Time getTime(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getTime(parameterIndex);
    }

    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getTimestamp(parameterIndex);
    }

    public Object getObject(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getObject(parameterIndex);
    }

    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBigDecimal(parameterIndex);
    }

    public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getObject(parameterIndex, map);
    }

    public Ref getRef(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getRef(parameterIndex);
    }

    public Blob getBlob(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBlob(parameterIndex);
    }

    public Clob getClob(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getClob(parameterIndex);
    }

    public Array getArray(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getArray(parameterIndex);
    }

    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getDate(parameterIndex, cal);
    }

    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getTime(parameterIndex, cal);
    }

    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getTimestamp(parameterIndex, cal);
    }

    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).registerOutParameter(parameterIndex, sqlType, typeName);
    }

    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).registerOutParameter(parameterName, sqlType);
    }

    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).registerOutParameter(parameterName, sqlType, scale);
    }

    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).registerOutParameter(parameterName, sqlType, typeName);
    }

    public URL getURL(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getURL(parameterIndex);
    }

    public void setURL(String parameterName, URL val) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setURL(parameterName, val);
    }

    public void setNull(String parameterName, int sqlType) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setNull(parameterName, sqlType);
    }

    public void setBoolean(String parameterName, boolean x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBoolean(parameterName, x);
    }

    public void setByte(String parameterName, byte x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setByte(parameterName, x);
    }

    public void setShort(String parameterName, short x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setShort(parameterName, x);
    }

    public void setInt(String parameterName, int x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setInt(parameterName, x);
    }

    public void setLong(String parameterName, long x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setLong(parameterName, x);
    }

    public void setFloat(String parameterName, float x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setFloat(parameterName, x);
    }

    public void setDouble(String parameterName, double x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setDouble(parameterName, x);
    }

    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBigDecimal(parameterName, x);
    }

    public void setString(String parameterName, String x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setString(parameterName, x);
    }

    public void setBytes(String parameterName, byte[] x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBytes(parameterName, x);
    }

    public void setDate(String parameterName, Date x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setDate(parameterName, x);
    }

    public void setTime(String parameterName, Time x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setTime(parameterName, x);
    }

    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setTimestamp(parameterName, x);
    }

    public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setAsciiStream(parameterName, x, length);
    }

    public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBinaryStream(parameterName, x, length);
    }

    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setObject(parameterName, x, targetSqlType, scale);
    }

    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setObject(parameterName, x, targetSqlType);
    }

    public void setObject(String parameterName, Object x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setObject(parameterName, x);
    }

    public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setCharacterStream(parameterName, reader, length);
    }

    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setDate(parameterName, x, cal);
    }

    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setTime(parameterName, x, cal);
    }

    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setTimestamp(parameterName, x, cal);
    }

    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setNull(parameterName, sqlType, typeName);
    }

    public String getString(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getString(parameterName);
    }

    public boolean getBoolean(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBoolean(parameterName);
    }

    public byte getByte(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getByte(parameterName);
    }

    public short getShort(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getShort(parameterName);
    }

    public int getInt(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getInt(parameterName);
    }

    public long getLong(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getLong(parameterName);
    }

    public float getFloat(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getFloat(parameterName);
    }

    public double getDouble(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getDouble(parameterName);
    }

    public byte[] getBytes(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBytes(parameterName);
    }

    public Date getDate(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getDate(parameterName);
    }

    public Time getTime(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getTime(parameterName);
    }

    public Timestamp getTimestamp(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getTimestamp(parameterName);
    }

    public Object getObject(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getObject(parameterName);
    }

    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBigDecimal(parameterName);
    }

    public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getObject(parameterName, map);
    }

    public Ref getRef(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getRef(parameterName);
    }

    public Blob getBlob(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBlob(parameterName);
    }

    public Clob getClob(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getClob(parameterName);
    }

    public Array getArray(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getArray(parameterName);
    }

    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getDate(parameterName, cal);
    }

    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getTime(parameterName, cal);
    }

    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getTimestamp(parameterName, cal);
    }

    public URL getURL(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getURL(parameterName);
    }

    public RowId getRowId(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getRowId(parameterIndex);
    }

    public RowId getRowId(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getRowId(parameterName);
    }

    public void setRowId(String parameterName, RowId x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setRowId(parameterName, x);
    }

    public void setNString(String parameterName, String value) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setNString(parameterName, value);
    }

    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setNCharacterStream(parameterName, value, length);
    }

    public void setNClob(String parameterName, NClob value) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setNClob(parameterName, value);
    }

    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setClob(parameterName, reader, length);
    }

    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBlob(parameterName, inputStream, length);
    }

    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setNClob(parameterName, reader, length);
    }

    public NClob getNClob(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getNClob(parameterIndex);
    }

    public NClob getNClob(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getNClob(parameterName);
    }

    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setSQLXML(parameterName, xmlObject);
    }

    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getSQLXML(parameterIndex);
    }

    public SQLXML getSQLXML(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getSQLXML(parameterName);
    }

    public String getNString(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getNString(parameterIndex);
    }

    public String getNString(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getNString(parameterName);
    }

    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getNCharacterStream(parameterIndex);
    }

    public Reader getNCharacterStream(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getNCharacterStream(parameterName);
    }

    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getCharacterStream(parameterIndex);
    }

    public Reader getCharacterStream(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getCharacterStream(parameterName);
    }

    public void setBlob(String parameterName, Blob x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBlob(parameterName, x);
    }

    public void setClob(String parameterName, Clob x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setClob(parameterName, x);
    }

    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setAsciiStream(parameterName, x, length);
    }

    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBinaryStream(parameterName, x, length);
    }

    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setCharacterStream(parameterName, reader, length);
    }

    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setAsciiStream(parameterName, x);
    }

    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBinaryStream(parameterName, x);
    }

    public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setCharacterStream(parameterName, reader);
    }

    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setNCharacterStream(parameterName, value);
    }

    public void setClob(String parameterName, Reader reader) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setClob(parameterName, reader);
    }

    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBlob(parameterName, inputStream);
    }

    public void setNClob(String parameterName, Reader reader) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setNClob(parameterName, reader);
    }
}
