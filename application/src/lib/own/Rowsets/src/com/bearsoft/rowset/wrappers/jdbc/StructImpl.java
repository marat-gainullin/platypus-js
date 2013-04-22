/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.wrappers.jdbc;

import com.bearsoft.rowset.compacts.CompactStruct;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Map;

/**
 * JDBC warapper of <code>CompactStruct</code> implementing <code>Struct</code> imterface
 * @author mg
 * @see Struct
 */
public class StructImpl implements Struct{

    protected CompactStruct struct;
    
    /**
     * JDBC Wrapper Constructor
     * @param aStruct
     */
    public StructImpl(CompactStruct aStruct)
    {
        super();
        struct = aStruct;
    }

    public String getSQLTypeName() throws SQLException {
        return struct.getSQLTypeName();
    }

    public Object[] getAttributes() throws SQLException {
        return struct.getAttributes();
    }

    public Object[] getAttributes(Map<String, Class<?>> map) throws SQLException {
        return struct.getAttributes();
    }

}
