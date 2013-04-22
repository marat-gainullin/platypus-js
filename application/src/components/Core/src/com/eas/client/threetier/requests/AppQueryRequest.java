/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;

/**
 *
 * @author mg
 */
public class AppQueryRequest extends Request {

    protected String queryId;

    public AppQueryRequest(long aRequestId) {
        super(aRequestId, Requests.rqAppQuery);
    }
    
    public AppQueryRequest(long aRequestId, String aQueryId) {
        this(aRequestId);
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
}
