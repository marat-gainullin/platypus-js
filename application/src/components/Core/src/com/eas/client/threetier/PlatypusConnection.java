/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.eas.client.AppConnection;
import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kl, mg refactoring
 */
public abstract class PlatypusConnection implements AppConnection {
    public static final String ACCESSCONTROL_EXCEPTION_LOG_MSG = "AccessControl in response {0}: {1}";
    public static final String SQL_EXCEPTION_LOG_MSG = "SQLException in response {0}: {1} {2} {3}";

    protected String sessionId;
    protected String password;
    protected String login;

    public void setLoginCredentials(String aUserName, String aPassword, String aSessionId) {
        login = aUserName;
        password = aPassword;
        sessionId = aSessionId;
    }

    @Override
    public void handleErrorResponse(ErrorResponse aResponse) throws Exception {
        if (aResponse.getSqlErrorCode() != null || aResponse.getSqlState() != null) {
            Logger.getLogger(PlatypusNativeConnection.class.getName()).log(Level.FINEST, SQL_EXCEPTION_LOG_MSG, new Object[]{aResponse.getRequestID(), aResponse.getError(), aResponse.getSqlState(), aResponse.getSqlErrorCode()});
            throw new SQLException(aResponse.getError(), aResponse.getSqlState(), aResponse.getSqlErrorCode());
        } else if (aResponse.isAccessControl()) {
            Logger.getLogger(PlatypusNativeConnection.class.getName()).log(Level.FINEST, ACCESSCONTROL_EXCEPTION_LOG_MSG, new Object[]{aResponse.getRequestID(), aResponse.getError()});
            throw new AccessControlException(((ErrorResponse) aResponse).getError());
        } else {
            throw new Exception("Error from server: "+((ErrorResponse) aResponse).getError());
        }
    }
}
