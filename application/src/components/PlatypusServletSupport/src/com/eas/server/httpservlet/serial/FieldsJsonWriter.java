/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet.serial;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.util.JSONUtils;

/**
 *
 * @author mg
 */
public class JsonWriter {

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

    public static String fields2a(Fields aFields) {
        StringBuilder sb = new StringBuilder();
        String[] res = new String[aFields.getFieldsCount()];
        for (int i = 1; i <= aFields.getFieldsCount(); i++) {
            res[i-1] = writeField(aFields.get(i));
        }
        return JSONUtils.a(sb, res);
    }

    private static String writeTypeInfo(DataTypeInfo aTypeInfo) {
        StringBuilder sb = new StringBuilder();
        JSONUtils.o(sb,
                TYPE_ID_PROP_NAME, String.valueOf(aTypeInfo.getSqlType()),
                TYPE_NAME_PROP_NAME, JSONUtils.s(aTypeInfo.getSqlTypeName()));
        return sb.toString();
    }

    private static String writeField(Field aField) {
        StringBuilder sb = new StringBuilder();
        JSONUtils.o(sb,
                NAME_PROP_NAME, JSONUtils.s(aField.getName()),
                DESCRIPTION_PROP_NAME, JSONUtils.s(aField.getDescription()),
                SIZE_PROP_NAME, String.valueOf(aField.getSize()),
                TYPE_PROP_NAME, writeTypeInfo(aField.getTypeInfo()),
                PK_PROP_NAME, String.valueOf(aField.isPk()),
                NULLABLE_PROP_NAME, String.valueOf(aField.isNullable()));
        return sb.toString();
    }
}
