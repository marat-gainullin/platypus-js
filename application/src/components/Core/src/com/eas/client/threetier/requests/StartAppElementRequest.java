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
public class StartAppElementRequest extends Request {

    public StartAppElementRequest() {
        super(Requests.rqStartAppElement);
    }
    
    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }

    public static class Response extends com.eas.client.threetier.Response {

        private String appElementId;

        public Response(String aAppElementId) {
            super();
            appElementId = aAppElementId;
        }

        public String getAppElementId() {
            return appElementId;
        }

        public void setAppElementId(String aValue) {
            appElementId = aValue;
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }
    }
}
