/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.json;

import com.eas.client.queries.Query;
import com.eas.util.JsonUtils;

/**
 *
 * @author mg
 */
public class QueryJSONWriter extends FieldsJSONWriter{

    private static final String TITLE_PROP_NAME = "title";
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
        StringBuilder sb = JsonUtils.o(TITLE_PROP_NAME, JsonUtils.s(query.getTitle()).toString(),
                APP_ELEMENT_PROP_NAME, JsonUtils.s(query.getEntityName()).toString(),
                PARAMETERS_PROP_NAME, fields2a(query.getParameters()).toString(),
                FIELDS_PROP_NAME, fields2a(query.getFields()).toString()
                );
        return sb.toString();
    }
}
