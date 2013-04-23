/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.compacts;

/**
 * 
 * @author mg
 */
public class CompactRowId {

    protected byte[] data;

    public CompactRowId(byte[] aData) {
        super();
        data = aData;
    }

    public byte[] getData() {
        return data;
    }
}
