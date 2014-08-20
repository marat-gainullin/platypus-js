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
public class AppElementChangedRequest extends Request {

    protected String databaseId;
    protected String entityId;

    public AppElementChangedRequest() {
        super(Requests.rqAppElementChanged);
    }
    
    public AppElementChangedRequest(String aDatadaseId, String aEntityId) {
        this();
        databaseId = aDatadaseId;
        entityId = aEntityId;
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String aValue) {
        databaseId = aValue;
    }

    public void setEntityId(String aValue) {
        entityId = aValue;
    }

    public String getEntityId() {
        return entityId;
    }

    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }

    public static class Response extends com.eas.client.threetier.Response {

        public Response() {
            super();
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }
    }
}
