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
public class CreateServerModuleRequest extends Request
{
    private String moduleName;

    public CreateServerModuleRequest(long aRequestId) {
        super(aRequestId, Requests.rqCreateServerModule);
    }
    
    public CreateServerModuleRequest(long aRequestId, String aModuleName)
    {
        this(aRequestId);
        moduleName = aModuleName;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    public void setModuleName(String aValue) {
        moduleName = aValue;
    }

    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }
}
