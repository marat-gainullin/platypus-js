/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.compacts;

/**
 *
 * @author mg
 */
public class CompactRef {

    protected String baseTypeName;
    protected Object value;

    public CompactRef(String aBaseTypeName)
    {
        super();
        baseTypeName = aBaseTypeName;
    }

    public String getBaseTypeName() {
        return baseTypeName;
    }

    public Object getObject() {
        return value;
    }

    public void setObject(Object aValue) {
        value = aValue;
    }
}
