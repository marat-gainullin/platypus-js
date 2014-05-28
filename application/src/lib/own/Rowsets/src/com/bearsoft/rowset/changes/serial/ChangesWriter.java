/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.changes.serial;

import com.bearsoft.rowset.changes.*;
import com.bearsoft.rowset.serial.CustomSerializer;
import com.bearsoft.rowset.serial.RowsetSerializer;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoWriter;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 *
 * @author mg
 */
public class ChangesWriter {

    public static class ChangeWriter implements ChangeVisitor {

        public ByteArrayOutputStream out = new ByteArrayOutputStream();
        protected ProtoWriter writer = new ProtoWriter(out);
        protected RowsetSerializer serializer;
        
        public ChangeWriter(RowsetSerializer aSerializer)
        {
            super();
            serializer = aSerializer;
        }
        
        protected ByteArrayOutputStream writeValue(ChangeValue aValue) throws Exception {
            ByteArrayOutputStream valueOut = new ByteArrayOutputStream();
            ProtoWriter valueWriter = new ProtoWriter(valueOut);
            valueWriter.put(ChangesTags.VALUE_NAME_TAG, aValue.name);
            valueWriter.put(ChangesTags.TYPE_ID_TAG, aValue.type.getSqlType());
            valueWriter.put(ChangesTags.TYPE_NAME_TAG, aValue.type.getSqlTypeName());
            valueWriter.put(ChangesTags.TYPE_CLASS_NAME_TAG, aValue.type.getJavaClassName());
            CustomSerializer customSerializer = serializer.getSerializer(aValue.type);
            if(customSerializer != null) {
                valueWriter.put(ChangesTags.CUSTOM_VALUE_TAG, customSerializer.serialize(aValue.value, aValue.type));
            }
            else {
                valueWriter.putJDBCCompatible(ChangesTags.VALUE_TAG, aValue.type.getSqlType(), aValue.value);
            }
            valueWriter.flush();
            return valueOut;
        }

        @Override
        public void visit(Insert aChange) throws Exception {
            writer.put(ChangesTags.CHANGE_TYPE_TAG, ChangesTags.CHANGE_TYPE_INSERT);
            writer.put(ChangesTags.CHANGE_ENTITY_TAG, aChange.entityId);
            for (ChangeValue value : aChange.data) {
                writer.put(ChangesTags.CHANGE_VALUE_TAG);
                writer.put(CoreTags.TAG_STREAM, writeValue(value));
            }
            writer.flush();
        }

        @Override
        public void visit(Update aChange) throws Exception {
            writer.put(ChangesTags.CHANGE_TYPE_TAG, ChangesTags.CHANGE_TYPE_UPDATE);            
            writer.put(ChangesTags.CHANGE_ENTITY_TAG, aChange.entityId);
            for (ChangeValue value : aChange.data) {
                writer.put(ChangesTags.CHANGE_VALUE_TAG);
                writer.put(CoreTags.TAG_STREAM, writeValue(value));
            }
            for (ChangeValue value : aChange.keys) {
                writer.put(ChangesTags.CHANGE_KEY_TAG);
                writer.put(CoreTags.TAG_STREAM, writeValue(value));
            }
            writer.flush();
        }

        @Override
        public void visit(Delete aChange) throws Exception {
            writer.put(ChangesTags.CHANGE_TYPE_TAG, ChangesTags.CHANGE_TYPE_DELETE);
            writer.put(ChangesTags.CHANGE_ENTITY_TAG, aChange.entityId);
            for (ChangeValue value : aChange.keys) {
                writer.put(ChangesTags.CHANGE_KEY_TAG);
                writer.put(CoreTags.TAG_STREAM, writeValue(value));
            }
            writer.flush();
        }

        @Override
        public void visit(Command aChange) throws Exception {
            writer.put(ChangesTags.CHANGE_TYPE_TAG, ChangesTags.CHANGE_TYPE_COMMAND);
            writer.put(ChangesTags.CHANGE_ENTITY_TAG, aChange.entityId);
            for (ChangeValue value : aChange.parameters) {
                writer.put(ChangesTags.CHANGE_PARAMETER_TAG);
                writer.put(CoreTags.TAG_STREAM, writeValue(value));
            }
            writer.flush();
        }
    }

    public static byte[] write(List<Change> aChanges, RowsetSerializer aSerializer) throws Exception {
        ByteArrayOutputStream changesOut = new ByteArrayOutputStream();
        ProtoWriter changesWriter = new ProtoWriter(changesOut);
        for (int i = 0; i < aChanges.size(); i++) {
            ChangeWriter changeWriter = new ChangeWriter(aSerializer);
            aChanges.get(i).accept(changeWriter);
            changesWriter.put(ChangesTags.CHANGE_TAG);
            changesWriter.put(CoreTags.TAG_STREAM, changeWriter.out);
        }
        changesWriter.flush();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(out);
        writer.put(ChangesTags.CHANGES_TAG);
        writer.put(CoreTags.TAG_STREAM, changesOut);
        writer.flush();
        return changesOut.toByteArray();
    }
}
