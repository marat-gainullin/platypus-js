/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da;

/**
 *
 * @author pk
 */
public enum DataType
{
    DEFAULT((short) 0), NULL((short) 1), INT16((short) 2), INT32((short) 3),
    FLOAT((short) 4), DOUBLE((short) 5), CY((short) 6), DATE((short) 7),
    BSTR((short) 8),
    DISPATCH((short) 9), ERROR((short) 10), BOOL((short) 11),
    VARIANT((short) 12), UNKNOWN((short) 13),
    DECIMAL((short) 14), UINT8((short) 16), UINT16((short) 18),
    UINT32((short) 19),
    INT64((short) 20), INT((short) 21), UINT((short) 22);
    private final short typeID;

    private DataType(short typeID)
    {
        this.typeID = typeID;
    }

    public short getTypeID()
    {
        return typeID;
    }

    public static DataType getDataType(short typeID)
    {
        for (DataType t : values())
            if (t.getTypeID() == typeID)
                return t;
        throw new IllegalArgumentException(String.format("Unknown data type %d", typeID)); //NOI18N
    }
}
