/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.metadata;

import com.eas.proto.CoreTags;
import com.eas.proto.ProtoReaderException;
import com.eas.proto.ProtoWriter;
import com.eas.proto.dom.ProtoNode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * RowsetReader descendant, implementing rowset binary serialization behavior.
 *
 * @author mg
 */
public class BinaryFields {

    public BinaryFields() {
        super();
    }

    public static Fields read(ProtoNode aNode) throws ProtoReaderException {
        assert aNode != null;
        List<ProtoNode> fieldNodes = aNode.getChildren(FieldsTags.FIELD);
        Fields fields = new Fields();
        for (ProtoNode fieldNode : fieldNodes) {
            assert fieldNode != null;
            Field field = new Field();
            ProtoNode fNameNode = fieldNode.getChild(FieldsTags.FIELD_NAME);
            if (fNameNode != null) {
                field.setName(fNameNode.getString());
            }
            ProtoNode fDescNode = fieldNode.getChild(FieldsTags.FIELD_DESCRIPTION);
            if (fDescNode != null) {
                field.setDescription(fDescNode.getString());
            }
            DataTypeInfo typeInfo = new DataTypeInfo();
            ProtoNode fTypeNode = fieldNode.getChild(FieldsTags.FIELD_TYPE);
            if (fTypeNode != null) {
                typeInfo.setSqlType(fTypeNode.getInt());
            }
            ProtoNode fTypeNameNode = fieldNode.getChild(FieldsTags.FIELD_TYPENAME);
            if (fTypeNameNode != null) {
                typeInfo.setSqlTypeName(fTypeNameNode.getString());
            }
            ProtoNode fClassNameNode = fieldNode.getChild(FieldsTags.FIELD_CLASSNAME);
            if (fClassNameNode != null) {
                typeInfo.setJavaClassName(fClassNameNode.getString());
            }
            field.setTypeInfo(typeInfo);
            ProtoNode fSizeNode = fieldNode.getChild(FieldsTags.FIELD_SIZE);
            if (fSizeNode != null) {
                field.setSize(fSizeNode.getInt());
            }
            ProtoNode fScaleNode = fieldNode.getChild(FieldsTags.FIELD_SCALE);
            if (fScaleNode != null) {
                field.setScale(fScaleNode.getInt());
            }
            ProtoNode fPrecisionNode = fieldNode.getChild(FieldsTags.FIELD_PRECISION);
            if (fPrecisionNode != null) {
                field.setPrecision(fPrecisionNode.getInt());
            }
            ProtoNode fpkNode = fieldNode.getChild(FieldsTags.FIELD_PK);
            field.setPk(fpkNode != null);
            ProtoNode fSignedNode = fieldNode.getChild(FieldsTags.FIELD_SIGNED);
            field.setSigned(fSignedNode != null);
            ProtoNode fNullableNode = fieldNode.getChild(FieldsTags.FIELD_NULLABLE);
            field.setNullable(fNullableNode != null);
            ProtoNode fReadonlyNode = fieldNode.getChild(FieldsTags.FIELD_READONLY);
            field.setReadonly(fReadonlyNode != null);
            ProtoNode fTableNameNode = fieldNode.getChild(FieldsTags.FIELD_TABLENAME);
            if (fTableNameNode != null) {
                field.setTableName(fTableNameNode.getString());
            }
            ProtoNode fSchamaNameNode = fieldNode.getChild(FieldsTags.FIELD_SCHEMANAME);
            if (fSchamaNameNode != null) {
                field.setSchemaName(fSchamaNameNode.getString());
            }
            ProtoNode fkNode = fieldNode.getChild(FieldsTags.FIELD_FK);
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
                ProtoNode fkSchemaNameNode = fkNode.getChild(FieldsTags.FK_SCHEMANAME);
                if (fkSchemaNameNode != null) {
                    fkSchema = fkSchemaNameNode.getString();
                }
                ProtoNode fkTableNameNode = fkNode.getChild(FieldsTags.FK_TABLENAME);
                if (fkTableNameNode != null) {
                    fkTable = fkTableNameNode.getString();
                }
                ProtoNode fkFieldNameNode = fkNode.getChild(FieldsTags.FK_FIELD_NAME);
                if (fkFieldNameNode != null) {
                    fkField = fkFieldNameNode.getString();
                }
                ProtoNode fkCNameNode = fkNode.getChild(FieldsTags.FK_CNAME);
                if (fkCNameNode != null) {
                    fkCName = fkCNameNode.getString();
                }
                ProtoNode pkSchemaNameNode = fkNode.getChild(FieldsTags.PK_SCHEMANAME);
                if (pkSchemaNameNode != null) {
                    pkSchema = pkSchemaNameNode.getString();
                }
                ProtoNode pkTableNameNode = fkNode.getChild(FieldsTags.PK_TABLENAME);
                if (pkTableNameNode != null) {
                    pkTable = pkTableNameNode.getString();
                }
                ProtoNode pkFieldNameNode = fkNode.getChild(FieldsTags.PK_FIELD_NAME);
                if (pkFieldNameNode != null) {
                    pkField = pkFieldNameNode.getString();
                }
                ProtoNode pkCNameNode = fkNode.getChild(FieldsTags.PK_CNAME);
                if (pkCNameNode != null) {
                    pkCName = pkCNameNode.getString();
                }
                ProtoNode fkUpdateRuleNode = fkNode.getChild(FieldsTags.FK_UPDATE_RULE);
                if (fkUpdateRuleNode != null) {
                    updateRule = Integer.valueOf(fkUpdateRuleNode.getInt()).shortValue();
                }
                ProtoNode fkDeleteRuleNode = fkNode.getChild(FieldsTags.FK_DELETE_RULE);
                if (fkDeleteRuleNode != null) {
                    deleteRule = Integer.valueOf(fkDeleteRuleNode.getInt()).shortValue();
                }
                ProtoNode fkDeferrableNode = fkNode.getChild(FieldsTags.FK_DEFERRABLE);
                if (fkDeferrableNode != null) {
                    deferrable = Integer.valueOf(fkDeferrableNode.getInt()).shortValue();
                }
                field.setFk(new ForeignKeySpec(fkSchema, fkTable, fkField, fkCName, ForeignKeySpec.ForeignKeyRule.valueOf(updateRule), ForeignKeySpec.ForeignKeyRule.valueOf(deleteRule), deferrable == 1, pkSchema, pkTable, pkField, pkCName));
            }
            fields.add(field);
        }
        return fields;
    }
    
    public static void write(Fields aFields, ByteArrayOutputStream aOut) throws IOException {
        ProtoWriter metadataPw = new ProtoWriter(aOut);
        for (int i = 1; i <= aFields.getFieldsCount(); i++) {
            Field field = aFields.get(i);
            ByteArrayOutputStream fieldOut = new ByteArrayOutputStream();
            writeField(field, fieldOut);
            metadataPw.put(FieldsTags.FIELD);
            metadataPw.put(CoreTags.TAG_STREAM, fieldOut);
        }
        metadataPw.flush();
    }

    public static ProtoWriter writeField(Field field, ByteArrayOutputStream aOut) throws IOException {
        ProtoWriter fieldPw = new ProtoWriter(aOut);
        assert field.getName() != null;
        fieldPw.put(FieldsTags.FIELD_NAME, field.getName());
        if (field.getDescription() != null) {
            fieldPw.put(FieldsTags.FIELD_DESCRIPTION, field.getDescription());
        }
        assert field.getTypeInfo() != null;
        fieldPw.put(FieldsTags.FIELD_TYPE, field.getTypeInfo().getSqlType());
        if (field.getTypeInfo().getSqlTypeName() != null) {
            fieldPw.put(FieldsTags.FIELD_TYPENAME, field.getTypeInfo().getSqlTypeName());
        }
        if (field.getTypeInfo().getJavaClassName() != null) {
            fieldPw.put(FieldsTags.FIELD_CLASSNAME, field.getTypeInfo().getJavaClassName());
        }
        fieldPw.put(FieldsTags.FIELD_SIZE, field.getSize());
        fieldPw.put(FieldsTags.FIELD_SCALE, field.getScale());
        fieldPw.put(FieldsTags.FIELD_PRECISION, field.getPrecision());
        if (field.isSigned()) {
            fieldPw.put(FieldsTags.FIELD_SIGNED);
        }
        if (field.isNullable()) {
            fieldPw.put(FieldsTags.FIELD_NULLABLE);
        }
        if (field.isReadonly()) {
            fieldPw.put(FieldsTags.FIELD_READONLY);
        }
        if (field.getTableName() != null) {
            fieldPw.put(FieldsTags.FIELD_TABLENAME, field.getTableName());
        }
        if (field.getSchemaName() != null) {
            fieldPw.put(FieldsTags.FIELD_SCHEMANAME, field.getSchemaName());
        }
        if (field.isPk()) {
            fieldPw.put(FieldsTags.FIELD_PK);
        }
        if (field.isFk()) {
            ByteArrayOutputStream fkOut = new ByteArrayOutputStream();
            ProtoWriter fkPw = new ProtoWriter(fkOut);
            if (field.getFk().getSchema() != null) {
                fkPw.put(FieldsTags.FK_SCHEMANAME, field.getFk().getSchema());
            }
            if (field.getFk().getTable() != null) {
                fkPw.put(FieldsTags.FK_TABLENAME, field.getFk().getTable());
            }
            if (field.getFk().getField() != null) {
                fkPw.put(FieldsTags.FK_FIELD_NAME, field.getFk().getField());
            }
            if (field.getFk().getCName() != null) {
                fkPw.put(FieldsTags.FK_CNAME, field.getFk().getCName());
            }
            fkPw.put(FieldsTags.FK_UPDATE_RULE, Integer.valueOf(field.getFk().getFkUpdateRule().toShort()));
            fkPw.put(FieldsTags.FK_DELETE_RULE, Integer.valueOf(field.getFk().getFkDeleteRule().toShort()));
            fkPw.put(FieldsTags.FK_DEFERRABLE, field.getFk().getFkDeferrable() ? 1 : 0);
            if (field.getFk().getReferee() != null) {
                if (field.getFk().getReferee().getSchema() != null) {
                    fkPw.put(FieldsTags.PK_SCHEMANAME, field.getFk().getReferee().getSchema());
                }
                if (field.getFk().getReferee().getTable() != null) {
                    fkPw.put(FieldsTags.PK_TABLENAME, field.getFk().getReferee().getTable());
                }
                if (field.getFk().getReferee().getField() != null) {
                    fkPw.put(FieldsTags.PK_FIELD_NAME, field.getFk().getReferee().getField());
                }
                if (field.getFk().getReferee().getCName() != null) {
                    fkPw.put(FieldsTags.PK_CNAME, field.getFk().getReferee().getCName());
                }
            }
            fkPw.flush();
            fieldPw.put(FieldsTags.FIELD_FK);
            fieldPw.put(CoreTags.TAG_STREAM, fkOut);
        }
        fieldPw.flush();
        return fieldPw;
    }
}
