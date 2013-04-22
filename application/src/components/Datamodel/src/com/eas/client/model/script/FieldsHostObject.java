/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.script;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.model.RowsetMissingException;
import com.eas.script.NativeJavaHostObject;
import com.eas.script.ScriptUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author mg
 */
public class FieldsHostObject extends NativeJavaHostObject {

    protected Fields fields;

    private FieldsHostObject(Fields aFields, Scriptable aScope) throws RowsetMissingException {
        super(aScope, aFields, null);
        fields = aFields;
        defineProperties();
    }

    private void defineProperties() throws RowsetMissingException {
        for (int i = 1; i <= fields.getFieldsCount(); i++) {
            Field field = fields.get(i);
            String fName = field.getName();
            if (fName != null && !fName.isEmpty()) {
                Object scField = ScriptUtils.javaToJS(field, this);
                field.setTag(scField);
                defineProperty(fName, scField);
            }
        }
    }
    
    @Override
    public Object[] getIds() {
        try {
            Integer[] indexes = new Integer[fields.getLength()];
            for (int i = 0; i < indexes.length; i++) {
                indexes[i] = i;
            }
            return indexes;
        } catch (Exception ex) {
            Logger.getLogger(RowsetHostObject.class.getName()).log(Level.SEVERE, null, ex);
            return super.getIds();
        }
    }
    
    @Override
    public boolean has(int aIndex, Scriptable aStart) {
        try {
            return 0 <= aIndex && aIndex < fields.getLength();
        } catch (Exception ex) {
            Logger.getLogger(FieldsHostObject.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public Object get(int aIndex, Scriptable start) {
        if (has(aIndex, start)) {
            try {
                return fields.get(aIndex + 1);
            } catch (Exception ex) {
                Logger.getLogger(FieldsHostObject.class.getName()).log(Level.SEVERE, null, ex);
                return NOT_FOUND;
            }
        } else {
            return NOT_FOUND;
        }
    }
    
    public static FieldsHostObject publishFields(Fields aFields, Scriptable aScope) throws Exception
    {
        FieldsHostObject published = (FieldsHostObject)aFields.getTag();
        if(published == null)
        {
            published = new FieldsHostObject(aFields, aScope);
            aFields.setTag(published);
        }
        return published;
    }

}
