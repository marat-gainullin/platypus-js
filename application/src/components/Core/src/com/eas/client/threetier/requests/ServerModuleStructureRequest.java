/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.ServerModuleInfo;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;
import java.util.Date;

/**
 *
 * @author pk, mg refactoring
 */
public class ServerModuleStructureRequest extends Request {

    private String moduleName;
    private Date timeStamp;

    public ServerModuleStructureRequest() {
        super(Requests.rqCreateServerModule);
    }

    public ServerModuleStructureRequest(String aModuleName, Date aTimeStamp) {
        this();
        moduleName = aModuleName;
        timeStamp = aTimeStamp;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String aValue) {
        moduleName = aValue;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date aValue) {
        timeStamp = aValue;
    }

    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }

    public static class Response extends com.eas.client.threetier.Response {

        protected String infoJson;
        private Date timeStamp;

        public Response(String aInfoJson) {
            super();
            infoJson = aInfoJson;
        }

        public String getInfoJson() {
            return infoJson;
        }

        public void setInfoJson(String aValue) {
            infoJson = aValue;
        }

        public Date getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(Date aValue) {
            timeStamp = aValue;
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }
    }

}
