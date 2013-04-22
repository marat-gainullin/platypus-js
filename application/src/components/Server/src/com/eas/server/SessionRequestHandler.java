/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;


/**
 *
 * @param <T>
 * @author pk
 */
public abstract class SessionRequestHandler<T extends Request> extends RequestHandler<T> {

    private final Session session;

    public SessionRequestHandler(PlatypusServerCore aServerCore, Session aSession, T aRequest) {
        super(aServerCore, aRequest);
        session = aSession;
    }

    public Session getSession() {
        return session;
    }

    @Override
    protected Response handle() throws Exception {
        if (session == null) {
            throw new UnauthorizedRequestException("Unauthorized. Login first.");
        }
        session.accessed();
        // Thread-local current session setting. 
        // Thus, all code, executed under current principal (java or js code) can be authorized.
        getServerCore().getSessionManager().setCurrentSession(session);
        try {
            return handle2();
        } finally {
            // Revoke current session to avoid ANY session/memory leak.
            getServerCore().getSessionManager().setCurrentSession(null);
        }
    }

    protected abstract Response handle2() throws Exception;
}
