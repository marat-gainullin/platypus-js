/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.compacts;

import com.bearsoft.rowset.exceptions.RowsetException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * CompactBlob objects hold compact, not streamed, not structured string data.
 * @author mg
 */
public class CompactBlob {

    protected byte[] data;

    /**
     * Default constructor of <code>CompactBlob</code> object.
     */
    public CompactBlob() {
        super();
    }

    /**
     * Constructs the <code>CompactBlob</code> object, based on passed data.
     * @param aData Byte array the new instance of <code>CompactBlob</code> to be based on.
     */
    public CompactBlob(byte[] aData) {
        this();
        if (aData != null) {
            data = Arrays.copyOf(aData, aData.length);
        }
    }

    /**
     * Constructs the <code>CompactBlob</code> object, based on passed instance.
     * @param aSource <code>CompactBlob</code> object, the new instance of <code>CompactBlob</code> to be based on.
     */
    private CompactBlob(CompactBlob aSource) {
        super();
        assign(aSource);
    }

    /**
     * Assigns to this instance data from passed <code>CompactBlob</code> object.
     * @param aSource <code>CompactBlob</code> object, the initializing data ot be used from.
     */
    protected void assign(CompactBlob aSource) {
        if (aSource.getData() != null) {
            data = Arrays.copyOf(aSource.getData(), aSource.getData().length);
        }
    }

    /**
     * Returns contained data.
     * @return Data, contained in this <code>CompactBlob</code> object.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Sets data to this <code>CompactBlob</code> object.
     * @param aData Data to setted.
     */
    public void setData(byte[] aData) {
        data = aData;
    }

    /**
     * Returns copy of this <code>CompactBlob</code> instance.
     * @return opy of this <code>CompactBlob</code> instance.
     */
    public CompactBlob copy() {
        CompactBlob cb = new CompactBlob(this);
        return cb;
    }


    /**
     * @inheritDoc
     */
    public long length() {
        return getData() == null ? 0 : getData().length;
    }

    /**
     * @inheritDoc
     */
    public byte[] getBytes(long pos, int length) throws RowsetException {
        if (getData() != null) {
            byte[] lData = getData();
            if (pos >= 1 && pos + length - 1 <= lData.length) {
                return Arrays.copyOfRange(lData, (int) pos - 1, (int) (pos + length - 1));
            } else {
                if (pos < 1) {
                    throw new RowsetException("Bad parameters. pos must be >= 1");
                } else if (pos + length - 1 > lData.length) {
                    throw new RowsetException("Bad parameters. Langth is too large");
                } else {
                    throw new RowsetException("Bad parameters.");
                }
            }
        } else {
            throw new RowsetException("Inner data is absent.");
        }
    }

    /**
     * @inheritDoc
     */
    public InputStream getBinaryStream() throws RowsetException {
        if (getData() != null) {
            byte[] lData = getData();
            return new ByteArrayInputStream(lData);
        } else {
            throw new RowsetException("Inner data is absent.");
        }
    }

    /**
     * @inheritDoc
     */
    public long position(byte[] pattern, long start) throws RowsetException {
        if (pattern != null && pattern.length > 0) {
            if (start >= 1) {
                byte[] innerData = getData();
                if (start - 1 + pattern.length <= innerData.length) {
                    for (int i = (int) start - 1; i <= innerData.length - pattern.length; i++) {
                        int compareFrom = i;
                        int compareTo = i + pattern.length - 1;
                        if (compareArrays(innerData, compareFrom, compareTo, pattern)) {
                            return i + 1;
                        }
                    }
                    return -1;
                } else {
                    throw new RowsetException("Start position is too large for passed pattern, or pattern is too long for passed start position");
                }
            } else {
                throw new RowsetException("Start position must be greater or equal 1");
            }
        } else {
            throw new RowsetException("Pattern must consist of at least 1 byte");
        }
    }

    /**
     * @inheritDoc
     */
    public int setBytes(long pos, byte[] bytes) throws RowsetException {
        return setBytes(pos, bytes, 0, bytes.length);
    }

    /**
     * @inheritDoc
     */
    public int setBytes(long pos, byte[] bytes, int offset, int len) throws RowsetException {
        if (bytes != null) {
            if (offset >= 0 && offset < bytes.length) {
                if (offset + len >= 0 && offset + len <= bytes.length) {
                    if (pos - 1 >= 0 && pos - 1 < getData().length) {
                        int secondChunkLength = Math.max(0, getData().length - len - (int) pos + 1);
                        byte[] firstChunk = Arrays.copyOfRange(getData(), 0, (int) pos - 1);
                        byte[] resData = new byte[firstChunk.length + len + secondChunkLength];
                        System.arraycopy(firstChunk, 0, resData, 0, firstChunk.length);
                        System.arraycopy(bytes, offset, resData, firstChunk.length, len);
                        if (secondChunkLength > 0) {
                            System.arraycopy(getData(), (int) pos - 1 + len, resData, firstChunk.length + len, secondChunkLength);
                        }
                        setData(resData);
                        return len;
                    } else {
                        throw new RowsetException("Bad parameters. Blob position must be in range [1;length()]");
                    }
                } else {
                    throw new RowsetException("Bad parameters. The value of (offset+len) must be in range [0;<source byte array>.length]");
                }
            } else {
                throw new RowsetException("Bad parameters. offset must be in range [0;<source byte array>.length-1]");
            }
        } else {
            throw new RowsetException("Bad parameters. Source byte array must be non null.");
        }
    }

    /**
     * @inheritDoc
     */
    public OutputStream setBinaryStream(long pos) throws RowsetException {
        if (pos >= 1 && pos <= length() + 1) {
            try {
                CompactBlobOutputStream os = new CompactBlobOutputStream(this, Arrays.copyOfRange(getData(), 0, (int) pos - 1));
                return os;
            } catch (Exception ex) {
                throw new RowsetException(ex);
            }
        } else {
            throw new RowsetException("Position must lie in range [1;length()+1]");
        }
    }

    /**
     * @inheritDoc
     */
    public void truncate(long len) throws RowsetException {
        if (getData() != null) {
            byte[] lData = getData();
            if (len < lData.length) {
                setData(Arrays.copyOfRange(lData, 0, (int) len));
            } else if (len > lData.length) {
                throw new RowsetException("Truncate will only decrease length.");
            }
        } else {
            throw new RowsetException("Inner data is absent.");
        }
    }

    /**
     * @inheritDoc
     */
    public InputStream getBinaryStream(long pos, long length) throws RowsetException {
        if (getData() != null) {
            byte[] lData = getData();
            if (pos >= 1 && pos + length - 1 <= lData.length) {
                return new ByteArrayInputStream(Arrays.copyOfRange(lData, (int) pos - 1, (int) (pos + length - 1)));
            } else {
                if (pos < 1) {
                    throw new RowsetException("Bad parameters. pos must be >= 1");
                } else if (pos + length - 1 > lData.length) {
                    throw new RowsetException("Bad parameters. Langth is too large");
                } else {
                    throw new RowsetException("Bad parameters.");
                }
            }
        } else {
            throw new RowsetException("Inner data is absent.");
        }
    }

    private boolean compareArrays(byte[] innerData, int compareFrom, int compareTo, byte[] pattern) {
        for (int i = compareFrom; i <= compareTo; i++) {
            byte b1 = innerData[i];
            byte b2 = pattern[i - compareFrom];
            if (b1 != b2) {
                return false;
            }
        }
        return true;
    }
}
