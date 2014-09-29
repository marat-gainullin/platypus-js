/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import java.util.function.Consumer;

/**
 *
 * @param <T>
 * @author pk, mg refactoring
 * @param <R>
 */
public abstract class SessionRequestHandler<T extends Request, R extends Response> extends RequestHandler<T, R> {

    public SessionRequestHandler(PlatypusServerCore aServerCore, T aRequest) {
        super(aServerCore, aRequest);
    }

    public SessionManager getSessionManager() {
        assert serverCore != null;
        return serverCore.getSessionManager();
    }

    public void handle(Session aSession, Consumer<R> onSuccess, Consumer<Exception> onFailure) {
        if (aSession == null) {
            if (onFailure != null) {
                onFailure.accept(new UnauthorizedRequestException("Unauthorized. Login first."));
            }
        } else {
            aSession.accessed();
            PlatypusPrincipal oldPrincipal = PlatypusPrincipal.getInstance();
            PlatypusPrincipal.setInstance(aSession.getPrincipal());
            try {
                handle2(aSession, onSuccess, onFailure);
            } finally {
                PlatypusPrincipal.setInstance(oldPrincipal);
            }
        }
    }

    protected abstract void handle2(Session aSession, Consumer<R> onSuccess, Consumer<Exception> onFailure);
}
