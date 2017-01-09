/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.json;

import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.queries.PlatypusQuery;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 */
public class QueryJSONReader {

    private static final String TITLE_PROP_NAME = "title";
    private static final String APP_ELEMENT_PROP_NAME = "appelement";
    private static final String PARAMETERS_PROP_NAME = "parameters";
    private static final String FIELDS_PROP_NAME = "fields";

    public static PlatypusQuery read(JSObject o) {
        PlatypusQuery query = new PlatypusQuery(null);
        String title = JSType.toString(o.getMember(TITLE_PROP_NAME));
        query.setTitle(title);
        String entityName = JSType.toString(o.getMember(APP_ELEMENT_PROP_NAME));
        query.setEntityName(entityName);
        // parameters
        JSObject jsParameters = (JSObject) o.getMember(PARAMETERS_PROP_NAME);
        assert jsParameters != null && jsParameters.isArray();
        Parameters params = new Parameters();
        FieldsJSONReader.readFields(jsParameters, params);
        for (int i = 0; i < params.getParametersCount(); i++) {
            Parameter p = params.get(i + 1);
            query.putParameter(p.getName(), p.getType(), p.getValue());
        }
        // fields
        JSObject jsFields = (JSObject) o.getMember(FIELDS_PROP_NAME);
        assert jsFields != null && jsFields.isArray();
        Fields fields = new Fields();
        FieldsJSONReader.readFields(jsFields, fields);
        query.setFields(fields);
        return query;
    }
}
