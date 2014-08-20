/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;
import java.util.Set;

/**
 *
 * @author pk, mg refactoring
 */
public class CreateServerModuleRequest extends Request {

    private String moduleName;

    public CreateServerModuleRequest() {
        super(Requests.rqCreateServerModule);
    }

    public CreateServerModuleRequest(String aModuleName) {
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

        private String moduleName;
        private Set<String> functionsNames;
        private boolean permitted;

        public Response(String aModuleName, Set<String> aFunctionsNames, boolean aPermitted) {
            super();
            moduleName = aModuleName;
            functionsNames = aFunctionsNames;
            permitted = aPermitted;
        }

        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String aValue) {
            moduleName = aValue;
        }

        public Set<String> getFunctionsNames() {
            return functionsNames;
        }

        public void setFunctionsNames(Set<String> aFuntionNames) {
            functionsNames = aFuntionNames;
        }

        public boolean isPermitted() {
            return permitted;
        }

        public void setPermitted(boolean aPermitted) {
            permitted = aPermitted;
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }
    }
}
