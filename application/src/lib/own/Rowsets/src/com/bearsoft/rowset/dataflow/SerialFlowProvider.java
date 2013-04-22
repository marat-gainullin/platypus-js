/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.dataflow;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.serial.BinaryRowsetReader;
import com.bearsoft.rowset.serial.BinaryRowsetWriter;
import com.bearsoft.rowset.serial.RowsetReader;
import com.bearsoft.rowset.serial.RowsetWriter;
import java.io.InputStream;

/**
 * It's abstract because of getting in/out streams.
 * @author mg
 */
public abstract class SerialFlowProvider implements FlowProvider {

    protected RowsetReader reader;

    /**
     * Constructor accepting reader and writer instances.
     * @param aReader A reader to be used in flow process. If null is passed, then BinaryRowsetReader is created.
     * @param aWriter A writer to be used in flow process. If null is passed, then BinaryRowsetWriter is created.
     * @see BinaryRowsetReader
     * @see BinaryRowsetWriter
     */
    public SerialFlowProvider(RowsetReader aReader, RowsetWriter aWriter) {
        super();
        reader = aReader;
        if (reader == null) {
            reader = new BinaryRowsetReader();
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public Rowset nextPage() throws Exception {
        return new Rowset();
    }

    /**
     * @inheritDoc
     */
    @Override
    public int getPageSize() {
        return -1;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Rowset refresh(Parameters aParams) throws Exception {
        return reader.read(getInputStream(aParams));
    }

    protected abstract InputStream getInputStream(Parameters aParams);

    /**
     * @inheritDoc
     */
    public boolean apply(Rowset aRowset) throws Exception {
        return false;
    }
}
