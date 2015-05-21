/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.resourcepool;

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

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).registerOutParameter(parameterIndex, sqlType);
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).registerOutParameter(parameterIndex, sqlType, scale);
    }

    @Override
    public boolean wasNull() throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).wasNull();
    }

    @Override
    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getString(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getString(parameterIndex);
    }

    @Override
    public boolean getBoolean(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBoolean(parameterIndex);
    }

    @Override
    public byte getByte(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getByte(parameterIndex);
    }

    @Override
    public short getShort(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getShort(parameterIndex);
    }

    @Override
    public int getInt(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getInt(parameterIndex);
    }

    @Override
    public long getLong(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getLong(parameterIndex);
    }

    @Override
    public float getFloat(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getFloat(parameterIndex);
    }

    @Override
    public double getDouble(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getDouble(parameterIndex);
    }

    @Override
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBigDecimal(parameterIndex, scale);
    }

    @Override
    public byte[] getBytes(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBytes(parameterIndex);
    }

    @Override
    public Date getDate(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getDate(parameterIndex);
    }

    @Override
    public Time getTime(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getTime(parameterIndex);
    }

    @Override
    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getTimestamp(parameterIndex);
    }

    @Override
    public Object getObject(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getObject(parameterIndex);
    }

    @Override
    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBigDecimal(parameterIndex);
    }

    @Override
    public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getObject(parameterIndex, map);
    }

    @Override
    public Ref getRef(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getRef(parameterIndex);
    }

    @Override
    public Blob getBlob(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBlob(parameterIndex);
    }

    @Override
    public Clob getClob(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getClob(parameterIndex);
    }

    @Override
    public Array getArray(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getArray(parameterIndex);
    }

    @Override
    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getDate(parameterIndex, cal);
    }

    @Override
    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getTime(parameterIndex, cal);
    }

    @Override
    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getTimestamp(parameterIndex, cal);
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).registerOutParameter(parameterIndex, sqlType, typeName);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).registerOutParameter(parameterName, sqlType);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).registerOutParameter(parameterName, sqlType, scale);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).registerOutParameter(parameterName, sqlType, typeName);
    }

    @Override
    public URL getURL(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getURL(parameterIndex);
    }

    @Override
    public void setURL(String parameterName, URL val) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setURL(parameterName, val);
    }

    @Override
    public void setNull(String parameterName, int sqlType) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setNull(parameterName, sqlType);
    }

    @Override
    public void setBoolean(String parameterName, boolean x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBoolean(parameterName, x);
    }

    @Override
    public void setByte(String parameterName, byte x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setByte(parameterName, x);
    }

    @Override
    public void setShort(String parameterName, short x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setShort(parameterName, x);
    }

    @Override
    public void setInt(String parameterName, int x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setInt(parameterName, x);
    }

    @Override
    public void setLong(String parameterName, long x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setLong(parameterName, x);
    }

    @Override
    public void setFloat(String parameterName, float x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setFloat(parameterName, x);
    }

    @Override
    public void setDouble(String parameterName, double x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setDouble(parameterName, x);
    }

    @Override
    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBigDecimal(parameterName, x);
    }

    @Override
    public void setString(String parameterName, String x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setString(parameterName, x);
    }

    @Override
    public void setBytes(String parameterName, byte[] x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBytes(parameterName, x);
    }

    @Override
    public void setDate(String parameterName, Date x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setDate(parameterName, x);
    }

    @Override
    public void setTime(String parameterName, Time x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setTime(parameterName, x);
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setTimestamp(parameterName, x);
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setAsciiStream(parameterName, x, length);
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBinaryStream(parameterName, x, length);
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setObject(parameterName, x, targetSqlType, scale);
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setObject(parameterName, x, targetSqlType);
    }

    @Override
    public void setObject(String parameterName, Object x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setObject(parameterName, x);
    }

    @Override
    public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setCharacterStream(parameterName, reader, length);
    }

    @Override
    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setDate(parameterName, x, cal);
    }

    @Override
    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setTime(parameterName, x, cal);
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setTimestamp(parameterName, x, cal);
    }

    @Override
    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setNull(parameterName, sqlType, typeName);
    }

    @Override
    public String getString(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getString(parameterName);
    }

    @Override
    public boolean getBoolean(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBoolean(parameterName);
    }

    @Override
    public byte getByte(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getByte(parameterName);
    }

    @Override
    public short getShort(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getShort(parameterName);
    }

    @Override
    public int getInt(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getInt(parameterName);
    }

    @Override
    public long getLong(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getLong(parameterName);
    }

    @Override
    public float getFloat(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getFloat(parameterName);
    }

    @Override
    public double getDouble(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getDouble(parameterName);
    }

    @Override
    public byte[] getBytes(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBytes(parameterName);
    }

    @Override
    public Date getDate(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getDate(parameterName);
    }

    @Override
    public Time getTime(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getTime(parameterName);
    }

    @Override
    public Timestamp getTimestamp(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getTimestamp(parameterName);
    }

    @Override
    public Object getObject(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getObject(parameterName);
    }

    @Override
    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBigDecimal(parameterName);
    }

    @Override
    public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getObject(parameterName, map);
    }

    @Override
    public Ref getRef(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getRef(parameterName);
    }

    @Override
    public Blob getBlob(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getBlob(parameterName);
    }

    @Override
    public Clob getClob(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getClob(parameterName);
    }

    @Override
    public Array getArray(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getArray(parameterName);
    }

    @Override
    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getDate(parameterName, cal);
    }

    @Override
    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getTime(parameterName, cal);
    }

    @Override
    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getTimestamp(parameterName, cal);
    }

    @Override
    public URL getURL(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getURL(parameterName);
    }

    @Override
    public RowId getRowId(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getRowId(parameterIndex);
    }

    @Override
    public RowId getRowId(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getRowId(parameterName);
    }

    @Override
    public void setRowId(String parameterName, RowId x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setRowId(parameterName, x);
    }

    @Override
    public void setNString(String parameterName, String value) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setNString(parameterName, value);
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setNCharacterStream(parameterName, value, length);
    }

    @Override
    public void setNClob(String parameterName, NClob value) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setNClob(parameterName, value);
    }

    @Override
    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setClob(parameterName, reader, length);
    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBlob(parameterName, inputStream, length);
    }

    @Override
    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setNClob(parameterName, reader, length);
    }

    @Override
    public NClob getNClob(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getNClob(parameterIndex);
    }

    @Override
    public NClob getNClob(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getNClob(parameterName);
    }

    @Override
    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setSQLXML(parameterName, xmlObject);
    }

    @Override
    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getSQLXML(parameterIndex);
    }

    @Override
    public SQLXML getSQLXML(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getSQLXML(parameterName);
    }

    @Override
    public String getNString(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getNString(parameterIndex);
    }

    @Override
    public String getNString(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getNString(parameterName);
    }

    @Override
    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getNCharacterStream(parameterIndex);
    }

    @Override
    public Reader getNCharacterStream(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getNCharacterStream(parameterName);
    }

    @Override
    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getCharacterStream(parameterIndex);
    }

    @Override
    public Reader getCharacterStream(String parameterName) throws SQLException {
        checkClosed();
        return ((CallableStatement) delegate).getCharacterStream(parameterName);
    }

    @Override
    public void setBlob(String parameterName, Blob x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBlob(parameterName, x);
    }

    @Override
    public void setClob(String parameterName, Clob x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setClob(parameterName, x);
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setAsciiStream(parameterName, x, length);
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBinaryStream(parameterName, x, length);
    }

    @Override
    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setCharacterStream(parameterName, reader, length);
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setAsciiStream(parameterName, x);
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBinaryStream(parameterName, x);
    }

    @Override
    public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setCharacterStream(parameterName, reader);
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setNCharacterStream(parameterName, value);
    }

    @Override
    public void setClob(String parameterName, Reader reader) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setClob(parameterName, reader);
    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setBlob(parameterName, inputStream);
    }

    @Override
    public void setNClob(String parameterName, Reader reader) throws SQLException {
        checkClosed();
        ((CallableStatement) delegate).setNClob(parameterName, reader);
    }
}
