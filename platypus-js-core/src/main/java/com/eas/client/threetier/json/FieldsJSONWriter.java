/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.json;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.util.JsonUtils;

/**
 *
 * @author mg
 */
public class FieldsJSONWriter {

    // Field props
    private static final String NAME_PROP_NAME = "name";
    private static final String DESCRIPTION_PROP_NAME = "description";
    private static final String TYPE_PROP_NAME = "type";
    private static final String PK_PROP_NAME = "pk";
    private static final String NULLABLE_PROP_NAME = "nullable";

    public static StringBuilder fields2a(Fields aFields) {
        StringBuilder[] res = new StringBuilder[aFields.getFieldsCount()];
        for (int i = 0; i < aFields.getFieldsCount(); i++) {
            res[i] = writeField(aFields.get(i + 1));
        }
        return JsonUtils.a(res);
    }

    private static StringBuilder writeField(Field aField) {
        StringBuilder sb = JsonUtils.o(new StringBuilder(NAME_PROP_NAME), JsonUtils.s(aField.getName()),
                new StringBuilder(DESCRIPTION_PROP_NAME), JsonUtils.s(aField.getDescription()),
                new StringBuilder(TYPE_PROP_NAME), JsonUtils.s(aField.getType()),
                new StringBuilder(PK_PROP_NAME), new StringBuilder(String.valueOf(aField.isPk())),
                new StringBuilder(NULLABLE_PROP_NAME), new StringBuilder(String.valueOf(aField.isNullable())));
        return sb;
    }
}
