/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.wrappers.jdbc;

import com.bearsoft.rowset.compacts.CompactBlob;
import com.bearsoft.rowset.exceptions.RowsetException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * JDBC Wrapper for CompactBlob class.
 * @author mg
 */
public class BlobImpl implements Blob {

    protected CompactBlob blob;

    /**
     * JDBC Wrapper Constructor
     * @param aBlob
     */
    public BlobImpl(CompactBlob aBlob) {
        super();
        blob = aBlob;
    }

    private void checkBlob() throws SQLException {
        if (blob == null) {
            throw new SQLException("Wrapped (actual) blob instance is missing");
        }
    }

    /**
     * @inheritDoc
     */
    public long length() throws SQLException {
        checkBlob();
        return blob.length();
    }

    /**
     * @inheritDoc
     */
    public byte[] getBytes(long pos, int length) throws SQLException {
        checkBlob();
        try {
            return blob.getBytes(pos, length);
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public InputStream getBinaryStream() throws SQLException {
        checkBlob();
        try {
            return blob.getBinaryStream();
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public long position(byte[] pattern, long start) throws SQLException {
        checkBlob();
        try {
            return blob.position(pattern, start);
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public long position(Blob pattern, long start) throws SQLException {
        if (pattern != null) {
            byte[] patternData = pattern.getBytes(1, (int) pattern.length());
            return position(patternData, start);
        } else {
            throw new SQLException("Bad parameters. Source pattern blob must be non null.");
        }
    }

    /**
     * @inheritDoc
     */
    public int setBytes(long pos, byte[] bytes) throws SQLException {
        return setBytes(pos, bytes, 0, bytes.length);
    }

    /**
     * @inheritDoc
     */
    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        checkBlob();
        try {
            return blob.setBytes(pos, bytes, offset, len);
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public OutputStream setBinaryStream(long pos) throws SQLException {
        checkBlob();
        try {
            return blob.setBinaryStream(pos);
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public void truncate(long len) throws SQLException {
        checkBlob();
        try {
            blob.truncate(len);
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public void free() throws SQLException {
        blob = null;
    }

    /**
     * @inheritDoc
     */
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        checkBlob();
        try {
            return blob.getBinaryStream(pos, length);
        } catch (RowsetException ex) {
            throw new SQLException(ex);
        }
    }
}
