/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;

/**
 *
 * @author pk, mg refactoring
 */
public class ModuleStructureRequest extends Request {

    protected String moduleName;
    
    public ModuleStructureRequest(String aModuleName) {
        this();
        moduleName = aModuleName;
    }
    
    public ModuleStructureRequest() {
        super(Requests.rqModuleStructure);
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

        protected String json;
                
        public Response(String aJson) {
            super();
            json = aJson;
        }

        public String getJson() {
            return json;
        }

        public void setJson(String aValue) {
            json = aValue;
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }
    }
}
