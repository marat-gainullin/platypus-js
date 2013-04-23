/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.compacts;

import com.bearsoft.rowset.metadata.DataTypeInfo;

/**
 *
 * @author mg
 */
public class CompactArray {

    protected DataTypeInfo typeInfo;

    public CompactArray(DataTypeInfo aTypeInfo)
    {
        super();
        typeInfo = aTypeInfo;
    }

    public String getBaseTypeName() {
        return typeInfo.getTypeName();
    }

    public int getBaseType() {
        return typeInfo.getType();
    }

    public Object getArray() {
        return null;
    }

    public Object getArray(long index, int count) {
        return null;
    }
}
