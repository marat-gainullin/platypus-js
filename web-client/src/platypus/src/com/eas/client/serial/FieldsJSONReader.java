/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.serial;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

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
    
    public static void readFields(JSONArray pa, Fields aFields) {
        for (int i = 0; i < pa.size(); i++) {
            JSONValue pv = pa.get(i);
            assert pv != null;
            JSONObject po = pv.isObject();
            assert po != null;
            assert po.containsKey(NAME_PROP_NAME);
            assert po.containsKey(DESCRIPTION_PROP_NAME);
            assert po.containsKey(SIZE_PROP_NAME);
            assert po.containsKey(TYPE_PROP_NAME);
            assert po.containsKey(PK_PROP_NAME);
            assert po.containsKey(NULLABLE_PROP_NAME);
            String name = po.get(NAME_PROP_NAME).isString().stringValue();
            String desc = po.get(DESCRIPTION_PROP_NAME).isString().stringValue();
            int size = (int) po.get(SIZE_PROP_NAME).isNumber().doubleValue();
            JSONObject to = po.get(TYPE_PROP_NAME).isObject();
            assert to != null;
            assert to.containsKey(TYPE_ID_PROP_NAME);
            assert to.containsKey(TYPE_NAME_PROP_NAME);

            DataTypeInfo typeInfo = new DataTypeInfo(
                    (int) to.get(TYPE_ID_PROP_NAME).isNumber().doubleValue(),
                    to.get(TYPE_NAME_PROP_NAME).isString().stringValue());
            boolean pk = po.get(PK_PROP_NAME).isBoolean().booleanValue();
            boolean nullable = po.get(NULLABLE_PROP_NAME).isBoolean().booleanValue();
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
