/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.client.threetier.platypus.PlatypusNativeConnection;
import com.eas.client.AppConnection;
import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kl, mg refactoring
 */
public abstract class PlatypusConnection implements AppConnection {
    public static final String ACCESSCONTROL_EXCEPTION_LOG_MSG = "AccessControl in response {0}}";
    public static final String SQL_EXCEPTION_LOG_MSG = "SQLException in response {0} {1} {2}";

    public static class RequestCallback {

        public Request request;
        public Response response;
        public Consumer<Response> onComplete;

        public RequestCallback(Request aRequest, Consumer<Response> aOnSuccess) {
            super();
            request = aRequest;
            onComplete = aOnSuccess;
        }

        public synchronized void waitCompletion() throws InterruptedException {
            while (!request.isDone()) {
                wait();
            }
        }
    }
    
    public abstract String getUrl();
    
    protected String sessionId;
    protected String password;
    protected String login;

    public void setLoginCredentials(String aUserName, String aPassword, String aSessionId) {
        login = aUserName;
        password = aPassword;
        sessionId = aSessionId;
    }

    public Exception handleErrorResponse(ErrorResponse aResponse) {
        if (aResponse.getSqlErrorCode() != null || aResponse.getSqlState() != null) {
            Logger.getLogger(PlatypusNativeConnection.class.getName()).log(Level.FINEST, SQL_EXCEPTION_LOG_MSG, new Object[]{aResponse.getErrorMessage(), aResponse.getSqlState(), aResponse.getSqlErrorCode()});
            return new SQLException(aResponse.getErrorMessage(), aResponse.getSqlState(), aResponse.getSqlErrorCode());
        } else if (aResponse.isAccessControl()) {
            Logger.getLogger(PlatypusNativeConnection.class.getName()).log(Level.FINEST, ACCESSCONTROL_EXCEPTION_LOG_MSG, new Object[]{aResponse.getErrorMessage()});
            return new AccessControlException(((ErrorResponse) aResponse).getErrorMessage());
        } else {
            return new Exception("Error from server: "+((ErrorResponse) aResponse).getErrorMessage());
        }
    }
}
