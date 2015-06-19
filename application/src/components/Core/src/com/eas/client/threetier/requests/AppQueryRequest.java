/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;
import java.util.Date;

/**
 *
 * @author mg
 */
public class AppQueryRequest extends Request {

    protected String queryName;
    protected Date timeStamp;

    public AppQueryRequest() {
        super(Requests.rqAppQuery);
    }

    public AppQueryRequest(String aQueryName, Date aTimeStamp) {
        this();
        queryName = aQueryName;
        timeStamp = aTimeStamp;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String aValue) {
        queryName = aValue;
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

        protected String appQueryJson;
        protected Date timeStamp;

        public Response(String aAppQueryJson, Date aTimeStamp) {
            super();
            appQueryJson = aAppQueryJson;
            timeStamp = aTimeStamp;
        }

        public String getAppQueryJson() {
            return appQueryJson;
        }

        public void setAppQueryJson(String aValue) {
            appQueryJson = aValue;
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
