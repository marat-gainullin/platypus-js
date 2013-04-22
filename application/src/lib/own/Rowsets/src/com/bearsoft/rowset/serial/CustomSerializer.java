/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.serial;

import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;

/**
 * CustomSerializer interface. Intended to serialize and deserialize any object, according to it's sql type, type name and java class name.
 * @author mg
 */
public interface CustomSerializer {

    /**
     * Serializes any object, according to it's sql type, type name and java class name.
     * @param aValue Value to be serialized.
     * @param aTypeInfo DataTypeInfo instance describing type information about data.
     * @return Byte array, representing serialized form of <code>aValue</code> object.
     * @throws RowsetException
     * @see DataTypeInfo
     */
    public byte[] serialize(Object aValue, DataTypeInfo aTypeInfo) throws RowsetException;

    /**
     * Deserializes <code>aData</code> byte array to some object, according to it's sql type, type name and java class name.
     * @param aData Byte array, representing serialized form object to be deserialized.
     * @param aOffset Offset in aData array where data of interest begins
     * @param aDataSize Size of data we are interested in.
     * @param aTypeInfo DataTypeInfo instance describing type information about data.
     * @return Some deserialized object.
     * @throws RowsetException
     * @see DataTypeInfo
     */
    public Object deserialize(byte[] aData, int aOffset, int aDataSize, DataTypeInfo aTypeInfo) throws RowsetException;
}
