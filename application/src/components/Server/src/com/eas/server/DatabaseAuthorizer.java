/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.DatabasesClient;
import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.MD5Generator;
import com.eas.client.login.PlatypusPrincipal;
import java.security.AccessControlException;
import java.util.function.Consumer;
import javax.security.auth.AuthPermission;

/**
 *
 * @author pk, mg refactoring
 */
public class DatabaseAuthorizer {

    public static class SessionPrincipal {

        private final Session session;
        private final PlatypusPrincipal principal;

        public SessionPrincipal(Session aSession, PlatypusPrincipal aPrincipal) {
            super();
            session = aSession;
            principal = aPrincipal;
        }

        public Session getSession() {
            return session;
        }

        public PlatypusPrincipal getPrincipal() {
            return principal;
        }

    }

    public static final String LOGIN_INCORRECT_MSG = "Bad user name or password";
    public static final String CREDENTIALS_MISSING_MSG = "User name and password are required while anonymous access is disabled.";
    private static final int LOGIN_DELAY = 500;

    public static void authorize(PlatypusServer aServer, String aUserName, String aPassword, Consumer<SessionPrincipal> onSuccess, Consumer<Exception> onFailure) {
        try {
            Thread.sleep(LOGIN_DELAY);
            if (aUserName != null && !aUserName.isEmpty()) {
                String passwordMd5 = MD5Generator.generate(aPassword != null ? aPassword : "");
                DatabasesClient.credentialsToPrincipalWithBasicAuthentication(aServer.getDatabasesClient(), aUserName, passwordMd5, (PlatypusPrincipal principal) -> {
                    if (principal != null) {
                        Session created = aServer.getSessionManager().createSession(String.valueOf(IDGenerator.genID()));
                        aServer.getPrincipals().put(created.getId(), principal);
                        SessionPrincipal sp = new SessionPrincipal(created, principal);
                        onSuccess.accept(sp);
                    } else {
                        onFailure.accept(new AccessControlException(LOGIN_INCORRECT_MSG, new AuthPermission("*")));
                    }
                }, onFailure);
            } else {
                PlatypusPrincipal principal = new AnonymousPlatypusPrincipal("anonymous-" + String.valueOf(IDGenerator.genID()));
                Session created = aServer.getSessionManager().createSession(String.valueOf(IDGenerator.genID()));
                aServer.getPrincipals().put(created.getId(), principal);
                SessionPrincipal sp = new SessionPrincipal(created, principal);
                onSuccess.accept(sp);
            }
        } catch (Exception ex) {
            onFailure.accept(ex);
        }
    }
}
