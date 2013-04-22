/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;

/**
 *
 * @author bl, mg refactoring
 */
public class OutHashRequest extends Request{
    
    private String userName;
    
    public OutHashRequest(long aRequestId) {
        super(aRequestId, Requests.rqOutHash);
    }
    
    public OutHashRequest(long aRequestId, String aUserName){
        this(aRequestId);
        userName = aUserName;
    }
    
    public String getUserName(){
        return userName;
    }

    public void setUserName(String aValue) {
        userName = aValue;
    }
    
    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }
    
    public static class Response extends com.eas.client.threetier.Response
    {               
        /**
         * СМС отправлено успешно
         */
        public static final int RES_CODE_SENDING_SUCCESS = 0;
        /**
         * В базе не найден номер телефона
         */
        public static final int RES_CODE_SENDING_NO_PHONE = 1;
        /**
         * Другие ошибки отправки СМС
         */
        public static final int RES_CODE_SENDING_ERROR = 2;
        
        private int resultCode;
        private String resultDescription;
        
        public Response(long aRequestId, int aCode, String aResultStr)
        {
            super(aRequestId);
            resultCode = aCode;
            resultDescription = aResultStr;
        }

        public int getResultCode() {
            return resultCode;
        }

        public void setResultCode(int aValue) {
            resultCode = aValue;
        }

        public String getResultDesc() {
            return resultDescription;
        }

        public void setResultDesc(String aValue) {
            resultDescription = aValue;
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }        
    }    
}
