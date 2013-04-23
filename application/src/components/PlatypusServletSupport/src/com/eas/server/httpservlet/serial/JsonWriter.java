/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet.serial;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;

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

    public static String s(String aPropValue) {
        StringBuilder sb = new StringBuilder();
        aPropValue = aPropValue == null ? "null" : aPropValue.replace("\\", "\\\\").replace("\"", "\\\"").replace("\t", "\\t").replace("\r", "").replace("\n", "\\n");
        sb.append("\"").append(aPropValue).append("\"");
        return sb.toString();
    }

    public static StringBuilder p(StringBuilder sb, String aPropName, String aPropValue) {
        return sb.append("\"").append(aPropName).append("\"").append(":").append(aPropValue);
    }

    public static StringBuilder o(StringBuilder sb, String... aValues) {
        assert aValues != null && aValues.length % 2 == 0;
        sb.append("{");
        for (int i = 0; i < aValues.length; i += 2) {
            if (i > 0) {
                sb.append(",");
            }
            p(sb, aValues[i], aValues[i + 1]);
        }
        sb.append("}");
        return sb;
    }

    public static String a(StringBuilder sb, String... aValues) {
        assert aValues != null;
        sb.append("[");
        for (int i = 0; i < aValues.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(aValues[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    public static String fields2a(Fields aFields) {
        StringBuilder sb = new StringBuilder();
        String[] res = new String[aFields.getFieldsCount()];
        for (int i = 1; i <= aFields.getFieldsCount(); i++) {
            res[i-1] = writeField(aFields.get(i));
        }
        return a(sb, res);
    }

    private static String writeTypeInfo(DataTypeInfo aTypeInfo) {
        StringBuilder sb = new StringBuilder();
        o(sb,
                TYPE_ID_PROP_NAME, String.valueOf(aTypeInfo.getSqlType()),
                TYPE_NAME_PROP_NAME, s(aTypeInfo.getSqlTypeName()));
        return sb.toString();
    }

    private static String writeField(Field aField) {
        StringBuilder sb = new StringBuilder();
        o(sb,
                NAME_PROP_NAME, s(aField.getName()),
                DESCRIPTION_PROP_NAME, s(aField.getDescription()),
                SIZE_PROP_NAME, String.valueOf(aField.getSize()),
                TYPE_PROP_NAME, writeTypeInfo(aField.getTypeInfo()),
                PK_PROP_NAME, String.valueOf(aField.isPk()),
                NULLABLE_PROP_NAME, String.valueOf(aField.isNullable()));
        return sb.toString();
    }
}
