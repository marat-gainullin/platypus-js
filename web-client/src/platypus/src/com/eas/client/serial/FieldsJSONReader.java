/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.serial;

import com.eas.client.Utils.JsObject;
import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.google.gwt.core.client.JavaScriptObject;

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
    
    public static void readFields(JavaScriptObject opa, Fields aFields) {
    	JsObject pa = opa.cast(); 
        for (int i = 0; i < pa.length(); i++) {
            JavaScriptObject pv = pa.getSlot(i);
            assert pv != null;
            JsObject po = pv.cast();
            assert po != null;
            assert po.has(NAME_PROP_NAME);
            assert po.has(DESCRIPTION_PROP_NAME);
            assert po.has(SIZE_PROP_NAME);
            assert po.has(TYPE_PROP_NAME);
            assert po.has(PK_PROP_NAME);
            assert po.has(NULLABLE_PROP_NAME);
            String name = po.getString(NAME_PROP_NAME);
            String desc = po.getString(DESCRIPTION_PROP_NAME);
            int size = po.getInteger(SIZE_PROP_NAME);
            JavaScriptObject _to = po.getJs(TYPE_PROP_NAME);
            assert _to != null;
            JsObject to = _to.cast();
            assert to.has(TYPE_ID_PROP_NAME);
            assert to.has(TYPE_NAME_PROP_NAME);

            DataTypeInfo typeInfo = new DataTypeInfo(
                    to.getInteger(TYPE_ID_PROP_NAME),
                    to.getString(TYPE_NAME_PROP_NAME));
            boolean pk = po.getBoolean(PK_PROP_NAME);
            boolean nullable = po.getBoolean(NULLABLE_PROP_NAME);
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
