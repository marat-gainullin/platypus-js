/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.metadata.ApplicationElement;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;

/**
 *
 * @author mg
 */
public class AppElementRequest extends Request {

    protected String appElementId;

    public AppElementRequest(long aRequestId) {
        super(aRequestId, Requests.rqAppElement);
    }
    
    public AppElementRequest(long aRequestId, String aAppElementId) {
        this(aRequestId);
        appElementId = aAppElementId;
    }

    public String getAppElementId() {
        return appElementId;
    }

    public void setAppElementId(String aValue) {
        appElementId = aValue;
    }

    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }

    public static class Response extends com.eas.client.threetier.Response {

        protected ApplicationElement appElement;

        public Response(long aRequestId, ApplicationElement aAppElement) {
            super(aRequestId);
            appElement = aAppElement;
        }

        public ApplicationElement getAppElement() {
            return appElement;
        }

        public void setAppElement(ApplicationElement aValue) {
            appElement = aValue;
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }
    }
}
