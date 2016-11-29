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

    protected Response() {
        super();
    }
    
    public abstract void accept(PlatypusResponseVisitor aVisitor) throws Exception;
}
