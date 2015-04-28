/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.serial;

import java.util.Date;
import java.util.List;

import com.eas.client.changes.Change;
import com.eas.client.changes.ChangeVisitor;
import com.eas.client.changes.Command;
import com.eas.client.changes.Delete;
import com.eas.client.changes.Insert;
import com.eas.client.changes.Update;
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
public class ChangeWriter implements ChangeVisitor {

	private static final String CHANGE_KIND_NAME = "kind";
	private static final String CHANGE_ENTITY_NAME = "entity";
	private static final String CHANGE_DATA_NAME = "data";
	private static final String CHANGE_KEYS_NAME = "keys";
	private static final String CHANGE_PARAMETERS_NAME = "parameters";

	private static JSONValue adoptValue(Change.Value aValue) throws Exception {
		if (aValue != null && aValue.getValue() != null) {
			if (aValue.getValue() instanceof Boolean) {
				return JSONBoolean.getInstance((Boolean) aValue.getValue());
			} else if (aValue.getValue() instanceof Number) {
				return new JSONNumber(((Number) aValue.getValue()).doubleValue());
			} else if (aValue.getValue() instanceof String) {
				return new JSONString((String) aValue.getValue());
			} else if (aValue.getValue() instanceof Date) {
				double millis = ((Date) aValue.getValue()).getTime();
				return new JSONObject(JsDate.create(millis));
			} else {
				throw new Exception("Value with name: " + aValue.getName() + " is of unsupported class: " + aValue.getValue().getClass().getSimpleName());
			}
		} else {
			return JSONNull.getInstance();
		}
	}

	public static String writeLog(List<Change> aLog) throws Exception {
		JSONArray changes = new JSONArray();
		for (int i = 0; i < aLog.size(); i++) {
			ChangeWriter writer = new ChangeWriter();
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
		jsoned.put(CHANGE_ENTITY_NAME, new JSONString(aChange.getEntityName()));
		JSONObject data = new JSONObject();
		jsoned.put(CHANGE_DATA_NAME, data);
		List<Change.Value> chData = aChange.getData();
		for (int i = 0; i < chData.size(); i++) {
			Change.Value v = chData.get(i);
			data.put(v.getName(), adoptValue(v));
		}
	}

	@Override
	public void visit(Update aChange) throws Exception {
		jsoned = new JSONObject();
		jsoned.put(CHANGE_KIND_NAME, new JSONString("update"));
		jsoned.put(CHANGE_ENTITY_NAME, new JSONString(aChange.getEntityName()));
		JSONObject data = new JSONObject();
		jsoned.put(CHANGE_DATA_NAME, data);
		List<Change.Value> chData = aChange.getData();
		for (int i = 0; i < chData.size(); i++) {
			Change.Value v = chData.get(i);
			data.put(v.getName(), adoptValue(v));
		}

		JSONObject keys = new JSONObject();
		jsoned.put(CHANGE_KEYS_NAME, keys);
		List<Change.Value> chKeys = aChange.getKeys();
		for (int i = 0; i < chKeys.size(); i++) {
			Change.Value v = chKeys.get(i);
			keys.put(v.getName(), adoptValue(v));
		}
	}

	@Override
	public void visit(Delete aChange) throws Exception {
		jsoned = new JSONObject();
		jsoned.put(CHANGE_KIND_NAME, new JSONString("delete"));
		jsoned.put(CHANGE_ENTITY_NAME, new JSONString(aChange.getEntityName()));
		JSONObject keys = new JSONObject();
		jsoned.put(CHANGE_KEYS_NAME, keys);

		List<Change.Value> chKeys = aChange.getKeys();
		for (int i = 0; i < chKeys.size(); i++) {
			Change.Value v = chKeys.get(i);
			keys.put(v.getName(), adoptValue(v));
		}
	}

	@Override
	public void visit(Command aChange) throws Exception {
		jsoned = new JSONObject();
		jsoned.put(CHANGE_KIND_NAME, new JSONString("command"));
		jsoned.put(CHANGE_ENTITY_NAME, new JSONString(aChange.getEntityName()));
		JSONObject parameters = new JSONObject();
		jsoned.put(CHANGE_PARAMETERS_NAME, parameters);
		List<Change.Value> chParameters = aChange.getParameters();
		for (int i = 0; i < chParameters.size(); i++) {
			Change.Value v = chParameters.get(i);
			parameters.put(v.getName(), adoptValue(v));
		}
	}
}
