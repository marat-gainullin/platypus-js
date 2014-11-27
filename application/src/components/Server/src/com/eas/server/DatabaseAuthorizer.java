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

    public static final String LOGIN_INCORRECT_MSG = "Bad user name or password";
    public static final String CREDENTIALS_MISSING_MSG = "User name and password are required while anonymous access is disabled.";
    private static final int LOGIN_DELAY = 500;

    public static void authorize(PlatypusServerCore aServerCore, String aUserName, String aPassword, Consumer<Session> onSuccess, Consumer<Exception> onFailure) {
        try {
            Thread.sleep(LOGIN_DELAY);
            if (aUserName != null && !aUserName.isEmpty()) {
                String passwordMd5 = MD5Generator.generate(aPassword != null ? aPassword : "");
                DatabasesClient.credentialsToPrincipalWithBasicAuthentication(aServerCore.getDatabasesClient(), aUserName, passwordMd5, (PlatypusPrincipal principal) -> {
                    if (principal != null) {
                        Session created = aServerCore.getSessionManager().createSession(principal, String.valueOf(IDGenerator.genID()));
                        onSuccess.accept(created);
                    } else {
                        onFailure.accept(new AccessControlException(LOGIN_INCORRECT_MSG, new AuthPermission("*")));
                    }
                }, onFailure);
            } else {
                Session created = aServerCore.getSessionManager().createSession(new AnonymousPlatypusPrincipal("anonymous-" + String.valueOf(IDGenerator.genID())), String.valueOf(IDGenerator.genID()));
                onSuccess.accept(created);
            }
        } catch (Exception ex) {
            onFailure.accept(ex);
        }
    }
}
