/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.compacts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Stream to write to <code>CompactBlob</code> object's data
 * @author mg
 */
public class CompactBlobOutputStream extends ByteArrayOutputStream {

    protected CompactBlob blob;

    /**
     * Simple stream constructor.
     * @param aBlob <code>CompactBlob</code> object to be written into.
     */
    public CompactBlobOutputStream(CompactBlob aBlob) {
        super();
        blob = aBlob;
    }

    /**
     * Stream constructor with initial data. The initial data will be written into the stream immediatly after construction.
     * @param aBlob <code>CompactBlob</code> object a data to be written into.
     * @param aInitialData Initial data to written to the stream after construction.
     * @throws IOException
     */
    public CompactBlobOutputStream(CompactBlob aBlob, byte[] aInitialData) throws IOException {
        this(aBlob);
        write(aInitialData);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void close() throws IOException {
        flush();
        blob.setData(toByteArray());
        blob = null;
        super.close();
    }
}
