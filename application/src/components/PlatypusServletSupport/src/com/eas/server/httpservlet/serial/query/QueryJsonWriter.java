/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet.serial.query;

import com.eas.client.queries.Query;
import com.eas.server.httpservlet.serial.JsonWriter;
import com.eas.util.JSONUtils;

/**
 *
 * @author mg
 */
public class QueryJsonWriter extends JsonWriter{

    private static final String TITLE_PROP_NAME = "title";
    private static final String MANUAL_PROP_NAME = "manual";
    private static final String APP_ELEMENT_PROP_NAME = "appelement";
    private static final String PARAMETERS_PROP_NAME = "parameters";
    private static final String FIELDS_PROP_NAME = "fields";
    protected Query<?> query;

    public QueryJsonWriter(Query<?> aQuery) {
        query = aQuery;
    }

    public String write() {
        StringBuilder sb = new StringBuilder();
        JSONUtils.o(sb,
                TITLE_PROP_NAME, JSONUtils.s(query.getTitle()),
                MANUAL_PROP_NAME, String.valueOf(query.isManual()),
                APP_ELEMENT_PROP_NAME, JSONUtils.s(query.getEntityId().toString()),
                PARAMETERS_PROP_NAME, fields2a(query.getParameters()),
                FIELDS_PROP_NAME, fields2a(query.getFields())
                );
        return sb.toString();
    }
}
