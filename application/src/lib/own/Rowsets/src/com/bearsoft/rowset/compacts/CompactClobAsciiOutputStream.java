/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.compacts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Stream to write to <code>CompactClob</code> object's data.
 * Coding of characters is "us-ascii".
 * @author mg
 */
public class CompactClobAsciiOutputStream extends ByteArrayOutputStream {

    protected CompactClob clob;

    /**
     * Simple stream constructor.
     * @param aClob <code>CompactClob</code> object with data to be written into.
     */
    public CompactClobAsciiOutputStream(CompactClob aClob) {
        super();
        clob = aClob;
    }

    /**
     * Stream constructor with initial data. The initial data will be written into the stream immediatly after construction.
     * @param aClob <code>CompactClob</code> object a data to be written into.
     * @param aInitialData Initial data to written to the stream after construction.
     * @throws IOException
     */
    public CompactClobAsciiOutputStream(CompactClob aClob, byte[] aInitialData) throws IOException {
        this(aClob);
        write(aInitialData);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void close() throws IOException {
        flush();
        clob.setData(new String(toByteArray(), "us-ascii"));
        clob = null;
        super.close();
    }
}
