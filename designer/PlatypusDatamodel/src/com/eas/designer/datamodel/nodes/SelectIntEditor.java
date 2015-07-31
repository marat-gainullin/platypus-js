/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.datamodel.nodes;

/**
 *
 * @author vv
 */
import java.beans.PropertyEditorSupport;
import java.util.Arrays;

public class SelectIntEditor extends PropertyEditorSupport {

    private final String[] keys;
    private final int[] values;

    public SelectIntEditor(String[] aKeys, int[] aValues) {
        super();
        if (aKeys == null || aValues == null) {
            throw new IllegalArgumentException("Keys or values arguments are null.");//NOI18N
        }
        if (aKeys.length != aValues.length) {
            throw new IllegalArgumentException("Keys and values arguments must have the save length.");//NOI18N
        }
        keys = aKeys;
        values = aValues;
    }

    @Override
    public String getAsText() {
        Integer val = (Integer) getValue();
        String result;
        if (val != null) {
            int intVal = val;
            int idx = -1;
            for (int j = 0; j < values.length; j++) {
                if (values[j] == intVal) {
                    idx = j;
                    break;
                }
            }
            if (idx != -1) {
                result = keys[idx];
            } else {    
                throw new IllegalStateException(String.format("Value %s is not found in values array.", val.toString()));//NOI18N
            }
        } else {
            result = "";//NOI18N
        }
        return result;
    }

    @Override
    public void setAsText(String s) {
        s = s.trim();
        //use the keys, translate to an int value
        int idx = Arrays.asList(keys).indexOf(s);
        if ((idx == -1) || (idx > values.length - 1)) {
            throw new IllegalArgumentException(String.format("Key %s is not found in keys array.", s));//NOI18N
        } else {
            setValue(values[idx]);
        }

    }

    @Override
    public String[] getTags() {
        return keys;
    }
}
