/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.wrappers.jdbc;

import com.bearsoft.rowset.compacts.CompactRowId;
import java.sql.RowId;

/**
 * JDBC warapper of <code>CompactRowId</code> implementing <code>RowId</code> imterface
 * @author mg
 * @see RowId
 */
public class RowIdImpl implements RowId{

    protected CompactRowId rowId;
    
    /**
     * JDBC Wrapper Constructor
     * @param aRowId
     */
    public RowIdImpl(CompactRowId aRowId)
    {
        super();
        rowId = aRowId;
    }

    public byte[] getBytes() {
        return rowId.getData();
    }
}
