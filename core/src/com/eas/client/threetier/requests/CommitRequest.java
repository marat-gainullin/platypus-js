/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;

/**
 *
 * @author pk, mg refactoring
 */
public class CommitRequest extends Request {

    protected String changesJson;
    
    public CommitRequest() {
        super(Requests.rqCommit);
    }
    
    public CommitRequest(String aChangesJson) {
        this();
        changesJson = aChangesJson;
    }

    public String getChangesJson() {
        return changesJson;
    }

    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }

    public void setChangesJson(String aValue) {
        changesJson = aValue;
    }

    public static class Response extends com.eas.client.threetier.Response {

        protected int updated;

        public Response(int aUpdated) {
            super();
            updated = aUpdated;
        }

        public int getUpdated() {
            return updated;
        }

        public void setUpdated(int aValue) {
            updated = aValue;
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }
    }
}
