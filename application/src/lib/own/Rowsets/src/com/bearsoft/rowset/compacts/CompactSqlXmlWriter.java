/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.compacts;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Writer for <code>CompactSqlXmlWriter</code> objects.
 * @author mg
 */
public class CompactSqlXmlWriter extends StringWriter{

    protected CompactSqlXml sqlXml;

    /**
     * Simple constructor
     * @param aSqlXml <code>CompactSqlXml</code> object to write to.
     */
    public CompactSqlXmlWriter(CompactSqlXml aSqlXml) {
        super();
        sqlXml = aSqlXml;
        assert sqlXml != null : "aSqlXml parameter must be non-null";
    }

    /**
    /**
     * Stream constructor with initial data. The initial data will be written into the stream immediatly after construction.
     * @param aSqlXml <code>CompactSqlXml</code> object a data to be written into.
     * @param aInitialData Initial data to written to the stream after construction.
     */
    public CompactSqlXmlWriter(CompactSqlXml aSqlXml, String aInitialData) {
        super();
        sqlXml = aSqlXml;
        if (aInitialData != null) {
            write(aInitialData);
        }
        assert sqlXml != null : "aSqlXml parameter must be non-null";
    }

    /**
     * @inheritDoc
     */
    @Override
    public void close() throws IOException {
        flush();
        String newData = getBuffer().toString();
        sqlXml.setData(newData);
        super.close();
    }
}
