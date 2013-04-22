/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.wrappers.jdbc;

import com.bearsoft.rowset.compacts.CompactClob;
import com.bearsoft.rowset.exceptions.RowsetException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * JDBC Wrapper for CompactClob
 * @author mg
 */
public class ClobImpl implements Clob {

    protected CompactClob clob = null;

    /**
     * JDBC Wrapper constructor.
     * @param aClob Clob to be wrapped
     */
    public ClobImpl(CompactClob aClob) {
        super();
        clob = aClob;
    }

    private void checkClob() throws SQLException {
        if (clob == null) {
            throw new SQLException("Wrapped (actual) clob instance is missing");
        }
    }

    /**
     * @inheritDoc
     */
    public long length() throws SQLException {
        checkClob();
        return clob.length();
    }

    /**
     * @inheritDoc
     */
    public String getSubString(long pos, int length) throws SQLException {
        checkClob();
        try {
            return clob.getSubString(pos, length);
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public Reader getCharacterStream() throws SQLException {
        checkClob();
        try {
            return clob.getCharacterStream();
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public Reader getCharacterStream(long pos, long length) throws SQLException {
        checkClob();
        try {
            return clob.getCharacterStream(pos, length);
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public InputStream getAsciiStream() throws SQLException {
        checkClob();
        try {
            return clob.getAsciiStream();
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public long position(String searchStr, long start) throws SQLException {
        checkClob();
        try {
            return clob.position(searchStr, start);
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public long position(Clob searchStr, long start) throws SQLException {
        return position(searchStr.getSubString(1, (int) searchStr.length()), start);
    }

    /**
     * @inheritDoc
     */
    public int setString(long pos, String str) throws SQLException {
        return setString(pos, str, 0, str.length());
    }

    /**
     * @inheritDoc
     */
    public int setString(long pos, String str, int offset, int len) throws SQLException {
        checkClob();
        try {
            return clob.setString(pos, str, offset, len);
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public OutputStream setAsciiStream(long pos) throws SQLException {
        checkClob();
        try {
            return clob.setAsciiStream(pos);
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public Writer setCharacterStream(long pos) throws SQLException {
        checkClob();
        try {
            return clob.setCharacterStream(pos);
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public void truncate(long len) throws SQLException {
        checkClob();
        try {
            clob.truncate(len);
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public void free() throws SQLException {
        clob = null;
    }
}
