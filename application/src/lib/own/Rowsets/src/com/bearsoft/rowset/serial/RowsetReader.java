/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.serial;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.RowsetException;
import java.io.InputStream;

/**
 * Abstract class for rowset reading from srial sources.
 * @author mg
 */
public abstract class RowsetReader extends RowsetSerializer {

    /**
     * Default reader constructor. Without any rowset, reader will create one while reading.
     */
    public RowsetReader() {
        super();
    }

    /**
     * Reads from an arbitrary bytes array.
     * @param aData Byte array, the rowset's data to be read from.
     * @return New Rowset instance, based on data read from byte <code>aData</code> array.
     * @throws RowsetException
     */
    public abstract Rowset read(byte[] aData) throws RowsetException;

    /**
     * Reads from an arbitrary bytes array.
     * @param aStream An abstract input stream, the rowset's data to be read from.
     * @return New Rowset instance, based on data read from byte <code>aStream</code> array.
     * @throws RowsetException
     */
    public abstract Rowset read(InputStream aStream) throws RowsetException;
}
