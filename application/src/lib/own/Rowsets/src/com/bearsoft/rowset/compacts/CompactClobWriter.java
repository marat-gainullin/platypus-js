/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.compacts;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Writer for <code>CompactClob</code> objects.
 * @author mg
 */
public class CompactClobWriter extends StringWriter {

    protected CompactClob clob;

    /**
     * Simple constructor
     * @param aClob <code>CompactClob</code> object to write to.
     */
    public CompactClobWriter(CompactClob aClob) {
        super();
        clob = aClob;
        assert clob != null : "aClob parameter must be non-null";
    }

    /**
    /**
     * Stream constructor with initial data. The initial data will be written into the stream immediatly after construction.
     * @param aClob <code>CompactClob</code> object a data to be written into.
     * @param aInitialData Initial data to written to the stream after construction.
     */
    public CompactClobWriter(CompactClob aClob, String aInitialData) {
        super();
        clob = aClob;
        if (aInitialData != null) {
            write(aInitialData);
        }
        assert clob != null : "aClob parameter must be non-null";
    }

    /**
     * @inheritDoc
     */
    @Override
    public void close() throws IOException {
        flush();
        String newData = getBuffer().toString();
        clob.setData(newData);
        super.close();
    }
}
