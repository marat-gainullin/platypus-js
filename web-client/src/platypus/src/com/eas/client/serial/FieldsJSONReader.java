/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.serial;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.core.Utils.JsObject;
import com.google.gwt.core.client.JavaScriptObject;

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
    
    public static void readFields(JavaScriptObject opa, Fields aFields) {
    	JsObject pa = opa.cast(); 
        for (int i = 0; i < pa.length(); i++) {
            JavaScriptObject pv = pa.getSlot(i);
            assert pv != null;
            JsObject po = pv.cast();
            assert po != null;
            assert po.has(NAME_PROP_NAME);
            assert po.has(DESCRIPTION_PROP_NAME);
            assert po.has(TYPE_PROP_NAME);
            assert po.has(PK_PROP_NAME);
            assert po.has(NULLABLE_PROP_NAME);
            String name = po.getString(NAME_PROP_NAME);
            String desc = po.getString(DESCRIPTION_PROP_NAME);
            String type = po.getString(TYPE_PROP_NAME);
            boolean pk = po.getBoolean(PK_PROP_NAME);
            boolean nullable = po.getBoolean(NULLABLE_PROP_NAME);
            Field f = aFields.createNewField(name);
            f.setDescription(desc);
            f.setType(type);
            f.setPk(pk);
            f.setNullable(nullable);
            aFields.add(f);
        }
    }
}
