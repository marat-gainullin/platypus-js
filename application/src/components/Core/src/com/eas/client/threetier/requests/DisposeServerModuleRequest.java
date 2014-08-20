/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;

/**
 *
 * @author pk, mg, kl refactoring
 */
public class DisposeServerModuleRequest extends Request {

    private String moduleName;

    public DisposeServerModuleRequest() {
        super(Requests.rqDisposeServerModule);
    }

    public DisposeServerModuleRequest(String aModuleName) {
        this();
        moduleName = aModuleName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String aValue) {
        moduleName = aValue;
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
