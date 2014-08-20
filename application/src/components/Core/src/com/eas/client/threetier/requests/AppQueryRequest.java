/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.queries.Query;
import com.eas.client.queries.SqlQuery;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;

/**
 *
 * @author mg
 */
public class AppQueryRequest extends Request {

    protected String queryId;

    public AppQueryRequest() {
        super(Requests.rqAppQuery);
    }

    public AppQueryRequest(String aQueryId) {
        this();
        queryId = aQueryId;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String aValue) {
        queryId = aValue;
    }

    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }

    public static class Response extends com.eas.client.threetier.Response {

        protected Query appQuery;

        public Response(SqlQuery aAppQuery) {
            super();
            appQuery = aAppQuery;
        }

        public Query getAppQuery() {
            return appQuery;
        }

        public void setAppQuery(Query aValue) {
            appQuery = aValue;
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }
    }
}
