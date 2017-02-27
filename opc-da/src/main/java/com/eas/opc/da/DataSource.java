/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da;

import com.eas.opc.da.dcom.OPCDATASOURCE;

/**
 *
 * @author pk
 */
public enum DataSource {

    Cache(OPCDATASOURCE.OPC_DS_CACHE),
    Device(OPCDATASOURCE.OPC_DS_DEVICE);
    private short value;

    private DataSource(int value) {
        this.value = (short) value;
    }

    public short getValue() {
        return value;
    }

    public static DataSource getDataSource(short value) {
        for (DataSource ds : values()) {
            if (ds.getValue() == value) {
                return ds;
            }
        }
        throw new IllegalArgumentException("Unknown OPCDATASOURCE value " + value);
    }
}
