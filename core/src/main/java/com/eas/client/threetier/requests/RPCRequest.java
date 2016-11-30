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
public class RPCRequest extends Request {

    private String methodName;
    private String[] argumentsJsons;
    private String moduleName;

    public RPCRequest() {
        super(Requests.rqExecuteServerModuleMethod);
    }

    public RPCRequest(String aModuleName, String aMethodName, String[] aArgumentsJsons) {
        this();
        moduleName = aModuleName;
        methodName = aMethodName;
        if (aArgumentsJsons == null) {
            throw new NullPointerException("No arguments.");
        }
        argumentsJsons = aArgumentsJsons;
    }

    public String[] getArgumentsJsons() {
        return argumentsJsons;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String aValue) {
        methodName = aValue;
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

    public void setArgumentsJsons(String[] aValue) {
        argumentsJsons = aValue;
    }
    
    public static class Response extends com.eas.client.threetier.Response {

        private Object result;

        public Response(Object aResult) {
            super();
            result = aResult;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object aValue) {
            result = aValue;
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }
    }
}
