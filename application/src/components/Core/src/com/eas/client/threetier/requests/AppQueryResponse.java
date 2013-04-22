/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.queries.Query;
import com.eas.client.queries.SqlQuery;
import com.eas.client.threetier.Response;

/**
 *
 * @author mg
 */
public class AppQueryResponse extends Response {

    protected Query appQuery;

    public AppQueryResponse(long requestId, SqlQuery aAppQuery) {
        super(requestId);
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
