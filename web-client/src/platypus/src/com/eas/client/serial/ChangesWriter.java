/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.serial;

import java.util.Date;
import java.util.List;

import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.changes.ChangeVisitor;
import com.bearsoft.rowset.changes.Command;
import com.bearsoft.rowset.changes.Delete;
import com.bearsoft.rowset.changes.Insert;
import com.bearsoft.rowset.changes.Update;
import com.google.gwt.core.client.JsDate;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * 
 * @author mg
 */
public class ChangesWriter implements ChangeVisitor {

	private static final String CHANGE_KIND_NAME = "kind";
	private static final String CHANGE_ENTITY_NAME = "entity";
	private static final String CHANGE_DATA_NAME = "data";
	private static final String CHANGE_KEYS_NAME = "keys";
	private static final String CHANGE_PARAMETERS_NAME = "parameters";

	private static JSONValue adoptValue(Object aValue) throws Exception {
		if (aValue != null) {
			if (aValue instanceof Boolean) {
				return JSONBoolean.getInstance((Boolean) aValue);
			} else if (aValue instanceof Number) {
				return new JSONNumber(((Number) aValue).doubleValue());
			} else if (aValue instanceof String) {
				return new JSONString((String) aValue);
			} else if (aValue instanceof Date) {
				double millis = ((Date) aValue).getTime();
				return new JSONObject(JsDate.create(millis));
			} else {
				throw new Exception("Value of unknown or unsupported type found! It's class is: " + aValue.getClass().getName());
			}
		} else {
			return JSONNull.getInstance();
		}
	}

	public static String writeLog(List<Change> aLog) throws Exception {
		JSONArray changes = new JSONArray();
		for (int i = 0; i < aLog.size(); i++) {
			ChangesWriter writer = new ChangesWriter();
			aLog.get(i).accept(writer);
			changes.set(changes.size(), writer.jsoned);
		}
		return changes.toString();
	}

	protected JSONObject jsoned;

	@Override
	public void visit(Insert aChange) throws Exception {
		jsoned = new JSONObject();
		jsoned.put(CHANGE_KIND_NAME, new JSONString("insert"));
		jsoned.put(CHANGE_ENTITY_NAME, new JSONString(aChange.entityId));
		JSONObject data = new JSONObject();
		jsoned.put(CHANGE_DATA_NAME, data);
		for (int i = 0; i < aChange.data.length; i++) {
			data.put(aChange.data[i].name, adoptValue(aChange.data[i].value));
		}
	}

	@Override
	public void visit(Update aChange) throws Exception {
		jsoned = new JSONObject();
		jsoned.put(CHANGE_KIND_NAME, new JSONString("update"));
		jsoned.put(CHANGE_ENTITY_NAME, new JSONString(aChange.entityId));
		JSONObject data = new JSONObject();
		jsoned.put(CHANGE_DATA_NAME, data);
		for (int i = 0; i < aChange.data.length; i++) {
			data.put(aChange.data[i].name, adoptValue(aChange.data[i].value));
		}
		JSONObject keys = new JSONObject();
		jsoned.put(CHANGE_KEYS_NAME, keys);
		for (int i = 0; i < aChange.keys.length; i++) {
			keys.put(aChange.keys[i].name, adoptValue(aChange.keys[i].value));
		}
	}

	@Override
	public void visit(Delete aChange) throws Exception {
		jsoned = new JSONObject();
		jsoned.put(CHANGE_KIND_NAME, new JSONString("delete"));
		jsoned.put(CHANGE_ENTITY_NAME, new JSONString(aChange.entityId));
		JSONObject keys = new JSONObject();
		jsoned.put(CHANGE_KEYS_NAME, keys);
		for (int i = 0; i < aChange.keys.length; i++) {
			keys.put(aChange.keys[i].name, adoptValue(aChange.keys[i].value));
		}
	}

	@Override
	public void visit(Command aChange) throws Exception {
		jsoned = new JSONObject();
		jsoned.put(CHANGE_KIND_NAME, new JSONString("command"));
		jsoned.put(CHANGE_ENTITY_NAME, new JSONString(aChange.entityId));
		JSONObject parameters = new JSONObject();
		jsoned.put(CHANGE_PARAMETERS_NAME, parameters);
		for (int i = 0; i < aChange.parameters.length; i++) {
			parameters.put(aChange.parameters[i].name, adoptValue(aChange.parameters[i].value));
		}
	}
}
