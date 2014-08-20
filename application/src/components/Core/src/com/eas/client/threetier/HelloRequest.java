/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.eas.client.threetier.requests.PlatypusRequestVisitor;
import com.eas.client.threetier.requests.PlatypusResponseVisitor;

/**
 *
 * @author pk, mg refactoring
 */
public class HelloRequest extends Request {

    public HelloRequest() {
        super(Requests.rqHello);
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
