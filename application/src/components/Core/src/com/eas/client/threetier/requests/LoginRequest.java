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
public class LoginRequest extends Request {

    private String login;
    private String password;
    private String session2restore;

    public LoginRequest() {
        super(Requests.rqLogin);
    }

    public LoginRequest(String aLogin, String aPassword) {
        this();
        login = aLogin;
        password = aPassword;
    }

    public LoginRequest(String aLogin, String aPassword, String aSession2restore) {
        this(aLogin, aPassword);
        session2restore = aSession2restore;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getSession2restore() {
        return session2restore;
    }

    public void setLogin(String aValue) {
        login = aValue;
    }

    public void setPassword(String aValue) {
        password = aValue;
    }

    @Override
    public void accept(PlatypusRequestVisitor aVisitor) throws Exception {
        aVisitor.visit(this);
    }

    public void setSession2restore(String aValue) {
        session2restore = aValue;
    }

    public static class Response extends com.eas.client.threetier.Response {

        private String sessionId;

        public Response(String aSessionId) {
            super();
            sessionId = aSessionId;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String aValue) {
            sessionId = aValue;
        }

        @Override
        public void accept(PlatypusResponseVisitor aVisitor) throws Exception {
            aVisitor.visit(this);
        }
    }
}
