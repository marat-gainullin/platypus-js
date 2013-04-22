/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.compacts;

import com.bearsoft.rowset.exceptions.RowsetException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * CompactClob objects hold compact, not streamed, not structured string data.
 * @author mg
 */
public class CompactClob {

    protected String data;

    /**
     * Default constructor
     */
    public CompactClob() {
        super();
    }

    /**
     * Constructor for creating <code>CompactClob</code> object based on existing string data.
     * This would copy the <code>aData</code> data.
     * @param aData A data the new object <code>CompactClob</code> will be based on
     */
    public CompactClob(String aData) {
        this();
        data = aData;
    }

    /**
     * Constructor for creating <code>CompactClob</code> object based on existing string data.
     * This would copy the <code>aData</code> data.
     * @param aData A data the new object <code>CompactClob</code> will be based on
     * @param aCoding A charset name.
     * @throws UnsupportedEncodingException
     */
    public CompactClob(byte[] aData, String aCoding) throws UnsupportedEncodingException {
        super();
        data = new String(aData, aCoding);
    }

    /**
     * Copy constructor
     * @param aSource <code>CompactClob</code> object the new <code>CompactClob</code> will be based on.
     */
    public CompactClob(CompactClob aSource) {
        super();
        assign(aSource);
    }

    /**
     * Assign method.
     * @param aSource
     */
    protected void assign(CompactClob aSource) {
        if (aSource.getData() != null) {
            data = new String(aSource.getData().toCharArray());
        }
    }

    /**
     * Returns contained string data.
     * @return String data, contained in this object.
     */
    public String getData() {
        return data;
    }

    /**
     * Sets string data to this object.
     * @param data String data to be set to this object.
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Returns contained data.
     * @param aCodingName Name of charset to be used to convert string data to plain byte array.
     * @return Plain byte array.
     * @throws UnsupportedEncodingException
     */
    public byte[] getData(String aCodingName) throws UnsupportedEncodingException {
        return data.getBytes(aCodingName);
    }

    /**
     * Sets data to this <code>CompactClob</code> object from passed byte array, according to passed charset name
     * @param aData Byte array to be setted as data.
     * @param aCodingName Charset name, to be used to convert byte array to string
     * @throws UnsupportedEncodingException
     */
    public void setData(byte[] aData, String aCodingName) throws UnsupportedEncodingException {
        data = new String(aData, aCodingName);
    }

    /**
     * Returns copy of this <code>CompactClob</code> object.
     * @return Copy of this <code>CompactClob</code> object.
     */
    public CompactClob copy() {
        CompactClob cc = new CompactClob(this);
        return cc;
    }

    public int length() {
        return data != null ? data.length() : 0;
    }

    public boolean isEmpty()
    {
        return data == null || data.isEmpty();
    }

    public String getSubString(long pos, int length) throws RowsetException {
        return data.substring((int) pos - 1, (int) pos - 1 + length);
    }

    public Reader getCharacterStream() throws RowsetException {
        if (isEmpty()) {
            return new StringReader("");
        } else {
            return new StringReader(getData());
        }
    }

    public Reader getCharacterStream(long pos, long length) throws RowsetException {
        if (isEmpty()) {
            return new StringReader("");
        } else {
            return new StringReader(getSubString(pos, (int) length));
        }
    }

    public InputStream getAsciiStream() throws RowsetException {
        try {
            if (isEmpty()) {
                return new ByteArrayInputStream(new byte[0]);
            }
            return new ByteArrayInputStream(data.getBytes("us-ascii"));
        } catch (UnsupportedEncodingException ex) {
            throw new RowsetException(ex);
        }
    }

    public long position(String searchstr, long start) throws RowsetException {
        if (!isEmpty()) {
            int res = data.indexOf(searchstr, (int) start - 1);
            if (res != -1) {
                return res + 1;
            } else {
                return -1;
            }
        }
        return -1;
    }

    public int setString(long pos, String str) throws RowsetException {
        return setString(pos, str, 0, str.length());
    }

    public int setString(long pos, String str, int offset, int len) throws RowsetException {
        if (str != null) {
            if (offset >= 0 && offset < str.length()) {
                if (offset + len >= 0 && offset + len <= str.length()) {
                    if (pos - 1 >= 0 && pos - 1 < data.length()) {
                        int secondChunkLength = Math.max(0, data.length() - len - (int) pos + 1);

                        String firstChunk = data.substring(0, (int) pos - 1);
                        String resData = firstChunk;
                        resData += str.substring(offset, offset + len);
                        if (secondChunkLength > 0) {
                            resData += data.substring((int) pos - 1 + len, (int) pos - 1 + len + secondChunkLength);
                        }
                        setData(resData);
                        return len;
                    } else {
                        throw new RowsetException("Bad parameters. Clob position must be in range [1;length()]");
                    }
                } else {
                    throw new RowsetException("Bad parameters. The value of (offset+len) must be in range [0;<source string>.length]");
                }
            } else {
                throw new RowsetException("Bad parameters. offset must be in range [0;<source string>.length-1]");
            }
        } else {
            throw new RowsetException("Bad parameters. Source string must be non null.");
        }
    }

    public OutputStream setAsciiStream(long pos) throws RowsetException {
        try {
            return new CompactClobAsciiOutputStream(this, data.substring(0, (int) pos - 1).getBytes("us-ascii"));
        } catch (Exception ex) {
            throw new RowsetException(ex);
        }
    }

    public Writer setCharacterStream(long pos) throws RowsetException {
        if (!isEmpty()) {
            if (pos >= 1 && pos <= length() + 1) {
                try {
                    return new CompactClobWriter(this, data.substring(0, (int) pos - 1));
                } catch (Exception ex) {
                    throw new RowsetException(ex);
                }
            } else {
                throw new RowsetException("Position must lie in range [1;length()+1]");
            }
        } else {
            return new CompactClobWriter(this);
        }
    }

    public void truncate(long len) throws RowsetException {
        if (!isEmpty()) {
            setData(data.substring(0, (int) len));
        }
    }
}