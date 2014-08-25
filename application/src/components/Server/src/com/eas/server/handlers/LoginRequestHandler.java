/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.DatabasesClient;
import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.MD5Generator;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.threetier.requests.LoginRequest;
import com.eas.client.threetier.requests.LoginRequest.Response;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import java.util.function.Consumer;
import javax.security.auth.login.FailedLoginException;

/**
 *
 * @author pk, mg refactoring
 */
public class LoginRequestHandler extends CommonRequestHandler<LoginRequest, LoginRequest.Response> {

    public static final String BAD_SESSION_ID_MSG = "Bad session id (old or from another (dead) server)";
    public static final String ILLEGAL_SESSION_ID_MSG = "Cannot restore session, because it belongs to another user.";
    public static final String LOGIN_INCORRECT_MSG = "Bad user name or password.";
    public static final String CREDENTIALS_MISSING_MSG = "User name and password are required while anonymous access is disabled.";
    private static final int LOGIN_DELAY = 500;

    public LoginRequestHandler(PlatypusServerCore aServerCore, LoginRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    public void handle(Consumer<Response> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Thread.sleep(LOGIN_DELAY);
        try {
            if (getRequest().getLogin() != null && !getRequest().getLogin().isEmpty() && getRequest().getPassword() != null) {
                String passwordMd5 = MD5Generator.generate(getRequest().getPassword());
                String sessionId = getRequest().getSession2restore();
                if (sessionId == null) {
                    PlatypusPrincipal principal = DatabasesClient.credentialsToPrincipalWithBasicAuthentication(getServerCore().getDatabasesClient(), getRequest().getLogin(), passwordMd5);
                    if (principal != null) {
                        sessionId = String.valueOf(IDGenerator.genID());
                        getServerCore().getSessionManager().createSession(principal, sessionId);
                        return new LoginRequest.Response(sessionId);
                    } else {
                        throw new FailedLoginException(LOGIN_INCORRECT_MSG);
                    }
                } else {
                    Session s = getServerCore().getSessionManager().get(sessionId);
                    if (s != null) {
                        s.accessed();
                        PlatypusPrincipal principal = DatabasesClient.credentialsToPrincipalWithBasicAuthentication(getServerCore().getDatabasesClient(), getRequest().getLogin(), passwordMd5);
                        if (principal != null) {
                            if (s.getUser().equals(getRequest().getLogin())) {
                                return new LoginRequest.Response(sessionId);
                            } else {
                                throw new FailedLoginException(ILLEGAL_SESSION_ID_MSG);
                            }
                        } else {
                            throw new FailedLoginException(LOGIN_INCORRECT_MSG);
                        }
                    } else {
                        throw new FailedLoginException(BAD_SESSION_ID_MSG);
                    }
                }
            } else if (getServerCore().isAnonymousEnabled()) {
                PlatypusPrincipal principal = new AnonymousPlatypusPrincipal("anonymous-" + IDGenerator.genID());
                String sessionId = String.valueOf(IDGenerator.genID());
                getServerCore().getSessionManager().createSession(principal, sessionId);
                return new LoginRequest.Response(sessionId);
            } else {
                throw new FailedLoginException(CREDENTIALS_MISSING_MSG);
            }
        } catch (Exception ex) {
            if (onFailure != null) {
                onFailure.accept(ex);
            }
        }
    }
}
