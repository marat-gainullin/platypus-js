/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;

/**
 *
 * @author mg
 */
public class IsAppElementActualRequest extends Request {

    private String appElementId;
    private long txtContentSize;
    private long txtContentCrc32;

    public IsAppElementActualRequest(long aRequestId) {
        super(aRequestId, Requests.rqIsAppElementActual);
    }
    
    public IsAppElementActualRequest(long aRequestId, String aAppElementId, long aTxtContentSize, long aTxtContentCrc32) {
        this(aRequestId);
        appElementId = aAppElementId;
        txtContentSize = aTxtContentSize;
        txtContentCrc32 = aTxtContentCrc32;
    }

    public String getAppElementId() {
        return appElementId;
    }

    public void setAppElementId(String aValue) {
        appElementId = aValue;
    }

    public long getTxtContentSize() {
        return txtContentSize;
    }

    public void setTxtContentSize(long aValue) {
        txtContentSize = aValue;
    }

    public long getTxtContentCrc32() {
        return txtContentCrc32;
    }

    public void setTxtContentCrc32(long aValue) {
        txtContentCrc32 = aValue;
    }

    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }

    public static class Response extends com.eas.client.threetier.Response {

        private boolean actual;

        public Response(long aRequestId, boolean aActual) {
            super(aRequestId);
            actual = aActual;
        }

        public boolean isActual() {
            return actual;
        }

        public void setActual(boolean aValue) {
            actual = aValue;
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }
    }
}
