/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.bearsoft.rowset.changes.Change;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author pk, mg refactoring
 */
public class CommitRequest extends Request {

    protected List<Change> changes;
    
    public CommitRequest() {
        super(Requests.rqCommit);
    }
    
    public CommitRequest(List<Change> aChanges) {
        this();
        changes = aChanges;
    }

    public List<Change> getChanges() {
        return Collections.unmodifiableList(changes);
    }

    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }

    public void setChanges(List<Change> aValue) {
        changes = aValue;
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
