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
public class CreateServerModuleRequest extends Request {

    private String moduleName;
    private Date timeStamp;

    public CreateServerModuleRequest() {
        super(Requests.rqCreateServerModule);
    }

    public CreateServerModuleRequest(String aModuleName, Date aTimeStamp) {
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

        protected ServerModuleInfo info;
        private Date timeStamp;

        public Response(ServerModuleInfo aInfo) {
            super();
            info = aInfo;
        }

        public ServerModuleInfo getInfo() {
            return info;
        }

        public void setInfo(ServerModuleInfo aValue) {
            info = aValue;
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
