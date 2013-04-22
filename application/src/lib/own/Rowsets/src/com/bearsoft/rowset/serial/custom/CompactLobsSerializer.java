/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.serial.custom;

import com.bearsoft.rowset.compacts.CompactBlob;
import com.bearsoft.rowset.compacts.CompactClob;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.serial.CustomSerializer;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This serializer acts on <code>CompactClob</code> and <code>CompactBlob</code> object.
 * @author mg
 */
public class CompactLobsSerializer implements CustomSerializer {

    protected static final String STRINGS_ENCODING = "utf-8";

    /**
     * @inheritDoc
     */
    @Override
    public byte[] serialize(Object aValue, DataTypeInfo aTypeInfo) throws RowsetException {
        if (aValue != null) {
            if (aTypeInfo.getSqlType() == java.sql.Types.CLOB && aTypeInfo.getJavaClassName().equals(CompactClob.class.getName())) {
                assert aValue instanceof CompactClob;
                CompactClob cc = (CompactClob) aValue;
                String lData = cc.getData();
                if (lData != null) {
                    try {
                        return lData.getBytes(STRINGS_ENCODING);
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(CompactLobsSerializer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return new byte[0];
            } else if ((aTypeInfo.getSqlType() == java.sql.Types.BLOB || aTypeInfo.getSqlType() == java.sql.Types.BINARY || aTypeInfo.getSqlType() == java.sql.Types.VARBINARY || aTypeInfo.getSqlType() == java.sql.Types.LONGVARBINARY) && aTypeInfo.getJavaClassName().equals(CompactBlob.class.getName())) {
                assert aValue instanceof CompactBlob;
                CompactBlob cb = (CompactBlob) aValue;
                return cb.getData();
            }
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Object deserialize(byte[] aData, int aOffset, int aDataSize, DataTypeInfo aTypeInfo) throws RowsetException {
        if (aDataSize > 0) {
            if (aTypeInfo.getSqlType() == java.sql.Types.CLOB && aTypeInfo.getJavaClassName().equals(CompactClob.class.getName())) {
                try {
                    return new CompactClob(Arrays.copyOfRange(aData, aOffset, aOffset + aDataSize), STRINGS_ENCODING);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(CompactLobsSerializer.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if ((aTypeInfo.getSqlType() == java.sql.Types.BLOB || aTypeInfo.getSqlType() == java.sql.Types.BINARY || aTypeInfo.getSqlType() == java.sql.Types.VARBINARY || aTypeInfo.getSqlType() == java.sql.Types.LONGVARBINARY) && aTypeInfo.getJavaClassName().equals(CompactBlob.class.getName())) {
                return new CompactBlob(Arrays.copyOfRange(aData, aOffset, aOffset + aDataSize));
            }
        }
        return null;
    }
}
