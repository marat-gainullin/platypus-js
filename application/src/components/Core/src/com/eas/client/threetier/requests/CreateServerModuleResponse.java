package com.eas.client.threetier.requests;

import com.eas.client.threetier.Response;
import java.util.Set;

public class CreateServerModuleResponse extends Response {

    private String moduleName;
    private Set<String> functionsNames;
    private boolean permitted;

    public CreateServerModuleResponse(long aRequestId, String aModuleName, Set<String> aFunctionsNames, boolean aPermitted) {
        super(aRequestId);
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
