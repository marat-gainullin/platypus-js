/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;

/**
 *
 * @author ab
 */
public class ExecuteServerReportRequest extends Request {

    private String moduleName;
    private NamedArgument[] arguments;

    public ExecuteServerReportRequest(long aRequestId) {
        super(aRequestId, Requests.rqExecuteReport);
    }
    
    public ExecuteServerReportRequest(long aRequestId, String aModuleName) {
        this(aRequestId);
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

    /**
     * @return the arguments
     */
    public NamedArgument[] getArguments() {
        return arguments;
    }

    /**
     * @param aArguments 
     */
    public void setArguments(NamedArgument[] aArguments) {
        arguments = aArguments;
    }

    public static class NamedArgument {
        
        private String name;
        private Object value;
        
        public NamedArgument() {
            name = "";
        }

        public NamedArgument(String aName, Object aArgument) {
            name = aName;
            value = aArgument;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param aName 
         */
        public void setName(String aName) {
            name = aName;
        }

        /**
         * @return the argument
         */
        public Object getValue() {
            return value;
        }

        /**
         * @param aValue the argument to set
         */
        public void setValue(Object aValue) {
            value = aValue;
        }
        
        
    }
    
    public static class Response extends com.eas.client.threetier.Response {

        private byte[] result;
        private String format;

        public Response(long requestID, byte[] aResult, String aFormat) {
            super(requestID);
            result = aResult;
            format = aFormat;
            
        }

        public byte[] getResult() {
            return result;
        }

        public void setResult(byte[] aValue) {
            result = aValue;
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }

        /**
         * @return the format
         */
        public String getFormat() {
            return format;
        }

        /**
         * @param aFormat the format to set
         */
        public void setFormat(String aFormat) {
            format = aFormat;
        }
    }
}
