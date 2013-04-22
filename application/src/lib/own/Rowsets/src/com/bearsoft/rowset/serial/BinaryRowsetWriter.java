/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.serial;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

/**
 * RowsetWrinter descendant, implementing rowset binary serialization behavior.
 * @author mg
 */
public class BinaryRowsetWriter extends RowsetWriter {

    public BinaryRowsetWriter() {
        super();
    }

    /**
     * Writes rowset's data to byte array. This nethod is wrapper for <code>write(Rowset rowset, OutputStream out)</code>
     * @param aRowset Rowset instance the data to be written from.
     * @return Byte array, containing written data.
     * @throws IOException
     * @throws RowsetException
     * @see #write(com.bearsoft.rowset.Rowset, java.io.OutputStream)
     */
    public byte[] write(Rowset aRowset) throws IOException, RowsetException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        write(aRowset, stream);
        stream.flush();
        return stream.toByteArray();
    }

    /**
     * Writes rowset's data to abstract output stream.
     * @param aRowset Rowset instance the data to be written from.
     * @param out Stream, that will contain written data.
     * @throws IOException
     * @throws RowsetException
     */
    public void write(Rowset aRowset, OutputStream out) throws IOException, RowsetException {
        assert !aRowset.isInserting();
        Set<RowsetListener> listeners = aRowset.getRowsetChangeSupport().getRowsetListeners();
        aRowset.getRowsetChangeSupport().setRowsetListeners(null);
        try {
            ProtoWriter pw = new ProtoWriter(out);
            // properties
            /*
            if (aRowset.getSessionId() != null) {
                pw.put(BinaryTags.SESSION, aRowset.getSessionId());
            }
            if (aRowset.isTransacted()) {
                pw.put(BinaryTags.TRANSACTED);
            }
            */
            pw.put(BinaryTags.CURSOR_POSITION, aRowset.getCursorPos());
            // fields
            ByteArrayOutputStream metadataOut = new ByteArrayOutputStream();
            writeFields(aRowset.getFields(), metadataOut);
            pw.put(BinaryTags.METADATA);
            pw.put(CoreTags.TAG_STREAM, metadataOut);

            boolean wasBeforeFirst = aRowset.isBeforeFirst();
            boolean wasAfterFirst = aRowset.isAfterLast();
            boolean wasShowOriginal = aRowset.isShowOriginal();
            int oldCursorPos = aRowset.getCursorPos();
            try {
                // data
                aRowset.setShowOriginal(true);
                ByteArrayOutputStream originalOut = writeData(aRowset);
                pw.put(BinaryTags.DATA);
                pw.put(CoreTags.TAG_STREAM, originalOut);
            } finally {
                aRowset.setShowOriginal(wasShowOriginal);
                if (wasBeforeFirst) {
                    aRowset.beforeFirst();
                } else if (wasAfterFirst) {
                    aRowset.afterLast();
                } else {
                    aRowset.absolute(oldCursorPos);
                }
            }
            pw.flush();
        } finally {
            aRowset.getRowsetChangeSupport().setRowsetListeners(listeners);
        }
    }

    public void writeFields(Fields aFields, ByteArrayOutputStream aOut) throws IOException {
        ProtoWriter metadataPw = new ProtoWriter(aOut);
        for (int i = 1; i <= aFields.getFieldsCount(); i++) {
            Field field = aFields.get(i);
            ByteArrayOutputStream fieldOut = new ByteArrayOutputStream();
            writeField(field, fieldOut);
            metadataPw.put(BinaryTags.FIELD);
            metadataPw.put(CoreTags.TAG_STREAM, fieldOut);
        }
        metadataPw.flush();
    }

    public ProtoWriter writeField(Field field, ByteArrayOutputStream aOut) throws IOException {
        ProtoWriter fieldPw = new ProtoWriter(aOut);
        assert field.getName() != null;
        fieldPw.put(BinaryTags.FIELD_NAME, field.getName());
        if (field.getDescription() != null) {
            fieldPw.put(BinaryTags.FIELD_DESCRIPTION, field.getDescription());
        }
        assert field.getTypeInfo() != null;
        fieldPw.put(BinaryTags.FIELD_TYPE, field.getTypeInfo().getSqlType());
        if (field.getTypeInfo().getSqlTypeName() != null) {
            fieldPw.put(BinaryTags.FIELD_TYPENAME, field.getTypeInfo().getSqlTypeName());
        }
        if (field.getTypeInfo().getJavaClassName() != null) {
            fieldPw.put(BinaryTags.FIELD_CLASSNAME, field.getTypeInfo().getJavaClassName());
        }
        fieldPw.put(BinaryTags.FIELD_SIZE, field.getSize());
        fieldPw.put(BinaryTags.FIELD_SCALE, field.getScale());
        fieldPw.put(BinaryTags.FIELD_PRECISION, field.getPrecision());
        if (field.isSigned()) {
            fieldPw.put(BinaryTags.FIELD_SIGNED);
        }
        if (field.isNullable()) {
            fieldPw.put(BinaryTags.FIELD_NULLABLE);
        }
        if (field.isReadonly()) {
            fieldPw.put(BinaryTags.FIELD_READONLY);
        }
        if (field.getTableName() != null) {
            fieldPw.put(BinaryTags.FIELD_TABLENAME, field.getTableName());
        }
        if (field.getSchemaName() != null) {
            fieldPw.put(BinaryTags.FIELD_SCHEMANAME, field.getSchemaName());
        }
        if (field.isPk()) {
            fieldPw.put(BinaryTags.FIELD_PK);
        }
        if (field.isFk()) {
            ByteArrayOutputStream fkOut = new ByteArrayOutputStream();
            ProtoWriter fkPw = new ProtoWriter(fkOut);
            if (field.getFk().getSchema() != null) {
                fkPw.put(BinaryTags.FK_SCHEMANAME, field.getFk().getSchema());
            }
            if (field.getFk().getTable() != null) {
                fkPw.put(BinaryTags.FK_TABLENAME, field.getFk().getTable());
            }
            if (field.getFk().getField() != null) {
                fkPw.put(BinaryTags.FK_FIELD_NAME, field.getFk().getField());
            }
            if (field.getFk().getCName() != null) {
                fkPw.put(BinaryTags.FK_CNAME, field.getFk().getCName());
            }
            fkPw.put(BinaryTags.FK_UPDATE_RULE, Integer.valueOf(field.getFk().getFkUpdateRule().toShort()));
            fkPw.put(BinaryTags.FK_DELETE_RULE, Integer.valueOf(field.getFk().getFkDeleteRule().toShort()));
            fkPw.put(BinaryTags.FK_DEFERRABLE, field.getFk().getFkDeferrable() ? 1 : 0);
            if (field.getFk().getReferee() != null) {
                if (field.getFk().getReferee().getSchema() != null) {
                    fkPw.put(BinaryTags.PK_SCHEMANAME, field.getFk().getReferee().getSchema());
                }
                if (field.getFk().getReferee().getTable() != null) {
                    fkPw.put(BinaryTags.PK_TABLENAME, field.getFk().getReferee().getTable());
                }
                if (field.getFk().getReferee().getField() != null) {
                    fkPw.put(BinaryTags.PK_FIELD_NAME, field.getFk().getReferee().getField());
                }
                if (field.getFk().getReferee().getCName() != null) {
                    fkPw.put(BinaryTags.PK_CNAME, field.getFk().getReferee().getCName());
                }
            }
            fkPw.flush();
            fieldPw.put(BinaryTags.FIELD_FK);
            fieldPw.put(CoreTags.TAG_STREAM, fkOut);
        }
        fieldPw.flush();
        return fieldPw;
    }

    /**
     * Writes collection of rows to out stream.
     * @param aRowset
     * @return ByteArrayOutputStream, containing written data.
     * @throws RowsetException
     * @throws IOException
     */
    protected ByteArrayOutputStream writeData(Rowset aRowset) throws RowsetException, IOException {
        ByteArrayOutputStream dataOut = new ByteArrayOutputStream();
        ProtoWriter dataPw = new ProtoWriter(dataOut);
        List<CustomSerializer> customSerializers = achieveCustomSerializers(aRowset.getFields());
        Fields fields = aRowset.getFields();
        aRowset.beforeFirst();
        while (aRowset.next()) {
            Row row = aRowset.getCurrentRow();
            if (needToWriteRow(row)) {
                ByteArrayOutputStream rowOut = new ByteArrayOutputStream();
                ProtoWriter rowPw = new ProtoWriter(rowOut);
                // row values
                for (int i = 1; i <= fields.getFieldsCount(); i++) {
                    Object toWrite = row.getColumnObject(i);
                    writeValue(fields, i, rowPw, customSerializers, toWrite, BinaryTags.CURRENT_VALUE);
                    toWrite = row.getOriginalColumnObject(i);
                    writeValue(fields, i, rowPw, customSerializers, toWrite, BinaryTags.ORIGINAL_VALUE);
                }
                // row flags
                if (aRowset.getCurrentRow().isInserted()) {
                    rowPw.put(BinaryTags.INSERTED);
                }
                if (aRowset.getCurrentRow().isDeleted()) {
                    rowPw.put(BinaryTags.DELETED);
                }
                // updated indicies set
                Set<Integer> updatedFields = aRowset.getCurrentRow().getUpdatedColumns();
                if (!updatedFields.isEmpty()) {
                    ByteArrayOutputStream updOut = new ByteArrayOutputStream();
                    ProtoWriter updPw = new ProtoWriter(updOut);
                    for (int updIndex : updatedFields) {
                        updPw.put(BinaryTags.UPDATED_INDEX, updIndex);
                    }
                    updPw.flush();
                    rowPw.put(BinaryTags.UPDATED);
                    rowPw.put(CoreTags.TAG_STREAM, updOut);
                }
                rowPw.flush();
                dataPw.put(BinaryTags.ROW);
                dataPw.put(CoreTags.TAG_STREAM, rowOut);
            }
        }
        dataPw.flush();
        return dataOut;
    }

    private static void writeValue(Fields fields, int colIndex, ProtoWriter rowPw, List<CustomSerializer> customSerializers, Object toWrite, int aTag) throws IOException, RowsetException {
        if (toWrite == null) {
            rowPw.put(aTag);
        } else {
            Field field = fields.get(colIndex);
            CustomSerializer serializer = customSerializers.get(colIndex - 1);
            if (serializer != null) {
                byte[] sData = serializer.serialize(toWrite, field.getTypeInfo());
                rowPw.put(aTag, sData);
            } else {
                rowPw.putJDBCCompatible(aTag, field.getTypeInfo().getSqlType(), toWrite);
            }
        }
    }

    protected boolean needToWriteRow(Row aRow) {
        return true;
    }
}
