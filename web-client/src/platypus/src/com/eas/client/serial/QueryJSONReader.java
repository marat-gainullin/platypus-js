/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.serial;

import com.eas.client.Utils.JsObject;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameters;
import com.eas.client.queries.Query;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @author mg
 */
public class QueryJSONReader {

	private static final String TITLE_PROP_NAME = "title";
	private static final String MANUAL_PROP_NAME = "manual";
	private static final String APP_ELEMENT_PROP_NAME = "appelement";
	private static final String PARAMETERS_PROP_NAME = "parameters";
	private static final String FIELDS_PROP_NAME = "fields";

	public static Query read(JavaScriptObject aValue) throws Exception {
		JsObject o = aValue.cast();
		assert o != null;
		Query query = new Query();
		query.setTitle(o.getString(TITLE_PROP_NAME));
		if(o.has(MANUAL_PROP_NAME)){
			query.setManual(o.getBoolean(MANUAL_PROP_NAME));
		}
		query.setEntityName(o.getString(APP_ELEMENT_PROP_NAME));
		// parameters
		assert o.has(PARAMETERS_PROP_NAME);
		JavaScriptObject pa = o.getJs(PARAMETERS_PROP_NAME);
		assert pa != null;
		Parameters params = new Parameters();
		FieldsJSONReader.readFields(pa, params);
		query.setParameters(params);
		// fields
		assert o.has(FIELDS_PROP_NAME);
		JavaScriptObject fa = o.getJs(FIELDS_PROP_NAME);
		assert fa != null;
		Fields fields = new Fields();
		FieldsJSONReader.readFields(fa, fields);
		query.setFields(fields);
		return query;
	}
}
