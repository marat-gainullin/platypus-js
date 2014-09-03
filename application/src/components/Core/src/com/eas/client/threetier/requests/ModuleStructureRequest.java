/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author pk, mg refactoring
 */
public class ModuleStructureRequest extends Request {

    protected String moduleOrResourceName;
    
    public ModuleStructureRequest(String aModuleOrResourceName) {
        this();
        moduleOrResourceName = aModuleOrResourceName;
    }
    
    public ModuleStructureRequest() {
        super(Requests.rqModuleStructure);
    }

    public String getModuleOrResourceName() {
        return moduleOrResourceName;
    }

    public void setModuleOrResourceName(String aValue) {
        moduleOrResourceName = aValue;
    }

    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }

    public static class Response extends com.eas.client.threetier.Response {

        protected Set<String> structure = new HashSet<>();
        protected Set<String> clientDependencies = new HashSet<>();
        protected Set<String> serverDependencies = new HashSet<>();
        protected Set<String> queryDependencies = new HashSet<>();

        public Response() {
            super();
        }

        public Set<String> getStructure() {
            return structure;
        }

        public Set<String> getClientDependencies() {
            return clientDependencies;
        }

        public Set<String> getServerDependencies() {
            return serverDependencies;
        }

        public Set<String> getQueryDependencies() {
            return queryDependencies;
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }
    }
}
