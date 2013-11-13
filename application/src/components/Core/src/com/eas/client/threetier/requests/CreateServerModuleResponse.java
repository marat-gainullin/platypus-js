package com.eas.client.threetier.requests;

import com.eas.client.threetier.Response;
import java.util.Set;

public class CreateServerModuleResponse extends Response {

    private String moduleName;
    private Set<String> functionsNames;
    private boolean report;
    private boolean permitted;

    public CreateServerModuleResponse(long aRequestId, String aModuleName, Set<String> aFunctionsNames, boolean isReport, boolean isPermitted) {
        super(aRequestId);
        moduleName = aModuleName;
        functionsNames = aFunctionsNames;
        report = isReport;
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

    /**
     * @return the report
     */
    public boolean isReport() {
        return report;
    }

    public boolean isPermitted() {
        return permitted;
    }

    @Override
    public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }
}
