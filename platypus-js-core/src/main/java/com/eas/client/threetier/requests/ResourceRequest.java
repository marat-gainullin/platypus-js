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
 * @author pk, mg refactoring
 */
public class ResourceRequest extends Request {

    protected Date timeStamp;
    protected String resourceName;

    public ResourceRequest() {
        super(Requests.rqResource);
    }

    public ResourceRequest(Date aTimeStamp, String aResourceName) {
        this();
        timeStamp = aTimeStamp;
        resourceName = aResourceName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String aValue) {
        resourceName = aValue;
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

        protected byte[] content;
        protected Date timeStamp;

        public Response() {
            super();
        }

        public Response(byte[] aContent, Date aTimeStamp) {
            super();
            content = aContent;
            timeStamp = aTimeStamp;
        }

        public byte[] getContent() {
            return content;
        }

        public void setContent(byte[] aValue) {
            content = aValue;
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
