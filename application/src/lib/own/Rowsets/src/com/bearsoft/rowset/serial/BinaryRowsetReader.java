/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.serial;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.proto.ProtoReaderException;
import com.eas.proto.dom.ProtoDOMBuilder;
import com.eas.proto.dom.ProtoNode;
import java.io.InputStream;
import java.util.List;

/**
 * RowsetReader descendant, implementing rowset binary serialization behavior.
 *
 * @author mg
 */
public class BinaryRowsetReader extends RowsetReader {

    public BinaryRowsetReader() {
        super();
    }

    public BinaryRowsetReader(Fields aExpectedFields) {
        super(aExpectedFields);
    }

    public Fields parseFieldsNode(ProtoNode aNode) throws ProtoReaderException {
        assert aNode != null;
        List<ProtoNode> fieldNodes = aNode.getChildren(BinaryTags.FIELD);
        Fields fields = new Fields();
        for (ProtoNode fieldNode : fieldNodes) {
            assert fieldNode != null;
            Field field = new Field();
            ProtoNode fNameNode = fieldNode.getChild(BinaryTags.FIELD_NAME);
            if (fNameNode != null) {
                field.setName(fNameNode.getString());
            }
            ProtoNode fDescNode = fieldNode.getChild(BinaryTags.FIELD_DESCRIPTION);
            if (fDescNode != null) {
                field.setDescription(fDescNode.getString());
            }
            DataTypeInfo typeInfo = new DataTypeInfo();
            ProtoNode fTypeNode = fieldNode.getChild(BinaryTags.FIELD_TYPE);
            if (fTypeNode != null) {
                typeInfo.setSqlType(fTypeNode.getInt());
            }
            ProtoNode fTypeNameNode = fieldNode.getChild(BinaryTags.FIELD_TYPENAME);
            if (fTypeNameNode != null) {
                typeInfo.setSqlTypeName(fTypeNameNode.getString());
            }
            ProtoNode fClassNameNode = fieldNode.getChild(BinaryTags.FIELD_CLASSNAME);
            if (fClassNameNode != null) {
                typeInfo.setJavaClassName(fClassNameNode.getString());
            }
            field.setTypeInfo(typeInfo);
            ProtoNode fSizeNode = fieldNode.getChild(BinaryTags.FIELD_SIZE);
            if (fSizeNode != null) {
                field.setSize(fSizeNode.getInt());
            }
            ProtoNode fScaleNode = fieldNode.getChild(BinaryTags.FIELD_SCALE);
            if (fScaleNode != null) {
                field.setScale(fScaleNode.getInt());
            }
            ProtoNode fPrecisionNode = fieldNode.getChild(BinaryTags.FIELD_PRECISION);
            if (fPrecisionNode != null) {
                field.setPrecision(fPrecisionNode.getInt());
            }
            ProtoNode fpkNode = fieldNode.getChild(BinaryTags.FIELD_PK);
            field.setPk(fpkNode != null);
            ProtoNode fSignedNode = fieldNode.getChild(BinaryTags.FIELD_SIGNED);
            field.setSigned(fSignedNode != null);
            ProtoNode fNullableNode = fieldNode.getChild(BinaryTags.FIELD_NULLABLE);
            field.setNullable(fNullableNode != null);
            ProtoNode fReadonlyNode = fieldNode.getChild(BinaryTags.FIELD_READONLY);
            field.setReadonly(fReadonlyNode != null);
            ProtoNode fTableNameNode = fieldNode.getChild(BinaryTags.FIELD_TABLENAME);
            if (fTableNameNode != null) {
                field.setTableName(fTableNameNode.getString());
            }
            ProtoNode fSchamaNameNode = fieldNode.getChild(BinaryTags.FIELD_SCHEMANAME);
            if (fSchamaNameNode != null) {
                field.setSchemaName(fSchamaNameNode.getString());
            }
            ProtoNode fkNode = fieldNode.getChild(BinaryTags.FIELD_FK);
            if (fkNode != null) {
                String fkSchema = null;
                String fkTable = null;
                String fkField = null;
                String pkSchema = null;
                String pkTable = null;
                String pkField = null;
                short updateRule = 0;
                short deleteRule = 0;
                short deferrable = 0;
                String pkCName = null;
                String fkCName = null;
                ProtoNode fkSchemaNameNode = fkNode.getChild(BinaryTags.FK_SCHEMANAME);
                if (fkSchemaNameNode != null) {
                    fkSchema = fkSchemaNameNode.getString();
                }
                ProtoNode fkTableNameNode = fkNode.getChild(BinaryTags.FK_TABLENAME);
                if (fkTableNameNode != null) {
                    fkTable = fkTableNameNode.getString();
                }
                ProtoNode fkFieldNameNode = fkNode.getChild(BinaryTags.FK_FIELD_NAME);
                if (fkFieldNameNode != null) {
                    fkField = fkFieldNameNode.getString();
                }
                ProtoNode fkCNameNode = fkNode.getChild(BinaryTags.FK_CNAME);
                if (fkCNameNode != null) {
                    fkCName = fkCNameNode.getString();
                }
                ProtoNode pkSchemaNameNode = fkNode.getChild(BinaryTags.PK_SCHEMANAME);
                if (pkSchemaNameNode != null) {
                    pkSchema = pkSchemaNameNode.getString();
                }
                ProtoNode pkTableNameNode = fkNode.getChild(BinaryTags.PK_TABLENAME);
                if (pkTableNameNode != null) {
                    pkTable = pkTableNameNode.getString();
                }
                ProtoNode pkFieldNameNode = fkNode.getChild(BinaryTags.PK_FIELD_NAME);
                if (pkFieldNameNode != null) {
                    pkField = pkFieldNameNode.getString();
                }
                ProtoNode pkCNameNode = fkNode.getChild(BinaryTags.PK_CNAME);
                if (pkCNameNode != null) {
                    pkCName = pkCNameNode.getString();
                }
                ProtoNode fkUpdateRuleNode = fkNode.getChild(BinaryTags.FK_UPDATE_RULE);
                if (fkUpdateRuleNode != null) {
                    updateRule = Integer.valueOf(fkUpdateRuleNode.getInt()).shortValue();
                }
                ProtoNode fkDeleteRuleNode = fkNode.getChild(BinaryTags.FK_DELETE_RULE);
                if (fkDeleteRuleNode != null) {
                    deleteRule = Integer.valueOf(fkDeleteRuleNode.getInt()).shortValue();
                }
                ProtoNode fkDeferrableNode = fkNode.getChild(BinaryTags.FK_DEFERRABLE);
                if (fkDeferrableNode != null) {
                    deferrable = Integer.valueOf(fkDeferrableNode.getInt()).shortValue();
                }
                field.setFk(new ForeignKeySpec(fkSchema, fkTable, fkField, fkCName, ForeignKeySpec.ForeignKeyRule.valueOf(updateRule), ForeignKeySpec.ForeignKeyRule.valueOf(deleteRule), deferrable == 1, pkSchema, pkTable, pkField, pkCName));
            }
            fields.add(field);
        }
        return fields;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Rowset read(byte[] aData) throws RowsetException {
        try {
            return parseDom(ProtoDOMBuilder.buildDOM(aData));
        } catch (Exception ex) {
            if (ex instanceof RowsetException) {
                throw (RowsetException) ex;
            } else {
                throw new RowsetException(ex);
            }
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public Rowset read(InputStream aStream) throws RowsetException {
        try {
            return read(RowsetUtils.readStream(aStream, -1));
        } catch (Exception ex) {
            if (ex instanceof RowsetException) {
                throw (RowsetException) ex;
            } else {
                throw new RowsetException(ex);
            }
        }
    }

    public Rowset parseDom(ProtoNode rowsetNode) throws RowsetException {
        Rowset rowset = expectedFields != null ? new Rowset(expectedFields) : new Rowset();
        try {
            // properties
            /*
             ProtoNode sessionIdNode = rowsetNode.getChild(BinaryTags.SESSION);
             if (sessionIdNode != null) {
             rowset.setSessionId(sessionIdNode.getString());
             }
             ProtoNode transactedNode = rowsetNode.getChild(BinaryTags.TRANSACTED);
             rowset.setTransacted(transactedNode != null);
             */

            int cursorPos = 0;
            ProtoNode cursorPosIdNode = rowsetNode.getChild(BinaryTags.CURSOR_POSITION);
            if (cursorPosIdNode != null) {
                cursorPos = cursorPosIdNode.getInt();
            }
            // fields
            if (expectedFields == null) {
                ProtoNode metadataNode = rowsetNode.getChild(BinaryTags.METADATA);
                Fields fields = parseFieldsNode(metadataNode);
                rowset.setFields(fields);
            }
            // data
            ProtoNode currentDataNode = rowsetNode.getChild(BinaryTags.DATA);
            readData(rowset, currentDataNode);
            // position
            rowset.absolute(cursorPos);
        } catch (Exception ex) {
            if (ex instanceof RowsetException) {
                throw (RowsetException) ex;
            } else {
                throw new RowsetException(ex);
            }
        }
        return rowset;
    }

    /**
     * Method reads data in passed rowset, from part of the binary dom.
     *
     * @param aRowset Rowset, the data to read to.
     * @param dataNode Node of the dom to read data from.
     * @throws ProtoReaderException
     * @throws RowsetException
     */
    protected void readData(Rowset aRowset, ProtoNode dataNode) throws ProtoReaderException, RowsetException {
        if (dataNode != null) {
            Fields fields = aRowset.getFields();
            List<CustomSerializer> customSerializers = achieveCustomSerializers(fields);
            List<ProtoNode> rowsNodes = dataNode.getChildren(BinaryTags.ROW);
            for (int i = 0; i < rowsNodes.size(); i++) {
                ProtoNode rowNode = rowsNodes.get(i);
                if (rowNode != null) {
                    // row flags
                    Row row = new Row("", fields);
                    // row values
                    List<ProtoNode> valuesNodes = rowNode.getChildren(BinaryTags.ORIGINAL_VALUE);
                    readRowValues(fields, customSerializers, row, valuesNodes);
                    row.currentToOriginal();
                    valuesNodes = rowNode.getChildren(BinaryTags.CURRENT_VALUE);
                    readRowValues(fields, customSerializers, row, valuesNodes);
                    // updated flags
                    row.clearUpdated();
                    aRowset.insert(row, true);
                    row.clearInserted();
                }
            }
        }
    }

    private static void readRowValues(Fields aFields, List<CustomSerializer> customSerializers, Row row, List<ProtoNode> valuesNodes) throws InvalidColIndexException, RowsetException, ProtoReaderException {
        if (aFields.getFieldsCount() != valuesNodes.size()) {
            throw new RowsetException("Fields count and data values count must be the same.");
        }
        for (int j = 1; j <= aFields.getFieldsCount(); j++) {
            Field field = aFields.get(j);
            ProtoNode valueNode = valuesNodes.get(j - 1);
            if (valueNode.getSize() == 0) {
                row.setColumnObject(j, null);
            } else {
                CustomSerializer lSerializer = customSerializers.get(j - 1);
                if (lSerializer != null) {
                    Object lValue = lSerializer.deserialize(valueNode.getData(), valueNode.getOffset(), valueNode.getSize(), field.getTypeInfo());
                    row.setColumnObject(j, lValue);
                } else {
                    Object lValue = valueNode.getJDBCCompatible(field.getTypeInfo().getSqlType());
                    row.setColumnObject(j, lValue);
                }
            }
        }
    }
}
