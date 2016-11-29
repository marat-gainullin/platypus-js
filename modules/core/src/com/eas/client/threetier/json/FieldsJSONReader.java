/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.json;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
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
    private static final String TYPE_PROP_NAME = "type";
    private static final String PK_PROP_NAME = "pk";
    private static final String NULLABLE_PROP_NAME = "nullable";
    
    public static void readFields(JSObject pa, Fields aFields) {
        int length = JSType.toInteger(pa.getMember("length"));
        for (int i = 0; i < length; i++) {
            JSObject po = (JSObject)pa.getSlot(i);
            assert po != null;
            String name = JSType.toString(po.getMember(NAME_PROP_NAME));
            String desc = JSType.toString(po.getMember(DESCRIPTION_PROP_NAME));
            
            String type = po.hasMember(TYPE_PROP_NAME) && po.getMember(TYPE_PROP_NAME) != null ? JSType.toString(po.getMember(TYPE_PROP_NAME)) : null;
            boolean pk = JSType.toBoolean(po.getMember(PK_PROP_NAME));
            boolean nullable = JSType.toBoolean(po.getMember(NULLABLE_PROP_NAME));
            Field f = aFields instanceof Parameters ? new Parameter(name) : new Field(name); 
            f.setDescription(desc);
            f.setType(type);
            f.setPk(pk);
            f.setNullable(nullable);
            aFields.add(f);
        }
    }
}
