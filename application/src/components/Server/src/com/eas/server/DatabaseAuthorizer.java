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
import com.eas.util.IdGenerator;
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

    public static void authorize(PlatypusServer aServer, String aUserName, String aPassword, Scripts.Space aSpace, Consumer<PlatypusPrincipal> onSuccess, Consumer<Exception> onFailure) {
        try {
            if (aUserName != null && !aUserName.isEmpty()) {
                String passwordMd5 = MD5Generator.generate(aPassword != null ? aPassword : "");
                DatabasesClient.credentialsToPrincipalWithBasicAuthentication(aServer.getDatabasesClient(), aUserName, passwordMd5, aSpace, (PlatypusPrincipal principal) -> {
                    if (principal != null) {
                        onSuccess.accept(principal);
                    } else {
                        onFailure.accept(new AccessControlException(LOGIN_INCORRECT_MSG, new AuthPermission("*")));
                    }
                }, onFailure);
            } else {
                PlatypusPrincipal principal = new AnonymousPlatypusPrincipal("anonymous-" + IdGenerator.genStringId());
                onSuccess.accept(principal);
            }
        } catch (Exception ex) {
            onFailure.accept(ex);
        }
    }
}
