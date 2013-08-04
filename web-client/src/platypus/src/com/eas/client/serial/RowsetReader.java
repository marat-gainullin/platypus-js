/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.serial;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.*;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mg
 */
public class RowsetReader extends JsonReader {

	private static DateTimeFormat ISO_DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss");
	
    public static Rowset read(JSONValue aValue, Fields aExpectedFields) throws Exception {
    	assert aExpectedFields != null;
        JSONArray da = aValue.isArray();
        Rowset rowset = new Rowset(aExpectedFields);
        List<Row> rows = new ArrayList();
        for (int i = 0; i < da.size(); i++) {
            Row row = readRow(da.get(i), rowset.getFields());
            rows.add(row);
        }
        rowset.setCurrent(rows);
        return rowset;
    }

    private static Row readRow(JSONValue aValue, Fields aFields) throws Exception {
        JSONObject ro = aValue.isObject();
        Row row = new Row(aFields);
        List<Object> currentValues = row.getInternalCurrentValues();
        assert currentValues.size() == aFields.getFieldsCount();
        assert ro.size() == currentValues.size();
        for (String propName : ro.keySet()) {
            int colIndex = aFields.find(propName);
            currentValues.set(colIndex - 1, adoptValue(ro.get(propName), aFields.get(colIndex)));
        }
        row.currentToOriginal();
        row.clearDeleted();
        row.clearInserted();
        row.clearUpdated();
        return row;
    }

    private static Object adoptValue(JSONValue aValue, Field aField) throws Exception {
        if (aValue != null && aValue.isNull() == null) {
            if (aValue.isArray() != null) {
                throw new Exception("Arrays are not allowed as column values in platypus rowsets.");
            } else {
                JSONBoolean jsb = aValue.isBoolean();
                if (jsb != null) {
                    return jsb.booleanValue();
                } else {
                    JSONNumber jsn = aValue.isNumber();
                    if (jsn != null) {
                        return jsn.doubleValue();
                    } else {
                        JSONString jss = aValue.isString();
                        if (jss != null) {
                            String sVal = jss.stringValue();
                            if(aField.getTypeInfo().getType() == Types.TIMESTAMP || aField.getTypeInfo().getType() == Types.TIME || aField.getTypeInfo().getType() == Types.DATE)
                            {
                            	return ISO_DATE_FORMAT.parse(sVal);
                            }else
                            	return sVal;
                        } else {
                            JSONObject jso = aValue.isObject();
                            if (jso != null) {
                                throw new Exception("Object are not supported yet as column value in platypus rowsets.");
                            } else {
                                throw new Exception("Value of unknkown type occured while adopting json value into platypus rowset");
                            }
                        }
                    }
                }
            }
        } else {
            return null;
        }
    }
}
