/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.bearsoft.rowset.exceptions.AlreadyExistSerializerException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.serial.BinaryRowsetReader;
import com.bearsoft.rowset.serial.custom.CompactLobsSerializer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class PlatypusRowsetReader extends BinaryRowsetReader {

    public PlatypusRowsetReader(Fields aExpectedFields) {
        super(aExpectedFields);
        try {
            CompactLobsSerializer lobsSerializer = new CompactLobsSerializer();
            addSerializer(DataTypeInfo.CLOB, lobsSerializer);
            addSerializer(DataTypeInfo.NCLOB, lobsSerializer);
            addSerializer(DataTypeInfo.BLOB, lobsSerializer);
            addSerializer(DataTypeInfo.BINARY, lobsSerializer);
            addSerializer(DataTypeInfo.VARBINARY, lobsSerializer);
            addSerializer(DataTypeInfo.LONGVARBINARY, lobsSerializer);
            addSerializer(DataTypeInfo.GEOMETRY.copy(), new JtsGeometrySerializer());
        } catch (AlreadyExistSerializerException ex) {
            Logger.getLogger(PlatypusRowsetReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
