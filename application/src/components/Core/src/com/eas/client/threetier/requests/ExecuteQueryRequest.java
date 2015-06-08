/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.metadata.Fields;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;
import java.util.Map;

/**
 *
 * @author pk, mg refacoring
 */
public class ExecuteQueryRequest extends Request {

    protected String queryName;
    protected Map<String, String> paramsJsons;
    protected Fields expectedFields;

    public ExecuteQueryRequest() {
        super(Requests.rqExecuteQuery);
    }

    public ExecuteQueryRequest(String aQueryName, Map<String, String> aParamsJsons, Fields aExpectedFields) {
        this();
        queryName = aQueryName;
        paramsJsons = aParamsJsons;
        expectedFields = aExpectedFields;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String aValue) {
        queryName = aValue;
    }

    public Map<String, String> getParamsJsons() {
        return paramsJsons;
    }

    public void setParamsJsons(Map<String, String> aValue) {
        paramsJsons = aValue;
    }

    public Fields getExpectedFields() {
        return expectedFields;
    }

    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }

    public static class Response extends com.eas.client.threetier.Response {

        private String json;
        private Fields expectedFields;

        public Response(String aJson) {
            super();
            json = aJson;
        }

        public Response(String aJson, Fields aExpectedFields) {
            this(aJson);
            expectedFields = aExpectedFields;
        }

        public String getJson() {
            return json;
        }

        public void setJson(String aValue) {
            json = aValue;
        }

        public Fields getExpectedFields() {
            return expectedFields;
        }

        public void setExpectedFields(Fields aFields) {
            expectedFields = aFields;
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }
    }
}
