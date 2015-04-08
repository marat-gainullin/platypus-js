/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.changes;

import com.eas.client.metadata.DataTypeInfo;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoWriter;
import com.eas.proto.dom.ProtoDOMBuilder;
import com.eas.proto.dom.ProtoNode;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mg
 */
public class BinaryChanges {

    protected static class ChangeReader implements ChangeVisitor {

        protected ProtoNode node;

        public ChangeReader(ProtoNode aNode) {
            super();
            node = aNode;
        }

        protected ChangeValue readValue(ProtoNode aNode) throws Exception {
            String valueName = aNode.getChild(ChangesTags.VALUE_NAME_TAG).getString();
            int typeId = aNode.getChild(ChangesTags.TYPE_ID_TAG).getInt();
            String typeName = aNode.getChild(ChangesTags.TYPE_NAME_TAG).getString();
            String typeClassName = aNode.getChild(ChangesTags.TYPE_CLASS_NAME_TAG).getString();
            DataTypeInfo typeInfo = new DataTypeInfo(typeId, typeName, typeClassName);
            Object value = aNode.getChild(ChangesTags.VALUE_TAG).getJDBCCompatible(typeId);
            return new ChangeValue(valueName, value, typeInfo);
        }

        @Override
        public void visit(Insert aChange) throws Exception {
            List<ProtoNode> dataNodes = node.getChildren(ChangesTags.CHANGE_VALUE_TAG);
            for (ProtoNode dataNode : dataNodes) {
                aChange.getData().add(readValue(dataNode));
            }
        }

        @Override
        public void visit(Update aChange) throws Exception {
            List<ProtoNode> dataNodes = node.getChildren(ChangesTags.CHANGE_VALUE_TAG);
            for (ProtoNode dataNode : dataNodes) {
                aChange.getData().add(readValue(dataNode));
            }
            List<ProtoNode> keysNodes = node.getChildren(ChangesTags.CHANGE_KEY_TAG);
            for (ProtoNode keysNode : keysNodes) {
                aChange.getKeys().add(readValue(keysNode));
            }
        }

        @Override
        public void visit(Delete aChange) throws Exception {
            List<ProtoNode> keysNodes = node.getChildren(ChangesTags.CHANGE_KEY_TAG);
            for (ProtoNode keysNode : keysNodes) {
                aChange.getKeys().add(readValue(keysNode));
            }
        }

        @Override
        public void visit(Command aChange) throws Exception {
            List<ProtoNode> keysNodes = node.getChildren(ChangesTags.CHANGE_PARAMETER_TAG);
            for (int i = 0; i < keysNodes.size(); i++) {
                aChange.getParameters().add(readValue(keysNodes.get(i)));
            }
        }
    }

    protected static Change readChange(ProtoNode aNode) throws Exception {
        ChangeReader reader = new ChangeReader(aNode);
        int changeType = aNode.getChild(ChangesTags.CHANGE_TYPE_TAG).getInt();
        String changedEntityId = aNode.getChild(ChangesTags.CHANGE_ENTITY_TAG).getString();
        switch (changeType) {
            case ChangesTags.CHANGE_TYPE_INSERT: {
                Insert change = new Insert(changedEntityId);
                change.accept(reader);
                return change;
            }
            case ChangesTags.CHANGE_TYPE_DELETE: {
                Delete change = new Delete(changedEntityId);
                change.accept(reader);
                return change;
            }
            case ChangesTags.CHANGE_TYPE_UPDATE: {
                Update change = new Update(changedEntityId);
                change.accept(reader);
                return change;
            }
            case ChangesTags.CHANGE_TYPE_COMMAND: {
                Command change = new Command(changedEntityId);
                change.accept(reader);
                return change;
            }
            default:
                throw new IllegalStateException(String.format("Unsuported change type occured: %d", changeType));
        }
    }

    public static List<Change> read(byte[] aData) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(aData);
        List<ProtoNode> changesNodes = dom.getChildren(ChangesTags.CHANGE_TAG);
        List<Change> read = new ArrayList<>();
        for (ProtoNode changesNode : changesNodes) {
            read.add(readChange(changesNode));
        }
        return read;
    }
    
    public static class ChangeWriter implements ChangeVisitor {

        public ByteArrayOutputStream out = new ByteArrayOutputStream();
        protected ProtoWriter writer = new ProtoWriter(out);

        public ChangeWriter() {
            super();
        }

        protected ByteArrayOutputStream writeValue(ChangeValue aValue) throws Exception {
            ByteArrayOutputStream valueOut = new ByteArrayOutputStream();
            ProtoWriter valueWriter = new ProtoWriter(valueOut);
            valueWriter.put(ChangesTags.VALUE_NAME_TAG, aValue.name);
            valueWriter.put(ChangesTags.TYPE_ID_TAG, aValue.type.getSqlType());
            valueWriter.put(ChangesTags.TYPE_NAME_TAG, aValue.type.getSqlTypeName());
            valueWriter.put(ChangesTags.TYPE_CLASS_NAME_TAG, aValue.type.getJavaClassName());
            valueWriter.putJDBCCompatible(ChangesTags.VALUE_TAG, aValue.type.getSqlType(), aValue.value);
            valueWriter.flush();
            return valueOut;
        }

        @Override
        public void visit(Insert aChange) throws Exception {
            writer.put(ChangesTags.CHANGE_TYPE_TAG, ChangesTags.CHANGE_TYPE_INSERT);
            writer.put(ChangesTags.CHANGE_ENTITY_TAG, aChange.entityName);
            for (ChangeValue value : aChange.getData()) {
                writer.put(ChangesTags.CHANGE_VALUE_TAG);
                writer.put(CoreTags.TAG_STREAM, writeValue(value));
            }
            writer.flush();
        }

        @Override
        public void visit(Update aChange) throws Exception {
            writer.put(ChangesTags.CHANGE_TYPE_TAG, ChangesTags.CHANGE_TYPE_UPDATE);
            writer.put(ChangesTags.CHANGE_ENTITY_TAG, aChange.entityName);
            for (ChangeValue value : aChange.getData()) {
                writer.put(ChangesTags.CHANGE_VALUE_TAG);
                writer.put(CoreTags.TAG_STREAM, writeValue(value));
            }
            for (ChangeValue value : aChange.getKeys()) {
                writer.put(ChangesTags.CHANGE_KEY_TAG);
                writer.put(CoreTags.TAG_STREAM, writeValue(value));
            }
            writer.flush();
        }

        @Override
        public void visit(Delete aChange) throws Exception {
            writer.put(ChangesTags.CHANGE_TYPE_TAG, ChangesTags.CHANGE_TYPE_DELETE);
            writer.put(ChangesTags.CHANGE_ENTITY_TAG, aChange.entityName);
            for (ChangeValue value : aChange.getKeys()) {
                writer.put(ChangesTags.CHANGE_KEY_TAG);
                writer.put(CoreTags.TAG_STREAM, writeValue(value));
            }
            writer.flush();
        }

        @Override
        public void visit(Command aChange) throws Exception {
            writer.put(ChangesTags.CHANGE_TYPE_TAG, ChangesTags.CHANGE_TYPE_COMMAND);
            writer.put(ChangesTags.CHANGE_ENTITY_TAG, aChange.entityName);
            for (ChangeValue value : aChange.getParameters()) {
                writer.put(ChangesTags.CHANGE_PARAMETER_TAG);
                writer.put(CoreTags.TAG_STREAM, writeValue(value));
            }
            writer.flush();
        }
    }

    public static byte[] write(List<Change> aChanges) throws Exception {
        ByteArrayOutputStream changesOut = new ByteArrayOutputStream();
        ProtoWriter changesWriter = new ProtoWriter(changesOut);
        for (int i = 0; i < aChanges.size(); i++) {
            ChangeWriter changeWriter = new ChangeWriter();
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
