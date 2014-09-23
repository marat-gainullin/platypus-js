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
public class CredentialRequest extends Request {

    public CredentialRequest() {
        super(Requests.rqStartAppElement);
    }
    
    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }

    public static class Response extends com.eas.client.threetier.Response {

        private String startAppElementName;
        private String name;

        public Response(String aAppElementName, String aName) {
            super();
            startAppElementName = aAppElementName;
            name = aName;
        }

        public String getName() {
            return name;
        }

        public void setName(String aValue) {
            name = aValue;
        }

        public String getAppElementName() {
            return startAppElementName;
        }

        public void setAppElementName(String aValue) {
            startAppElementName = aValue;
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }
    }
}
