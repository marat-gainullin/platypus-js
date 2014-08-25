/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.server.PlatypusServerCore;
import com.eas.server.RequestHandler;
import com.eas.server.Session;
import com.eas.server.SessionManager;
import com.eas.server.UnauthorizedRequestException;
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
            handle2(aSession, onSuccess, onFailure);
        }
    }

    protected abstract void handle2(Session aSession, Consumer<R> onSuccess, Consumer<Exception> onFailure);
}
