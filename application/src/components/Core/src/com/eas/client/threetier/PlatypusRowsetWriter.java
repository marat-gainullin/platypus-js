/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.AlreadyExistSerializerException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.serial.BinaryRowsetWriter;
import com.bearsoft.rowset.serial.custom.CompactLobsSerializer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class PlatypusRowsetWriter extends BinaryRowsetWriter {

    protected boolean writeOnlyChanged;

    protected PlatypusRowsetWriter(boolean aWriteOnlyChanged, boolean aWriteFields) {
        super(aWriteFields);
        try {
            writeOnlyChanged = aWriteOnlyChanged;
            CompactLobsSerializer lobsSerializer = new CompactLobsSerializer();
            addSerializer(DataTypeInfo.CLOB, lobsSerializer);
            addSerializer(DataTypeInfo.NCLOB, lobsSerializer);
            addSerializer(DataTypeInfo.BLOB, lobsSerializer);
            addSerializer(DataTypeInfo.BINARY, lobsSerializer);
            addSerializer(DataTypeInfo.VARBINARY, lobsSerializer);
            addSerializer(DataTypeInfo.LONGVARBINARY, lobsSerializer);
            addSerializer(DataTypeInfo.GEOMETRY.copy(), new JtsGeometrySerializer());
        } catch (AlreadyExistSerializerException ex) {
            Logger.getLogger(PlatypusRowsetWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PlatypusRowsetWriter() {
        this(false, false);
    }

    /*
    @Override
    public void writeFields(Fields aFields, ByteArrayOutputStream aOut) throws IOException {
        if (preProcessFields) {
            aFields = aFields.copy();
            SQLUtils.processFieldsPreClient(aFields);
        }
        super.writeFields(aFields, aOut);
    }
*/
    @Override
    protected ByteArrayOutputStream writeData(Rowset aRowset) throws RowsetException, IOException {
        return super.writeData(aRowset);
    }

    @Override
    protected boolean needToWriteRow(Row aRow) {
        return !writeOnlyChanged || aRow.isDeleted() || aRow.isInserted() || aRow.isUpdated();
    }
}
