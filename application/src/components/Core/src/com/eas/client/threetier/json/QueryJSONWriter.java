/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.json;

import com.eas.client.queries.Query;
import com.eas.util.JSONUtils;

/**
 *
 * @author mg
 */
public class QueryJSONWriter extends FieldsJSONWriter{

    private static final String TITLE_PROP_NAME = "title";
    private static final String MANUAL_PROP_NAME = "manual";
    private static final String APP_ELEMENT_PROP_NAME = "appelement";
    private static final String PARAMETERS_PROP_NAME = "parameters";
    private static final String FIELDS_PROP_NAME = "fields";
    protected Query query;

    public QueryJSONWriter(Query aQuery) {
        super();
        query = aQuery;
    }

    public static String write(Query aQuery){
        QueryJSONWriter w = new QueryJSONWriter(aQuery);
        return w.write();
    }
    
    public String write() {
        StringBuilder sb = JSONUtils.o(
                TITLE_PROP_NAME, JSONUtils.s(query.getTitle()).toString(),
                MANUAL_PROP_NAME, String.valueOf(query.isManual()),
                APP_ELEMENT_PROP_NAME, JSONUtils.s(query.getEntityName()).toString(),
                PARAMETERS_PROP_NAME, fields2a(query.getParameters()).toString(),
                FIELDS_PROP_NAME, fields2a(query.getFields()).toString()
                );
        return sb.toString();
    }
}
