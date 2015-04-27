/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameters;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author pk, mg refacoring
 */
public class ExecuteQueryRequest extends Request {

    protected String queryName;
    protected Parameters params;
    protected Fields expectedFields;

    public ExecuteQueryRequest() {
        super(Requests.rqExecuteQuery);
    }

    public ExecuteQueryRequest(String aQueryName, Parameters aParams, Fields aExpectedFields) {
        this();
        queryName = aQueryName;
        params = aParams;
        expectedFields = aExpectedFields;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String aValue) {
        queryName = aValue;
    }

    public Parameters getParams() {
        return params;
    }

    public void setParams(Parameters aValue) {
        params = aValue;
    }

    public Fields getExpectedFields() {
        return expectedFields;
    }

    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }

    public static class Response extends com.eas.client.threetier.Response {

        private JSObject rowset;
        private Fields expectedFields;
        private int updateCount;

        public Response(JSObject aRowset, int aUpdateCount) {
            super();
            rowset = aRowset;
            updateCount = aUpdateCount;
        }

        public Response(JSObject aRowset, int aUpdateCount, Fields aExpectedFields) {
            this(aRowset, aUpdateCount);
            expectedFields = aExpectedFields;
        }

        public JSObject getRowset() {
            return rowset;
        }

        public void setRowset(JSObject aValue) {
            rowset = aValue;
        }

        public Fields getExpectedFields() {
            return expectedFields;
        }

        public void setExpectedFields(Fields aFields) {
            expectedFields = aFields;
        }

        /**
         * @return the updateCount
         */
        public int getUpdateCount() {
            return updateCount;
        }

        public void setUpdateCount(int aValue) {
            updateCount = aValue;
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }
    }
}
