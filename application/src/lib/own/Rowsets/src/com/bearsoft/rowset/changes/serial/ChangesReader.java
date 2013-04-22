/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.changes.serial;

import com.bearsoft.rowset.changes.*;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.serial.CustomSerializer;
import com.bearsoft.rowset.serial.RowsetSerializer;
import com.eas.proto.dom.ProtoDOMBuilder;
import com.eas.proto.dom.ProtoNode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class ChangesReader {

    protected static class ChangeReader implements ChangeVisitor {

        protected ProtoNode node;
        protected RowsetSerializer serializer;

        public ChangeReader(ProtoNode aNode, RowsetSerializer aSerializer) {
            super();
            node = aNode;
            serializer = aSerializer;
        }

        protected Change.Value readValue(ProtoNode aNode) throws Exception {
            String valueName = aNode.getChild(ChangesTags.VALUE_NAME_TAG).getString();
            int typeId = aNode.getChild(ChangesTags.TYPE_ID_TAG).getInt();
            String typeName = aNode.getChild(ChangesTags.TYPE_NAME_TAG).getString();
            String typeClassName = aNode.getChild(ChangesTags.TYPE_CLASS_NAME_TAG).getString();
            DataTypeInfo typeInfo = new DataTypeInfo(typeId, typeName, typeClassName);
            Object value = null;
            if (aNode.containsChild(ChangesTags.CUSTOM_VALUE_TAG)) {
                CustomSerializer customSerializer = serializer.getSerializer(typeInfo);
                if (customSerializer != null) {
                    ProtoNode customValueNode = aNode.getChild(ChangesTags.CUSTOM_VALUE_TAG);
                    value = customSerializer.deserialize(customValueNode.getData(), customValueNode.getOffset(), customValueNode.getSize(), typeInfo);
                } else {
                    Logger.getLogger(ChangesReader.class.getName()).log(Level.SEVERE, String.format("Can't find custom serializer for type id:%d and type name: %s", typeId, typeName));
                }
            } else {
                value = aNode.getChild(ChangesTags.VALUE_TAG).getJDBCCompatible(typeId);
            }
            return new Change.Value(valueName, value, typeInfo);
        }

        @Override
        public void visit(Insert aChange) throws Exception {
            List<ProtoNode> dataNodes = node.getChildren(ChangesTags.CHANGE_VALUE_TAG);
            aChange.data = new Change.Value[dataNodes.size()];
            for (int i = 0; i < dataNodes.size(); i++) {
                aChange.data[i] = readValue(dataNodes.get(i));
            }
        }

        @Override
        public void visit(Update aChange) throws Exception {
            List<ProtoNode> dataNodes = node.getChildren(ChangesTags.CHANGE_VALUE_TAG);
            aChange.data = new Change.Value[dataNodes.size()];
            for (int i = 0; i < dataNodes.size(); i++) {
                aChange.data[i] = readValue(dataNodes.get(i));
            }
            List<ProtoNode> keysNodes = node.getChildren(ChangesTags.CHANGE_KEY_TAG);
            aChange.keys = new Change.Value[keysNodes.size()];
            for (int i = 0; i < keysNodes.size(); i++) {
                aChange.keys[i] = readValue(keysNodes.get(i));
            }
        }

        @Override
        public void visit(Delete aChange) throws Exception {
            List<ProtoNode> keysNodes = node.getChildren(ChangesTags.CHANGE_KEY_TAG);
            aChange.keys = new Change.Value[keysNodes.size()];
            for (int i = 0; i < keysNodes.size(); i++) {
                aChange.keys[i] = readValue(keysNodes.get(i));
            }
        }

        @Override
        public void visit(Command aChange) throws Exception {
            List<ProtoNode> keysNodes = node.getChildren(ChangesTags.CHANGE_PARAMETER_TAG);
            aChange.parameters = new Change.Value[keysNodes.size()];
            for (int i = 0; i < keysNodes.size(); i++) {
                aChange.parameters[i] = readValue(keysNodes.get(i));
            }
        }
    }

    protected static Change readChange(ProtoNode aNode, RowsetSerializer aSerializer) throws Exception {
        ChangeReader reader = new ChangeReader(aNode, aSerializer);
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

    public static List<Change> read(byte[] aData, RowsetSerializer aSerializer) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(aData);
        List<ProtoNode> changesNodes = dom.getChildren(ChangesTags.CHANGE_TAG);
        List<Change> read = new ArrayList<>();
        for (int i = 0; i < changesNodes.size(); i++) {
            read.add(readChange(changesNodes.get(i), aSerializer));
        }
        return read;
    }
}
