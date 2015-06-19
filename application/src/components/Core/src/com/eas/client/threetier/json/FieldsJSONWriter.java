/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.json;

import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.util.JSONUtils;

/**
 *
 * @author mg
 */
public class FieldsJSONWriter {

    // Field props
    private static final String NAME_PROP_NAME = "name";
    private static final String DESCRIPTION_PROP_NAME = "description";
    private static final String SIZE_PROP_NAME = "size";
    private static final String TYPE_PROP_NAME = "type";
    private static final String PK_PROP_NAME = "pk";
    private static final String NULLABLE_PROP_NAME = "nullable";
    // Type info props
    private static final String TYPE_ID_PROP_NAME = "typeid";
    private static final String TYPE_NAME_PROP_NAME = "typename";

    public static StringBuilder fields2a(Fields aFields) {
        StringBuilder[] res = new StringBuilder[aFields.getFieldsCount()];
        for (int i = 0; i < aFields.getFieldsCount(); i++) {
            res[i] = writeField(aFields.get(i + 1));
        }
        return JSONUtils.a(res);
    }

    private static StringBuilder writeTypeInfo(DataTypeInfo aTypeInfo) {
        StringBuilder sb = JSONUtils.o(
                new StringBuilder(TYPE_ID_PROP_NAME), new StringBuilder(String.valueOf(aTypeInfo.getSqlType())),
                new StringBuilder(TYPE_NAME_PROP_NAME), JSONUtils.s(aTypeInfo.getSqlTypeName()));
        return sb;
    }

    private static StringBuilder writeField(Field aField) {
        StringBuilder sb = JSONUtils.o(
                new StringBuilder(NAME_PROP_NAME), JSONUtils.s(aField.getName()),
                new StringBuilder(DESCRIPTION_PROP_NAME), JSONUtils.s(aField.getDescription()),
                new StringBuilder(SIZE_PROP_NAME), new StringBuilder(String.valueOf(aField.getSize())),
                new StringBuilder(TYPE_PROP_NAME), writeTypeInfo(aField.getTypeInfo()),
                new StringBuilder(PK_PROP_NAME), new StringBuilder(String.valueOf(aField.isPk())),
                new StringBuilder(NULLABLE_PROP_NAME), new StringBuilder(String.valueOf(aField.isNullable())));
        return sb;
    }
}
