/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.compacts;

/**
 *
 * @author mg
 */
public class CompactStruct {

    protected String typeName;
    protected Object[] attributes;

    public CompactStruct(String aTypeName, Object[] aAttributes) {
        super();
        typeName = aTypeName;
        attributes = aAttributes;
    }

    public String getSQLTypeName() {
        return typeName;
    }

    public Object[] getAttributes() {
        return attributes;
    }
}
