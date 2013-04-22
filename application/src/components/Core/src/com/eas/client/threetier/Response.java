/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.eas.client.threetier.requests.PlatypusResponseVisitor;

/**
 *
 * @author pk, mg refactoring
 */
public abstract class Response {

    private long requestId;

    protected Response(long aRequestId) {
        requestId = aRequestId;
    }
    
    public long getRequestID() {
        return requestId;
    }
    
    public abstract void accept(PlatypusResponseVisitor aVisitor) throws Exception;
}
