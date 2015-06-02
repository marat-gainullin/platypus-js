/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.DatabasesClient;
import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.MD5Generator;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.script.Scripts;
import com.eas.util.IDGenerator;
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

    public static void authorize(PlatypusServer aServer, String aUserName, String aPassword, Consumer<Session> onSuccess, Consumer<Exception> onFailure) {
        try {
            if (aUserName != null && !aUserName.isEmpty()) {
                String passwordMd5 = MD5Generator.generate(aPassword != null ? aPassword : "");
                Session created = aServer.getSessionManager().create(String.valueOf(IDGenerator.genID()));
                DatabasesClient.credentialsToPrincipalWithBasicAuthentication(aServer.getDatabasesClient(), aUserName, passwordMd5, created.getSpace(), (PlatypusPrincipal principal) -> {
                    if (principal != null) {
                        created.getSpace().setPrincipal(principal);
                        onSuccess.accept(created);
                    } else {
                        onFailure.accept(new AccessControlException(LOGIN_INCORRECT_MSG, new AuthPermission("*")));
                    }
                }, onFailure);
            } else {
                PlatypusPrincipal principal = new AnonymousPlatypusPrincipal("anonymous-" + String.valueOf(IDGenerator.genID()));
                Session created = aServer.getSessionManager().create(String.valueOf(IDGenerator.genID()));
                created.getSpace().setPrincipal(principal);
                created.getSpace().process(()->{
                    onSuccess.accept(created);
                });
            }
        } catch (Exception ex) {
            onFailure.accept(ex);
        }
    }
}
