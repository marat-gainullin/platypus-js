/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.serial;

/**
 * Base class for rowset writer of any kind.
 *
 * @author mg
 */
public class RowsetWriter extends RowsetSerializer {

    protected boolean writeFields = true;

    /**
     * Default no-parameters constrcuctor of rowset's writer.
     */
    public RowsetWriter() {
        super();
    }

    public RowsetWriter(boolean aWriteFields) {
        super();
        writeFields = aWriteFields;
    }
}
