/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;

/**
 *
 * @author pk
 */
public class ExecuteQueryRequest extends Request {

    protected String queryId;
    protected Parameters params;

    public ExecuteQueryRequest(long aRequestId) {
        super(aRequestId, Requests.rqExecuteQuery);
    }
    
    public ExecuteQueryRequest(long aRequestId, String aQueryId, Parameters aParams) {
        this(aRequestId);
        queryId = aQueryId;
        params = aParams;
    }
    
    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String aValue) {
        queryId = aValue;
    }

    public Parameters getParams() {
        return params;
    }

    public void setParams(Parameters aValue) {
        params = aValue;
    }
    
    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }
}
