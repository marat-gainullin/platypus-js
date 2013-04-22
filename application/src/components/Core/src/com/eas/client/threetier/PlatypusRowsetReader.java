/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.threetier;

import com.bearsoft.rowset.exceptions.AlreadyExistSerializerException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.serial.BinaryRowsetReader;
import com.bearsoft.rowset.serial.custom.CompactLobsSerializer;
import com.vividsolutions.jts.geom.Geometry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class PlatypusRowsetReader extends BinaryRowsetReader{

    public PlatypusRowsetReader()
    {
        try {
            CompactLobsSerializer ser = new CompactLobsSerializer();
            addSerializer(DataTypeInfo.CLOB, ser);
            addSerializer(DataTypeInfo.NCLOB, ser);
            addSerializer(DataTypeInfo.BLOB, ser);
            addSerializer(DataTypeInfo.BINARY, ser);
            addSerializer(DataTypeInfo.VARBINARY, ser);
            addSerializer(DataTypeInfo.LONGVARBINARY, ser);
            addSerializer(new DataTypeInfo(java.sql.Types.STRUCT, "MDSYS.SDO_GEOMETRY", Geometry.class.getName()), new JtsGeometrySerializer());
            addSerializer(new DataTypeInfo(java.sql.Types.STRUCT, "MDSYS.SDO_GEOMETRY", Object.class.getName()), new JtsGeometrySerializer());
        } catch (AlreadyExistSerializerException ex) {
            Logger.getLogger(PlatypusRowsetReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
