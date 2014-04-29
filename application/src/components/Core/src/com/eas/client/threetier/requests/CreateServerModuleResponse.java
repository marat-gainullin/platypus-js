package com.eas.client.threetier.requests;

import com.eas.client.threetier.Response;
import java.util.Set;

public class CreateServerModuleResponse extends Response {

    private String moduleName;
    private final Set<String> functionsNames;
    private final boolean permitted;

    public CreateServerModuleResponse(long aRequestId, String aModuleName, Set<String> aFunctionsNames, boolean isPermitted) {
        super(aRequestId);
        moduleName = aModuleName;
        functionsNames = aFunctionsNames;
        permitted = isPermitted;
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

    public boolean isPermitted() {
        return permitted;
    }

    @Override
    public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }
}
