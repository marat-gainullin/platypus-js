/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.compacts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Stream to write to <code>CompactSqlXml</code> object's data
 * @author mg
 */
public class CompactSqlXmlOutputStream extends ByteArrayOutputStream{

    protected CompactSqlXml compactSqlXml;
    protected String encoding = "utf-8";

    /**
     * Simple stream constructor.
     * @param aCompactSqlXml <code>CompactSqlXml</code> object to be written into.
     */
    public CompactSqlXmlOutputStream(CompactSqlXml aCompactSqlXml) {
        super();
        compactSqlXml = aCompactSqlXml;
    }

    /**
     * Stream constructor with initial data. The initial data will be written into the stream immediatly after construction.
     * @param aCompactSqlXml <code>CompactSqlXml</code> object a data to be written into.
     * @param aInitialData Initial data to written to the stream after construction.
     * @throws IOException
     */
    public CompactSqlXmlOutputStream(CompactSqlXml aCompactSqlXml, byte[] aInitialData) throws IOException {
        this(aCompactSqlXml);
        write(aInitialData);
    }

    /**
     * Simple stream constructor with encoding hint.
     * @param aCompactSqlXml <code>CompactSqlXml</code> object to be written into.
     * @param anEncoding Encoding to be used when data is transformed to string.
     */
    public CompactSqlXmlOutputStream(CompactSqlXml aCompactSqlXml, String anEncoding) {
        super();
        compactSqlXml = aCompactSqlXml;
        encoding = anEncoding;
    }

    /**
     * Stream constructor with initial data. The initial data will be written into the stream immediatly after construction.
     * @param aCompactSqlXml <code>CompactSqlXml</code> object a data to be written into.
     * @param aInitialData Initial data to written to the stream after construction.
     * @param anEncoding Encoding to be used when data is transformed to string.
     * @throws IOException
     */
    public CompactSqlXmlOutputStream(CompactSqlXml aCompactSqlXml, byte[] aInitialData, String anEncoding) throws IOException {
        this(aCompactSqlXml);
        write(aInitialData);
        encoding = anEncoding;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void close() throws IOException {
        flush();
        compactSqlXml.setData(new String(toByteArray(), encoding));
        compactSqlXml = null;
        super.close();
    }
}
