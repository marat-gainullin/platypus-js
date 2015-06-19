/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.json;

import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 */
public class FieldsJSONReader {    
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
    
    public static void readFields(JSObject pa, Fields aFields) {
        int length = JSType.toInteger(pa.getMember("length"));
        for (int i = 0; i < length; i++) {
            JSObject po = (JSObject)pa.getSlot(i);
            assert po != null;
            String name = JSType.toString(po.getMember(NAME_PROP_NAME));
            String desc = JSType.toString(po.getMember(DESCRIPTION_PROP_NAME));
            int size = JSType.toInteger(po.getMember(SIZE_PROP_NAME));
            
            JSObject to = (JSObject)po.getMember(TYPE_PROP_NAME);
            assert to != null;

            int sqlType = JSType.toInteger(to.getMember(TYPE_ID_PROP_NAME));
            DataTypeInfo typeInfo = DataTypeInfo.valueOf(sqlType);
            
            boolean pk = JSType.toBoolean(po.getMember(PK_PROP_NAME));
            boolean nullable = JSType.toBoolean(po.getMember(NULLABLE_PROP_NAME));
            Field f = aFields.createNewField(name);
            f.setDescription(desc);
            f.setSize(size);
            f.setTypeInfo(typeInfo);
            f.setPk(pk);
            f.setNullable(nullable);
            aFields.add(f);
        }
    }
}
